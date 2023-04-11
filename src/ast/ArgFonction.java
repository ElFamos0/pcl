package ast;

import java.util.ArrayList;

import parser.exprParser.AppelFonctionContext;

public class ArgFonction implements Ast {
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public ArrayList<Ast> args;
    public AppelFonctionContext ctx;

    public ArgFonction(AppelFonctionContext ctx) {
        this.ctx = ctx;
        this.args = new ArrayList<>();
    }

    public void addArg(Ast arg) {
        this.args.add(arg);
    }
}
