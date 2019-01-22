package com.interpreter.bear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.interpreter.bear.TokenType.*;

class Scanner{

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }
    //full source code in this string
    private final String source;
    //list with tokens that have been generated
    private final List<Token> tokens = new ArrayList();
    //start char of the current lexeme, and the current char being scanned
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        //when we get to end, put an EOF token in
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            //for these four we need to check the following char
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/': 
                if (match('/')) {
                    //rest of line is comment
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                //this isn't python - ignore the whitespace!
                break;
            case '\n':
                line++;
                break;
            case '"': createStringToken(); break;
            default:
                if (isDigit(c)){
                    createNumberToken();
                }
                else if (isAlpha(c)){
                    createIdentifierToken();
                }
                else{
                    //detect errors but continue scanning to check for more!
                    Bear.error(line, "Unexpected character!");
                    break;
                }

        }
    }

    private void createIdentifierToken() {
        while (isAlphaNumeric(peek())) advance();

        //check list of reserved words
        String text = source.substring(start, current);

        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void createNumberToken() {
        while (isDigit(peek())) advance();

        //look for a fractional part if it's there
        if (peek() == '.' && isDigit(peekNext())) {
            //consume the point
            advance();
            while (isDigit(peek())) advance();
        }
        //take entire number as a token - get its numeric value
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void createStringToken() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            Bear.error(line, "Unterminated string!");
            return;
        }

        //we reach the closing ", so consume it
        advance();

        //trim the quotes - they're not actually part of the string!
        //add the token as the literal value
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    //looks ahead to the next char
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    //looks ahead to the char after the next one
    //we only ever look two chars ahead at most
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    //character is either a letter or an underscore
    //valid identifier name can start with either
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    //Consumes next char only if it is our expected value
    private boolean match(char expected){
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    //gets next character in source file
    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    //output -- creates a new token for the current lexeme
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}