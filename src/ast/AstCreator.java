/* ************************************************************************** */
/*                                                                            */
/*                                                                            */
/*   AstCreator.java                                                          */
/*                                                                            */
/*   By: Thibault Cheneviere <thibault.cheneviere@telecomnancy.eu>            */
/*                                                                            */
/*   Created: 2022/11/30 17:11:04 by Golem Géant,                             */
/*   Updated: 2023/01/03 00:40:30 by Bo gosse                                 */
/*                                                                            */
/* ************************************************************************** */

package ast;

import java.util.ArrayList;

import parser.exprBaseVisitor;
import parser.exprParser;
import parser.exprParser.AppelFonctionContext;
import parser.exprParser.DeclarationArrayTypeContext;
import parser.exprParser.DeclarationChampContext;
import parser.exprParser.DeclarationRecordTypeContext;
import parser.exprParser.DeclarationTypeClassiqueContext;
import parser.exprParser.DeclarationTypeContext;
import parser.exprParser.DeclarationValeurContext;
import parser.exprParser.DefinitionContext;
import parser.exprParser.EntierContext;
import parser.exprParser.ExpressionIdentifiantContext;
import parser.exprParser.ExpressionUnaireContext;
import parser.exprParser.IdentifiantContext;
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
import parser.exprParser.DeclarationFonctionContext;
import parser.exprParser.ChaineChrContext;
import parser.exprParser.BreakContext;
import parser.exprParser.InstanciationTypeContext;
import st.*;
import st.Record;

public class AstCreator extends exprBaseVisitor<Ast> {
    private SymbolLookup table;
    private int region;
    private String idf;

    public AstCreator(SymbolLookup table) {
        this.table = table;
        region = 0;
    }

    @Override
    public Ast visitProgram(exprParser.ProgramContext ctx) {
        Ast child = ctx.getChild(0).accept(this);
        return new Program(child);
    }

    @Override
    public Ast visitExpression(exprParser.ExpressionContext ctx) {
        Ast noeudTemporaire = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            noeudTemporaire = new Expression(noeudTemporaire, ctx.getChild(2 * i + 2).accept(this));
        }

        return noeudTemporaire;
    }


    @Override
    public Ast visitInstanciationType(exprParser.InstanciationTypeContext ctx){
        InstanciationType ist = new InstanciationType();

        int temp = region;
        SymbolLookup table = this.table.getSymbolLookup(region);
        table.addChildren();
        region++;

        boolean endOfArgs = false;
        int count = 1;
        String txt = ctx.getChild(count).getText();
        if (txt.equals("}")) {
            endOfArgs = true;
        }
        while (!endOfArgs) {
            Ast id = ctx.getChild(count).accept(this);
            ist.addID(id);
            count+=2;

            Ast expr = ctx.getChild(count).accept(this);
            ist.addExpr(expr);
            count++;

            txt = ctx.getChild(count).getText();
            if (txt.equals(",")) {
                count++;
            } else{
                endOfArgs = true;
                count++;
            }

        }
        return ist;

    }

    @Override
    public Ast visitOperationOu(OperationOuContext ctx) {
        Ast noeudTemporaire = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            noeudTemporaire = new Ou(noeudTemporaire, ctx.getChild(2 * i + 2).accept(this));
        }

        return noeudTemporaire;
    }

    @Override
    public Ast visitOperationEt(OperationEtContext ctx) {
        Ast noeudTemporaire = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            noeudTemporaire = new Et(noeudTemporaire, ctx.getChild(2 * i + 2).accept(this));
        }

        return noeudTemporaire;
    }

    @Override
    public Ast visitOperationComparaison(OperationComparaisonContext ctx) {
        Ast noeudTemporaire = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            noeudTemporaire = new Compar(noeudTemporaire, ctx.getChild(2 * i + 2).accept(this),
                    ctx.getChild(2 * i + 1).getText());
        }

        return noeudTemporaire;
    }

    @Override
    public Ast visitOperationAddition(OperationAdditionContext ctx) {
        Ast noeudTemporaire = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            String operation = ctx.getChild(2 * i + 1).getText();
            Ast right = ctx.getChild(2 * i + 2).accept(this);

            switch (operation) {
                case "+":
                    noeudTemporaire = new Addition(noeudTemporaire, right);
                    break;
                case "-":
                    noeudTemporaire = new Soustraction(noeudTemporaire, right);
                    break;
                default:
                    break;
            }
        }

        return noeudTemporaire;
    }

    @Override
    public Ast visitOperationMultiplication(OperationMultiplicationContext ctx) {
        Ast noeudTemporaire = ctx.getChild(0).accept(this);

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            String operation = ctx.getChild(2 * i + 1).getText();
            Ast right = ctx.getChild(2 * i + 2).accept(this);

            switch (operation) {
                case "*":
                    noeudTemporaire = new Multiplication(noeudTemporaire, right);
                    break;
                case "/":
                    noeudTemporaire = new Division(noeudTemporaire, right);
                    break;
                default:
                    break;
            }
        }

        return noeudTemporaire;
    }

    @Override
    public Ast visitExpressionUnaire(ExpressionUnaireContext ctx) {
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Ast visitSequence(SequenceContext ctx) {
        Sequence sequence = new Sequence();

        for (int i = 0; 2 * i < ctx.getChildCount() - 1; i++) {
            sequence.addSeq(ctx.getChild(2 * i + 1).accept(this));
        }

        return sequence;
    }

    @Override
    public Ast visitNegation(NegationContext ctx) {
        return new Negation(ctx.getChild(1).accept(this));
    }

    @Override
    public Ast visitIdentifiant(IdentifiantContext ctx) {
        return new ID(ctx.getChild(0).getText());
    }

    @Override
    public Ast visitEntier(EntierContext ctx) {
        return new Int(Integer.parseInt(ctx.getChild(0).getText()));
    }

    @Override
    public Ast visitChaineChr(ChaineChrContext ctx) {
        return new ChaineChr(ctx.getChild(0).getText());
    }

    @Override
    public Ast visitNil(NilContext ctx) {
        return new Nil();
    }

    @Override
    public Ast visitBreak(BreakContext ctx) {
        return new Break();
    }

    @Override
    public Ast visitAppelFonction(AppelFonctionContext ctx) {
        ArgFonction argFonction = new ArgFonction();
        // args are located at even indexes
        for (int i = 0; 2 * i + 1 < ctx.getChildCount() - 1; i++) {
            argFonction.addArg(ctx.getChild(2 * i + 1).accept(this));
        }
        AppelFonction af = new AppelFonction(argFonction);
        return af;
    }

    @Override
    public Ast visitExpressionIdentifiant(ExpressionIdentifiantContext ctx) {
        // right child needs to know this child 0
        Ast child = ctx.getChild(0).accept(this);
        if (ctx.getChildCount() > 1) {
            Ast branch = ctx.getChild(1).accept(this);
            if (branch instanceof AppelFonction) {
                ((AppelFonction) branch).setId(child);
                return branch;
            }
            else if (branch instanceof InstanciationType) {
                ((InstanciationType) branch).setId(child);
                return branch;
            }
        }
        return child;
    }

    @Override
    public Ast visitDeclarationType(DeclarationTypeContext ctx) {
        DeclarationType dt = new DeclarationType();

        idf = ctx.getChild(1).getText();

        dt.setId(ctx.getChild(1).accept(this));
        dt.setType(ctx.getChild(3).accept(this));
        return dt;
    }

    @Override
    public Ast visitDeclarationTypeClassique(DeclarationTypeClassiqueContext ctx) {
        DeclarationTypeClassique dtc = new DeclarationTypeClassique();
        SymbolLookup table = this.table.getSymbolLookup(region);
        table.addType(idf, TypeInferer.inferType(table, ctx.getChild(0).getText()));
        dtc.setId(ctx.getChild(0).accept(this));
        return dtc;
    }

    @Override
    public Ast visitDeclarationArrayType(DeclarationArrayTypeContext ctx) {
        DeclarationArrayType dat = new DeclarationArrayType();

        SymbolLookup table = this.table.getSymbolLookup(region);
        table.addType(idf, new Array(TypeInferer.inferType(table, ctx.getChild(2).getText())));

        dat.setId(ctx.getChild(2).accept(this));
        return dat;
    }

    @Override
    public Ast visitDeclarationRecordType(DeclarationRecordTypeContext ctx) {
        DeclarationRecordType drt = new DeclarationRecordType();
        Record rec = new Record();
        SymbolLookup table = this.table.getSymbolLookup(region);
        for (int i = 0; 2 * i + 1 < ctx.getChildCount() - 1; i++) {
            String[] split = ctx.getChild(2 * i + 1).getText().split(":");
            Variable v = new Variable(split[0], TypeInferer.inferType(table, split[1]));
            rec.addField(v);
            drt.addChamp(ctx.getChild(2 * i + 1).accept(this));
        }
        table.addType(idf, rec);
        return drt;
    }

    @Override
    public Ast visitDeclarationChamp(DeclarationChampContext ctx) {
        return new DeclarationChamp(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this));
    }

    @Override
    public Ast visitDeclarationValeur(DeclarationValeurContext ctx) {
        DeclarationValeur dv = new DeclarationValeur();
        dv.setId(ctx.getChild(1).accept(this));

        SymbolLookup table = this.table.getSymbolLookup(region);
        String idf = ctx.getChild(1).getText();
        String expr = ctx.getChild(3).getText();

        if (ctx.getChild(2).getText().equals(":")) {
            table.addSymbolVarAndFunc(new Variable(idf, TypeInferer.inferType(table, expr)));
            dv.setType(ctx.getChild(3).accept(this));
            dv.setExpr(ctx.getChild(5).accept(this));
        } else {
            // TODO: System.out.println("expr = " + expr);
            table.addSymbolVarAndFunc(new Variable(idf, TypeInferer.inferType(table, expr)));
            dv.setExpr(ctx.getChild(3).accept(this));
        }
        return dv;

    }

    @Override
    public Ast visitDeclarationFonction(DeclarationFonctionContext ctx) {
        DeclarationFonction drf = new DeclarationFonction();

        int temp = region;
        SymbolLookup table = this.table.getSymbolLookup(region);
        table.addChildren();
        ArrayList<Variable> params = new ArrayList<>();
        region++;

        drf.setId(ctx.getChild(1).accept(this));
        String idf = ctx.getChild(1).getText();

        boolean endOfArgs = false;
        int count = 3;
        while (!endOfArgs) {
            Ast branch = ctx.getChild(count).accept(this);
            String txt = ctx.getChild(count).getText();
            if (txt.equals(",")) {
                count++;
            } else if (txt.equals(")")) {
                endOfArgs = true;
                count++;
            } else {
                String[] split = txt.split(":");
                params.add(new Variable(split[0], TypeInferer.inferType(table, split[1])));
                drf.addArg(((DeclarationChamp) branch));
                count++;
            }
        }

        Ast branch = ctx.getChild(count).accept(this);
        String txt = ctx.getChild(count).getText();
        Type type = new Primitive(Void.class);

        if (txt.equals(":")) {
            count++;
            type = TypeInferer.inferType(table, ctx.getChild(count).getText());
            branch = ctx.getChild(count).accept(this);
            drf.setReturn(((ID) branch));
            count++;
        }
        count++;
        branch = ctx.getChild(count).accept(this);
        drf.setExpr(branch);

        Function f = new Function(idf, type);
        table.addSymbolVarAndFunc(f);
        f.addParams(params);

        // Get out of the lookupTable
        region = temp;

        return drf;

    }

    @Override
    public Ast visitSiAlors(SiAlorsContext ctx) {
        int temp = region;
        table.getSymbolLookup(region).addChildren();
        region++;

        Ast condition = ctx.getChild(1).accept(this);
        Ast thenBlock = ctx.getChild(3).accept(this);

        if (condition instanceof Sequence) {
            ((Sequence) condition).setNom("Condition");
        } else {
            Sequence seq = new Sequence();
            seq.addSeq(condition);
            condition = seq;
        }

        if (thenBlock instanceof Sequence) {
            ((Sequence) thenBlock).setNom("Then");
        } else {
            Sequence seq = new Sequence();
            seq.addSeq(thenBlock);
            thenBlock = seq;
        }

        region = temp;

        return new IfThen(condition, thenBlock);
    }

    @Override
    public Ast visitSiAlorsSinon(SiAlorsSinonContext ctx) {
        int temp = region;
        table.getSymbolLookup(region).addChildren();
        region++;

        Ast condition = ctx.getChild(1).accept(this);
        Ast thenBlock = ctx.getChild(3).accept(this);

        table.getSymbolLookup(temp).addChildren();
        Ast elseBlock = ctx.getChild(5).accept(this);

        if (condition instanceof Sequence) {
            ((Sequence) condition).setNom("Condition");
        } else {
            Sequence seq = new Sequence();
            seq.addSeq(condition);
            condition = seq;
        }

        if (thenBlock instanceof Sequence) {
            ((Sequence) thenBlock).setNom("Then");
        } else {
            Sequence seq = new Sequence();
            seq.addSeq(thenBlock);
            thenBlock = seq;
        }

        if (elseBlock instanceof Sequence) {
            ((Sequence) elseBlock).setNom("Else");
        } else {
            Sequence seq = new Sequence();
            seq.addSeq(elseBlock);
            elseBlock = seq;
        }

        region = temp;

        return new IfThenElse(condition, thenBlock, elseBlock);
    }

    @Override
    public Ast visitTantQue(TantQueContext ctx) {
        int temp = region;
        table.getSymbolLookup(region).addChildren();
        region++;

        Ast condition = ctx.getChild(1).accept(this);
        Ast block = ctx.getChild(3).accept(this);

        region = temp;

        return new While(condition, block);
    }

    @Override
    public Ast visitPour(PourContext ctx) {
        int temp = region;
        table.getSymbolLookup(region).addChildren();
        region++;

        Ast init = ctx.getChild(1).accept(this);
        Ast condition = ctx.getChild(3).accept(this);
        Ast increment = ctx.getChild(5).accept(this);
        Ast block = ctx.getChild(7).accept(this);

        region = temp;

        return new For(init, condition, increment, block);
    }

    @Override
    public Ast visitDefinition(DefinitionContext ctx) {
        boolean sawIn = false;

        Definition def = new Definition();

        int temp = region;
        table.getSymbolLookup(region).addChildren();
        region++;

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i).getText().equals("in")) {
                sawIn = true;
                continue;
            }
            if (ctx.getChild(i).getText().equals("let") || ctx.getChild(i).getText().equals("end")
                    || ctx.getChild(i).getText().equals(";")) {
                continue;
            }
            if (sawIn) {
                def.addExpr(ctx.getChild(i).accept(this));
            } else {
                def.addDeclaration(ctx.getChild(i).accept(this));
            }
        }
        region = temp;
        return def;
    }

}
