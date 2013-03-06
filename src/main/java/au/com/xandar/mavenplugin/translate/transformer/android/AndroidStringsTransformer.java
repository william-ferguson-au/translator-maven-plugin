package au.com.xandar.mavenplugin.translate.transformer.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import au.com.xandar.mavenplugin.translate.transformer.ResourceTransformer;
import au.com.xandar.mavenplugin.translate.translator.LineEndReplacementDecorator;
import au.com.xandar.mavenplugin.translate.translator.StringFormatReplacementDecorator;
import au.com.xandar.mavenplugin.translate.translator.Translator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Responsible for transforming an AndroidStrings file from one language to another.
 * <p/>
 * User: William
 * Date: 28/08/11
 * Time: 10:45 AM
 */
public class AndroidStringsTransformer implements ResourceTransformer {

    private final String sourceLanguage;
    private final Translator translator;
    private String targetFileEncoding = "UTF-8";

    public AndroidStringsTransformer(String sourceLanguage, Translator translator) {
        this.sourceLanguage = sourceLanguage;

        final LineEndReplacementDecorator lineEndReplacer = new LineEndReplacementDecorator(translator);
        //lineEndReplacer.setDebug(true);

        this.translator = new StringFormatReplacementDecorator(lineEndReplacer);
    }

    /**
     * @param sourceFileEncoding    Ignored as the encoding is read from the file.
     */
    public void setSourceFileEncoding(String sourceFileEncoding) {
        // Nothing to do.
    }

    /**
     * @param targetFileEncoding    File encoding in which to write the translated file. Defaults to UTF-8.
     */
    public void setTargetFileEncoding(String targetFileEncoding) {
        this.targetFileEncoding = targetFileEncoding;
    }

    public void transform(File sourceFile, File targetFile, String targetLanguage) throws IOException {

        final String translatedText;
        final InputStream stream = new FileInputStream(sourceFile);
        try {
            translatedText = parse(stream, sourceLanguage, targetLanguage);
        } finally {
            stream.close();
        }

        writeText(targetFile, translatedText);
    }

    private String parse(InputStream stream, String sourceLanguage, String targetLanguage) throws IOException {
        try {
            final AndroidStringsHandler handler = new AndroidStringsHandler(translator, sourceLanguage, targetLanguage);
            final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();

            // Using a LexicalHandler so that we can replicate comments in the output.
            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);

            saxParser.parse(stream, handler);
            return handler.getTranslatedDocument();
        } catch (ParserConfigurationException e) {
            throw new IOException("Could not create SaxParser", e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }


    private void writeText(File file, String text) throws IOException {

        // Create any folders that need to be created.
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), targetFileEncoding));
        try {
            writer.write(text);
        } finally {
            writer.close();
        }
    }
}