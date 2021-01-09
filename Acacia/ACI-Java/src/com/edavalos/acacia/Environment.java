package com.edavalos.acacia;

import java.util.HashMap;
import java.util.Map;

class Environment {
    // HashMap that holds all identifier->value bindings
    private final Map<String, Object> variables = new HashMap<>();

    // Checks if variable already exists, and throws error if if does, otherwise saves new variable binding
    void define(String name, Object value, int line) {
        if (variables.containsKey(name))
            Acacia.error(line, "Variable '" + name + "' already exists.");

        else variables.put(name, value);
    }
}
