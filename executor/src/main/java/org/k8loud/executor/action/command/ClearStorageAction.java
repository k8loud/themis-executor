package org.k8loud.executor.action.command;

import data.Params;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClearStorageAction extends CommandAction {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    private String paths;
    private String regexPattern;
    private Date dateFrom;
    private Date dateTo;

    public ClearStorageAction(Params params, CommandExecutionService commandExecutionService) throws ActionException {
        super(params, commandExecutionService);
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        paths = params.getRequiredParam("paths");
        regexPattern = params.getRequiredParam("regexPattern");
        dateFrom = params.getOptionalParamAsDate("dateFrom", new Date(Long.MIN_VALUE), DATE_FORMATTER);
        dateTo = params.getOptionalParamAsDate("dateTo", new Date(Long.MAX_VALUE), DATE_FORMATTER);
    }

    @Override
    protected String performCommandAction() throws CommandException {
        return delegateCommandExecution(paths);
    }

    @Override
    protected String buildCommand(Object... args) {
        // -depth fixes 'No such file or directory' error which occurs when
        // find is trying to enter the directory after it has been deleted
        return String.format("find %s -name '%s' -newermt %s ! -newermt %s -depth -exec rm -rf {} \\;",
                args[0], regexPattern, DATE_FORMATTER.format(dateFrom), DATE_FORMATTER.format(dateTo));
    }
}
