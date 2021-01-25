package com.edavalos.acacia;

import java.util.Arrays;
import java.util.List;

public final class Natives {
    // This list (and entire file) holds every native function of Acacia
    static final List<AcaciaCallable> functions = Arrays.asList(
            // 'clock()' - returns current system time in seconds
            new AcaciaCallable() {
                public final String name = "clock";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return (double)System.currentTimeMillis() / 1000.0;
                }

                @Override
                public String toString() {
                    return "<native fn:" + name + ">";
                }
            },

            // 'print(object)' - prints to console
            new AcaciaCallable() {
                final String name = "print";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return -1; // wildcard to allow any number of arguments
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    StringBuilder printer = new StringBuilder();
                    for (Object arg : arguments) {
                        printer.append(Acacia.stringify(arg)).append(" ");
                    }
                    System.out.print(printer.toString().trim());
                    return null;
                }

                @Override
                public String toString() {
                    return "<native fn:" + name + ">";
                }
            },

            // 'println(object)' - prints to console with newline at the end
            new AcaciaCallable() {
                final String name = "println";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return -1; // wildcard to allow any number of arguments
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    StringBuilder printer = new StringBuilder();
                    for (Object arg : arguments) {
                        printer.append(Acacia.stringify(arg)).append(" ");
                    }
                    System.out.println(printer.toString().trim());
                    return null;
                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            }
    );
}
