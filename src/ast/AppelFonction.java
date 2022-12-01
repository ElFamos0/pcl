package ast;

import java.util.ArrayList;

public class AppelFonction implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public ArrayList<Ast> args;

    public AppelFonction() {
        this.args = new ArrayList<>();
    }

    public void addArg(Ast arg) {
        this.args.add(arg);
    }
}
