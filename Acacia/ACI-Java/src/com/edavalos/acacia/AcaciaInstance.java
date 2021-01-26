package com.edavalos.acacia;

import java.util.HashMap;
import java.util.Map;

class AcaciaInstance {
    private AcaciaClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    AcaciaInstance(AcaciaClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    void put(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }

}
