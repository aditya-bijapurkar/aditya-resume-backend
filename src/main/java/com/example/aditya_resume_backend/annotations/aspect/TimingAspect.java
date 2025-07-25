package com.example.aditya_resume_backend.annotations.aspect;

import com.example.aditya_resume_backend.annotations.Time;
import com.example.aditya_resume_backend.annotations.service.MetricsService;
import com.example.aditya_resume_backend.exceptions.PrometheusMetricException;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class TimingAspect {
    private final MetricsService metricsService;
    private static final Logger logger = LoggerFactory.getLogger(TimingAspect.class);
    private static final String TOTAL_TIMER = "TOTAL_TIMER";

    public TimingAspect(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Around("@annotation(time)")
    public Object timeMethod(ProceedingJoinPoint joinPoint, Time time) throws Throwable {
        Timer.Sample startTimeSample = metricsService.getStartTimeSample();

        try{
            Object result = joinPoint.proceed();

            Timer totalTimer = metricsService.getTimer(TOTAL_TIMER);
            long nanosecondsElapsed = startTimeSample.stop(totalTimer);
            long timeInMilliseconds = nanosecondsElapsed / (long) 1e6;

            Timer timer = metricsService.getTimer(time.metricName());
            timer.record(timeInMilliseconds, TimeUnit.SECONDS);
            logger.info("Time taken for {} is {} milliseconds", time.metricName(), timeInMilliseconds);

            return result;
        }
        catch (Throwable throwable){
            throw new PrometheusMetricException(throwable);
        }
    }
}
