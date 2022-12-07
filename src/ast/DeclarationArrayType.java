package ast;

public class DeclarationArrayType implements Ast{
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast id;
    public Ast type;

    public DeclarationArrayType(Ast type) {
        this.type = type;
    }

    public void setId(Ast id) {
        this.id = id;
    }
}
