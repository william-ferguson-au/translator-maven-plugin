package au.com.xandar.mavenplugin.translate.translator;

/**
 * Responsible providing a blank translation of a String.
 * <p/>
 * Can be used to generate empty translation files ready for filling in via manual translation.
 *
 * User: William
 * Date: 28/08/11
 * Time: 9:14 AM
 */
public final class EmptyStringTranslator implements Translator {

    /**
     * @param text              ignored.
     * @param sourceLanguage    ignored.
     * @param targetLanguage    ignored.
     * @return an empty String.
     */
    public String translate(CharSequence text, String sourceLanguage, String targetLanguage) {
        return "";
    }
}
