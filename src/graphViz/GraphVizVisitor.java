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

import ast.Addition;
import ast.AppelFonction;
import ast.Ast;
import ast.AstVisitor;
import ast.Compar;
import ast.Division;
import ast.Et;
import ast.ExprValeur;
import ast.Expression;
import ast.ID;
import ast.Int;
import ast.Multiplication;
import ast.Negation;
import ast.Ou;
import ast.Program;
import ast.Sequence;
import ast.Soustraction;

public class GraphVizVisitor implements AstVisitor<String> {
    private int state;
    private String nodeBuffer;
    private String linkBuffer;

    public GraphVizVisitor(){
        this.state = 0;
        this.nodeBuffer = "digraph \"ast\"{\n\n\tnodesep=1;\n\tranksep=1;\n\n";
        this.linkBuffer = "\n";
    }

	public void dumpGraph(String filepath) throws IOException{
            
        FileOutputStream output = new FileOutputStream(filepath);

        String buffer = this.nodeBuffer + this.linkBuffer + "}";
        byte[] strToBytes = buffer.getBytes();

        output.write(strToBytes);

        output.close();
    }

	private String nextState(){
        int returnedState = this.state;
        this.state++;
        return "N"+ returnedState;
    }

    private void addTransition(String from,String dest){
        this.linkBuffer += String.format("\t%s -> %s; \n", from,dest);

    }

    private void addNode(String node,String label){
        this.nodeBuffer += String.format("\t%s [label=\"%s\", shape=\"box\"];\n", node,label);

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
        if (expression.right != null) {
            String rightState = expression.right.accept(this);
            // Le nom dépend du type du fils de droite
            this.addNode(nodeIdentifier, "Expression");
            this.addTransition(nodeIdentifier, leftState);
            this.addTransition(nodeIdentifier, rightState);
        } else {
            this.addNode(nodeIdentifier, "Expression");
            this.addTransition(nodeIdentifier, leftState);
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

        this.addNode(nodeIdentifier, "Comparaison");
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
        
        this.addNode(nodeIdentifier, "Sequence");

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
    public String visit(ExprValeur expression) {
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
    public String visit(AppelFonction af) {
        String nodeIdentifier = this.nextState();
        
        this.addNode(nodeIdentifier, "Arguments");

        for (Ast ast : af.args) {
            String state = ast.accept(this);
            this.addTransition(nodeIdentifier, state);
        }

        return nodeIdentifier;
    }   
}
