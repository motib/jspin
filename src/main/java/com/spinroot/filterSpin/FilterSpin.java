// Copyright 2008 by Mordechai (Moti) Ben-Ari. See copyright.txt.
/**
 * Class with main method for standalone FilterSpin
 */
package com.spinroot.filterSpin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class FilterSpin {
    /**
     * Read the files of excluded variables/statements and
     * return as a single string
     */
    private static String readFile(File fc) {
        BufferedReader textReader = null;
        String s = "";
        try {
            textReader = new BufferedReader(new FileReader(fc));
            String line;
            while (true) {
                line = textReader.readLine();
                if (line == null) break;
                else s = s + line + '\n';
            }
            textReader.close();
            return s;
        } catch (IOException e) {
            error("File error " + fc);
            return "";
        }
    }

    /**
     * Read the Spin raw output and call the filter for each line
     */
    private static void filterFile(File fc, Filter filter) {
        BufferedReader textReader = null;
        try {
            textReader = new BufferedReader(new FileReader(fc));
            String line;
            while (true) {
                line = textReader.readLine();
                if (line == null) break;
                line = filter.filter(line);
                if (!line.equals("")) System.out.print(line);
            }
            textReader.close();
        } catch (IOException e) {
            error("File error " + fc);
            System.exit(1);
        }
    }

    static void error(String s) {
        System.err.println(s);
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length == 0) error(Config.USAGE);

        // Last argument is file name
        File file = new File(args[args.length - 1]);

        // Search for excluded file names or strings
        String exc = Config.SEPARATOR, exs = Config.SEPARATOR;
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equals("-v")) exc = args[i + 1];
            else if (args[i].equals("-s")) exs = args[i + 1];

        // Create filter and property objects and initialize
        Filter filter = new Filter();
        java.util.Properties properties = new java.util.Properties();
        Config.init(properties);
        filter.init(properties);

        // Read excluded files (if any) and set excluded data structures
        if (exc.indexOf(Config.SEPARATOR) == -1)
            exc = readFile(new File(exc));
        filter.setExcluded(exc, true);
        if (exs.indexOf(Config.SEPARATOR) == -1)
            exs = readFile(new File(exs));
        filter.setExcluded(exs, false);

        // Filter the raw Spin data
        filterFile(file, filter);
    }
}
