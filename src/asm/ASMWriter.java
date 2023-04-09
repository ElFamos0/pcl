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
    public void Add(Register dst, Register val1, Register val2) {
        String instr = "ADD" + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Add(Register dst, Register val1, int val2) {
        String instr = "ADD" + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Adc functions
    public void Adc(Register dst, Register val1, Register val2) {
        String instr = "ADC" + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Adc(Register dst, Register val1, int val2) {
        String instr = "ADC" + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Sub functions
    public void Sub(Register dst, Register val1, Register val2) {
        String instr = "SUB" + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Sub(Register dst, Register val1, int val2) {
        String instr = "SUB" + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Sbc functions
    public void Sbc(Register dst, Register val1, Register val2) {
        String instr = "SBC" + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Sbc(Register dst, Register val1, int val2) {
        String instr = "SBC" + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Rsb functions
    public void Rsb(Register dst, Register val1, Register val2) {
        String instr = "RSB" + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Rsb(Register dst, Register val1, int val2) {
        String instr = "RSB" + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Rsc functions
    public void Rsc(Register dst, Register val1, Register val2) {
        String instr = "RSC" + " " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Rsc(Register dst, Register val1, int val2) {
        String instr = "RSC" + " " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Move functions
    public void Mov(Register dst, Register val) {
        String instr = "MOV" + " " + dst.getName() + ", " + val.getName();

        // Write instruction to file
        write(instr);
    }

    public void Mov(Register dst, int val) {
        String instr = "MOV" + " " + dst.getName() + ", #" + val;

        // Write instruction to file
        write(instr);
    }

    public void Mvn(Register dst, int val) {
        String instr = "MVN" + " " + dst.getName() + ", #" + val;

        // Write instruction to file
        write(instr);
    }

    public void Mvn(Register dst, Register val) {
        String instr = "MVN" + " " + dst.getName() + ", " + val.getName();

        // Write instruction to file
        write(instr);
    }


    public void And(Register dst, Register val1, Register val2) {
        String instr = "AND" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void And(Register dst, Register val1, int val2) {
        String instr = "AND" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    public void Eor(Register dst, Register val1, Register val2) {
        String instr = "ORR" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Eor(Register dst, Register val1, int val2) {
        String instr = "ORR" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Lsl functions
    public void Lsl(Register dst, Register val1, Register val2) {
        String instr = "LSL" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Lsl(Register dst, Register val1, int val2) {
        String instr = "LSL" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Lsr functions
    public void Lsr(Register dst, Register val1, Register val2) {
        String instr = "LSR" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Lsr(Register dst, Register val1, int val2) {
        String instr = "LSR" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Asr functions
    public void Asr(Register dst, Register val1, Register val2) {
        String instr = "ASR" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Asr(Register dst, Register val1, int val2) {
        String instr = "ASR" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Ror functions
    public void Ror(Register dst, Register val1, Register val2) {
        String instr = "ROR" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Ror(Register dst, Register val1, int val2) {
        String instr = "ROR" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }
    public void Orr(Register dst, Register val1, Register val2) {
        String instr = "ORR" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Orr(Register dst, Register val1, int val2) {
        String instr = "ORR" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    public void Bic(Register dst, Register val1, Register val2) {
        String instr = "BIC" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Bic(Register dst, Register val1, int val2) {
        String instr = "BIC" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    

}
