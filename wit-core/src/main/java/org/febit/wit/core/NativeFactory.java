// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.febit.wit.exceptions.AmbiguousMethodException;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.lang.method.MixedMultiNativeMethodDeclare;
import org.febit.wit.lang.method.MultiNativeMethodDeclare;
import org.febit.wit.lang.method.NativeConstructorDeclare;
import org.febit.wit.lang.method.NativeMethodDeclare;
import org.febit.wit.lang.method.NativeNewArrayDeclare;
import org.febit.wit.loggers.Logger;
import org.febit.wit.security.NativeSecurityManager;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class NativeFactory {

    protected final ConcurrentMap<Object, MethodDeclare> CACHE = new ConcurrentHashMap<>();

    protected Logger logger;
    protected NativeSecurityManager nativeSecurityManager;

    public MethodDeclare getNativeNewArrayMethodDeclare(Class componentType) {
        return createNativeNewArrayMethodDeclare(componentType, -1, -1, true);
    }

    public MethodDeclare getNativeNewArrayMethodDeclare(Class componentType, int line, int column) {
        return createNativeNewArrayMethodDeclare(componentType, line, column, true);
    }

    public MethodDeclare createNativeNewArrayMethodDeclare(Class componentType, int line, int column, boolean checkAccess) {
        Class classForCheck = componentType;
        while (classForCheck.isArray()) {
            classForCheck = classForCheck.getComponentType();
        }

        if (classForCheck == Void.class || classForCheck == Void.TYPE) {
            throw new ParseException("ComponentType must not Void.class", line, column);
        }

        if (checkAccess) {
            final String path = classForCheck.getName().concat(".[]");
            if (!this.nativeSecurityManager.access(path)) {
                throw new ParseException("Not accessable of native path: ".concat(path), line, column);
            }
        }

        return new NativeNewArrayDeclare(componentType);
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, Class[] paramTypes) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, true);
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName) {
        List<Method> methods = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            if (!ClassUtil.isPublic(clazz)
                    || !method.getName().equals(methodName)) {
                continue;
            }
            methods.add(method);
        }
        if (methods.isEmpty()) {
            throw new ScriptRuntimeException("Method not found： " + clazz.getName() + '#' + methodName);
        }
        if (methods.size() == 1) {
            return getNativeMethodDeclare(methods.get(0));
        }
        return createMultiNativeMethodDeclare(methods.toArray(new Method[methods.size()]));
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, Class[] paramTypes, boolean checkAccess) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, -1, -1, checkAccess);
    }

    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, Class[] paramTypes, int line, int column) {
        return getNativeMethodDeclare(clazz, methodName, paramTypes, line, column, true);
    }

    @SuppressWarnings("unchecked")
    public MethodDeclare getNativeMethodDeclare(Class clazz, String methodName, Class[] paramTypes, int line, int column, boolean checkAccess) {
        if (checkAccess) {
            final String path = StringUtil.concat(clazz.getName(), ".", methodName);
            if (!this.nativeSecurityManager.access(path)) {
                throw new ParseException("Not accessable of native path: ".concat(path), line, column);
            }
        }
        try {
            return getNativeMethodDeclare(clazz.getMethod(methodName, paramTypes));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    public MethodDeclare getNativeConstructorDeclare(Class clazz, Class[] paramTypes) {
        return getNativeConstructorDeclare(clazz, paramTypes, -1, -1, true);
    }

    public MethodDeclare getNativeConstructorDeclare(Class clazz, Class[] paramTypes, int line, int column) {
        return getNativeConstructorDeclare(clazz, paramTypes, line, column, true);
    }

    @SuppressWarnings("unchecked")
    public MethodDeclare getNativeConstructorDeclare(Class clazz, Class[] paramTypes, int line, int column, boolean checkAccess) {
        if (checkAccess) {
            final String path = clazz.getName().concat(".<init>");
            if (!this.nativeSecurityManager.access(path)) {
                throw new ParseException("Not accessable of native path: ".concat(path), line, column);
            }
        }
        try {
            return getNativeConstructorDeclare(clazz.getConstructor(paramTypes));
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }

    public MethodDeclare getNativeMethodDeclare(Method method) {
        MethodDeclare declare = CACHE.get(method);
        if (declare == null) {
            declare = createNativeMethodDeclare(method);
            MethodDeclare old = CACHE.putIfAbsent(method, declare);
            if (old != null) {
                return old;
            }
        }
        return declare;
    }

    public MethodDeclare getNativeConstructorDeclare(Constructor constructor) {
        MethodDeclare declare = CACHE.get(constructor);
        if (declare == null) {
            declare = createNativeConstructorDeclare(constructor);
            MethodDeclare old = CACHE.putIfAbsent(constructor, declare);
            if (old != null) {
                return old;
            }
        }
        return declare;
    }

    public MethodDeclare createNativeMethodDeclare(Method method) {
        return new NativeMethodDeclare(method);
    }

    public MethodDeclare createMultiNativeMethodDeclare(Method[] methods) {
        final boolean isStatic = ClassUtil.isStatic(methods[0]);
        boolean mix = false;
        for (int i = 1; i < methods.length; i++) {
            if (isStatic != ClassUtil.isStatic(methods[0])) {
                mix = true;
                break;
            }
        }
        return mix ? new MixedMultiNativeMethodDeclare(methods)
                : new MultiNativeMethodDeclare(methods, isStatic);
    }

    @Deprecated
    public MethodDeclare createMultiNativeMethodDeclare(Method[] methods, boolean isStatic) {
        return new MultiNativeMethodDeclare(methods, isStatic);
    }

    protected MethodDeclare createNativeConstructorDeclare(Constructor constructor) {
        return new NativeConstructorDeclare(constructor);
    }
}
