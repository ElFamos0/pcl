package st;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Symbol> funcAndVar = new HashMap<String, Symbol>();
    private HashMap<String, Type> types = new HashMap<String, Type>();
    private int scope;
    private SymbolTable parent;
    private ArrayList<SymbolTable> children = new ArrayList<SymbolTable>();

    public SymbolTable(SymbolTable parent) {
        this.scope = parent != null ? parent.getScope() + 1 : 0;
        this.parent = parent;
    }

    public Symbol getSymbol(String name) {
        return funcAndVar.get(name);
    }

    public Type getType(String name) {
        return types.get(name);
    }

    public int getScope() {
        return scope;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public ArrayList<SymbolTable> getChildren() {
        return children;
    }

    public void addSymbol(Symbol s) {
        funcAndVar.put(s.getName(), s);
    }
}