package com.edavalos.acacia;

import java.util.HashMap;
import java.util.Map;

class Environment {
    // HashMap that holds all identifier->value bindings
    private final Map<String, Object> variables = new HashMap<>();

    Object get(Token name) {
        if (variables.containsKey(name.lexeme)) {
            return variables.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    void define(Token name, Object value) {
        if (variables.containsKey(name.lexeme)) {
            throw new RuntimeError(name, "Variable '" + name.lexeme + "' already exists.");
        }

        else variables.put(name.lexeme, value);
    }
}
