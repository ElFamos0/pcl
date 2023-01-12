package csem;

import org.antlr.v4.runtime.ParserRuleContext;

public class CSemErrorFormatter {

    public void printError(ParserRuleContext ctx, String error) {
        // Print error like this :
        // [error] : integer overflow at line 1 column 1
        // var a:int := 2147483648
        //              ^

        System.out.println("[\033[31;1merror\033[0m] : " + error + " at line " + ctx.getStart().getLine() + " column " + ctx.getStart().getCharPositionInLine());
        String program = ctx.getStart().getInputStream().toString();
        String currentLine = program.split("\n")[ctx.getStart().getLine() - 1];
        System.out.println("   "+currentLine);
        System.out.print("   ");
        for (int i = 0; i < ctx.getStart().getCharPositionInLine(); i++) {
            System.out.print(" ");
        }
        System.out.println("^");
    }

    public void printWarning(ParserRuleContext ctx, String warning) {
        // Print wa like this :
        // [warning] : integer overflow at line 1 column 1
        // var a:int := 2147483648
        //              ^

        System.out.println("[\033[33;1mwarning\033[0m] : " + warning + " at line " + ctx.getStart().getLine() + " column " + ctx.getStart().getCharPositionInLine());
        String program = ctx.getStart().getInputStream().toString();
        String currentLine = program.split("\n")[ctx.getStart().getLine() - 1];
        System.out.println("   "+currentLine);
        System.out.print("   ");
        for (int i = 0; i < ctx.getStart().getCharPositionInLine(); i++) {
            System.out.print(" ");
        }
        System.out.println("^");
    }
    
}
