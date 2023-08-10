package com.imooc.restroom.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.google.common.collect.Lists;
import com.imooc.restroom.converter.ToiletConverter;
import com.imooc.restroom.dao.ToiletDao;
import com.imooc.restroom.entity.ToiletEntity;
import com.imooc.restroom.pojo.Toilet;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/restroom")
public class RestroomService implements IRestroomTccService {

    @Autowired
    private ToiletDao toiletDao;

    @Override
    @GetMapping("/checkAvailable")
    @SentinelResource(value = "checkAvailable",fallback = "getAvailableToiletFallback")
    public List<Toilet> getAvailableToilet() {
        List<ToiletEntity> result = toiletDao.findAllByCleanAndAvailable(true, true);

        return result.stream()
                .map(ToiletConverter::convert)
                .collect(Collectors.toList());
    }
    public List<Toilet> getAvailableToiletFallback() {
        log.info("getAvailableToilet - Fallback");
        return Lists.newArrayList();
    }



    @Override
    @PostMapping("/occupy")
    public Toilet occupy(Long id) {
        ToiletEntity toilet = toiletDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Toilet not found"));

        if (!toilet.isAvailable() || !toilet.isClean()) {
            throw new RuntimeException("restroom not available or unclean");
        }

        toilet.setAvailable(false);
        toilet.setClean(false);
        toiletDao.save(toilet);

        return ToiletConverter.convert(toilet);
    }

    @Override
    @PostMapping("/release")
    public Toilet release(Long id) {
        ToiletEntity toilet = toiletDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Toilet not found"));

        toilet.setAvailable(true);
        toilet.setClean(true);
        toiletDao.save(toilet);

        return ToiletConverter.convert(toilet);
    }

    @TwoPhaseBusinessAction(name = "releaseTcc", commitMethod = "releaseCommit", rollbackMethod = "releaseCancel")
    @Override
    @PostMapping("/releaseTcc")
    public Toilet releaseTcc(Long id) {
        try {
            log.info("*****Try release TCC *** id={}", id);
            ToiletEntity toilet = toiletDao.findById(id)
                    .filter(e -> !e.isReserved())
                    .orElseThrow(() -> new RuntimeException("Toilet not found"));
//            toilet.setClean(true);
//            toilet.setAvailable(true);
            toilet.setReserved(true);
            toiletDao.save(toilet);
            return ToiletConverter.convert(toilet);
        } catch (Exception e) {
            log.error("*****cannot release the restroom", e);
            throw e;
        }
    }

    @Override
    public boolean releaseCommit(BusinessActionContext actionContext) {
        try {
            Long id = Long.parseLong(actionContext.getActionContext("id").toString());
            log.info("*****Confirm release TCC id={}, xid={}*****", id, actionContext.getXid());
            Optional<ToiletEntity> operation = toiletDao.findById(id);

            if (operation.isPresent() && operation.get().isReserved()) {
                ToiletEntity entity = operation.get();
                entity.setClean(true);
                entity.setAvailable(true);
                entity.setReserved(false);
                toiletDao.save(entity);
                log.info("Clean:{},Available:{},Reserved:{}", entity.isClean(), entity.isAvailable(), entity.isReserved());
                return true;
            }
//            else {
//                return false;
//            }
        } catch (Exception e) {
            log.error("*****Cannot release the restroom", e);
            return false;
        }
        return false;
    }

    @Override
    public boolean releaseCancel(BusinessActionContext actionContext) {
        try {
            Long id = Long.parseLong(actionContext.getActionContext("id").toString());
            log.info("*****Cancel release TCC id={}, xid={}*****", id, actionContext.getXid());
            Optional<ToiletEntity> operation = toiletDao.findById(id);

            if (operation.isPresent()) {
                ToiletEntity entity = operation.get();
                entity.setClean(false);
                entity.setAvailable(false);
                entity.setReserved(false);
                toiletDao.save(entity);
                return true;
            }
        } catch (Exception e) {
            log.error("*****Cancel release the restroom", e);
            return false;
        }
        return false;
    }


    @Override
    @GetMapping("/checkAvailability")
    public boolean checkAvailability(Long id) {
        ToiletEntity toilet = toiletDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Toilet not found"));
        return toilet.isAvailable();
    }
}
