package ast;

public class Soustraction implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast left;
    public Ast right;

    public Soustraction(Ast left, Ast right) {
        this.left = left;
        this.right = right;
    }
}
