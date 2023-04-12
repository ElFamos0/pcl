.data

/* Data segment: define our message string and calculate its length. */
msg:    .ascii          "Hello World!\0"
format_str: .ascii      "Je print un string : %s\n\0"
format_int: .ascii      "Je print un int : %d\n\0"

.text

/* Our application's entry point. */
.globl main
main:
    /* libc call printf(format, 42) */
    ldr     %r0, =format_int
    mov     %r1, #42
    BL      printf
    



    /* libc call printf(format, 48) */
    ldr     %r0, =format_int
    BL      printf

    /* libc call printf(format, &hello) */
    ldr     %r0, =format_str
    ldr     %r1, =msg
    BL      printf

    /* syscall exit(int status) */
    mov     %r0, $0     /* status := 0 */
    mov     %r7, $1     /* exit is syscall #1 */
    swi     $0          /* invoke syscall */