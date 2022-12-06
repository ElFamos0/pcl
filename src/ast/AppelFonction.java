package ast;

import java.util.ArrayList;

public class AppelFonction implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast id;
    public Ast args;

    public AppelFonction(Ast id, Ast args) {
        this.id = id;
        this.args = args;
    }
}
