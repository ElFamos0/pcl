package ast;

public class ExprValeur implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast left;
    public Ast right;

    public ExprValeur(Ast left, Ast right) {
        this.left = left;
        this.right = right;
    }

    public ExprValeur(Ast left) {
        this.left = left;
    }
}
