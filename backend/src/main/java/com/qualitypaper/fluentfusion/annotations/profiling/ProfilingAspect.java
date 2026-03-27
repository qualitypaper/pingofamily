package com.qualitypaper.fluentfusion.annotations.profiling;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ProfilingAspect {

  @Around("@annotation(Profiling)")
  public Object profiling(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object proceed = joinPoint.proceed();
    log.info("Executing method: {}", joinPoint.getSignature().getName());
    log.info("Execution Result: {}", proceed);
    log.info("Execution Time: {} in millis", System.currentTimeMillis() - start);

    return proceed;
  }
}
