package com.imooc.restroom.service;

import com.imooc.restroom.api.IRestroomService;
import com.imooc.restroom.pojo.Toilet;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface IRestroomTccService extends IRestroomService {

    @TwoPhaseBusinessAction(
            name = "releaseTcc",
            commitMethod = "releaseCommit",
            rollbackMethod = "releaseCancel"
    )
    Toilet releaseTcc(@BusinessActionContextParameter(paramName = "id") Long id);

    boolean releaseCommit(BusinessActionContext actionContext);
    boolean releaseCancel(BusinessActionContext actionContext);


}
