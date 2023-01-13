package csem;

import java.util.ArrayList;

import ast.*;
import sl.Function;
import sl.SymbolLookup;
import sl.Type;
import sl.TypeInferer;

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
        a.left.accept(this);
        a.right.accept(this);

        return null;
    }

    @Override
    public String visit(Addition a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table);

        System.out.println("Addition: " + left + " + " + right);

        return left + ":" + right;
    }

    @Override
    public String visit(Soustraction a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table);

        return left + ":" + right;
    }

    @Override
    public String visit(Multiplication a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table);

        return left + ":" + right;
    }

    @Override
    public String visit(Division a) {
        String left = a.left.accept(this);
        String right = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a.ctx, left, right, table);

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
        OpCSem.checkint(a.ctx, left, right, table);
        return a.expression.accept(this);
    }

    @Override
    public String visit(ID a) {
        String idf = a.nom;

        return idf;
    }

    @Override
    public String visit(Int a) {
        SymbolLookup table = this.table.getSymbolLookup(region);
        OpCSem.checkint(a, table);

        String val = String.valueOf(a.valeur);

        return val;
    }

    @Override
    public String visit(ExpressionIdentifiant a) {
        a.left.accept(this);
        if (a.right != null)
            a.right.accept(this);

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
        a.condition.accept(this);
        a.thenBlock.accept(this);
        a.elseBlock.accept(this);

        region = temp;

        return null;
    }

    @Override
    public String visit(IfThen a) {
        int temp = region;
        region++;
        a.condition.accept(this);
        a.thenBlock.accept(this);

        region = temp;

        return null;
    }

    @Override
    public String visit(While a) {
        int temp = region;
        region++;
        a.condition.accept(this);
        a.block.accept(this);

        region = temp;

        return null;
    }

    @Override
    public String visit(For a) {
        int temp = region;
        region++;
        a.start.accept(this);
        a.startValue.accept(this);
        a.endValue.accept(this);
        a.block.accept(this);

        region = temp;

        return "";
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

        return null;
    }

    @Override
    public String visit(DeclarationType a) {
        a.id.accept(this);
        a.type.accept(this);

        return null;
    }

    @Override
    public String visit(DeclarationTypeClassique a) {
        return a.id.accept(this);
    }

    @Override
    public String visit(DeclarationArrayType a) {
        return a.id.accept(this);
    }

    @Override
    public String visit(DeclarationRecordType a) {
        for (Ast ast : a.champs)
            ast.accept(this);

        return null;
    }

    @Override
    public String visit(DeclarationChamp a) {
        String idf = a.id.accept(this);
        String type = a.type.accept(this);

        return idf + ":" + type;
    }

    @Override
    public String visit(DeclarationFonction a) {
        int temp = region;
        region++;
        SymbolLookup table = this.table.getSymbolLookup(temp);
        String idf = a.id.accept(this);
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

        if (!t.equals(ft))
            errorHandler.error(a.ctx, "Type mismatch in function declaration " + idf + " : " + ft + " != " + t);

        String err = FuncCSem.checkFuncFromLib(idf);

        if (err != null)
            errorHandler.error(a.ctx, err);

        if (table.getParent().isMultiDec(idf))
            errorHandler.error(a.ctx, "Multiple declaration of " + idf);

        region = temp;

        return null;
    }

    @Override
    public String visit(DeclarationValeur a) {
        String idf = a.id.accept(this);
        String type = null;
        if (a.getType() != null)
            type = a.getType().accept(this);
        String expr = a.expr.accept(this);
        String[] split = null;

        if (expr != null && expr.contains(":")) {
            split = expr.split(":");
        }

        SymbolLookup table = this.table.getSymbolLookup(region);

        if (split != null) {
            if (type != null && split[0].equals("function")) {
                Type t = table.getSymbol(split[1]).getType();

                if (!t.equals(TypeInferer.inferType(table, type)))
                    errorHandler.error(a.ctx,
                            "Type mismatch in variable declaration " + idf + " : " + type + " != " + t);
            }
        }

        if (table.isMultiDec(idf))
            errorHandler.error(a.ctx, "Multiple declaration of " + idf);

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
        // Nothing to do

        return null;
    }

    @Override
    public String visit(InstanciationType a) {
        a.getId().accept(this);
        ArrayList<Ast> idf = a.getIdentifiants();
        ArrayList<Ast> expr = a.getExpressions();
        for (int i = 0; i < idf.size(); i++) {
            idf.get(i).accept(this);
            expr.get(i).accept(this);
        }

        return null;
    }

    @Override
    public String visit(AccesChamp a) {
        a.getChild().accept(this);

        return null;
    }

    @Override
    public String visit(ExpressionArray a) {
        a.getId().accept(this);
        a.getSize().accept(this);
        a.getExpr().accept(this);

        return null;
    }

    @Override
    public String visit(ListeAcces a) {
        a.getId().accept(this);
        if (a.getisExpressionArray())
            a.getExpressionArray().accept(this);
        else {
            for (Ast ast : a.getAccesChamps())
                ast.accept(this);
        }

        return null;
    }

}
