grammar expr;

@header{
    package parser;
}

program
:   expression EOF
;

expression
:   operationOu ( ':=' operationOu)?
;

operationOu
:   operationEt ('|' operationEt)*
;

operationEt
:   operationComparaison ('&' operationComparaison)*
;

operationComparaison
:   operationAddition ( ( '=' | '<>' | '<' | '>=' | '>' | '<=') operationAddition )?
;

operationAddition
:   operationMultiplication ('+' operationMultiplication)*
    ('-' operationMultiplication ('+' operationMultiplication)*)*
;

operationMultiplication
:   expressionUnaire  ('*' expressionUnaire)*
    ('/' expressionUnaire('*' expressionUnaire)*)*
;

expressionUnaire
:   sequenceInstruction
|   operationNegation
|   expressionValeur
|   operationSi
|   operationTantque
|   operationBoucle
|   definition
|   STR
|   INT
|   'nil'
|   'break'
;

sequenceInstruction
:   '(' expression ( ';' expression )* ')'
;

operationNegation
:   '-' expressionUnaire
;

expressionValeur
:   ID
    ( 
      '(' ( expression ( ',' expression )* )? ')'
    | '[' expression ']' ( ( '[' expression ']' | '.' ID )* | 'of' expressionUnaire )
    | '.' ID ( '[' expression ']' | '.' ID )*
    | '{' ( ID '=' expression ( ',' ID '=' expression )* )? '}'
    )?
;

operationSi
:   'if' expression 'then' expression ( 'else' expression )?
;

operationTantque
:   'while' expression 'do' expression
;

operationBoucle
:   'for' ID ':=' expression 'to' expression 'do' expression
;

definition
:   'let' declaration+ 'in' ( expression ( ';' expression )* )? 'end'
;

declaration
:   declarationType
|   declarationFonction
|   declarationValeur
;

declarationType
:   'type' ID '=' ( ID | 'array' 'of' ID | '{' ( ID ':' ID ( ',' ID ':' ID )* )? '}')
;

declarationFonction
:   'function' ID '(' ( i += ID ':' j += ID ( ',' i += ID ':' j += ID )* )? ')' ( ':' k = ID )? '=' expression
;

declarationValeur
:   'var' ID ( ':' k = ID )? ':=' expression
;

ID
:   ( 'A'..'Z' | 'a'..'z' ) // Les identificateurs ne peuvent pas commencer par des caractéres de types numériques
    ( '0'..'9' | 'A'..'Z' | '_' | 'a'..'z' )*
;

STR
:   '"'
    ( ' '..'!' | '#'..'[' | ']'..'~' | '\\' )*
    '"'
;

INT
:   '0'..'9'+
;

COMMENT
:    '/*' .*? '*/' -> skip
;

WS
:   ( ' ' | '\t' | '\n' | '\r' | '\f' )+ -> skip
;