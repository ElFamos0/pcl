package ast;

public interface AstVisitor<T> {
	public T visit(Program a);
	public T visit(Expression a);
	public T visit(Ou a);
	public T visit(Et a);
	public T visit(Compar a);
	public T visit(Addition a);
	public T visit(Soustraction a);
	public T visit(Multiplication a);
	public T visit(Division a);
	public T visit(Sequence a);
	public T visit(Negation a);
	public T visit(ID a);
	public T visit(Int a);
	public T visit(ExpressionIdentifiant a);
	public T visit(AppelFonction a);
	public T visit(ArgFonction a);
	public T visit(IfThenElse a);
	public T visit(IfThen a);
	public T visit(While a);
	public T visit(For a);
	public T visit(Definition a);
	public T visit(DeclarationType a);
	public T visit(DeclarationTypeClassique a);
	public T visit(DeclarationArrayType a);
	public T visit(DeclarationRecordType a);
	public T visit(DeclarationChamp a);
	public T visit(DeclarationFonction a);
	public T visit(DeclarationValeur a);
	public T visit(ChaineChr a);
	public T visit(Nil a);
	public T visit(Break a);
	public T visit(InstanciationType a);
}
