package com.voxlr.marmoset.validation.handler;

import java.util.function.Consumer;

import com.google.common.base.Supplier;
import com.voxlr.marmoset.model.AuthUser;

public interface ValidationHandler<T> {
    void validate(AuthUser authUser, Supplier<T> getter, Consumer<T> setter);
}
