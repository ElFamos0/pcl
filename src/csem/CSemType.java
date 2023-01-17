package csem;

import ast.AccesChamp;
import ast.Addition;
import ast.AppelFonction;
import ast.ArgFonction;
import ast.Ast;
import ast.AstVisitor;
import ast.Break;
import ast.ChaineChr;
import ast.Compar;
import ast.DeclarationArrayType;
import ast.DeclarationChamp;
import ast.DeclarationFonction;
import ast.DeclarationRecordType;
import ast.DeclarationType;
import ast.DeclarationTypeClassique;
import ast.DeclarationValeur;
import ast.Definition;
import ast.Division;
import ast.Et;
import ast.Expression;
import ast.ExpressionArray;
import ast.ExpressionIdentifiant;
import ast.For;
import ast.ID;
import ast.IfThen;
import ast.IfThenElse;
import ast.InstanciationType;
import ast.Int;
import ast.ListeAcces;
import ast.Multiplication;
import ast.Negation;
import ast.Nil;
import ast.Ou;
import ast.Program;
import ast.Sequence;
import ast.Soustraction;
import ast.While;
import sl.Array;
import sl.Primitive;
import sl.SymbolLookup;
import sl.Type;

public class CSemType implements AstVisitor<Type> {
    private SymbolLookup table;
    private int region;

    public CSemType(SymbolLookup table) {
        this.table = table;
        this.region = 0;
    }

    public void setTable(SymbolLookup table) {
        this.table = table;
        region = table.getRegion();
    }

    @Override
    public Type visit(Program a) {
        return a.expression.accept(this);
    }

    @Override
    public Type visit(Expression a) {
        // System.out.println("Expression");
        if (a.right == null) {
            return a.left.accept(this);
        }

        return new Primitive(Void.class);
    }

    @Override
    public Type visit(Ou a) {
        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Et a) {
        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Compar a) {
        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Addition a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return null;
        }

        return t1;
    }

    @Override
    public Type visit(Soustraction a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return null;
        }

        return t1;
    }

    @Override
    public Type visit(Multiplication a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return null;
        }

        return t1;
    }

    @Override
    public Type visit(Division a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return null;
        }

        return t1;
    }

    @Override
    public Type visit(Sequence a) {
        Ast ast = a.seqs.get(a.seqs.size() - 1);

        return ast.accept(this);
    }

    @Override
    public Type visit(Negation a) {
        return a.expression.accept(this);
    }

    @Override
    public Type visit(ID a) {
        SymbolLookup table = this.table.getSymbolLookup(region);
        return table.getSymbol(a.nom) == null ? table.getType(a.nom) : table.getSymbol(a.nom).getType();
    }

    @Override
    public Type visit(Int a) {
        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(ExpressionIdentifiant a) {
        // System.out.println("ExpressionIdentifiant");
        return null;
    }

    @Override
    public Type visit(AppelFonction a) {
        return a.id.accept(this);
    }

    @Override
    public Type visit(ArgFonction a) {
        return null;
    }

    @Override
    public Type visit(IfThenElse a) {
        region++;
        Type t1 = a.thenBlock.accept(this);
        region++;
        Type t2 = a.elseBlock.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return null;
        }

        return t1;
    }

    @Override
    public Type visit(IfThen a) {
        return a.thenBlock.accept(this);
    }

    @Override
    public Type visit(While a) {
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(For a) {
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(Definition a) {
        // System.out.println("Definition");
        region++;
        return a.exprs.get(a.exprs.size() - 1).accept(this);
    }

    @Override
    public Type visit(DeclarationType a) {
        // System.out.println("DeclarationType");
        return a.id.accept(this);
    }

    @Override
    public Type visit(DeclarationTypeClassique a) {
        // System.out.println("DeclarationTypeClassique");
        return null;
    }

    @Override
    public Type visit(DeclarationArrayType a) {
        // System.out.println("DeclarationArrayType");
        return null;
    }

    @Override
    public Type visit(DeclarationRecordType a) {
        // System.out.println("DeclarationRecordType");
        return null;
    }

    @Override
    public Type visit(DeclarationChamp a) {
        // System.out.println("DeclarationChamp");
        return null;
    }

    @Override
    public Type visit(DeclarationFonction a) {
        return null;
    }

    @Override
    public Type visit(DeclarationValeur a) {
        // System.out.println("DeclarationValeur");
        return null;
    }

    @Override
    public Type visit(ChaineChr a) {
        return new Array(new Primitive(Character.class));
    }

    @Override
    public Type visit(Nil a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type visit(Break a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Type visit(InstanciationType a) {
        // System.out.println("InstanciationType");
        return null;
    }

    @Override
    public Type visit(ListeAcces a) {
        // System.out.println("ListeAcces");

        Type t = a.id.accept(this);

        if (t instanceof Array) {
            Array arr = (Array) t;
            for (int i = 0; i < a.accesChamps.size(); i++) {
                if (i == a.accesChamps.size() - 1)
                    return arr.getType();
                arr = (Array) arr.getType();
            }
        }

        return new Primitive(Void.class);
    }

    @Override
    public Type visit(ExpressionArray a) {
        return a.getId().accept(this);
    }

    @Override
    public Type visit(AccesChamp a) {
        return a.getChild().accept(this);
    }

}
