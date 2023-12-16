package org.k8loud.executor.service;

import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.model.ClassElement;
import org.k8loud.executor.model.ClassField;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class ActionService {

    private final String packageName = "org.k8loud.executor.actions";

    public List<ClassElement> getNonAbstractClasses() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));

        Set<ClassElement> nonAbstractClasses = new HashSet<>();

        return getClassElements(scanner, nonAbstractClasses, packageName);
    }

    public List<ClassElement> getNonAbstractClasses(String subPackageName) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));

        Set<ClassElement> nonAbstractClasses = new HashSet<>();
        String fullPackageName = getFullPackageName(subPackageName);
        return getClassElements(scanner, nonAbstractClasses, fullPackageName);
    }

    @NotNull
    private List<ClassElement> getClassElements(ClassPathScanningCandidateComponentProvider scanner, Set<ClassElement> nonAbstractClasses, String fullPackageName) {
        for (var beanDefinition : scanner.findCandidateComponents(fullPackageName)) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                if (!clazz.isInterface() && !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {

                    nonAbstractClasses.add(new ClassElement(
                            clazz.getSimpleName(),
                            clazz.getPackageName().substring(28), //org.k8loud.executor.actions. needs to be cut
                            getFieldsInfo(clazz)
                    ));
                }
            } catch (ClassNotFoundException e) {
                // Handle exceptions if needed
            }
        }

        return new LinkedList<>(nonAbstractClasses);
    }


    public ClassElement getNonAbstractClass(String subPackage, String actionName) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));

        String fullPackageName = getFullPackageName(subPackage);
        String className = getFullClassName(fullPackageName, actionName);
        for (var beanDefinition : scanner.findCandidateComponents(fullPackageName)) {
            if (className.equals(beanDefinition.getBeanClassName())) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    if (!clazz.isInterface() && !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                        return new ClassElement(
                                clazz.getSimpleName(),
                                clazz.getPackageName().substring(28),
                                getFieldsInfo(clazz)
                        );
                    }
                } catch (ClassNotFoundException e) {
                    // Handle exceptions if needed
                }
            }
        }

        // If no class is found, return null
        return null;
    }
    private String getFullPackageName(String subPackageName) {
        return "org.k8loud.executor.actions." + subPackageName;
    }

    private String getFullClassName(String packageName, String className) {
        return packageName + "." + className;
    }

    private String getFullClassName(String packageName, String subPackageName, String className) {
        return packageName + "." + subPackageName + "." + className;
    }


    private List<ClassField> getFieldsInfo(Class<?> clazz) {
        List<ClassField> fieldsInfo = new LinkedList<>();

        for (Field field : clazz.getDeclaredFields()) {
            ClassField response = new ClassField(field.getName(), field.getType());
            if (!field.getType().getPackageName().contains("org.k8loud.executor")) {
                fieldsInfo.add(response);
            }
        }

        return fieldsInfo;
    }

}
