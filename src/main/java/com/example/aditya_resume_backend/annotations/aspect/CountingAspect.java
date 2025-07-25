package com.example.aditya_resume_backend.annotations.aspect;

import com.example.aditya_resume_backend.annotations.Count;
import com.example.aditya_resume_backend.annotations.service.MetricsService;
import io.micrometer.core.instrument.Counter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CountingAspect {
    private final MetricsService metricsService;

    public CountingAspect(MetricsService metricsService){
        this.metricsService = metricsService;
    }

    @Around("@annotation(count)")
    public Object countMethod(ProceedingJoinPoint joinPoint, Count count) throws Throwable {
        Counter counter = metricsService.getCounter(count.metricName());
        counter.increment();

        return joinPoint.proceed();
    }
}
