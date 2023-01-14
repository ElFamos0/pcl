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

import csem.ErrorHandler;
import csem.FuncCSem;
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
import sl.*;
import sl.Record;
import parser.exprParser.DeclarationFonctionContext;
import parser.exprParser.ChaineChrContext;
import parser.exprParser.BreakContext;
import parser.exprParser.ListeAccesContext;
//import st.*;

public class AstCreator extends exprBaseVisitor<Ast> {
    private SymbolLookup table;
    private int region;
    private String idf;
    private ErrorHandler errorHandler;

    public AstCreator(SymbolLookup table, ErrorHandler errorHandler) {
        this.table = table;
        region = 0;
        this.errorHandler = errorHandler;
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
            noeudTemporaire = new Expression(ctx, noeudTemporaire, ctx.getChild(2 * i + 2).accept(this));
        }

        return noeudTemporaire;
    }

    @Override
    public Ast visitInstanciationType(exprParser.InstanciationTypeContext ctx) {
        InstanciationType ist = new InstanciationType(ctx);

        boolean endOfArgs = false;
        int count = 1;
        String txt = ctx.getChild(count).getText();
        if (txt.equals("}")) {
            endOfArgs = true;
        }
        while (!endOfArgs) {
            Ast id = ctx.getChild(count).accept(this);
            ist.addID(id);
            count += 2;

            Ast expr = ctx.getChild(count).accept(this);
            ist.addExpr(expr);
            count++;

            txt = ctx.getChild(count).getText();
            if (txt.equals(",")) {
                count++;
            } else {
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
            noeudTemporaire = new Compar(ctx, noeudTemporaire, ctx.getChild(2 * i + 2).accept(this),
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
                    noeudTemporaire = new Addition(ctx, noeudTemporaire, right);
                    break;
                case "-":
                    noeudTemporaire = new Soustraction(ctx, noeudTemporaire, right);
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
                    noeudTemporaire = new Multiplication(ctx, noeudTemporaire, right);
                    break;
                case "/":
                    noeudTemporaire = new Division(ctx, noeudTemporaire, right);
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
        return new Negation(ctx, ctx.getChild(1).accept(this));
    }

    @Override
    public Ast visitIdentifiant(IdentifiantContext ctx) {
        return new ID(ctx, ctx.getChild(0).getText());
    }

    @Override
    public Ast visitEntier(EntierContext ctx) {
        return new Int(ctx, ctx.getChild(0).getText());
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
        AppelFonction af = new AppelFonction(argFonction, ctx);
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
            } else if (branch instanceof InstanciationType) {
                ((InstanciationType) branch).setId(child);
                return branch;
            } else if (branch instanceof ListeAcces) {
                ((ListeAcces) branch).setId(child);
                if (((ListeAcces) branch).getisExpressionArray()) {
                    ((ListeAcces) branch).getExpressionArray().setId(child);
                    return ((ListeAcces) branch).getExpressionArray();
                } else {
                    return branch;
                }

            }
        }
        return child;
    }

    @Override
    public Ast visitListeAcces(ListeAccesContext ctx) {
        Ast retour;
        String text = ctx.getChild(0).getText();
        int countmax = ctx.getChildCount();
        int count = 1;
        if (text.equals(".")) {
            ListeAcces listeacces = new ListeAcces();
            listeacces.setisExpressionArray(false);
            Ast champ = ctx.getChild(count).accept(this);
            AccesChamp acceschamp = new AccesChamp();
            acceschamp.setChild(champ);
            acceschamp.setisArrayAccess(false);
            listeacces.addAccesChamp(acceschamp);
            count++;
            boolean endOfArgs = false;
            if (count >= countmax) {
                endOfArgs = true;
            }
            while (!endOfArgs) {
                String text2 = ctx.getChild(count).getText();
                if (text2.equals(".")) {
                    count++;
                    Ast champ2 = ctx.getChild(count).accept(this);
                    AccesChamp acceschamp2 = new AccesChamp();
                    acceschamp2.setChild(champ2);
                    acceschamp2.setisArrayAccess(false);
                    listeacces.addAccesChamp(acceschamp2);
                    count++;
                } else if (text2.equals("[")) {
                    count++;
                    Ast index = ctx.getChild(count).accept(this);
                    AccesChamp acceschamp2 = new AccesChamp();
                    acceschamp2.setChild(index);
                    acceschamp2.setisArrayAccess(true);
                    listeacces.addAccesChamp(acceschamp2);
                    count++;
                    count++;
                } else {
                    endOfArgs = true;
                }
                if (count >= countmax) {
                    endOfArgs = true;
                }

            }
            retour = listeacces;
        } else if (text.equals("[")) {
            Ast expression = ctx.getChild(count).accept(this);
            ListeAcces listeacces = new ListeAcces();
            listeacces.setisExpressionArray(false);
            count += 2;
            if (count >= countmax) {
                AccesChamp acceschamp = new AccesChamp();
                acceschamp.setChild(expression);
                acceschamp.setisArrayAccess(true);
                listeacces.addAccesChamp(acceschamp);
                retour = listeacces;
            } else {
                String text3 = ctx.getChild(count).getText();
                if (text3.equals("of")) {
                    listeacces.setisExpressionArray(true);
                    listeacces.setExpressionArray(new ExpressionArray());
                    listeacces.getExpressionArray().setSize(expression);
                    count++;
                    Ast expression2 = ctx.getChild(count).accept(this);
                    listeacces.getExpressionArray().setExpr(expression2);

                    retour = listeacces;
                } else {
                    AccesChamp acceschamp = new AccesChamp();
                    acceschamp.setChild(expression);
                    acceschamp.setisArrayAccess(true);
                    listeacces.addAccesChamp(acceschamp);
                    boolean endOfArgs = false;
                    while (!endOfArgs) {
                        String text2 = ctx.getChild(count).getText();
                        if (text2.equals(".")) {
                            count++;
                            Ast champ2 = ctx.getChild(count).accept(this);
                            AccesChamp acceschamp2 = new AccesChamp();
                            acceschamp2.setChild(champ2);
                            acceschamp2.setisArrayAccess(false);
                            listeacces.addAccesChamp(acceschamp2);
                            count++;
                        } else if (text2.equals("[")) {
                            count++;
                            Ast index = ctx.getChild(count).accept(this);
                            AccesChamp acceschamp2 = new AccesChamp();
                            acceschamp2.setChild(index);
                            acceschamp2.setisArrayAccess(true);
                            listeacces.addAccesChamp(acceschamp2);
                            count++;
                            count++;
                        } else {
                            endOfArgs = true;
                        }
                        if (count >= countmax) {
                            endOfArgs = true;
                        }

                    }
                    retour = listeacces;

                }
            }

        } else {
            retour = null;
        }
        return retour;
    }

    @Override
    public Ast visitDeclarationType(DeclarationTypeContext ctx) {
        DeclarationType dt = new DeclarationType(ctx);

        idf = ctx.getChild(1).getText();

        SymbolLookup table = this.table.getSymbolLookup(region);

        // Check for existence of the identifier
        if (table.getType(idf) != null) {
            errorHandler.error(ctx, "Type '" + idf + "' already defined");
        }

        dt.setId(ctx.getChild(1).accept(this));
        dt.setType(ctx.getChild(3).accept(this));
        return dt;
    }

    @Override
    public Ast visitDeclarationTypeClassique(DeclarationTypeClassiqueContext ctx) {
        DeclarationTypeClassique dtc = new DeclarationTypeClassique(ctx);
        SymbolLookup table = this.table.getSymbolLookup(region);

        table.addType(idf, TypeInferer.inferType(table, ctx.getChild(0).getText()));
        dtc.setId(ctx.getChild(0).accept(this));
        return dtc;
    }

    @Override
    public Ast visitDeclarationArrayType(DeclarationArrayTypeContext ctx) {
        DeclarationArrayType dat = new DeclarationArrayType(ctx);

        SymbolLookup table = this.table.getSymbolLookup(region);

        table.addType(idf, new Array(TypeInferer.inferType(table, ctx.getChild(2).getText())));

        dat.setId(ctx.getChild(2).accept(this));
        return dat;
    }

    @Override
    public Ast visitDeclarationRecordType(DeclarationRecordTypeContext ctx) {
        DeclarationRecordType drt = new DeclarationRecordType(ctx);
        Record rec = new Record();
        SymbolLookup table = this.table.getSymbolLookup(region);
        for (int i = 0; 2 * i + 1 < ctx.getChildCount() - 1; i++) {
            String[] split = ctx.getChild(2 * i + 1).getText().split(":");
            Variable v = null;
            v = new Variable(split[0], TypeInferer.inferType(table, split[1]));
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
        DeclarationValeur dv = new DeclarationValeur(ctx);
        dv.setId(ctx.getChild(1).accept(this));

        SymbolLookup table = this.table.getSymbolLookup(region);
        String idf = ctx.getChild(1).getText();
        String expr = ctx.getChild(3).getText();

        Symbol s = table.getSymbolInScope(idf);

        if (ctx.getChild(2).getText().equals(":")) {
            if (s != null)
                errorHandler.error(ctx,
                        "Variable '" + idf + "' already defined as a " + s.toString() + "in this scope");
            else
                table.addSymbolVarAndFunc(new Variable(idf, TypeInferer.inferType(table, expr)));
            dv.setType(ctx.getChild(3).accept(this));
            dv.setExpr(ctx.getChild(5).accept(this));
        } else {
            if (s != null)
                errorHandler.error(ctx,
                        "Variable '" + idf + "' already defined as a " + s.toString() + "in this scope");
            else
                table.addSymbolVarAndFunc(new Variable(idf, TypeInferer.inferType(table, expr)));
            dv.setExpr(ctx.getChild(3).accept(this));
        }
        return dv;

    }

    @Override
    public Ast visitDeclarationFonction(DeclarationFonctionContext ctx) {
        DeclarationFonction drf = new DeclarationFonction(ctx);

        int temp = region;
        SymbolLookup table = this.table.getSymbolLookup(region);

        // Add a new SymbolLookup for the function
        table.addChildren();
        // Get the id of the new SymbolLookup
        int id = table.getChildren().size() - 1;

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
        Type type = null;

        if (txt.equals(":")) {
            count++;
            type = TypeInferer.inferType(table, ctx.getChild(count).getText());
            branch = ctx.getChild(count).accept(this);
            drf.setReturn(((ID) branch));
            count++;
        }
        count++;
        branch = ctx.getChild(count).accept(this);
        String b = ctx.getChild(count).getText();

        if (type == null) {
            type = TypeInferer.inferType(table, b);
        }

        drf.setExpr(branch);

        Symbol s = table.getSymbolInScope(idf);
        String err = FuncCSem.checkFuncFromLib(idf);

        if (s != null) {
            errorHandler.error(ctx, "Symbol '" + idf + "' already defined as a " + s.toString() + " in this scope");
        } else if (err != null) {
            errorHandler.error(ctx, err);
        } else {
            Function f = new Function(idf, type);
            table.addSymbolVarAndFunc(f);
            f.setTable(table.getChildren(id));
            f.addParams(params);
        }

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

        String idf = ctx.getChild(1).getText();
        Ast init = ctx.getChild(1).accept(this);
        Ast condition = ctx.getChild(3).accept(this);
        Ast increment = ctx.getChild(5).accept(this);
        Ast block = ctx.getChild(7).accept(this);

        region = temp;

        Symbol s = table.getSymbolInScope(idf);

        // Add init to SLT
        if (s != null)
            errorHandler.error(ctx, "Variable '" + ctx.getChild(1).getText() + "' already defined");
        else
            table.addSymbolVarAndFunc(new Variable(idf, TypeInferer.inferType(table, "int")));

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
