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

public class AstCreator extends exprBaseVisitor<Ast> {
	@Override public Ast visitProgram(exprParser.ProgramContext ctx) { return visitChildren(ctx); }
}
