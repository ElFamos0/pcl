package asm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import ast.*;
import parser.exprParser.*;
import sl.Array;
import sl.Function;
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

    public String getLabel(SymbolLookup sl) {
        String label = "_blk_";

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
        writer.Bl("exit", Flags.NI);
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

        writer.SkipLine();
        writer.Comment("Id in expression",1);
        
        a.left.accept(this);

        writer.Stmfd(StackPointer, new Register[] { r9 });
        
        writer.SkipLine();
        writer.Comment("Right expr in expression",1);
        
        a.right.accept(this);

        writer.Mov(r0, r8, Flags.NI);

        writer.SkipLine();
        writer.Comment("Store the value inside the var addr", 1);
        writer.Ldmfd(StackPointer, new Register[] { r1 });
        writer.Str(r0, r1, 0);

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
                writer.Mov(r0, 0, Flags.PL);

                // Set R0 to 0 if N flag is set.
                writer.Mov(r0, 1, Flags.MI);

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
                writer.Mov(r0, 0, Flags.MI);

                // Set R0 to 0 otherwise.
                writer.Mov(r0, 1, Flags.PL);

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
                writer.Cmp(r0, r1);

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
                writer.Cmp(r1, r0);

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
        
        writer.SkipLine();
        writer.Comment("Store left side of addition",1);

        writer.Stmfd(StackPointer, new Register[] { r8 });
        
        a.right.accept(this);

        writer.SkipLine();
        writer.Comment("Add the two values and send it back to the parent", 1);
        writer.Ldmfd(StackPointer, new Register[] { r0 });
        writer.Add(r8, r0, r8, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Soustraction a) {
        // System.out.println("Soustraction");
        a.left.accept(this);
        a.right.accept(this);

        if (a.left instanceof ID) {
            ID id = (ID) a.left;

            int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
            Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
                
            writer.SkipLine();
            writer.Comment("Use the static chain to get back " + id.nom, 1);
            writer.Mov(r0, BasePointer, Flags.NI);

            if (offset > 0) {
                writer.Mov(r1, offset, Flags.NI);
                writer.Bl("_stack_var", Flags.NI);
            }

            writer.Ldr(r0, r0, Flags.NI, v.getOffset());

            writer.Comment("Add " + id.nom + " to the stack", 1);
            writer.Stmfd(StackPointer, new Register[] { r0 });
            writer.SkipLine();
        }

        if (a.right instanceof ID) {
            ID id = (ID) a.left;

            int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
            Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
                
            writer.SkipLine();
            writer.Comment("Use the static chain to get back " + id.nom, 1);
            writer.Mov(r0, BasePointer, Flags.NI);

            if (offset > 0) {
                writer.Mov(r1, offset, Flags.NI);
                writer.Bl("_stack_var", Flags.NI);
            }

            writer.Ldr(r0, r0, Flags.NI, v.getOffset());

            writer.Comment("Add " + id.nom + " to the stack", 1);
            writer.Stmfd(StackPointer, new Register[] { r0 });
            writer.SkipLine();
        }




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

        if (a.left instanceof ID) {
            ID id = (ID) a.left;

            int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
            Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
                
            writer.SkipLine();
            writer.Comment("Use the static chain to get back " + id.nom, 1);
            writer.Mov(r0, BasePointer, Flags.NI);

            if (offset > 0) {
                writer.Mov(r1, offset, Flags.NI);
                writer.Bl("_stack_var", Flags.NI);
            }

            writer.Ldr(r0, r0, Flags.NI, v.getOffset());

            writer.Comment("Add " + id.nom + " to the stack", 1);
            writer.Stmfd(StackPointer, new Register[] { r0 });
            writer.SkipLine();
        }


        if (a.right instanceof ID) {
            ID id = (ID) a.left;

            int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
            Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
                
            writer.SkipLine();
            writer.Comment("Use the static chain to get back " + id.nom, 1);
            writer.Mov(r0, BasePointer, Flags.NI);

            if (offset > 0) {
                writer.Mov(r1, offset, Flags.NI);
                writer.Bl("_stack_var", Flags.NI);
            }

            writer.Ldr(r0, r0, Flags.NI, v.getOffset());

            writer.Comment("Add " + id.nom + " to the stack", 1);
            writer.Stmfd(StackPointer, new Register[] { r0 });
            writer.SkipLine();
        }

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

        if (a.left instanceof ID) {
            ID id = (ID) a.left;

            int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
            Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
                
            writer.SkipLine();
            writer.Comment("Use the static chain to get back " + id.nom, 1);
            writer.Mov(r0, BasePointer, Flags.NI);

            if (offset > 0) {
                writer.Mov(r1, offset, Flags.NI);
                writer.Bl("_stack_var", Flags.NI);
            }

            writer.Ldr(r0, r0, Flags.NI, v.getOffset());

            writer.Comment("Add " + id.nom + " to the stack", 1);
            writer.Stmfd(StackPointer, new Register[] { r0 });
            writer.SkipLine();
        }

        if (a.right instanceof ID) {
            ID id = (ID) a.left;

            int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
            Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
                
            writer.SkipLine();
            writer.Comment("Use the static chain to get back " + id.nom, 1);
            writer.Mov(r0, BasePointer, Flags.NI);

            if (offset > 0) {
                writer.Mov(r1, offset, Flags.NI);
                writer.Bl("_stack_var", Flags.NI);
            }

            writer.Ldr(r0, r0, Flags.NI, v.getOffset());

            writer.Comment("Add " + id.nom + " to the stack", 1);
            writer.Stmfd(StackPointer, new Register[] { r0 });
            writer.SkipLine();
        }

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
        for (Ast expr : a.seqs){
            expr.accept(this);
        }
        return a.ctx;
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

        int offset = this.table.getSymbolLookup(this.region).getVarOffset(a.nom);
        Symbol s = this.table.getSymbolLookup(this.region).getSymbol(a.nom);

        if (!(s instanceof Variable)) {
            return a.ctx;
        }

        Variable v = (Variable) s;
                
        writer.SkipLine();
        writer.Comment("Use the static chain to get back " + a.nom, 1);
        writer.Mov(r0, BasePointer, Flags.NI);

        if (offset > 0) {
            writer.Mov(r1, offset, Flags.NI);
            writer.Bl("_stack_var", Flags.NI);
        }

        writer.Add(r0, r0, v.getOffset(), Flags.NI);

        // writer.Comment("Add " + id.nom + " to the stack", 1);
        writer.Mov(r9, r0, Flags.NI);
        writer.Ldr(r8, r0, Flags.NI, 0);
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Int a) {
        // System.out.println("Int");

        writer.SkipLine();
        writer.Comment("Add int to the r8 register", 1);
        writer.Mov(r8, a.toInt(), Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ExpressionIdentifiant a) {
        // System.out.println("ExpressionIdentifiant");
        // TODO Auto-generated method stub


        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});


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

            String profile = "printf(" + t + ")";

            writer.SkipLine();
            writer.Comment("call: "+profile, 1);

            if (t.equals(new Primitive(Integer.class))) {
                writer.Ldr(r0, "format_int");
                writer.Ldmfd(StackPointer, new Register[] { r1 });
                writer.Ldr(r1, r1, Flags.NI, 0);
            } else {
                writer.Ldr(r0, "format_str");
                writer.Ldmfd(StackPointer, new Register[] { r1 });
            }

            writer.Bl("printf", Flags.NI);
        } else {
            // Get the sl 
            SymbolLookup sl = this.table.getSymbolLookup(this.region);
            // Get the function
            Function f = (Function) sl.getSymbol(id.nom);
            SymbolLookup fsl = f.getTable();
            String funclabel = getLabel(fsl);
            writer.SkipLine();
            writer.Comment("call: " + f.getProfile(), 1);

            // Push args
            a.args.accept(this);

            // Add #4 to the stack pointer to leave a place for the return value
            Register[] registers = { r0 };
            writer.Stmfd(StackPointer, registers);

            // Branch to the function
            writer.Bl(funclabel, Flags.NI);
        }
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ArgFonction a) {
        // System.out.println("ArgFonction");
        for (Ast e : a.args) {
            e.accept(this);

            writer.Stmfd(StackPointer, new Register[] { r9 });
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(IfThen a) {
        int temp = region;

        writer.SkipLine();
        writer.Comment("If-Then", 0);

        SymbolLookup table = this.table.getSymbolLookup(this.region+1);
        String label = "_blk_" + table.getScope()+ "_"+ table.getRegion();


        a.condition.accept(this);
        // on recupere la valeur de la condition dans r0
        // si c'est 0 on saute a la fin du if
        // sinon on continue
        Register[] load_register = { r0 };
        writer.Ldmfd(StackPointer, load_register);
        writer.Cmp(r0, 0);
        writer.B("exit", Flags.EQ);
        writer.B(label, Flags.NI);

        StepOneRegion();
        // Do the then block
        writer.SkipLine();

        writer.SkipLine();
        writer.Label(label);

        a.thenBlock.accept(this);
        writer.SkipLine();
    
        // Get back to the original region
        this.region = temp;
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

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }


    @Override
    public ParserRuleContext visit(While a) {
        // System.out.println("While");
        int temp = region;
        StepOneRegion();
        SymbolLookup table = this.table.getSymbolLookup(this.region);
        Ast cond = a.condition;
        writer.Comment("While block", 0);
        writer.Label(this.getLabel(table));

        cond.accept(this);

        if (cond instanceof ID) {
            ID id = (ID) cond;

            int offset = this.table.getSymbolLookup(this.region).getVarOffset(id.nom);
            Variable v = (Variable) this.table.getSymbolLookup(this.region).getSymbol(id.nom);
            
            writer.SkipLine();
            writer.Comment("Use the static chain to get back " + id.nom, 1);
            writer.Mov(r0, BasePointer, Flags.NI);

            if (offset > 0) {
                writer.Mov(r1, offset, Flags.NI);
                writer.Bl("_stack_var", Flags.NI);
            }

            writer.Add(r0, r0,v.getOffset(), Flags.NI );
            writer.Ldr(r0,r0, Flags.NI, 0);

            writer.Comment("Add " + id.nom + " to the stack", 1);
            writer.Stmfd(StackPointer, new Register[] { r0 });
            writer.SkipLine();
        }

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Cmp(r0, 0);
        writer.B(this.getLabel(table) + "_end", Flags.EQ);

        Ast block = a.block;
        writer.Comment("While content", 1);
        block.accept(this);

        writer.B(this.getLabel(table),Flags.NI);
        writer.Comment("While EndBlock", 1);

        writer.Label(this.getLabel(table) + "_end");

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
        System.out.println("Definition: " + this.region);
        // Do the definition block

        // Add StackPointer + Return address and create label
        writer.SkipLine();
        writer.Comment("Definition block", 0);
        writer.Label(this.getLabel());
        
        Register[] registers = { BasePointer };
        writer.Stmfd(StackPointer, registers);
        writer.Mov(BasePointer, StackPointer, Flags.NI);

        for (Ast ast : a.declarations) {
            if (!(ast instanceof DeclarationFonction)) {
                ast.accept(this);
            }
        }

        writer.B(this.getLabel() + "_dead_zone", Flags.NI);

        for (Ast ast : a.declarations) {
            if (ast instanceof DeclarationFonction) {
                ast.accept(this);
            }
        }

        writer.Label(this.getLabel() + "_dead_zone");

        for (Ast ast : a.exprs) {
            ast.accept(this);
        }

        registers = new Register[] { r0 };
        for (int i = 0 ; i < a.declarations.size() ; i++) {
            writer.Ldmfd(StackPointer, registers);
        }

        registers = new Register[] { BasePointer };
        writer.Ldmfd(StackPointer, registers);
        writer.SkipLine();

        // Get back to the original region
        this.region = temp;
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationType a) {

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationTypeClassique a) {

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationArrayType a) {

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationRecordType a) {

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(DeclarationChamp a) {

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        return a.ctx; 
    }

    @Override
    public ParserRuleContext visit(DeclarationFonction a) {
        int temp = region;
        SymbolLookup sl = this.table.getSymbolLookup(this.region);

        StepOneRegion();
        System.out.println("DeclarationFonction:");
        writer.SkipLine();

        for (Ast ast : a.args) {
            ast.accept(this);
        }

        ID id = (ID) a.id;
        Symbol s = sl.getSymbol(id.nom);
        Function f = (Function) s;
        writer.Comment("func: " + f.getProfile(), 0);
        String l = getLabel();
        writer.Label(l);
        
        Register[] registers = { BasePointer, LinkRegister };
        // Begin
        writer.Comment("begin:", 1);
        writer.Stmfd(StackPointer, registers);
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        writer.SkipLine();

        // Block
        writer.Comment("block:", 1);
        a.expr.accept(this);
        writer.SkipLine();

        // Return
        writer.Comment("return:", 1);
        if (!(f.getType().equals(new Primitive(Void.class)))) {
            // Load the return value in r0 and store it in the return value slot
            registers = new Register[] { r0 };
            writer.Ldmfd(StackPointer, registers);
            // r11 in r1
            writer.Mov(r1, BasePointer, Flags.NI);
            writer.Add(r1, r1, 8, Flags.NI);
            writer.Str(r0, r1, 0);
        }
        writer.SkipLine();

        // End
        writer.Comment("end:", 1);
        registers = new Register[] { ProgramCounter, BasePointer };
        writer.Ldmfd(StackPointer, registers);
        writer.SkipLine();

        // Get back to the original region
        this.region = temp;
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationValeur a) {
        ID id = (ID) a.id;
        
        writer.SkipLine();
        writer.Comment("Declare variable " + id.nom, 1);

        a.id.accept(this);

        a.expr.accept(this);

        // Save the value in the stack
        Register[] registers = { r8 };
        writer.Stmfd(StackPointer, registers);
        
        // We let the expression inside the stack
        // because we declare the variable in the stack.

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ChaineChr a) {
        Constant c = new Constant(a.getValeur());
        constants.add(c);

        writer.Ldr(r9, c.getId());
        
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Nil a) {

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Break a) {


        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(InstanciationType a) {


        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(ListeAcces a) {


        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(ExpressionArray a) {

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(AccesChamp a) {

        writer.Ldmfd(StackPointer, new Register[] {r0});
        writer.Stmfd(StackPointer, new Register[] {r1});

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }
    
}
