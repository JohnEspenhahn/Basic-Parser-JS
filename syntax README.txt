Note: See the readmes in com.hahn.basic.definition for lower level expression definition

The syntax for the language is defined in com.hahn.basic.definition

The lexer in com.hahn.basic.lexer users EnumToken to parse the input into tokens
The parser in com.hahn.basic.parser converts these tokens into legal expressions in the intermediate language
The intermediate language is then outputted using a definition from com.hahn.basic.target which was specified in the main function
 - note: currently the only available output language is Javascript, which is used by default

When reading/writing to a file the default encoding is UTF-8

The following format for explaining the syntax was inspired by: http://www.tutorialspoint.com/python/

==================
== Identifiers
==================
A Kava identifier is a case sensitive name used to identify a variable, function, class, etc.
A legal identifier matches the following regular expression:
	[_$a-zA-Z][_$a-zA-Z0-9]*
	

Special note: Identifiers starting with three underscores "___" are generally reserved and are bad style

==================
== Reserved Words
==================
const, final, private, static 
abstract, if, else, for, while
continue, break, return, function,
struct, true, false, new, null,
import, class, constructor,
implements, extends, this, super

==================
== Types 
==================
bool, real, String, Array, Object

==================
== Comments 
==================
// This is a comment to the end of the line

==================
== Defining a variable
==================
real i = 10;
real j = 12, k = 21;
String hi = "Hello world";

// Two ways to define arrays
Array<real> arr1 = [ 1, 2, 3];
real[] arr2 = [ 1, 2, 3 ];

// Legal but not recommended, essentually Array<Object> arr = [ 1, 2, 3 ];
Array arr = [ 1, 2, 3 ];

Note: semicolon at end of line is required. Multiple semicolon terminated expressions can be on one line

