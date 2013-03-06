package au.com.xandar.mavenplugin.translate.transformer.android;

import au.com.xandar.mavenplugin.translate.translator.BingTranslator;
import au.com.xandar.mavenplugin.translate.translator.GoogleTranslator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

/**
 * Responsible for testing the AndroidStringsParser.
 * <p/>
 * User: William
 * Date: 23/08/11
 * Time: 9:36 PM
 */
public class TestAndroidStringsTransformer {

    private static final String SOURCE_LANG = "en";
    //private static final String[] TARGET_LANG = {"en", "de", "zh"};
    private static final String[] TARGET_LANG = {"de", "zh"};

    private static final File OUTPUT_FOLDER = new File("target/test-translation-hack/androidStrings-transformer");

    private GoogleTranslator translator = new GoogleTranslator(); //new BingTranslator();

    @Before
    public void setUp() {
        // translator.setApiKey("F78B61630900A397F5DFFD522F8B35BE76DD04D6"); // BingKey
        translator.setApiKey("AIzaSyDl5cCiQT1cm6I9yzvPI6EXQBaFJAPdy0A"); // GoogleKey
        translator.setDebug(true);
    }

    //@Test
    public void testStringWithInjections() throws Exception {
        testResource("StringsWithInjections");
    }

    //@Test
    public void testFinishButtonString() throws Exception {
        testResource("finish_button_string");
    }

    //@Test
    public void testParseLineEndStrings() throws Exception {
        testResource("LineEndStrings");
    }

    //@Test
    public void testParseSimpleStrings() throws Exception {
        testResource("SimpleStrings");
    }

    //@Test
    public void testParseStringFormats() throws Exception {
        testResource("StringFormats");
    }

    //@Test
    public void testParseStringArray() throws Exception {
        testResource("StringArray");
    }

    private static final File SOURCE_FOLDER = new File("src/test/resources/au/com/xandar/mavenplugin/translate/transformer");

    private void testResource(String resourceName) throws Exception {
        for (final String targetLang : TARGET_LANG) {
            final String outputFileName = resourceName + "-" + targetLang + ".xml";

            final AndroidStringsTransformer transformer = new AndroidStringsTransformer(SOURCE_LANG, translator);
            final File sourceFile = new File(SOURCE_FOLDER, resourceName + "-base.xml");
            final File targetFile = new File(OUTPUT_FOLDER, outputFileName);

            System.out.println("Transforming " + resourceName + " from " + SOURCE_LANG + " to " + targetLang);
            transformer.transform(sourceFile, targetFile, targetLang);

            final String expected = readFile(new File(SOURCE_FOLDER, outputFileName));
            final String actual = readFile(new File(OUTPUT_FOLDER, outputFileName));

            Assert.assertEquals("Translation to '" + targetLang + "' should match expected for " + resourceName, expected, actual);
        }
    }

    private String readFile(File file) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
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

        return sb.toString();
    }
}
