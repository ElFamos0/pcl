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
                System.out.println("Symbol " + s + " is not an integer");
            }
        }
    }

        if(table.getSymbol(right) != null) {
            if (!(table.getSymbol(right).getType().equals(new Primitive(Integer.class)))){
                System.out.println("Symbol droit " + right + " is not an integer");
            }
        }
        else{ 
            if (!(TypeInferer.inferType(table, right).equals(new Primitive(Integer.class)))){
                System.out.println("Symbol droit" + right + " is not an integer");
            }
        }
    }
}
