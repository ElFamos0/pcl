package csem;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import ast.*;
import sl.Array;
import sl.Function;
import sl.Primitive;
import sl.Record;
import sl.Symbol;
import sl.SymbolLookup;
import sl.Type;
import sl.TypeInferer;

public class CSemVisitor implements AstVisitor<ParserRuleContext> {
    private SymbolLookup table;
    private int region;
    private int biggestRegion = 0;
    private ErrorHandler errorHandler;
    private TypeInferer tipe = new TypeInferer();
    private Record nilRecord = new Record();

    public CSemVisitor(SymbolLookup table, ErrorHandler errorHandler) {
        this.table = table;
        // permet de récuperer le premier offset (on l'a déjà oublié donc on le note)
        region = table.getLibFunc();
        biggestRegion = region;
        this.errorHandler = errorHandler;
        this.nilRecord.setIsNil(true);
    }

    public void StepOneRegion() {
        biggestRegion++;
        region = biggestRegion;
    }

    public void StepDownRegion() {
        region--;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public Boolean isOp(Ast a) {
        if (a instanceof Ou || a instanceof Et || a instanceof Compar || a instanceof Addition
                || a instanceof Soustraction
                || a instanceof Multiplication || a instanceof Division) {
            return true;
        }
        return false;
    }

    @Override
    public ParserRuleContext visit(Program a) {
        a.expression.accept(this);
        return null;
    }

    @Override
    public ParserRuleContext visit(Expression a) {
        a.left.accept(this);
        ParserRuleContext right = a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(region);

        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        if (!leftType.equals(rightType)) {
            errorHandler.error(right, "Type mismatch between types " + leftType + " and " + rightType);
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Ou a) {
        ParserRuleContext left = a.left.accept(this);
        ParserRuleContext right = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        // Check if left and right are integers

        Boolean rightIsOp = isOp(a.right);
        Boolean leftIsOp = isOp(a.left);

        if (!leftType.equals(new Primitive(Integer.class)) && !leftIsOp) {
            if (a.left instanceof ID) {
                ID id = (ID) a.left;
                errorHandler.error(left, id.nom + " is not an integer");
            } else {
                errorHandler.error(left, "this expression is not an integer");
            }
        }

        if (rightType != null && !rightType.equals(new Primitive(Integer.class)) && !rightIsOp) {
            if (a.right instanceof ID) {
                ID id = (ID) a.right;
                errorHandler.error(right, id.nom + " is not an integer");
            } else {
                errorHandler.error(right, "this expression is not an integer");
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Et a) {
        ParserRuleContext left = a.left.accept(this);
        ParserRuleContext right = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        // Check if left and right are integers

        Boolean rightIsOp = isOp(a.right);
        Boolean leftIsOp = isOp(a.left);

        if (!leftType.equals(new Primitive(Integer.class)) && !leftIsOp) {
            if (a.left instanceof ID) {
                ID id = (ID) a.left;
                errorHandler.error(left, id.nom + " is not an integer");
            } else {
                errorHandler.error(left, "this expression is not an integer");
            }
        }

        if (rightType != null && !rightType.equals(new Primitive(Integer.class)) && !rightIsOp) {
            if (a.right instanceof ID) {
                ID id = (ID) a.right;
                errorHandler.error(right, id.nom + " is not an integer");
            } else {
                errorHandler.error(right, "this expression is not an integer");
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Compar a) {
        ParserRuleContext left = a.left.accept(this);
        ParserRuleContext right = a.right.accept(this);
        String operator = a.operator;

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        // Check if left and right are integers or string

        if (operator.equals("<") || operator.equals(">") || operator.equals(">=") || operator.equals("<=")) {
            OpCSem.checkIntOrString(left, right, leftType, rightType, table, errorHandler);
        } else if (operator.equals("=") || operator.equals("<>")) {
            if (leftType != null && rightType != null && !leftType.equals(rightType)) {
                errorHandler.error(a.ctx, "Cannot compare different types");
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Addition a) {
        ParserRuleContext left = a.left.accept(this);
        ParserRuleContext right = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        // Check if left and right are integers

        Boolean rightIsOp = isOp(a.right);
        Boolean leftIsOp = isOp(a.left);

        if (!leftType.equals(new Primitive(Integer.class)) && !leftIsOp) {
            if (a.left instanceof ID) {
                ID id = (ID) a.left;
                errorHandler.error(left, id.nom + " is not an integer");
            } else {
                errorHandler.error(left, "this expression is not an integer");
            }
        }

        if (rightType != null && !rightType.equals(new Primitive(Integer.class)) && !rightIsOp) {
            if (a.right instanceof ID) {
                ID id = (ID) a.right;
                errorHandler.error(right, id.nom + " is not an integer");
            } else {
                errorHandler.error(right, "this expression is not an integer");
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Soustraction a) {
        ParserRuleContext left = a.left.accept(this);
        ParserRuleContext right = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        // Check if left and right are integers

        Boolean rightIsOp = isOp(a.right);
        Boolean leftIsOp = isOp(a.left);

        if (!leftType.equals(new Primitive(Integer.class)) && !leftIsOp) {
            if (a.left instanceof ID) {
                ID id = (ID) a.left;
                errorHandler.error(left, id.nom + " is not an integer");
            } else {
                errorHandler.error(left, "this expression is not an integer");
            }
        }

        if (!rightType.equals(new Primitive(Integer.class)) && !rightIsOp) {
            if (a.right instanceof ID) {
                ID id = (ID) a.right;
                errorHandler.error(right, id.nom + " is not an integer");
            } else {
                errorHandler.error(right, "this expression is not an integer");
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Multiplication a) {
        ParserRuleContext left = a.left.accept(this);
        ParserRuleContext right = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        // Check if left and right are integers

        Boolean rightIsOp = isOp(a.right);
        Boolean leftIsOp = isOp(a.left);

        if (!leftType.equals(new Primitive(Integer.class)) && !leftIsOp) {
            if (a.left instanceof ID) {
                ID id = (ID) a.left;
                errorHandler.error(left, id.nom + " is not an integer");
            } else {
                errorHandler.error(left, "this expression is not an integer");
            }
        }

        if (rightType != null && !rightType.equals(new Primitive(Integer.class)) && !rightIsOp) {
            if (a.right instanceof ID) {
                ID id = (ID) a.right;
                errorHandler.error(right, id.nom + " is not an integer");
            } else {
                errorHandler.error(right, "this expression is not an integer");
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Division a) {
        ParserRuleContext left = a.left.accept(this);
        ParserRuleContext right = a.right.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type leftType = tipe.inferType(table, a.left);
        Type rightType = tipe.inferType(table, a.right);

        // Check if left and right are integers

        Boolean rightIsOp = isOp(a.right);
        Boolean leftIsOp = isOp(a.left);

        if (!leftType.equals(new Primitive(Integer.class)) && !leftIsOp) {
            if (a.left instanceof ID) {
                ID id = (ID) a.left;
                errorHandler.error(left, id.nom + " is not an integer");
            } else {
                errorHandler.error(left, "this expression is not an integer");
            }
        }

        if (rightType != null && !rightType.equals(new Primitive(Integer.class)) && !rightIsOp) {
            if (a.right instanceof ID) {
                ID id = (ID) a.right;
                errorHandler.error(right, id.nom + " is not an integer");
            } else {
                errorHandler.error(right, "this expression is not an integer");
            }
        }

        // check division by zero
        if (a.right instanceof Int && ((Int) a.right).valeur.equals("0")) {
            errorHandler.error(right, "Division by zero");
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Sequence a) {
        for (Ast ast : a.seqs) {
            ast.accept(this);
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Negation a) {
        ParserRuleContext expr = a.expression.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        Type exprType = tipe.inferType(table, a.expression);

        // Check if left and right are integers
        if (!exprType.equals(new Primitive(Integer.class))) {
            if (a.expression instanceof ID) {
                ID id = (ID) a.expression;
                errorHandler.error(expr, id.nom + " is not an integer");
            } else {
                errorHandler.error(expr, "this expression is not an integer");
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ID a) {
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Int a) {
        SymbolLookup table = this.table.getSymbolLookup(region);

        if (table != null && a.alreadySeen == false) {
            a.alreadySeen = true;
            OpCSem.checkint(a, table, errorHandler);
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ExpressionIdentifiant a) {
        a.left.accept(this);

        if (a.right != null) {
            a.right.accept(this);
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(AppelFonction a) {
        ParserRuleContext idCtx = a.id.accept(this);
        ParserRuleContext argCtx = a.args.accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);

        if (table == null)
            return a.ctx;

        ID id = (ID) a.id;
        ArgFonction argF = (ArgFonction) a.args;
        ArrayList<Ast> args = argF.args;

        Function f = (Function) table.getSymbol(id.nom);

        if (table.getSymbol(id.nom) == null) {
            errorHandler.error(idCtx, "Function is not defined");
            return a.ctx;
        }

        // Verify that the function is not called with too many arguments
        if (args.size() != f.getParamsCount()) {
            errorHandler.error(argCtx, "Function " + id.nom + " expects " + f.getParamsCount() + " arguments, but "
                    + args.size() + " were given");
            return a.ctx;
        }

        // Verify params in inverted order
        for (int i = 0; i < args.size(); i++) {
            Type t = tipe.inferType(table, args.get(i));
            Type ft = f.getParams().get(args.size() - 1 - i).getType();

            if (!(ft.equals(t))) {
                errorHandler.error(argCtx, "Function " + id.nom + " expects argument " + (i + 1) + " to be of type "
                        + ft + ", but " + t + " was given");
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ArgFonction a) {
        for (Ast ast : a.args) {
            ast.accept(this);
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(IfThenElse a) {
        int temp = region;
        SymbolLookup table = this.table.getSymbolLookup(temp);
        ParserRuleContext cond = a.condition.accept(this);
        Type condType = tipe.inferType(table.getSymbolLookup(region), a.condition);

        StepOneRegion();
        table = this.table.getSymbolLookup(region);
        a.thenBlock.accept(this);
        Type thenType = tipe.inferType(table.getSymbolLookup(region), a.thenBlock);

        StepOneRegion();
        table = this.table.getSymbolLookup(region);
        ParserRuleContext els = a.elseBlock.accept(this);
        Type elseType = tipe.inferType(table.getSymbolLookup(region), a.elseBlock);

        // Check if condition is an integer
        if (!condType.equals(new Primitive(Integer.class))) {
            errorHandler.error(cond, "Condition is not an integer");
        }

        // OpCSem.checksametype(a.ctx, then, els, table, errorHandler);
        if (!thenType.equals(elseType)) {
            errorHandler.error(els, "Then and else blocks must return the same type");
        }

        region = temp;

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(IfThen a) {
        int temp = region;
        SymbolLookup table = this.table.getSymbolLookup(temp);

        ParserRuleContext cond = a.condition.accept(this);
        StepOneRegion();
        table = this.table.getSymbolLookup(region);
        a.thenBlock.accept(this);

        Type condType = tipe.inferType(table, a.condition);

        if (!condType.equals(new Primitive(Integer.class))) {
            errorHandler.error(cond, "Condition is not an integer");
        }

        region = temp;

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(While a) {
        int temp = region;
        SymbolLookup table = this.table.getSymbolLookup(temp);

        ParserRuleContext cond = a.condition.accept(this);
        Type condType = tipe.inferType(table, a.condition);

        if (!condType.equals(new Primitive(Integer.class))) {
            errorHandler.error(cond, "Condition is not an integer");
        }

        StepOneRegion();

        a.condition.accept(this);
        a.block.accept(this);

        region = temp;

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(For a) {
        int temp = region;
        SymbolLookup table = this.table.getSymbolLookup(region);
        a.start.accept(this);
        Type start = tipe.inferType(table, a.startValue);
        Type end = tipe.inferType(table, a.endValue);

        if (table != null) {
            BouclesCSem.checkint(a.ctx, start, table, errorHandler);
            BouclesCSem.checkint(a.ctx, end, table, errorHandler);
        }

        StepOneRegion();
        a.start.accept(this);
        a.startValue.accept(this);
        a.endValue.accept(this);
        a.block.accept(this);

        region = temp;

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Definition a) {
        int temp = region;
        StepOneRegion();
        for (Ast ast : a.declarations) {
            ast.accept(this);
        }
        for (Ast ast : a.exprs) {
            ast.accept(this);
        }

        region = temp;

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationType a) {
        a.id.accept(this);
        a.type.accept(this);

        // Check if identifier is a reserved word
        // if (idf.equals("array") || idf.equals("record")) {
        // errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        // }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationTypeClassique a) {
        ParserRuleContext type = a.id.accept(this);
        ID idf = (ID) a.id;

        SymbolLookup table = this.table.getSymbolLookup(region);

        // Check that type exists
        if (table.getType(idf.nom) == null) {
            errorHandler.error(type, "Type '" + idf.nom + "' is not defined");
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationArrayType a) {
        ParserRuleContext idf = a.id.accept(this);

        Type t = tipe.inferType(this.table.getSymbolLookup(region), a.id);

        if (t.equals(new Primitive(Void.class))) {
            errorHandler.error(idf, "Type '" + idf.getText() + "' is not defined");
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationRecordType a) {
        List<String> fields = new ArrayList<>();
        for (Ast ast : a.champs) {
            ast.accept(this);
            DeclarationChamp champ = (DeclarationChamp) ast;
            ID idf = (ID) champ.id;
            // ID type = (ID) champ.type;

            // Check for existence of the field
            if (fields.contains(idf.nom)) {
                errorHandler.error(idf.ctx, "Field '" + idf.nom + "' is redefined in record");
            }

            fields.add(idf.nom);
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationChamp a) {
        a.id.accept(this);
        ParserRuleContext type = a.type.accept(this);

        // Check if identifier is a reserved word
        // if (idf.equals("array") || idf.equals("record")) {
        // errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        // }

        Type t = tipe.inferType(this.table.getSymbolLookup(region), a.type);

        if (t.equals(new Primitive(Void.class))) {
            errorHandler.error(type, "Type '" + type.getText() + "' is not defined");
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationFonction a) {
        int temp = region;
        SymbolLookup table = this.table.getNearSymbolLookup(temp);
        StepOneRegion();
        ParserRuleContext idfCtx = a.id.accept(this);

        ID idf = (ID) a.id;

        if (!(table.getSymbol(idf.nom) instanceof Function)) {
            return a.ctx;
        }

        // Check if identifier is a reserved word
        // if (idf.equals("array") || idf.equals("record")) {
        // errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        // }

        for (Ast ast : a.args) {
            ast.accept(this);
        }

        if (a.has_return)
            a.return_type.accept(this);

        Function f = (Function) table.getSymbol(idf.nom);
        SymbolLookup table2 = f.getTable();
        if (table2 == null)
            return a.ctx;

        a.expr.accept(this);
        Type t = tipe.inferType(table2, a.expr);
        Type ft = table2.getSymbol(idf.nom).getType();

        if (ft == null || t == null || (!FuncCSem.isFuncFromLib(idf.nom) && !t.equals(ft) && !ft.equals(nilRecord))) {
            errorHandler.error(idfCtx, "Type mismatch in function declaration " + idf.nom + " : " + ft + " != " + t);
        }

        region = temp;

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationValeur a) {
        ParserRuleContext idfCtx = a.id.accept(this);
        a.expr.accept(this);
        Type t = null;
        SymbolLookup table = this.table.getSymbolLookup(region);
        ID idf = (ID) a.id;

        if (a.getType() != null) {
            t = tipe.inferType(table, a.getType());
        } else {
            Type tExpr = tipe.inferType(table, a.expr);
            if (tExpr instanceof Record && ((Record) tExpr).getIsNil())
                errorHandler.error(a.ctx, "Cannot assigned nil value to " + idf.nom + " because it is of type "
                        + new Primitive(Void.class));

            if (tExpr.equals(new Primitive(Void.class)))
                errorHandler.error(a.ctx, "Variable " + idf.nom + " cannot be of type "
                        + tExpr);
            return a.ctx;
        }

        // Check if identifier is a reserved word
        // if (idf.equals("array") || idf.equals("record")) {
        // errorHandler.error(a.ctx, "Identifier '" + idf + "' is a reserved word");
        // }

        if (table == null)
            return a.ctx;

        Type tExpr = tipe.inferType(table, a.expr);
        if (tExpr == null)
            return a.ctx;

        if (t != null && (!t.equals(tExpr) && !tExpr.equals(nilRecord))) {
            errorHandler.error(idfCtx, "Type mismatch in variable declaration " + idf.nom + " : " + t + " != " + tExpr);
        }

        if (t == null && tExpr.equals(new Primitive(Void.class))) {
            errorHandler.error(a.ctx, "nil has no type in this context or expression has not returned a value");
        }

        if (t != null && !(t instanceof Record) && tExpr.equals(nilRecord)) {
            errorHandler.error(a.ctx, "Cannot assigned nil value to " + idf.nom + " because it is of type " + t);
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ChaineChr a) {
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Nil a) {
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Break a) {
        BouclesCSem.checkInBoucle(a.ctx, table, errorHandler);
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(InstanciationType a) {
        ParserRuleContext idfCtx = a.getId().accept(this);
        ID idf = (ID) a.getId();

        SymbolLookup table = this.table.getSymbolLookup(region);
        Type t = table.getType(idf.nom);
        if (t == null) {
            errorHandler.error(idfCtx, "Type '" + idf.nom + "' not defined");
            return a.ctx;
        }

        Record r = new Record();
        if (t instanceof Record) {
            r = (Record) t;
        }
        List<String> fields = new ArrayList<String>();

        ArrayList<Ast> identifiants = a.getIdentifiants();
        ArrayList<Ast> expr = a.getExpressions();
        for (int i = 0; i < identifiants.size(); i++) {
            ParserRuleContext fieldCtx = identifiants.get(i).accept(this);
            String fieldname = ((ID) identifiants.get(i)).nom;

            expr.get(i).accept(this);

            // Check for existence of the field
            if (r.getField(fieldname) == null) {
                errorHandler.error(fieldCtx, "Field '" + fieldname + "' not defined in type '" + idf.nom + "'");
                continue;
            }

            // Check for type of the field
            Type fieldtype = r.getField(fieldname).getType();
            Type expType = tipe.inferType(table, expr.get(i));
            if (expType == null || !fieldtype.equals(expType)) {
                errorHandler.error(fieldCtx,
                        "Type mismatch in field '" + fieldname + "' : " + fieldtype + " != " + expType);
            }

            fields.add(fieldname);
        }

        // List missing fields
        for (Symbol field : r.getFields()) {
            if (!fields.contains(field.getName())) {
                errorHandler.error(a.ctx,
                        "Field '" + field.getName() + "' not defined on instanciation of type '" + idf.nom + "'");
            }
        }

        if (t instanceof Record) {
            return a.ctx;
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(AccesChamp a) {
        return a.getChild().accept(this);
    }

    @Override
    public ParserRuleContext visit(ExpressionArray a) {
        a.getId().accept(this);
        ParserRuleContext sizeCtx = a.getSize().accept(this);
        ParserRuleContext exprCtx = a.getExpr().accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);

        ID idf = (ID) a.getId();
        Type idType = table.getType(idf.nom);

        // Check if of type array
        if (!(idType instanceof Array)) {
            errorHandler.error(a.ctx, "Type '" + idf.nom + "' is not an array type");
        }

        Type sizeType = tipe.inferType(table, a.getSize());
        // Check if size is of type int
        if (!sizeType.equals(new Primitive(Integer.class))) {
            errorHandler.error(sizeCtx, "Size of array must be of type int");
        }

        if (idType instanceof Array) {
            // Check that expression is of the right type
            Type exprType = tipe.inferType(table, a.getExpr());
            Type arrayType = ((Array) idType).getType();
            boolean state = true;
            if (exprType == null || !exprType.equals(arrayType)) {
                if (exprType instanceof Record && arrayType instanceof Record) {
                    state = !((Record) exprType).getIsNil();
                }
                if (state) {
                    errorHandler.error(exprCtx,
                            "Type mismatch in array declaration : " + arrayType + " != " + exprType);
                }
            }
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ListeAcces a) {
        ParserRuleContext idfCtx = a.getId().accept(this);
        SymbolLookup table = this.table.getSymbolLookup(region);
        ID idf = (ID) a.getId();
        Symbol s = table.getSymbol(idf.nom);
        if (s == null) {
            errorHandler.error(idfCtx, "Variable '" + idf.nom + "' not defined");
            return a.ctx;
        }
        Type t = s.getType();
        if (a.getisExpressionArray()) {
            ParserRuleContext idk = a.getExpressionArray().accept(this);
            return idk;
        } else {
            String out = "";
            for (Ast ast : a.getAccesChamps()) {
                ParserRuleContext field = ast.accept(this);
                AccesChamp ac = (AccesChamp) ast;

                if (t instanceof Record) {
                    String fieldname = ((ID) ac.getChild()).nom;

                    if (((Record) t).getIsNil()) {
                        errorHandler.error(field, idf + out + "' is nil");
                        break;
                    } else {
                        Record r = (Record) t;
                        if (r.getField(fieldname) == null) {
                            errorHandler.error(field, "Field '" + fieldname + "' not defined in type '" + t + "'");
                            break;
                        }
                        t = r.getField(fieldname).getType();
                        out += "." + field;
                    }
                } else if (t instanceof Array) {
                    Array a1 = (Array) t;
                    // Check if field is an integer, positive and less than size of array
                    if (!tipe.inferType(table, ast).equals(new Primitive(Integer.class))) {
                        errorHandler.error(field, "Index of array must be an integer");
                        break;
                    }
                    if (TypeInferer.isNumeric(field.getText()) && Integer.parseInt(field.getText()) < 0) {
                        errorHandler.error(field, "Index of array must be positive");
                        break;
                    }
                    t = a1.getType();
                    out += "[" + field + "]";
                } else {
                    errorHandler.error(a.ctx, "Type '" + t + "' is not a record or an array");
                    break;
                }
            }
            // return idf.field1.field2...
            return a.ctx;
        }
    }

}
