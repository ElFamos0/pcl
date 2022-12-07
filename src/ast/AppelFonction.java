package ast;

public class AppelFonction implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast id;
    public Ast args;

    public AppelFonction(Ast args) {
        this.args = args;
    }

    public void setId(Ast id) {
        this.id = id;
    }
}
