package com.edavalos.acacia;

import java.util.List;
import java.util.Map;

class AcaciaClass implements AcaciaCallable {
    final String name;
    private final Map<String, AcaciaFunction> methods;

    AcaciaClass(String name, Map<String, AcaciaFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    AcaciaFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        AcaciaFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        else return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
        AcaciaInstance instance = new AcaciaInstance(this);
        AcaciaFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments, location);
        }

        return instance;
    }

    @Override
    public String name() {
        return name;
    }
}
