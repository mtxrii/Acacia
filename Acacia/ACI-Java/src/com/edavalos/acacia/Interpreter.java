package com.edavalos.acacia;

class Interpreter implements Expr.Visitor<Object> {

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

        switch (expr.operator.type) {
            // If unary is a minus, assume value is number and return its negation
            case MINUS:
                return -(double)right;
            // If unary is a not, assess value as bool and return its negation
            case BANG:
                return !isTruthy(right);
        }

        // Unreachable
        return null;
    }
}
