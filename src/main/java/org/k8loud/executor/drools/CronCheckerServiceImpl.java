package org.k8loud.executor.drools;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Service
public class CronCheckerServiceImpl implements CronCheckerService {
    private static final int FIXED_RATE_S = 60; // TODO: env var
    private static final int RANGE_STEP_S = 1;

    public boolean checkPattern(String pattern, Date date) {
        try {
            CronExpression cronExpression = new CronExpression(pattern);
            boolean match = cronExpression.isSatisfiedBy(date);
            log.trace("pattern: '{}', date: '{}', match: {}", pattern, date, match);
            return match;
        } catch (ParseException e) {
            log.error("Invalid pattern: '{}', the exception is: '{}'", pattern, e);
            return false;
        }
    }

    public boolean checkPatternInRange(String pattern, Date dateFrom, Date dateTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTo);
        Date date = calendar.getTime();
        boolean result = false;
        while (dateFrom.before(date)) {
            result = checkPattern(pattern, date);
            if (result) {
                break;
            }
            calendar.add(Calendar.SECOND, -RANGE_STEP_S);
            date = calendar.getTime();
        }
        return result;
    }

    public boolean checkPatternForSession(String pattern) {
        Date dateTo = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTo);
        calendar.add(Calendar.SECOND, -FIXED_RATE_S);
        Date dateFrom = calendar.getTime();

        boolean result = checkPatternInRange(pattern, dateFrom, dateTo);
        if (!result) {
            log.info("Pattern '{}' didn't match, the checked range was [{}, {}] with step {} s", pattern, dateFrom,
                    dateTo, RANGE_STEP_S);
        }
        return result;
    }
}
