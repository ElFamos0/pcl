package ast;

import parser.exprParser.AppelFonctionContext;

public class AppelFonction implements Ast {
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

    public Ast id;
    public Ast args;
    public AppelFonctionContext ctx;

    public AppelFonction(Ast args, AppelFonctionContext ctx) {
        this.args = args;
        this.ctx = ctx;
    }

    public void setId(Ast id) {
        this.id = id;
    }
}
