/* ************************************************************************** */
/*                                                                            */
/*                                                                            */
/*   AstVisitor.java                                                          */
/*                                                                            */
/*   By: Thibault Cheneviere <thibault.cheneviere@telecomnancy.eu>            */
/*                                                                            */
/*   Created: 2022/11/30 17:07:19 by Thibault Cheneviere                      */
/*   Updated: 2022/11/30 17:08:08 by Thibault Cheneviere                      */
/*                                                                            */
/* ************************************************************************** */

package ast;

public interface AstVisitor<T> {
	public T visit(Program affect);
}
