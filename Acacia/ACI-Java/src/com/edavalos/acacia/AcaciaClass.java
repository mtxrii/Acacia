package com.edavalos.acacia;

import java.util.List;

class AcaciaClass implements AcaciaCallable {
    final String name;

    AcaciaClass(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
        AcaciaInstance instance = new AcaciaInstance(this);
        return instance;
    }

    @Override
    public String name() {
        return name;
    }
}
