package com.edavalos.acacia;

final class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line, column, length;

    Token(TokenType type, String lexeme, Object literal, int line, int column, int length) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
        this.length = length;
    }

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = -1;
        this.length = -1;
    }

    public String toString() {
        return type + " " + lexeme + " => " + literal;
    }
}