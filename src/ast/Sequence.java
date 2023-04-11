package ast;

import java.util.ArrayList;

import org.antlr.v4.runtime.ParserRuleContext;

public class Sequence implements Ast {
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String nom = "Sequence";
    public ArrayList<Ast> seqs;
    public ParserRuleContext ctx;

    public Sequence(ParserRuleContext ctx) {
        this.ctx = ctx;
        this.seqs = new ArrayList<>();
    }
    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

    public void addSeq(Ast seq) {
        this.seqs.add(seq);
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
