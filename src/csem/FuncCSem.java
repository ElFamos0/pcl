package csem;

import sl.Symbol;
import sl.SymbolLookup;
import sl.Type;

public class FuncCSem {
    public static void checkFuncFromLib(String name) {
        String[] lib = { "print" };
        for (String s : lib) {
            if (s.equals(name)) {
                System.out.println("Error: function " + name + " is part of the standard library");
            }
        }
    }

    public static void checkArgs(String arg, String type, SymbolLookup table) {
        Symbol s = table.getSymbol(arg);
        Type t = table.getType(type);

        if (s == null)
            System.out.println("Error: variable " + arg + " is not declared");
        if (t == null)
            System.out.println("Error: type " + type + " is not declared");
    }
}
