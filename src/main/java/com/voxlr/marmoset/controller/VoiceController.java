package com.voxlr.marmoset.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.voxlr.marmoset.model.dto.SimpleResponseDTO;
import com.voxlr.marmoset.service.TwilioService;

@RestController
public class VoiceController extends ApiController {
    public static final String VOICE = "/voice";

    @Autowired
    private TwilioService twilioService;
    
    @RequestMapping(
	    method = RequestMethod.GET,
	    value = VOICE + "/token")
    public ResponseEntity<?> getToken() {
	String token = twilioService.getClientToken();
	SimpleResponseDTO<String> responseDTO = new SimpleResponseDTO<String>(token);
	return new ResponseEntity<SimpleResponseDTO<String>>(responseDTO, HttpStatus.OK);
    }
}
