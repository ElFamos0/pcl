package st;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolLookup {
    private HashMap<String, Symbol> funcAndVar;
    private HashMap<String, Type> types;
    private int scope;
    private int region;
    private int offset = 0;
    private static int regionCount = 0;
    private SymbolLookup parent;
    private ArrayList<SymbolLookup> children;

    public SymbolLookup(SymbolLookup parent) {
        this.scope = parent != null ? parent.getScope() + 1 : 0;
        this.region = regionCount++;
        this.parent = parent;
        funcAndVar = new HashMap<String, Symbol>();
        types = new HashMap<String, Type>();
        children = new ArrayList<SymbolLookup>();
    }

    public SymbolLookup getSymbolLookup(int region) {
        if (this.region == region) {
            return this;
        } else {
            for (SymbolLookup child : children) {
                SymbolLookup result = child.getSymbolLookup(region);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public Symbol getSymbol(String name) {
        if (funcAndVar.containsKey(name)) {
            return funcAndVar.get(name);
        } else if (parent != null) {
            return parent.getSymbol(name);
        } else {
            return null;
        }
    }

    public Type getType(String name) {
        return types.get(name);
    }

    public int getScope() {
        return scope;
    }

    public int getRegion() {
        return region;
    }

    public SymbolLookup getParent() {
        return parent;
    }

    public ArrayList<SymbolLookup> getChildren() {
        return children;
    }

    public SymbolLookup getChildren(int i) {
        if (i == -1)
            return children.get(children.size() - 1);
        else
            return children.get(i);
    }

    public void addSymbol(Symbol s) {
        if (s instanceof Variable) {
            ((Variable) s).setOffset(offset);
            offset += s.getType().getSize();
        }
        if (s instanceof Function) {
            // Get the last children
            ((Function) s).setTable(getChildren(-1));
        }
        funcAndVar.put(s.getName(), s);
    }

    public void addChildren() {
        SymbolLookup child = new SymbolLookup(this);
        children.add(child);
    }
}