package com.voxlr.marmoset.convert;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DateTimeConverter implements Converter<String, DateTime> {

    @Override
    public DateTime convert(String source) {
	return DateTime.parse(source);
    }

}
