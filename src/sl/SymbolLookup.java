package sl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SymbolLookup {
    private HashMap<String, Symbol> funcAndVar;
    private HashMap<String, Type> types;
    private int scope;
    private int region;
    private int varOffset = -4;
    private int libFunc = 0;
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

    public SymbolLookup getNearSymbolLookup(int region) {
        SymbolLookup result = null;

        while (result == null) {
            result = getSymbolLookup(region);
            region++;
        }

        return result;
    }

    public int getLibFunc() {
        return libFunc;
    }

    public String toString() {
        String indent = "";
        ArrayList<Integer> regions = new ArrayList<Integer>();

        for (int i = 0; i < scope; i++) {
            indent += "  ";
        }

        String result = indent + "Scope " + scope + " (region " + region + ")\n";

        for (String name : funcAndVar.keySet()) {
            if (funcAndVar.get(name) instanceof Variable) {
                Variable v = (Variable) funcAndVar.get(name);
                result += indent + "  " + "variable " + name + " : " + funcAndVar.get(name).getType() + " offset "
                        + v.getOffset() + "\n";
            }
        }

        result += "\n";

        for (String name : funcAndVar.keySet()) {
            if (funcAndVar.get(name) instanceof Function) {
                Function f = (Function) funcAndVar.get(name);
                result += indent + "  " + "function " + name + " : " + funcAndVar.get(name).getType() + "\n";
                result += f.getTable().toString();
                regions.add(f.getTable().getRegion());
            }
        }

        for (SymbolLookup child : children) {
            if (!regions.contains(child.getRegion()))
                result += child.toString();
        }

        return result;
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

    public Type getTypeScope(String name) {
        if (types.containsKey(name)) {
            return types.get(name);
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

    public void removeChildren(int i) {
        children.remove(i);
    }

    public ArrayList<Variable> getParams() {
        ArrayList<Variable> result = new ArrayList<Variable>();

        for (Symbol s : funcAndVar.values()) {
            if (s instanceof Variable && ((Variable) s).getOffset() > 0) {
                result.add((Variable) s);
            }
        }

        // Sort result by offset
        Collections.sort(result, new Comparator<Variable>() {
            public int compare(Variable v1, Variable v2) {
                return v1.getOffset() - v2.getOffset();
                // To flip it
                // return v2.getOffset() - v1.getOffset();
            }
        });

        return result;
    }

    public void addSymbolVarAndFunc(Symbol s) {
        funcAndVar.put(s.getName(), s);
        if (s instanceof Variable) {
            ((Variable) s).setOffset(varOffset);

            if (((Variable) s).getType() instanceof Array) {
                varOffset -= ((Array) s.getType()).getSize() * 4;
            } else {
                varOffset -= 4;
            }
        }
        if (s instanceof Function) {
            // Get the last children
            ((Function) s).setTable(getChildren(-1));
        }
    }

    public void addSymbolParam(Variable s) {
        funcAndVar.put(s.getName(), s);
    }

    public void addType(String key, Type t) {
        types.put(key, t);
    }

    public Integer addChildren() {
        SymbolLookup child = new SymbolLookup(this);
        children.add(child);
        return child.getRegion();
    }

    private void initLib() {
        Function print = new Function("print", new Primitive(Void.class));
        ArrayList<Variable> params = new ArrayList<Variable>();
        params.add(new Variable("string", new Primitive(Any.class)));
        this.addChildren();
        this.addSymbolVarAndFunc(print);
        print.addParams(params);

        Function random = new Function("random", new Primitive(Integer.class));
        this.addChildren();
        this.addSymbolVarAndFunc(random);

        Function not = new Function("not", new Primitive(Integer.class));
        params = new ArrayList<Variable>();
        params.add(new Variable("int", new Primitive(Integer.class)));
        this.addChildren();
        this.addSymbolVarAndFunc(not);
        not.addParams(params);

        libFunc = 3;

        types.put("int", new Primitive(Integer.class));
        types.put("string", new Array(new Primitive(Character.class)));
        types.put("void", new Primitive(Void.class));
    }

    public int getVarOffset(String var) {
        if (funcAndVar.containsKey(var)) {
            return 0;
        } else if (parent == null) {
            return -1;
        } else {
            return parent.getVarOffset(var) + 1;
        }
    }
}