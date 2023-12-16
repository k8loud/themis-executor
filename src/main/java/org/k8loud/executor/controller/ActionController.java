package org.k8loud.executor.controller;

import org.k8loud.executor.model.ClassElement;
import org.k8loud.executor.service.ActionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/actions")
public class ActionController {

    private final ActionService actionService;

    public ActionController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping(value = "")
    public ResponseEntity<List<ClassElement>> getActions() {
        return new ResponseEntity<>(actionService.getNonAbstractClasses(), HttpStatus.OK);
    }

    @GetMapping(value = "/{subPackageName}")
    public ResponseEntity<List<ClassElement>> getAction(@PathVariable String subPackageName) {
        return new ResponseEntity<>(actionService.getNonAbstractClasses(subPackageName), HttpStatus.OK);
    }

    @GetMapping(value = "/{subPackageName}/{actionName}")
    public ResponseEntity<ClassElement> getAction(@PathVariable String subPackageName, @PathVariable String actionName) {
        return new ResponseEntity<>(actionService.getNonAbstractClass(subPackageName, actionName), HttpStatus.OK);
    }
}
