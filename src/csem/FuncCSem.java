package csem;

import sl.Symbol;
import sl.SymbolLookup;
import sl.Type;

public class FuncCSem {
    private static String[] lib = { "print", "not" };

    public static String checkFuncFromLib(String name) {
        for (String s : lib) {
            if (s.equals(name)) {
                return "function " + name + " is part of the standard library";
            }
        }

        return null;
    }

    public static boolean isFuncFromLib(String name) {
        for (String s : lib) {
            if (s.equals(name)) {
                return true;
            }
        }

        return false;
    }

    public static String checkArgs(String arg, String type, SymbolLookup table) {
        Symbol s = table.getSymbol(arg);
        Type t = table.getType(type);

        if (s == null)
            return "variable " + arg + " is not declared";
        if (t == null)
            return "type " + type + " is not declared";

        return null;
    }
}
