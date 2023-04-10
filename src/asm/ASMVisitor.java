package asm;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import ast.*;
import parser.exprParser.*;
import sl.Array;
import sl.Primitive;
import sl.SymbolLookup;
import sl.Type;
import sl.TypeInferer;

public class ASMVisitor implements AstVisitor<ParserRuleContext> {
    
    private SymbolLookup table;
    private int region;
    private int biggestRegion = 0;
    private TypeInferer type = new TypeInferer();

    private ASMWriter writer;

    private List<Register> registers = new ArrayList<Register>();
    private Register BasePointer = new Register("r11", 0);
    private Register StackPointer = new Register("r13", 0);
    private Register LinkRegister = new Register("r14", 0);
    private Register ProgramCounter = new Register("r15", 0);

    private List<Constant> constants = new ArrayList<Constant>();

    public ASMVisitor(SymbolLookup table, ASMWriter writer) {
        this.table = table;
        // permet de récuperer le premier offset (on l'a déjà oublié donc on le note)
        region = table.getLibFunc();
        biggestRegion = region;
        this.writer = writer;
    }

    public void initRegisters() {
        // Init ARMv7 registers
        registers.add(new Register("r0", 0));
        registers.add(new Register("r1", 0));
        registers.add(new Register("r2", 0));
        registers.add(new Register("r3", 0));
        registers.add(new Register("r4", 0));
        registers.add(new Register("r5", 0));
        registers.add(new Register("r6", 0));
        registers.add(new Register("r7", 0));
        registers.add(new Register("r8", 0));
        registers.add(new Register("r9", 0));
        registers.add(new Register("r10", 0));
        // registers.add(new Register("r11", 0)); BP
        // registers.add(new Register("r12", 0)); UNUSED
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
        writer.write(".text\n");
        writer.write(".globl main\n");
        writer.Label("main");

        a.expression.accept(this);

        writer.write(".data\n");
        writer.write("format_str: .ascii      \"%s\\n\\0\"\n");
        writer.write("format_int: .ascii      \"%d\\n\\0\"\n");

        for (Constant c : constants) {
            writer.write(c.toASM()+"\n");
        }

        return null;
    }

    @Override
    public ParserRuleContext visit(Expression a) {
        a.left.accept(this);
        a.right.accept(this);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Ou a) {
        a.left.accept(this);
        a.right.accept(this);

        Register r0 = new Register("r0", 0);
        Register r1 = new Register("r1", 0);

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
        a.left.accept(this);
        a.right.accept(this);

        Register r0 = new Register("r0", 0);
        Register r1 = new Register("r1", 0);

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
        ParserRuleContext left = a.left.accept(this);
        a.right.accept(this);

        SymbolLookup table = this.table.getSymbolLookup(this.region);

        Type leftType = this.type.inferType(table, left);

        if (leftType.equals(new Primitive(Integer.class))) {
            // We have to compare two integers

            Register r0 = new Register("r0", 0);
            Register r1 = new Register("r1", 0);

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
        a.left.accept(this);
        a.right.accept(this);

        Register r0 = new Register("r0", 0);
        Register r1 = new Register("r1", 0);

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
        a.left.accept(this);
        a.right.accept(this);

        Register r0 = new Register("r0", 0);
        Register r1 = new Register("r1", 0);

        writer.Sub(r0, r0, r1,null);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Multiplication a) {
        a.left.accept(this);
        a.right.accept(this);
        Register r2 = new Register("r2", 0);
        Register r1 = new Register("r1", 0);
        Register r0 = new Register("r0",0);
        // Generate arm code
        // Load two last values in the stack in R0 and R1.
        // We have :
        //      R2 = a.right
        //      R1 = a.left
        Register[] load_register = { r2, r1 };
        writer.Ldmfd(StackPointer, load_register);

        // Mult R2 and R1
        writer.Bl("mul");

        // Store R0 in the stack
        Register[] store_registers = { r0 };
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }


    @Override
    public ParserRuleContext visit(Division a) {
        a.left.accept(this);
        a.right.accept(this);
        Register r2 = new Register("r2", 0);
        Register r1 = new Register("r1", 0);
        Register r0 = new Register("r0",0);
        // Generate arm code
        // Load two last values in the stack in R0 and R1.
        // We have :
        //      R2 = a.right
        //      R1 = a.left
        Register[] load_register = { r2, r1 };
        writer.Ldmfd(StackPointer, load_register);

        // Div R2 and R1
        writer.Bl("div");

        // Store R0 in the stack
        Register[] store_registers = { r0 };
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Sequence a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Negation a) {
        a.expression.accept(this);
        Register r0 = new Register("r0", 0);
        Register[] load_register = { r0 };
        Register[] store_registers = { r0 };

        // Load the last value in the stack in R0.
        writer.Ldmfd(StackPointer, load_register);
        // Negate R0 using the MVN instruction
        writer.Mvn(r0, r0, Flags.NI);
        // Store R0 in the stack
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ID a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Int a) {
        Register r0 = new Register("r0", 0);
        Register[] store_registers = { r0 };
        writer.Mov(r0, a.toInt(), Flags.NI);
        writer.Stmfd(StackPointer, store_registers);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ExpressionIdentifiant a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(AppelFonction a) {
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

            Register r0 = new Register("r0", 0);
            writer.Ldr(r0, def);

            Register r1 = new Register("r1", 0);
            Register[] load_registers = { r1 };
            writer.Ldmfd(StackPointer, load_registers);

            writer.Bl("printf");
        } else {
            throw new UnsupportedOperationException("Unimplemented function '" + a.id + "'");
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ArgFonction a) {
        for (Ast e : a.args) {
            e.accept(this);
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(IfThenElse a) {
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

        StepOneRegion();
        // Do the then block

        // Get back to the original region
        this.region = temp;
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(While a) {
        int temp = region;

        StepOneRegion();
        // Do the while block

        // Get back to the original region
        this.region = temp;
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
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
        int temp = region;

        StepOneRegion();
        // Do the definition block

        // Get back to the original region
        this.region = temp;
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(ChaineChr a) {
        Constant c = new Constant(a.getValeur());
        constants.add(c);

        Register r0 = new Register("r0", 0);
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
