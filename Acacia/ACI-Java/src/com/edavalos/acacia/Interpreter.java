package com.edavalos.acacia;

class Interpreter implements Expr.Visitor<Object> {

    // Main method to interpret a given expression
    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Acacia.runtimeError(error);
        }
    }

    /* --- Expressions' visitor methods --- */

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            // If minus, assume both are numbers and return difference (num)
            case MINUS -> {
                validateNumbers(expr.operator, left, right);
                yield (double)left - (double)right;
            }
            // If addition, add numbers together, and concatenate strings
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left + (double)right;
                }

                if (left instanceof String || right instanceof String) {
                    yield stringify(left) + stringify(right);
                }
                // If values are neither both numbers or one string, throw error
                throw new RuntimeError(expr.operator, "Operands must either all be numbers or" +
                        " at least one must be a string.");
            }
            // If division, assume both are numbers and return quotient (num)
            case SLASH ->  {
                validateNumbers(expr.operator, left, right);
                yield (double)left / (double)right;
            }
            // If multiplication, assume both are numbers and return product (num)
            case STAR ->  {
                validateNumbers(expr.operator, left, right);
                yield (double)left * (double)right;
            }

            // If greater/less or any variant, compare number sizes, and compare string lengths (bool for both)
            case GREATER -> {
                if (left instanceof Double && right instanceof Double) {
                    yield  (double)left > (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    yield ((String)left).length() > ((String)right).length();
                }
                // If values are neither type number or string, throw error
                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            }
            case GREATER_EQUAL -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left >= (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    yield ((String)left).length() >= ((String)right).length();
                }
                // If values are neither type number or string, throw error
                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            }
            case LESS -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left < (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    yield ((String)left).length() < ((String)right).length();
                }
                // If values are neither type number or string, throw error
                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            }
            case LESS_EQUAL -> {
                if (left instanceof Double && right instanceof Double) {
                    yield (double)left <= (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    yield ((String)left).length() <= ((String)right).length();
                }
                // If values are neither type number or string, throw error
                throw new RuntimeError(expr.operator, "Operands must both be numbers or strings.");
            }

            // If equality comparison, return equality value (bool)
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);

            default -> null;
        };
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
            case MINUS -> {
                validateNumbers(expr.operator, right);
                yield -(double) right;
            }

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

    // Convert a value into a string
    private String stringify(Object object) {
        // If object is nil, string returned should be 'nil' instead of 'null'
        if (object == null) return "nil";

        // If object is a number, and has a decimal where it doesn't need it, remove it
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        // Otherwise, toString() should take care of it
        return object.toString();
    }

    // Determines whether a given value is considered true
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

    // Determined whether two values are equal
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    // Ensures given objects are numbers
    private void validateNumbers(Token operator, Object... objects) {
        for (Object object : objects) {
            // If any object of the ones given isn't a number, throw an error
            if (!(object instanceof Double)) {
                throw new RuntimeError(operator, "Operand must be a number.");
            }
        }
    }
}
