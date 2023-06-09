package ast;

import parser.exprParser.DeclarationChampContext;

public class DeclarationChamp implements Ast {
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public DeclarationChampContext ctx;
    public Ast id;
    public Ast type;

    public DeclarationChamp(DeclarationChampContext ctx, Ast id, Ast type) {
        this.ctx = ctx;
        this.id = id;
        this.type = type;
    }
}
