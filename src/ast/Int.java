package ast;

public class Int implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public int valeur;

	public Int(int valeur) {
		this.valeur = valeur;
	}
}
