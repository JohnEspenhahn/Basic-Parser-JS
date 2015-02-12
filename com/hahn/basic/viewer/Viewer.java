package com.hahn.basic.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.Main;
import com.hahn.basic.parser.Node;
import com.sun.glass.events.KeyEvent;

public class Viewer extends JPanel implements ActionListener, DocumentListener {
    private static final long serialVersionUID = 4757110261297031804L;
    
    private static final int DELAY_TIME = 2500;
    
    public static Color BACKGROUND = new Color(0xFDF6E3);
    public static Font FONT = new Font("Arial", Font.PLAIN, 20);
    
    private long lastTextChange;
    private boolean changed;
    
    private Node node;
    
    JEditorPane textArea;
    JLabel status;
    
    JMenuItem save, open;
    
    protected Viewer(JFrame frame) {
        this(frame, "");
    }
    
    protected Viewer(JFrame frame, String text) {        
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        
        UIManager.put("Menu.font", FONT);
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);        
        frame.setJMenuBar(menuBar);
        
        save = new JMenuItem("Save");
        save.setMnemonic(KeyEvent.VK_S);
        save.addActionListener(this);
        menu.add(save);
        
        open = new JMenuItem("Open");
        open.setMnemonic(KeyEvent.VK_O);
        open.addActionListener(this);
        menu.add(open);
        
        textArea = new JEditorPane();
        textArea.setContentType("text/html");
        textArea.setBackground(BACKGROUND);
        textArea.setDoubleBuffered(true);
        textArea.getDocument().addDocumentListener(this);
        
        try {
            ((HTMLDocument) textArea.getDocument()).getStyleSheet().addRule(
                        StringUtils.join(Files.readAllLines(Paths.get("viewer.css"), StandardCharsets.UTF_8), "")
                    );
        } catch (IOException e) {
            System.err.println("Failed to read 'viewer.css'!");
        }
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(640, 480));
        scrollPane.setMinimumSize(new Dimension(30, 30));
        scrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TextColor.GREY.asColor()));
        
        status = new JLabel();
        status.setFont(FONT);
        status.setPreferredSize(new Dimension(100, 30));
        status.setMinimumSize(new Dimension(100, 30));
        status.setForeground(TextColor.GREY.asColor());
        status.setHorizontalAlignment(SwingConstants.RIGHT);
        status.setHorizontalTextPosition(SwingConstants.RIGHT);
        status.setVerticalAlignment(SwingConstants.CENTER);
        status.setVerticalTextPosition(SwingConstants.CENTER);
        
        add(scrollPane, BorderLayout.CENTER);
        add(status, BorderLayout.PAGE_END);
        
        (new Thread(new ViewerUpdateThread(this))).start();
    }
    
    public void setTextFromNode(Node node) {
        if (node != null) {
            this.node = node;
            
            this.lastTextChange = System.currentTimeMillis();
            textArea.setText("<html><pre>" + node.getFormattedHTML() + "</pre></html>");
            
            this.changed = false;
        }
    }
    
    public void setStatus(String statusText) {
        if (statusText != null) {
            this.status.setText(statusText);
        }
    }
    
    public boolean needsUpdate() {
        return this.changed && (System.currentTimeMillis() - this.lastTextChange > DELAY_TIME);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save) {
            doSaveFile();
        } else if (e.getSource() == open) {
            doOpenFile();
        }
    }   
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        markChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        markChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) { }
    
    private void markChanged() {
        changed = true;
        lastTextChange = System.currentTimeMillis();
    }
    
    private void doSaveFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (!f.exists() || f.isFile()) {
                if (f.isFile()) {
                    int replace = JOptionPane.showConfirmDialog(this, "The file '" + f.getName() + "' already exists. Replace it?", "Replace Existing File?", JOptionPane.YES_NO_OPTION);
                    if (replace != JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(this, "Canceled");
                        return;
                    }
                }
                
                FileOutputStream out = null;
                try {
                    f.delete();
                    f.createNewFile();                    
                    out = new FileOutputStream(f);
                    if (node != null) out.write(node.getFormattedText().getBytes(Charset.defaultCharset()));
                    
                    JOptionPane.showMessageDialog(this, "Saved successfully");
                } catch (Exception err) {
                    err.printStackTrace();
                } finally {
                    if (out != null) { 
                        try {
                            out.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid file selected. Save canceled");
            }
        } else {
            System.out.println("ERROR: Canceled by user");
        }
    }

    private void doOpenFile() {
        Main.getInstance().guiFileInput();
    }
    
    public void update() {
        changed = false;
        
        setStatus("Recompiling...");
        
        String text = textArea.getText();
        int start = text.indexOf("<pre>");
        int end = text.indexOf("</pre>");
        
        text = text.substring(start + 5, end);
        text = text.replaceAll("<br>", "\n");
        text = text.replaceAll("<.+?>", "");
        text = StringEscapeUtils.unescapeHtml4(text);
        
        System.out.println(text);
        
        Main.getInstance().parseLinesFromString(text);
        
        Main.getInstance().reset();
        Main.getInstance().handleInput();
    }


}
