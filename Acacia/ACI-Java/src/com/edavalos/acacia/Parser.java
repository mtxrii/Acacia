package com.edavalos.acacia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static com.edavalos.acacia.TokenType.*;

class Parser {
    // Custom exception for parser errors
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }


    /* --- Token processing methods --- */

    private Stmt declaration() {
        try {
            if (match(CLASS)) return classDeclaration();
            if (match(DEF)) return function("function");
            if (match(LET)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt classDeclaration() {
        Token name = consume(IDENTIFIER, "Expected class name.");

        Expr.Variable superclass = null;
        if (match(LESS)) {
            consume(IDENTIFIER, "Expected superclass name.");
            superclass = new Expr.Variable(previous());
        }

        consume(LEFT_BRACE, "Expected '{' before class body.");

        List<Stmt.Function> methods = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"));
        }

        consume(RIGHT_BRACE, "Expected '}' after class body.");

        return new Stmt.Class(name, superclass, methods);
    }

    private Stmt statement() {
        if (match(EXIT)) return exitStatement(false);
        if (match(FOR)) return forStatement();
        if (match(FOREACH)) return foreachStatement();
        if (match(IF)) return ifStatement();
        if (match(NEXT)) return exitStatement(true);
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();

        if (match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
    }

    private Stmt exitStatement(boolean cont) {
        Token keyword = previous();
        consume(SEMICOLON, "Expected ';' after statement.");

        return cont ? new Stmt.Next(keyword) : new Stmt.Exit(keyword);
    }

    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'for'.");

        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        }
        else if (match(LET)) {
            initializer = varDeclaration();
        }
        else {
            initializer = expressionStatement();
        }

        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expected ';' after loop condition.");

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expected ')' after for clauses.");
        Stmt body = statement();

        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body, increment);

        if (initializer != null) {
            body = new Stmt.Block(
                    Arrays.asList(
                            initializer,
                            body
                    )
            );
        }

        return body;
    }

    private Stmt foreachStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'foreach'.");

        Token iterator;
        if (match(LET) && match(IDENTIFIER)) {
            iterator = previous();
        }
        else {
            throw new RuntimeError(peek(), "Expected variable initializer (for iterator)");
        }
        consume(SEMICOLON, "Expected ';' after variable initializer.");

        Token iterableName = peek();
        Expr iterable = expression();

        consume(SEMICOLON, "Expected ';' after iterable.");

        Token index = null;
        if (!check(RIGHT_PAREN)) {
            if (match(LET) && match(IDENTIFIER)) {
                index = previous();
            }
        }
        consume(RIGHT_PAREN, "Expected ')' after foreach clauses.");
        Stmt body = statement();

        return new Stmt.Foreach(iterator, iterable, iterableName, index, body);
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected ')' after if condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expected ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
            value = expression();
        }

        consume(SEMICOLON, "Expected ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expected variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expected ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expected '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expected ')' after condition.");
        Stmt body = statement();

        return new Stmt.While(condition, body, null);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expected ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expected " + kind + " name.");
        consume(LEFT_PAREN, "Expected '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                parameters.add(consume(IDENTIFIER, "Expected parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expected ')' after parameters.");

        consume(LEFT_BRACE, "Expected '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expected '}' at end of block.");
        return statements;
    }

    private Expr expression() {
        if (match(LEFT_BRACKET)) return new Expr.Set(set());

        return increment();
    }

    private Expr increment() {
        Expr expr = assignment();
        if (match(DOUBLE_PLUS) || match(DOUBLE_MINUS) ||
            match(TRIPLE_PLUS) || match(TRIPLE_MINUS)) {
            Token type = previous();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Increment(name, type);
            }

            if (expr instanceof Expr.Index) {
                Stack<Expr> depth = new Stack<>();

                Expr.Index current = ((Expr.Index) expr);
                depth.push(current.location);
                while (current.set instanceof Expr.Index) {
                    current = ((Expr.Index) current.set);
                    depth.push(current.location);
                }
                return new Expr.IncSet(((Expr.Index) expr).name, depth, type);
            }

            error(type, "Invalid increment target.");
        }

        return expr;
    }

    private Expr assignment() {
        Expr expr = or();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = expression();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            if (expr instanceof Expr.Get) {
                Expr.Get get = (Expr.Get) expr;
                return new Expr.Put(get.object, get.name, value);
            }

            if (expr instanceof Expr.Index) {
                Stack<Expr> depth = new Stack<>();

                Expr.Index current = ((Expr.Index) expr);
                depth.push(current.location);
                while (current.set instanceof Expr.Index) {
                    current = ((Expr.Index) current.set);
                    depth.push(current.location);
                }
                return new Expr.EditSet(((Expr.Index) expr).name, depth, value);

            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
        Expr expr = equality();

        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR, MODULO)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return call();
    }

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                arguments.add(expression());
            } while (match(COMMA));
        }

        Token paren = consume(RIGHT_PAREN, "Expected ')' after arguments.");

        return new Expr.Call(callee, paren, arguments);
    }

    private Expr call() {
        Expr expr = index();

        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expected property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else{
                break;
            }
        }

        return expr;
    }

    private Expr index() {
        Expr expr = primary();
        Token varName = null;

        if (expr instanceof Expr.Variable) {
            varName = ((Expr.Variable) expr).name;
        }

        while (true) {
            if (match(LEFT_BRACKET)) {
                Expr index = expression();
                Token bracket = consume(RIGHT_BRACKET, "Expected ']' after index.");
                expr = new Expr.Index(expr, varName, bracket, index);
            } else {
                break;
            }
        }

        return expr;
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(THIS)) {
            return new Expr.This(previous());
        }

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expected ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expected expression.");
    }

    private List<Expr> set() {
        List<Expr> values = new ArrayList<>();

        boolean last = false;
        while (!check(RIGHT_BRACKET) && !isAtEnd()) {
            if (!last) {
                values.add(expression());
                last = true;
            }
            if (match(COMMA)) {
                last = false;
            }
        }

        consume(RIGHT_BRACKET, "Expected ']' at end of set.");
        return values;
    }


    /* --- Token traversing methods --- */

    // Check if current token is one of any number of specified TokenTypes
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    // Check current token, and if it's of specified type, advance to next --
    // if it's not, report error with the given message
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    // Check if current token is of a specified TokenType
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    // Go on to next token, provided there is at least one left
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    // Check if current token is last
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    // Get current token
    private Token peek() {
        return tokens.get(current);
    }

    // Get previous token
    private Token previous() {
        return tokens.get(current - 1);
    }


    /* --- Error handling methods --- */

    // Sends error notification to main class
    private ParseError error(Token token, String message) {
        Acacia.error(token, message);
        return new ParseError();
    }

    // Discards tokens until parser reaches an end of statement
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            // Semicolon means end of statement
            if (previous().type == SEMICOLON) {
                return;
            }

            // Start of new statement means end of previous one
            switch (peek().type) {
                case CLASS, DEF, LET, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }

            advance();
        }
    }
}