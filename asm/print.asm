.global _start
_start:
    @ Put "Hello World!\n" in the memory
    ldr r0, =message
    @ Put the address of the string in the stack
    push {r0}
    @ Call the print function
    bl _print
    @ Exit
	b _stop

@ print function that takes a pointer to a string and prints it
_print:
    @ We save the stack pointer in r12
    push {lr, r12}
    mov r12, sp
    @ We load the string pointer from the stack 
    ldr r0, [sp, #4*2]
    @ We load the first character of the string
    ldrb r1, [r0]
    @ We check that the character is not null
    cmp r1, #0
    @ If it is null, we return to LR
    popeq {pc, r12}    
    @ Set r11 to the PP (print pointer)
    ldr r11, =0xff201000
    @ We write the character in r1 to the PP
    strb r1, [r11]
    @ We increment the string pointer by 4
    add r0, r0, #1
    @ We store the new string pointer in the stack
    stmfd sp!, {r0}
    @ We call the print function again
    bl _print
    pop {r0, pc, r12}

_stop:
	b _stop

.data
message:
    .asciz "hello world\n"