/* ************************************************************************** */
/*                                                                            */
/*                                                                            */
/*   AstCreator.java                                                          */
/*                                                                            */
/*   By: Thibault Cheneviere <thibault.cheneviere@telecomnancy.eu>            */
/*                                                                            */
/*   Created: 2022/11/30 17:11:04 by Thibault Cheneviere                      */
/*   Updated: 2022/11/30 17:13:00 by Thibault Cheneviere                      */
/*                                                                            */
/* ************************************************************************** */

package ast;

import parser.exprBaseVisitor;
import parser.exprParser;
import parser.exprParser.AppelFonctionContext;
import parser.exprParser.EntierContext;
import parser.exprParser.ExpressionIdentifiantContext;
import parser.exprParser.ExpressionUnaireContext;
import parser.exprParser.IdentifiantContext;
import parser.exprParser.NegationContext;
import parser.exprParser.OperationAdditionContext;
import parser.exprParser.OperationComparaisonContext;
import parser.exprParser.OperationEtContext;
import parser.exprParser.OperationMultiplicationContext;
import parser.exprParser.OperationOuContext;
import parser.exprParser.SequenceContext;
import parser.exprParser.SiAlorsContext;
import parser.exprParser.SiAlorsSinonContext;

public class AstCreator extends exprBaseVisitor<Ast> {
	@Override
	public Ast visitProgram(exprParser.ProgramContext ctx) {
		Ast child = ctx.getChild(0).accept(this);
		return new Program(child);
	}

	@Override
	public Ast visitExpression(exprParser.ExpressionContext ctx) {
		Ast noeudTemporaire = ctx.getChild(0).accept(this);

		for (int i = 0; 2*i < ctx.getChildCount()-1; i++) {
			noeudTemporaire = new Expression(noeudTemporaire, ctx.getChild(2*i+2).accept(this));
		}

		return noeudTemporaire;
	}

	@Override
	public Ast visitOperationOu(OperationOuContext ctx) {
		Ast noeudTemporaire = ctx.getChild(0).accept(this);

		for (int i = 0; 2*i < ctx.getChildCount()-1; i++) {
			noeudTemporaire = new Ou(noeudTemporaire, ctx.getChild(2*i+2).accept(this));
		}

		return noeudTemporaire;
	}

	@Override
	public Ast visitOperationEt(OperationEtContext ctx) {
		Ast noeudTemporaire = ctx.getChild(0).accept(this);

		for (int i = 0; 2*i < ctx.getChildCount()-1; i++) {
			noeudTemporaire = new Et(noeudTemporaire, ctx.getChild(2*i+2).accept(this));
		}

		return noeudTemporaire;
	}

	@Override
	public Ast visitOperationComparaison(OperationComparaisonContext ctx) {
		Ast noeudTemporaire = ctx.getChild(0).accept(this);

		for (int i = 0; 2*i < ctx.getChildCount()-1; i++) {
			noeudTemporaire = new Compar(noeudTemporaire, ctx.getChild(2*i+2).accept(this), ctx.getChild(2*i+1).getText());
		}

		return noeudTemporaire;
	}

	@Override
	public Ast visitOperationAddition(OperationAdditionContext ctx) {
		Ast noeudTemporaire = ctx.getChild(0).accept(this);

		for (int i = 0; 2*i < ctx.getChildCount()-1; i++) {
			String operation = ctx.getChild(2*i+1).getText();
			Ast right = ctx.getChild(2*i+2).accept(this);

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

		for (int i = 0; 2*i < ctx.getChildCount()-1; i++) {
			String operation = ctx.getChild(2*i+1).getText();
			Ast right = ctx.getChild(2*i+2).accept(this);

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

		for (int i = 0; 2*i < ctx.getChildCount()-1; i++) {
			sequence.addSeq(ctx.getChild(2*i+1).accept(this));
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
	public Ast visitAppelFonction(AppelFonctionContext ctx) {
		ArgFonction argFonction = new ArgFonction();
		// args are located at even indexes
		if (ctx.getChildCount() > 3) {
			for (int i = 0; 2*i+1 < ctx.getChildCount()-1; i++) {
				argFonction.addArg(ctx.getChild(2*i+2).accept(this));
			}
		}

		AppelFonction af = new AppelFonction(ctx.getChild(0).accept(this), argFonction);
		return af;
	}

	@Override
	public Ast visitExpressionIdentifiant(ExpressionIdentifiantContext ctx) {
		return ctx.getChild(0).accept(this);
	}

	@Override
	public Ast visitSiAlors(SiAlorsContext ctx) {
		Ast condition = ctx.getChild(1).accept(this);
		Ast thenBlock = ctx.getChild(3).accept(this);

		return new IfThen(condition, thenBlock);
	}

	@Override
	public Ast visitSiAlorsSinon(SiAlorsSinonContext ctx) {
		Ast condition = ctx.getChild(1).accept(this);
		Ast thenBlock = ctx.getChild(3).accept(this);
		Ast elseBlock = ctx.getChild(5).accept(this);

		return new IfThenElse(condition, thenBlock, elseBlock);
	}
}
