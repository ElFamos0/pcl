package st;

public class Variable extends FuncOrVariable {
    private int offset;

    public Variable(String name, Type type) {
        super(name, type);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
