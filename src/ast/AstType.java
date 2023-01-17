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

public class AstType extends exprBaseVisitor<Type> {
    private SymbolLookup table;

    public AstType(SymbolLookup table) {
        this.table = table;
    }

    public void setTable(SymbolLookup table) {
        this.table = table;
    }

    @Override
    public Type visitProgram(exprParser.ProgramContext ctx) {
        return null;
    }

    @Override
    public Type visitExpression(exprParser.ExpressionContext ctx) {
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return null;
            }
        }

        return t1;
    }

    @Override
    public Type visitInstanciationType(exprParser.InstanciationTypeContext ctx) {
        return null;
    }

    @Override
    public Type visitOperationOu(OperationOuContext ctx) {
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return null;
            }
        }

        return t1;
    }

    @Override
    public Type visitOperationEt(OperationEtContext ctx) {
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return null;
            }
        }

        return t1;
    }

    @Override
    public Type visitOperationComparaison(OperationComparaisonContext ctx) {
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return null;
            }
        }

        return t1;
    }

    @Override
    public Type visitOperationAddition(OperationAdditionContext ctx) {
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return null;
            }
        }

        return t1;
    }

    @Override
    public Type visitOperationMultiplication(OperationMultiplicationContext ctx) {
        Type t1 = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            Type t2 = ctx.getChild(2 * i + 2).accept(this);
            if (t1 == null || t2 == null || !t1.equals(t2)) {
                return null;
            }
        }

        return t1;
    }

    @Override
    public Type visitExpressionUnaire(ExpressionUnaireContext ctx) {
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Type visitSequence(SequenceContext ctx) {
        return ctx.getChild(ctx.getChildCount() - 1).accept(this);
    }

    @Override
    public Type visitNegation(NegationContext ctx) {
        return ctx.accept(this);
    }

    @Override
    public Type visitIdentifiant(IdentifiantContext ctx) {
        return table.getSymbol(ctx.getText()) == null ? table.getType(ctx.getText())
                : table.getSymbol(ctx.getText()).getType();
    }

    @Override
    public Type visitEntier(EntierContext ctx) {
        return new Primitive(Integer.class);
    }

    @Override
    public Type visitChaineChr(ChaineChrContext ctx) {
        return new Array(new Primitive(Character.class));
    }

    @Override
    public Type visitNil(NilContext ctx) {
        return null;
    }

    @Override
    public Type visitBreak(BreakContext ctx) {
        return null;
    }

    @Override
    public Type visitAppelFonction(AppelFonctionContext ctx) {
        return null;
    }

    @Override
    public Type visitExpressionIdentifiant(ExpressionIdentifiantContext ctx) {
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Type visitListeAcces(ListeAccesContext ctx) {
        return null;
    }

    @Override
    public Type visitDeclarationType(DeclarationTypeContext ctx) {
        return null;
    }

    @Override
    public Type visitDeclarationTypeClassique(DeclarationTypeClassiqueContext ctx) {
        return null;
    }

    @Override
    public Type visitDeclarationArrayType(DeclarationArrayTypeContext ctx) {
        return null;
    }

    @Override
    public Type visitDeclarationRecordType(DeclarationRecordTypeContext ctx) {
        return null;
    }

    @Override
    public Type visitDeclarationChamp(DeclarationChampContext ctx) {
        return ctx.getChild(2).accept(this);
    }

    @Override
    public Type visitDeclarationValeur(DeclarationValeurContext ctx) {
        System.out.println("Declaration valeur");
        return null;
    }

    @Override
    public Type visitDeclarationFonction(DeclarationFonctionContext ctx) {
        System.out.println("Declaration fonction");
        return null;
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
            return null;
        }

        return t1;
    }

    @Override
    public Type visitTantQue(TantQueContext ctx) {
        return new Primitive(Void.class);
    }

    @Override
    public Type visitPour(PourContext ctx) {
        return new Primitive(Void.class);
    }

    @Override
    public Type visitDefinition(DefinitionContext ctx) {
        for (int i = ctx.getChildCount() - 1; i > -1; i--) {
            if (ctx.getChild(i).getText().equals("let") || ctx.getChild(i).getText().equals("end")
                    || ctx.getChild(i).getText().equals(";")) {
                continue;
            }

            return ctx.getChild(i).accept(this);
        }

        return null;
    }
}
