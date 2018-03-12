package com.voxlr.marmoset.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

  public static final String VOXLR_STORE = "voxlr-store";
  public static final String VOXLR_STORE_RECORDINGS = VOXLR_STORE + "/recordings";
  public static final String VOXLR_STORE_TRANSCRIPTS = VOXLR_STORE + "/transcripts";

  @Bean
  public AmazonS3 amazonS3() {
    return AmazonS3Client.builder()
        .withRegion(Regions.US_EAST_1)
        .withCredentials(new DefaultAWSCredentialsProviderChain())
        .build();
  }
}
