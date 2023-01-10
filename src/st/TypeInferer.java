package st;

public class TypeInferer {
    public static Type inferType(SymbolLookup table, String expr) {
        if (expr.contains("-") || expr.contains("+") || expr.contains("*") || expr.contains("/")
                || isNumeric(expr) || expr.equals("int"))
            return new Primitive(Integer.class);
        if (table.getSymbol(expr) != null)
            return table.getSymbol(expr).getType();

        return null;
    }

    private static boolean isNumeric(String expr) {
        if (expr == null)
            return false;

        try {
            Integer.parseInt(expr);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }
}
