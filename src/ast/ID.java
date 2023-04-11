package ast;

import parser.exprParser.IdentifiantContext;

public class ID implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String nom;
	public IdentifiantContext ctx;

	public ID(IdentifiantContext ctx, String nom) {
		this.ctx = ctx;
		this.nom = nom;
	}
}
