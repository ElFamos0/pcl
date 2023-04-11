package ast;
import parser.exprParser.SiAlorsSinonContext;

public class IfThenElse implements Ast{

    // Utile pour la dernière partie
    public <T> T accept(AstVisitor<T> visitor){
        return visitor.visit(this);
    }
    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

    public Ast condition;
    public Ast thenBlock;
    public Ast elseBlock; 
    public SiAlorsSinonContext ctx;

    public IfThenElse(SiAlorsSinonContext ctx,Ast condition,Ast thenBlock,Ast elseBlock){
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
        this.ctx = ctx;
    }

    
}
