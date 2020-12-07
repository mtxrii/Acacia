package com.edavalos.acacia;

enum TokenType {
    // Single-character tokens:
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens:
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL, MODULO,

    // Literals:
    IDENTIFIER, STRING, NUMBER,

    // Keywords:
    AND, CLASS, DEF, ELSE, FALSE, FOR, IF, LET, NIL,
    OR, PRINT, RETURN, SUPER, THIS, TRUE, WHILE,

    EOF
}
