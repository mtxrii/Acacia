package com.edavalos.acacia;

import java.util.HashMap;
import java.util.Map;

class Environment {
    // HashMap that holds all identifier->value bindings
    private final Map<String, Object> variables = new HashMap<>();

    // Environment to link global scope to various inner blocks
    final Environment enclosing;

    // Constructor for global scope
    Environment() {
        enclosing = null;
    }

    // Constructor for local scopes that inherit the global
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    // Looks up a variable and returns it, or throws error if it does not exist
    Object get(Token name) {
        if (variables.containsKey(name.lexeme)) {
            return variables.get(name.lexeme);
        }

        // checks parent as well
        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    // Looks up a variable and assigns a new value to it, or throws an error if it does not exist
    void assign(Token name, Object value) {
        if (variables.containsKey(name.lexeme)) {
            variables.put(name.lexeme, value);
        }

        // checks parent as well
        else if (enclosing != null) {
            enclosing.assign(name, value);
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
