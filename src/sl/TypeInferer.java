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

        if (table.getSymbol(expr) != null) {
            return table.getSymbol(expr).getType();
        }
        if (table.getType(expr) != null) {
            return table.getType(expr);
        }
        if (expr.startsWith("record {")) {
            Record r = new Record();
            String[] fields = expr.substring(8, expr.length() - 1).split(",");
            for (String field : fields) {
                String[] split = field.split(":",2);
                Symbol s = new Variable(split[0].trim(), inferType(table, split[1].trim()));
                r.addField(s);
            }
            return r;
        }
        if (expr.contains("-") || expr.contains("*") || expr.contains("/")
                || isNumeric(expr) || expr.equals("int") || expr.contains("+") || expr.contains(":"))
            return new Primitive(Integer.class);
        if (expr.contains("array"))
            return new Array(inferType(table, expr.substring(7, expr.length())));
        if (expr.equals("()") || expr.isEmpty())
            return new Primitive(Void.class);
        if (expr == "nil") {
            Record r = new Record();
            r.setIsNil(true);
            return r;
        }
        if (expr.contains("[")) {
            String[] split = expr.split("\\[");
            String type = split[0];
            //System.out.println(table);
            if (table.getSymbol(type) != null) {
                return table.getSymbol(type).getType();
            }
            return table.getType(type);
        }
        if (expr.startsWith("`") && expr.endsWith("`")) {
            return new Array(new Primitive(Character.class));
        }
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return new Array(new Primitive(Character.class));
        }

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
