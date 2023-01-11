package ast;

public class ExpressionArray implements Ast{
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    Ast size;
    Ast expr;

    public ExpressionArray() {
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
