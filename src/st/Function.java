package st;

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

    public boolean isNative() {
        return table.getParent() == null;
    }
}
