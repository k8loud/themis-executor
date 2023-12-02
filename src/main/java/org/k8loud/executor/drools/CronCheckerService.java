package org.k8loud.executor.drools;

import org.quartz.CronExpression;

import java.util.Date;

public interface CronCheckerService {
    boolean checkPattern(String pattern, Date date);

    boolean checkPatternInRange(String pattern, Date dateFrom, Date dateTo);

    boolean checkPatternForSession(String pattern);

    CronExpression getCronExpression(String pattern);
}
