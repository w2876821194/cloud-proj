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
                                // 自定义断言
//                                .asyncPredicate(factory.applyAsync(e -> {
//                                            e.setPatterns(Lists.newArrayList());
//                                        }
//                                ))
                                .path("/restroom/**")
                                .filters(f -> f.requestRateLimiter(limiter -> {
                                    // 添加自定义限流规则
                                    limiter.setKeyResolver(hostAddressKeyResolver);
                                    // 添加自定义限流器
                                    limiter.setRateLimiter(restroomRateLimiter);
                                    //如果被限流返回数字
                                    limiter.setStatusCode(HttpStatus.BAD_GATEWAY);
                                }))
                                // 时间断言
//                        .or().before(ZonedDateTime.now())
//                        .or().after(ZonedDateTime.now())
//                        .or().between(ZonedDateTime.now(),ZonedDateTime.now().plusDays(1))
                                // cookie断言
//                        .or().cookie("name","regex")
                                // header
//                        .or().header("name")
//                        .or().header("name","regex")
                                // 参数断言
//                        .or().query("name")
//                        .or().query("name","regex")
                                // 请求方式
//                        .or().method(HttpMethod.GET)
                                // 远程服务地址
//                        .or().remoteAddr("192.168.92.1")
                                // 权重
//                        .or().weight("权重",1)
                                //各司其职
                                .uri("lb://restroom-service")
                )
                .route(route -> route
                        .path("/employee/**")
                        .uri("lb://employee-service")
                )
                .build();
    }
}
