package asm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ASMWriter {
    private FileWriter writer;

    public ASMWriter(String filename) throws IOException {
        // Opens file and creates a writer
        File file = new File(filename);
        writer = new FileWriter(file);
    }

    public void write(String s) {
        // Writes a string to the file
        try {
            writer.write(s);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // We can implement the {, SHIFT_op #expression}
    // but it's not necessary for the moment.

    // Label functions
    public void Label(String label) {
        String instr = label + ":\n";

        // Write instruction to file
        write(instr);
    }

    // Add functions
    public void Add(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ADD"
            + flag.toString()
            + " " + dst.getName()
            + ", "
            + val1.getValue()
            + ", "
            + val2.getValue()
            + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Add(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ADD"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Adc functions
    public void Adc(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ADC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Adc(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ADC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Sub functions
    public void Sub(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "SUB"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Sub(Register dst, Register val1, int val2, Flags flag) {
        String instr = "SUB"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Sbc functions
    public void Sbc(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "SBC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Sbc(Register dst, Register val1, int val2, Flags flag) {
        String instr = "SBC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Rsb functions
    public void Rsb(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "RSB"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Rsb(Register dst, Register val1, int val2, Flags flag) {
        String instr = "RSB"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Rsc functions
    public void Rsc(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "RSC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Rsc(Register dst, Register val1, int val2, Flags flag) {
        String instr = "RSC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Move functions
    public void Mov(Register dst, Register val, Flags flag) {
        String instr = "MOV"
            + flag.toString()
            + " "
            + dst.getName()
            + ", " 
            + val.getName()
            + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Mov(Register dst, int val, Flags flag) {
        String instr = "MOV"
            + flag.toString()
            + " "
            + dst.getName()
            + ", #"
            + val
            + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Mvn(Register dst, int val, Flags flag) {
        String instr = "MVN"+flag.toString() + " " + dst.getName() + ", #" + val + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Mvn(Register dst, Register val, Flags flag) {
        String instr = "MVN"+flag.toString() + " " + dst.getName() + ", " + val.getName() + "\n";

        // Write instruction to file
        write(instr);
    }


    public void And(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "AND"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void And(Register dst, Register val1, int val2, Flags flag) {
        String instr = "AND"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Eor(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ORR"+ flag.toString()+ " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName() + "\n";
        // Write instruction to file
        write(instr);
    }

    public void Eor(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ORR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Lsl functions
    public void Lsl(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "LSL"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Lsl(Register dst, Register val1, int val2, Flags flag) {
        String instr = "LSL"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Lsr functions
    public void Lsr(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "LSR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Lsr(Register dst, Register val1, int val2, Flags flag) {
        String instr = "LSR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Asr functions
    public void Asr(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ASR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Asr(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ASR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // Ror functions
    public void Ror(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ROR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Ror(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ROR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }
    public void Orr(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ORR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Orr(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ORR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Bic(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "BIC"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Bic(Register dst, Register val1, int val2, Flags flag) {
        String instr = "BIC"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // CMP functions
    public void Cmp(Register val1, Register val2) {
        String instr = "CMP"
            + " "
            + val1.getName()
            + ", "
            + val2.getName()
            + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Cmp(Register val1, int val2) {
        String instr = "CMP"
            + " "
            + val1.getName()
            + ", #"
            + val2
            + "\n";

        // Write instruction to file
        write(instr);
    }

    // CMN functions
    public void Cmn(Register val1, Register val2) {
        String instr = "CMN" + " " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Cmn(Register val1, int val2) {
        String instr = "CMN" + " " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // TST functions
    public void Tst(Register val1, Register val2) {
        String instr = "TST" + " " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Tst(Register val1, int val2) {
        String instr = "TST" + " " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // TEQ functions
    public void Teq(Register val1, Register val2) {
        String instr = "TEQ" + " " + val1.getName() + ", " + val2.getName() + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Teq(Register val1, int val2) {
        String instr = "TEQ" + " " + val1.getName() + ", #" + val2 + "\n";

        // Write instruction to file
        write(instr);
    }

    // ADR function
    // ADR generates an address by adding an immediate value to the PC,
    // and writes the result to the destination register.
    public void Adr(Register dst, String label) {
        String instr = "ADR" + " " + dst.getName() + ", " + label + "\n";

        // Write instruction to file
        write(instr);
    }

    // LDR function
    // LDR loads a word from memory into a register.
    public void Ldr(Register dst, Register addr) {
        String instr = "LDR" + " " + dst.getName() + ", [" + addr.getName() + "]" + "\n";

        // Write instruction to file
        write(instr);
    }

    public void Ldr(Register dst, String offset) {
        String instr = "LDR" + " " + dst.getName() + ", =" + offset + "\n";

        // Write instruction to file
        write(instr);
    }

    // STR function
    // STR stores a word from a register into memory.
    public void Str(Register dst, Register addr) {
        String instr = "STR" + " " + dst.getName() + ", [" + addr.getName() + "]" + "\n";

        // Write instruction to file
        write(instr);
    }

    // LDMFD function
    // LDMFD loads multiple registers from memory.
    public void Ldmfd(Register src, Register[] regs) {
        String instr = "LDMFD" + " " + src.getName() + "!, {";

        for (int i = 0; i < regs.length; i++) {
            instr += regs[i].getName();

            if (i != regs.length - 1) {
                instr += ", ";
            }
        }

        instr += "}"
            + "\n";

        // Write instruction to file
        write(instr);
    }

    // STMFD function
    // STMFD stores multiple registers into memory.
    public void Stmfd(Register dst, Register[] regs) {
        String instr = "STMFD" + " " + dst.getName() + "!, {";

        for (int i = 0; i < regs.length; i++) {
            instr += regs[i].getName();

            if (i != regs.length - 1) {
                instr += ", ";
            }
        }

        instr += "}"
            + "\n";

        // Write instruction to file
        write(instr);
    }

    // B function
    // B branches to a label.
    public void B(String label) {
        String instr = "B" + " " + label + "\n";

        // Write instruction to file
        write(instr);
    }

    // BL function
    // BL branches to a label and stores the return address in LR.
    public void Bl(String label) {
        String instr = "BL" + " " + label + "\n";

        // Write instruction to file
        write(instr);
    }

    // EQU function
    // EQU defines a label.
    public void Equ(String label, String value) {
        String instr = label + " " + "EQU" + " " + value + "\n";

        // Write instruction to file
        write(instr);
    }

    // FILL function
    // FILL fills a memory location with a value.
    public void Fill(String addr, String value) {
        String instr = addr + " " + "FILL" + " " + value + "\n";

        // Write instruction to file
        write(instr);
    }

    // End function
    // End writes the end of the program.
    public void End() {
        String instr = "END";

        // Write instruction to file
        write(instr);
    }

    public void Exit(Integer status) {
        String instr = "mov %r0, $" + status.toString() + "\n";
        instr = "mov %r7, $1\n"
        instr = "swi $0\n";

        // Write instruction to file
        write(instr);
    }

    // Mul function
    // Mul multiplies two numbers inside R1 and R2 register.
    // The result is stored in R0 register.
    public void Mul() {
        String fn = """
            mul         STMFD SP!, {R1,R2}
                        MOV R0, #0
                """;

        String fn_loop = """
            _mul_loop   LSRS R2, R2, #1
                        ADDCS R0, R0, R1
                        LSL R1, R1, #1
                        TST R2, R2
                        BNE _mul_loop
                        LDMFD SP!, {R1,R2}
                        LDR PC, [R13, #-4]!
                """;
        
        // Write function to file
        write(fn + "\n" + fn_loop + "\n");
    }

    // Div function
    // Div divides two numbers inside R1 and R2 register.
    // The result is stored in R0 register.
    public void Div() {
        String fn = """
            div         STMFD SP!, {R2-R5}
                        MOV R0, #0
                        MOV R3, #0
                        CMP R1, #0
                        RSBLT R1, R1, #0
                        EORLT R3, R3, #1
                        CMP R2, #0
                        RSBLT R2, R2, #0
                        EORLT R3, R3, #1
                        MOV R4, R2
                        MOV R5, #1
                """;
        
        String fn_max = """
            _div_max    LSL R4, R4, #1
                        LSL R5, R5, #1
                        CMP R4, R1
                        BLE _div_max
                """;
        
        String fn_loop = """
            _div_loop   LSR R4, R4, #1
                        LSR R5, R5, #1
                        CMP R4,R1
                        BGT _div_loop
                        ADD R0, R0, R5
                        SUB R1, R1, R4
                        CMP R1, R2
                        BGE _div_loop
                        CMP R3, #1
                        BNE _div_exit
                        CMP R1, #0
                        ADDNE R0, R0, #1
                        RSB R0, R0, #0
                        RSB R1, R1, #0
                        ADDNE R1, R1, R2
                """;
        
        String fn_exit = """
            _div_exit   LDMFD SP!, {R2-R5}
                        LDR PC, [R13, #-4]!
                """;

        write(fn + "\n" + fn_max + "\n" + fn_loop + "\n" + fn_exit + "\n");
    }

    // Itoa function
    // Itoa converts an integer to a string inside R1 register.
    // The result is stored in the address pointed by the R2 register.
    public void Itoa() {
        String fn = """
            its         STMFD SP!, {R0-R10}
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
                """;

        String fn_init = """
            _its_init   CMP R3, R4
                        BLT _its_loop
                        MOV R1, R4
                        MOV R2, #10
                        STR PC, [SP], #4
                        B mul
                        MOV R4, R0
                        B _its_init
                """;

        String fn_loop = """
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
                """;
        
        String fn_2 = """
            _its2       MOV R1, R4
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
                        ADD R6, R6, R0
                        MOV R2, R4
                        STR PC, [SP], #4
                        B mul
                        SUB R3, R3, R0
                        ADD R8, R8, #8
                        ADD R10, R10, #1
                        CMP R8, #32
                        MOVEQ R8, #0
                        BEQ _its3
                        B _its2
                """;
        
        String fn_3 = """
            _its3       MOV R1, R4
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
                        ADD R7, R7, R0
                        ADD R10, R10, #1
                        MOV R2, R4
                        STR PC, [SP], #4
                        B mul
                        SUB R3, R3, R0
                        ADD R8, R8,
                """;
        
        String fn_exit = """
            _its_exit   STMIA R9, {R5-R7}
                        LDMFD SP!, {R0-R10}
                        LDR PC, [SP, #-4]!
                """;
        
        String instr = fn
            .concat("\n")
            .concat(fn_init)
            .concat("\n")
            .concat(fn_loop)
            .concat("\n")
            .concat(fn_2)
            .concat("\n")
            .concat(fn_3)
            .concat("\n")
            .concat(fn_exit)
            .concat("\n");

        write(instr);
    }
}
