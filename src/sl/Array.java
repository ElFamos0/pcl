package sl;

public class Array extends Type {
    private Type type;

    public Array(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Integer getSize() {
        // Array size is 8 because it is a ptr to the heap
        return 8;
    }
}
