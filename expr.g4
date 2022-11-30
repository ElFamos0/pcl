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
:   operationMultiplication ( ( '+' | '-' ) operationMultiplication )*
;

operationMultiplication
:   expressionUnaire ( ( '*' | '/' ) expressionUnaire )*
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
:   '(' expression ( ';' expression )* ')'                                                  #Sequence
;

operationNegation
:   '-' expressionUnaire                                                                    #Negation
;

expressionValeur
:   
    ID ( expressionValeur2 )?                                                               #Merde1
;

expressionValeur2
:     '(' ( expression ( ',' expression )* )? ')'                                           #AppelFonction
    | expressionValeurItem ( ( expressionValeurItem | expressionValeurChamps )* | expressionValeurCreationArray )     #Merde2
    | expressionValeurChamps ( expressionValeurItem | expressionValeurChamps )*                                       #Merde2
    | '{' ( ID '=' expression ( ',' ID '=' expression )* )? '}'                             #InstanciationType
;

expressionValeurItem
:
    '[' expression ']' #Item
;

expressionValeurChamps
:
    '.' ID #Champs
;

expressionValeurCreationArray
:
    'of' expressionUnaire #CreationArray
;

operationSi
:   'if' expression 'then' expressionUnaire                                                       #SiAlors
  | 'if' expression 'then' expressionUnaire 'else' expressionUnaire                               #SiAlorsSinon
;

operationTantque
:   'while' expression 'do' expressionUnaire                                                      #TantQue
;

operationBoucle                                                                             
:   'for' ID ':=' expression 'to' expression 'do' expressionUnaire                                #Pour
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