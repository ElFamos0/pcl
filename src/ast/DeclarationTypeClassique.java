package ast;

import parser.exprParser.DeclarationTypeClassiqueContext;

public class DeclarationTypeClassique implements Ast{
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast id;
    public DeclarationTypeClassiqueContext ctx;

    public DeclarationTypeClassique(DeclarationTypeClassiqueContext ctx) {
        this.ctx = ctx;
    }

    public void setId(Ast id) {
        this.id = id;
    }
}
