import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

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

            // Visiteur de représentation graphique + appel
            GraphVizVisitor graphViz = new GraphVizVisitor();
            ast.accept(graphViz);

            // Controle semantique
            CSemVisitor csem = new CSemVisitor(table, errorHandler);
            ast.accept(csem);

            // Display TDS
            System.out.println(table);

            if (errorHandler.getErrorCount() > 0) {
                System.out.println("Compilation failed with " + errorHandler.getErrorCount() + " errors");
                System.exit(1);
            }

            System.out.println("Compilation successful");

            graphViz.dumpGraph("./out/tree.dot");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
    }
}
