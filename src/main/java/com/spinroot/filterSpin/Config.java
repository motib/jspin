//  Copyright 2008 by Mordechai (Moti) Ben-Ari.
/**
 * Local configuration class for standalone FilterSpin
 */
package com.spinroot.filterSpin;

public class Config {
    static final String VERSION = "0.1";
    static final String CONFIG_FILE = "config.cfg";
    static final String SEPARATOR = "#";
    static final String USAGE =
            "FilterSpin Version " + VERSION +
                    "  Copyright 2008 (GNU GPL) by Moti Ben-Ari.\n" +
                    "Usage   java com.spinroot.filterSpin.FilterSpin [arguments] file\n" +
                    "  file       File with Spin output\n" +
                    "  -v string  Variables to exclude\n" +
                    "  -s string  Statements to exclude\n" +
                    "     string may be file name or list of identifiers with '/'";

    static void setDefaultProperties(
            java.util.Properties properties) {
        properties.put("PROCESS_WIDTH", Integer.toString(7));
        properties.put("STATEMENT_WIDTH", Integer.toString(18));
        properties.put("VARIABLE_WIDTH", Integer.toString(10));
        properties.put("LINES_PER_TITLE", Integer.toString(20));
        properties.put("PROCESS_TITLE", "Process ");
        properties.put("STATEMENT_TITLE", "Statement ");
        properties.put("MSC", Boolean.toString(false));
    }

    /**
     * Set the default properties and then try to read configuration file
     */
    static void init(java.util.Properties properties) {
        setDefaultProperties(properties);
        try {
            properties.load(new java.io.FileInputStream(CONFIG_FILE));
        } catch (java.io.IOException e1) {
            System.err.println("Cannot open configuration file; using defaults");
        }
    }
}
