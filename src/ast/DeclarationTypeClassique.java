package ast;

public class DeclarationTypeClassique implements Ast{
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast id;
    public Ast type;

    public DeclarationTypeClassique(Ast type) {
        this.type = type;
    }

    public void setId(Ast id) {
        this.id = id;
    }
}
