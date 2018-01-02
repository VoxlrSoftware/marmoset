package com.voxlr.marmoset.callback;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.voxlr.marmoset.model.dto.CallbackResult;

public interface CallbackHandler {
    CallbackResult handleRequest(ObjectNode body);
}
