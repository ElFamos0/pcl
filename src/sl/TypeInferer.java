package sl;

public class TypeInferer {
    public static Type inferType(SymbolLookup table, String expr) {
        if (expr == null) {
            return new Primitive(Void.class);
        }

        while (expr.contains("(")) {
            expr = expr.substring(expr.indexOf("(") + 1, expr.lastIndexOf(")"));
            String[] split = expr.split(";");
            expr = split[split.length - 1];
        }
        expr = expr.trim();

        if (table.getSymbol(expr) != null)
            return table.getSymbol(expr).getType();
        if (table.getType(expr) != null)
            return table.getType(expr);
        if (expr.contains("-") || expr.contains("*") || expr.contains("/")
                || isNumeric(expr) || expr.equals("int") || expr.contains("+"))
            return new Primitive(Integer.class);
        if (expr.contains("array"))
            return new Array(inferType(table, expr.substring(7, expr.length())));
        if (expr.equals("()") || expr.isEmpty())
            return new Primitive(Void.class);

        // Default to string
        return new Array(new Primitive(Character.class));
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
