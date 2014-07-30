==================
== Lexer
==================
Provide an enum with all of the tokens to parse input into. 

The enum should implements IEnumToken and the `regex` for each
token should be a standard Java regex. The following special
shorthands can be used 

<<WORD>>  ==  [_a-zA-Z][_a-zA-Z0-9]*


==================
== Parser
==================
Provide an enum with all of the expressions and tokens to parse
lexed tokens into.

-------------
BNF Parser
-------------
A string within triangular braces <...> references an expression,
as string not in triangular braces references a token.

+--------------------+-------+
| Expression loop    | $     |
| NEXT is optional   | ?     |
| Match 0 or 1 times | (...) |
| Match 0+ times     | {...} |
| Match 1+ times     | [...] |
| End subexpression  | |     |
+--------------------+-------+

