package ast;

import java.util.ArrayList;

// function' identifiant '(' ( declarationChamp ( ',' declarationChamp )* )? ')' ( ':' identifiant )? '=' expression 

public class DeclarationFonction implements Ast{
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
    
    public Ast id;
    public ArrayList<Ast> args = new ArrayList<Ast>();
    public boolean has_return = false;
    public Ast return_type;
    public Ast expr;


    public DeclarationFonction() {
        this.args = new ArrayList<Ast>();
    }

    public void setId(Ast id) {
        this.id = id;
    }

    public void setReturn(Ast type) {
        this.has_return = true;
        this.return_type = type;
    }

    public void setExpr(Ast expr) {
        this.expr = expr;
    }

    public void addArg(Ast arg) {
        this.args.add(arg);
    }
}
