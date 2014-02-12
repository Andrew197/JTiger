README.TXT - Project Summary
Team Members: Mitchell Mason & Andrew Pinion

We handled strings and comments by utilizing the extra JLex states named
<STRING> and <COMMENT>, so that once we're inside of a string we only accept
input as if it were a string even if keywords are encountered, except for
newlines, carriage returns, and linefeeds. When those are found, we still
raise the line count with newLine(); Tokens are
added to a string buffer (named stringBuf) one at a time until a second double
quote has been found. 

For comments, we keep track of how many nested "levels" the lexical analyzer
is reading inside of with the global variable commentLevel. Whenever we
encounter /* this variable is incremented, and decremented for when */ is
seen.

Control characters are handled within the catch-all clause at the end. It
normally throws out an error, but we do a last minute check to see if any of
those characters fit within \u0000 through \u001F. If so, we continue
lexical analysis as if they never occured. 
