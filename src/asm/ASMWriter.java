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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // We can implement the {, SHIFT_op #expression}
    // but it's not necessary for the moment.

    // Add functions
    public void Add(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ADD"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Add(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ADD"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Adc functions
    public void Adc(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ADC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Adc(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ADC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Sub functions
    public void Sub(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "SUB"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Sub(Register dst, Register val1, int val2, Flags flag) {
        String instr = "SUB"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Sbc functions
    public void Sbc(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "SBC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Sbc(Register dst, Register val1, int val2, Flags flag) {
        String instr = "SBC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Rsb functions
    public void Rsb(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "RSB"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Rsb(Register dst, Register val1, int val2, Flags flag) {
        String instr = "RSB"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Rsc functions
    public void Rsc(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "RSC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Rsc(Register dst, Register val1, int val2, Flags flag) {
        String instr = "RSC"+flag.toString() + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Move functions
    public void Mov(Register dst, Register val, Flags flag) {
        String instr = "MOV"+flag.toString() + " " + dst.getName() + ", " + val.getName();

        // Write instruction to file
        write(instr);
    }

    public void Mov(Register dst, int val, Flags flag) {
        String instr = "MOV"+flag.toString() + " " + dst.getName() + ", #" + val;

        // Write instruction to file
        write(instr);
    }

    public void Mvn(Register dst, int val, Flags flag) {
        String instr = "MVN"+flag.toString() + " " + dst.getName() + ", #" + val;

        // Write instruction to file
        write(instr);
    }

    public void Mvn(Register dst, Register val, Flags flag) {
        String instr = "MVN"+flag.toString() + " " + dst.getName() + ", " + val.getName();

        // Write instruction to file
        write(instr);
    }


    public void And(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "AND"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void And(Register dst, Register val1, int val2, Flags flag) {
        String instr = "AND"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    public void Eor(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ORR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Eor(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ORR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Lsl functions
    public void Lsl(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "LSL"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Lsl(Register dst, Register val1, int val2, Flags flag) {
        String instr = "LSL"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Lsr functions
    public void Lsr(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "LSR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Lsr(Register dst, Register val1, int val2, Flags flag) {
        String instr = "LSR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Asr functions
    public void Asr(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ASR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Asr(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ASR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Ror functions
    public void Ror(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ROR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Ror(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ROR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }
    public void Orr(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "ORR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Orr(Register dst, Register val1, int val2, Flags flag) {
        String instr = "ORR"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    public void Bic(Register dst, Register val1, Register val2, Flags flag) {
        String instr = "BIC"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Bic(Register dst, Register val1, int val2, Flags flag) {
        String instr = "BIC"+flag.toString() + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // CMP functions
    public void Cmp(Register val1, Register val2) {
        String instr = "CMP" + " " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Cmp(Register val1, int val2) {
        String instr = "CMP" + " " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // CMN functions
    public void Cmn(Register val1, Register val2) {
        String instr = "CMN" + " " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Cmn(Register val1, int val2) {
        String instr = "CMN" + " " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // TST functions
    public void Tst(Register val1, Register val2) {
        String instr = "TST" + " " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Tst(Register val1, int val2) {
        String instr = "TST" + " " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // TEQ functions
    public void Teq(Register val1, Register val2) {
        String instr = "TEQ" + " " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Teq(Register val1, int val2) {
        String instr = "TEQ" + " " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // ADR functions
    // ADR generates an address by adding an immediate value to the PC,
    // and writes the result to the destination register.
    public void Adr(Register dst, String label, Flags flag) {
        String instr = "ADR" + flag.toString() + " " + dst.getName() + ", " + label;

        // Write instruction to file
        write(instr);
    }
}
