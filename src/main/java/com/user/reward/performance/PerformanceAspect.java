package com.user.reward.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chaklader on 2019-04-15.
 */
@Aspect
@Component
public class PerformanceAspect {


    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void repositoryClassMethods() {

    }

    /**
     * This method will mesure and tell us the execution time of methods from the DAO layer
     * For example, we will get message like "Execution of findUsersWithRewardHistory took
     * 101 ms" etc
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("repositoryClassMethods()")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint)
            throws Throwable {

        long start = System.nanoTime();
        Object returnValue = joinPoint.proceed();

        long end = System.nanoTime();

        String methodName = joinPoint.getSignature().getName();
        System.out.println(
                "Execution of " + methodName + " took " +
                        TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");

        return returnValue;
    }
}
