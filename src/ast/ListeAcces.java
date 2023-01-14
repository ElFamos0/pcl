package ast;
import java.util.ArrayList;

import parser.exprParser.ListeAccesContext;

public class ListeAcces implements Ast{
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean isExpressionArray;

    public ExpressionArray expressionArrayAst;

    public Ast id;

    public ArrayList<Ast> accesChamps;

    public ListeAccesContext ctx;

    public ListeAcces(ListeAccesContext ctx) {
        this.ctx = ctx;
        this.accesChamps = new ArrayList<Ast>();
        isExpressionArray = false;
    }

    public void setExpressionArray(ExpressionArray expressionArrayAst) {
        this.expressionArrayAst = expressionArrayAst;
    }

    public void addAccesChamp(Ast accesChamp) {
        this.accesChamps.add(accesChamp);
    }

    public ArrayList<Ast> getAccesChamps() {
        return this.accesChamps;
    }


    public ExpressionArray getExpressionArray() {
        return this.expressionArrayAst;
    }

    public void setisExpressionArray(boolean isExpressionArray) {
        this.isExpressionArray = isExpressionArray;
    }

    public boolean getisExpressionArray() {
        return this.isExpressionArray;
    }

    public void setId(Ast id) {
        this.id = id;
    }

    public Ast getId() {
        return this.id;
    }
}
