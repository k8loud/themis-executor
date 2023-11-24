package org.k8loud.executor.actions.command;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;

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

    @Builder
    public ClearStorageAction(CommandExecutionService commandExecutionService, String host, Integer port,
                              String privateKey, String user,
                              String paths, String regexPattern, Date dateFrom, Date dateTo) {
        super(commandExecutionService, host, port, privateKey, user);
        this.paths = paths;
        this.regexPattern = regexPattern;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        paths = params.getRequiredParam("paths");
        regexPattern = params.getRequiredParam("regexPattern");
        dateFrom = params.getOptionalParamAsDate("dateFrom", new Date(Long.MIN_VALUE), DATE_FORMATTER);
        dateTo = params.getOptionalParamAsDate("dateTo", new Date(Long.MAX_VALUE), DATE_FORMATTER);
    }

    @Override
    protected String buildCommand() {
        // -depth fixes 'No such file or directory' error which occurs when
        // find is trying to enter the directory after it has been deleted
        return String.format("find %s -name '%s' -newermt %s ! -newermt %s -depth -exec rm -rf {} \\;",
                paths, regexPattern, DATE_FORMATTER.format(dateFrom), DATE_FORMATTER.format(dateTo));
    }
}
