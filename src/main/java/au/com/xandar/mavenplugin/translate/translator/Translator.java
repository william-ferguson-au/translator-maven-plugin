package au.com.xandar.mavenplugin.translate.translator;

import com.google.api.translate.Language;

/**
 * Responsible for translating text from one language to another.
 * <p/>
 * User: William
 * Date: 22/08/11
 * Time: 7:57 PM
 */
public interface Translator {

    /**
     * @param text              Text to translate.
     * @param sourceLanguage    Source language.
     * @param targetLanguage    Language into which to translate.
     * @return the text translated into the target language.
     * @throws TranslationException if the text could not be translated.
     */
    public String translate(CharSequence text, String sourceLanguage, String targetLanguage);
}
