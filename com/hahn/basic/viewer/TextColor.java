package com.hahn.basic.viewer;

public enum TextColor {
    DARK_BLUE("#073642"),
    LIGHT_BLUE("#268BD2"),
    YELLOW("#B58900"),
    BLACK("#002B36"),
    GREY("#586E75");
    
    private String hex;
    private TextColor(String hex) {
        this.hex = hex;
    }
    
    @Override
    public String toString() {
        return hex;
    }
}
