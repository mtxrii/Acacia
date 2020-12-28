package com.edavalos.acacia;

class Interpreter implements Expr.Visitor<Object> {

    /* --- Expressions' visitor methods --- */

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            // If unary is a minus, assume value is a number and return its negation
            case MINUS -> -(double)right;

            // If unary is a not, assess value as bool and return its negation
            case BANG -> !isTruthy(right);

            default -> null;
        };
    }


    /* --- Utility methods --- */

    // Sends the given expression back into the interpreter’s visitor method
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    // Determines whether a given value is true or false
    private boolean isTruthy(Object object) {
        // Anything nil is false
        if (object == null) return false;

        // Any boolean is just itself
        if (object instanceof Boolean) return (boolean)object;

        // Any number is false only if its value is zero
        if (object instanceof Double) return ((double)object != 0.0);

        // Anything else is true
        return true;
    }
}
