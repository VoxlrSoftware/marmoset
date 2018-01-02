package com.voxlr.marmoset.model;

import java.util.Arrays;
import java.util.HashSet;

public class CallOutcome {
    public static final String NONE = "NONE";
    public static final String LOST = "Lost";
    public static final String VOICEMAIL = "Voicemail";
    public static final String WON = "Won";
    
    private static final HashSet<String> CALL_OUTCOMES = new HashSet<>(Arrays.asList(
	NONE,
	LOST,
	VOICEMAIL,
	WON
	));
    
    public static boolean validateOutcome(String outcome) {
	return CALL_OUTCOMES.contains(outcome);
    }
}
