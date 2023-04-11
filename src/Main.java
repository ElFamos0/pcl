import java.io.IOException;
import java.sql.Time;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import asm.ASMVisitor;
import asm.ASMWriter;
import parser.*;
import parser.exprParser.ProgramContext;
import sl.*;
import ast.*;
import csem.CSemVisitor;
import csem.ErrorHandler;
import graphViz.GraphVizVisitor;

public class Main {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Error : Expected 1 argument filepath, found 0");
            return;
        }

        String testFile = args[0];

        // Create a new thread to update and print elapsed time
        Thread timerThread = new Thread(() -> {
            Time startTime = new Time(System.currentTimeMillis());
            while (true) {
                long ms = (System.currentTimeMillis() - startTime.getTime());
                // Format ms to 0.000s
                String elapsedSeconds = String.format("%.3f", ms / 1000.0);
                System.out.print("\r\033[0;34m COMPILING... (" + elapsedSeconds + "s) \033[0m");
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // Thread was interrupted, exit the loop
                    break;
                }
            }
        });
        timerThread.start();

        try {
            // chargement du fichier et construction du parser
            CharStream input = CharStreams.fromFileName(testFile);
            exprLexer lexer = new exprLexer(input);
            CommonTokenStream stream = new CommonTokenStream(lexer);
            exprParser parser = new exprParser(stream);

            // Error handling
            ErrorHandler errorHandler = new ErrorHandler();

            // Récupération du noeud program (le noeud à la racine)
            ProgramContext program = parser.program();

            // Creation de la TDS
            SymbolLookup table = new SymbolLookup(null);

            // Visiteur de création de l'AST + création de l'AST
            AstCreator creator = new AstCreator(table, errorHandler);
            Ast ast = program.accept(creator);

            System.out.println(table.toString());

            // Visiteur de représentation graphique + appel
            GraphVizVisitor graphViz = new GraphVizVisitor();
            ast.accept(graphViz);

            // Controle semantique
            CSemVisitor csem = new CSemVisitor(table, errorHandler);
            ast.accept(csem);

            graphViz.dumpGraph("./out/tree.dot");

            ASMWriter writer = new ASMWriter("test.asm");

            ASMVisitor asmv = new ASMVisitor(table, writer);
            ast.accept(asmv);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            timerThread.interrupt();
            System.out.println("\r\033[0;32m COMPILATION SUCCESSFUL \033[0m");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
        timerThread.interrupt();
        System.out.println("\r\033[0;31m COMPILATION FAILED \033[0m");
    }
}
