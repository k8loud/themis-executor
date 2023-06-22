package org.k8loud.executor.service;

import data.ExecutionRS;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.action.Action;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExecutionServiceImpl implements ExecutionService {

    @Override
    public ExecutionRS execute(Action action) {
        log.info("Performing action: {}", action);
        return action.perform();
    }
}
