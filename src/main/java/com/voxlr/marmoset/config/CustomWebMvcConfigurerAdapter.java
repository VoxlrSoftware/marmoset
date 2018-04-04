package com.voxlr.marmoset.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.voxlr.marmoset.convert.ObjectIdConverter;
import com.voxlr.marmoset.convert.ObjectIdToStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CustomWebMvcConfigurerAdapter implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new ObjectIdConverter());
  }

  @Autowired private ObjectMapper objectMapper;

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    ObjectMapper webObjectMapper = objectMapper.copy();
    webObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    webObjectMapper.setDateFormat(new StdDateFormat());
    webObjectMapper.registerModule(new JodaModule());
    converters.add(new MappingJackson2HttpMessageConverter(webObjectMapper));
  }
}
