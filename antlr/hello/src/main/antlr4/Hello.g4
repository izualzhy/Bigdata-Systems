// Define a grammar called Hello
grammar Hello;
hello  : 'Hello' name ', I am' name EOF {System.out.println("Parsed successfully!");};         // match keyword hello followed by an identifier
name : ID ;
ID : [a-zA-Z']+ ;             // match lower-case identifiers
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
