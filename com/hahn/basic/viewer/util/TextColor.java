package com.hahn.basic.viewer.util;

import java.awt.Color;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public enum TextColor {
    DARK_BLUE(0x073642),
    LIGHT_BLUE(0x268BD2),
    YELLOW(0xB58900),
    BLACK(0x002B36),
    GREY(0x586E75),
    PALE(0xFDF6E3),
    GREEN(0x859900),
    RED(0xDC322F),
    VIOLET(0x6C71C4),
    MAGENTA(0xD33682),
    ORANGE(0xCB4B16),
    CYAN(0x2AA198);
    
    private int hex;
    private Color color;
    private MutableAttributeSet attributes;
    private TextColor(int hex) {
        this.hex = hex;
        this.color = new Color(hex);
        
        this.attributes = new SimpleAttributeSet();
        StyleConstants.setForeground(this.attributes, this.color);
    }
    
    public int getHex() {
        return hex;
    }
    
    public Color getColor() {
        return color;
    }
    
    public MutableAttributeSet getAttribute() {
        return attributes;
    }
    
    @Override
    public String toString() {
        return "#" + Integer.toHexString(hex);
    }
}
