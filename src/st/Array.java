package st;

public class Array extends Type {
    private int size;
    private Type type;

    public Array(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    public Integer getSize() {
        return size * type.getSize();
    }

    public Type getType() {
        return type;
    }
}
