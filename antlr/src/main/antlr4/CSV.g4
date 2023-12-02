grammar CSV;
file: header line+;
header: line;
line: entry (',' entry)* '\r'? '\n' ;
entry : TEXT #text
 | #empty
 ;
TEXT: ~[,\n\r"]+ ;