package com.edavalos.acacia;

import java.util.List;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment();

    // Main method to interpret given statements
    void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Acacia.error(error);
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

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }


    /* --- Statements' visitor methods --- */

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        }
        else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.print(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name, value);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }


    /* --- Utility methods --- */

    // Sends a given expression back into the interpreter’s visitor method
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    // Sends a given statement back into the interpreter's visitor method
    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    // Loops thru a list of statements in a block and executes them, also handles variable scoping
    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
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
