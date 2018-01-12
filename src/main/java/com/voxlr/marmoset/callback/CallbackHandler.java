package com.voxlr.marmoset.callback;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.model.dto.CallbackResult;

public interface CallbackHandler {
    CallbackResult handleRequest(String requestPath, ObjectNode body);
    void initialize(ApplicationContext applicationContext);
}
