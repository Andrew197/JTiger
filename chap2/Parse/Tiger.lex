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

%eofval
{
	{
	  return tok(sym.EOF, null);
  }
%eofval}       

/*under construction
regex testing: http://regexpal.com/
----------------------------------------------
identifier regex [a-zA-Z0-9_]*   <- not quite?
digits regex [0-9]+
----------------------------------------------*/

%%
" "	{}
\n	{newline();}

//TODO: COMMENTS

//TODO: IDENTIFIERS

//TODO: DIGITS

//TODO: STRINGS

//looking up some tokens...
//http://www2.hh.se/staff/vero/languages/03/lectures/lecture2.pdf
//https://www.lrde.epita.fr/~akim/ccmp/tiger.html#Lexical-Specifications <-- better

//SYMBOLS
","	{return tok(sym.COMMA, null);}
":" {return tok(sym.COLON, null);}
";" {return tok(sym.SEMICOLON, null);}
"(" {return tok(sym.LPAREN, null);}
")" {return tok(sym.RPAREN, null);}
"[" {return tok(sym.LBRACK, null);}
"]" {return tok(sym.RBRACK, null);}
"{" {return tok(sym.LBRACE, null);}
"}" {return tok(sym.RBRACE, null);}
"." {return tok(sym.DOT, null);}
"+" {return tok(sym.PLUS, null);}
"-" {return tok(sym.MINUS, null);}
"*" {return tok(sym.TIMES, null);}
"\" {return tok(sym.DIVIDE, null);}
"=" {return tok(sym.EQ, null);}
"!=" {return tok(sym.NEQ, null);}
"<" {return tok(sym.LT, null);}
"<=" {return tok(sym.LE, null);}
">" {return tok(sym.GT, null);}
">=" {return tok(sym.GE, null);}
"&" {return tok(sym.AND, null);}
"|" {return tok(sym.OR, null);}
":=" {return tok(sym.ASSIGN, null);}

//KEYWORDS
array {return tok(sym.ARRAY, null);}
if {return tok(sym.IF, null);}
then {return tok(sym.THEN, null);}
else {return tok(sym.ELSE, null);}
while {return tok(sym.WHILE, null);}
for {return tok(sym.FOR, null);}
to {return tok(sym.TO, null);}
do {return tok(sym.DO, null);}
let {return tok(sym.LET, null);}
in {return tok(sym.IN, null);}
end {return tok(sym.END, null);}
of {return tok(sym.OF, null);}
break {return tok(sym.BREAK, null);}
nil {return tok(sym.NIL, null);}
function {return tok(sym.FUNCTION, null);}
var {return tok(sym.VAR, null);}
type {return tok(sym.TYPE, null);}
import {return tok(sym.IMPORT, null);}
primitive {return tok(sym.PRIMITIVE, null);}

//object extension
class {return tok(sym.CLASS, null);}
extends {return tok(sym.EXTENDS, null);}
method {return tok(sym.METHOD, null);}
new {return tok(sym.NEW, null);}

. { err("Illegal character: " + yytext()); }
