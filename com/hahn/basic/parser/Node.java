package com.hahn.basic.parser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.StyledDocument;

import org.apache.commons.lang3.StringUtils;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.lexer.PackedToken;
import com.hahn.basic.viewer.util.TextColor;

public class Node {    
    private final Node parent;
    private final List<Node> children;
    
    private String value;
    private final Enum<?> token;
    
    private String text;
    
    private TextColor color;
    private String errorText;
    
    private final int idx, row, col;

    public Node(Node parent, PackedToken packedToken) {
        this(parent, (Enum<?>) packedToken.token, packedToken.value, packedToken.idx, packedToken.row, packedToken.col);
    }

    public Node(Node parent, Enum<?> token, int idx, int row, int col) {
        this(parent, token, null, idx, row, col);
    }
    
    public Node(Node parent, Enum<?> token, String value, int idx, int row, int col) {
        this.idx = idx;
        this.row = row;
        this.col = col;
        
        // For rendering
        if (value != null) {
            this.text = value;            
            this.value = value.trim();
        }
        
        this.errorText = "";
        
        this.token = token;
        if (token instanceof EnumToken) this.color = ((EnumToken) token).getDefaultColor();
        else this.color = TextColor.GREY;
        
        this.parent = parent;
        this.children = new ArrayList<Node>();
    }

    private Node() {
        this(null, EnumExpression.START, 0, 0, 0);
    }

    public static Node newTopNode() {
        return new Node();
    }
    
    public int getIdx() {
        return idx;
    }
    
    public int getLength() {
        if (value != null) return value.length();
        else return 0;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public TextColor getColor() {
        return color;
    }
    
    /**
     * Change the text value of this node, use
     * with caution
     * @param val The new text value
     */
    public void setValue(String val) {
        this.value = val;
    }
    
    public void setColor(TextColor c) {
        this.color = c;
    }
    
    public void setErrorText(String text) {
        this.errorText = (text == null ? "" : text);
    }
    
    public boolean hasError() {
        return (errorText != null && errorText.length() > 0);
    }

    public void print() {
        for (Node child : children)
            child.printChildren(0);
    }
    
    public String getFormattedText() {
        String str;
        if (isTerminal() && children.size() == 0) {
            return text;
        } else if (isTerminal()) {
            str = text;
        } else {
            str = "";
        }
        
        for (Node child : children) {
            if (child != this) str += child.getFormattedText();
        }
        
        return str;
    }

    public void colorTextArea(StyledDocument doc) {
        if (isTerminal()) {
            doc.setCharacterAttributes(getIdx(), getLength(), color.getAttribute(), false);
        } else {
            for (Node child : children) {
                child.colorTextArea(doc);
            }
        }
    }
    
    private void printChildren(int space) {
        for (int i = 0; i < space; i++)
            System.out.print("  ");
        System.out.print("|- ");

        if (this.isTerminal()) {
            System.out.println(token + " (" + this + ")");
        } else {
            System.out.println("<" + token + ">");

            for (Node child : children)
                child.printChildren(space + 1);
        }
    }
    
    public void clearChildren() {
        children.clear();
    }

    public void addChild(Node child) {
        children.add(child.getTerminalListEnd());
    }
    
    public void addChildren(List<Node> children) {
        for (Node c: children) {
            addChild(c);
        }
    }
    
    private Node getTerminalListEnd() {
        if (children.size() == 1) {
            Node child = children.get(0);
            if (canFlatten()) {
                if (!child.isTerminal()) {
                    return child.getTerminalListEnd();
                } else {
                    return child;
                }
            }
        }
        
        return this;
    }
    
    public boolean canFlatten() {
        return token instanceof IEnumExpression && ((IEnumExpression) token).canFlatten();
    }
    
    public List<Node> getAsChildren() {        
        if (isTerminal()) {
            if (children.isEmpty()) {
                children.add(this);
            }
            
            return children;
        } else {
            return children;
        }
    }

    public boolean isTerminal() {
        return value != null;
    }

    public boolean isHead() {
        return parent == null;
    }

    /**
     * @return If terminal will return the value, otherwise returns null
     */
    public String getValue() {        
        return value;
    }

    public Enum<?> getToken() {        
        return token;
    }
    
    /**
     * Join all of the contained token values into a single string
     * Ex] (add, add) => "++"
     * @return A joined string representation without separating commas
     */
    public String joinToString() {
        if (isTerminal()) {
            return getValue();
        } else {
            return StringUtils.join(children.stream().map(child -> child.joinToString()).toArray());
        }
    }

    /**
     * Join all of the contained token values into a single string. If there
     * is more than one child they are seperated by commas (like a list)
     * Ex] (add, add) => "+,+"
     * @return A joined string representation with separating commas
     */
    @Override
    public String toString() {
        if (isTerminal()) {
            return getValue();
        } else {
            return children.toString();
        }
    }
}
