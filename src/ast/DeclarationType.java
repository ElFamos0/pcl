package ast;

import parser.exprParser.DeclarationTypeContext;

public class DeclarationType implements Ast{
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }
    
    public Ast id;
    public Ast type;
    public DeclarationTypeContext ctx;

    public DeclarationType(DeclarationTypeContext ctx) {
        this.ctx = ctx;
    }

    public void setId(Ast id) {
        this.id = id;
    }
    public void setType(Ast type) {
        this.type = type;
    }
    public Ast getId() {
        return this.id;
    }
    public Ast getType() {
        return this.type;
    }
    
}
