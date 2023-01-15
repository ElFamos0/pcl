package sl;

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

    public int getParamsCount() {
        if (table == null) {
            return 0;
        }
        return table.getParams().size();
    }

    public ArrayList<Variable> getParams() {
        return table.getParams();
    }

    public void addParams(ArrayList<Variable> params) {
        for (Variable param : params) {
            table.addSymbolParam(param);
        }
    }

    public boolean isNative() {
        return table.getParent() == null;
    }

    public String toString() {
        return "function";
    }
}
