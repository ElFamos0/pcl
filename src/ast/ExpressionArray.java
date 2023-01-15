package ast;

import parser.exprParser.ListeAccesContext;

public class ExpressionArray implements Ast{
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    Ast size;
    Ast expr;
    Ast id;
    public ListeAccesContext ctx;

    public Ast getId() {
        return id;
    }

    public void setId(Ast id) {
        this.id = id;
    }

    public ExpressionArray(ListeAccesContext ctx) {
        this.ctx = ctx;
    }

    public void setSize(Ast size) {
        this.size = size;
    }

    public void setExpr(Ast expr) {
        this.expr = expr;
    }

    public Ast getSize() {
        return this.size;
    }

    public Ast getExpr() {
        return this.expr;
    }
    
}
