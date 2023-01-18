package ast;

import parser.exprParser.ExpressionIdentifiantContext;

public class ExpressionIdentifiant implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast left;
    public Ast right;
    public ExpressionIdentifiantContext ctx;

    public ExpressionIdentifiant(ExpressionIdentifiantContext ctx, Ast left, Ast right) {
        this.ctx = ctx;
        this.left = left;
        this.right = right;
    }

    public ExpressionIdentifiant(Ast left) {
        this.left = left;
    }
}
