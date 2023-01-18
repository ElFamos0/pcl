package ast;

import parser.exprBaseVisitor;
import parser.exprParser;
import parser.exprParser.AppelFonctionContext;
import parser.exprParser.BreakContext;
import parser.exprParser.ChaineChrContext;
import parser.exprParser.DeclarationArrayTypeContext;
import parser.exprParser.DeclarationChampContext;
import parser.exprParser.DeclarationFonctionContext;
import parser.exprParser.DeclarationRecordTypeContext;
import parser.exprParser.DeclarationTypeClassiqueContext;
import parser.exprParser.DeclarationTypeContext;
import parser.exprParser.DeclarationValeurContext;
import parser.exprParser.DefinitionContext;
import parser.exprParser.EntierContext;
import parser.exprParser.ExpressionIdentifiantContext;
import parser.exprParser.ExpressionUnaireContext;
import parser.exprParser.IdentifiantContext;
import parser.exprParser.ListeAccesContext;
import parser.exprParser.NegationContext;
import parser.exprParser.NilContext;
import parser.exprParser.OperationAdditionContext;
import parser.exprParser.OperationComparaisonContext;
import parser.exprParser.OperationEtContext;
import parser.exprParser.OperationMultiplicationContext;
import parser.exprParser.OperationOuContext;
import parser.exprParser.PourContext;
import parser.exprParser.SequenceContext;
import parser.exprParser.SiAlorsContext;
import parser.exprParser.SiAlorsSinonContext;
import parser.exprParser.TantQueContext;
import sl.Array;
import sl.Function;
import sl.Primitive;
import sl.Symbol;
import sl.SymbolLookup;
import sl.Type;
import sl.TypeInferer;
import sl.Variable;
import sl.Record;

public class AstType extends exprBaseVisitor<Type> {
    private SymbolLookup table;
    private int region;

    public AstType(SymbolLookup table) {
        this.table = table;
        if (table != null) {
            region = table.getRegion();
        } else {
            region = 0;
        }
    }

    public void setTable(SymbolLookup table) {
        this.table = table;
    }

    @Override
    public Type visitProgram(exprParser.ProgramContext ctx) {
        // System.out.println("Programme");
        return null;
    }

    @Override
    public Type visitExpression(exprParser.ExpressionContext ctx) {
        // System.out.println("Expression");
        if (ctx.getChildCount() == 1) {
            return ctx.getChild(0).accept(this);
        } else {
            return new Primitive(Void.class);
        }
    }

    @Override
    public Type visitInstanciationType(exprParser.InstanciationTypeContext ctx) {
        // System.out.println("InstanciationType");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitOperationOu(OperationOuContext ctx) {
        // System.out.println("OperationOu");
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return new Primitive(Void.class);
            }
        }

        return t1;
    }

    @Override
    public Type visitOperationEt(OperationEtContext ctx) {
        // System.out.println("OperationEt");
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return new Primitive(Void.class);
            }
        }

        return t1;
    }

    @Override
    public Type visitOperationComparaison(OperationComparaisonContext ctx) {
        // System.out.println("OperationComparaison");
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return new Primitive(Void.class);
            }
        }

        return t1;
    }

    @Override
    public Type visitOperationAddition(OperationAdditionContext ctx) {
        // System.out.println("OperationAddition");
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return new Primitive(Void.class);
            }
        }

        return t1;
    }

    @Override
    public Type visitOperationMultiplication(OperationMultiplicationContext ctx) {
        // System.out.println("OperationMultiplication");
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return new Primitive(Void.class);
            }
        }

        return t1;
    }

    @Override
    public Type visitExpressionUnaire(ExpressionUnaireContext ctx) {
        // System.out.println("ExpressionUnaire");
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Type visitSequence(SequenceContext ctx) {
        // System.out.println("Sequence");
        return ctx.getChild(ctx.getChildCount() - 2).accept(this);
    }

    @Override
    public Type visitNegation(NegationContext ctx) {
        // System.out.println("Negation");
        return ctx.accept(this);
    }

    @Override
    public Type visitIdentifiant(IdentifiantContext ctx) {
        // System.out.println("Identifiant");
        SymbolLookup table = this.table;
        region = table.getRegion();
        if (table.getSymbol(ctx.getText()) == null) {
            if (table.getType(ctx.getText()) == null) {
                return new Primitive(Void.class);
            }
            return table.getType(ctx.getText());
        }
        return table.getSymbol(ctx.getText()).getType();
    }

    @Override
    public Type visitEntier(EntierContext ctx) {
        // System.out.println("Entier");
        return new Primitive(Integer.class);
    }

    @Override
    public Type visitChaineChr(ChaineChrContext ctx) {
        // System.out.println("ChaineChr");
        return new Array(new Primitive(Character.class));
    }

    @Override
    public Type visitNil(NilContext ctx) {
        Record nil = new Record();
        nil.setIsNil(true);
        return nil;
    }

    @Override
    public Type visitBreak(BreakContext ctx) {
        // System.out.println("Break");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitAppelFonction(AppelFonctionContext ctx) {
        // System.out.println("Appel fonction");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitExpressionIdentifiant(ExpressionIdentifiantContext ctx) {
        // System.out.println("Expression identifiant");
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Type visitListeAcces(ListeAccesContext ctx) {
        // System.out.println("Liste acces");
        return null;
    }

    @Override
    public Type visitDeclarationType(DeclarationTypeContext ctx) {
        // System.out.println("Declaration type");
        return ctx.getChild(1).accept(this);
    }

    @Override
    public Type visitDeclarationTypeClassique(DeclarationTypeClassiqueContext ctx) {
        // System.out.println("Declaration type classique");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitDeclarationArrayType(DeclarationArrayTypeContext ctx) {
        // System.out.println("Declaration array type");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitDeclarationRecordType(DeclarationRecordTypeContext ctx) {
        // System.out.println("Declaration record type");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitDeclarationChamp(DeclarationChampContext ctx) {
        // System.out.println("Declaration champ");
        return ctx.getChild(2).accept(this);
    }

    @Override
    public Type visitDeclarationValeur(DeclarationValeurContext ctx) {
        // System.out.println("Declaration valeur");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitDeclarationFonction(DeclarationFonctionContext ctx) {
        // System.out.println("Declaration fonction");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitSiAlors(SiAlorsContext ctx) {
        return ctx.getChild(3).accept(this);
    }

    @Override
    public Type visitSiAlorsSinon(SiAlorsSinonContext ctx) {
        Type t1 = ctx.getChild(3).accept(this);
        Type t2 = ctx.getChild(5).accept(this);

        if (t1 == null || t2 == null || !t1.equals(t2)) {
            return new Primitive(Void.class);
        }

        return t1;
    }

    @Override
    public Type visitTantQue(TantQueContext ctx) {
        // System.out.println("TantQue");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitPour(PourContext ctx) {
        // System.out.println("Pour");
        return new Primitive(Void.class);
    }

    @Override
    public Type visitDefinition(DefinitionContext ctx) {
        // System.out.println("Definition");
        for (int i = ctx.getChildCount() - 1; i > -1; i--) {
            if (ctx.getChild(i).getText().equals("let") || ctx.getChild(i).getText().equals("end")
                    || ctx.getChild(i).getText().equals(";")) {
                continue;
            }

            return ctx.getChild(i).accept(this);
        }

        return new Primitive(Void.class);
    }
}
