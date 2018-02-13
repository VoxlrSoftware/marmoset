package com.voxlr.marmoset.analysis.service;

import static com.voxlr.marmoset.model.persistence.factory.CallUpdate.anUpdate;
import static com.voxlr.marmoset.util.StreamUtils.asStream;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.analysis.model.CallAnalysisResult;
import com.voxlr.marmoset.analysis.model.CallAnalysisResult.PhraseResult;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.Call.Analysis;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis.Detection;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis.DetectionType;
import com.voxlr.marmoset.service.domain.CallService;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

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
	Stream<PhraseResult> phrases = result.getPhrases().stream();
	
	expectedPhrases.stream().forEach(phrase -> {
	    PhraseAnalysis phraseAnalysis = new PhraseAnalysis(phrase);
	    
	    try {
		PhraseResult phraseResult = phrases.filter(x -> x.getPhrase().equals(phrase)).findFirst().get();
		phraseResult.getDetections().stream().forEach(detectionResult -> {
		    Detection detection = new Detection();
		    detection.setMatchedText(detectionResult.getMatchedText());
		    detection.setScore(detectionResult.getScore());
		    detection.setType(DetectionType.fromType(detectionResult.getType()));
		    phraseAnalysis.addDetection(detection);
		});
	    } catch (Exception e) {}
	    
	    analysis.addPhraseAnalysis(phraseAnalysis);
	    postProcessAnalysis(analysis);
	});
	
	callService.updateInternal(anUpdate(call).withAnalysis(analysis));
    }
    
    private void postProcessAnalysis(Analysis analysis) {
	int phraseCount = analysis.getPhraseAnalysis().size();
	int detectionCount = (int) asStream(analysis.getPhraseAnalysis())
		.map(PhraseAnalysis::getDetections)
		.filter(detections -> asStream(detections).anyMatch(detection -> detection.wasDetected(DEFAULT_DETECTION_THRESHOLD))).count();
	analysis.setDetectionRatio(phraseCount > 0 ? detectionCount / phraseCount : 0);
    }

}
