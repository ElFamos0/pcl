package ast;

import parser.exprParser.OperationOuContext;

public class Ou implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

    public Ast left;
    public Ast right;
    public OperationOuContext ctx;

    public Ou(OperationOuContext ctx, Ast left, Ast right) {
        this.ctx = ctx;
        this.left = left;
        this.right = right;
    }
}
