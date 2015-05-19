package com.hahn.basic.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.syntaxkits.JavaScriptSyntaxKit;
import jsyntaxpane.util.Configuration;

import com.hahn.basic.Main;
import com.hahn.basic.parser.Node;
import com.hahn.basic.util.exceptions.LexException;
import com.hahn.basic.viewer.controller.ViewerController;
import com.hahn.basic.viewer.util.TextColor;
import com.hahn.basic.viewer.util.TextLineNumber;
import com.hahn.basic.viewer.util.ViewerListener;

public class Viewer extends JPanel implements ActionListener, DocumentListener {
    private static final long serialVersionUID = 4757110261297031804L;
    
    private static final int DELAY_TIME = 2500;
    
    public static Font FONT = new Font("Serif", Font.PLAIN, 24);
    public static Font UI_FONT = new Font("Arial", Font.PLAIN, 20);
    public static Dimension MIN_SIZE = new Dimension(30, 30);
    public static Dimension PREF_SIZE = new Dimension(640, 480);
    
    private List<ViewerListener> viewerListeners;
    
    private long lastTextChange;
    private boolean changed;
    
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
        this.viewerListeners = new ArrayList<ViewerListener>();
        
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
        
        addViewerListener(new ViewerController());
        
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
    
    public String getSourceText() {
        return textArea.getText();
    }
    
    public String getTargetText() {
        return jsArea.getText();
    }
    
    protected boolean needsUpdate() {       
        return this.changed && (System.currentTimeMillis() - this.lastTextChange > DELAY_TIME);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ViewerListener.EnumAction action = null;
        
        Object source = e.getSource();
        if (source == saveAs) {
            action = ViewerListener.EnumAction.SAVE_AS;
        } else if (source == save) {
            action = ViewerListener.EnumAction.SAVE;
        } else if (source == open) {
            action = ViewerListener.EnumAction.OPEN;
        } else if (source == debug) {
            action = ViewerListener.EnumAction.TOGGLE_DEBUG;
        } else if (source == pretty) {
            action = ViewerListener.EnumAction.TOGGLE_PRETTY;
        }
        
        if (action != null) {
            for (ViewerListener listener: viewerListeners) {
                listener.onViewerAction(this, action);
            }
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
    
    public void markChanged() {
        if (!changed) {
            setStatus("");
        }
        
        changed = true;
        lastTextChange = System.currentTimeMillis();
    }
    
    public void update() {        
        setStatus("Recompiling...");
        
        String text = textArea.getText();        
        Main.getInstance().parseLinesFromString(text);        
        Main.getInstance().reset();
        
        try {
            Main.getInstance().handleInput();
            
            setStatus("Compiled"); // setTextFromNode will be called eventually
        } catch (LexException e) {
            setErrorStatus();
        }
    }
    
    public void setTextFromNode(Node n) {
        if (n != null) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    lastTextChange = System.currentTimeMillis();
                    
                    int carot = textArea.getCaretPosition();
                    
                    textArea.setText(n.getFullText());
                    
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
    
    public void setErrorStatus() {
        setStatus("Error");
        
        changed = false;
    }
    
    public void setStatus(String statusText) {
        if (statusText != null) {
            this.status.setText(statusText);
        }
    }

    public void putLineError(int line, String mss) {
        tln.putLineError(line, mss);
    }
    
    public void clearLineErrors() {
        tln.clearLineErrors();
    }
    
    public void addViewerListener(ViewerListener listener) {
        this.viewerListeners.add(listener);
    }
    
    public void removeViewerListener(ViewerListener listener) {
        this.viewerListeners.remove(listener);
    }

}
