package ast;

import parser.exprParser.TantQueContext;

public class While implements Ast {

    // Utile pour la dernière partie
    public <T> T accept(AstVisitor<T> visitor){
        return visitor.visit(this);
    }
    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

    public Ast condition;
    public Ast block;
    public TantQueContext ctx;

    public While(Ast condition,Ast block, TantQueContext ctx){
        this.condition = condition;
        this.block = block;
        this.ctx = ctx;
    }

}
