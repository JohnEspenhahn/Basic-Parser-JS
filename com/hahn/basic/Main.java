package com.hahn.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.util.CompileException;

public abstract class Main {
    private static int ROW, COLUMN;    
    private static List<String> LINES = new ArrayList<String>();
    
    public static boolean DEBUG = false, 
                          OPTIMIZE = false;    
    
    protected File inputFile;
    
    public Main() {
        inputFile = null;
    }
    
    public abstract void handleTermInput(String input);
    public abstract void handleFileLine(String str, int line);
    public abstract void handleFileReadComplete();
    
    public void termInput() {
        // Create input scanner
        Scanner scanner = new Scanner(System.in);
         
        String input;
        do {
            System.out.print("> ");
            
            try {
                input = scanner.nextLine();
            } catch (Exception e) {
                input = "";
            }
            
            if (input.equalsIgnoreCase("debug")) {
                Main.toggleDebug();
            } else if (input.equalsIgnoreCase("optimize")) {
                Main.toggleOptimize();
            } else {
                try {               
                    // Reset
                    Main.ROW = 1;
                    Main.LINES.clear();
                    Main.LINES.add(input.trim());
                    
                    handleTermInput(input);
                } catch (CompileException e) {
                    printCompileException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (!input.equalsIgnoreCase("exit"));
        
        scanner.close();
    }
    
    public void fileInput() {        
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            inputFile = chooser.getSelectedFile();
            
            // Reset
            Main.ROW = 1;
            Main.LINES.clear();
            
            Scanner scanner = null;
            try {
                long start = System.currentTimeMillis();
                
                scanner = new Scanner(inputFile);
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    
                    Main.LINES.add(line.trim());
                    handleFileLine(line, Main.ROW);
                    
                    Main.ROW += 1;
                }
                
                handleFileReadComplete();
                
                if (DEBUG) {
                    System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");
                }
            } catch (CompileException e) {
                printCompileException(e);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
                
                inputFile = null;
            }
        } else {
            System.out.println("ERROR: Canceled by user");
        }
    }
    
    private static void toggleDebug() {
        DEBUG = !DEBUG;
        System.out.println("Debug = " + DEBUG);
    }
    
    private static void toggleOptimize() {
        OPTIMIZE = !OPTIMIZE;
        System.out.println("Optimize = " + OPTIMIZE);
    }
    
    public static void printCompileException(CompileException e) {
        if (DEBUG) e.printStackTrace();
        else System.out.println("ERROR: " + e.getMessage());
    }
    
    public static String getLineStr() {
        return getLineStr(getRow());
    }
    
    public static String getLineStr(int line) {
        return LINES.get(line - 1);
    }
    
    public static int getRow() {
        return Main.ROW;
    }
    
    public static int getCol() {
        return Main.COLUMN;
    }
    
    public static void setLine(int row) {
        Main.setLine(row, 0);
    }
    
    public static void setLine(int row, int col) {
        Main.ROW = row;
        Main.COLUMN = col;
    }
    
    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        
        try {
            Main main = new BASICMain(new LangFactory(), EnumToken.class, EnumExpression.class);
            
            if (argsList.contains("--debug") || argsList.contains("-d")) {
                toggleDebug();
            }
            
            // Choose execution mode
            if (argsList.contains("--term") || argsList.contains("-t")) {
                main.termInput();
            } else {
                main.fileInput();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
