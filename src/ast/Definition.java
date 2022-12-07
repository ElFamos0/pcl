
// :   'let' declaration+ 'in' ( expression ( ';' expression )* )? 'end'

package ast;

import java.util.ArrayList;

public class Definition implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
    
    public ArrayList<Ast> declarations;
    public ArrayList<Ast> exprs;

    public Definition() {
        this.declarations = new ArrayList<Ast>();
        this.exprs = new ArrayList<Ast>();
    }

    public void addDeclaration(Ast declaration) {
        this.declarations.add(declaration);
    }

    public void addExpr(Ast expr) {
        this.exprs.add(expr);
    }
}
