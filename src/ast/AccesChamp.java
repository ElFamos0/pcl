package ast;

public class AccesChamp implements Ast{
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public <T> T accept(AstVisitorBool<T> visitor, boolean bool) {
        return visitor.visit(this,bool);
    }

    boolean isArrayAccess;
    Ast child;

    public AccesChamp() {
        this.isArrayAccess = false;
    }

    public void setChild(Ast child) {
        this.child = child;
    }

    public Ast getChild() {
        return this.child;
    }

    public void setisArrayAccess(boolean isArrayAccess) {
        this.isArrayAccess = isArrayAccess;
    }

    public boolean getisArrayAccess() {
        return this.isArrayAccess;
    }
    
}
