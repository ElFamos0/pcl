package ast;
import parser.exprParser.BreakContext;

public class Break implements Ast {
	public <T> T accept(AstVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public BreakContext ctx; 

	public Break(BreakContext ctx) {
		this.ctx = ctx;
	}
}
