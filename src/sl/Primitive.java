package sl;

public class Primitive extends Type {
    private PrimitiveType type;

    public <T> Primitive(Class<T> classz) {
        type = classToType(classz);
    }

    private enum PrimitiveType {
        INT, CHAR, BOOL, VOID, ANY;
    }

    public <T> PrimitiveType classToType(Class<T> classz) {
        if (classz == Integer.class)
            return PrimitiveType.INT;
        else if (classz == Character.class)
            return PrimitiveType.CHAR;
        else if (classz == Boolean.class)
            return PrimitiveType.BOOL;
        else if (classz == Void.class)
            return PrimitiveType.VOID;
        else if (classz == Any.class)
            return PrimitiveType.ANY;
        else {
            return null;
        }
    }

    public PrimitiveType getType() {
        return type;
    }

    public boolean isChar() {
        return type == PrimitiveType.CHAR;
    }

    public Integer getOffset() {
        switch (type) {
            case INT:
                // Int size
                return 4;
            case CHAR:
                // String size
                return 4;
            case BOOL:
                // Bool size
                return 4;
            case ANY:
                // Any size
                return 4;
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Type t) {
        if (type == PrimitiveType.ANY)
            return true;

        if (t instanceof Primitive) {
            Primitive p = (Primitive) t;

            if (p.getType() == PrimitiveType.ANY)
                return true;

            return p.getType() == type;
        }
        return false;
    }

    public String toString() {
        switch (type) {
            case INT:
                return "int";
            case CHAR:
                return "char";
            case BOOL:
                return "bool";
            case VOID:
                return "void";
            case ANY:
                return "any";
            default:
                return null;
        }
    }
}
