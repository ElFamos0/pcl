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
import sl.Record;

public class CSemType implements AstVisitor<Type> {
    private SymbolLookup table;
    private int region;
    private int biggestRegion;

    public CSemType(SymbolLookup table) {
        this.table = table;
    }

    public void setTable(SymbolLookup table) {
        this.table = table;
        region = table.getRegion();
        biggestRegion = region;
    }

    public void StepOneRegion() {
        biggestRegion++;
        region = biggestRegion;
    }

    public void StepDownRegion() {
        region--;
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
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(new Primitive(Integer.class))
                || !t2.equals(new Primitive(Integer.class))) {
            return new Primitive(Void.class);
        }

        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Et a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(new Primitive(Integer.class))
                || !t2.equals(new Primitive(Integer.class))) {
            return new Primitive(Void.class);
        }

        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Compar a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return new Primitive(Void.class);
        }

        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Addition a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return new Primitive(Void.class);
        }

        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Soustraction a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return new Primitive(Void.class);
        }

        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Multiplication a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return new Primitive(Void.class);
        }

        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(Division a) {
        Type t1 = a.left.accept(this);
        Type t2 = a.right.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return new Primitive(Void.class);
        }

        return new Primitive(Integer.class);
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

        if (table.getSymbol(a.nom) == null) {
            if (table.getType(a.nom) == null) {
                return new Primitive(Void.class);
            }
            return table.getType(a.nom);
        }
        return table.getSymbol(a.nom).getType();
    }

    @Override
    public Type visit(Int a) {
        return new Primitive(Integer.class);
    }

    @Override
    public Type visit(ExpressionIdentifiant a) {
        // System.out.println("ExpressionIdentifiant");
        if (a.right == null) {
            return a.left.accept(this);
        }
        return a.right.accept(this);
    }

    @Override
    public Type visit(AppelFonction a) {
        return a.id.accept(this);
    }

    @Override
    public Type visit(ArgFonction a) {
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(IfThenElse a) {
        int temp = region;

        StepOneRegion();
        Type t1 = a.thenBlock.accept(this);

        StepOneRegion();
        Type t2 = a.elseBlock.accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return new Primitive(Void.class);
        }

        region = temp;

        return t1;
    }

    @Override
    public Type visit(IfThen a) {
        int temp = region;

        StepOneRegion();
        Type t = a.thenBlock.accept(this);

        region = temp;

        return t;
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
        int temp = region;
        StepOneRegion();
        Type t = a.exprs.get(a.exprs.size() - 1).accept(this);

        region = temp;
        return t;
    }

    @Override
    public Type visit(DeclarationType a) {
        // System.out.println("DeclarationType");
        return a.id.accept(this);
    }

    @Override
    public Type visit(DeclarationTypeClassique a) {
        // System.out.println("DeclarationTypeClassique");
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(DeclarationArrayType a) {
        // System.out.println("DeclarationArrayType");
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(DeclarationRecordType a) {
        // System.out.println("DeclarationRecordType");
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(DeclarationChamp a) {
        // System.out.println("DeclarationChamp");
        return a.type.accept(this);
    }

    @Override
    public Type visit(DeclarationFonction a) {
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(DeclarationValeur a) {
        // System.out.println("DeclarationValeur");
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(ChaineChr a) {
        return new Array(new Primitive(Character.class));
    }

    @Override
    public Type visit(Nil a) {
        Record nil = new Record();
        nil.setIsNil(true);
        return nil;
    }

    @Override
    public Type visit(Break a) {
        // System.out.println("Break");
        return new Primitive(Void.class);
    }

    @Override
    public Type visit(InstanciationType a) {
        // System.out.println("InstanciationType");
        return a.id.accept(this);
    }

    @Override
    public Type visit(ListeAcces a) {
        // System.out.println("ListeAcces");

        Type t = a.id.accept(this);

        int i = 0;

        while (i < a.accesChamps.size() - 1) {
            if (t instanceof Record) {
                AccesChamp ac = (AccesChamp) a.accesChamps.get(i);
                Record r = (Record) t;

                if (!(ac.getChild() instanceof ID)) {
                    return new Primitive(Void.class);
                }
                ID id = (ID) ac.getChild();
                t = r.getField(id.nom).getType();
            } else if (t instanceof Array) {
                Array arr = (Array) t;
                t = arr.getType();
            } else {
                return new Primitive(Void.class);
            }
            i++;
        }

        if (t instanceof Record) {
            AccesChamp ac = (AccesChamp) a.accesChamps.get(i);
            Record r = (Record) t;

            if (!(ac.getChild() instanceof ID)) {
                return new Primitive(Void.class);
            }
            ID id = (ID) ac.getChild();
            return r.getField(id.nom).getType();
        } else if (t instanceof Array) {
            Array arr = (Array) t;
            return arr.getType();
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
