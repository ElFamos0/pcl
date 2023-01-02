package st;

public class Function extends FuncOrVariable {
    private SymbolTable table;

    public Function(String name, Type type, SymbolTable table) {
        super(name, type);
        this.table = table;
    }

    public SymbolTable getTable() {
        return table;
    }

    public boolean isNative() {
        return table.getParent() == null;
    }
}
