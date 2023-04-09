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
}
