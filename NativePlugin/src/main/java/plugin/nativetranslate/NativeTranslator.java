package plugin.nativetranslate;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

@Mojo(name = "translate")
public class NativeTranslator extends AbstractMojo {

    private static String ls = System.getProperty("line.separator");
    @Parameter(property = "translate.workDir", defaultValue = "${project.build.outputDirectory}")
    protected File workDir;
    @Parameter(property = "translate.includes", defaultValue = "**/*.properties")
    protected String[] includes;
    @Parameter(property = "translate.tempDir", defaultValue = "**/*.properties")
    protected File tempDir;

    public static void main(String[] strings) throws MojoExecutionException {
        translateFile(new File("D:/message_ru.properties"));
    }

    public static String translate(String src) {
        final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
        final StringBuilder result = new StringBuilder();
        for (final Character character : src.toCharArray()) {
            if (asciiEncoder.canEncode(character)) {
                result.append(character);
            } else {
                result.append("\\u");
                result.append(Integer.toHexString(0x10000 | character).substring(1).toUpperCase());
            }
        }
        return result.toString();
    }

    public static void translateFile(File file) {
        try {
            String text = readFile(file);
            writeFile(file, translate(text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(File file, String text) throws IOException {
        BufferedWriter bwr = new BufferedWriter(new FileWriter(file));
        try {
            bwr.write(translate(text));
            bwr.newLine();
            bwr.flush();
        } finally {
            bwr.close();
        }
    }

    private static String readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();

        try {
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append(ls);
            }
            return builder.toString();
        } finally {
            br.close();
        }
    }

    public void execute() throws MojoExecutionException {
        getLog().info("Start native2ascii translation");
        if (workDir.exists()) {
            Iterator files;
            try {
                String incl = StringUtils.join(includes, ",");
                files = FileUtils.getFiles(workDir, incl, null).iterator();
            } catch (IOException e) {
                throw new MojoExecutionException("Unable to get list of files");
            }

            while (files.hasNext()) {
                File file = (File) files.next();
                translateFile(file);
            }
        }
    }
}
