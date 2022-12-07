package ast;

import java.util.ArrayList;

// | '{' ( identifiant ':' identifiant ( ',' identifiant ':' identifiant )* )? '}' #DeclarationRecordType

public class DeclarationRecordType implements Ast{
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
    
    public Ast id;
    public ArrayList<Ast> champs = new ArrayList<Ast>();


    public DeclarationRecordType() {
        this.champs = new ArrayList<Ast>();
    }

    public void setId(Ast id) {
        this.id = id;
    }

    public void addChamp(Ast champ) {
        this.champs.add(champ);
    }
}
