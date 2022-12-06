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
|   constantes                                                                       
;

sequenceInstruction
:   '(' expression ( ';' expression )* ')'                                                  #Sequence
;

operationNegation
:   '-' expressionUnaire                                                                    #Negation
;

expressionValeur
:   
    identifiant                                                                                            #ExpressionIdentifiant
  | identifiant '(' ( expression ( ',' expression )* )? ')'                                                #AppelFonction
  | identifiant '[' expression ']' ( ( '[' expression ']' | '.' identifiant )* | 'of' expressionUnaire )   #ExpressionItem
  | identifiant '.' identifiant ( '[' expression ']' | '.' identifiant )*                                  #ExpressionChamps
  | identifiant '{' ( identifiant '=' expression ( ',' identifiant '=' expression )* )? '}'                #InstanciationType
;

operationSi
:   'if' expression 'then' expressionUnaire                                                       #SiAlors
  | 'if' expression 'then' expressionUnaire 'else' expressionUnaire                               #SiAlorsSinon
;

operationTantque
:   'while' expression 'do' expressionUnaire                                                      #TantQue
;

operationBoucle                                                                             
:   'for' identifiant ':=' expression 'to' expression 'do' expressionUnaire                                #Pour
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
:   'type' identifiant '=' ( identifiant | 'array' 'of' identifiant | '{' ( identifiant ':' identifiant ( ',' identifiant ':' identifiant )* )? '}')
;

declarationFonction
:   'function' identifiant '(' ( i += identifiant ':' j += identifiant ( ',' i += identifiant ':' j += identifiant )* )? ')' ( ':' k = identifiant )? '=' expression
;

declarationValeur
:   'var' identifiant ( ':' k = identifiant )? ':=' expression
;

constantes
:   STR #String
|   INT #Entier
|   'nil' #Nil
|   'break' #Break
;

identifiant
: ID
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