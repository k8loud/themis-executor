package org.k8loud.executor.service;

import data.ExecutionRS;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.action.Action;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExecutionServiceImpl implements ExecutionService {
    @NotNull
    @Override
    public ExecutionRS execute(@NotNull Action action) {
        log.info("Executing action: {}", action.getClass());
        return action.execute();
    }
}
