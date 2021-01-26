package com.edavalos.acacia;

import java.util.List;
import java.util.Stack;

abstract class Expr {
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitBinaryExpr(Binary expr);
    R visitCallExpr(Call expr);
    R visitEditSetExpr(EditSet expr);
    R visitGetExpr(Get expr);
    R visitGroupingExpr(Grouping expr);
    R visitIncrementExpr(Increment expr);
    R visitIncSetExpr(IncSet expr);
    R visitIndexExpr(Index expr);
    R visitLiteralExpr(Literal expr);
    R visitLogicalExpr(Logical expr);
    R visitSetExpr(Set expr);
    R visitPutExpr(Put expr);
    R visitUnaryExpr(Unary expr);
    R visitVariableExpr(Variable expr);
  }
  static class Assign extends Expr {
    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    final Token name;
    final Expr value;
  }

  static class Binary extends Expr {
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }

  static class Call extends Expr {
    Call(Expr callee, Token paren, List<Expr> arguments) {
      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }

    final Expr callee;
    final Token paren;
    final List<Expr> arguments;
  }

  static class EditSet extends Expr {
    EditSet(Token name, Stack<Expr> depth, Expr value) {
      this.name = name;
      this.depth = depth;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitEditSetExpr(this);
    }

    final Token name;
    final Stack<Expr> depth;
    final Expr value;
  }

  static class Get extends Expr {
    Get(Expr object, Token name) {
      this.object = object;
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGetExpr(this);
    }

    final Expr object;
    final Token name;
  }

  static class Grouping extends Expr {
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    final Expr expression;
  }

  static class Increment extends Expr {
    Increment(Token var, Token type) {
      this.var = var;
      this.type = type;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIncrementExpr(this);
    }

    final Token var;
    final Token type;
  }

  static class IncSet extends Expr {
    IncSet(Token name, Stack<Expr> depth, Token type) {
      this.name = name;
      this.depth = depth;
      this.type = type;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIncSetExpr(this);
    }

    final Token name;
    final Stack<Expr> depth;
    final Token type;
  }

  static class Index extends Expr {
    Index(Expr set, Token name, Token bracket, Expr location) {
      this.set = set;
      this.name = name;
      this.bracket = bracket;
      this.location = location;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIndexExpr(this);
    }

    final Expr set;
    final Token name;
    final Token bracket;
    final Expr location;
  }

  static class Literal extends Expr {
    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    final Object value;
  }

  static class Logical extends Expr {
    Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }

  static class Set extends Expr {
    Set(List<Expr> values) {
      this.values = values;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSetExpr(this);
    }

    final List<Expr> values;
  }

  static class Put extends Expr {
    Put(Expr object, Token name, Expr value) {
      this.object = object;
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPutExpr(this);
    }

    final Expr object;
    final Token name;
    final Expr value;
  }

  static class Unary extends Expr {
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    final Token operator;
    final Expr right;
  }

  static class Variable extends Expr {
    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    final Token name;
  }


  abstract <R> R accept(Visitor<R> visitor);
}
