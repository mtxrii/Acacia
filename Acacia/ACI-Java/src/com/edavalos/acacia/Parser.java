package com.edavalos.acacia;

import java.util.ArrayList;
import java.util.List;

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
            if (match(LET)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt statement() {
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());

        return expressionStatement();
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

        return new Stmt.While(condition, body);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expected ';' after expression.");
        return new Stmt.Expression(expr);
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

        return assignment();
    }

    private Expr assignment() {
        Expr expr = or();

        if (match(EQUAL) || match(DOUBLE_PLUS) || match(DOUBLE_MINUS) ||
                match(TRIPLE_PLUS) || match(TRIPLE_MINUS)) {

            Token equals = previous();
            if (!(expr instanceof Expr.Variable))
                error(equals, "Invalid target to modify.");

            else {
                Token name = ((Expr.Variable) expr).name;
                Expr value = switch (equals.type) {
                    case EQUAL -> assignment();
                    case DOUBLE_PLUS -> new Expr.Binary(expr, new Token(PLUS, "+"), new Expr.Literal(1.0));
                    case DOUBLE_MINUS -> new Expr.Binary(expr, new Token(MINUS, "-"), new Expr.Literal(1.0));
                    case TRIPLE_PLUS -> new Expr.Binary(expr, new Token(STAR, "*"), new Expr.Literal(2.0));
                    case TRIPLE_MINUS -> new Expr.Binary(expr, new Token(SLASH, "/"), new Expr.Literal(2.0));
                    default -> null;
                };
                return new Expr.Assign(name, value);
            }
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

        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(INPUT)) {
            return new Expr.Input(DataType.STRING);
        }

        if (match(IDENTIFIER)) {
            Token varName = previous();
            if (match(LEFT_BRACKET)) {
                Expr index = expression();
                consume(RIGHT_BRACKET, "Expected ']' after index.");
                return new Expr.Index(varName, index);
            }

            return new Expr.Variable(varName);
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