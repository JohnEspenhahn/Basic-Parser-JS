package com.hahn.basic.parser;


import java.util.ArrayList;
import java.util.List;

import com.hahn.basic.Main;
import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.lexer.PackedToken;

public class Node {    
    private final Node parent;
    private final List<Node> children;
    private List<Node> asChildren;
    
    private final String value;
    private final Enum<?> token;
    
    private final int row, col;

    public Node(Node parent, PackedToken packedToken) {
        this(parent, (Enum<?>) packedToken.token, packedToken.value, packedToken.row, packedToken.col);
    }

    public Node(Node parent, Enum<?> token, int row, int col) {
        this(parent, token, null, row, col);
    }
    
    private Node(Node parent, Enum<?> token, String value, int row, int col) {
        this.row = row;
        this.col = col;
        
        this.value = value;
        this.token = token;
        
        this.parent = parent;
        this.children = new ArrayList<Node>();
    }

    private Node() {
        this(null, EnumExpression.START, 0, 0);
    }

    public static Node newTopNode() {
        return new Node();
    }

    public void print() {
        for (Node child : children)
            child.printChildren(0);
    }

    private void printChildren(int space) {
        for (int i = 0; i < space; i++)
            System.out.print("  ");
        System.out.print("|- ");

        if (this.isTerminal()) {
            System.out.println(this);
        } else {
            System.out.println("<" + token + ">");

            for (Node child : children)
                child.printChildren(space + 1);
        }
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
        Main.setLine(row, col);
        
        if (isTerminal()) {
            if (asChildren == null) {
                asChildren = new ArrayList<Node>(1);
                asChildren.add(this);
            }
            
            return asChildren;
        } else {
            return this.children;
        }
    }

    public boolean isTerminal() {
        return value != null;
    }

    public boolean isHead() {
        return parent == null;
    }

    public String getValue() {
        Main.setLine(row, col);
        
        return value;
    }

    public Enum<?> getToken() {
        Main.setLine(row, col);
        
        return token;
    }

    @Override
    public String toString() {
        if (this.isTerminal()) {
            return token + " (" + value + ")";
        } else {
            return children.toString();
        }
    }
}
