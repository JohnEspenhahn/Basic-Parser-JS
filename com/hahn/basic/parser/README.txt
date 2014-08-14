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

