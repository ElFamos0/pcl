package st;

import java.util.ArrayList;

public class Function extends FuncOrVariable {
    private SymbolLookup table;

    public Function(String name, Type type) {
        super(name, type);
    }

    public SymbolLookup getTable() {
        return table;
    }

    public void setTable(SymbolLookup table) {
        this.table = table;
    }

    public void addParams(ArrayList<Variable> params) {
        for (Variable param : params) {
            table.addSymbolParam(param);
        }
    }

    public boolean isNative() {
        return table.getParent() == null;
    }
}
