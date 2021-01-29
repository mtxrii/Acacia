package com.edavalos.acacia;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AcaciaSet extends AcaciaInstance {
    private final List<Object> contents;
    private final Map<String, AcaciaCallable> methods;

    AcaciaSet(List<Object> contents) {
        super(null);
        this.contents = contents;
        this.methods = new HashMap<>();

        for (AcaciaCallable setMethod : Natives.setMethods) {
            methods.put(setMethod.name(), setMethod);
        }
    }

    Object get(int index) {
        return contents.get(convertIndex(index));
    }

    void put(int index, Object value) {
        contents.set(convertIndex(index), value);
    }

    double inc(int index, Token increment) {
        Object priorVal = get(index);
        if (!(priorVal instanceof Double)) {
            throw new RuntimeError(increment, "Invalid increment target.");
        }

        double newVal = ((Double) priorVal) + switch (increment.type) {
            case DOUBLE_PLUS -> 1.0;
            case DOUBLE_MINUS -> -1.0;
            case TRIPLE_PLUS -> ((Double) priorVal);
            case TRIPLE_MINUS -> -(((Double) priorVal) / 2);
            default -> 0.0;
        };

        put(index, newVal);
        return newVal;
    }

    int cSize() {
        return contents.size();
    }

    @Override
    public String toString() {
        return Acacia.stringify(contents);
    }

    int convertIndex(int index) {
        int length = contents.size();
        if (index >= 0) return index % length;
        else return index + length;
    }

}
