/*
    Config - configuration constants and file
      Copyright 2005-10 by Mordechai (Moti) Ben-Ari. See SpinSpider.java
*/
package com.spinroot.spinSpider;

public class Config {
    public static final String[] names = {"", "-trail-1", "-trail-2", "-automata"};
    static final String VERSION = "6.0";
    // Properties data structure is used for command names
    // Initialize properties from file or create file or from defaults
    static final String CONFIG_FILE = "config.cfg";
    static final int MAX_STACK = 1000;    // Stack for processing -DCHECK file
    static final int INITIAL = 100;          // For ArrayLists
    static final int DELTA = 1000;        // For node offset in automata
    static final String TITLE =
            "SpinSpider Version " + VERSION +
                    " Copyright 2005-7 (GNU GPL) by Moti Ben-Ari.";
    static final String USAGE =
            "Usage   java com.spinroot.spinSpider.SpinSpider arguments file\n\n" +
                    "  file     Promela source file\n\n" +
                    "  -p       number of processes (must be >= 1)\n" +
                    "  -vname   name of a variable (one parameter per variable)\n\n" +
                    "  -dot     dot format\n" +
                    "  -Txxx    dot format and run DOT to convert to xxx format\n" +
                    "           (default is png)\n" +
                    "  -fsm     fsm format\n\n" +
                    "  -t1      use trail file to emphasize a path\n" +
                    "  -t2      use trail file to draw a path\n" +
                    "  -a       draw automata corresponding to source\n\n" +
                    "  -small   size of dot graph is smaller\n" +
                    "  -bold    bold style instead of color for path\n" +
                    "  -debug   write internal tables to file.dbg";
    static final String dotTrailBold = " style = bold";
    static final String dotTrailColor = " color = red";
    static final String[] smallDotPrologue = {
            "\tgraph [size=\"12.5,7.5\",ranksep=.20];",
            "\tnode [shape=box,fontname=Helvetica,fontsize=10];",
            "\tnode [width=1.25,height=0.75,fixedsize=true];"
    };
    static final String[] largeDotPrologue = {
            "\tgraph [size=\"16,12\",ranksep=.25];",
            "\tnode [shape=box,fontname=Helvetica,fontsize=14];",
            "\tnode [width=1.6,height=1.2,fixedsize=true];"
    };
    static final String[] automataDotPrologue = {
            "\tgraph [size=\"16,12\",ranksep=.4];",
            "\tnode [shape=circle,fontname=Helvetica,fontsize=14];",
            "\tedge [fontname=Helvetica,fontsize=14];"
    };

    static public void setDefaultProperties(
            java.util.Properties properties) {
        // Spin version: format changed with Spin 6
        properties.put("VERSION", "6");

        properties.put("C_COMPILER", "c:\\mingw\\bin\\gcc.exe");
        properties.put("SPIN", "bin\\spin.exe");
        properties.put("PAN", "pan.exe");
        properties.put("DOT", "bin\\dot.exe");
    }

    static public void init(java.util.Properties properties) {
        setDefaultProperties(properties);
        try {
            properties.load(new java.io.FileInputStream(CONFIG_FILE));
        } catch (java.io.IOException e1) {
            System.err.println("Cannot open configuration file; using defaults");
        }
    }

}
