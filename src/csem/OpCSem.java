package csem;

import sl.SymbolLookup;
import sl.Type;

import org.antlr.v4.runtime.ParserRuleContext;

import ast.Int;
import parser.exprParser.NegationContext;
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
    }

    public static void checkint(ParserRuleContext ctx, Type left, Type right, SymbolLookup table,
            ErrorHandler errorHandler, String leftExpr, String rightExpr) {

        if (left == null || !left.equals(new Primitive(Integer.class))) {
            if (!(leftExpr.contains("+") || leftExpr.contains("-") || leftExpr.contains("*")
                    || leftExpr.contains("/"))) {
                if (leftExpr.contains("for:") || left == null) {
                    errorHandler.error(ctx, "Cannot add null to an integer");
                } else {
                    errorHandler.error(ctx, leftExpr + " is not an integer");
                }
            }
        }

        if (right == null || !right.equals(new Primitive(Integer.class))) {
            if (!(rightExpr.contains("+") || rightExpr.contains("-") || rightExpr.contains("*")
                    || rightExpr.contains("/"))) {
                if (rightExpr.contains("for:") || right == null) {
                    errorHandler.error(ctx, "Cannot add null to an integer");
                } else {
                    errorHandler.error(ctx, rightExpr + " is not an integer");
                }
            }
        }

    }

    public static void checkIntOrString(ParserRuleContext leftCtx, ParserRuleContext rightCtx, Type left, Type right,
            SymbolLookup table,
            ErrorHandler errorHandler) {
        boolean isInt = false;
        if (left.equals(new Primitive(Integer.class))) {
            isInt = true;
            // System.out.println("left is int");
        } else if (!left.equals(new Array(new Primitive(Character.class)))) {
            errorHandler.error(leftCtx, "Invalid type for comparaison");
        }

        if (right.equals(new Primitive(Integer.class))) {
            if (!isInt) {
                errorHandler.error(rightCtx, "Cannot compare an integer and a string");
            }
        } else if (right.equals(new Array(new Primitive(Character.class)))) {
            if (isInt) {
                errorHandler.error(rightCtx, "Cannot compare an integer and a string");
            }
        } else {
            errorHandler.error(rightCtx, "Invalid type for comparaison");
        }

    }
}
