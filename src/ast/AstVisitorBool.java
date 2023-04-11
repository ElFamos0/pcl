package ast;

public interface AstVisitorBool<T> {
	public T visit(Program a, boolean bool);
	public T visit(Expression a, boolean bool);
	public T visit(Ou a, boolean bool);
	public T visit(Et a, boolean bool);
	public T visit(Compar a, boolean bool);
	public T visit(Addition a, boolean bool);
	public T visit(Soustraction a, boolean bool);
	public T visit(Multiplication a, boolean bool);
	public T visit(Division a, boolean bool);
	public T visit(Sequence a, boolean bool);
	public T visit(Negation a, boolean bool);
	public T visit(ID a, boolean bool);
	public T visit(Int a, boolean bool);
	public T visit(ExpressionIdentifiant a, boolean bool);
	public T visit(AppelFonction a, boolean bool);
	public T visit(ArgFonction a, boolean bool);
	public T visit(IfThenElse a, boolean bool);
	public T visit(IfThen a, boolean bool);
	public T visit(While a, boolean bool);
	public T visit(For a, boolean bool);
	public T visit(Definition a, boolean bool);
	public T visit(DeclarationType a, boolean bool);
	public T visit(DeclarationTypeClassique a, boolean bool);
	public T visit(DeclarationArrayType a, boolean bool);
	public T visit(DeclarationRecordType a, boolean bool);
	public T visit(DeclarationChamp a, boolean bool);
	public T visit(DeclarationFonction a, boolean bool);
	public T visit(DeclarationValeur a, boolean bool);
	public T visit(ChaineChr a, boolean bool);
	public T visit(Nil a, boolean bool);
	public T visit(Break a, boolean bool);
	public T visit(InstanciationType a, boolean bool);
	public T visit(ListeAcces a, boolean bool);
	public T visit(ExpressionArray a, boolean bool);
	public T visit(AccesChamp a, boolean bool);
}
