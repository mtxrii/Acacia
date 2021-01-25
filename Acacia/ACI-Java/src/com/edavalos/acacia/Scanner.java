package com.edavalos.acacia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.edavalos.acacia.TokenType.*;

class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",      AND);
        keywords.put("exit",     EXIT);
        keywords.put("class",    CLASS);
        keywords.put("next",     NEXT);
        keywords.put("def",      DEF);
        keywords.put("else",     ELSE);
        keywords.put("false",    FALSE);
        keywords.put("for",      FOR);
        keywords.put("foreach",  FOREACH);
        keywords.put("if",       IF);
        keywords.put("let",      LET);
        keywords.put("match",    MATCH);
        keywords.put("nil",      NIL);
        keywords.put("or",       OR);
        keywords.put("print",    PRINT);
        keywords.put("return",   RETURN);
        keywords.put("super",    SUPER);
        keywords.put("this",     THIS);
        keywords.put("true",     TRUE);
        keywords.put("while",    WHILE);
        keywords.put("with",     WITH);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line, start, current));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> addToken(LEFT_PAREN);
            case ')' -> addToken(RIGHT_PAREN);
            case '{' -> addToken(LEFT_BRACE);
            case '}' -> addToken(RIGHT_BRACE);
            case '[' -> addToken(LEFT_BRACKET);
            case ']' -> addToken(RIGHT_BRACKET);
            case ',' -> addToken(COMMA);
            case '.' -> addToken(DOT);
            case '-' -> {
                if (match('-')) {
                    addToken(match('-') ? TRIPLE_MINUS : DOUBLE_MINUS);
                }
                else addToken(MINUS);
            }
            case '+' -> {
                if (match('+')) {
                    addToken(match('+') ? TRIPLE_PLUS : DOUBLE_PLUS);
                }
                else addToken(PLUS);
            }
            case ';' -> addToken(SEMICOLON);
            case '*' -> addToken(match('*') ? TRIPLE_PLUS : STAR);
            case '%' -> addToken(MODULO);
            case '!' -> addToken(match('=') ? BANG_EQUAL : BANG);
            case '=' -> addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '<' -> addToken(match('=') ? LESS_EQUAL : LESS);
            case '>' -> addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                }
                else {
                    addToken(SLASH);
                }
            }
            case ' ', '\r', '\t' -> {}
            case '\n' -> line++;
            case '"' -> string();
            default -> {
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Acacia.error(line, "Unexpected character.");
                }
            }
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Acacia.error(line, "Unterminated string.");
            return;
        }

        // The closing '"'
        advance();

        // Trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current).toLowerCase();
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }


    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return  (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c == '_');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line, start, current));
    }
}