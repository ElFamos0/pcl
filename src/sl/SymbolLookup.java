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
    private ArrayList<Variable> params;

    public SymbolLookup(SymbolLookup parent) {
        this.scope = parent != null ? parent.getScope() + 1 : 0;
        this.region = regionCount++;
        this.parent = parent;
        funcAndVar = new HashMap<String, Symbol>();
        types = new HashMap<String, Type>();
        children = new ArrayList<SymbolLookup>();
        params = new ArrayList<Variable>();

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
        // Split name by .
        String[] names = name.split("\\.");

        if (funcAndVar.containsKey(names[0])) {
            // If there is other names we check the fields
            if (names.length > 1) {
                // Go as deep as we can
                Symbol s = funcAndVar.get(names[0]);
                for (int i = 1; i < names.length; i++) {
                    if (s.getType() instanceof Record) {
                        Record r = (Record) s.getType();
                        if (r.hasField(names[i])) {
                            s = r.getField(names[i]);
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
                return s;
            } else {
                return funcAndVar.get(names[0]);
            }
        } else if (parent != null) {
            return parent.getSymbol(names[0]);
        } else {
            return null;
        }
    }

    public Symbol getSymbolInScope(String name) {
        String[] names = name.split("\\.");
        if (funcAndVar.containsKey(names[0])) {
            // If there is other names we check the fields
            if (names.length > 1) {
                // Go as deep as we can
                Symbol s = funcAndVar.get(names[0]);
                for (int i = 1; i < names.length; i++) {
                    if (s.getType() instanceof Record) {
                        Record r = (Record) s.getType();
                        if (r.hasField(names[i])) {
                            s = r.getField(names[i]);
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
                return s;
            } else {
                return funcAndVar.get(names[0]);
            }
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

    public ArrayList<Variable> getParams() {
        return params;
    }

    public void addSymbolVarAndFunc(Symbol s) {
        funcAndVar.put(s.getName(), s);
        if (s instanceof Variable) {
            ((Variable) s).setOffset(varOffset);

            varOffset += s.getType().getOffset();
        }
        if (s instanceof Function) {
            // Get the last children
            ((Function) s).setTable(getChildren(-1));
        }
    }

    public void addSymbolParam(Variable s) {
        s.setOffset(paramOffset);
        paramOffset -= s.getType().getOffset();
        params.add(s);

        funcAndVar.put(s.getName(), s);
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