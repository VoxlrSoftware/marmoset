package com.voxlr.marmoset.analysis.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
public class CallAnalysisResult {
  private ObjectId callId;
  private List<PhraseResult> phrases;

  @Getter
  @Setter
  @NoArgsConstructor
  public static class PhraseResult {
    private String phrase;
    private List<DetectionResult> detections;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class DetectionResult {
    private String type;
    private String matchedText;
    private double score;
  }
}
