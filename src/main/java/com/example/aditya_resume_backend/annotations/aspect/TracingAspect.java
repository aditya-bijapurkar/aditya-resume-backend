package com.example.aditya_resume_backend.annotations.aspect;

import com.example.aditya_resume_backend.annotations.Trace;
import com.example.aditya_resume_backend.constants.LoggerConstants;
import com.example.aditya_resume_backend.utils.LoggerUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
@SuppressWarnings("all")
public class TracingAspect {

    @Around("@annotation(trace)")
    public Object traceMethod(ProceedingJoinPoint joinPoint, Trace trace) throws Throwable {
        Object[] args = joinPoint.getArgs();
        String traceId = "";

        for (Object arg : args) {
            Class<?> argClass = arg.getClass();
            try {
                Field traceIdField = argClass.getDeclaredField(LoggerConstants.TRACE_ID_HEADER);
                traceIdField.setAccessible(true);
                if(traceIdField != null && traceIdField.get(arg) != null) {
                    traceId = traceIdField.get(arg).toString();
                    break;
                }
            }
            catch (NoSuchFieldException | IllegalAccessException e) {
                // Do nothing in case trace_id is not present
            }
        }

        try {
            LoggerUtils.setTraceContext(traceId);
            return joinPoint.proceed();
        }
        catch (Throwable throwable) {
            throw throwable;
        }
        finally {
            LoggerUtils.clearTraceContext();
        }
    }

}
