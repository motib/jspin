/* Copyright 2005 by Mordechai (Moti) Ben-Ari. See copyright.txt. */
/*
 * File filter for jSpin
*/

package jspin;
import java.io.File;
import javax.swing.filechooser.*;

public class JSpinFileFilter extends FileFilter {

    private String ext1 = "";
    private String ext2 = "";
    private String ext3 = "";
    private String description = "";

    public JSpinFileFilter(String d, String e1, String e2, String e3) {
        ext1 = e1; ext2 = e2; ext3 = e3; description = d;
    }
    
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        else {
            String e = f.getName().toUpperCase();
            return e.endsWith(ext1) || 
				   (ext2 == null ? false : e.endsWith(ext2)) || 
				   (ext3 == null ? false : e.endsWith(ext3)); 
        }
    }

    public String getDescription() {
        return description;
    }
}
