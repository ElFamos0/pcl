package ast;

import parser.exprParser.OperationEtContext;

public class Et implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast left;
    public Ast right;
    public OperationEtContext ctx;

    public Et(OperationEtContext ctx, Ast left, Ast right) {
        this.ctx = ctx;
        this.left = left;
        this.right = right;
    }
}
