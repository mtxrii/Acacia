package com.edavalos.acacia;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    R visitAssignExpr(Assign expr);
    R visitBinaryExpr(Binary expr);
    R visitGroupingExpr(Grouping expr);
    R visitIndexExpr(Index expr);
    R visitInputExpr(Input expr);
    R visitLiteralExpr(Literal expr);
    R visitSetExpr(Set expr);
    R visitLogicalExpr(Logical expr);
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

  static class Index extends Expr {
    Index(Token setName, Expr location) {
      this.setName = setName;
      this.location = location;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIndexExpr(this);
    }

    final Token setName;
    final Expr location;
  }

  static class Input extends Expr {
    Input(DataType type) {
      this.type = type;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitInputExpr(this);
    }

    final DataType type;
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
