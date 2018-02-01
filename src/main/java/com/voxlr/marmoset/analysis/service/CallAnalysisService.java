package com.voxlr.marmoset.analysis.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.voxlr.marmoset.analysis.model.CallAnalysisResult;
import com.voxlr.marmoset.analysis.model.CallAnalysisResult.PhraseResult;
import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.CallAnalysis;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis.Detection;
import com.voxlr.marmoset.model.persistence.PhraseAnalysis.DetectionType;
import com.voxlr.marmoset.service.domain.CallService;
import com.voxlr.marmoset.util.exception.EntityNotFoundException;

@Service
public class CallAnalysisService {
    
    @Autowired
    private CallService callService;
    
    public void processAnalysis(CallAnalysisResult result) throws EntityNotFoundException {
	Call call = callService.getInternal(result.getCallId());
	handleAnalysisResult(call, result);
    }
    
    @Async("analysisExecutor")
    private void handleAnalysisResult(Call call, CallAnalysisResult result) {
	CallAnalysis analysis = new CallAnalysis(call);
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
	});
	
	
    }

}
