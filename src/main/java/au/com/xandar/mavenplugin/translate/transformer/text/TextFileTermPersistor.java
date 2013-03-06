package au.com.xandar.mavenplugin.translate.transformer.text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

import au.com.xandar.mavenplugin.translate.OrderedTerms;
import au.com.xandar.mavenplugin.translate.transformer.TermPersistor;

/**
 * Responsible for reading a text file as a single term.
 * <p/>
 * User: William
 * Date: 21/08/11
 * Time: 6:08 PM
 */
public final class TextFileTermPersistor implements TermPersistor {

    private static final String TEXT_TERM = "textTerm";

    private String sourceFileEncoding;
    private String targetFileEncoding;

    public void setSourceFileEncoding(String sourceFileEncoding) {
        this.sourceFileEncoding = sourceFileEncoding;
    }

    public void setTargetFileEncoding(String targetFileEncoding) {
        this.targetFileEncoding = targetFileEncoding;
    }

    /**
     * Reads a properties file into an OrderedTerms.
     *
     * @param file  Properties file to read.
     * @return OrderedTerms read from the file.
     * @throws java.io.IOException if the file could not be read.
     */
    public OrderedTerms readTerms(File file) throws IOException {

        final StringBuilder sb = new StringBuilder();
        final LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), sourceFileEncoding));
        try {
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append("\n");
            }
        } finally {
            reader.close();
        }

        final OrderedTerms terms = new OrderedTerms();
        terms.setProperty(TEXT_TERM, sb.toString());
        return terms;
    }

    /**
     * Write the translated terms to a Property file.
     *
     * @param file  File to which to write the terms.
     * @param terms Terms and their translations to write to the file.
     * @throws java.io.IOException if the file could not be written.
     */
    public void writeTerms(File file, OrderedTerms terms) throws IOException {

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), targetFileEncoding));
        try {
            final String term = terms.getProperty(TEXT_TERM);
            writer.append(term);
        } finally {
            writer.close();
        }
    }
}
