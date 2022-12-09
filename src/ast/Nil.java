package ast;

public class Nil implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public Nil() {
	}
}
