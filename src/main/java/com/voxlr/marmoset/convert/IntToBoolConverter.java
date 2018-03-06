package com.voxlr.marmoset.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IntToBoolConverter implements Converter<Integer, Boolean> {

    @Override
    public Boolean convert(Integer source) {
	return source >= 1;
    }

}
