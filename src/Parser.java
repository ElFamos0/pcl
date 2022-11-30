import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.io.IOException;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import parser.*;   
import parser.exprParser.ProgramContext;
import java.awt.event.MouseListener;
import java.awt.event.KeyListener;
import java.awt.Point;

public class Parser implements MouseListener, KeyListener {
    public void mouseClicked(java.awt.event.MouseEvent arg0){
        Point p = viewer.getLocation();
        if (arg0.getButton() == 1){
            viewer.setScale(viewer.getScale()+0.1);
            p.x -= 200;
        } else if (arg0.getButton() == 3){
            viewer.setScale(viewer.getScale()-0.1);
            p.x += 200;
        }
        viewer.setLocation(p);
    }
    
    public void keyPressed(java.awt.event.KeyEvent arg0) {
        Point p = viewer.getLocation();
        switch (arg0.getKeyCode()){
            case java.awt.event.KeyEvent.VK_UP:
                p.y += 20;
                break;
            case java.awt.event.KeyEvent.VK_DOWN:
                p.y -= 20;
                break;
            case java.awt.event.KeyEvent.VK_LEFT:
                p.x += 20;
                break;
            case java.awt.event.KeyEvent.VK_RIGHT:
                p.x -= 20;
                break;
            }
        viewer.setLocation(p);
    }

    private JPanel panel = new JPanel();
    private JFrame frame = new JFrame("Antlr AST");
    private TreeViewer viewer;

    public Parser(String testFile) {
        try {

            //chargement du fichier et construction du parser
            CharStream input = CharStreams.fromFileName(testFile);
            exprLexer lexer = new exprLexer(input); 
            CommonTokenStream stream = new CommonTokenStream(lexer);
            exprParser parser = new exprParser(stream);

            ProgramContext program = parser.program();

            // code d'affichage de l'arbre syntaxique
            viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()),program);
            viewer.setScale(0.75); // Scale a little
            panel.add(viewer);
            frame.addKeyListener(this);
            panel.addMouseListener(this);
            frame.add(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } 
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("erreur io");
        }
        catch (RecognitionException e) {
            e.printStackTrace();
            System.out.println("erreur de reconnaissance");
        }
    }
  
    public void mousePressed(java.awt.event.MouseEvent arg0){
        // do nothing
    }
    
    public void mouseReleased(java.awt.event.MouseEvent arg0){
        // do nothing
    }
    
    public void mouseEntered(java.awt.event.MouseEvent arg0){
        // do nothing
    }
    
    public void mouseExited(java.awt.event.MouseEvent arg0) {
        // do nothing
    }
    
    public void keyReleased(java.awt.event.KeyEvent arg0){
        // do nothing
    }
  
    public void keyTyped(java.awt.event.KeyEvent arg0) {
        // do nothing
    }
}