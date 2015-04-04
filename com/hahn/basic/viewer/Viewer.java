package com.hahn.basic.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

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
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.html.HTMLDocument;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.syntaxkits.JavaScriptSyntaxKit;
import jsyntaxpane.util.Configuration;

import com.hahn.basic.Main;
import com.hahn.basic.parser.Node;
import com.hahn.basic.viewer.util.TextColor;
import com.hahn.basic.viewer.util.TextLineNumber;

public class Viewer extends JPanel implements ActionListener, DocumentListener {
    private static final long serialVersionUID = 4757110261297031804L;
    
    private static final int DELAY_TIME = 2500;
    
    public static Font FONT = new Font("Serif", Font.PLAIN, 24);
    public static Font UI_FONT = new Font("Arial", Font.PLAIN, 20);
    public static Dimension MIN_SIZE = new Dimension(30, 30);
    public static Dimension PREF_SIZE = new Dimension(640, 480);
    
    private long lastTextChange;
    private boolean changed;
    
    private Node node;
    
    TextLineNumber tln;
    JTextPane textArea;
    JEditorPane jsArea;
    JLabel status;
    
    JMenuItem save, saveAs, open;
    JMenuItem debug, pretty;
    
    protected Viewer(JFrame frame) {
        this(frame, "");
    }
    
    protected Viewer(JFrame frame, String text) {        
        setLayout(new BorderLayout());
        setBackground(TextColor.PALE.getColor());
        
        ToolTipManager.sharedInstance().setInitialDelay(100);
        ToolTipManager.sharedInstance().setDismissDelay(10000);
        
        UIManager.put("Menu.font", UI_FONT);
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu optionsMenu = new JMenu("Options");
        menuBar.add(fileMenu);     
        menuBar.add(optionsMenu);
        frame.setJMenuBar(menuBar);
        
        save = new JMenuItem("Save");
        save.setMnemonic(KeyEvent.VK_S);
        save.addActionListener(this);
        fileMenu.add(save);
        
        saveAs = new JMenuItem("Save As");
        saveAs.addActionListener(this);
        fileMenu.add(saveAs);
        
        open = new JMenuItem("Open");
        open.setMnemonic(KeyEvent.VK_O);
        open.addActionListener(this);
        fileMenu.add(open);
        
        debug = new JMenuItem("Toggle Debug");
        debug.addActionListener(this);
        optionsMenu.add(debug);
        
        pretty = new JMenuItem("Toggle Pretty Print");
        pretty.addActionListener(this);
        optionsMenu.add(pretty);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(TextColor.PALE.getColor());
        tabbedPane.setForeground(TextColor.GREY.getColor());
        // tabbedPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TextColor.GREY.getColor()));
        
        createViewer(tabbedPane);
        createJSViewer(tabbedPane);
        
        status = new JLabel();
        status.setFont(UI_FONT);
        status.setPreferredSize(new Dimension(100, 30));
        status.setMinimumSize(new Dimension(100, 30));
        status.setForeground(TextColor.GREY.getColor());
        status.setHorizontalAlignment(SwingConstants.RIGHT);
        status.setHorizontalTextPosition(SwingConstants.RIGHT);
        status.setVerticalAlignment(SwingConstants.CENTER);
        status.setVerticalTextPosition(SwingConstants.CENTER);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(status, BorderLayout.PAGE_END);
        
        (new Thread(new ViewerUpdateThread(this))).start();
    }
    
    private void createViewer(JTabbedPane tabbedPane) {
        textArea = new JTextPane();
        textArea.setBackground(tabbedPane.getBackground());
        textArea.setForeground(tabbedPane.getForeground());
        textArea.setFont(FONT);
        textArea.setPreferredSize(PREF_SIZE);
        textArea.setMinimumSize(MIN_SIZE);
        textArea.setDoubleBuffered(true);
        textArea.getDocument().addDocumentListener(this);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(PREF_SIZE);
        scrollPane.setMinimumSize(MIN_SIZE);
        
        tln = new TextLineNumber(textArea);
        tln.setForeground(textArea.getForeground());
        tln.setCurrentLineForeground(TextColor.BLACK.getColor());
        scrollPane.setRowHeaderView(tln);
        
        tabbedPane.addTab("Editor", scrollPane);
    }
    
    private void createJSViewer(JTabbedPane tabbedPane) {        
        Configuration config = DefaultSyntaxKit.getConfig(JavaScriptSyntaxKit.class);
        config.put("Components", "jsyntaxpane.components.PairsMarker");
        config.put("PopupMenu", "cut-to-clipboard, copy-to-clipboard, -, find, find-next, goto-line, jump-to-pair");
        config.put("DefaultFont", String.format("%s-%s-%s", FONT.getFamily(), "PLAIN", FONT.getSize()));
        
        DefaultSyntaxKit.initKit();
        
        jsArea = new JEditorPane();
        jsArea.setBackground(tabbedPane.getBackground());
        jsArea.setForeground(tabbedPane.getForeground());
        jsArea.setFont(FONT);
        jsArea.setPreferredSize(PREF_SIZE);
        jsArea.setMinimumSize(MIN_SIZE);
        jsArea.setDoubleBuffered(true);
        jsArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(jsArea);
        scrollPane.setPreferredSize(PREF_SIZE);
        scrollPane.setMinimumSize(MIN_SIZE);
        
        jsArea.setContentType("text/javascript");
        
        TextLineNumber jsTln = new TextLineNumber(jsArea);
        jsTln.setForeground(jsArea.getForeground());
        jsTln.setCurrentLineForeground(TextColor.BLACK.getColor());
        scrollPane.setRowHeaderView(jsTln);
        
        tabbedPane.addTab("Target", scrollPane);
    }
    
    public void setTextFromNode(Node n) {
        if (n != null) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    node = n;
                    
                    lastTextChange = System.currentTimeMillis();
                    
                    textArea.setText(n.getFullText());
                    
                    int carot = textArea.getCaretPosition();
                    int maxPosition = textArea.getText().length();
                    if (carot >= maxPosition) carot = maxPosition;
                    textArea.setCaretPosition(carot);
                    
                    n.colorTextArea(textArea.getStyledDocument());
                    
                    // XXX JS Target code
                    jsArea.setText(Main.getInstance().getLangBuildTarget().toString());
                    
                    changed = false;
                }
            });
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
        Object source = e.getSource();
        if (source == saveAs) {
            doSaveAsFile();
        } else if (source == save) {
            doSaveFile();
        } else if (source == open) {
            doOpenFile();
        } else if (source == debug) {
            Main.getInstance().toggleDebug();
            markChanged();
        } else if (source == pretty) {
            Main.getInstance().togglePretty();
            markChanged();
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
        setStatus("");
        
        changed = true;
        lastTextChange = System.currentTimeMillis();
    }
    
    private void doSaveFile() {
        File f = Main.getInstance().getInputFile();
        if (f != null) saveToFile(f);
        else doSaveAsFile();
    }
    
    private void doSaveAsFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            saveToFile(chooser.getSelectedFile());
        } else {
            System.out.println("ERROR: Canceled by user");
        }
    }
    
    private void saveToFile(File f) {
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
                if (node != null) out.write(node.getFullText().getBytes(Charset.defaultCharset()));
                
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
    }

    private void doOpenFile() {
        Main.getInstance().guiFileInput();
    }
    
    public void update() {
        changed = false;
        
        setStatus("Recompiling...");
        
        String text = textArea.getText();        
        Main.getInstance().parseLinesFromString(text);        
        Main.getInstance().reset();
        Main.getInstance().handleInput();
        
        setStatus("Compiled");
    }

    public void putLineError(int line, String mss) {
        tln.putLineError(line, mss);
    }
    
    public void clearLineErrors() {
        tln.clearLineErrors();
    }

}
