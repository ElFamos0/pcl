package st;

import java.util.ArrayList;

public class Record extends Type {
    private ArrayList<Symbol> fields;

    public Record() {
        fields = new ArrayList<Symbol>();
    }

    public ArrayList<Symbol> getFields() {
        return fields;
    }

    public void addField(Symbol s) {
        fields.add(s);
    }

    public Symbol getField(String name) {
        for (Symbol s : fields) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public Integer getSize() {
        int size = 0;
        for (Symbol s : fields) {
            size += s.getType().getSize();
        }
        return size;
    }
}
