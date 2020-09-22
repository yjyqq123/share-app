package com.soft1851.contentcenter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/content")
public class ContentHello {
    @GetMapping
    public String getHello(){
        return "hello content-center!";
    }
}