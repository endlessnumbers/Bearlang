package com.interpreter.bear;

class Token{
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    Token (TokenType type, String lexeme, Object literal, int line){
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString(){
        return String.Format("%s %s %s", type, lexeme, literal);
    }
}