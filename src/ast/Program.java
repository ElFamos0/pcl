/* ************************************************************************** */
/*                                                                            */
/*                                                                            */
/*   Program.java                                                             */
/*                                                                            */
/*   By: Thibault Cheneviere <thibault.cheneviere@telecomnancy.eu>            */
/*                                                                            */
/*   Created: 2022/11/30 17:08:39 by Thibault Cheneviere                      */
/*   Updated: 2022/11/30 17:20:08 by Thibault Cheneviere                      */
/*                                                                            */
/* ************************************************************************** */

package ast;

public class Program implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
	public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

	public Ast expression;

	public Program(Ast expression) {
		this.expression = expression;
	}
}
