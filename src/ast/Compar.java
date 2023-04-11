package ast;

import parser.exprParser.OperationComparaisonContext;

public class Compar implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast left;
    public Ast right;
    public String operator;
    public OperationComparaisonContext ctx;

    public Compar(OperationComparaisonContext ctx,Ast left, Ast right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.ctx = ctx;
    }
}
