package asm;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import ast.*;
import sl.SymbolLookup;

public class ASMVisitor implements AstVisitor<ParserRuleContext> {
    
    private SymbolLookup table;
    private int region;
    private int biggestRegion = 0;

    private ASMWriter writer;

    private List<Register> registers = new ArrayList<Register>();
    private Register BasePointer = new Register("r11", 0);
    private Register StackPointer = new Register("r13", 0);
    private Register LinkRegister = new Register("r14", 0);
    private Register ProgramCounter = new Register("r15", 0);

    public ASMVisitor(SymbolLookup table) {
        this.table = table;
        // permet de récuperer le premier offset (on l'a déjà oublié donc on le note)
        region = table.getLibFunc();
        biggestRegion = region;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Expression a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Ou a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Et a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Compar a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Addition a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Soustraction a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Multiplication a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Division a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Sequence a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Negation a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(ID a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(Int a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(ExpressionIdentifiant a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(AppelFonction a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
    }

    @Override
    public ParserRuleContext visit(ArgFonction a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visit'");
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
