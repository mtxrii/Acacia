package com.edavalos.acacia;

import java.util.*;

// This class holds every native function of Acacia
public final class Natives {
    // List of valid strings that represent data types
    static final String[] validTypes = {"boolean", "bool", "string", "number", "any"};

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

            // 'generateRandomNumber()' - returns random number between 0 and 1
            new AcaciaCallable() {
                public final String name = "generateRandomNumber";

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
                    return Math.random();
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
                    System.out.print(printer.toString().trim().replaceAll("\\\\n", "\n"));
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
                    System.out.println(printer.toString().trim().replaceAll("\\\\n", "\n"));
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
            },

            // 'convert(object, string)' - converts a given object to given type (as string) and returns it
            new AcaciaCallable() {
                final String name = "convert";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
                    Object given = arguments.get(0);
                    String newType = Acacia.stringify(arguments.get(1)).toLowerCase();
                    if (!Arrays.asList(validTypes).contains(newType)) {
                        throw new RuntimeError(location, "'" + newType + "' is not a valid type " +
                                "to convert to. Must be: 'boolean', 'string' or 'number'.");
                    }

                    String err = "Failed to convert '" + Acacia.stringify(given) + "' to " + newType;

                    if (newType.equals("string")) return Acacia.stringify(given);

                    if (given instanceof String) {
                        String obj = ((String) given);
                        return switch (newType) {
                            case "boolean" -> {
                                if ("true".contains(obj.toLowerCase())) yield true;
                                else if ("false".contains(obj.toLowerCase())) yield false;
                                else throw new RuntimeError(location, err);
                            }
                            case "number" -> {
                                try {
                                    yield Double.parseDouble(obj);
                                } catch (Exception e) {
                                    throw new RuntimeError(location, err);
                                }
                            }
                            default -> obj;
                        };
                    }

                    else if (given instanceof Double) {
                        Double obj = ((Double) given);
                        if (newType.equals("boolean")) {
                            if (obj <= 0) return false;
                            else return true;
                        }
                        else return obj;
                    }

                    else if (given instanceof Boolean) {
                        Boolean obj = ((Boolean) given);
                        if (newType.equals("number")) {
                            if (obj) return 1.0;
                            else return 0.0;
                        }
                        else return obj;
                    }

                    else throw new RuntimeError(location, err);
                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            },

            // 'type(object)' - returns the type (as string) of whatever passed
            new AcaciaCallable() {
                final String name = "type";

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
                    Object thing = arguments.get(0);
                    if (thing == null) return null;
                    if (thing instanceof Boolean) return validTypes[0];
                    if (thing instanceof String) return validTypes[2];
                    if (thing instanceof Double) return validTypes[3];

                    String name = thing.getClass().getName().replace("com.edavalos.acacia.Acacia", "");
                    return switch (name) {
                        case "Set" -> "set";
                        case "Instance" -> "instance";
                        case "Function" -> "function";
                        case "Class" -> "class";
                        default -> thing.getClass().getCanonicalName();
                    };
                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            },

            // 'callable(object)' - returns true if given object is callable
            new AcaciaCallable() {
                final String name = "callable";

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
                    return (arguments.get(0) instanceof AcaciaCallable);
                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            },

            // 'inherits(object, class)' - returns true if given object inherits another class.
            new AcaciaCallable() {
                final String name = "inherits";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
                    if (!(arguments.get(1) instanceof AcaciaClass))
                        throw new RuntimeError(location, "'" + arguments.get(1) + "' is not a valid class");
                    AcaciaClass superior = ((AcaciaClass) arguments.get(1));

                    if (arguments.get(0) instanceof AcaciaClass) {
                        return ((AcaciaClass) arguments.get(0)).superclass == superior;
                    }
                    else if (arguments.get(0) instanceof AcaciaInstance) {
                        return ((AcaciaInstance) arguments.get(0)).klass.superclass == superior;
                    }
                    else return false;

                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            },

            // 'instanceof(object, class)' - returns true if given object is an instance of another class.
            new AcaciaCallable() {
                final String name = "instanceof";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
                    if (!(arguments.get(1) instanceof AcaciaClass))
                        throw new RuntimeError(location, "'" + arguments.get(1) + "' is not a valid class");
                    AcaciaClass type = ((AcaciaClass) arguments.get(1));

                    if (arguments.get(0) instanceof AcaciaInstance) {
                        AcaciaClass thing = ((AcaciaInstance) arguments.get(0)).klass;
                        if (thing == type) return true;
                        while (thing != null) {
                            thing = thing.superclass;
                            if (thing == type) return true;
                        }
                    }

                    return false;

                }

                @Override
                public String toString() {
                    return "<native fn " + name + ">";
                }
            }
    );



    static final List<AcaciaCallable> setMethods = Arrays.asList(
            // -- these only visit the original set:

            // '.join(str) - returns a string from elements in a set with delimiter provided
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
            },

            // '.contains(obj) - returns whether or not a set contains an object. Takes in anything to find.'
            new AcaciaCallable() {
                public final String name = "contains";

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
                    AcaciaSet set = ((AcaciaSet) arguments.get(0));

                    return set.getAll().contains(arguments.get(1));
                }

                @Override
                public String toString() {
                    return "<set method " + name + ">";
                }
            },

            // -- these methods *modify* the original set:

            // '.sort()' - sorts a set.
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
                    if (!(arguments.get(0) instanceof AcaciaSet))
                        throw new RuntimeError(location, "'" + arguments.get(0) + "' is not a set.");
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
                    return null;
                }

                @Override
                public String toString() {
                    return "<set method " + name + ">";
                }
            },

            // '.reverse()' - reverses order of a set.
            new AcaciaCallable() {
                public final String name = "reverse";

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
                    if (!(arguments.get(0) instanceof AcaciaSet))
                        throw new RuntimeError(location, "'" + arguments.get(0) + "' is not a set.");
                    List<Object> set = ((AcaciaSet) arguments.get(0)).getAll();

                    Collections.reverse(set);
                    return null;
                }

                @Override
                public String toString() {
                    return "<set method " + name + ">";
                }
            },

            // '.push()' - adds an element to the end of a set.
            new AcaciaCallable() {
                public final String name = "push";

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
                    if (!(arguments.get(0) instanceof AcaciaSet))
                        throw new RuntimeError(location, "'" + arguments.get(0) + "' is not a set.");
                    List<Object> set = ((AcaciaSet) arguments.get(0)).getAll();

                    set.add(arguments.get(1));
                    return null;
                }

                @Override
                public String toString() {
                    return "<set method " + name + ">";
                }
            },

            // '.pop()' - removes the last element of a set and returns it.
            new AcaciaCallable() {
                public final String name = "pop";

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
                    if (!(arguments.get(0) instanceof AcaciaSet))
                        throw new RuntimeError(location, "'" + arguments.get(0) + "' is not a set.");
                    List<Object> set = ((AcaciaSet) arguments.get(0)).getAll();

                    if (set.size() == 0) return null;

                    Object thing = set.get(set.size()-1);
                    set.remove(set.size()-1);
                    return thing;
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
            },

            // '.strip() - removes whitespaces from the beginning and end of a string'
            new AcaciaCallable() {
                public final String name = "strip";

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
                    if (!(arguments.get(0) instanceof String)) return null;
                    String str = ((String) arguments.get(0));

                    return str.trim();
                }

                @Override
                public String toString() {
                    return "<string method " + name + ">";
                }
            },

            // '.replace(str, str) - replaces given string with another given string inside a string'
            new AcaciaCallable() {
                public final String name = "replace";

                @Override
                public String name() {
                    return name;
                }

                @Override
                public int arity() {
                    return 2;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments, Token location) {
                    if (!(arguments.get(0) instanceof String)) return null;
                    String str = ((String) arguments.get(0));

                    if (!(arguments.get(1) instanceof String) ||!(arguments.get(2) instanceof String) ) {
                        throw new RuntimeError(location, "Expected strings as argument.");
                    }

                    return str.replaceAll(((String) arguments.get(1)), ((String) arguments.get(2)));
                }

                @Override
                public String toString() {
                    return "<string method " + name + ">";
                }
            },

            // '.contains(str) - returns whether or not a string contains another. Takes in a string to find.'
            new AcaciaCallable() {
                public final String name = "contains";

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
                    if (!(arguments.get(0) instanceof String)) return null;
                    String str = ((String) arguments.get(0));

                    if (!(arguments.get(1) instanceof String)) {
                        throw new RuntimeError(location, "Expected string as argument.");
                    }
                    String cont = ((String) arguments.get(1));

                    return str.contains(cont);
                }

                @Override
                public String toString() {
                    return "<string method " + name + ">";
                }
            }

    );
}
