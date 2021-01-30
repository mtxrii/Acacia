package com.edavalos.acacia;

import java.util.*;

// This class holds every native function of Acacia
public final class Natives {
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
                    return "<native fn " + name + ">";
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
                    return "<native fn " + name + ">";
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
                                " set or string as argument");
                    }
                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            },

            // 'input(string)' - gets input from user in console, takes in optional string specifying input type
            new AcaciaCallable() {
                final String name = "input";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return -1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
                    String[] validTypes = {"boolean", "bool", "string", "number", "any"};
                    if (arguments.size() > 1) {
                        throw new RuntimeError(location, "Expected 0 or 1 arguments but got " +
                                arguments.size() + " (in '" + name + "').");
                    }
                    String arg = "any";
                    if (arguments.size() == 1) {
                        arg = Acacia.stringify(arguments.get(0)).toLowerCase();
                        if (!Arrays.asList(validTypes).contains(arg)) {
                            throw new RuntimeError(location, "'" + arg + "' is not a valid type " +
                                    "to convert to. Must be: 'boolean', 'string', 'number' or 'any'.");
                        }
                    }

                    java.util.Scanner input = new java.util.Scanner(System.in);
                    String given =  input.nextLine();
                    return switch (arg) {
                        case "boolean", "bool" -> {
                            if (Arrays.asList("t", "true", "yes", "1").contains(given.toLowerCase())) {
                                yield true;
                            }
                            else if (Arrays.asList("f", "false", "no", "0").contains(given.toLowerCase())) {
                                yield false;
                            }
                            throw new RuntimeError(location, "Cannot convert '" + given +
                                    "' to boolean.");
                        }
                        case "string" -> given;
                        case "number" -> {
                            try {
                                yield Double.parseDouble(given);
                            } catch (NumberFormatException e) {
                                throw new RuntimeError(location, "Cannot convert '" + given +
                                        "' to number.");
                            }
                        }
                        default -> {
                            if (Arrays.asList("t", "true", "yes", "1").contains(given.toLowerCase())) {
                                yield true;
                            }
                            else if (Arrays.asList("f", "false", "no", "0").contains(given.toLowerCase())) {
                                yield false;
                            }
                            try {
                                yield Double.parseDouble(given);
                            } catch (NumberFormatException e) {
                                yield given;
                            }
                        }
                    };
                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            },

            // 'sleep(number)' - pauses thread, takes in number of milliseconds to wait
            new AcaciaCallable() {
                final String name = "sleep";

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
                    if (!(arguments.get(0) instanceof Double)) {
                        throw new RuntimeError(location, "Function '" + name + "' expected" +
                                " number as argument");
                    }
                    try {
                        Thread.sleep(((Double) arguments.get(0)).longValue());
                    } catch (InterruptedException ignore) {}

                    return null;
                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            }
    );



    static final List<AcaciaCallable> setMethods = Arrays.asList(
            // '.sort()' - sorts a set and returns it
            new AcaciaCallable() {
                public final String name = "sort";

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
                    if (!(arguments.get(0) instanceof AcaciaSet)) return null;
                    List<Object> set = ((AcaciaSet) arguments.get(0)).getAll();

                    set.sort(new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            if (o1 instanceof AcaciaClass ||
                                o1 instanceof AcaciaInstance ||
                                o1 instanceof AcaciaFunction) {
                                    throw new RuntimeError(location, "Can't sort functions or classes.");
                            }
                            if (o2 instanceof AcaciaClass ||
                                o2 instanceof AcaciaInstance ||
                                o2 instanceof AcaciaFunction) {
                                    throw new RuntimeError(location, "Can't sort functions or classes.");
                            }

                            return Double.compare(Acacia.weight(o1), Acacia.weight(o2));
                        }
                    });
                    return set;
                }

                @Override
                public String toString() {
                    return "<set method " + name + ">";
                }
            },

            // '.join(str) - returns a string from elements in a set with delimiter provided'
            new AcaciaCallable() {
                public final String name = "join";

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
                    if (!(arguments.get(0) instanceof AcaciaSet)) return null;
                    List<Object> set = ((AcaciaSet) arguments.get(0)).getAll();

                    if (!(arguments.get(1) instanceof String)) {
                        throw new RuntimeError(location, "Expected string as argument.");
                    }
                    String delim = ((String) arguments.get(1));
                    List<String> elems = new ArrayList<>();
                    for (Object elem : set) {
                        if (elem instanceof String) {
                            elems.add(((String) elem));
                        } else {
                            elems.add(Acacia.stringify(elem));
                        }
                    }
                    return String.join(delim, elems);
                }

                @Override
                public String toString() {
                    return "<set method " + name + ">";
                }
            }
    );



    static final List<AcaciaCallable> stringMethods = Arrays.asList(
            // '.split(str)' - splits a string at the delimiter provided and returns a set
            new AcaciaCallable() {
                public final String name = "split";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return -1;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
                    if (!(arguments.get(0) instanceof String)) return null;
                    String str = ((String) arguments.get(0));

                    if (arguments.size() > 2) {
                        throw new RuntimeError(location, "Expected 0 or 1 arguments but got " +
                                (arguments.size()-1) + " (in '" + name + "').");
                    }

                    String delim = " ";
                    if (arguments.size() == 2) {
                        if (!(arguments.get(1) instanceof String)) {
                            throw new RuntimeError(location, "Expected string as argument.");
                        }
                        delim = ((String) arguments.get(1));
                    }

                    List<Object> split = Arrays.asList(((Object[]) str.split(delim)));
                    return new AcaciaSet(split);
                }

                @Override
                public String toString() {
                    return "<string method " + name + ">";
                }
            }

            // '.strip() - removes whitespaces from the beginning and end of a string'

            // '.replace(str, str) - replaces given string with another given string inside a string'

    );
}
