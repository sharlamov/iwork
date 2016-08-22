package md.sh.bin;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Created by sharlamov on 18.07.2016.
 */
public class Util {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public final static int INDENT = 1;

    public static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}
