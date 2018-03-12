package com.voxlr.marmoset.model.persistence.factory;

import com.voxlr.marmoset.model.persistence.Call;
import com.voxlr.marmoset.model.persistence.Call.Analysis;
import com.voxlr.marmoset.model.persistence.CallStrategy;

public class CallUpdate extends EntityUpdate<Call> {

  public CallUpdate(Call call) {
    super(call);
  }

  public static enum DBField {
    RECORDING_URL("recordingUrl"),
    TRANSCRIPTION_ID("transcriptionId"),
    TRANSCRIPTION_URL("transcriptionUrl"),
    CALL_OUTCOME("callOutcome"),
    CALL_STRATEGY("callStrategy"),
    ANALYSIS("analysis"),
    HAS_BEEN_ANALYZED("hasBeenAnalyzed"),
    STAT_DURATION("statistics.duration"),
    STAT_TOTALTALKTIME("statistics.totalTalkTime"),
    STAT_CUSTOMERTALKTIME("statistics.customerTalkTime"),
    STAT_EMPLOYEETALKTIME("statistics.employeeTalkTime");

    private String fieldName;

    private DBField(String fieldName) {
      this.fieldName = fieldName;
    }

    public String get() {
      return fieldName;
    }
  }

  public static CallUpdate anUpdate(Call call) {
    return new CallUpdate(call);
  }

  public CallUpdate withRecordingUrl(String recordingUrl) {
    getUpdate().set(DBField.RECORDING_URL.get(), recordingUrl);
    return this;
  }

  public CallUpdate withTranscriptionId(String transcriptionId) {
    getUpdate().set(DBField.TRANSCRIPTION_ID.get(), transcriptionId);
    return this;
  }

  public CallUpdate withTranscriptionUrl(String transcriptionUrl) {
    getUpdate().set(DBField.TRANSCRIPTION_URL.get(), transcriptionUrl);
    return this;
  }

  public CallUpdate withCallOutcome(String callOutcome) {
    getUpdate().set(DBField.CALL_OUTCOME.get(), callOutcome);
    return this;
  }

  public CallUpdate withCallStrategy(CallStrategy callStrategy) {
    getUpdate().set(DBField.CALL_STRATEGY.get(), callStrategy);
    return this;
  }

  public CallUpdate withDuration(int duration) {
    getUpdate().set(DBField.STAT_DURATION.get(), duration);
    return this;
  }

  public CallUpdate withTotalTalkTime(int totalTalkTime) {
    getUpdate().set(DBField.STAT_TOTALTALKTIME.get(), totalTalkTime);
    return this;
  }

  public CallUpdate withCustomerTalkTime(int customerTalkTime) {
    getUpdate().set(DBField.STAT_CUSTOMERTALKTIME.get(), customerTalkTime);
    return this;
  }

  public CallUpdate withEmployeeTalkTime(int employeeTalkTime) {
    getUpdate().set(DBField.STAT_EMPLOYEETALKTIME.get(), employeeTalkTime);
    return this;
  }

  public CallUpdate withAnalysis(Analysis analysis) {
    getUpdate().set(DBField.ANALYSIS.get(), analysis).set(DBField.HAS_BEEN_ANALYZED.get(), true);
    return this;
  }
}
