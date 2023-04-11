package ast;

import parser.exprParser.NilContext;

public class Nil implements Ast {

	public NilContext ctx;

	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
	public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

	public Nil(NilContext ctx) {
		this.ctx = ctx;
	}
}
