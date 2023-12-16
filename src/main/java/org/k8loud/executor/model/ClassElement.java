package org.k8loud.executor.model;

import java.util.List;

public record ClassElement(String name, String packageName, List<ClassField> fields) {
}
