package csem;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import ast.*;
import sl.Array;
import sl.Function;
import sl.Record;
import sl.Symbol;
import sl.SymbolLookup;
import sl.Type;
import sl.TypeInferer;
import parser.exprParser.OperationBoucleContext;

public class CSemVisitor implements AstVisitor<String> {
    private SymbolLookup table;
    private int region;
    private ErrorHandler errorHandler;

    public CSemVisitor(SymbolLookup table, ErrorHandler errorHandler) {
        this.table = table;
        region = 0;
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public String visit(Program a) {
        a.expression.accept(this);
        return null;
    }

    @Override
    public String visit(Expression a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);

        // If expression is like `idf := expr` we need to check that expr is the same
        // type as idf
        if (right != null) {
            SymbolLookup table = this.table.getSymbolLookup(region);
            Type t = TypeInferer.inferType(table, right);
            if (table.getSymbol(left) != null) {
                Type t2 = table.getSymbol(left).getType();
                if (!t.equals(t2) && !t2.equals(TypeInferer.inferType(table, "nil"))) {
                    errorHandler.error(a.ctx,
                            "type mismatch between '" + left + "' and '" + right + "' of type " + t2 + " and " + t);
                }
            }
        }

        return left + ":" + right;
    }

    @Override
    public String visit(Ou a) {
        a.left.accept(this);
        a.right.accept(this);

        return null;
    }

    @Override
    public String visit(Et a) {
        a.left.accept(this);
        a.right.accept(this);

        return null;
    }

    @Override
    public String visit(Compar a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);
        String operator = a.operator;

        SymbolLookup table = this.table.getSymbolLookup(region);

        // Check if left and right are integers or string

        if (operator.equals("<") || operator.equals(">") || operator.equals(">=") || operator.equals("<=")) {
            OpCSem.checkIntOrString(a.ctx, left, right, table,errorHandler);
        } else if (operator.equals("=") || operator.equals("<>")) {
            OpCSem.checksametype(a.ctx, left, right, table,errorHandler);
        }

        return left + ":" + right;
    }

    @Override
    public String visit(Addition a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table,errorHandler);

        // System.out.println("Addition: " + left + " + " + right);

        return left + ":" + right;
    }

    @Override
    public String visit(Soustraction a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table,errorHandler);

        return left + ":" + right;
    }

    @Override
    public String visit(Multiplication a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table, errorHandler);

        return left + ":" + right;
    }

    @Override
    public String visit(Division a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table,errorHandler);

        // check division by zero

        if (right.equals("0")) {
            errorHandler.error(a.ctx, "Division by zero");
        }

        return left + ":" + right;
    }

    @Override
    public String visit(Sequence a) {
        String seq = "";
        for (Ast ast : a.seqs) {
            seq += ast.accept(this) + ":";
        }

        // Return the last expression
        // Because this is the only one to infer the type
        return seq.substring(0, seq.length() - 1);
    }

    @Override
    public String visit(Negation a) {
        String left = a.expression.accept(this);
        String right = null;
        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table,errorHandler);
        String data = a.expression.accept(this);
        if (data.startsWith("-")) {
            return data.substring(1);
        } else {
            return "-" + data;
        }
    }

    @Override
    public String visit(ID a) {
        String idf = a.nom;

        return idf;
    }

    @Override
    public String visit(Int a) {
        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a, table,errorHandler);

        String val = String.valueOf(a.valeur);

        return val;
    }

    @Override
    public String visit(ExpressionIdentifiant a) {
        a.left.accept(this);

        if (a.right != null) {
            a.right.accept(this);
        }

        return null;
    }

    @Override
    public String visit(AppelFonction a) {
        String idf = a.id.accept(this);
        String args = a.args.accept(this);
        String[] arg = args.split(":");

        SymbolLookup table = this.table.getSymbolLookup(region);

        Function f = (Function) table.getSymbol(idf);

        if (table.getSymbol(idf) == null)
            errorHandler.error(a.ctx, "Function " + idf + " is not defined");

        if (f != null && !args.equals("") && f.getParamsCount() != arg.length)
            errorHandler.error(a.ctx, "Function " + idf + " expects " + f.getParamsCount() + " arguments, but "
                    + arg.length + " were given");

        for (int i = 0; i < arg.length; i++) {
            Type t = TypeInferer.inferType(table, arg[i]);

            if (f != null && i < f.getParamsCount() && !(t.equals(f.getParams().get(i).getType())))
                errorHandler.error(a.ctx, "Function " + idf + " expects " + f.getParams().get(i).getType()
                        + " as argument " + (i + 1) + ", but " + t + " was given");
        }

        return "function:" + idf;
    }

    @Override
    public String visit(ArgFonction a) {
        String args = "";
        for (Ast ast : a.args) {
            args += ast.accept(this) + ":";
        }

        return args.equals("") ? "" : args.substring(0, args.length() - 1);
    }

    @Override
    public String visit(IfThenElse a) {
        int temp = region;
        region++;
        String cond = a.condition.accept(this);
        String then = a.thenBlock.accept(this);
        String els = a.elseBlock.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);

        OpCSem.checkint(a.ctx, cond, null, table,errorHandler);
        OpCSem.checksametype(a.ctx, then, els, table,errorHandler);

        region = temp;

        return null;
    }

    @Override
    public String visit(IfThen a) {
        int temp = region;
        region++;
        String cond = a.condition.accept(this);
        String then = a.thenBlock.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);

        OpCSem.checkint(a.ctx, cond, null, table,errorHandler);

        region = temp;

        return null;
    }

    @Override
    public String visit(While a) {
        int temp = region;
        String cond = a.condition.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        BouclesCSem.checkint(a.ctx, cond, table,errorHandler);

        region++;


        a.condition.accept(this);
        a.block.accept(this);

        region = temp;

        return "";
    }

    @Override
    public String visit(For a) {
        int temp = region;
        String start = a.startValue.accept(this);
        String end = a.endValue.accept(this);
        String id = a.start.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        BouclesCSem.checkint(a.ctx, start, table,errorHandler);
        BouclesCSem.checkint(a.ctx, end, table,errorHandler);
        region++;
        a.start.accept(this);
        a.startValue.accept(this);
        a.endValue.accept(this);
        a.block.accept(this);

        region = temp;

        return "for";
    }

    @Override
    public String visit(Definition a) {
        int temp = region;
        region++;
        for (Ast ast : a.declarations) {
            ast.accept(this);
        }
        for (Ast ast : a.exprs) {
            ast.accept(this);
        }

        region = temp;

        return "while";
    }

    @Override
    public String visit(DeclarationType a) {
        String idf = a.id.accept(this);
        String type = a.type.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);

        // Check if identifier is a reserved word
        if (idf.equals("array") || idf.equals("record")) {
            errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        }

        // Check for existence of the type
        if (table.getType(type) == null && table.getType(idf) == null) {
            errorHandler.error(a.ctx, "Type '" + type + "' not defined");
        }

        return null;
    }

    @Override
    public String visit(DeclarationTypeClassique a) {
        String idk = a.id.accept(this);

        return idk;
    }

    @Override
    public String visit(DeclarationArrayType a) {
        String idk = a.id.accept(this);

        return idk;
    }

    @Override
    public String visit(DeclarationRecordType a) {
        List<String> fields = new ArrayList<>();
        for (Ast ast : a.champs) {
            String expr = ast.accept(this);

            String[] split = expr.split(":");
            String idf = split[0];
            String type = split[1];

            SymbolLookup table = this.table.getSymbolLookup(region);

            // Check for existence of the field
            if (fields.contains(idf)) {
                errorHandler.error(a.ctx, "Field '" + split[0] + "' is redefined in record");
            }
            // Check for existence of the type
            if (table.getType(type) == null) {
                errorHandler.error(a.ctx, "Type '" + split[1] + "' is not defined");
            }

            fields.add(idf);
        }

        return null;
    }

    @Override
    public String visit(DeclarationChamp a) {
        String idf = a.id.accept(this);
        String type = a.type.accept(this);

        // Check if identifier is a reserved word
        if (idf.equals("array") || idf.equals("record")) {
            errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        }

        return idf + ":" + type;
    }

    @Override
    public String visit(DeclarationFonction a) {
        int temp = region;
        region++;
        SymbolLookup table = this.table.getSymbolLookup(temp);
        String idf = a.id.accept(this);

        // Check if identifier is a reserved word
        if (idf.equals("array") || idf.equals("record")) {
            errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        }

        if (!(table.getSymbol(idf) instanceof Function))
            return null;

        Function f = (Function) table.getSymbol(idf);
        SymbolLookup fTable = f.getTable();

        for (Ast ast : a.args) {
            String field = ast.accept(this);
            String[] split = field.split(":");
            String err = FuncCSem.checkArgs(split[0], split[1], fTable);

            if (err != null)
                errorHandler.error(a.ctx, err);
        }
        if (a.has_return)
            a.return_type.accept(this);

        String expr = a.expr.accept(this);
        Type t = TypeInferer.inferType(table, expr);
        Type ft = table.getSymbol(idf).getType();

        if (!t.equals(ft) && !ft.equals(TypeInferer.inferType(table, "nil")))
            errorHandler.error(a.ctx, "Type mismatch in function declaration " + idf + " : " + ft + " != " + t);

        region = temp;

        return null;
    }

    @Override
    public String visit(DeclarationValeur a) {
        String idf = a.id.accept(this);
        String type = null;
        if (a.getType() != null) {
            type = a.getType().accept(this);
        }
        String expr = a.expr.accept(this);
        String[] split = null;

        // Check if identifier is a reserved word
        if (idf.equals("array") || idf.equals("record")) {
            errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        }

        if (expr != null && expr.contains(":")) {
            split = expr.split(":");
        }

        SymbolLookup table = this.table.getSymbolLookup(region);

        if (type == null) {
            return null;
        }

        if (split != null && split[0].equals("function")) {
            Type t = table.getSymbol(split[1]).getType();
            Type t2 = TypeInferer.inferType(table, type);

            if (!t.equals(t2) && !t2.equals(TypeInferer.inferType(table, "nil"))) {
                errorHandler.error(a.ctx, "Type mismatch in variable declaration " + idf + " : " + type + " != " + t);
            }
        }

        Type t = table.getType(type);
        if (t == null) {
            errorHandler.error(a.ctx, "Type '" + type + "' not defined");
        }
        Type tExpr = TypeInferer.inferType(table, expr);
        if (!t.equals(tExpr) && !tExpr.equals(TypeInferer.inferType(table, "nil"))) {
            errorHandler.error(a.ctx, "Type mismatch in variable declaration " + idf + " : " + type + " != " + tExpr);
        }

        return null;
    }

    @Override
    public String visit(ChaineChr a) {
        String val = a.valeur;

        return val;
    }

    @Override
    public String visit(Nil a) {
        String val = "nil";

        return val;
    }

    @Override
    public String visit(Break a) {
        // check if in a loop
        // ParserRuleContext parent = a.ctx.getParent();
        // while (parent != null) {
        //     if (parent.getParent() instanceof OperationBoucleContext) {
        //         return null;
        //     }
        //     parent = parent.getParent();
        // }
        // errorHandler.error(a.ctx, "Break outside of loop");
        BouclesCSem.checkInBoucle(a.ctx, table, errorHandler);
        return null;
    }

    @Override
    public String visit(InstanciationType a) {

        String fidf = a.getId().accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type t = table.getType(fidf);
        if (t == null) {
            errorHandler.error(a.ctx, "Type '" + fidf + "' not defined");
        }

        Record r = new Record();
        if (t instanceof Record) {
            r = (Record) t;
        }
        List<String> fields = new ArrayList<String>();

        ArrayList<Ast> idf = a.getIdentifiants();
        ArrayList<Ast> expr = a.getExpressions();
        for (int i = 0; i < idf.size(); i++) {
            String fieldname = idf.get(i).accept(this);
            String exp = expr.get(i).accept(this);

            // Check for existence of the field
            if (r.getField(fieldname) == null) {
                errorHandler.error(a.ctx, "Field '" + fieldname + "' not defined in type '" + fidf + "'");
                continue;
            }
            // Check for type of the field
            Type fieldtype = r.getField(fieldname).getType();
            Type expType = TypeInferer.inferType(table, exp);
            if (!fieldtype.equals(expType) && !expType.equals(TypeInferer.inferType(table, "nil"))) {
                errorHandler.error(a.ctx,
                        "Type mismatch in field '" + fieldname + "' : " + fieldtype + " != " + expType);
            }

            fields.add(fieldname);
        }

        // List missing fields
        for (Symbol field : r.getFields()) {
            if (!fields.contains(field.getName())) {
                errorHandler.error(a.ctx,
                        "Field '" + field.getName() + "' not defined on instanciation of type '" + fidf + "'");
            }
        }

        if (t instanceof Record) {
            return r.toString();
        }

        return fidf;
    }

    @Override
    public String visit(AccesChamp a) {
        return a.getChild().accept(this);
    }

    @Override
    public String visit(ExpressionArray a) {
        String idf = a.getId().accept(this);
        String size = a.getSize().accept(this);
        String expr = a.getExpr().accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type t = table.getType(idf);
        if (!(t instanceof Array)) {
            errorHandler.error(a.ctx, "Type '" + idf + "' is not an array type");
        }

        // Check if size is an integer
        if (!TypeInferer.inferType(table, size).equals(TypeInferer.inferType(table, "1"))) {
            errorHandler.error(a.ctx, "Size of array must be an integer");
        } else {
            // Check that size is positive
            if (Integer.parseInt(size) <= 0) {
                errorHandler.error(a.ctx, "Size of array must be positive");
            }
        }

        if (t instanceof Array) {
            // Check that expression is of the right type
            Type exprType = TypeInferer.inferType(table, expr);
            Type arrayType = ((Array) t).getType();
            if (!exprType.equals(arrayType)) {
                errorHandler.error(a.ctx, "Type mismatch in array declaration : " + arrayType + " != " + exprType);
            }
        }

        return null;
    }

    @Override
    public String visit(ListeAcces a) {
        String idf = a.getId().accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type t = table.getSymbol(idf).getType();
        if (a.getisExpressionArray()) {
            String idk = a.getExpressionArray().accept(this);
            return idk;
        } else {
            String out = "";
            for (Ast ast : a.getAccesChamps()) {
                String field = ast.accept(this);
                if (t instanceof Record) {
                    Record r = (Record) t;
                    if (r.getField(field) == null) {
                        errorHandler.error(a.ctx, "Field '" + field + "' not defined in type '" + t + "'");
                        break;
                    }
                    t = r.getField(field).getType();
                    out += "." + field;
                } else if (t instanceof Array) {
                    Array a1 = (Array) t;
                    // Check if field is an integer, positive and less than size of array
                    if (!TypeInferer.inferType(table, field).equals(TypeInferer.inferType(table, "1"))) {
                        errorHandler.error(a.ctx, "Index of array must be an integer");
                        break;
                    } else {
                        if (Integer.parseInt(field) <= 0) {
                            errorHandler.error(a.ctx, "Index of array must be positive");
                            break;
                        } else {
                            if (Integer.parseInt(field) > ((Array) t).getOffset()) {
                                errorHandler.error(a.ctx, "Index of array must be less than size of array");
                                break;
                            }
                        }
                    }
                    t = a1.getType();
                    out += "[" + field + "]";
                } else {
                    errorHandler.error(a.ctx, "Type '" + t + "' is not a record or an array");
                    break;
                }
            }
            // return idf.field1.field2...
            return idf + out;
        }
    }

}
