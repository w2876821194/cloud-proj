package com.imooc.platform.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

@Slf4j
@Configuration
public class RouteConfiguration {

    // 可导入自定义断言工厂
//    @Autowired
//    private RoutePredicateFactory factory;

    @Autowired
    private KeyResolver hostAddressKeyResolver;

    @Autowired
    @Qualifier("employeeRateLimiter")
    private RateLimiter restroomRateLimiter;

    @Bean
    @Order(-1)
    public GlobalFilter globalFilter() {
        return ((exchange, chain) -> {
            //PRE logic
            log.info("PRE filter");
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        // POST logic
                        log.info("POST filter");
                    })
            );
        });
    }


    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(route -> route
                                .path("/restroom/**")
                                .filters(f -> f.requestRateLimiter(limiter -> {
                                                    // 添加自定义限流规则
                                                    limiter.setKeyResolver(hostAddressKeyResolver);
                                                    // 添加自定义限流器
                                                    limiter.setRateLimiter(restroomRateLimiter);
                                                    //如果被限流返回数字
                                                    limiter.setStatusCode(HttpStatus.BAD_GATEWAY);
                                                }
                                        )
                                        // 在这编写hystrix限流
                                        .hystrix(filter -> {
                                                    filter.setName("fallbackName");
                                                    filter.setFallbackUri("forward:/global-errors");
                                        })
                                )
                                .uri("lb://restroom-service")
                )
                .route(route -> route
                        .path("/employee/**")
                        .uri("lb://employee-service")
                )
                .build();
    }

    @RestController
    public class ErrorHandler {
        @RequestMapping("/global-errors")
        public String errorMethod() {
            return "Hystrix fallback";
        }
    }


}
