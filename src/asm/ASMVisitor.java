package asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import ast.*;
import parser.exprParser.*;
import sl.Array;
import sl.Primitive;
import sl.Symbol;
import sl.SymbolLookup;
import sl.Type;
import sl.TypeInferer;
import sl.Variable;

public class ASMVisitor implements AstVisitor<ParserRuleContext> {
    
    private SymbolLookup table;
    private int region;
    private int biggestRegion = 0;
    private TypeInferer type = new TypeInferer();

    private ASMWriter writer;

    private Register BasePointer = new Register("r11", 0);
    private Register StackPointer = new Register("r13", 0);
    private Register LinkRegister = new Register("r14", 0);
    private Register ProgramCounter = new Register("r15", 0);

    private Register r0 = new Register("r0", 0);
    private Register r1 = new Register("r1", 0);
    private Register r2 = new Register("r2", 0);
    private Register r3 = new Register("r3", 0);
    private Register r4 = new Register("r4", 0);
    private Register r5 = new Register("r5", 0);
    private Register r6 = new Register("r6", 0);
    private Register r7 = new Register("r7", 0);
    private Register r8 = new Register("r8", 0);
    private Register r9 = new Register("r9", 0);

    private List<Constant> constants = new ArrayList<Constant>();

    public ASMVisitor(SymbolLookup table, ASMWriter writer) {
        this.table = table;
        // permet de récuperer le premier offset (on l'a déjà oublié donc on le note)
        region = table.getLibFunc();
        biggestRegion = region;
        this.writer = writer;
    }

    public String getLabel() {
        String label = "_blk_";

        SymbolLookup sl = this.table.getSymbolLookup(this.region);

        label += sl.getScope() + "_" + sl.getRegion();

        return label;
    }

    public String[] getSonsLabels() {
        SymbolLookup sl = this.table.getSymbolLookup(this.region);
        String[] labels = new String[sl.getChildren().size()];

        for (int i = 0; i < sl.getChildren().size(); i++) {
            labels[i] = "_blk_" + sl.getChildren().get(i).getScope() + "_" + sl.getChildren().get(i).getRegion();
        }

        return labels;
    }

    public void initStack() {
        // Init stack
        writer.write(".text\n");
        writer.write(".globl main\n");
        writer.Label("main");
        writer.Comment("Init stack", 1);

        writer.SkipLine();
        writer.Comment("Add stack pointer in base pointer", 1);
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        
        String label = "_blk_1_" + (this.region + 1);
        writer.Bl(label, Flags.NI);
        writer.SkipLine();
        writer.Bl("exit", Flags.NI);
    }

    public void StepOneRegion() {
        biggestRegion++;
        region = biggestRegion;
    }

    public void StepDownRegion() {
        region--;
    }

    @Override
    public ParserRuleContext visit(Program a) {
        // System.out.println("Program");
        initStack();

        a.expression.accept(this);

        writer.SkipLine();
        writer.StackVar();

        writer.SkipLine();
        writer.Comment("syscall exit(int status = 0)", 0);
        writer.Label("exit");
        writer.Exit(0);
        writer.SkipLine();

        writer.write(".data\n");
        writer.write("\tformat_str: .ascii      \"%s\\n\\0\"\n");
        writer.write("\tformat_int: .ascii      \"%d\\n\\0\"\n");

        for (Constant c : constants) {
            writer.write("\t" + c.toASM() + "\n");
        }

        return null;
    }

    @Override
    public ParserRuleContext visit(Expression a) {
        // System.out.println("Expression");
        a.left.accept(this);
        a.right.accept(this);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Ou a) {
        // System.out.println("Ou");
        a.left.accept(this);
        a.right.accept(this);

        // Generate arm code
        // Load two last values in the stack in R0 and R1.
        // We have :
        //      R0 = a.right
        //      R1 = a.left
        Register[] load_register = { r0, r1 };
        writer.Ldmfd(StackPointer, load_register);

        // ORR R0 and R1
        writer.Orr(r0, r0, r1, Flags.NI);

        // Store R0 in the stack
        Register[] store_registers = { r0 };
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Et a) {
        // System.out.println("Et");
        a.left.accept(this);
        a.right.accept(this);

        // Generate arm code
        // Load two last values in the stack in R0 and R1.
        // We have :
        //      R0 = a.right
        //      R1 = a.left
        Register[] load_register = { r0, r1 };
        writer.Ldmfd(StackPointer, load_register);

        // AND R0 and R1
        writer.And(r0, r0, r1, Flags.NI);

        // Store R0 in the stack
        Register[] store_registers = { r0 };
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Compar a) {
        // System.out.println("Compar");
        ParserRuleContext left = a.left.accept(this);
        a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(this.region);

        Type leftType = this.type.inferType(table, left);

        if (leftType.equals(new Primitive(Integer.class))) {
            // We have to compare two integers

            Register[] load_register = { r0, r1 };
            Register[] store_registers = { r0 };

            // Load two last values in the stack in R0 and R1.
            // We have :
            //      R0 = a.right
            //      R1 = a.left
            writer.Ldmfd(StackPointer, load_register);

            // CMP R0 and R1 following the operator.
            switch (a.operator) {
            case "<":
                // CMP R0 and R1
                // CMP does R0 - R1
                // We need here R0 > R1
                writer.Cmp(r1, r0);

                // Store the N flag in R0
                // N flag is set if R0 > R1

                // Set R0 to 1 if N flag is not set.
                writer.Mov(r0, 1, Flags.PL);

                // Set R0 to 0 if N flag is set.
                writer.Mov(r0, 0, Flags.MI);

                // Set R0 to 0 if Z flag is set because we want R0 != R1.
                writer.Mov(r0, 0, Flags.EQ);

                // Store R0 in the stack
                writer.Stmfd(StackPointer, store_registers);
                break;
            case ">":
                // CMP R0 and R1
                // CMP does R0 - R1
                // We need here R0 < R1
                writer.Cmp(r1, r0);

                // Set R0 to 1 if N flag is set.
                writer.Mov(r0, 1, Flags.MI);

                // Set R0 to 0 otherwise.
                writer.Mov(r0, 0, Flags.PL);

                // Store R0 in the stack
                writer.Stmfd(StackPointer, store_registers);
                break;
            case "=":
                // CMP R0 and R1
                // CMP does R0 - R1
                // We need here R0 = R1
                writer.Cmp(r1, r0);

                // Set R0 to 1 if Z flag is set.
                writer.Mov(r0, 1, Flags.EQ);

                // Set R0 to 0 otherwise.
                writer.Mov(r0, 0, Flags.NE);

                // Store R0 in the stack
                writer.Stmfd(StackPointer, store_registers);
                break;
            case "<=":
                // CMP R0 and R1
                // CMP does R0 - R1
                // We need here R0 >= R1
                writer.Cmp(r1, r0);

                // Set R0 to 1 if N flag is not set.
                writer.Mov(r0, 1, Flags.PL);

                // Set R0 to 0 if N flag is set.
                writer.Mov(r0, 0, Flags.MI);

                // Store R0 in the stack
                writer.Stmfd(StackPointer, store_registers);
                break;
            case ">=":
                // CMP R0 and R1
                // CMP does R1 - R0
                // We need here R1 >= R0
                writer.Cmp(r0, r1);

                // Set R0 to 1 if N flag is not set.
                writer.Mov(r0, 1, Flags.PL);

                // Set R0 to 0 if N flag is set.
                writer.Mov(r0, 0, Flags.MI);

                // Store R0 in the stack
                writer.Stmfd(StackPointer, store_registers);
                break;
            case "<>":
                // CMP R0 and R1
                // CMP does R0 - R1
                // We need here R0 != R1
                writer.Cmp(r1, r0);

                // Set R0 to 1 if Z flag is not set.
                writer.Mov(r0, 1, Flags.NE);

                // Set R0 to 0 if Z flag is set.
                writer.Mov(r0, 0, Flags.EQ);

                // Store R0 in the stack
                writer.Stmfd(StackPointer, store_registers);
                break;
            default:
                throw new UnsupportedOperationException("Unimplemented operator '" + a.operator + "'");
            }
        }


        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Addition a) {
        // System.out.println("Addition");
        a.left.accept(this);
        a.right.accept(this);

        // If we have an ID in the left, we have to load it in R0
        if (a.left instanceof ID) {
            ID id = (ID) a.left;

            int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
            Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
                
            writer.SkipLine();
            writer.Comment("Use the static chain to get back " + id.nom, 1);
            writer.Mov(r0, BasePointer, Flags.NI);

            for (int i = 0; i < offset; i++) {
                writer.Ldr(r0, r0, Flags.NI, 0);
            }
            writer.Ldr(r0, r0, Flags.NI, v.getOffset() - 4);

            writer.Comment("Add " + id.nom + " to the stack", 1);
            writer.Stmfd(StackPointer, new Register[] { r0 });
            writer.SkipLine();
        }

        Register[] load_register = { r0, r1 };
        Register[] store_registers = { r0 };

        // Load two last values in the stack in R0 and R1.
        // We have :
        //      R0 = a.right
        //      R1 = a.left

        writer.Ldmfd(StackPointer, load_register);

        // ADD R0 and R1
        writer.Add(r0, r0, r1, Flags.NI);

        // Store R0 in the stack
        writer.Stmfd(StackPointer, store_registers);


        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Soustraction a) {
        // System.out.println("Soustraction");
        a.left.accept(this);
        a.right.accept(this);

        Register[] load_register = { r0, r1 };
        Register[] store_registers = { r0 };


        writer.Ldmfd(StackPointer, load_register);

        // SUB R1 and R0
        // We have :
        //      R0 = a.right
        //      R1 = a.left
        
        writer.Sub(r0, r1, r0, Flags.NI);

        // Store R0 in the stack
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Multiplication a) {
        // System.out.println("Multiplication");
        a.left.accept(this);
        a.right.accept(this);

        // Generate arm code
        // Load two last values in the stack in R0 and R1.
        // We have :
        //      R2 = a.right
        //      R1 = a.left
        Register[] load_register = { r2, r1 };
        writer.Ldmfd(StackPointer, load_register);

        // Mult R2 and R1
        writer.Bl("mul", Flags.NI);

        // Store R0 in the stack
        Register[] store_registers = { r0 };
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }


    @Override
    public ParserRuleContext visit(Division a) {
        // System.out.println("Division");
        a.left.accept(this);
        a.right.accept(this);

        // Generate arm code
        // Load two last values in the stack in R0 and R1.
        // We have :
        //      R2 = a.right
        //      R1 = a.left
        Register[] load_register = { r2, r1 };
        writer.Ldmfd(StackPointer, load_register);

        // Div R2 and R1
        writer.Bl("div", Flags.NI);

        // Store R0 in the stack
        Register[] store_registers = { r0 };
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Sequence a) {
        // System.out.println("Sequence");
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Negation a) {
        // System.out.println("Negation");
        a.expression.accept(this);

        Register[] load_register = { r0 };
        Register[] store_registers = { r0 };

        // Load the last value in the stack in R0.
        writer.Ldmfd(StackPointer, load_register);
        // Mov 0 in R1
        writer.Mov(r1, 0, Flags.NI);
        // Negate R0 using the MVN instruction
        writer.Sub(r0, r1, r0, Flags.NI);
        // Store R0 in the stack
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ID a) {
        // System.out.println("ID");
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Int a) {
        // System.out.println("Int");

        Register[] store_registers = { r0 };
        writer.Mov(r0, a.toInt(), Flags.NI);
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ExpressionIdentifiant a) {
        // System.out.println("ExpressionIdentifiant");
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(AppelFonction a) {
        // System.out.println("AppelFonction");
        ID id = (ID) a.id;

        // Only implement "print" for integers
        if (id.nom.equals("print")) {
            // Get the argument
            a.args.accept(this);

            SymbolLookup table = this.table.getSymbolLookup(this.region);

            ArgFonction argF = (ArgFonction) a.args;
            ArrayList<Ast> args = argF.args;

            Type t = type.inferType(table, args.get(0));

            String def = "format_int";
            if (t.equals(new Array(new Primitive(Character.class)))) {
                def = "format_str";
            }

            writer.Comment("Load param values inside r0 and r1", 1);
            writer.Ldr(r0, def);
            writer.Ldmfd(StackPointer, new Register[] { r1 });
            writer.SkipLine();

            writer.Bl("printf", Flags.NI);
        } else {
            throw new UnsupportedOperationException("Unimplemented function '" + a.id + "'");
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ArgFonction a) {
        // System.out.println("ArgFonction");
        for (Ast e : a.args) {
            e.accept(this);

            if (e instanceof ID) {
                ID id = (ID) e;

                int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
                Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
                
                writer.SkipLine();
                writer.Comment("Use the static chain to get back " + id.nom, 1);
                writer.Mov(r0, BasePointer, Flags.NI);

                if (offset > 0) {
                    writer.Mov(r1, offset, Flags.NI);
                    writer.Bl("_stack_var", Flags.NI);
                }
                
                writer.Ldr(r0, r0, Flags.NI, v.getOffset() - 4);

                writer.Comment("Add " + id.nom + " to the stack", 1);
                writer.Stmfd(StackPointer, new Register[] { r0 });
                writer.SkipLine();
            }
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(IfThenElse a) {
        // System.out.println("IfThenElse");
        int temp = region;

        StepOneRegion();
        // Do the then block

        StepOneRegion();
        // Do the else block

        // Get back to the original region
        this.region = temp;
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(IfThen a) {
        int temp = region;

        // do the if block


        StepOneRegion();
        // Do the then block

        // Get back to the original region
        this.region = temp;
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(While a) {
        // System.out.println("While");
        int temp = region;

        Register[] store = {r0};

        // TODO: Remove this code
        writer.Add(r0, r0, 10, Flags.NI);
        writer.Stmfd(StackPointer, store);

        StepOneRegion();
        // Do the while block
        writer.SkipLine();
        writer.Label("while_" + this.region);

        


        // End of the while
        writer.SkipLine();
        writer.Label("while_exit_" + this.region);
        writer.B("_exit", Flags.NI);
        writer.SkipLine();

        // Get back to the original region
        this.region = temp;
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(For a) {
        int temp = region;

        StepOneRegion();
        // Do the for block

        // Get back to the original region
        this.region = temp;
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Definition a) {
        // System.out.println("Definition");
        int temp = region;

        StepOneRegion();
        // Do the definition block

        // Add StackPointer + Return address and create label
        writer.SkipLine();
        writer.Comment("Definition block", 0);
        writer.Label(this.getLabel());
        
        Register[] registers = { BasePointer, LinkRegister };
        writer.Stmfd(StackPointer, registers);
        writer.Mov(BasePointer, StackPointer, Flags.NI);

        for (Ast ast : a.declarations) {
            ast.accept(this);
        }

        for (Ast ast : a.exprs) {
            if (!(ast instanceof Definition)) {
                ast.accept(this);
            }
        }

        for (String label : this.getSonsLabels()) {
            writer.Bl(label, Flags.NI);
        }

        registers = new Register[] { r0 };
        for (int i = 0 ; i < a.declarations.size() ; i++) {
            writer.Ldmfd(StackPointer, registers);
        }

        registers = new Register[] { ProgramCounter, BasePointer };
        writer.Ldmfd(StackPointer, registers);
        writer.SkipLine();

        for (Ast ast : a.exprs) {
            if (ast instanceof Definition) {
                ast.accept(this);
            }
        }

        // Get back to the original region
        this.region = temp;
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationType a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationTypeClassique a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationArrayType a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationRecordType a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationChamp a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationFonction a) {
        int temp = region;

        StepOneRegion();
        // Do the function block

        // Get back to the original region
        this.region = temp;
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationValeur a) {
        a.id.accept(this);
        ID id = (ID) a.id;
        a.expr.accept(this);

        if (a.expr instanceof ID) {

        }
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ChaineChr a) {
        Constant c = new Constant(a.getValeur());
        constants.add(c);

        Register[] store_registers = { r0 };
        writer.Ldr(r0, c.getId());
        writer.Stmfd(StackPointer, store_registers);
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Nil a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Break a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(InstanciationType a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(ListeAcces a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(ExpressionArray a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(AccesChamp a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
}
