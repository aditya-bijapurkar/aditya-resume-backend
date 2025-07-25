package com.example.aditya_resume_backend.annotations.service;

import com.example.aditya_resume_backend.annotations.Count;
import com.example.aditya_resume_backend.annotations.Time;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Service
public class MetricsService implements ApplicationListener<ApplicationReadyEvent> {

    // Counters accessed via @Count custom annotation only
    // Timers accessed via @Time custom annotation only
    private final MeterRegistry meterRegistry;
    private static final String TOTAL_TIMER = "TOTAL_TIMER";
    private static final String SECONDS = "seconds";
    private static final String MILLISECONDS = "milliseconds";

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event){
        meterRegistry.timer(TOTAL_TIMER, "base_units", SECONDS);

        for(String beanName : event.getApplicationContext().getBeanDefinitionNames()){
            Object bean = event.getApplicationContext().getBean(beanName);
            for(Method method : bean.getClass().getDeclaredMethods()){

                Time time = AnnotationUtils.findAnnotation(method, Time.class);
                if(time != null){
                    meterRegistry.timer(time.metricName(), "api_name", time.apiName(), "method", time.method(), "base_unit", MILLISECONDS);
                }

                Count count = AnnotationUtils.findAnnotation(method, Count.class);
                if(count != null){
                    meterRegistry.counter(count.metricName());
                }

            }
        }
    }

    public Counter getCounter(String metricName) {
        return meterRegistry.counter(metricName);
    }

    public Timer getTimer(String metricName) {
        return meterRegistry.find(metricName)
                .timers().stream().findFirst().orElse(Timer.builder(metricName).register(meterRegistry));
    }

    public Timer.Sample getStartTimeSample(){
        return Timer.start(meterRegistry);
    }

}
