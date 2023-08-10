package com.imooc.restroom;

//import com.imooc.restroom.config.MyCustomJMXAuthenticator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.management.remote.JMXAuthenticator;

@EnableDiscoveryClient
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.imooc"})
@SpringBootApplication
public class RestroomApplication {

//    @Bean
//    public JMXAuthenticator jmxAuthenticator() {
//        return new MyCustomJMXAuthenticator();
//    }

    public static void main(String[] args) {
        SpringApplication.run(RestroomApplication.class);
    }
}
