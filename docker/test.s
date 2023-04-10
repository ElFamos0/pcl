.text
.globl main
main:
    MOV r0, #5
    STMFD r13!, {r0}
    MOV r0, #3
    STMFD r13!, {r0}
    LDMFD r13!, {r0, r1}
    ADD r0, r0, r1
    STMFD r13!, {r0}
    LDR r0, =format_int
    LDMFD r13!, {r1}
    BL printf
.data
    format_str: .ascii      "%s\n\0"
    format_int: .ascii      "%d\n\0"
