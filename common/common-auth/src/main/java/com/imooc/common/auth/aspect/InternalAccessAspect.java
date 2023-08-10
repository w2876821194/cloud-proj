package com.imooc.common.auth.aspect;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InternalAccessAspect {

    private HttpServletRequest request;

    @Autowired
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Pointcut("@annotation(InternalAccess)")
    public void internalAccessPointcut() {
    }

    @Before("internalAccessPointcut()")
    public void beforeInternalAccess(JoinPoint joinPoint) {
        String sourceIp = request.getRemoteAddr();
        // 根据实际情况设置内网 IP 段
        boolean isInternal = isInternalIp(sourceIp);

        if (!isInternal) {
            throw new RuntimeException("Access denied. This API is for internal use only.");
        }
    }

    // 判断 IP 是否属于内网 IP 段
    private boolean isInternalIp(String ip) {
        // 编写判断逻辑，判断 IP 是否属于内网 IP 段
        // 返回 true 或 false
        return true;
    }

    @AfterReturning(pointcut = "internalAccessPointcut()", returning = "result")
    public void afterInternalAccess(JoinPoint joinPoint, Object result) {
        // 可以在此处编写在内网访问时的后置逻辑
    }
}
