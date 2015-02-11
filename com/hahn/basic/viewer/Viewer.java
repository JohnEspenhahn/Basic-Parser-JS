package com.hahn.basic.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLDocument;

public class Viewer extends JPanel {
    private static final long serialVersionUID = 4757110261297031804L;
    
    public static Color BACKGROUND = new Color(0xFDF6E3);
    public static Font FONT = new Font("Serif", Font.PLAIN, 26);
    
    JEditorPane textArea;
    
    protected Viewer(String text) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        
        textArea = new JEditorPane();
        textArea.setContentType("text/html");
        textArea.setBackground(BACKGROUND);
        
        String bodyRule = "body { font-family:"+FONT.getFamily() + ";" + "font-size:"+FONT.getSize()+"pt; }";
        ((HTMLDocument) textArea.getDocument()).getStyleSheet().addRule(bodyRule);
        
        textArea.setEditable(false);
        textArea.setText("<html>" + text + "</html>");
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(640, 480));
        scrollPane.setMinimumSize(new Dimension(30, 30));
        
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(scrollPane);
    }
    
    public static void create(String text) {
        //Create and set up the window.
        JFrame frame = new JFrame("Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        frame.add(new Viewer(text));
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
    }
}
