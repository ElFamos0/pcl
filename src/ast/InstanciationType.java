package ast;

import java.util.ArrayList;

public class InstanciationType implements Ast {
    public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
    ArrayList<Ast> identifiants;
    ArrayList<Ast> expressions;
    Ast id;

    public InstanciationType(){
        this.identifiants = new ArrayList<Ast>();
        this.expressions = new ArrayList<Ast>();

    }

    public void setId(Ast id){
        this.id = id;
    }

    public Ast getId(){
        return this.id;
    }

    public void addID(Ast id){
        this.identifiants.add(id);
    }

    public void addExpr(Ast expr){
        this.expressions.add(expr);
    }

    public ArrayList<Ast> getIdentifiants(){
        return this.identifiants;
    }

    public ArrayList<Ast> getExpressions(){
        return this.expressions;
    }
}
