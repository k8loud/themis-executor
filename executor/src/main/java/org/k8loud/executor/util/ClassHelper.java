package org.k8loud.executor.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ClassHelper {

    public static Class<?> getClassFromName(String fullClassName) throws ClassNotFoundException {
        return Class.forName(fullClassName);
    }

    public static Object getInstance(Class<?> clazz,
                                     ClassParameter... classParameters) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] constructorClasses = Arrays.stream(classParameters)
                .map(ClassParameter::getClazz)
                .toArray(Class<?>[]::new);
        Object[] instanceArguments = Arrays.stream(classParameters).map(ClassParameter::getInstance).toArray();
        return clazz.getConstructor(constructorClasses).newInstance(instanceArguments);
    }


}
