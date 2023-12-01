package org.k8loud.executor.util;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

@Slf4j
public class CronChecker {
    private CronChecker() {
        // Meant for static methods only, don't instantiate
    }

    public static boolean checkPattern(String pattern, Date date) {
        try {
            CronExpression cronExpression = new CronExpression(pattern);
            boolean match = cronExpression.isSatisfiedBy(date);
            log.debug("pattern: '{}', date: '{}', match: {}", pattern, date, match);
            return match;
        } catch (ParseException e) {
            log.error("Invalid pattern: '{}', the exception is: '{}'", pattern, e);
            return false;
        }
    }

    public static boolean checkPatternCurrDateTime(String pattern) {
        return checkPattern(pattern, new Date());
    }
}
