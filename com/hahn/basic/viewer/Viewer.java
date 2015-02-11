package com.hahn.basic.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLDocument;

public class Viewer extends JPanel {
    private static final long serialVersionUID = 4757110261297031804L;
    
    public static Color BACKGROUND = new Color(0xFDF6E3);
    public static Font FONT = new Font("Serif", Font.PLAIN, 26);
    
    JEditorPane textArea;
    JLabel status;
    
    protected Viewer() {
        this("");
    }
    
    protected Viewer(String text) {        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND);
        
        textArea = new JEditorPane();
        textArea.setContentType("text/html");
        textArea.setBackground(BACKGROUND);
        textArea.setDoubleBuffered(true);
        textArea.setEditable(false);
        
        String bodyRule = "body { font-family:"+FONT.getFamily() + ";" + "font-size:"+FONT.getSize()+"pt; }";
        ((HTMLDocument) textArea.getDocument()).getStyleSheet().addRule(bodyRule);
        
        updateTextArea(text);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(640, 480));
        scrollPane.setMinimumSize(new Dimension(30, 30));
        scrollPane.setBorder(new LineBorder(BACKGROUND, 1));
        
        status = new JLabel();
        status.setFont(FONT);
        status.setBackground(BACKGROUND);
        status.setText("Status bar");
        status.setHorizontalAlignment(SwingConstants.RIGHT);
        
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(scrollPane);
        add(status);
    }
    
    protected void updateTextArea(String text) {
        textArea.setText("<html><pre>" + text + "</pre></html>");
    }
    
    private static Viewer view;    
    public static void create() {
        if (view != null) System.err.println("Attempted to create viewer twice!");
        
        //Create and set up the window.
        JFrame frame = new JFrame("Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        view = new Viewer();
        frame.add(view);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.toFront();
    }
    
    public static void setText(String text) {
        if (view != null) {
            view.updateTextArea(text);
        } else {
            System.err.println("Tried to set viewer text before it was created!");
        }
    }
}
