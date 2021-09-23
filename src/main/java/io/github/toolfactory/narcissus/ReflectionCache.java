/*
 * This file is part of Narcissus.
 *
 * Hosted at: https://github.com/toolfactory/narcissus
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.toolfactory.narcissus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReflectionCache -- caches fields and methods of a class. Use this if you need to use reflection to access
 * multiple methods and/or fields, for speed.
 * 
 * @author Luke Hutchison
 */
public class ReflectionCache {
    private Map<String, List<Method>> methodNameToMethods = new HashMap<>();
    private Map<String, Field> fieldNameToField = new HashMap<>();

    /**
     * Instantiate a new reflection cache for a class.
     *
     * @param cls
     *            the class to instantiate.
     */
    public ReflectionCache(Class<?> cls) {
        List<Method> methods = Narcissus.enumerateMethods(cls);
        for (Method method : methods) {
            List<Method> methodsForName = methodNameToMethods.get(method.getName());
            if (methodsForName == null) {
                methodNameToMethods.put(method.getName(), methodsForName = new ArrayList<>());
            }
            methodsForName.add(method);
        }
        List<Field> fields = Narcissus.enumerateFields(cls);
        for (Field field : fields) {
            fieldNameToField.put(field.getName(), field);
        }
    }

    /**
     * Instantiate a new reflection cache for a class.
     *
     * @param className
     *            the name of the class to instantiate.
     */
    public ReflectionCache(String className) {
        this(Narcissus.findClass(className));
    }

    /**
     * Get the field of the class that has a given field name.
     * 
     * @param fieldName
     *            The name of the field.
     * @return The {@link Field} object for the requested field name, or null if no such field was found in the
     *         class.
     */
    public Field getField(String fieldName) {
        return fieldNameToField.get(fieldName);
    }

    /**
     * Get all methods in the class for a given method name. (There may be multiple methods with the same name but
     * different parameter types).
     * 
     * @param methodName
     *            The name of the method.
     * @return A list of {@link Method} objects for methods of the requested method name, or null if no such method
     *         was found in the class.
     */
    public List<Method> getMethods(String methodName) {
        return methodNameToMethods.get(methodName);
    }

    /**
     * Get a method by name and parameter types.
     * 
     * @param methodName
     *            The name of the method.
     * @param paramTypes
     *            The types of the parameters of the method. For primitive-typed parameters, use e.g. Integer.TYPE.
     * @return The {@link Method} object for the matching method, or null if no such method was found in the class.
     */
    public Method getMethod(String methodName, Class<?>... paramTypes) {
        List<Method> methods = getMethods(methodName);
        if (methods != null) {
            for (Method method : methods) {
                if (Arrays.equals(method.getParameterTypes(), paramTypes)) {
                    return method;
                }
            }
        }
        return null;
    }
}
