package au.com.xandar.mavenplugin.translate.transformer.properties;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;

import au.com.xandar.mavenplugin.translate.OrderedTerms;
import au.com.xandar.mavenplugin.translate.transformer.TermPersistor;

/**
 * Responsible for reading a property file.
 * <p/>
 * User: William
 * Date: 21/08/11
 * Time: 6:08 PM
 */
public final class PropertyTermPersistor implements TermPersistor {

    private final OrderedPropertiesPersistor persistor = new OrderedPropertiesPersistor();
    private String sourceFileEncoding;
    private String targetFileEncoding;

    public void setIncludeDateHeader(boolean value) {
        persistor.setIncludeDateHeader(value);
    }

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
        final Reader reader = new InputStreamReader(new FileInputStream(file), sourceFileEncoding);
        try {
            return persistor.load(reader);
        } finally {
            reader.close();
        }
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
            persistor.store(writer, terms);
        } finally {
            writer.close();
        }
    }
}
