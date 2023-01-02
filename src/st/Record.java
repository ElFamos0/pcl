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
}
