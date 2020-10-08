package com.soft1851.usercenter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping(value = "test")
public class TestController {
    @Autowired
    private DiscoveryClient discoveryClient;
    private RestOperations restTemplate;

    @GetMapping("/discovery")
    public List<ServiceInstance> getInstances(){
        return this.discoveryClient.getInstances("user-center");
    }

    @GetMapping("/call/hello")
    public String callUserCenter(){
        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
        ServiceInstance serviceInstance = instances.get(new Random().nextInt(instances.size()));
        String targetUrl = serviceInstance.getUri()+"/user/hello";
//        String targetUrl = instances.stream()
//                .map(instance -> instance.getUri().toString()+"/user/hello")
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("当前没有示例！"));
        log.info("请求的目标地址：{}",targetUrl);
        return restTemplate.getForObject(targetUrl,String.class);
    }
}
