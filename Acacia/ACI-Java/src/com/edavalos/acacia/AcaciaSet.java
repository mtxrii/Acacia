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
