package com.voxlr.marmoset.controller;

import com.voxlr.marmoset.rest.PageableHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/" + ApiController.ROOT)
public abstract class ApiController implements InitializingBean {
  public static final String ROOT = "api";

  private PageableHandler pageableHandler = null;

  private void onControllerInit() {}

  public Map<String, List<String>> getSortOptions() {
    return new HashMap<>();
  }

  public Pageable page(Pageable pageable) {
    return pageableHandler.handleSort(pageable);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    pageableHandler = new PageableHandler(getSortOptions());
    onControllerInit();
  }
}
