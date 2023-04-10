package asm;

public enum Flags {
    EQ, NE, CS, HS, CC, LO, MI, PL, VS, VC, HI, LS, GE, LT, GT, LE, AL, NI;

    public String toString() {
        if (this == NI) {
            return "";
        }
        return this.name().toUpperCase();
    }
}


