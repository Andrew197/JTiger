package Parse;
import ErrorMsg.ErrorMsg;

%% 

%implements Lexer
%function nextToken
%type java_cup.runtime.Symbol
%char

%{

private void newline() 
{
  errorMsg.newline(yychar);
}

private void err(int pos, String s) 
{
  errorMsg.error(pos,s);
}

private void err(String s) '
{
  err(yychar,s);
}

private java_cup.runtime.Symbol tok(int kind) 
{
    return tok(kind, null);
}

private java_cup.runtime.Symbol tok(int kind, Object value) 
{
    return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}

private ErrorMsg errorMsg;

Yylex(java.io.InputStream s, ErrorMsg e) 
{
  this(s);
  errorMsg=e;
}

%}

%eofval{
	{
	  return tok(sym.EOF, null);
  }
%eofval}       


identifier regex [a-zA-Z][a-zA-Z0-9_]* <- better solution
first char letters only. second onward letter or number

digits regex [0-9]+

%%
" "	{}
\n	{newline();}

//TODO: COMMENTS

//TODO: STRINGS

//looking up some tokens...
//http://www2.hh.se/staff/vero/languages/03/lectures/lecture2.pdf
//https://www.lrde.epita.fr/~akim/ccmp/tiger.html#Lexical-Specifications <-- better

<YINITIAL> ","	{return tok(sym.COMMA, null);}
<YINITIAL> ":" {return tok(sym.COLON, null);}
<YINITIAL> ";" {return tok(sym.SEMICOLON, null);}
<YINITIAL> "(" {return tok(sym.LPAREN, null);}
<YINITIAL> ")" {return tok(sym.RPAREN, null);}
<YINITIAL> "[" {return tok(sym.LBRACK, null);}
<YINITIAL> "]" {return tok(sym.RBRACK, null);}
<YINITIAL> "{" {return tok(sym.LBRACE, null);}
<YINITIAL> "}" {return tok(sym.RBRACE, null);}
<YINITIAL> "." {return tok(sym.DOT, null);}
<YINITIAL> "+" {return tok(sym.PLUS, null);}
<YINITIAL> "-" {return tok(sym.MINUS, null);}
<YINITIAL> "*" {return tok(sym.TIMES, null);}
<YINITIAL> "\" {return tok(sym.DIVIDE, null);}
<YINITIAL> "=" {return tok(sym.EQ, null);}
<YINITIAL> "!=" {return tok(sym.NEQ, null);}
<YINITIAL> "<" {return tok(sym.LT, null);}
<YINITIAL> "<=" {return tok(sym.LE, null);}
<YINITIAL> ">" {return tok(sym.GT, null);}
<YINITIAL> ">=" {return tok(sym.GE, null);}
<YINITIAL> "&" {return tok(sym.AND, null);}
<YINITIAL> "|" {return tok(sym.OR, null);}
<YINITIAL> ":=" {return tok(sym.ASSIGN, null);}

//KEYWORDS
<YINITIAL> array {return tok(sym.ARRAY, null);}
<YINITIAL> if {return tok(sym.IF, null);}
<YINITIAL> then {return tok(sym.THEN, null);}
<YINITIAL> else {return tok(sym.ELSE, null);}
<YINITIAL> while {return tok(sym.WHILE, null);}
<YINITIAL> for {return tok(sym.FOR, null);}
<YINITIAL> to {return tok(sym.TO, null);}
<YINITIAL> do {return tok(sym.DO, null);}
<YINITIAL> let {return tok(sym.LET, null);}
<YINITIAL> in {return tok(sym.IN, null);}
<YINITIAL> end {return tok(sym.END, null);}
<YINITIAL> of {return tok(sym.OF, null);}
<YINITIAL> break {return tok(sym.BREAK, null);}
<YINITIAL> nil {return tok(sym.NIL, null);}
<YINITIAL> function {return tok(sym.FUNCTION, null);}
<YINITIAL> var {return tok(sym.VAR, null);}
<YINITIAL> type {return tok(sym.TYPE, null);}
<YINITIAL> import {return tok(sym.IMPORT, null);}
<YINITIAL> primitive {return tok(sym.PRIMITIVE, null);}

<YINITIAL> class {return tok(sym.CLASS, null);}
<YINITIAL> extends {return tok(sym.EXTENDS, null);}
<YINITIAL> method {return tok(sym.METHOD, null);}
<YINITIAL> new {return tok(sym.NEW, null);}

. { err("Illegal character: " + yytext()); }
