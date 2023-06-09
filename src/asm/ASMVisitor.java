package asm;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import parser.exprParser.*;
import ast.*;
import sl.Array;
import sl.Function;
import sl.Primitive;
import sl.Record;
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
    private Register r10 = new Register("r10", 0);

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
        writer.B("main", Flags.NI);

        writer.Label("main");
        writer.Comment("Init rdm", 1);
        writer.SRand();
        writer.Comment("Init stack", 1);
        writer.SkipLine();
        writer.Comment("Add stack pointer in base pointer", 1);
        writer.Mov(BasePointer, StackPointer, Flags.NI);

        writer.SkipLine();
        writer.Comment("Display allocation", 1);
        writer.Mov(r0, table.getMaxScope() * 4, Flags.NI);
        writer.Bl("malloc", Flags.NI);
        writer.Mov(r10, r0, Flags.NI);
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
        // Permet d'instancier le main ainsi que la stack
        initStack();

        a.expression.accept(this);

        // On écrit ensuite les fonctions de la librairie
        writer.SkipLine();
        writer.Bl("_exit", Flags.NI);
        writer.SkipLine();
        writer.Mul();
        writer.SkipLine();
        writer.Div();

        // Puis une fonction spéciale pour sortir du programme
        writer.SkipLine();
        writer.Comment("syscall exit(int status = 0)", 0);
        writer.Label("_exit");
        writer.Exit(0);
        writer.SkipLine();

        // Enfin on écrit les données statiques du programme
        writer.write(".data\n");
        writer.write("\tformat_str: .ascii      \"%s\\0\"\n");
        writer.write("\tformat_int: .ascii      \"%d\\0\"\n");
        writer.write("\tformat_debug_int: .ascii      \"debug: %d\\n\\0\"\n");
        writer.write("\tformat_debug_x: .ascii      \"debug: %x\\n\\0\"\n");
        writer.write("\tformat_debug_addr: .ascii      \"debug: %p\\n\\0\"\n");
        writer.write("\tscanf_int: .ascii      \"%d\"\n");

        // Ainsi que les données statiques données par l'utilisateur
        for (Constant c : constants) {
            writer.write("\t" + c.toASM() + "\n");
        }

        return null;
    }

    @Override
    public ParserRuleContext visit(Expression a) {
        // System.out.println("Expression");

        writer.SkipLine();
        writer.Comment("Id in expression", 1);

        a.left.accept(this);

        Type t = type.inferType(table.getSymbolLookup(region), a.left);

        // Load addr from r8 if it's an array
        if (t instanceof Array) {
            writer.Stmfd(StackPointer, new Register[] { r8, r9 });
        } else {
            writer.Stmfd(StackPointer, new Register[] { r9 });
        }

        writer.SkipLine();
        writer.Comment("Right expr in expression", 1);

        a.right.accept(this);

        writer.Mov(r3, r8, Flags.NI);

        writer.SkipLine();
        writer.Comment("Store the value inside the var addr", 1);
        if (t instanceof Array && (!t.equals(new Array(new Primitive(Character.class))))) {
            Array arr = (Array) t;

            if (!(a.right instanceof ExpressionArray)) {
                writer.SkipLine();
                writer.Comment("Overwrite array pointer", 1);
                writer.Ldmfd(StackPointer, new Register[] { r1, r2 });
                writer.Str(r8, r2, 0);
            } else {
                writer.SkipLine();
                writer.Comment("Call malloc for allocation", 1);
                writer.Stmfd(StackPointer, new Register[] { r8 });
                writer.Mov(r0, r9, Flags.NI);
                writer.Lsl(r0, r0, 2, Flags.NI);
                writer.Bl("malloc", Flags.NI);

                writer.SkipLine();
                writer.Comment("Alloc the fields of the array", 1);
                writer.Ldmfd(StackPointer, new Register[] { r3 });
                writer.Ldmfd(StackPointer, new Register[] { r1, r2 });
                writer.Str(r0, r2, 0);
                for (int i = 0; i < arr.getSize(); i++) {
                    writer.Str(r3, r0, -(i * 4));
                }
            }
        } else if (a.left instanceof ListeAcces) {
            writer.Ldmfd(StackPointer, new Register[] { r1 });
            if (t.equals(new Array(new Primitive(Character.class)))) {
                writer.Ldr(r3, r3, Flags.NI, 0);
            }
            writer.Str(r3, r1, 0);
        } else {
            writer.Ldmfd(StackPointer, new Register[] { r1 });
            writer.Str(r3, r1, 0);
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Ou a) {
        // System.out.println("Ou");

        a.left.accept(this);

        writer.SkipLine();
        writer.Comment("Store left side of the OR", 1);

        writer.Stmfd(StackPointer, new Register[] { r8 });

        a.right.accept(this);

        writer.SkipLine();
        writer.Comment("OR the two values and send it back to the parent", 1);
        // on recupère la valeur du fils gauche dans r0 pour pouvoir l'ajouter à celle
        // du fils droit
        writer.Ldmfd(StackPointer, new Register[] { r0 });
        writer.Orr(r8, r0, r8, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Et a) {
        // System.out.println("Et");

        a.left.accept(this);

        writer.SkipLine();
        writer.Comment("Store left side of the AND", 1);

        writer.Stmfd(StackPointer, new Register[] { r8 });

        a.right.accept(this);

        writer.SkipLine();
        writer.Comment("AND the two values and send it back to the parent", 1);

        writer.Ldmfd(StackPointer, new Register[] { r0 });
        writer.And(r8, r0, r8, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Compar a) {
        // System.out.println("Compar");
        a.left.accept(this);

        writer.Stmfd(StackPointer, new Register[] { r8 });

        a.right.accept(this);

        writer.Mov(r0, r8, Flags.NI);
        writer.Ldmfd(StackPointer, new Register[] { r1 });

        SymbolLookup table = this.table.getSymbolLookup(this.region);

        Type leftType = this.type.inferType(table, a.left);

        if (leftType.equals(new Primitive(Integer.class))) {
            // We have to compare two integers

            // Load two last values in the stack in R0 and R1.
            // We have :
            // R0 = a.right
            // R1 = a.left

            // CMP R0 and R1 following the operator.
            switch (a.operator) {
                case "<":
                    // CMP R0 and R1
                    // CMP does R1 - R0
                    // We need here R0 > R1
                    writer.Cmp(r1, r0);

                    // Store the N flag in R
                    // N flag is set if R0 > R1

                    // Set R8 to 1 if N flag is not set.
                    writer.Mov(r8, 0, Flags.PL);

                    // Set R8 to 0 if N flag is set.
                    writer.Mov(r8, 1, Flags.MI);

                    // Set R8 to 0 if Z flag is set because we want R0 != R1.
                    writer.Mov(r8, 0, Flags.EQ);
                    break;
                case ">":
                    // CMP R0 and R1
                    // CMP does R0 - R1
                    // We need here R0 < R1
                    writer.Cmp(r0, r1);

                    // Set R0 to 1 if N flag is set.
                    writer.Mov(r8, 0, Flags.PL);

                    // Set R0 to 0 otherwise.
                    writer.Mov(r8, 1, Flags.MI);
                    break;
                case "=":
                    // CMP R0 and R1
                    // CMP does R1 - R0
                    // We need here R0 = R1
                    writer.Cmp(r1, r0);

                    // Set R8 to 1 if Z flag is set.
                    writer.Mov(r8, 1, Flags.EQ);

                    // Set R8 to 0 otherwise.
                    writer.Mov(r8, 0, Flags.NE);
                    break;
                case "<=":
                    // CMP R0 and R1
                    // CMP does R0 - R1
                    // We need here R0 >= R1
                    writer.Cmp(r0, r1);

                    // Set R8 to 1 if N flag is not set.
                    writer.Mov(r8, 1, Flags.PL);

                    // Set R8 to 0 if N flag is set.
                    writer.Mov(r8, 0, Flags.MI);
                    break;
                case ">=":
                    // CMP R0 and R1
                    // CMP does R1 - R0
                    // We need here R1 >= R0
                    writer.Cmp(r1, r0);

                    // Set R8 to 1 if N flag is not set.
                    writer.Mov(r8, 1, Flags.PL);

                    // Set R8 to 0 if N flag is set.
                    writer.Mov(r8, 0, Flags.MI);
                    break;
                case "<>":
                    // CMP R0 and R1
                    // CMP does R1 - R0
                    // We need here R0 != R1
                    writer.Cmp(r1, r0);

                    // Set R8 to 1 if Z flag is not set.
                    writer.Mov(r8, 1, Flags.NE);

                    // Set R8 to 0 if Z flag is set.
                    writer.Mov(r8, 0, Flags.EQ);
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
        writer.Comment("Store left side of addition", 1);

        writer.Stmfd(StackPointer, new Register[] { r8 });

        a.right.accept(this);

        writer.SkipLine();
        writer.Comment("Add the two values and send it back to the parent", 1);
        // on recupère la valeur du fils gauche dans r0 pour pouvoir l'ajouter à celle
        // du fils droit
        writer.Ldmfd(StackPointer, new Register[] { r0 });
        writer.Add(r8, r0, r8, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Soustraction a) {
        // System.out.println("Soustraction");
        a.left.accept(this);

        writer.SkipLine();
        writer.Comment("Store left side of soustraction", 1);

        writer.Stmfd(StackPointer, new Register[] { r8 });

        a.right.accept(this);

        writer.SkipLine();
        writer.Comment("Substract the two values and send it back to the parent", 1);
        writer.Ldmfd(StackPointer, new Register[] { r0 });
        writer.Sub(r8, r0, r8, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Multiplication a) {
        // System.out.println("Multiplication");
        a.left.accept(this);

        writer.SkipLine();
        writer.Comment("Store left side of Multiplication", 1);

        writer.Stmfd(StackPointer, new Register[] { r8 });

        // Receive right in r8
        a.right.accept(this);

        writer.SkipLine();
        writer.Comment("Mult the two values and send them to r8", 1);

        // Store the left result in r1 and move the right result to r2
        writer.Ldmfd(StackPointer, new Register[] { r1 });
        writer.Mov(r2, r8, Flags.NI);

        // Generate arm code
        // Load two last values in the stack in R0 and R1.
        // We have :
        // R2 = a.right
        // R1 = a.left

        // Mult R2 and R1
        writer.Bl("_mul", Flags.NI);

        // Mov R0 (result of mult) to R8
        writer.Mov(r8, r0, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Division a) {
        // System.out.println("Division");
        a.left.accept(this);

        writer.SkipLine();
        writer.Comment("Store left side of Division", 1);

        writer.Stmfd(StackPointer, new Register[] { r8 });

        // Receive right in r8
        a.right.accept(this);

        writer.SkipLine();
        writer.Comment("Divide the two values and send them to r8", 1);

        // Store the left result in r1 and move the right result to r2
        writer.Ldmfd(StackPointer, new Register[] { r1 });
        writer.Mov(r2, r8, Flags.NI);

        // Generate arm code
        // Load two last values in the stack in R0 and R1.
        // We have :
        // R2 = a.right
        // R1 = a.left

        // Div R2 and R1
        writer.Bl("_div", Flags.NI);

        // Mov R0 (result of div) to R8
        writer.Mov(r8, r0, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Sequence a) {
        // System.out.println("Sequence");
        for (Ast expr : a.seqs) {
            expr.accept(this);
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Negation a) {
        // System.out.println("Negation");
        a.expression.accept(this);

        // Mov 0 in R1
        writer.Mov(r1, 0, Flags.NI);
        // Negate R0 using the SUB instruction
        writer.Sub(r8, r1, r8, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ID a) {
        // System.out.println("ID");

        SymbolLookup table = this.table.getSymbolLookup(this.region);
        int offset = table.getVarOffset(a.nom);
        Symbol s = table.getSymbol(a.nom);

        if (!(s instanceof Variable)) {
            return a.ctx;
        }

        Variable v = (Variable) s;

        writer.SkipLine();
        writer.Comment("Use the static chain to get back " + a.nom, 1);
        writer.Ldr(r0, r10, Flags.NI, -((table.getScope() - offset) * 4));

        writer.Add(r0, r0, v.getOffset(), Flags.NI);

        // writer.Comment("Add " + id.nom + " to the stack", 1);
        writer.Ldr(r8, r0, Flags.NI, 0);

        writer.Mov(r9, r0, Flags.NI);
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Int a) {
        // System.out.println("Int");

        writer.SkipLine();
        writer.Comment("Add int to the r8 register", 1);
        writer.Ldr(r8, a.toHex());

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ExpressionIdentifiant a) {
        // System.out.println("ExpressionIdentifiant");

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(AppelFonction a) {
        // System.out.println("AppelFonction");
        ID id = (ID) a.id;

        // Only implement "print" for integers
        switch (id.nom) {
            case "print":
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
                String profile = "lib_print(" + t + ")";
                writer.SkipLine();
                writer.Comment("call: " + profile, 1);
                writer.Ldr(r0, def);
                writer.Ldmfd(StackPointer, new Register[] { r1 });
                writer.Bl("printf", Flags.NI);
                writer.Mov(r0, 0, Flags.NI);
                writer.Bl("fflush", Flags.NI);
                break;
            case "random":
                // Get the argument
                a.args.accept(this);
                writer.SkipLine();
                writer.Comment("call: lib_random()", 1);
                writer.Bl("rand(PLT)", Flags.NI);
                writer.Mov(r8, r0, Flags.NI);
                break;
            case "not":
                // Get the argument
                a.args.accept(this);
                writer.SkipLine();
                writer.Comment("call: lib_not()", 1);
                writer.Ldmfd(StackPointer, new Register[] { r8 });
                writer.Eor(r8, r8, 1, Flags.NI);
                break;
            case "input_i":
                // sub     sp, sp, #4
                // @ Call scanf and store value in r4
                // ldr     r0, addrInp
                // mov     r1, sp
                // bl      scanf
                // ldr     r4, [sp]
                // add     sp, sp, #4
                writer.SkipLine();
                writer.Comment("call: lib_input_i()", 1);
                writer.Sub(StackPointer, StackPointer, 4, Flags.NI);
                writer.Ldr(r0, "scanf_int");
                writer.Mov(r1, StackPointer, Flags.NI);
                writer.Bl("scanf", Flags.NI);
                writer.Ldr(r8, StackPointer, Flags.NI, 0);
                writer.Add(StackPointer, StackPointer, 4, Flags.NI);
                break;
            default:
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

                // Get the return value in r8
                writer.Ldmfd(StackPointer, new Register[] { r8 });
                writer.Mov(r9, r8, Flags.NI);

                // Pop args
                int nbArgs = f.getParamsCount();
                writer.Add(StackPointer, StackPointer, 4 * (nbArgs), Flags.NI);
                break;
        }

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ArgFonction a) {
        // System.out.println("ArgFonction");
        // Reverse args
        for (int i = a.args.size() - 1; i >= 0; i--) {
            a.args.get(i).accept(this);

            writer.Stmfd(StackPointer, new Register[] { r8 });
        }
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(IfThen a) {
        int temp = region;

        writer.SkipLine();
        writer.Comment("If-Then", 0);
        a.condition.accept(this);
        // on store le resultat de la condition dans r0
        writer.Mov(r0, r8, Flags.NI);
        // on compare r0 a 0
        writer.Cmp(r0, 0);

        StepOneRegion();
        SymbolLookup table = this.table.getSymbolLookup(this.region);
        // on saute dans une imbrication pour écrire le code du then
        Register[] registers = { BasePointer, r0 };
        writer.Ldr(r0, r10, Flags.NI, - (table.getScope() * 4));
        writer.Stmfd(StackPointer, registers);
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        writer.Str(BasePointer, r10, - (table.getScope() * 4));
        // on met en mémoire le base pointer de la région précédente 
        // on initialise notre nouveau base pointer

        // on saute a la fin du if si la condition est fausse
        writer.B(this.getLabel(table) + "_end", Flags.EQ);
        // on écrit le code du then
        Ast then = a.thenBlock;
        then.accept(this);

        // condition de fin du if then
        writer.Comment("End of If-Then", 1);
        writer.Label(this.getLabel(table) + "_end");

        registers = new Register[] { BasePointer, r0 };
        writer.Ldmfd(StackPointer, registers);
        writer.Str(r0, r10, - (table.getScope() * 4));
        // on remet le stack pointer au bon endroit

        // on retourne à la région originale
        this.region = temp;
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(IfThenElse a) {
        // System.out.println("IfThenElse");
        int temp = region;

        writer.SkipLine();
        writer.Comment("If-Then-Else", 0);
        a.condition.accept(this);
        // on store le resultat de la condition dans r0
        writer.Mov(r0, r8, Flags.NI);
        // on compare r0 a 0
        writer.Cmp(r0, 0);
        // on saute dans le else si la condition est fausse
        StepOneRegion();
        SymbolLookup table = this.table.getSymbolLookup(this.region);
        Register[] registers = { BasePointer, r0 };
        writer.Ldr(r0, r10, Flags.NI, - (table.getScope() * 4));
        writer.Stmfd(StackPointer, registers);
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        writer.Str(BasePointer, r10, - (table.getScope() * 4));
        String label1 = this.getLabel(table);
        writer.B(label1 + "_else", Flags.EQ);

        Ast then = a.thenBlock;
        then.accept(this);

        writer.B(label1 + "_end", Flags.NI);
        
        StepOneRegion();
        table = this.table.getSymbolLookup(this.region);
        writer.Label(label1 + "_else");
        Register[] registers2 = { BasePointer, r0 };
        writer.Ldr(r0, r10, Flags.NI, - (table.getScope() * 4));
        writer.Stmfd(StackPointer, registers2);
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        writer.Str(BasePointer, r10, - (table.getScope() * 4));

        Ast elseBlock = a.elseBlock;

        elseBlock.accept(this);

        registers = new Register[] { BasePointer, r0 };
        writer.Ldmfd(StackPointer, registers);
        writer.Str(r0, r10, - (table.getScope() * 4));

        writer.Label(label1 + "_end");

        registers2 = new Register[] { BasePointer, r0 };
        writer.Ldmfd(StackPointer, registers2);
        writer.Str(r0, r10, - (table.getScope() * 4));
        this.region = temp;
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(While a) {
        // System.out.println("While");
        int temp = region;
        StepOneRegion();
        SymbolLookup table = this.table.getSymbolLookup(this.region);
        Register[] registers = { BasePointer, r0 };
        writer.Ldr(r0, r10, Flags.NI, - (table.getScope() * 4));
        writer.Stmfd(StackPointer, registers);
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        writer.Str(BasePointer, r10, - (table.getScope() * 4));
        writer.Stmfd(StackPointer, new Register[] { r7 });
        writer.Mov(r7, StackPointer, Flags.NI);

        Ast cond = a.condition;
        writer.Comment("While block", 0);
        writer.Label(this.getLabel(table));

        cond.accept(this);

        writer.Mov(r0, r8, Flags.NI);
        writer.Cmp(r0, 0);
        writer.B(this.getLabel(table) + "_end", Flags.EQ);

        Ast block = a.block;
        writer.Comment("While content", 1);

        block.accept(this);

        writer.B(this.getLabel(table), Flags.NI);
        writer.Comment("While EndBlock", 1);

        writer.Mov(StackPointer, r7, Flags.NI);
        writer.Ldmfd(StackPointer, new Register[] { r7 });
        writer.Label(this.getLabel(table) + "_end");
        registers = new Register[] { BasePointer, r0 };
        writer.Ldmfd(StackPointer, registers);
        writer.Str(r0, r10, - (table.getScope() * 4));

        writer.SkipLine();

        // Get back to the original region
        this.region = temp;
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(For a) {
        int temp = region;

        StepOneRegion();
        SymbolLookup table = this.table.getSymbolLookup(this.region);
        Register[] registers = { BasePointer, r0 };
        writer.Ldr(r0, r10, Flags.NI, - (table.getScope() * 4));
        writer.Stmfd(StackPointer, registers);
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        writer.Str(BasePointer, r10, - (table.getScope() * 4));
        // Ast variant = a.start;
        Ast startValue = a.startValue;
        Ast endValue = a.endValue;
        Ast block = a.block;
        writer.Comment("For block", 0);
        writer.Label(this.getLabel(table));

        startValue.accept(this);
        writer.Stmfd(StackPointer, new Register[] { r8 });

        endValue.accept(this);
        writer.Stmfd(StackPointer, new Register[] { r8 });

        // Add the current value of the r7 register to the stack
        writer.Stmfd(StackPointer, new Register[] { r7 });

        // Save the current StackPointer value in r7
        writer.Mov(r7, StackPointer, Flags.NI);

        writer.Label(this.getLabel(table) + "_cond");
        writer.Ldr(r0, BasePointer, Flags.NI, -4);
        writer.Ldr(r1, BasePointer, Flags.NI, -8);
        writer.Cmp(r0, r1);
        writer.B(this.getLabel(table) + "_end", Flags.GE);

        block.accept(this);

        writer.Ldr(r0, BasePointer, Flags.NI, -4);
        writer.Add(r0, r0, 1, Flags.NI);
        writer.Str(r0, BasePointer, -4);
        writer.B(this.getLabel(table) + "_cond", Flags.NI);

        writer.SkipLine();
        writer.Label(this.getLabel(table) + "_end");

        // Get back the value of the r7 register inside the stack pointer
        writer.Mov(StackPointer, r7, Flags.NI);

        // Unstack the value of the r7 register
        writer.Ldmfd(StackPointer, new Register[] { r7 });

        writer.Ldmfd(StackPointer, new Register[] { r0, r1 });
        registers = new Register[] { BasePointer, r0 };
        writer.Ldmfd(StackPointer, registers);
        writer.Str(r0, r10, - (table.getScope() * 4));
        // Get back to the original region
        this.region = temp;
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Definition a) {
        // System.out.println("Definition");
        int temp = region;

        StepOneRegion();
        // Do the definition block
        SymbolLookup sl = this.table.getSymbolLookup(this.region);

        // Add StackPointer + Return address and create label
        writer.SkipLine();
        writer.Comment("Definition block", 0);
        writer.Label(this.getLabel());

        Register[] registers = { BasePointer, r0 };
        writer.Ldr(r0, r10, Flags.NI, - (sl.getScope() * 4));
        writer.Stmfd(StackPointer, registers);
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        writer.Str(BasePointer, r10, - (sl.getScope() * 4));

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
        for (Ast dec : a.declarations) {
            if (!(dec instanceof DeclarationFonction))
                writer.Ldmfd(StackPointer, registers);
        }

        registers = new Register[] { BasePointer, r0 };
        writer.Ldmfd(StackPointer, registers);
        writer.Str(r0, r10, - (sl.getScope() * 4));
        writer.SkipLine();

        // Get back to the original region
        this.region = temp;

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationType a) {
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationTypeClassique a) {
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationArrayType a) {
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationRecordType a) {
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationChamp a) {
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(DeclarationFonction a) {
        int temp = region;

        StepOneRegion();
        SymbolLookup sl = this.table.getSymbolLookup(this.region);
        writer.SkipLine();

        ID id = (ID) a.id;
        Symbol s = sl.getSymbol(id.nom);
        Function f = (Function) s;
        writer.Comment("func: " + f.getProfile(), 0);
        String l = getLabel();
        writer.Label(l);

        Register[] registers = { BasePointer, LinkRegister };
        // Begin
        writer.Comment("begin:", 1);
        writer.Ldr(r0, r10, Flags.NI, - (sl.getScope() * 4));
        writer.Stmfd(StackPointer, registers);
        writer.Stmfd(StackPointer, new Register[] { r0 });
        writer.Mov(BasePointer, StackPointer, Flags.NI);
        writer.Str(BasePointer, r10, - (sl.getScope() * 4));
        writer.SkipLine();

        // Block
        writer.Comment("block:", 1);
        a.expr.accept(this);
        writer.SkipLine();

        // Return
        writer.Comment("return:", 1);
        writer.Str(r8, BasePointer, 12);
        writer.SkipLine();

        // End
        writer.Comment("end:", 1);
        registers = new Register[] { ProgramCounter, BasePointer };
        writer.Ldmfd(StackPointer, new Register[] { r0 });
        writer.Str(r0, r10, - (sl.getScope() * 4));
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

        // a.id.accept(this);
        a.expr.accept(this);
        Register[] registers = { r8 };

        // Save the value in the stack
        Type t = type.inferType(table.getSymbolLookup(region), id);
        if (t instanceof Array && (!t.equals(new Array(new Primitive(Character.class))))) {
            Array arr = (Array) t;

            int size = arr.getSize();
            writer.SkipLine();
            writer.Comment("Call to malloc for the array allocation", 1);
            writer.Mov(r0, size * 4, Flags.NI);
            writer.Bl("malloc", Flags.NI);
            writer.Stmfd(StackPointer, new Register[] { r0 });

            for (int i = 0; i < size; i++) {
                writer.Str(r8, r0, -(i * 4));
            }
        } else {
            writer.Stmfd(StackPointer, registers);
        }

        // We let the expression inside the stack
        // because we declare the variable in the stack.

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ChaineChr a) {
        Constant c = new Constant(a.getValeur());
        constants.add(c);

        writer.Ldr(r8, c.getId());

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Nil a) {
        writer.SkipLine();
        writer.Comment("Nil value", 1);
        writer.Mov(r8, 0, Flags.NI);
        writer.Mov(r9, 0, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(Break a) {
        ParserRuleContext c = a.ctx;
        SymbolLookup sl = this.table.getSymbolLookup(this.region);

        while (c != null) {
            if (c instanceof PourContext || c instanceof TantQueContext) {
                break;
            }
            if (c instanceof SiAlorsContext || c instanceof SiAlorsSinonContext) {
                sl = sl.getParent();
            }

            c = c.getParent();
        }

        String label = "_blk_" + sl.getScope() + "_" + sl.getRegion() + "_end";
        writer.B(label, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(InstanciationType a) {
        // On récupère la TDS afin de pouvoir récuperer chacun des fiels dans l'ordre de
        // la TDS
        SymbolLookup table = this.table.getSymbolLookup(this.region);
        Type t = type.inferType(table, a.id);
        Record r = (Record) t;

        // On alloue la mémoire afin de pouvoir stocker les champs
        int size = r.getFields().size();
        writer.SkipLine();
        writer.Comment("Call to malloc for the array allocation", 1);
        writer.Mov(r0, size * 4, Flags.NI);
        writer.Bl("malloc", Flags.NI);
        writer.Stmfd(StackPointer, new Register[] { r0 });

        ArrayList<Symbol> sortedFields = r.getFields();

        // On trie les expressions dans l'ordre des champs pour pouvoir les stocker dans
        // l'ordre
        ArrayList<Ast> sortedExpressions = new ArrayList<Ast>();
        for (Symbol s : sortedFields) {
            for (int i = 0; i < a.identifiants.size(); i++) {
                ID id = (ID) a.identifiants.get(i);
                Ast expr = a.expressions.get(i);
                if (id.nom.equals(s.getName())) {
                    sortedExpressions.add(expr);
                }
            }
        }

        // On accepte les expressions afin de les stocker dans le bon ordre
        for (int i = 0; i < sortedExpressions.size(); i++) {
            sortedExpressions.get(i).accept(this);
            writer.Ldmfd(StackPointer, new Register[] { r0 });
            writer.Str(r8, r0, -(i * 4));
            writer.Stmfd(StackPointer, new Register[] { r0 });
        }

        // On récupère l'adresse de la mémoire allouée
        writer.Ldmfd(StackPointer, new Register[] { r8 });
        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ListeAcces a) {
        a.id.accept(this);
        ID id = (ID) a.id;

        Register[] regs = new Register[] { r8 };

        writer.SkipLine();
        writer.Comment("Access of a list", 1);
        writer.Stmfd(StackPointer, regs);

        Record r = null;
        for (Ast ast : a.accesChamps) {
            ast.accept(this);
            AccesChamp ac = (AccesChamp) ast;

            // Load the value in r8 in r0
            writer.SkipLine();
            writer.Comment("Load the value of the access in r0 and read the pointed value", 1);
            if (!ac.getisArrayAccess()) {
                // records
                ID id2 = (ID) ac.getChild();
                if (r == null) {
                    Type t = type.inferType(table.getSymbolLookup(region), id);
                    r = (Record) t;
                } else {
                    // We are in a nested record
                    int idx = r.getFields().indexOf(r.getField(id.nom));
                    r = (Record) r.getFields().get(idx).getType();
                }
                // Get the field index
                int index = r.getFields().indexOf(r.getField(id2.nom));
                writer.Mov(r0, index, Flags.NI);
                id = id2;
            } else {
                // array
                writer.Mov(r0, r8, Flags.NI);
            }

            writer.Ldmfd(StackPointer, new Register[] { r1 });
            writer.Lsl(r0, r0, 2, Flags.NI);
            writer.Sub(r1, r1, r0, Flags.NI);
            writer.Ldr(r2, r1, Flags.NI, 0);
            writer.Stmfd(StackPointer, new Register[] { r2 });
        }

        writer.SkipLine();
        writer.Comment("Load the value accessed in r8 and the addr in r9", 1);
        writer.Ldmfd(StackPointer, new Register[] { r8 });
        writer.Mov(r9, r1, Flags.NI);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(ExpressionArray a) {
        a.getSize().accept(this);
        writer.Mov(r9, r8, Flags.NI);

        a.getExpr().accept(this);

        return a.ctx;
    }

    @Override
    public ParserRuleContext visit(AccesChamp a) {
        return a.getChild().accept(this);
    }

}
