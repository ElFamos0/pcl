package ast;

public class DeclarationTypeClassique implements Ast{
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast id;
    public DeclarationTypeClassique() {
    }

    public void setId(Ast id) {
        this.id = id;
    }
}
