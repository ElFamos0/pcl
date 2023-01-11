package st;

public class TypeInferer {
    public static Type inferType(SymbolLookup table, String expr) {
        expr = expr.trim();
        expr = expr.split(" ")[0].split("\\[")[0];

        if (expr.contains("-") || expr.contains("+") || expr.contains("*") || expr.contains("/")
                || isNumeric(expr) || expr.equals("int"))
            return new Primitive(Integer.class);
        if (table.getSymbol(expr) != null)
            return table.getSymbol(expr).getType();
        if (table.getType(expr) != null)
            return table.getType(expr);
        if (expr.contains("array"))
            return new Array(inferType(table, expr.substring(7, expr.length())));
        if (expr.equals("string"))
            return new Array(new Primitive(Character.class));

        // Default to void
        return new Primitive(Void.class);
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
