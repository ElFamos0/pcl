package ast;

import parser.exprParser.NegationContext;

public class Negation implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
	public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

	public Ast expression;
	public NegationContext ctx;

	public Negation(NegationContext ctx, Ast expression) {
		this.ctx = ctx;
		this.expression = expression;
	}
}
