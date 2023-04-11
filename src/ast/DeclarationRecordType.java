package ast;

import java.util.ArrayList;

import parser.exprParser.DeclarationRecordTypeContext;

// | '{' ( identifiant ':' identifiant ( ',' identifiant ':' identifiant )* )? '}' #DeclarationRecordType

public class DeclarationRecordType implements Ast{
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}
    
    // public Ast id;
    public ArrayList<Ast> champs = new ArrayList<Ast>();
    public DeclarationRecordTypeContext ctx;


    public DeclarationRecordType(DeclarationRecordTypeContext ctx) {
        this.ctx = ctx;
        this.champs = new ArrayList<Ast>();
    }

    // public void setId(Ast id) {
    //     this.id = id;
    // }

    public void addChamp(Ast champ) {
        this.champs.add(champ);
    }
}
