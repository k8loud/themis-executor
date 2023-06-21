package org.k8loud.executor.service;

import data.ExecutionRQ;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.action.ActionHelper;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Slf4j
@Service
public class MapperServiceImpl implements MapperService {
    private final ActionHelper actionHelper = new ActionHelper();

    @Override
    public Action map(ExecutionRQ executionRQ) {
        String collectionName = executionRQ.getCollectionName();
        String actionName = executionRQ.getActionName();
        Class<?> actionClass = actionHelper.getActionClass(collectionName, actionName);
        Action action = null;
        try {
            Map<String, String> params = executionRQ.getParams();
            action = (Action) actionClass.getConstructor(Map.class).newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 NullPointerException e) {
            log.error("Failed to map `{}` to Action object, details: `{}`", executionRQ, e.getMessage());
        }
        return action;
    }
}
