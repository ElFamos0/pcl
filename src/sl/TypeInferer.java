package sl;

public class TypeInferer {
    public static Type inferType(SymbolLookup table, String expr) {
        while (expr.contains("(")) {
            expr = expr.substring(expr.indexOf("(") + 1, expr.lastIndexOf(")"));
            String[] split = expr.split(";");
            expr = split[split.length - 1];
        }
        expr = expr.trim();
        expr = expr.split(" ")[0].split("\\[")[0];

        if (table.getSymbol(expr) != null)
            return table.getSymbol(expr).getType();
        if (table.getType(expr) != null)
            return table.getType(expr);
        if (expr.contains("-") || expr.contains("*") || expr.contains("/")
                || isNumeric(expr) || expr.equals("int"))
            return new Primitive(Integer.class);
        if (expr.contains("+")) {
            String[] split = expr.split("\\+");
            System.out.println(inferType(table, split[0]));
            if (inferType(table, split[0]) != inferType(table, split[1]))
                throw new RuntimeException(String.format("Type mismatch: %s + %s", split[0], split[1]));

            return inferType(table, split[0]);
        }
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
