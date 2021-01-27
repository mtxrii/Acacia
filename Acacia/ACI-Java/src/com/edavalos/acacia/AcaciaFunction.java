package com.edavalos.acacia;

import java.util.List;

class AcaciaFunction implements AcaciaCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    AcaciaFunction(Stmt.Function declaration, Environment closure, boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    AcaciaFunction bind(AcaciaInstance instance) {
        Environment environment = new Environment(closure);
        environment.hardDefine("this", instance);
        return new AcaciaFunction(declaration, environment, isInitializer);
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
        Environment innerEnvironment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            innerEnvironment.define(declaration.params.get(i), arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, innerEnvironment);
        } catch (Return returnValue) {
            if (isInitializer) return closure.getAt(0, "this");
            else return returnValue.value;
        }

        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }

    @Override
    public String name() {
        return declaration.name.lexeme;
    }

    @Override
    public String toString() {
        return "<fn " + this.name() + ">";
    }
}
