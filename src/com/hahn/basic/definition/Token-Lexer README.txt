Use BasicLexer for more efficient, but harder to customize, lexing.

Use RegexLexer for more complex and easier to customize lexing, but slower performance.

==================
== Basic Lexer
==================
A custom basic lexer token enum cannot be created. You must
cutomize the existing com.hahn.basic.definition.EnumToken


==================
== Regex Lexer
== @deprecated
==================
Provide an enum with all of the tokens to parse input into. 

The enum should implements IEnumToken and the `regex` for each
token should be a standard Java regex. 

The following special shorthands can be used:
  <<WORD>>  ==  [_a-zA-Z][_a-zA-Z0-9]*
  