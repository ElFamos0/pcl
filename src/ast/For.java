package ast;

import parser.exprParser.PourContext;;

public class For implements Ast {

    // Utile pour la dernière partie
    public <T> T accept(AstVisitor<T> visitor){
        return visitor.visit(this);
    }
    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

    public Ast start; 
    public Ast startValue;
    public Ast endValue;
    public Ast block;
    public PourContext ctx;

    public For(Ast start, Ast startValue, Ast endValue, Ast block, PourContext ctx){
        this.start = start;
        this.startValue = startValue;
        this.endValue = endValue;
        this.block = block;
        this.ctx = ctx;
    }

}
