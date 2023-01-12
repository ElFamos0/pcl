package sl;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolLookup {
    private HashMap<String, Symbol> funcAndVar;
    private HashMap<String, Type> types;
    private int scope;
    private int region;
    private int varOffset = 0;
    private int paramOffset = 0;
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

        if (parent == null) {
            initLib();
        }
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
        if (types.containsKey(name)) {
            return types.get(name);
        } else if (parent != null) {
            return parent.getType(name);
        } else {
            return null;
        }
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

    public void addSymbolVarAndFunc(Symbol s) {
        if (s instanceof Variable) {
            ((Variable) s).setOffset(varOffset);

            varOffset += s.getType().getSize();
        }
        if (s instanceof Function) {
            // Get the last children
            ((Function) s).setTable(getChildren(-1));
        }
        Symbol temp = funcAndVar.put(s.getName(), s);
        if (temp != null) {
            System.out.println("Error: " + s.toString() + " " + s.getName() + " already exists");
        }
    }

    public void addSymbolParam(Variable s) {
        ((Variable) s).setOffset(paramOffset);
        paramOffset -= s.getType().getSize();

        Symbol temp = funcAndVar.put(s.getName(), s);
        if (temp != null) {
            System.out.println("Error: arg " + s.getName() + " already exists");
        }
    }

    public void addType(String key, Type t) {
        types.put(key, t);
    }

    public void addChildren() {
        SymbolLookup child = new SymbolLookup(this);
        children.add(child);
    }

    private void initLib() {
        funcAndVar.put("print", new Function("print", new Primitive(Void.class)));
        types.put("int", new Primitive(Integer.class));
        types.put("string", new Array(new Primitive(Character.class)));
        types.put("void", new Primitive(Void.class));
    }
}