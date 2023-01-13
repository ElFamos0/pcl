package test;

import sl.*;

public class TypeInfererTest {
    public static void main(String[] args) {
        SymbolLookup table = new SymbolLookup(null);
        try {
            table.addSymbolVarAndFunc(new Variable("a", new Primitive(Integer.class)));
            table.addSymbolVarAndFunc(new Variable("b", new Array(new Primitive(Character.class))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        testSeqList(table);
        testArray(table);
        testOp(table);
    }

    public static void testSeqList(SymbolLookup table) {
        System.out.println("-- Testing sequence lists --");
        String expr = "(5+6; \"yo\"; 13)";
        Type t = null;
        t = TypeInferer.inferType(table, expr);

        if (t instanceof Primitive) {
            Primitive p = (Primitive) t;
            System.out.println("Expected INT : " + p.getType());
        }

        expr = "(5+6; \"yo\"; \"13\")";
        t = TypeInferer.inferType(table, expr);

        if (t instanceof Array) {
            Array a = (Array) t;
            Primitive p = (Primitive) a.getType();
            System.out.println("Expected CHAR : " + p.getType());
        }

        expr = "()";
        t = TypeInferer.inferType(table, expr);

        if (t instanceof Primitive) {
            Primitive p = (Primitive) t;
            System.out.println("Expected VOID : " + p.getType());
        }

        expr = "(5+6; \"yo\"; a)";
        t = TypeInferer.inferType(table, expr);
        if (t instanceof Primitive) {
            Primitive p = (Primitive) t;
            // Expected INT because a is of type INT
            System.out.println("Expected INT : " + p.getType());
        }
    }

    public static void testArray(SymbolLookup table) {
        System.out.println("-- Testing arrays --");
        String expr = "arrayofint";
        Type t = null;
        t = TypeInferer.inferType(table, expr);

        if (t instanceof Array) {
            Array a = (Array) t;
            Primitive p = (Primitive) a.getType();
            System.out.println("Expected INT : " + p.getType());
        }

        expr = "arrayofstring";
        t = TypeInferer.inferType(table, expr);

        if (t instanceof Array) {
            Array a = (Array) t;
            Array b = (Array) a.getType();
            Primitive p = (Primitive) b.getType();
            System.out.println("Expected CHAR : " + p.getType());
        }
    }

    public static void testOp(SymbolLookup table) {
        System.out.println("-- Testing operators --");
        String expr = "5+6";
        Type t = null;
        t = TypeInferer.inferType(table, expr);

        if (t instanceof Primitive) {
            Primitive p = (Primitive) t;
            System.out.println("Expected INT : " + p.getType());
        }

        expr = "a+a";
        t = TypeInferer.inferType(table, expr);

        if (t instanceof Primitive) {
            Primitive p = (Primitive) t;
            System.out.println("Expected INT : " + p.getType());
        }

        expr = "b+b";
        t = TypeInferer.inferType(table, expr);

        if (t instanceof Array) {
            Array a = (Array) t;
            Primitive p = (Primitive) a.getType();
            System.out.println("Expected CHAR : " + p.getType());
        }

        expr = "a+b";
        t = TypeInferer.inferType(table, expr);

    }
}
