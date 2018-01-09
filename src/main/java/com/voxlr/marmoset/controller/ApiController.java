package com.voxlr.marmoset.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/" + ApiController.ROOT)
public abstract class ApiController {
    public static final String ROOT = "api";
}
