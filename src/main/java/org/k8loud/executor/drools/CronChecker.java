package org.k8loud.executor.drools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class CronChecker {
    private static final int RANGE_STEP_S = 1;
    private final Date sessionStartDate;
    private final int sessionIntervalSeconds;

    public boolean checkPatternForSession(String pattern) {
        if (getCronExpression(pattern) == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sessionStartDate);
        calendar.add(Calendar.SECOND, sessionIntervalSeconds);
        Date sessionEndDate = calendar.getTime();

        boolean result = checkPatternInRange(pattern, sessionStartDate, sessionEndDate);
        if (!result) {
            log.info("Pattern '{}' didn't match, the checked range was [{}, {}] with step {} s", pattern,
                    sessionStartDate, sessionEndDate, RANGE_STEP_S);
        }
        return result;
    }

    public static boolean checkPatternInRange(String pattern, Date dateFrom, Date dateTo) {
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

    public static boolean checkPattern(String pattern, Date date) {
        CronExpression cronExpression = getCronExpression(pattern);
        if (cronExpression != null) {
            boolean match = cronExpression.isSatisfiedBy(date);
            log.trace("pattern: '{}', date: '{}', match: {}", pattern, date, match);
            return match;
        }
        return false;
    }

    @Nullable
    private static CronExpression getCronExpression(String pattern) {
        try {
            return new CronExpression(pattern);
        } catch (ParseException e) {
            log.error("Invalid pattern: '{}', the exception: '{}'", pattern, e.toString());
            return null;
        }
    }
}
