package ast;

public class ChaineChr implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public String valeur;

	public ChaineChr(String valeur) {
		this.valeur = valeur;
        // enlever les guillemets
        this.valeur = this.valeur.substring(1, this.valeur.length() - 1);
        // add ` to the string 
        this.valeur = "`" + this.valeur + "`";
	}
}
