package ast;

import parser.exprParser.SiAlorsContext;

public class IfThen implements Ast {

    // Utile pour la dernière partie
    public <T> T accept(AstVisitor<T> visitor){
        return visitor.visit(this);
    }

    public Ast condition;
    public Ast thenBlock;
    public SiAlorsContext ctx;

    public IfThen(SiAlorsContext ctx,Ast condition,Ast thenBlock){
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.ctx = ctx;
    }

}
