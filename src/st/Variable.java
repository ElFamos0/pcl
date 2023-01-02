package st;

public class Variable extends FuncOrVariable {
    private int offset;

    public Variable(Type type, String name, int offset) {
        super(name, type);
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
