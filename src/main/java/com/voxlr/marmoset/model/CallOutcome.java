package com.voxlr.marmoset.model;

import java.util.HashSet;
import java.util.Set;

public class CallOutcome {
  public static final String NONE = "None";
  public static final String LOST = "Lost";
  public static final String VOICEMAIL = "Voicemail";
  public static final String WON = "Won";
  public static final String PROGRESS = "Progress";

  @SuppressWarnings("serial")
  private static final Set<String> CALL_OUTCOMES =
      new HashSet<String>() {
        {
          add(NONE);
          add(LOST);
          add(VOICEMAIL);
          add(WON);
          add(PROGRESS);
        }
      };

  public static boolean validateOutcome(String outcome) {
    return CALL_OUTCOMES.contains(outcome);
  }
}
