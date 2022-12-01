package ast;

import java.util.ArrayList;

public class Sequence implements Ast {
    public <T> T accept(AstVisitor<T> visitor){
        return visitor.visit(this);
    }
    
    public ArrayList<Ast> seqs;

    public Sequence() {
        this.seqs = new ArrayList<>();
    }

    public void addSeq(Ast seq){
        this.seqs.add(seq);
    }
}
