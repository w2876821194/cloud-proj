package com.imooc.restroom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RefreshScope
public class RestroomServiceTest {

    // Nacos配置中心读取
    @Value("${restroom.value1:100}")
    private Integer value1;
    // Nacos配置中心读取
    @Value("${restroom.value2:200}")
    private Integer value2;
    // Config配置中心读取
    @Value("${config.title:default}")
    private String value3;

    @GetMapping("/test")
    public Integer test() {
        log.info("config.title:{}",value3);
        try {
            throw new RuntimeException("111");
        }catch (RuntimeException e) {
            log.error("111{}",1,e);
            log.error("111");
        }

        return value1 + value2;
    }
}
