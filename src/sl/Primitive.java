package sl;

public class Primitive extends Type {
    private PrimitiveType type;

    public <T> Primitive(Class<T> classz) {
        type = classToType(classz);
    }

    private enum PrimitiveType {
        INT, CHAR, BOOL, VOID;
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
        else {
            return null;
        }
    }

    public PrimitiveType getType() {
        return type;
    }

    public Integer getSize() {
        switch (type) {
            case INT:
                // Int size
                return 8;
            case CHAR:
                // String size
                return 1;
            case BOOL:
                // Bool size
                return 1;
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Type t) {
        System.out.println(t);
        if (t instanceof Primitive) {
            Primitive p = (Primitive) t;
            System.out.println(p.getType() + " " + type);
            return p.getType() == type;
        }
        return false;
    }
}
