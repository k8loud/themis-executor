package org.k8loud.executor.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Aspect
public class ExecutorAspect {
    @Around("@annotation(org.k8loud.executor.util.annotation.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getThis().getClass());

        Object output;
        Instant start = Instant.now();
        try {
            output = joinPoint.proceed();
        } finally {
            Instant end = Instant.now();
            log.info("Executed {} in {} ms", joinPoint.getSignature().getName(), getExecutionTimeInMillis(start, end));
        }

        return output;
    }

    @Around("@annotation(annotation)")
    public Object throwExceptionAndLogExecutionTime(ProceedingJoinPoint joinPoint,
                                                    ThrowExceptionAndLogExecutionTime annotation) throws Throwable {
        String fullExceptionClassName = String.format("%s.%s", CustomException.class.getPackageName(),
                annotation.exceptionClass());
        String fullExceptionCodeClassName = String.format("%s.code.%sCode", CustomException.class.getPackageName(),
                annotation.exceptionClass());

        Class<?> exceptionClass = ClassHelper.getClassFromName(fullExceptionClassName);
        validateInstanceOf(Exception.class, exceptionClass);

        Class<?> exceptionCodeClass = ClassHelper.getClassFromName(fullExceptionCodeClassName);
        validateInstanceOf(Enum.class, exceptionCodeClass);


        Object output;
        try {
            output = this.logExecutionTime(joinPoint);
        } catch (Exception e) {
            ClassParameter exceptionParameter = new ClassParameter(Exception.class, e);
            ClassParameter exceptionCodeParameter = new ClassParameter(Enum.class,
                    getEnumConstant(exceptionCodeClass, annotation.exceptionCode()));
            throw (Exception) ClassHelper.getInstance(exceptionClass, exceptionParameter, exceptionCodeParameter);
        }

        return output;
    }

    private void validateInstanceOf(Class<?> expectedClass, Class<?> givenClass) {
        if (givenClass.isAssignableFrom(expectedClass)) {
            throw new ClassCastException("Failed to cast. Expected " + expectedClass + " but was " + givenClass);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> T getEnumConstant(Class<?> enumClass, String constantName) {
        try {
            return Enum.valueOf((Class<T>) enumClass, constantName);
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new IllegalArgumentException("Enum " + enumClass + "does not have value " + constantName, e);
        }
    }

    private long getExecutionTimeInMillis(Instant start, Instant end) {
        return start.until(end, ChronoUnit.MILLIS);
    }
}
