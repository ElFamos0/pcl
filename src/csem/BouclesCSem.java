package csem;

import sl.SymbolLookup;
import sl.TypeInferer;

import org.antlr.v4.runtime.ParserRuleContext;

import ast.Int;
import parser.exprParser.NegationContext;
import parser.exprParser.PourContext;
import parser.exprParser.TantQueContext;
import parser.exprParser.BreakContext;
import sl.Primitive;

public class BouclesCSem {

    public static void checkint(ParserRuleContext ctx, String cond, SymbolLookup table, ErrorHandler errorHandler) {
        CSemErrorFormatter err = new CSemErrorFormatter();
        if(table.getSymbol(cond) != null) {
            if (!(table.getSymbol(cond).getType().equals(new Primitive(Integer.class)))){
                errorHandler.error(ctx, cond + " is not an integer");
            }
        } else{   
            if (!(TypeInferer.inferType(table, cond).equals(new Primitive(Integer.class)))){
                errorHandler.error(ctx, cond + " is not an integer");
            }
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
        if(!state) {
            errorHandler.error(ctx, "Break is not in a loop");
        }

    }   
}
