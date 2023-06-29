package org.k8loud.executor.service;

import data.ExecutionRQ;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.action.ActionHelper;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.MapperException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.k8loud.executor.exception.code.MapperExceptionCode.INVALID_CONSTRUCTOR;
import static org.k8loud.executor.exception.code.MapperExceptionCode.NEW_INSTANCE_FAILURE;

@Slf4j
@Service
public class MapperServiceImpl implements MapperService {
    private final ActionHelper actionHelper = new ActionHelper();

    @NotNull
    @Override
    public Action map(@NotNull ExecutionRQ executionRQ) throws MapperException, ActionException {
        String collectionName = executionRQ.getCollectionName();
        String actionName = executionRQ.getActionName();
        Map<String, String> params = executionRQ.getParams();

        Class<?> actionClass = actionHelper.getActionClass(collectionName, actionName);
        try {
            return (Action) actionClass.getConstructor(Map.class).newInstance(params);
        } catch (NoSuchMethodException e) {
            throw new MapperException(e, INVALID_CONSTRUCTOR);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new MapperException(e, NEW_INSTANCE_FAILURE);
        }
    }
}
