/* ************************************************************************** */
/*                                                                            */
/*                                                                            */
/*   Ast.java                                                                 */
/*                                                                            */
/*   By: Thibault Cheneviere <thibault.cheneviere@telecomnancy.eu>            */
/*                                                                            */
/*   Created: 2022/11/30 17:05:52 by Thibault Cheneviere                      */
/*   Updated: 2022/11/30 17:06:43 by Thibault Cheneviere                      */
/*                                                                            */
/* ************************************************************************** */

package ast;

public interface Ast {
	public <T> T accept(AstVisitor<T> visitor);
	public <T> T accept(AstVisitorBool<T> visitor, boolean bool);
}
