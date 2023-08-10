package com.imooc.platform.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(route -> route
                        .path("/restroom-service/**")
                        .uri("lb://restroom-service")
                )
                .route(route -> route
                        .path("/employee-service/**")
                        .uri("lb://employee-service")
                )
                .build();
    }
}
