package au.com.xandar.mavenplugin.translate.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Responsible for testing TestLineEndDecorator.
 * <p/>
 * User: William
 * Date: 1/09/11
 * Time: 6:05 PM
 */
public class TestLineEndReplacementDecorator {

    private static class  TestTranslator implements Translator {
        private List<String> request = new ArrayList<String>();
        public String translate(CharSequence text, String sourceLanguage, String targetLanguage) {
            request.add(text.toString());
            return text.toString();
        }
    }

    private static final String SOURCE_LANG = "en";
    private static final String TARGET_LANG = "de";

    @Test
    public void testSingleLineEnd() throws Exception {
        final String foo = "Well done!\\nYou found another one.";
        final TestTranslator testTranslator = new TestTranslator();

        final LineEndReplacementDecorator replacer = new LineEndReplacementDecorator(testTranslator);
        //replacer.setDebug(true);

        replacer.translate(foo, SOURCE_LANG, TARGET_LANG);

        Assert.assertEquals(Arrays.asList("Well done!", "You found another one."), testTranslator.request);
    }

    @Test
    public void testSpaceAfterLineEnd() throws Exception {
        final String foo =
                "Jumblee is a word game where you must find as many words of four or more letters in the allotted time.\n" +
                        "\\n - Time runs out, or\n";

        final String expected = "Jumblee is a word game where you must find as many words of four or more letters in the allotted time.\n\\n - Time runs out, or\n";

        final TestTranslator testTranslator = new TestTranslator();

        final LineEndReplacementDecorator replacer = new LineEndReplacementDecorator(testTranslator);
        //replacer.setDebug(true);

        replacer.translate(foo, SOURCE_LANG, TARGET_LANG);

        Assert.assertEquals(expected, foo);

        Assert.assertEquals(
                Arrays.asList(
                    "Jumblee is a word game where you must find as many words of four or more letters in the allotted time.",
                    "- Time runs out, or\n"
                ),
                testTranslator.request);
    }
}
