package com.van.mc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.mc.common.InternalException;
import com.van.mc.controller.RestResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by van on 2016/12/6.
 */
@Aspect
@Component
public class ServiceExceptionHandle {

    @Autowired
    private PushService pushService;

    private final Logger logger= LoggerFactory.getLogger(ServiceExceptionHandle.class);

    private ObjectMapper objectMapper=new ObjectMapper();

    @Around(value = "@within(com.van.mc.common.AdviceAnnotation)")
    public Object handleException(ProceedingJoinPoint pjp) {
        RestResponse ret;
        try {
            return pjp.proceed();
        } catch (InternalException e){
            logger.warn(e.getMessage()+":"+pjp.getSignature());
            pushService.pushWarn(e.getMessage()+":"+pjp.getSignature());
            ret=new RestResponse(300,e.getMessage(),e);
        }catch (Throwable throwable) {
                ret= new RestResponse(500,throwable.getMessage(),throwable);
        }
        try {
            return objectMapper.writeValueAsString(ret);
        } catch (JsonProcessingException e) {
            return "{\"code\":500,\"msg\":\"error when serializing result into json on remote machine\"}";
        }
    }
}
