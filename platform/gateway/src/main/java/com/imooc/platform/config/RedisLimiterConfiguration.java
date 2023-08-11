package com.imooc.platform.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RedisLimiterConfiguration {

    // 限流规则
    @Bean
    @Primary
    public KeyResolver redisLimiterKey() {
        return exchange -> Mono.just(
                // 请求参数携带这个参数是1
                // 1 来了5次    2 来了3次
//                exchange.getRequest().getQueryParams().getFirst("customerId")
                // 访问路径
//                exchange.getRequest().getPath().value();
                // 主机地址
                exchange.getRequest().getRemoteAddress()
//                        .getAddress().getHostName()
                        .getAddress().getHostAddress()


        );
    }

    // 限流器
    @Bean("restroomRateLimiter")
    @Primary
    public RedisRateLimiter redisRateLimiter() {
        // 1: 每秒当中有多少个token可以产生
        // 1. 装令牌的桶总共有多少容量
        return new RedisRateLimiter(1,1);
    }

    // 限流器
    @Bean("employeeRateLimiter")
    @Primary
    public RedisRateLimiter employeeRedisRateLimiter() {
        // 1: 每秒当中有多少个token可以产生
        // 1. 装令牌的桶总共有多少容量
        return new RedisRateLimiter(1,1);
    }

}
