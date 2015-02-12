package com.hahn.basic.viewer;

import java.awt.Color;

public enum TextColor {
    DARK_BLUE(0x073642),
    LIGHT_BLUE(0x268BD2),
    YELLOW(0xB58900),
    BLACK(0x002B36),
    GREY(0x586E75),
    GREEN(0x859900);
    
    private int hex;
    private Color color;
    private TextColor(int hex) {
        this.hex = hex;
        this.color = new Color(hex);
    }
    
    public int getHex() {
        return hex;
    }
    
    public Color asColor() {
        return this.color;
    }
    
    @Override
    public String toString() {
        return "#" + Integer.toHexString(hex);
    }
}
