package ast;

public class DeclarationType implements Ast{
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public Ast id;
    public Ast type;

    public DeclarationType() {
    }

    public void setId(Ast id) {
        this.id = id;
    }
    public void setType(Ast type) {
        this.type = type;
    }
    public Ast getId() {
        return this.id;
    }
    public Ast getType() {
        return this.type;
    }
    
}
