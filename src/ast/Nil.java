package ast;

import parser.exprParser.NilContext;

public class Nil implements Ast {

	public NilContext ctx;

	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public Nil(NilContext ctx) {
		this.ctx = ctx;
	}
}
