/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * Read and write the SpinSpider properties file for this program
 */

package jspin;
import java.io.*;
import java.util.Properties;

class SpiderFile {
	private Properties properties;
	private String fileName;
	
	SpiderFile (Properties p, String f) {
		properties = p;
		fileName = f + ".spd";
	}
	
	private void setDefaultProperties() {
		properties.put("TRAIL_CODE",   Integer.toString(0));
		properties.put("DOT_SIZE",     Integer.toString(0));
		properties.put("TRAIL_STYLE",  Integer.toString(0));
		properties.put("SPIDER_DEBUG", Boolean.toString(false));
		properties.put("PROCESSES",    Integer.toString(2));
		properties.put("FORMAT",       Config.PNG);
		properties.put("VARIABLES",    "");
	}
	
	// Initialize configuration file
    public void init() {
        setDefaultProperties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(fileName);
        } catch (FileNotFoundException e1) {
            System.out.println(
				"Cannot open SpinSpider file, creating new file");
            try {
                saveFile();
                in = new FileInputStream(fileName);
            } catch (IOException e2) {
                System.err.println("Cannot write SpinSpider file");
            }
        }
        try {
            properties.load(in);
            in.close();
        } catch (IOException e3) {
            System.err.println("Cannot read SpinSpider file");
        }
    }

	// Save configuration file
    void saveFile() {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            properties.store(out, "SpinSpider configuration file");
            out.close();
            System.out.println("Saved SpinSpider file " + fileName);
        } catch (IOException e2) {
            System.err.println("Cannot write SpinSpider file");
        }
    }

	// Interface to get/set propertyies of various types
    public String getStringProperty(String s) {
        return properties.getProperty(s);
    }

    void setStringProperty(String s, String newValue) {
        properties.setProperty(s, newValue);
    }

    public boolean getBooleanProperty(String s) {
        return Boolean.valueOf(properties.getProperty(s)).booleanValue();
    }

    void setBooleanProperty(String s, boolean newValue) {
        properties.setProperty(s, Boolean.toString(newValue));
    }

    public int getIntProperty(String s) {
        return Integer.valueOf(properties.getProperty(s)).intValue();
    }

    void setIntProperty(String s, int newValue) {
        properties.setProperty(s, Integer.toString(newValue));
    }
}
