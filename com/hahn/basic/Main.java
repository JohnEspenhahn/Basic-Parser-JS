package com.hahn.basic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;

import com.hahn.basic.definition.EnumExpression;
import com.hahn.basic.definition.EnumToken;
import com.hahn.basic.target.LangBuildTarget;
import com.hahn.basic.target.js.JSLangFactory;
import com.hahn.basic.util.EnumInputType;
import com.hahn.basic.util.exceptions.CompileException;

public abstract class Main {
    private static int ROW, COLUMN;    
    private static List<String> LINES = new ArrayList<String>();
    
    public static boolean DEBUG = false; 
    
    private static final String FILE_KEY = "file";
    private static final String DIR_KEY = "dir";
    private Map<String, String> values;
    
    private EnumInputType inputType;
    protected File inputFile;
    
    public Main() {
        this.inputType = null;
        this.inputFile = null;
        
        this.values = new HashMap<String, String>();
    }
    
    public abstract LangBuildTarget getLangBuildTarget();
    
    public abstract void printShellTitle();
    
    public abstract void handleTermInput(String input);
    public abstract void handleFileLine(String str, int line);
    public abstract void handleFileReadComplete();
    
    public void setInputType(EnumInputType type) {
        if (this.inputType == null) this.inputType = type;
        else throw new IllegalArgumentException("Only one input type parameter is allowed");
    }
    
    public void setValue(String name, String value) {
        if (!values.containsKey(name)) values.put(name, value);
        else throw new IllegalArgumentException("Duplicate parameter `" + name + "`");
    }
    
    public String getValue(String name) {
        return values.get(name);
    }
    
    public File getTargetFile() {
        return new File(inputFile.getAbsolutePath() + "." + getLangBuildTarget().getExtension());
    }
    
    public void run() {
        if (inputType == EnumInputType.SHELL) {
            shellInput();
        } else if (inputType == EnumInputType.GUI_FILE) {
            guiFileInput();
        } else if (inputType == EnumInputType.FILE) {
            fileInput(new File(getValue(FILE_KEY)));
        } else if (inputType == EnumInputType.DIR) {
            dirInput(getValue(DIR_KEY));
        } else {
            printHelp();
        }
    }
    
    public void printHelp() {
        System.out.println("Usage: cmp [-options] input");
        System.out.println();
        System.out.println("Options:");
        System.out.println(" --debug                       Run in debug mode");
        System.out.println();
        System.out.println("Input Modes:");
        System.out.println(" -d [<path>], --dir [<path>]   Attempt to compile a project");
        System.out.println("                               in the given directory <path>");
        System.out.println(" -f <path>, --file <path>      Compile file at the given");
        System.out.println("                               file <path>");
        System.out.println(" -g, --gui                     Select file to be compiled");
        System.out.println("                               with the gui file selector");
        System.out.println(" -s, --shell                   Start an input shell");
    }
    
    public void printShellHelp() {
        System.out.println(":debug   Toggle debug mode");
        System.out.println(":help    Print this");
        System.out.println(":exit    Quit the shell");
        System.out.println();
    }
    
    public void shellInput() {
        printShellTitle();
        
        // Create input scanner
        Scanner scanner = new Scanner(System.in);
         
        String input;
        while (true) {
            System.out.print("> ");
            
            try {
                input = scanner.nextLine();
            } catch (Exception e) {
                input = "";
            }
            
            if (input.equalsIgnoreCase(":debug")) {
                Main.toggleDebug();
            } else if (input.equalsIgnoreCase(":help")) {
                printShellHelp();
            } else if (input.equalsIgnoreCase(":exit")) {
                break;
            } else {
                try {               
                    // Reset
                    Main.setLine(1);
                    Main.LINES.clear();
                    Main.LINES.add(input.trim());
                    
                    handleTermInput(input);
                } catch (CompileException e) {
                    printCompileException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        scanner.close();
    }
    
    public void guiFileInput() {        
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(null);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            fileInput(chooser.getSelectedFile());
        } else {
            System.out.println("ERROR: Canceled by user");
        }
    }
    
    public void fileInput(File file) {
        this.inputFile = file;
        
        // Reset
        Main.setLine(1);
        Main.LINES.clear();
        
        Scanner scanner = null;
        try {
            long start = System.currentTimeMillis();
            
            scanner = new Scanner(this.inputFile);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                Main.LINES.add(line.trim());
                handleFileLine(line, Main.ROW);
                
                Main.ROW += 1;
            }
            
            handleFileReadComplete();
            
            if (DEBUG) {
                System.out.println();
                System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");
            }
        } catch (CompileException e) {
            printCompileException(e);
        } catch (FileNotFoundException e) {
            System.err.println("Could not find the file `" + getValue(FILE_KEY) + "`");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            
            inputFile = null;
        }
    }
    
    public void dirInput(String dir) {
        if (dir == null) dir = System.getProperty("user.dir");
        File dirFile = new File(dir);
        
        System.out.println("Compiling files in directory `" + dir + "` with extension `" + getLangBuildTarget().getInputExtension() + "`");
        
        if (dirFile.isDirectory()) {            
            Scanner scanner = null;
            try {
                // Reset
                Main.setLine(1);
                Main.LINES.clear();
                
                int found = 0;
                long start = System.currentTimeMillis();                
                this.inputFile = new File(dirFile, "main");
                
                for (File f: dirFile.listFiles()) {
                    String extension = f.getName().replaceFirst("^.+\\.", "");
                    if (f.isFile() && extension.equals(getLangBuildTarget().getInputExtension())) {
                        found += 1;
                        
                        scanner = new Scanner(f);
                        while(scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            
                            Main.LINES.add(line.trim());
                            handleFileLine(line, Main.ROW);
                            
                            Main.ROW += 1;
                        }
                        scanner.close();
                        scanner = null;
                    }
                }
                
                handleFileReadComplete();
                
                System.out.println();
                if (found == 0) {
                    System.err.println("No files found in the directory `" + dir + "` with the file extension `." + getLangBuildTarget().getInputExtension() + "`");
                }
                
                System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms");
            } catch (CompileException e) {
                printCompileException(e);
            } catch (FileNotFoundException e) {
                System.err.println("Could not find the directory `" + dir + "`");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
                
                inputFile = null;
            }
        } else {
            throw new IllegalArgumentException("The given directory path is not a directory");
        }
    }
    
    private static void toggleDebug() {
        DEBUG = !DEBUG;
        System.out.println("Debug = " + DEBUG);
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
        try {
            Main main = new BASICMain(new JSLangFactory(), EnumToken.class, EnumExpression.class);
            
            String s;
            for (int i = 0; i < args.length; i++) {
                s = args[i];
                if (s.equals("--debug")) {
                    toggleDebug();
                } else if (s.equals("--gui") || s.equals("-g")) {
                    main.setInputType(EnumInputType.GUI_FILE);
                } else if (s.equals("--shell") || s.equals("-s")) {
                    main.setInputType(EnumInputType.SHELL);
                } else if (s.equals("--file") || s.equals("-f")) {
                    main.setInputType(EnumInputType.FILE);
                    if (i+1 < args.length && !args[i+1].startsWith("-")) {
                        main.setValue(FILE_KEY, args[++i]);
                    } else {
                        throw new IllegalArgumentException("No file name provided after `" + s + "`");
                    }
                } else if (s.equals("--dir") || s.equals("-d")) {
                    main.setInputType(EnumInputType.DIR);
                    if (i+1 < args.length && !args[i+1].startsWith("-")) {
                        main.setValue(DIR_KEY, args[++i]);
                    }
                } else {
                    throw new IllegalArgumentException("Illegal command line parameter `" + s + "`");
                }
            }
            
            // Choose execution mode
            main.run();
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}
