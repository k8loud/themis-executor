package org.k8loud.executor.service;


import data.ExecutionRQ;

public interface ValidationService {

    boolean validate(ExecutionRQ request);

}
