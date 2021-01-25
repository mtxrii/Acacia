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
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
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
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
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
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
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
            },

            // 'len(set|string)' - returns number of elements in something
            new AcaciaCallable() {
                final String name = "len";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return 1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
                    Object arg = arguments.get(0);
                    if (arg instanceof List) {
                        return (double)(((List) arg).size());
                    }
                    else if (arg instanceof String) {
                        return (double)(((String) arg).length());
                    }
                    else {
                        throw new RuntimeError(location, "Function '" + name + "' expected" +
                                "set or string as argument");
                    }
                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            }
    );
}
