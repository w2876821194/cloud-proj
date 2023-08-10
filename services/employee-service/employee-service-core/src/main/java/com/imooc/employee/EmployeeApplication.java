package com.imooc.employee;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCircuitBreaker
@EnableDiscoveryClient
@EnableJpaAuditing
//@EnableAutoDataSourceProxy
@ComponentScan(basePackages = {"com.imooc"})
@EnableFeignClients(basePackages = {"com.imooc"})
@SpringBootApplication
public class EmployeeApplication {

//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate () {
//        return new RestTemplate();
//    }

    @Bean
    Logger.Level feignLoggerData() {
        return Logger.Level.FULL;
    }

    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class);
    }

}
