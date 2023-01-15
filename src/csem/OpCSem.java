package csem;

import sl.SymbolLookup;
import sl.TypeInferer;

import org.antlr.v4.runtime.ParserRuleContext;

import ast.Int;
import parser.exprParser.NegationContext;
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

    public static void checkint(ParserRuleContext ctx, String left, String right, SymbolLookup table, ErrorHandler errorHandler) {
        String split[] = left.split(":");

        // check if an expression return null

        if (split.length == 1) {
            String s = split[0];
            // System.out.println(s);
            if (table.getSymbol(s) != null) {
                if (!(table.getSymbol(s).getType().equals(new Primitive(Integer.class)))) {
                    errorHandler.error(ctx, s + " is not an integer");
                }
            } else {
                if (!(TypeInferer.inferType(table, s).equals(new Primitive(Integer.class)))) {
                    if (s == "") {
                        if (ctx instanceof SiAlorsContext || ctx instanceof SiAlorsSinonContext) {
                            errorHandler.error(ctx, "Cannot evaluate null in if statement");
                        } else {
                            errorHandler.error(ctx, "Cannot make operation with null");
                        }
                    } else {
                        errorHandler.error(ctx, s + " is not an integer");
                    }
                }
            }
        }

        if (right == null) {
            return;
        }
        String split2[] = right.split(":");

        if (split2.length == 1) {
            String s2 = split2[0];
            // System.out.println(s2);
            if (table.getSymbol(s2) != null) {
                if (!(table.getSymbol(s2).getType().equals(new Primitive(Integer.class)))) {
                    errorHandler.error(ctx, s2 + " is not an integer");
                }
            } else {
                if (!(TypeInferer.inferType(table, s2).equals(new Primitive(Integer.class)))) {
                    if (s2 == "") {
                        errorHandler.error(ctx, "Cannot make operation with null");
                    } else {
                        errorHandler.error(ctx, s2 + " is not an integer");
                    }
                }
            }
        }
    }

    public static void checkIntOrString(ParserRuleContext ctx, String left, String right, SymbolLookup table, ErrorHandler errorHandler) {
        String split[] = left.split(":");
        boolean isInt = false;

        if (split.length == 1) {
            String s = split[0];
            // System.out.println(s);
            // System.out.println(table.getSymbol(s).getType());
            if (table.getSymbol(s) != null) {
                if (table.getSymbol(s).getType().equals(new Primitive(Integer.class))) {
                    isInt = true;
                }

                else if (!(table.getSymbol(s).getType().equals(new Array(new Primitive(Character.class))))) {
                    errorHandler.error(ctx, "Invalid type for comparaison");
                }
            } else {
                if (TypeInferer.inferType(table, s).equals(new Primitive(Integer.class))) {
                    isInt = true;
                    // System.out.println(s + "isInt");
                } else if (!(TypeInferer.inferType(table, s).equals(new Array(new Primitive(Character.class))))) {
                    errorHandler.error(ctx, "Invalid type for comparaison");
                }
            }
        }

        String split2[] = right.split(":");

        if (split2.length == 1) {
            String s2 = split2[0];
            // System.out.println(s2);
            if (table.getSymbol(s2) != null) {
                if (table.getSymbol(s2).getType().equals(new Primitive(Integer.class))) {
                    if (!isInt) {
                        errorHandler.error(ctx, "Cannot compare an integer and a string");
                    }
                } else if (table.getSymbol(s2).getType().equals(new Array(new Primitive(Character.class)))) {
                    if (isInt) {
                        errorHandler.error(ctx, "Cannot compare an integer and a string");
                    } else {
                        errorHandler.error(ctx, "Invalid type for comparaison");
                    }
                }
            } else {
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

    }

    public static void checksametype(ParserRuleContext ctx, String left, String right, SymbolLookup table, ErrorHandler errorHandler) {
        String split[] = left.split(":");
        String split2[] = right.split(":");
        if (split.length == 1 && split2.length == 1) {
            String s = split[0];
            String s2 = split2[0];
            if (table.getSymbol(s) != null && table.getSymbol(s2) != null) {
                if (!(table.getSymbol(s).getType().equals(table.getSymbol(s2).getType()))) {
                    if (ctx instanceof SiAlorsSinonContext) {
                        errorHandler.error(ctx, "Not the same value in then and else block");
                    } else {
                    errorHandler.error(ctx, "Cannot compare different types");
                    }
                }
            } else if (table.getSymbol(s) == null && table.getSymbol(s2) == null) {
                if (!(TypeInferer.inferType(table, s).equals(TypeInferer.inferType(table, s2)))) {
                    if (ctx instanceof SiAlorsSinonContext) {
                        errorHandler.error(ctx, "Not the same value in then and else block");
                    } else {
                    errorHandler.error(ctx, "Cannot compare different types");
                    }
                }
            } else if (table.getSymbol(s) == null && table.getSymbol(s2) != null) {
                if (!(TypeInferer.inferType(table, s).equals(table.getSymbol(s2).getType()))) {
                    if (ctx instanceof SiAlorsSinonContext) {
                        errorHandler.error(ctx, "Not the same value in then and else block");
                    } else {
                    errorHandler.error(ctx, "Cannot compare different types");
                    }
                }
            } else if (table.getSymbol(s2) == null && table.getSymbol(s) != null) {
                if (!(table.getSymbol(s).getType().equals(TypeInferer.inferType(table, s2)))) {
                    if (ctx instanceof SiAlorsSinonContext) {
                        errorHandler.error(ctx, "Not the same value in then and else block");
                    } else {
                    errorHandler.error(ctx, "Cannot compare different types");
                    }
                }
            }
        }
    }
}
