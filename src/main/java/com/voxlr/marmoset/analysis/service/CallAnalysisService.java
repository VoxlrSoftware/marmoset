package com.voxlr.marmoset.analysis.service;

import static com.voxlr.marmoset.model.persistence.factory.CallUpdate.anUpdate;
import static com.voxlr.marmoset.util.ObjectUtils.isEqual;
import static com.voxlr.marmoset.util.StreamUtils.asStream;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.analysis.model.CallAnalysisResult;
import com.voxlr.marmoset.analysis.model.CallAnalysisResult.PhraseResult;
import com.voxlr.marmoset.exception.EntityNotFoundException;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.Call.Analysis;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis.Detection;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis.DetectionType;
import com.voxlr.marmoset.service.domain.CallService;

@Service
public class CallAnalysisService {
    
    public static double DEFAULT_DETECTION_THRESHOLD = 0.45;
    
    @Autowired
    private CallService callService;
    
    public void processAnalysis(CallAnalysisResult result) throws EntityNotFoundException {
	Call call = callService.getInternal(result.getCallId());
	handleAnalysisResult(call, result);
    }
    
    @Async("analysisExecutor")
    private void handleAnalysisResult(Call call, CallAnalysisResult result) {
	Analysis analysis = call.getAnalysis().reset();
	List<String> expectedPhrases = call.getCallStrategyPhrases();
	
	List<PhraseAnalysis> phraseAnalysis = expectedPhrases.stream().map(phrase -> {
	    PhraseAnalysis pAnalysis = new PhraseAnalysis(phrase);
	    
	    try {
		PhraseResult phraseResult = result.getPhrases().stream().filter(isEqual(PhraseResult::getPhrase, pAnalysis.getPhrase())).findFirst().get();
		phraseResult.getDetections().stream().forEach(detectionResult -> {
		    Detection detection = new Detection();
		    detection.setMatchedText(detectionResult.getMatchedText());
		    detection.setScore(detectionResult.getScore());
		    detection.setType(DetectionType.fromType(detectionResult.getType()));
		    pAnalysis.addDetection(detection);
		});
		
	    } catch (Exception e) {}

	    return pAnalysis;
	}).collect(Collectors.toList());
	
	analysis.setPhraseAnalysis(phraseAnalysis);
	postProcessAnalysis(analysis);
	callService.updateInternal(anUpdate(call).withAnalysis(analysis));
    }
    
    private void postProcessAnalysis(Analysis analysis) {
	int phraseCount = analysis.getPhraseAnalysis().size();
	int detectionCount = (int) asStream(analysis.getPhraseAnalysis())
		.map(PhraseAnalysis::getDetections)
		.filter(detections -> asStream(detections).anyMatch(detection -> detection.wasDetected(DEFAULT_DETECTION_THRESHOLD))).count();
	analysis.setDetectedPhraseCount(detectionCount);
	analysis.setDetectionRatio(phraseCount > 0 ? ((double)detectionCount) / ((double)phraseCount) : 0.0);
    }

}
