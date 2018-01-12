package com.voxlr.marmoset.callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMethod;

import com.voxlr.marmoset.service.CallbackService.CallbackType;
import com.voxlr.marmoset.service.CallbackService.Platform;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Callback {
    CallbackType type();
    RequestMethod[] methods() default { RequestMethod.POST };
    Platform platform();
}
