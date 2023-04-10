.text
.globl main
main:
LDR r0, =string_1
STMFD r13!, {r0}
LDR r0, =format_str
LDMFD r13!, {r1}
BL printf
.data
format_str: .ascii      "%s\n\0"
format_int: .ascii      "%d\n\0"
string_1:	.ascii "j'adore le caca\0"
