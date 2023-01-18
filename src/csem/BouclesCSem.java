package csem;

import sl.SymbolLookup;
import sl.Type;

import org.antlr.v4.runtime.ParserRuleContext;

import parser.exprParser.PourContext;
import parser.exprParser.TantQueContext;
import parser.exprParser.BreakContext;
import sl.Primitive;

public class BouclesCSem {

    public static void checkint(ParserRuleContext ctx, Type cond, SymbolLookup table, ErrorHandler errorHandler) {
        if (cond == null || !(cond.equals(new Primitive(Integer.class)))) {
            errorHandler.error(ctx, cond + " is not an integer");
        }
    }

    public static void checkInBoucle(BreakContext ctx, SymbolLookup table, ErrorHandler errorHandler) {

        boolean state = false;
        ParserRuleContext c = ctx;

        while (c != null && !state) {
            if (c instanceof PourContext || c instanceof TantQueContext) {
                state = true;
            }
            c = c.getParent();
        }
        if (!state) {
            errorHandler.error(ctx, "Break is not in a loop");
        }

    }
}
