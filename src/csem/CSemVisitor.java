package csem;

import java.util.ArrayList;

import ast.*;
import sl.SymbolLookup;
import sl.Type;
import sl.TypeInferer;

public class CSemVisitor implements AstVisitor<String> {
    private SymbolLookup table;
    private int region;
    private ErrorHandler errorHandler;

    public CSemVisitor(SymbolLookup table) {
        this.table = table;
        region = 0;
        errorHandler = new ErrorHandler();
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

        // If expression is like `idf := expr` we need to check that expr is the same type as idf
        if (right != null) {
            SymbolLookup table = this.table.getSymbolLookup(region);
            Type t = TypeInferer.inferType(table, right);
            if (table.getSymbol(left) != null) {
                Type t2 = table.getSymbol(left).getType();
                if (!t.equals(t2)) {
                    new CSemErrorFormatter().printError(a.ctx, "type mismatch between '" + left + "' and '" + right +"' of type " + t + " and " + t2);
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

        // Check for existence of the identifier
        SymbolLookup table = this.table.getSymbolLookup(region);
        if (table.getSymbol(idf) == null && table.getType(idf) == null) {
            new CSemErrorFormatter().printError(a.ctx, "Unknown identifier");
        }

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

        if (a.right != null) {
            a.right.accept(this);
        }

        return null;
    }

    @Override
    public String visit(AppelFonction a) {
        a.id.accept(this);
        a.args.accept(this);

        return null;
    }

    @Override
    public String visit(ArgFonction a) {
        for (Ast ast : a.args) {
            ast.accept(this);
        }

        return null;
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

        return null;
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
        SymbolLookup table = this.table.getSymbolLookup(region);
        String idf = a.id.accept(this);
        for (Ast ast : a.args) {
            String field = ast.accept(this);
            String[] split = field.split(":");
            FuncCSem.checkArgs(split[0], split[1], table);
        }
        if (a.has_return)
            a.return_type.accept(this);

        Type t = TypeInferer.inferType(table, a.expr.accept(this));
        Type f = table.getSymbol(idf).getType();

        if (!t.equals(f))
            System.out.println("Type mismatch in function " + idf + " : " + t + " != " + f);

        FuncCSem.checkFuncFromLib(idf);

        region = temp;

        return null;
    }

    @Override
    public String visit(DeclarationValeur a) {
        String idf = a.id.accept(this);
        if (a.getType() != null) {
            a.getType().accept(this);
        }
        
        String expr = a.expr.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type t = TypeInferer.inferType(table, expr);
        Type f = table.getSymbol(idf).getType();

        CSemErrorFormatter err = new CSemErrorFormatter();

        // If t is of type void, there is an error
        if (t.equals(new Primitive(Void.class))) {
            err.printError(a.ctx, "The return value for the declaration of '" + idf + "' is void");
            return null;
        }

        // If they don't match, there is an error
        if (!t.equals(f)) {
            err.printError(a.ctx, "Type mismatch in declaration '" + idf + "' : " + t + " != " + f);
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
