package com.hahn.basic.intermediate;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CodeLines {
    private int row, col;
    private Stack<Integer> rows, columns;
    
    private List<String> lines;
    
    public CodeLines() {
        rows = new Stack<Integer>();
        columns = new Stack<Integer>();
        
        lines = new ArrayList<String>();
    }
    
    public void reset() {
        lines.clear();
    }
    
    public List<String> getLines() {
        return lines;
    }
    
    public void add(String line) {
        lines.add(line);
    }
    
    public String getLineStr() {
        return getLineStr(getRow());
    }
    
    public String getLineStr(int line) {
        return lines.get(line - 1);
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public void setLine(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    public void pushLine(int row, int col) {
        rows.push(this.row);
        columns.push(this.col);
        
        setLine(row, col);
    }
    
    public void popLine() {
        setLine(rows.pop(), columns.pop());
    }
    
}
