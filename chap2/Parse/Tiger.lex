package Parse;
import ErrorMsg.ErrorMsg;

%% 

%implements Lexer
%function nextToken
%type java_cup.runtime.Symbol
%char

%{

int commentLevel = 0;
boolean insideString = false;

private void newline(){
  errorMsg.newline(yychar);
}

private void err(int pos, String s){
  errorMsg.error(pos,s);
}

private void err(String s){
  err(yychar,s);
}

private java_cup.runtime.Symbol tok(int kind){
    return tok(kind, null);
}

private java_cup.runtime.Symbol tok(int kind, Object value){
    return new java_cup.runtime.Symbol(kind, yychar, yychar+yylength(), value);
}

private ErrorMsg errorMsg;

Yylex(java.io.InputStream s, ErrorMsg e){
  this(s);
  errorMsg=e;
}

%}

%eofval{
	{
	  return tok(sym.EOF, null);
  }
%eofval}       


id = [a-z|A-Z][a-z|A-Z|0-9|_]*
digits = [0-9]+

%state COMMENT, STRING
%%
" "	{}
\n	{newline();}
\r  {newline();}
\n\r {newline();}
\r\n {newline();}
\t {newline();}

<YYINITIAL> "/*"  { commentLevel++; System.out.println("begin comment"); yybegin(COMMENT);}
<YYINITIAL> "\"" { if (!insideString) { yybegin(STRING); insideString = true; } else { yybegin(YYINITIAL); insideString = false;} }

<COMMENT> "*/"   { if(commentLevel == 1) yybegin(YYINITIAL); else commentLevel--;}
<COMMENT> . { }

<STRING> . { return tok(sym.STRING, null);}

<YYINITIAL> ","	{return tok(sym.COMMA, null);}
<YYINITIAL> ":" {return tok(sym.COLON, null);}
<YYINITIAL> ";" {return tok(sym.SEMICOLON, null);}
<YYINITIAL> "(" {return tok(sym.LPAREN, null);}
<YYINITIAL> ")" {return tok(sym.RPAREN, null);}
<YYINITIAL> "[" {return tok(sym.LBRACK, null);}
<YYINITIAL> "]" {return tok(sym.RBRACK, null);}
<YYINITIAL> "{" {return tok(sym.LBRACE, null);}
<YYINITIAL> "}" {return tok(sym.RBRACE, null);}
<YYINITIAL> "." {return tok(sym.DOT, null);}
<YYINITIAL> "+" {return tok(sym.PLUS, null);}
<YYINITIAL> "-" {return tok(sym.MINUS, null);}
<YYINITIAL> "*" {return tok(sym.TIMES, null);}
<YYINITIAL> "/" {return tok(sym.DIVIDE, null);}
<YYINITIAL> "=" {return tok(sym.EQ, null);}
<YYINITIAL> "!=" {return tok(sym.NEQ, null);}
<YYINITIAL> "<" {return tok(sym.LT, null);}
<YYINITIAL> "<=" {return tok(sym.LE, null);}
<YYINITIAL> ">" {return tok(sym.GT, null);}
<YYINITIAL> ">=" {return tok(sym.GE, null);}
<YYINITIAL> "&" {return tok(sym.AND, null);}
<YYINITIAL> "|" {return tok(sym.OR, null);}
<YYINITIAL> ":=" {return tok(sym.ASSIGN, null);}
<YYINITIAL> array {return tok(sym.ARRAY, null);}
<YYINITIAL> if {return tok(sym.IF, null);}
<YYINITIAL> then {return tok(sym.THEN, null);}
<YYINITIAL> else {return tok(sym.ELSE, null);}
<YYINITIAL> while {return tok(sym.WHILE, null);}
<YYINITIAL> for {return tok(sym.FOR, null);}
<YYINITIAL> to {return tok(sym.TO, null);}
<YYINITIAL> do {return tok(sym.DO, null);}
<YYINITIAL> let {return tok(sym.LET, null);}
<YYINITIAL> in {return tok(sym.IN, null);}
<YYINITIAL> end {return tok(sym.END, null);}
<YYINITIAL> of {return tok(sym.OF, null);}
<YYINITIAL> break {return tok(sym.BREAK, null);}
<YYINITIAL> nil {return tok(sym.NIL, null);}
<YYINITIAL> function {return tok(sym.FUNCTION, null);}
<YYINITIAL> var {return tok(sym.VAR, null);}
<YYINITIAL> type {return tok(sym.TYPE, null);}
<YYINITIAL> {id}  { return tok(sym.ID, yytext());}
<YYINITIAL> {digits} { return tok(sym.INT, yytext());}
. { err("Illegal character: " + yytext()); }

