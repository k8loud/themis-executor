package org.k8loud.executor.service;


import data.ActionRequest;

public interface ValidationService {

    boolean validate(ActionRequest request);

}
