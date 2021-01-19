package com.edavalos.acacia;

import java.util.List;

/**
 * Implementing AcaciaCallable allows an object to be called like a function, object constructor or closure
 */
interface AcaciaCallable {
    /**
     * Gets the number of arguments this callable expects. i.e. its arity.
     * @return number of arguments expected.
     */
    int arity();

    /**
     * Invokes this callable object. i.e. tells it to evaluate itself and provide a value.
     * @param interpreter The instance where the call is being interpreted.
     * @param arguments The arguments to pass to it.
     * @return The value evaluated by the call.
     */
    Object call(Interpreter interpreter, List<Object> arguments);

    /**
     * Gets this callable's identifier. Usually same as the variable name in the environment.
     * @return name used to call
     */
    String name();
}
