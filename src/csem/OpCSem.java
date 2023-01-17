package csem;

import sl.SymbolLookup;
import sl.TypeInferer;
import sl.Type;

import org.antlr.v4.runtime.ParserRuleContext;

import ast.Int;
import parser.exprParser.NegationContext;
import parser.exprParser.PourContext;
import parser.exprParser.SiAlorsContext;
import parser.exprParser.SiAlorsSinonContext;
import sl.Primitive;
import sl.Array;

public class OpCSem {
    public static void checkint(Int a, SymbolLookup table, ErrorHandler errorHandler) {
        String s = a.valeur;
        // Check for overflow / underflow

        if (!(a.ctx.getParent().getParent() instanceof NegationContext)) {
            String max = Integer.MAX_VALUE + "";
            if (s.length() > max.length()) {
                errorHandler.error(a.ctx, "Integer overflow");
                return;
            } else if (s.length() == max.length()) {
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) > max.charAt(i)) {
                        errorHandler.error(a.ctx, "Integer overflow");
                        return;
                    }
                }
            }
        } else {
            s = "-" + s;
            String min = Integer.MIN_VALUE + "";
            if (s.length() > min.length()) {
                errorHandler.error(a.ctx, "Integer underflow");
                return;
            } else if (s.length() == min.length()) {
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) > min.charAt(i)) {
                        errorHandler.error(a.ctx, "Integer underflow");
                        return;
                    }
                }
            }
        }
        if (table.getSymbol(s) != null) {
            if (!(table.getSymbol(s).getType().equals(new Primitive(Integer.class)))) {
                errorHandler.error(a.ctx, "Not an integer");
            }
        } else {
            if (!(TypeInferer.inferType(table, s).equals(new Primitive(Integer.class)))) {
                errorHandler.error(a.ctx, "Not an integer");
            }
        }
    }

    public static void checkint(ParserRuleContext ctx, Type left, Type right, SymbolLookup table,
            ErrorHandler errorHandler, String leftExpr, String rightExpr) {
        // check if an expression return null

        if ((left == null) || !(left.equals(new Primitive(Integer.class)))) {
            if (ctx instanceof SiAlorsContext || ctx instanceof SiAlorsSinonContext) {
                errorHandler.error(ctx, "Cannot evaluate null in if statement");
            } else {
                errorHandler.error(ctx, "Cannot make operation with null");
            }
        } else {
            errorHandler.error(ctx, leftExpr + " is not an integer");
        }

        if (right == null)

        {
            return;
        }
        if (right == null || !(right.equals(new Primitive(Integer.class)))) {
            if (ctx instanceof PourContext) {
                errorHandler.error(ctx, "Cannot make operation with null");
            } else {
                errorHandler.error(ctx, rightExpr + " is not an integer");
            }
        }

    }

    public static void checkIntOrString(ParserRuleContext ctx, String left, String right, SymbolLookup table,
            ErrorHandler errorHandler) {
        String split[] = left.split("0x");
        boolean isInt = false;

        if (split.length == 1) {
            String s = split[0];
            // System.out.println(s);
            // System.out.println(table.getSymbol(s).getType());

            if (TypeInferer.inferType(table, s).equals(new Primitive(Integer.class))) {
                isInt = true;
                // System.out.println(s + "isInt");
            } else if (!(TypeInferer.inferType(table, s).equals(new Array(new Primitive(Character.class))))) {
                errorHandler.error(ctx, "Invalid type for comparaison");
            }

        }

        String split2[] = right.split("0x");

        if (split2.length == 1) {
            String s2 = split2[0];
            // System.out.println(s2);
            if (TypeInferer.inferType(table, s2).equals(new Primitive(Integer.class))) {
                if (!isInt) {
                    errorHandler.error(ctx, "Cannot compare an integer and a string");
                }
            } else if (TypeInferer.inferType(table, s2).equals(new Array(new Primitive(Character.class)))) {
                if (isInt) {
                    errorHandler.error(ctx, "Cannot compare an integer and a string");
                } else {
                    errorHandler.error(ctx, "Invalid type for comparaison");
                }
            }
        }
    }

    public static void checksametype(ParserRuleContext ctx, String left, String right, SymbolLookup table,
            ErrorHandler errorHandler) {
        String split[] = left.split("0x");
        String split2[] = right.split("0x");
        if (split.length == 1 && split2.length == 1) {
            String s = split[0];
            String s2 = split2[0];
            if (!(TypeInferer.inferType(table, s).equals(TypeInferer.inferType(table, s2)))) {
                if (ctx instanceof SiAlorsSinonContext) {
                    errorHandler.error(ctx, "Not the same value in then and else block");
                } else {
                    errorHandler.error(ctx, "Cannot compare different types");
                }

            }
        }
    }
}
