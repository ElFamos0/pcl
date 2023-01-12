package csem;

import java.util.ArrayList;

import ast.*;
import sl.SymbolLookup;

public class CSemVisitor implements AstVisitor<String> {
    private SymbolLookup table;
    private int region;

    public CSemVisitor(SymbolLookup table) {
        this.table = table;
        region = 0;
    }

    @Override
    public String visit(Program a) {
        a.expression.accept(this);
        return null;
    }

    @Override
    public String visit(Expression a) {
        a.left.accept(this);
        a.right.accept(this);
        return null;
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
        a.left.accept(this);
        a.right.accept(this);

        return null;
    }

    @Override
    public String visit(Soustraction a) {
        a.left.accept(this);
        a.right.accept(this);

        return null;
    }

    @Override
    public String visit(Multiplication a) {
        a.left.accept(this);
        a.right.accept(this);

        return null;
    }

    @Override
    public String visit(Division a) {
        a.left.accept(this);
        a.right.accept(this);

        return null;
    }

    @Override
    public String visit(Sequence a) {
        for (Ast ast : a.seqs) {
            ast.accept(this);
        }

        return a.nom;
    }

    @Override
    public String visit(Negation a) {
        return a.expression.accept(this);
    }

    @Override
    public String visit(ID a) {
        String idf = a.nom;

        return idf;
    }

    @Override
    public String visit(Int a) {
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
        String idf = a.id.accept(this);
        for (Ast ast : a.args) {
            String field = ast.accept(this);
            String[] split = field.split(":");
            FuncCSem.checkArgs(split[0], split[1], table.getSymbolLookup(region));
        }
        if (a.has_return)
            a.return_type.accept(this);
        a.expr.accept(this);

        FuncCSem.checkFuncFromLib(idf);

        region = temp;

        return null;
    }

    @Override
    public String visit(DeclarationValeur a) {
        a.id.accept(this);
        if (a.getType() != null)
            a.getType().accept(this);
        a.expr.accept(this);

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
