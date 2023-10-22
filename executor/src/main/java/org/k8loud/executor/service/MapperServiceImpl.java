package org.k8loud.executor.service;

import data.ExecutionRQ;
import data.Params;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.action.ActionHelper;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.MapperException;
import org.k8loud.executor.openstack.OpenstackService;
import org.k8loud.executor.util.ClassHelper;
import org.k8loud.executor.util.ClassParameter;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static org.k8loud.executor.exception.code.MapperExceptionCode.INVALID_CONSTRUCTOR;
import static org.k8loud.executor.exception.code.MapperExceptionCode.NEW_INSTANCE_FAILURE;

@Slf4j
@Service
public class MapperServiceImpl implements MapperService {
    private final ActionHelper actionHelper = new ActionHelper();
    private final OpenstackService openstackService;
    private final CommandExecutionService commandExecutionService;

    public MapperServiceImpl(OpenstackService openstackService, CommandExecutionService commandExecutionService) {
        this.openstackService = openstackService;
        this.commandExecutionService = commandExecutionService;
    }

    @NotNull
    @Override
    public Action map(@NotNull ExecutionRQ executionRQ) throws MapperException, ActionException {
        String collectionName = executionRQ.getCollectionName();
        String actionName = executionRQ.getActionName();
        Params params = executionRQ.getParams();

        Class<?> actionClass = actionHelper.getActionClass(collectionName, actionName);
        List<ClassParameter> classParameters = new ArrayList<>(List.of(new ClassParameter(Params.class, params)));
        try {
            if (collectionName.equals("openstack")) {
                classParameters.add(new ClassParameter(OpenstackService.class, openstackService));
            } else if (collectionName.equals("command")) {
                classParameters.add(new ClassParameter(CommandExecutionService.class, commandExecutionService));
            }

            return (Action) ClassHelper.getInstance(actionClass, classParameters.toArray(ClassParameter[]::new));
        } catch (NoSuchMethodException e) {
            throw new MapperException(e, INVALID_CONSTRUCTOR);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new MapperException(e, NEW_INSTANCE_FAILURE);
        }
    }
}
