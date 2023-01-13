package csem;

import sl.Symbol;
import sl.SymbolLookup;
import sl.Type;

public class FuncCSem {
    public static String checkFuncFromLib(String name) {
        String[] lib = { "print" };
        for (String s : lib) {
            if (s.equals(name)) {
                return "Error: function " + name + " is part of the standard library";
            }
        }

        return null;
    }

    public static String checkArgs(String arg, String type, SymbolLookup table) {
        Symbol s = table.getSymbol(arg);
        Type t = table.getType(type);

        if (s == null)
            return "Error: variable " + arg + " is not declared";
        if (t == null)
            return "Error: type " + type + " is not declared";

        return null;
    }
}
