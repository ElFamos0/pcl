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

    public boolean equals(Type t) {
        if (t instanceof Array) {
            Array a = (Array) t;
            return a.getType().equals(type);
        }
        return false;
    }

    public String toString() {
        if (type instanceof Primitive) {
            Primitive p = (Primitive) type;
            if (p.isChar())
                return "string";
        }

        return "array of " + type.toString() + "s";
    }
}
