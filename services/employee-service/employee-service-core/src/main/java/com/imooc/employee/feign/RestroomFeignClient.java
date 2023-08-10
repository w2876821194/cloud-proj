package com.imooc.employee.feign;

import com.imooc.employee.feign.fallback.RestroomFeignFallback;
import com.imooc.restroom.api.IRestroomService;
import com.imooc.restroom.pojo.Toilet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(value = "restroom-service", fallback = RestroomFeignFallback.class)
public interface RestroomFeignClient extends IRestroomService {

    @GetMapping("/restroom/checkAvailable")
    public List<Toilet> getAvailableToilet();

    @PostMapping("/restroom/occupy")
    public Toilet occupy(@RequestParam("id") Long id);

    @PostMapping("/restroom/release")
    public Toilet release(@RequestParam("id") Long id);

    @GetMapping("/restroom/checkAvailability")
    public boolean checkAvailability(@RequestParam("id") Long id);

    @PostMapping("/restroom/releaseTcc")
    public Toilet releaseTcc(@RequestParam("id") Long id);

}
