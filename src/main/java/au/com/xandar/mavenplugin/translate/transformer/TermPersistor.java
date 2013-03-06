package au.com.xandar.mavenplugin.translate.transformer;

import java.io.File;
import java.io.IOException;

import au.com.xandar.mavenplugin.translate.OrderedTerms;

/**
 * Responsible for reading a file containing terms to translate.
 * <p/>
 * User: William
 * Date: 21/08/11
 * Time: 6:03 PM
 */
public interface TermPersistor {

    public OrderedTerms readTerms(File file) throws IOException;

    /**
     * Write the translated terms to a Property file.
     *
     * @param file  File to which to write the terms.
     * @param terms Terms and their translations to write to the file.
     * @throws java.io.IOException if the file could not be written.
     */
    public void writeTerms(File file, OrderedTerms terms) throws IOException;
}
