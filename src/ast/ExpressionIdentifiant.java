package ast;

public class ExpressionIdentifiant implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast left;
    public Ast right;

    public ExpressionIdentifiant(Ast left, Ast right) {
        this.left = left;
        this.right = right;
    }

    public ExpressionIdentifiant(Ast left) {
        this.left = left;
    }
}
