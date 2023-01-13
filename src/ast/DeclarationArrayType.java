package ast;

import parser.exprParser.DeclarationArrayTypeContext;

public class DeclarationArrayType implements Ast{
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

    public Ast id;
    public DeclarationArrayTypeContext ctx;

    public DeclarationArrayType(DeclarationArrayTypeContext ctx) {
        this.ctx = ctx;
    }

    public void setId(Ast id) {
        this.id = id;
    }
}
