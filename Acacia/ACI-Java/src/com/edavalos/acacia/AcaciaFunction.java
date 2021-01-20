package com.edavalos.acacia;

import java.util.List;

class AcaciaFunction implements AcaciaCallable {
    private final Stmt.Function declaration;

    AcaciaFunction(Stmt.Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment innerEnvironment = new Environment(interpreter.globals);
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
