package asm;

public class Constant {
    private String id;
    private Object value;
    private int type;

    public Constant(Integer value, String id) {
        this.value = value;
        this.type = 0;
        this.id = id;
    }

    public Constant(String value, String id) {
        this.value = value;
        this.type = 1;
        this.id = id;
    }

    public String toString() {
        if (type == 0) {
            return value.toString();
        } else {
            return "\"" + value.toString() + "\"";
        }
    }

    public String toASM() {
        switch (type) {
            case 0:
                return id+":\t.word " + value.toString();
            case 1:
                return id+":\t.ascii \"" + value.toString() + "\\0\"";
            default:
                return "";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
