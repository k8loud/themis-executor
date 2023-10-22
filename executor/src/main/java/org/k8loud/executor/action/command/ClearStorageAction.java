package org.k8loud.executor.action.command;

import data.Params;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClearStorageAction extends CommandAction {
    private static final String PATHS_SEPARATOR = ";";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    private List<String> paths;
    private String regexPattern;
    private Date dateFrom;
    private Date dateTo;

    public ClearStorageAction(Params params, CommandExecutionService commandExecutionService) throws ActionException {
        super(params, commandExecutionService);
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        paths = List.of(params.getRequiredParam("paths").split(PATHS_SEPARATOR));
        regexPattern = params.getRequiredParam("regexPattern");
        dateFrom = params.getOptionalParamAsDate("dateFrom", new Date(Long.MIN_VALUE), DATE_FORMATER);
        dateTo = params.getOptionalParamAsDate("dateTo", new Date(Long.MAX_VALUE), DATE_FORMATER);
    }

    @Override
    protected void performCommandAction() throws CommandException {
        delegateCommandExecution("touch /home/ubuntu/brooks_was_here");

//        for (String path : paths) {
//            delegateCommandExecution("touch /home/ubuntu/brooks_was_here");
//        }
    }
}
