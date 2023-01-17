package ast;

import parser.exprParser.EntierContext;

public class Int implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String valeur;
	public EntierContext ctx;
	public Boolean alreadySeen = false;
	public Int(EntierContext ctx, String valeur) {
		this.ctx = ctx;
		this.valeur = valeur;
	}
}
