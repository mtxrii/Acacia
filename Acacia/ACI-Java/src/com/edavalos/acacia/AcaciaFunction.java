package com.edavalos.acacia;

import java.util.List;

class AcaciaFunction implements AcaciaCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    AcaciaFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    AcaciaFunction bind(AcaciaInstance instance) {
        Environment environment = new Environment(closure);
        environment.hardDefine("this", instance);
        return new AcaciaFunction(declaration, environment);
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
            return returnValue.value;
        }
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
