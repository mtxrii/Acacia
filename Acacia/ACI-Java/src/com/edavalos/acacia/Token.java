package com.edavalos.acacia;

class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line, column, length;

    // Constructor for actual tokens with positions in the code
    Token(TokenType type, String lexeme, Object literal, int line, int column, int length) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
        this.length = length;
    }

    // Constructor for pseudotokens used in syntactic sugar
    Token(TokenType type, Object literal) {
        this.type = type;
        this.lexeme = type.toString();
        this.literal = literal;

        this.line = -1;
        this.column = -1;
        this.length = -1;
    }

    public String toString() {
        return type + " " + lexeme + " => " + literal;
    }
}