package com.edavalos.acacia;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void>  {
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private final Stack<BlockType> nestedBlocks = new Stack<>();

    private ClassType currentClass = ClassType.NONE;

    private enum BlockType {
        NONE,
        LOOP,
        FUNCTION,
        METHOD,
        INITIALIZER
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
        this.nestedBlocks.push(BlockType.NONE);
    }

    void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
            resolve(statement);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void resolveFunction(Stmt.Function function, BlockType type) {
        nestedBlocks.push(type);

        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();

        nestedBlocks.pop();
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            Acacia.error(name, "Variable '" + name.lexeme + "' already exists in this scope.");
        }

        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }





    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitEditSetExpr(Expr.EditSet expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitIncrementExpr(Expr.Increment expr) {
        resolveLocal(expr, expr.var);
        return null;
    }

    @Override
    public Void visitIncSetExpr(Expr.IncSet expr) {
        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitIndexExpr(Expr.Index expr) {
        resolve(expr.set);
        resolve(expr.location);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        for (Expr value : expr.values) {
            resolve(value);
        }
        return null;
    }

    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        if (currentClass == ClassType.NONE) {
            Acacia.error(expr.keyword, "Can't use 'super' outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            Acacia.error(expr.keyword, "Can't use 'super' in a class with no superclass.");
        }

        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visitThisExpr(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
            Acacia.error(expr.keyword, "Can't use 'this' outside of a class.");
            return null;
        }

        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visitPutExpr(Expr.Put expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Acacia.error(expr.name, "Can't read local variable in its own initializer.");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        if (nestedBlocks.peek() != BlockType.NONE) {
            Acacia.error(stmt.name, "Classes cannot be declared inside functions");
            return null;
        }

        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;

        declare(stmt.name);
        define(stmt.name);

        if (stmt.superclass != null) {
            if (stmt.name.lexeme.equals(stmt.superclass.name.lexeme)) {
                Acacia.error(stmt.superclass.name, "A class can't inherit itself.");
                return null;
            }

            resolve(stmt.superclass);
            currentClass = ClassType.SUBCLASS;

            beginScope();
            scopes.peek().put("super", true);
        }

        beginScope();
        scopes.peek().put("this", true);

        for (Stmt.Function method : stmt.methods) {
            BlockType declaration = BlockType.METHOD;
            if (method.name.lexeme.equals("init")) {
                declaration = BlockType.INITIALIZER;
            }

            resolveFunction(method, declaration);
        }

        endScope();

        if (stmt.superclass != null) endScope();

        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitExitStmt(Stmt.Exit stmt) {
        BlockType currentBlockType = nestedBlocks.peek();
        if (currentBlockType == BlockType.NONE || currentBlockType == BlockType.FUNCTION) {
            Acacia.error(stmt.keyword, "'Exit' can only be used inside loops.");
        }
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitForeachStmt(Stmt.Foreach stmt) {
        nestedBlocks.push(BlockType.LOOP);

        resolve(new Stmt.Var(stmt.iterator, null));
        resolve(stmt.iterable);
        if (stmt.index != null) {
            resolve(new Stmt.Var(stmt.index, new Expr.Literal(0.0)));
        }
        resolve(stmt.body);

        nestedBlocks.pop();
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, BlockType.FUNCTION);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitNextStmt(Stmt.Next stmt) {
        BlockType currentBlockType = nestedBlocks.peek();
        if (currentBlockType == BlockType.NONE || currentBlockType == BlockType.FUNCTION) {
            Acacia.error(stmt.keyword, "'Next' can only be used inside loops.");
        }
        return null;
    }

    @Override
    public Void visitOpenStmt(Stmt.Open stmt) {
        if (scopes.size() > 0) {
            Acacia.error(stmt.keyword, "'open' can only be used in outermost scope.");
            return null;
        }
        resolve(stmt.file);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (!nestedBlocks.contains(BlockType.FUNCTION) && !nestedBlocks.contains(BlockType.METHOD)) {
            Acacia.error(stmt.keyword, "Can't return outside methods or functions.");
            return null;
        }

        if (stmt.value != null) {
            if (nestedBlocks.contains(BlockType.INITIALIZER)) {
                Acacia.error(stmt.keyword, "Can't return a value from an initializer.");
                return null;
            }

            resolve(stmt.value);
        }

        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        nestedBlocks.push(BlockType.LOOP);

        resolve(stmt.condition);
        if (stmt.increment != null) resolve(stmt.increment);
        resolve(stmt.body);

        nestedBlocks.pop();
        return null;
    }
}
