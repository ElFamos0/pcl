package csem;

import sl.SymbolLookup;
import sl.TypeInferer;
import sl.Primitive;

public class OpCSem {
    public static void checkint(String left,String right, SymbolLookup table) {
        String split[] = left.split(":");

        if (split.length == 1){
            String s = split[0];
        if(table.getSymbol(s) != null) {
            if (!(table.getSymbol(s).getType().equals(new Primitive(Integer.class)))){
                System.out.println("Symbol " + s + " is not an integer");
            }
        }
        else{ 
            if (!(TypeInferer.inferType(table, s).equals(new Primitive(Integer.class)))){
                System.out.println("Value : " + s + " is not an integer");
            }
        }
    }

    if (right == null) {
        return;
    }
    String split2[] = right.split(":");

    if (split2.length == 1){
        String s2 = split2[0];
        if(table.getSymbol(s2) != null) {
            if (!(table.getSymbol(s2).getType().equals(new Primitive(Integer.class)))){
                System.out.println("Symbol : " + s2 + " is not an integer");
            }
        }
        else{ 
            if (!(TypeInferer.inferType(table, s2).equals(new Primitive(Integer.class)))){
                System.out.println("Value : " + s2 + " is not an integer");
            }
        }
    }
    }
}
