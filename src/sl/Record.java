package sl;

import java.util.ArrayList;

public class Record extends Type {
    private ArrayList<Symbol> fields;
    private Boolean isNil;

    public Record() {
        fields = new ArrayList<Symbol>();
    }

    public ArrayList<Symbol> getFields() {
        return fields;
    }

    public void addField(Symbol s) {
        fields.add(s);
    }

    public Boolean hasField(String name) {
        for (Symbol s : fields) {
            if (s.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Symbol getField(String name) {
        for (Symbol s : fields) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public Integer getOffset() {
        int size = 0;
        for (Symbol s : fields) {
            size += s.getType().getOffset();
        }
        return size;
    }

    public boolean equals(Type t) {
        if (t instanceof Record) {
            Record r = (Record) t;
            if (r.getFields().size() != fields.size()) {
                return false;
            }
            for (int i = 0; i < fields.size(); i++) {
                if (!fields.get(i).getType().equals(r.getFields().get(i).getType())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public String toString() {
        String s = "record {";
        for (Symbol field : fields) {
            s += field.getName() + ":" + field.getType().toString() + ", ";
        }
        if (fields.size() > 0)
            s = s.substring(0, s.length() - 2);
        s += "}";
        return s;
    }

    public void setFields(ArrayList<Symbol> fields) {
        this.fields = fields;
    }

    public Boolean getIsNil() {
        return isNil;
    }

    public void setIsNil(Boolean isNil) {
        this.isNil = isNil;
    }
}
