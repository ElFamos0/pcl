/* ************************************************************************** */
/*                                                                            */
/*                                                                            */
/*   GraphVizVisitor.java                                                     */
/*                                                                            */
/*   By: Thibault Cheneviere <thibault.cheneviere@telecomnancy.eu>            */
/*                                                                            */
/*   Created: 2022/11/30 17:17:13 by Thibault Cheneviere                      */
/*   Updated: 2022/11/30 17:28:34 by Thibault Cheneviere                      */
/*                                                                            */
/* ************************************************************************** */

package graphViz;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ast.Addition;
import ast.AppelFonction;
import ast.ArgFonction;
import ast.Ast;
import ast.AstVisitor;
import ast.Break;
import ast.Compar;
import ast.DeclarationArrayType;
import ast.DeclarationChamp;
import ast.DeclarationRecordType;
import ast.DeclarationType;
import ast.DeclarationTypeClassique;
import ast.DeclarationFonction;
import ast.DeclarationValeur;
import ast.Definition;
import ast.Division;
import ast.Et;
import ast.ExpressionIdentifiant;
import ast.Expression;
import ast.For;
import ast.ID;
import ast.IfThen;
import ast.IfThenElse;
import ast.Int;
import ast.Multiplication;
import ast.Negation;
import ast.Nil;
import ast.Ou;
import ast.Program;
import ast.Sequence;
import ast.Soustraction;
import ast.While;
import ast.ChaineChr;
import ast.InstanciationType;
import ast.ListeAcces;
import ast.ExpressionArray;
import ast.AccesChamp;

public class GraphVizVisitor implements AstVisitor<String> {
    private int state;
    private String nodeBuffer;
    private String linkBuffer;

    public GraphVizVisitor() {
        this.state = 0;
        this.nodeBuffer = "digraph \"ast\"{\n\n\tnodesep=1;\n\tranksep=1;\n\n";
        this.linkBuffer = "\n";
    }

    public void dumpGraph(String filepath) throws IOException {

        FileOutputStream output = new FileOutputStream(filepath);

        String buffer = this.nodeBuffer + this.linkBuffer + "}";
        byte[] strToBytes = buffer.getBytes();

        output.write(strToBytes);

        output.close();
    }

    private String nextState() {
        int returnedState = this.state;
        this.state++;
        return "N" + returnedState;
    }

    private void addTransition(String from, String dest) {
        this.linkBuffer += String.format("\t%s -> %s; \n", from, dest);

    }

    private void addNode(String node, String label) {
        this.nodeBuffer += String.format("\t%s [label=\"%s\", shape=\"box\"];\n", node, label);

    }

    @Override
    public String visit(Program program) {

        String nodeIdentifier = this.nextState();

        String instructionsState = program.expression.accept(this);

        this.addNode(nodeIdentifier, "Program");
        this.addTransition(nodeIdentifier, instructionsState);

        return nodeIdentifier;
    }

    @Override
    public String visit(Expression expression) {
        String nodeIdentifier = this.nextState();

        String leftState = expression.left.accept(this);
        String rightState = expression.right.accept(this);

        this.addNode(nodeIdentifier, ":=");
        this.addTransition(nodeIdentifier, leftState);
        this.addTransition(nodeIdentifier, rightState);

        return nodeIdentifier;
    }

    @Override
    public String visit(InstanciationType instantiation) {
        String nodeIdentifier = this.nextState();
        this.addNode(nodeIdentifier, "InstanciationType");
        String idvar = instantiation.getId().accept(this);
        this.addTransition(nodeIdentifier, idvar);
        ArrayList<Ast> identifiants = instantiation.getIdentifiants();
        ArrayList<Ast> expressions = instantiation.getExpressions();
        for (int i = 0; i < identifiants.size(); i++) {
            String id = identifiants.get(i).accept(this);
            this.addTransition(nodeIdentifier, id);
            String expr = expressions.get(i).accept(this);
            this.addTransition(nodeIdentifier, expr);
        }

        return nodeIdentifier;
    }

    @Override
    public String visit(Ou ou) {
        String nodeIdentifier = this.nextState();

        String leftState = ou.left.accept(this);
        String rightState = ou.right.accept(this);

        this.addNode(nodeIdentifier, "Ou");
        this.addTransition(nodeIdentifier, leftState);
        this.addTransition(nodeIdentifier, rightState);

        return nodeIdentifier;
    }

    @Override
    public String visit(Et et) {
        String nodeIdentifier = this.nextState();

        String leftState = et.left.accept(this);
        String rightState = et.right.accept(this);

        this.addNode(nodeIdentifier, "Et");
        this.addTransition(nodeIdentifier, leftState);
        this.addTransition(nodeIdentifier, rightState);

        return nodeIdentifier;
    }

    @Override
    public String visit(Compar comp) {
        String nodeIdentifier = this.nextState();

        String leftState = comp.left.accept(this);
        String rightState = comp.right.accept(this);

        this.addNode(nodeIdentifier, comp.operator);
        this.addTransition(nodeIdentifier, leftState);
        this.addTransition(nodeIdentifier, rightState);

        return nodeIdentifier;
    }

    @Override
    public String visit(Addition add) {
        String nodeIdentifier = this.nextState();

        String leftState = add.left.accept(this);
        String rightState = add.right.accept(this);

        this.addNode(nodeIdentifier, "+");
        this.addTransition(nodeIdentifier, leftState);
        this.addTransition(nodeIdentifier, rightState);

        return nodeIdentifier;
    }

    @Override
    public String visit(Soustraction sous) {
        String nodeIdentifier = this.nextState();

        String leftState = sous.left.accept(this);
        String rightState = sous.right.accept(this);

        this.addNode(nodeIdentifier, "-");
        this.addTransition(nodeIdentifier, leftState);
        this.addTransition(nodeIdentifier, rightState);

        return nodeIdentifier;
    }

    @Override
    public String visit(Multiplication mul) {
        String nodeIdentifier = this.nextState();

        String leftState = mul.left.accept(this);
        String rightState = mul.right.accept(this);

        this.addNode(nodeIdentifier, "*");
        this.addTransition(nodeIdentifier, leftState);
        this.addTransition(nodeIdentifier, rightState);

        return nodeIdentifier;
    }

    @Override
    public String visit(Division div) {
        String nodeIdentifier = this.nextState();

        String leftState = div.left.accept(this);
        String rightState = div.right.accept(this);

        this.addNode(nodeIdentifier, "/");
        this.addTransition(nodeIdentifier, leftState);
        this.addTransition(nodeIdentifier, rightState);

        return nodeIdentifier;
    }

    @Override
    public String visit(Sequence sequence) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, sequence.nom);

        for (Ast ast : sequence.seqs) {
            String state = ast.accept(this);
            this.addTransition(nodeIdentifier, state);
        }

        return nodeIdentifier;
    }

    @Override
    public String visit(Negation nega) {
        String nodeIdentifier = this.nextState();
        String instructionsState = nega.expression.accept(this);
        this.addNode(nodeIdentifier, "Negation");
        this.addTransition(nodeIdentifier, instructionsState);
        return nodeIdentifier;
    }

    @Override
    public String visit(ID idf) {
        String nodeIdentifier = this.nextState();
        this.addNode(nodeIdentifier, idf.nom);
        return nodeIdentifier;
    }

    @Override
    public String visit(Int entier) {
        String nodeIdentifier = this.nextState();
        this.addNode(nodeIdentifier, String.valueOf(entier.valeur));
        return nodeIdentifier;
    }

    @Override
    public String visit(ChaineChr chaine) {
        String nodeIdentifier = this.nextState();
        this.addNode(nodeIdentifier, chaine.valeur);
        return nodeIdentifier;
    }

    @Override

    public String visit(Nil chaine) {
        String nodeIdentifier = this.nextState();
        this.addNode(nodeIdentifier, "nil");
        return nodeIdentifier;
    }

    @Override

    public String visit(Break chaine) {
        String nodeIdentifier = this.nextState();
        this.addNode(nodeIdentifier, "break");
        return nodeIdentifier;
    }

    @Override
    public String visit(ExpressionIdentifiant expression) {
        String nodeIdentifier = this.nextState();

        String leftState = expression.left.accept(this);
        if (expression.right != null) {
            String rightState = expression.right.accept(this);
            this.addNode(nodeIdentifier, "exprValeur");
            this.addTransition(nodeIdentifier, leftState);
            this.addTransition(nodeIdentifier, rightState);
        } else {
            this.addNode(nodeIdentifier, "exprValeur");
            this.addTransition(nodeIdentifier, leftState);
        }

        return nodeIdentifier;
    }

    @Override
    public String visit(ListeAcces listeacces) {
        String nodeIdentifier = this.nextState();
        String id = listeacces.getId().accept(this);

        this.addTransition(nodeIdentifier, id);
        if (listeacces.getisExpressionArray()) {
            this.addNode(nodeIdentifier, "ExprArray");
            String expressionarray = listeacces.getExpressionArray().accept(this);
            this.addTransition(nodeIdentifier, expressionarray);
        } else {
            this.addNode(nodeIdentifier, "AccèsChamps");
            for (Ast accesChamp : listeacces.getAccesChamps()) {
                String accesChampState = accesChamp.accept(this);
                this.addTransition(nodeIdentifier, accesChampState);
            }
        }
        return nodeIdentifier;
    }

    @Override
    public String visit(ExpressionArray expressionarray) {
        String nodeIdentifier = this.nextState();
        String id = expressionarray.getId().accept(this);
        String size = expressionarray.getSize().accept(this);
        String expr = expressionarray.getExpr().accept(this);
        this.addNode(nodeIdentifier, "ExpressionArray");
        this.addTransition(nodeIdentifier, id);
        this.addTransition(nodeIdentifier, size);
        this.addTransition(nodeIdentifier, expr);
        return nodeIdentifier;
    }

    @Override
    public String visit(AccesChamp accesChamp) {
        String nodeIdentifier = this.nextState();
        String child = accesChamp.getChild().accept(this);
        if (accesChamp.getisArrayAccess()) {
            this.addNode(nodeIdentifier, "ArrayElt");

        } else {
            this.addNode(nodeIdentifier, "Champ");
        }
        this.addTransition(nodeIdentifier, child);
        return nodeIdentifier;
    }

    @Override
    public String visit(AppelFonction af) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, "AppelFonction");
        // Add subtree for name & subtree for args
        String nameState = af.id.accept(this);
        this.addTransition(nodeIdentifier, nameState);
        String args = af.args.accept(this);
        this.addTransition(nodeIdentifier, args);

        return nodeIdentifier;
    }

    @Override
    public String visit(ArgFonction a) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, "ArgFonction");
        for (Ast ast : a.args) {
            String state = ast.accept(this);
            this.addTransition(nodeIdentifier, state);
        }

        return nodeIdentifier;
    }

    @Override
    public String visit(IfThen ifThen) {
        String nodeIdentifier = this.nextState();

        String conditionState = ifThen.condition.accept(this);
        String thenBlockState = ifThen.thenBlock.accept(this);

        this.addNode(nodeIdentifier, "IfThen");

        this.addTransition(nodeIdentifier, conditionState);
        this.addTransition(nodeIdentifier, thenBlockState);

        return nodeIdentifier;
    }

    @Override
    public String visit(IfThenElse ifThenElse) {
        String nodeIdentifier = this.nextState();

        String conditionState = ifThenElse.condition.accept(this);
        String thenBlockState = ifThenElse.thenBlock.accept(this);
        String elseBlockState = ifThenElse.elseBlock.accept(this);

        this.addNode(nodeIdentifier, "IfThenElse");

        this.addTransition(nodeIdentifier, conditionState);
        this.addTransition(nodeIdentifier, thenBlockState);
        this.addTransition(nodeIdentifier, elseBlockState);

        return nodeIdentifier;
    }

    @Override
    public String visit(While wh) {
        String nodeIdentifier = this.nextState();

        String conditionState = wh.condition.accept(this);
        String blockState = wh.block.accept(this);

        this.addNode(nodeIdentifier, "While");

        this.addTransition(nodeIdentifier, conditionState);
        this.addTransition(nodeIdentifier, blockState);

        return nodeIdentifier;
    }

    @Override
    public String visit(For a) {
        String nodeIdentifier = this.nextState();

        String start = a.start.accept(this);
        String starVal = a.startValue.accept(this);
        String end = a.endValue.accept(this);
        String block = a.block.accept(this);

        this.addNode(nodeIdentifier, "For");

        this.addTransition(nodeIdentifier, start);
        this.addTransition(nodeIdentifier, starVal);
        this.addTransition(nodeIdentifier, end);
        this.addTransition(nodeIdentifier, block);

        return nodeIdentifier;
    }

    @Override
    public String visit(Definition a) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, "Let");
        for (Ast ast : a.declarations) {
            String state = ast.accept(this);
            this.addTransition(nodeIdentifier, state);
        }
        for (Ast ast : a.exprs) {
            String state = ast.accept(this);
            this.addTransition(nodeIdentifier, state);
        }

        return nodeIdentifier;
    }

    @Override
    public String visit(DeclarationType a) {
        String nodeIdentifier = this.nextState();
        this.addNode(nodeIdentifier, "Declaration Type");
        String nameState = a.id.accept(this);
        String typeState = a.type.accept(this);
        this.addTransition(nodeIdentifier, nameState);
        this.addTransition(nodeIdentifier, typeState);
        return nodeIdentifier;
    }

    @Override
    public String visit(DeclarationArrayType a) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, "Array");
        String nameState = a.id.accept(this);
        this.addTransition(nodeIdentifier, nameState);

        return nodeIdentifier;
    }

    @Override
    public String visit(DeclarationTypeClassique a) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, "Type Classique");
        String nameState = a.id.accept(this);
        this.addTransition(nodeIdentifier, nameState);

        return nodeIdentifier;
    }

    @Override
    public String visit(DeclarationRecordType a) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, "Record");
        // String nameState = a.id.accept(this);
        // this.addTransition(nodeIdentifier, nameState);
        for (Ast ast : a.champs) {
            String state = ast.accept(this);
            this.addTransition(nodeIdentifier, state);
        }

        return nodeIdentifier;
    }

    @Override
    public String visit(DeclarationChamp a) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, "Champ");
        String nameState = a.id.accept(this);
        String typeState = a.type.accept(this);
        this.addTransition(nodeIdentifier, nameState);
        this.addTransition(nodeIdentifier, typeState);

        return nodeIdentifier;
    }

    @Override
    public String visit(DeclarationValeur a) {
        String nodeIdentifier = this.nextState();

        this.addNode(nodeIdentifier, "Declaration valeur");
        String nameState = a.id.accept(this);
        this.addTransition(nodeIdentifier, nameState);
        if (a.getType() != null) {
            String typeState = a.getType().accept(this);
            this.addTransition(nodeIdentifier, typeState);
        }
        String exprState = a.expr.accept(this);
        this.addTransition(nodeIdentifier, exprState);
        return nodeIdentifier;
    }

    @Override
    public String visit(DeclarationFonction a) {
        String nodeIdentifier = this.nextState();
        this.addNode(nodeIdentifier, "Declaration Fonction");
        String nameState = a.id.accept(this);
        this.addTransition(nodeIdentifier, nameState);
        for (Ast ast : a.args) {
            String state = ast.accept(this);
            this.addTransition(nodeIdentifier, state);
        }
        if (a.has_return) {
            String typeState = a.return_type.accept(this);
            this.addTransition(nodeIdentifier, typeState);
        }
        String exprState = a.expr.accept(this);
        this.addTransition(nodeIdentifier, exprState);

        return nodeIdentifier;

    }
}
