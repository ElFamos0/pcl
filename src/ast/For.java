package ast;

public class For implements Ast {

    // Utile pour la dernière partie
    public <T> T accept(AstVisitor<T> visitor){
        return visitor.visit(this);
    }

    public Ast start;
    public Ast startValue;
    public Ast endValue;
    public Ast block;

    public For(Ast start, Ast startValue, Ast endValue, Ast block){
        this.start = start;
        this.startValue = startValue;
        this.endValue = endValue;
        this.block = block;
    }

}
