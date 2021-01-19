package com.edavalos.acacia;

import java.util.List;

/**
 * Implementing AcaciaCallable allows an object to be called like a function, object constructor or closure
 */
interface AcaciaCallable {
    /**
     * Invokes this callable object. i.e. tells it to evaluate itself and provide a value.
     * @param interpreter The instance where the call is being interpreted.
     * @param arguments The arguments to pass to it.
     * @return The value evaluated by the call.
     */
    Object call(Interpreter interpreter, List<Object> arguments);
}
