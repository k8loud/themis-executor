package org.k8loud.executor.service;


import data.ExecutionRQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidationServiceImpl implements ValidationService {


    @Override
    public boolean validate(ExecutionRQ request) {
        return false;
    }

}
