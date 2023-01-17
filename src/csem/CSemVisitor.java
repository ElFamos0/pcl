package csem;

import java.util.ArrayList;
import java.util.List;

import ast.*;
import sl.Array;
import sl.Function;
import sl.Primitive;
import sl.Record;
import sl.Symbol;
import sl.SymbolLookup;
import sl.Type;
import sl.TypeInferer;

public class CSemVisitor implements AstVisitor<String> {
    private SymbolLookup table;
    private int region;
    private int biggestRegion = 0;
    private ErrorHandler errorHandler;
    private TypeInferer tipe = new TypeInferer();

    public CSemVisitor(SymbolLookup table, ErrorHandler errorHandler) {
        this.table = table;
        region = table.getLibFunc();
        biggestRegion = region;
        this.errorHandler = errorHandler;
    }

    public void StepOneRegion() {
        biggestRegion++;
        region = biggestRegion;
    }

    public void StepDownRegion() {
        region--;
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
        if (left == null || right == null) {
            return null;
        }

        // If expression is like `idf := expr` we need to check that expr is the same
        // type as idf
        if (right != null) {
            SymbolLookup table = this.table.getSymbolLookup(region);
            Type t = tipe.inferType(table, a.right);
            if (table.getSymbol(left) != null) {
                Type t2 = table.getSymbol(left).getType();
                if (t2 == null || !t.equals(t2)) {
                    errorHandler.error(a.ctx,
                            "Type mismatch between '" + left + "' and '" + right + "' of type " + t2 + " and " + t);
                }
            }
        }

        return left + "=" + right;
    }

    @Override
    public String visit(Ou a) {
        a.left.accept(this);
        a.right.accept(this);

        return null;
    }

    @Override
    public String visit(Et a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);

        return left + "0x87" + right;
    }

    @Override
    public String visit(Compar a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);
        String operator = a.operator;

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        // Check if left and right are integers or string

        if (operator.equals("<") || operator.equals(">") || operator.equals(">=") || operator.equals("<=")) {
            //System.out.println("Compar "+leftType+" "+rightType);
            OpCSem.checkIntOrString(a.ctx, leftType, rightType, table, errorHandler);
        } else if (operator.equals("=") || operator.equals("<>")) {
            //System.out.println("Compar "+leftType+" "+rightType);
            if (leftType != null && rightType != null && !leftType.equals(rightType)) {
                errorHandler.error(a.ctx, "Cannot compare different types");
            }
        }

        return left + "&&" + right;
    }

    @Override
    public String visit(Addition a) {
        String leftExpr = a.left.accept(this);
        String rightExpr = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type left = tipe.inferType(table, a.left);
        Type right = tipe.inferType(table, a.right);

        // Check if left and right are integers

        OpCSem.checkint(a.ctx, left, right, table, errorHandler, leftExpr, rightExpr);

        // OpCSem.checkint(a.ctx, left, right, table, errorHandler, leftExpr,
        // rightExpr);

        // System.out.println("Addition: " + left + " + " + right);

        return leftExpr + "+" + rightExpr;
    }

    @Override
    public String visit(Soustraction a) {
        String leftExpr = a.left.accept(this);
        String rightExpr = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type left = tipe.inferType(table, a.left);
        Type right = tipe.inferType(table, a.right);

        // Check if left and right are integers

        OpCSem.checkint(a.ctx, left, right, table, errorHandler, leftExpr, rightExpr);

        return leftExpr + "-" + rightExpr;
    }

    @Override
    public String visit(Multiplication a) {
        String leftExpr = a.left.accept(this);
        String rightExpr = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type left = tipe.inferType(table, a.left);
        Type right = tipe.inferType(table, a.right);

        // Check if left and right are integers
        OpCSem.checkint(a.ctx, left, right, table, errorHandler, leftExpr, rightExpr);

        return leftExpr + "*" + rightExpr;
    }

    @Override
    public String visit(Division a) {
        String leftExpr = a.left.accept(this);
        String rightExpr = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type left = tipe.inferType(table, a.left);
        Type right = tipe.inferType(table, a.right);

        // Check if left and right are integers
        OpCSem.checkint(a.ctx, left, right, table, errorHandler, leftExpr, rightExpr);
        // check division by zero
        if (rightExpr.equals("0")) {
            errorHandler.error(a.ctx, "Division by zero");
        }

        return leftExpr + "/" + rightExpr;
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
        String leftExpr = a.expression.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type left = tipe.inferType(table, a.expression);

        // Check if left and right are integers
        if (left == null || !left.equals(new Primitive(Integer.class))) {
            errorHandler.error(a.ctx, leftExpr + " is not an integer");
        }

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

        if (table != null)
            OpCSem.checkint(a, table, errorHandler);

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

        if (table == null)
            return idf;

        Function f = (Function) table.getSymbol(idf);

        if (table.getSymbol(idf) == null)
            errorHandler.error(a.ctx, "Function " + idf + " is not defined");

        if (f != null && !args.equals("") && f.getParamsCount() != arg.length)
            errorHandler.error(a.ctx, "Function " + idf + " expects " + f.getParamsCount() + " arguments, but "
                    + arg.length + " were given");

        for (int i = 0; i < f.getParamsCount(); i++) {
            Type t = TypeInferer.inferType(table, arg[i]);
            Type ft = f.getParams().get(f.getParamsCount() - (i + 1)).getType();

            if (f != null && t != null && i < f.getParamsCount()
                    && !(ft.equals(t)))
                errorHandler.error(a.ctx, "Function " + idf + " expects " + ft
                        + " as argument " + (i + 1) + ", but " + t + " was given");
        }

        return idf;
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
        String cond = a.condition.accept(this);

        StepOneRegion();
        String then = a.thenBlock.accept(this);
        Type thenType = tipe.inferType(table.getSymbolLookup(region), a.thenBlock);

        StepOneRegion();
        String els = a.elseBlock.accept(this);
        Type elseType = tipe.inferType(table.getSymbolLookup(region), a.elseBlock);

        SymbolLookup table = this.table.getSymbolLookup(temp);
        Type condType = tipe.inferType(table, a.condition);

        if (condType == null || !condType.equals(new Primitive(Integer.class))) {
            if (condType == null || cond.contains("for")){
                errorHandler.error(a.ctx, "Condition of if statement cannot be null");
            }
            else{
                errorHandler.error(a.ctx, "Condition of if "+ cond + " is not an integer");
            }
            
        }

        // OpCSem.checksametype(a.ctx, then, els, table, errorHandler);
        if (thenType == null || elseType == null || !thenType.equals(elseType)) {
            errorHandler.error(a.ctx, "then and else blocks must return the same type");
        }

        region = temp;

        return "ifthen" + then + "else" + els;
    }

    @Override
    public String visit(IfThen a) {
        int temp = region;
        String cond = a.condition.accept(this);
        StepOneRegion();
        String then = a.thenBlock.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(temp);
        Type condType = tipe.inferType(table, a.condition);

        if (condType == null || !condType.equals(new Primitive(Integer.class))) {
            if (condType == null || cond.contains("for")){
                errorHandler.error(a.ctx, "Condition of if statement cannot be null");
            }
            else{
                errorHandler.error(a.ctx, "Condition of if "+ cond + " is not an integer");
            }
        }

        region = temp;

        return "ifthen" + then;
    }

    @Override
    public String visit(While a) {
        int temp = region;
        String cond = a.condition.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(temp);
        Type condType = tipe.inferType(table, a.condition);

        if (condType == null || !condType.equals(new Primitive(Integer.class))) {
            errorHandler.error(a.ctx, cond + " is not an integer");
        }

        StepOneRegion();

        a.condition.accept(this);
        String b = a.block.accept(this);

        region = temp;

        return "while:" + b;
    }

    @Override
    public String visit(For a) {
        int temp = region;
        String id = a.start.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type start = tipe.inferType(table, a.startValue);
        Type end = tipe.inferType(table, a.endValue);

        if (table != null) {
            BouclesCSem.checkint(a.ctx, start, table, errorHandler);
            BouclesCSem.checkint(a.ctx, end, table, errorHandler);
        }

        StepOneRegion();
        a.start.accept(this);
        a.startValue.accept(this);
        a.endValue.accept(this);
        String s = a.block.accept(this);

        region = temp;

        return "for:" + s;
    }

    @Override
    public String visit(Definition a) {
        int temp = region;
        StepOneRegion();
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
        a.type.accept(this);

        // Check if identifier is a reserved word
        if (idf.equals("array") || idf.equals("record")) {
            errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        }

        return null;
    }

    @Override
    public String visit(DeclarationTypeClassique a) {
        String type = a.id.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);

        // Check that type exists
        if (table.getType(type) == null) {
            errorHandler.error(a.ctx, "Type '" + type + "' is not defined");
        }
        return type;
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

            // Check for existence of the field
            if (fields.contains(idf)) {
                errorHandler.error(a.ctx, "Field '" + split[0] + "' is redefined in record");
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
        SymbolLookup table = this.table.getNearSymbolLookup(temp);
        StepOneRegion();
        String idf = a.id.accept(this);

        if (!(table.getSymbol(idf) instanceof Function)) {
            return null;
        }

        table = this.table.getSymbolLookup(region);

        // Check if identifier is a reserved word
        if (idf.equals("array") || idf.equals("record")) {
            errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        }

        if (!(table.getSymbol(idf) instanceof Function))
            return null;

        for (Ast ast : a.args) {
            ast.accept(this);
        }

        if (a.has_return)
            a.return_type.accept(this);

        String expr = a.expr.accept(this);
        String[] split = null;

        if (expr != null && expr.contains(":"))
            split = expr.split(":");

        Type t = null;
        if (split != null && table.getSymbol(split[split.length - 1]) != null) {
            t = table.getSymbol(split[split.length - 1]).getType();
        } else {
            t = tipe.inferType(table, a.expr);
        }
        Type ft = table.getSymbol(idf).getType();

        if (ft == null || t == null
                || (!idf.equals("print") && !t.equals(ft) && !ft.equals(TypeInferer.inferType(table, "nil"))))
            errorHandler.error(a.ctx, "Type mismatch in function declaration " + idf + " : " + ft + " != " + t);

        region = temp;

        return null;
    }

    @Override
    public String visit(DeclarationValeur a) {
        String idf = a.id.accept(this);
        Type t = null;
        if (a.getType() != null) {
            t = tipe.inferType(table, a.getType());
        }
        String expr = a.expr.accept(this);

        // Check if identifier is a reserved word
        if (idf.equals("array") || idf.equals("record")) {
            errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        }

        SymbolLookup table = this.table.getSymbolLookup(region);

        // if (type == null) {
        // errorHandler.error(a.ctx, "Expression '"+type+"' does not evaluate to any
        // type");
        // return null;
        // }

        // if (t == null) {
        // return null;
        // }
        Type tExpr = tipe.inferType(table, a.expr);

        if (t != null && !t.equals(tExpr) && !tExpr.equals  (TypeInferer.inferType(table, "nil"))) {
            errorHandler.error(a.ctx, "Type mismatch in variable declaration " + idf + " : " + t + " != " + tExpr);
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
        // if (parent.getParent() instanceof OperationBoucleContext) {
        // return null;
        // }
        // parent = parent.getParent();
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
            return fidf;
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
            expr.get(i).accept(this);

            // Check for existence of the field
            if (r.getField(fieldname) == null) {
                errorHandler.error(a.ctx, "Field '" + fieldname + "' not defined in type '" + fidf + "'");
                continue;
            }
            // Check for type of the field
            Type fieldtype = r.getField(fieldname).getType();
            Type expType = tipe.inferType(table, expr.get(i));
            if (expType == null || !fieldtype.equals(expType)) {
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
        String[] split = null;

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type t = table.getType(idf);
        if (!(t instanceof Array)) {
            errorHandler.error(a.ctx, "Type '" + idf + "' is not an array type");
        }

        if (size.contains(":")) {
            split = size.split(":");
        }

        // Check if size is an integer
        if ((split != null && (split[0].equals("for") || split[0].equals("while")))
                || !tipe.inferType(table, a.getSize()).equals(TypeInferer.inferType(table, "int"))) {
            errorHandler.error(a.ctx, "Size of array must be an integer");
        } else {
            // Check that size is positive
            if (!tipe.inferType(table, a.getSize()).equals(TypeInferer.inferType(table, "int"))
                    && Integer.parseInt(size) <= 0) {
                errorHandler.error(a.ctx, "Size of array must be positive");
            }
        }

        if (expr.contains(":"))
            split = expr.split(":");

        if (t instanceof Array) {
            // Check that expression is of the right type
            Type exprType = tipe.inferType(table, a.getExpr());
            Type arrayType = ((Array) t).getType();
            if (exprType == null || !exprType.equals(arrayType)) {
                errorHandler.error(a.ctx, "Type mismatch in array declaration : " + arrayType + " != " + exprType);
            }
        }

        return null;
    }

    @Override
    public String visit(ListeAcces a) {
        String idf = a.getId().accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Symbol s = table.getSymbol(idf);
        if (s == null) {
            errorHandler.error(a.ctx, "Variable '" + idf + "' not defined");
            return null;
        }
        Type t = s.getType();
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
                    if (!tipe.inferType(table, ast).equals(new Primitive(Integer.class))) {
                        errorHandler.error(a.ctx, "Index of array must be an integer");
                        break;
                    } else {
                        if (TypeInferer.isNumeric(field) && Integer.parseInt(field) <= 0) {
                            errorHandler.error(a.ctx, "Index of array must be positive");
                            break;
                        } else {
                            if (TypeInferer.isNumeric(field) && Integer.parseInt(field) > ((Array) t).getOffset()) {
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
