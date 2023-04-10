package asm;

public class Constant {
    private static Integer sid = 0;
    private String id;
    private Object value;
    private int type;

    public Constant(Integer value) {
        this.value = value;
        this.type = 0;

        this.sid += 1;
        this.id = sid.toString();
    }

    public Constant(String value) {
        this.value = value;
        this.type = 1;
        
        this.sid += 1;
        this.id = sid.toString();
    }

    public String toString() {
        return value.toString();
    }

    public String toASM() {
        switch (type) {
            case 0:
                return "string_"+id+":\t.word " + value.toString();
            case 1:
                return "string_"+id+":\t.ascii \"" + value.toString() + "\\0\"";
            default:
                return "";
        }
    }

    public String getId() {
        return "string_"+id;
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
