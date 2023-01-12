package csem;

import sl.SymbolLookup;
import sl.TypeInferer;
import ast.Int;
import ast.Negation;
import parser.exprParser.NegationContext;
import sl.Primitive;

public class OpCSem {
    public static void checkint(Int a, SymbolLookup table) {
        CSemErrorFormatter err = new CSemErrorFormatter();
        String s = a.valeur;
        // Check for overflow / underflow

        if (!(a.ctx.getParent().getParent() instanceof NegationContext)) {
            String max = Integer.MAX_VALUE + "";
            if (s.length() > max.length()) {
                err.printError(a.ctx, "Integer overflow");
                return;
            } else if (s.length() == max.length()){
                for (int i = 0; i < s.length(); i++){
                    if (s.charAt(i) > max.charAt(i)){
                        err.printError(a.ctx, "Integer overflow");
                        return;
                    }
                }
            }
        } else {
            s = "-" + s;
            String min = Integer.MIN_VALUE + "";
            if (s.length() > min.length()) {
                err.printError(a.ctx, "Integer underflow");
                return;
            } else if (s.length() == min.length()){
                for (int i = 0; i < s.length(); i++){
                    if (s.charAt(i) > min.charAt(i)){
                        err.printError(a.ctx, "Integer underflow");
                        return;
                    }
                }
            }
        }
        if(table.getSymbol(s) != null) {
            if (!(table.getSymbol(s).getType().equals(new Primitive(Integer.class)))){
                err.printError(a.ctx, "Not an integer");
            }
        } else{ 
            if (!(TypeInferer.inferType(table, s).equals(new Primitive(Integer.class)))){
                err.printError(a.ctx, "Not an integer");
            }
        }
    }
}
