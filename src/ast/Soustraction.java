package ast;

import parser.exprParser.OperationAdditionContext;

public class Soustraction implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast left;
    public Ast right;
    public OperationAdditionContext ctx;

    public Soustraction(OperationAdditionContext ctx, Ast left, Ast right) {
        this.ctx = ctx;
        this.left = left;
        this.right = right;
    }
}
