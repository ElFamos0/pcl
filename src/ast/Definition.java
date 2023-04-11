
// :   'let' declaration+ 'in' ( expression ( ';' expression )* )? 'end'

package ast;

import java.util.ArrayList;

import parser.exprParser.DefinitionContext;

public class Definition implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }
    
    public ArrayList<Ast> declarations;
    public ArrayList<Ast> exprs;
    public DefinitionContext ctx;

    public Definition(DefinitionContext ctx) {
        this.ctx = ctx;
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
