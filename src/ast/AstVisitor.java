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
	public T visit(ExprValeur a);
	public T visit(AppelFonction a);
	public T visit(ArgFonction a);
}
