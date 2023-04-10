its         STMFA SP!, {R0-R10}
            MOV R9, R2
            MOV R3, R1
            MOV R4, #10
            MOV R8, #0
            MOV R5, #0
            MOV R6, #0
            MOV R7, #0
            CMP R3, #0
            MOV R10, #0
            RSBLT R3, R3, #0
            MOVLT R5, #0x2D
            ADDLT R8, R8, #8
            ADDLT R10, R10, #1

_its_init   CMP R3, R4
            BLT _its_loop
            MOV R1, R4
            MOV R2, #10
            STR PC, [SP], #4
            B mul
            MOV R4, R0
            B _its_init

_its_loop   MOV R1, R4
            MOV R2, #10
            STR PC, [SP], #4
            B div
            MOV R4, R0
            CMP R4, #0
            BEQ _its_exit
            MOV R2, R0
            MOV R1, R3
            STR PC, [SP], #4
            B div
            MOV R1, R0
            ADD R0, R0, #0x30
            LSL R0, R0, R8
            ADD R5, R5, R0
            MOV R2, R4
            STR PC, [SP], #4
            B mul
            SUB R3, R3, R0
            ADD R8, R8, #8

END