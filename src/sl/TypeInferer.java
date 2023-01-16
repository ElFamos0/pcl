package sl;

public class TypeInferer {
    public static Type inferType(SymbolLookup table, String expr) {
        String original = expr;
        if (expr == null) {
            return new Primitive(Void.class);
        }
        if (expr.startsWith("for"))
            return new Primitive(Void.class);

        expr = expr.trim();

        if (expr.startsWith("ifthen")) {
            String s = expr.substring(6, expr.length() - 1);

            if (s.contains("else")) {
                String[] split = s.split("else");
                Type t1 = inferType(table, split[0]);
                Type t2 = inferType(table, split[1]);
                if (t1 == null || t2 == null || !t1.equals(t2)) {
                    return null;
                }

                return t1;
            }

            return inferType(table, s);
        }
        if (expr.startsWith("record {")) {
            Record r = new Record();
            String[] fields = expr.substring(8, expr.length() - 1).split(",");
            for (String field : fields) {
                String[] split = field.split(":", 2);
                Symbol s = new Variable(split[0].trim(), inferType(table, split[1].trim()));
                r.addField(s);
            }
            return r;
        }
        if (expr.contains("0x88")) {
            String[] split = expr.split("0x88");
            Type t1 = inferType(table, split[0]);

            for (int i = 1; i < split.length; i++) {
                Type t2 = inferType(table, split[i]);
                if (t1 == null || t2 == null || !t1.equals(t2)) {
                    return null;
                }
            }

            return t1;
        }
        if (expr.contains("0x87")) {
            String[] split = expr.split("0x87");
            Type t1 = inferType(table, split[0]);

            for (int i = 1; i < split.length; i++) {
                Type t2 = inferType(table, split[i]);
                if (t1 == null || t2 == null || !t1.equals(t2)) {
                    return null;
                }
            }

            return t1;
        }
        if (expr.startsWith("`") && expr.endsWith("`")) {
            return new Array(new Primitive(Character.class));
        }
        if (expr.startsWith("\"") && expr.endsWith("\"")) {
            return new Array(new Primitive(Character.class));
        }
        if (expr.contains("0x80")
                && (!expr.contains("[") || expr.contains("[") && expr.indexOf("0x80") < expr.indexOf("["))) {
            String[] split2 = expr.split("0x80");
            Type t1 = inferType(table, split2[0]);

            for (int i = 1; i < split2.length; i++) {
                Type t2 = inferType(table, split2[i]);
                if (t1 == null || t2 == null || !t1.equals(t2)) {
                    return null;
                }
            }

            return t1;
        }
        if (expr.contains("0x81")
                && (!expr.contains("[") || expr.contains("[") && expr.indexOf("0x81") < expr.indexOf("["))) {
            String[] split2 = expr.split("0x81");
            Type t1 = inferType(table, split2[0]);

            for (int i = 1; i < split2.length; i++) {
                Type t2 = inferType(table, split2[i]);
                if (t1 == null || t2 == null || !t1.equals(t2)) {
                    return null;
                }
            }

            return t1;
        }
        if (expr.contains("0x82")
                && (!expr.contains("[") || expr.contains("[") && expr.indexOf("0x82") < expr.indexOf("["))) {
            String[] split2 = expr.split("0x82");
            Type t1 = inferType(table, split2[0]);

            for (int i = 1; i < split2.length; i++) {
                Type t2 = inferType(table, split2[i]);
                if (t1 == null || t2 == null || !t1.equals(t2)) {
                    return null;
                }
            }

            return t1;
        }
        if (expr.contains("0x83")
                && (!expr.contains("[") || expr.contains("[") && expr.indexOf("0x83") < expr.indexOf("["))) {
            String[] split2 = expr.split("0x83");
            Type t1 = inferType(table, split2[0]);

            for (int i = 1; i < split2.length; i++) {
                Type t2 = inferType(table, split2[i]);
                if (t1 == null || t2 == null || !t1.equals(t2)) {
                    return null;
                }
            }

            return t1;
        }

        while (expr.startsWith("(") && expr.endsWith(")")) {
            expr = expr.substring(expr.indexOf("(") + 1, expr.lastIndexOf(")"));
            String[] split = expr.split(";");
            expr = split[split.length - 1];
        }
        if (expr.contains("[") && expr.endsWith("]")) {
            expr = expr.substring(0, expr.indexOf("["));
        }

        if (table.getSymbol(expr) != null) {
            if (original.contains("[") && original.endsWith("]")) {
                Array a = (Array) table.getSymbol(expr).getType();
                return a.getType();
            }
            return table.getSymbol(expr).getType();
        }
        if (table.getType(expr) != null) {
            return table.getType(expr);
        }
        if (expr.startsWith("function") && expr.contains(":")) {
            String[] split = expr.split(":");
            return inferType(table, split[1]);
        }
        if (expr.contains("of") && expr.contains("[") && expr.contains("]")) {
            return table.getType(expr.substring(0, expr.indexOf("[")));
        }
        if (expr.contains("array"))
            return new Array(inferType(table, expr.substring(7, expr.length())));
        if (expr.equals("()") || expr.isEmpty())
            return new Primitive(Void.class);
        if (expr == "nil") {
            Record r = new Record();
            r.setIsNil(true);
            return r;
        }

        if (isNumeric(expr) || expr.equals("int") || expr.contains(":"))
            return new Primitive(Integer.class);

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
