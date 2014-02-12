package Parse;
import ErrorMsg.ErrorMsg;

%% 

%implements Lexer
%function nextToken
%type java_cup.runtime.Symbol
%char

%{

int commentLevel = 0;
String stringBuf = "";

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
		//We need to ensure we don't hit EOF in the middle of a string or comment
	  	if(commentLevel != 0 || !"".equals(stringBuf))
	  		{
	  			err("End of file hit in between a comment or string."); 
	  			return tok(sym.EOF, null);
	  		}
	  	else 
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
<YYINITIAL> "\"" { System.out.println("STRING START"); yybegin(STRING); }

<COMMENT> "/*"  { commentLevel++; System.out.println("begin comment");}
<COMMENT> "*/"   { System.out.println("End of comment found. Comment level is now: " + (commentLevel-1));if(commentLevel == 1) {commentLevel=0;yybegin(YYINITIAL);} else commentLevel--;}
<COMMENT> . { }

<STRING> "\"" { System.out.println("STRING END"); yybegin(YYINITIAL); String tempStringBuf = stringBuf; stringBuf = ""; return tok((sym.STRING), tempStringBuf);}
<STRING> \\[n|r|f|\s] {System.out.println("Link break in string not recorded.");}
<STRING> [\t-!|#-~]+ { System.out.println("adding " + yytext()); stringBuf += yytext(); }

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
. { if(Character.isISOControl(yytext().charAt(0)))
	{System.out.println("Ignoring control key.");}
	else err("Illegal character: " + (int)yytext().toCharArray()[0]); }

