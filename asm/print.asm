.global _start
_start:
    @ Put "Hello World!\n" in the memory
    ldr r0, =message
    @ Put the address of the string in the stack
    stmfd sp!, {r0}
    @ Call the print function
    bl _print
    @ Exit
    mov r7, #1
    mov r0, #0
    svc 0


@ print function that takes a pointer to a string and prints it
_print:
    @ We save the stack pointer in r11
    stmfd sp!, {lr, r11}
    mov r11, sp
    @ We load the string pointer from the stack 
    ldr r0, [sp, #4*2]
    @ We load the first character of the string
    ldrb r1, [r0]
    @ We check that the character is not null
    cmp r1, #0
    @ If it is null, we return to LR
    beq _print_return
    @ Set r12 to the PP (print pointer)
    ldr r12, =0xff201000
    @ We write the character in r1 to the PP
    strb r1, [r12]
    @ We increment the string pointer by 4
    add r0, r0, #1
    @ We store the new string pointer in the stack
    stmfd sp!, {r0}
    @ We call the print function again
    bl _print
    b _print_return
    
_print_return:
    @ We restore the stack pointer
    ldmfd sp!, {lr, r11}
    mov sp, r11
    @ We return to LR
    ldmfd sp!, {lr}
    mov pc, lr

.data
message:
    .asciz "hello world\n"