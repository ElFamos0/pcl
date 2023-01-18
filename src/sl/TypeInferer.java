package sl;

import javax.net.ssl.TrustManagerFactorySpi;

import org.antlr.v4.runtime.tree.ParseTree;

import ast.Ast;
import ast.AstType;
import csem.CSemType;

public class TypeInferer {
    private AstType astType = new AstType(null);
    private CSemType cSemType = new CSemType(null);

    public Type inferType(SymbolLookup table, Ast expr) {
        cSemType.setTable(table);

        return expr.accept(cSemType);
    }

    public Type inferType(SymbolLookup table, ParseTree expr) {
        astType.setTable(table);

        return expr.accept(astType);
    }

    public static boolean isNumeric(String expr) {
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
