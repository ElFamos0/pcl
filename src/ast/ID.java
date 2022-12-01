package ast;

public class ID implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String nom;

	public ID(String nom) {
		this.nom = nom;
	}
}
