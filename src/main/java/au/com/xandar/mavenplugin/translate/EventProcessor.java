package au.com.xandar.mavenplugin.translate;

import au.com.xandar.mavenplugin.translate.translator.Translator;

/**
 * Responsible for accepting notifications of text to translate and/or store.
 * <p/>
 * User: William
 * Date: 21/08/11
 * Time: 10:33 AM
 */
final class EventProcessor {

    //Boolean debug;
    private final String sourceLanguage;
    private final String destLanguage;
    private final String key;
    private final OrderedTerms terms;
    private final Translator translator;

    public EventProcessor(Translator translator, String sourceLanguage, String destLanguage, String key, OrderedTerms terms) {
        this.translator = translator;
        this.sourceLanguage = sourceLanguage;
        this.destLanguage = destLanguage;
        this.key = key;
        this.terms = terms;
    }

    /**
     * Appends the mark up without any translation.
     *
     * @param markup    String to append untranslated.
     */
    public void foundMarkup(String markup) {
        // if (debug) {
        // System.out.println("markup0:[" + markup + "]");
        // }

        // <REV 1.3; RSavage> we no longer need to escape the line feed,
        //                    as the 'OrderedTerms' class should take
        //                    case of all character escaping
        //markup = markup.replaceAll("\n", "\\\\n");
        //writer.write(markup);

        // !! POSSIBLE BUG !! - now that the code has been converted over
        //                      to using the 'OrderedTerms' to write
        //                      to translated property files, we no longer
        //                      have control over managing multi-line
        //                      property values, the current implementation
        //                      just writes the entire property value to a
        //                      single line.
        //if (markup.equalsIgnoreCase("<br>")) {
        //    writer.write("\\\r\n");

        // set named property value;
        // append to any existing value in the property
        final String currentValue = terms.getProperty(key,"");
        terms.setProperty(key, currentValue + markup);
    }

    /**
     * Translates the content and appends it.
     *
     * @param content   Content to be translated and appended.
     * @return translated content.
     */
    public String foundContent(String content) {
        final String translatedText;
        if (content.trim().length() > 0) {
            final String prefix = stripFront(content); // extract whitespace from start
            final String suffix = stripEnd(content); // extract whitespace from end
            content = content.trim();
            final String coreText = translator.translate(content, sourceLanguage, destLanguage);
            translatedText = prefix + coreText + suffix;
        } else {
            translatedText = content;
        }

        final String currentValue = terms.getProperty(key,"");
        terms.setProperty(key, currentValue + translatedText);

        return translatedText;
    }

    /**
     * @param s String from which to strip leading white space.
     * @return the whitespace from the front of a string.
     */
    private String stripFront(String s) {
        final int len = s.length();
        int st = 0;
        final char[] val = s.toCharArray();
        while ((st < len) && (val[st] <= ' ')) {
            st++;
        }
        return (st > 0) ? s.substring(0, st) : "";
    }

    /**
     * @param s String from which to strip trailing white space.
     * @return the whitespace from the end of a string.
     */
    private String stripEnd(String s) {
        int len = s.length();
        int c = 0;
        final char[] val = s.toCharArray();
        while (len > 0 && val[len - 1] <= ' ') {
            len--;
            c++;
        }
        return (c > 0) ? s.substring(s.length() - c) : "";
    }
}
