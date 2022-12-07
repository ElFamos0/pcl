package ast;

public class While implements Ast {

    // Utile pour la dernière partie
    public <T> T accept(AstVisitor<T> visitor){
        return visitor.visit(this);
    }

    public Ast condition;
    public Ast block;

    public While(Ast condition,Ast block){
        this.condition = condition;
        this.block = block;
    }

}
