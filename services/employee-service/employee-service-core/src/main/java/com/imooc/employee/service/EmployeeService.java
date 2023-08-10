package com.imooc.employee.service;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.imooc.employee.api.IEmployeeActivityService;
import com.imooc.employee.dao.EmployeeActivityDao;
import com.imooc.employee.entity.EmployeeActivityEntity;
import com.imooc.employee.enums.ActivityType;
import com.imooc.employee.pojo.EmployeeActivity;
import com.imooc.restroom.pojo.Toilet;
import com.imooc.employee.feign.RestroomFeignClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeService implements IEmployeeActivityService {

    @Autowired
    private EmployeeActivityDao employeeActivityDao;

//    @Autowired
//    private RestTemplate restTemplate;

    @Autowired
    private RestroomFeignClient restroomService;

    @GetMapping("/test")
    public int test() {
        return 1;
    }

    @Override
    @GlobalTransactional(name = "toilet-serv", rollbackFor = Exception.class)
    @PostMapping("/toilet-break")
    @HystrixCommand(
            commandKey = "hystrixTest", // 全局唯一标识
            groupKey = "test",  // 全局服务分组,用于组织仪表盘,统计信息 (默认值=类名)
            fallbackMethod = "testUseToiletFallback",   // 降级方法
            threadPoolKey = "threadPoolA",  // 线程池名称
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "30"),  // 核心线程数
                    @HystrixProperty(name = "maxQueueSize", value = "50")   // 最大队列长度
            },
            commandProperties = {}  //通用配置
    )
    public EmployeeActivity useToilet(Long employeeId) {
        int count = employeeActivityDao.countByEmployeeIdAndActivityTypeAndActive(
                employeeId, ActivityType.TOILET_BREAK, true
        );
        if (count > 0) {
            throw new RuntimeException("快拉!");
        }

        // 发起远程调用
//        Toilet[] toilets = restTemplate.getForObject(
//                "http://restroom-service/toilet-service/checkAvailable/",
//                Toilet[].class
//        );

        List<Toilet> toilets = restroomService.getAvailableToilet();


        if (CollectionUtils.isEmpty(toilets)) {
            throw new RuntimeException("shit in urinal");
        }


        // 抢坑

//        MultiValueMap<String, Object> args = new LinkedMultiValueMap<>();
//        args.add("id", toilets[0].getId());
//        Toilet toilet = restTemplate.postForObject(
//                "http://restroom-service/toilet-service/occupy",
//                args,
//                Toilet.class);

        Toilet toilet = restroomService.occupy(toilets.get(0).getId());

        // 保存如厕记录
        EmployeeActivityEntity toiletBreak = EmployeeActivityEntity.builder()
                .employeeId(employeeId)
                .active(true)
                .activityType(ActivityType.TOILET_BREAK)
                .resourceId(toilet.getId())
                .build();
        employeeActivityDao.save(toiletBreak);

        EmployeeActivity result = new EmployeeActivity();
        BeanUtils.copyProperties(toiletBreak, result);

        return result;
//        throw new RuntimeException("分布式");
    }

    public EmployeeActivity testUseToiletFallback(Long employeeId) {
        log.info("testUseToiletFallback");
        return null;
    }


    @Override
    @GlobalTransactional(name = "toilet-release", rollbackFor = Exception.class)
    @PostMapping("/done")
    public EmployeeActivity restoreToilet(Long activityId) {
        EmployeeActivityEntity record = employeeActivityDao.findByEmployeeId(activityId)
                .orElseThrow(() -> new RuntimeException("record not found"));
        if (!record.isActive()) {
            throw new RuntimeException("activity is not longer active");
        }

        // 释放坑
//        MultiValueMap<String, Object> args = new LinkedMultiValueMap<>();
//        args.add("id", record.getResourceId());
//        restTemplate.postForObject(
//                "http://restroom-service/toilet-service/release",
//                args,
//                Toilet.class);
//        restroomService.release(record.getResourceId());
        restroomService.releaseTcc(record.getResourceId());

        record.setActive(false);
        record.setEndTime(new Date());
        employeeActivityDao.save(record);

        EmployeeActivity result = new EmployeeActivity();
        BeanUtils.copyProperties(record, result);
        return result;
    }
}
