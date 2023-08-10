package com.imooc.employee.feign.fallback;

import com.imooc.employee.feign.RestroomFeignClient;
import com.imooc.restroom.pojo.Toilet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RestroomFeignFallback implements RestroomFeignClient {
    @Override
    public List<Toilet> getAvailableToilet() {
        log.info("fallback");
        return null;
    }

    @Override
    public Toilet occupy(Long id) {
        return null;
    }

    @Override
    public Toilet release(Long id) {
        return null;
    }

    @Override
    public boolean checkAvailability(Long id) {
        return false;
    }

    @Override
    public Toilet releaseTcc(Long id) {
        return null;
    }
}
