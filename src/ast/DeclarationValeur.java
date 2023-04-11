package ast;

import parser.exprParser.DeclarationValeurContext;

public class DeclarationValeur implements Ast {
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Ast id;
    public Ast type;
    public Ast expr;
    public DeclarationValeurContext ctx;

    public DeclarationValeur(DeclarationValeurContext ctx) {
        this.ctx = ctx;
    }

    public void setId(Ast id) {
        this.id = id;
    }

    public void setType(Ast type) {
        this.type = type;
    }

    public void setExpr(Ast expr) {
        this.expr = expr;
    }

    public Ast getId() {
        return this.id;
    }

    public Ast getType() {
        return this.type;
    }

    public Ast getExpr() {
        return this.expr;
    }
}
