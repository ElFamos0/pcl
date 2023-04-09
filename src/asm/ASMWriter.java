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

    /// We can implement the {, SHIFT_op #expression} 
    /// but it's not necessary for the moment.

    /// Add functions
    public void Add(Register dst, Register val1, Register val2) {
        String instr = "add" + ", " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Add(Register dst, Register val1, int val2) {
        String instr = "add" + ", " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    /// Adc functions
    public void Adc(Register dst, Register val1, Register val2) {
        String instr = "adc" + ", " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Adc(Register dst, Register val1, int val2) {
        String instr = "adc" + ", " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    /// Sub functions
    public void Sub(Register dst, Register val1, Register val2) {
        String instr = "sub" + ", " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Sub(Register dst, Register val1, int val2) {
        String instr = "sub" + ", " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    // Sbc functions
    public void Sbc(Register dst, Register val1, Register val2) {
        String instr = "sbc" + ", " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Sbc(Register dst, Register val1, int val2) {
        String instr = "sbc" + ", " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    /// Rsb functions
    public void Rsb(Register dst, Register val1, Register val2) {
        String instr = "rsb" + ", " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Rsb(Register dst, Register val1, int val2) {
        String instr = "rsb" + ", " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    /// Rsc functions
    public void Rsc(Register dst, Register val1, Register val2) {
        String instr = "rsc" + ", " + dst.getName() + ", " + val1.getValue() + ", " + val2.getValue();

        // Write instruction to file
        write(instr);
    }

    public void Rsc(Register dst, Register val1, int val2) {
        String instr = "rsc" + ", " + dst.getName() + ", " + val1.getValue() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    /// Move functions
    public void Mov(Register dst, Register val) {
        String instr = "mov" + ", " + dst.getName() + ", " + val.getName();

        // Write instruction to file
        write(instr);
    }

    public void Mov(Register dst, int val) {
        String instr = "mov" + ", " + dst.getName() + ", #" + val;

        // Write instruction to file
        write(instr);
    }

    public void Mvn(Register dst, int val) {
        String instr = "mvn" + ", " + dst.getName() + ", #" + val;

        // Write instruction to file
        write(instr);
    }

    public void Mvn(Register dst, Register val) {
        String instr = "mvn" + ", " + dst.getName() + ", " + val.getName();

        // Write instruction to file
        write(instr);
    }


    public void And(Register dst, Register val1, Register val2) {
        String instr = "and" + " " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();
        // TODO: write instruction to file
    }

    public void And(Register dst, Register val1, int val2) {
        String instr = "and" + " " + dst.getName() + ", " + val1.getName() + ", #" + val2;
        // TODO: write instruction to file
    }

    public void And(Register dst, int val1, Register val2) {
        String instr = "and" + " " + dst.getName() + ", #" + val1 + ", " + val2.getName();
        // TODO: write instruction to file
    }

    public void And(Register dst, int val1, int val2) {
        String instr = "and" + " " + dst.getName() + ", #" + val1 + ", #" + val2;
        // TODO: write instruction to file
    }


    /// Lsl functions
    public void Lsl(Register dst, Register val1, Register val2) {
        String instr = "lsl" + ", " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Lsl(Register dst, Register val1, int val2) {
        String instr = "lsl" + ", " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    /// Lsr functions
    public void Lsr(Register dst, Register val1, Register val2) {
        String instr = "lsr" + ", " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Lsr(Register dst, Register val1, int val2) {
        String instr = "lsr" + ", " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    /// Asr functions
    public void Asr(Register dst, Register val1, Register val2) {
        String instr = "asr" + ", " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Asr(Register dst, Register val1, int val2) {
        String instr = "asr" + ", " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }

    /// Ror functions
    public void Ror(Register dst, Register val1, Register val2) {
        String instr = "ror" + ", " + dst.getName() + ", " + val1.getName() + ", " + val2.getName();

        // Write instruction to file
        write(instr);
    }

    public void Ror(Register dst, Register val1, int val2) {
        String instr = "ror" + ", " + dst.getName() + ", " + val1.getName() + ", #" + val2;

        // Write instruction to file
        write(instr);
    }
}
