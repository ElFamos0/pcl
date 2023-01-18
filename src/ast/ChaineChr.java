package ast;

import parser.exprParser.ChaineChrContext;

public class ChaineChr implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String valeur;
	public ChaineChrContext ctx;

	public ChaineChr(ChaineChrContext ctx,String valeur) {
		this.ctx = ctx;
		this.valeur = valeur;
        // enlever les guillemets
        this.valeur = this.valeur.substring(1, this.valeur.length() - 1);
        // add ` to the string 
        this.valeur = "`" + this.valeur + "`";
	}
}
