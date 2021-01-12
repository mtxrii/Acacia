package com.edavalos.acacia;

import java.util.HashMap;
import java.util.Map;

class Environment {
    // HashMap that holds all identifier->value bindings
    private final Map<String, Object> variables = new HashMap<>();

    // Looks up a variable and returns it, or throws error if it does not exist
    Object get(Token name) {
        if (variables.containsKey(name.lexeme)) {
            return variables.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    void assign(Token name, Object value) {
        if (variables.containsKey(name.lexeme)) {
            variables.put(name.lexeme, value);
        }

        else throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    // Saves a new variable, or throws an error if it already exists
    void define(Token name, Object value) {
        if (variables.containsKey(name.lexeme)) {
            throw new RuntimeError(name, "Variable '" + name.lexeme + "' already exists.");
        }

        else variables.put(name.lexeme, value);
    }
}
