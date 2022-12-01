package ast;

public class Negation implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public Ast expression;

	public Negation(Ast expression) {
		this.expression = expression;
	}
}
