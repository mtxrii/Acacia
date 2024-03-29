package com.edavalos.acacia;

import java.util.List;
import java.util.Stack;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitClassStmt(Class stmt);
    R visitExitStmt(Exit stmt);
    R visitExpressionStmt(Expression stmt);
    R visitForeachStmt(Foreach stmt);
    R visitFunctionStmt(Function stmt);
    R visitIfStmt(If stmt);
    R visitNextStmt(Next stmt);
    R visitOpenStmt(Open stmt);
    R visitPrintStmt(Print stmt);
    R visitReturnStmt(Return stmt);
    R visitVarStmt(Var stmt);
    R visitWhileStmt(While stmt);
  }
  static class Block extends Stmt {
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
  }

  static class Class extends Stmt {
    Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
      this.name = name;
      this.superclass = superclass;
      this.methods = methods;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitClassStmt(this);
    }

    final Token name;
    final Expr.Variable superclass;
    final List<Stmt.Function> methods;
  }

  static class Exit extends Stmt {
    Exit(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExitStmt(this);
    }

    final Token keyword;
  }

  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }

  static class Foreach extends Stmt {
    Foreach(Token iterator, Expr iterable, Token iterableName, Token index, Stmt body) {
      this.iterator = iterator;
      this.iterable = iterable;
      this.iterableName = iterableName;
      this.index = index;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitForeachStmt(this);
    }

    final Token iterator;
    final Expr iterable;
    final Token iterableName;
    final Token index;
    final Stmt body;
  }

  static class Function extends Stmt {
    Function(Token name, List<Token> params, List<Stmt> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }

    final Token name;
    final List<Token> params;
    final List<Stmt> body;
  }

  static class If extends Stmt {
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }

  static class Next extends Stmt {
    Next(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitNextStmt(this);
    }

    final Token keyword;
  }

  static class Open extends Stmt {
    Open(Token keyword, Expr file) {
      this.keyword = keyword;
      this.file = file;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitOpenStmt(this);
    }

    final Token keyword;
    final Expr file;
  }

  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }

  static class Return extends Stmt {
    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    final Token keyword;
    final Expr value;
  }

  static class Var extends Stmt {
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
  }

  static class While extends Stmt {
    While(Expr condition, Stmt body, Expr increment) {
      this.condition = condition;
      this.body = body;
      this.increment = increment;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
    final Expr increment;
  }


  abstract <R> R accept(Visitor<R> visitor);
}
