package com.edavalos.acacia;

class AcaciaInstance {
    private AcaciaClass klass;

    AcaciaInstance(AcaciaClass klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }

}
