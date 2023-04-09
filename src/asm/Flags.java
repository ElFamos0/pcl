package asm;

public enum Flags {
    EQ, NE, CS, HS, CC, LO, MI, PL, VS, VC, HI, LS, GE, LT, GT, LE, AL;

    public String toString() {
        return this.name().toUpperCase();
    }
}


