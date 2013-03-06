package au.com.xandar.mavenplugin.translate.translator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for replacing StringFormat snippets with tokens and vice versa.
 * <p/>
 * User: William
 * Date: 23/08/11
 * Time: 9:16 PM
 */
public final class StringFormatReplacementDecorator implements Translator {

    private static final String ANY_WHITESPACE = "\\s*";
    private static final String ARG_INDEX_PATTERN = "(\\d*\\$)?"; // The dollar needs to be escaped because $ is a Regex assertion.
    private static final String OPTIONAL_FLAGS = "[,+ (-0#]?"; // [,+ (-0#]? before quoting
    private static final String OPTIONAL_WIDTH = "\\d*";
    private static final String OPTIONAL_PRECISION = "(.\\d*)?";
    private static final String MANDATORY_CONVERSION_TYPE = "(" +
        "[sScCdoxXfeEgGaAbBhH%n]|" +
        "ta|tA|tb|tB|tc|tC|td|tD|te|eF|th|tH|tI|tj|tk|tk|tl|tL|tm|tM|tN|tp|tQ|tr|tR|ts|tS|tT|ty|tY|tz|tZ" +
    ")";
    private static final String NON_TRANSLATABLE_CHARS = "[-_:]?"; // continue the StringToken if the next chars are untranslatable.

    private static final String STRING_FORMAT_PATTERN =
            ANY_WHITESPACE +
            "%" +
            ARG_INDEX_PATTERN +
            OPTIONAL_FLAGS +
            OPTIONAL_WIDTH +
            OPTIONAL_PRECISION +
            MANDATORY_CONVERSION_TYPE +
            NON_TRANSLATABLE_CHARS +
            ANY_WHITESPACE;

    private static final String MULTI_INSTANCE_PATTERN = "(" + STRING_FORMAT_PATTERN + ")+";

    private static final Pattern PATTERN = Pattern.compile(MULTI_INSTANCE_PATTERN);

    private final Translator translator;

    public StringFormatReplacementDecorator(Translator translator) {
        this.translator = translator;
    }

    public String translate(CharSequence text, String sourceLanguage, String targetLanguage) {
        final TokenReplacer replacement = replaceStringFormatsWithTokens(text);
        final String outputStringWithTokens = translator.translate(replacement.getTokenizedText(), sourceLanguage, targetLanguage);
        return replacement.getTextWithTokensReplaced(outputStringWithTokens);
    }

    /**
     * Replaces StringFormat snippets with tokens in the provided text and provides a StringFormatReplacer that can reinstate them.
     *
     * @param text  Text in which to replace the StringFormat snippets.
     * @return StringFormatReplacer that can reinstate the StringFormat snippets from text containing the replacement tokens.
     */
    private TokenReplacer replaceStringFormatsWithTokens(CharSequence text) {

        int tokenNr = 1;
        final Map<String, String> replacedFormats = new HashMap<String, String>();
        final StringBuilder output = new StringBuilder(text);

        // Find StringFormats
        final Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            // Replace found SF with generated untranslatable token.
            final String token = "STR_TOKEN_" + tokenNr;
            final String foundText = matcher.group();

            //System.out.println("Found='" + foundText + "'");

            // Update the original text to represents the replaced value.
            final String textWithReplacementToken = matcher.replaceFirst(" " + token + " ");
            output.setLength(0);
            output.append(textWithReplacementToken);

            matcher.reset(textWithReplacementToken);

            // Add token and StringFormat to Map.
            replacedFormats.put(token, foundText);
            tokenNr++;
        }

        return new TokenReplacer(output.toString(), replacedFormats);
    }

    private static class TokenReplacer {

        private final CharSequence tokenizedText;
        private final Map<String, String> tokens;

        private TokenReplacer(CharSequence tokenizedText, Map<String, String> tokens) {
            this.tokenizedText = tokenizedText;
            this.tokens = tokens;
        }

        public CharSequence getTokenizedText() {
            return tokenizedText;
        }

        /**
         * Replaces the tokens in the provided text with the original StringFormat instances.
         *
         * @param text  StringBuilder in which to replace the tokens.
         */
        public String getTextWithTokensReplaced(CharSequence text) {

            final StringBuilder output = new StringBuilder(text);

            // First pass is to remove any whitespace surrounding tokens
            // as any surrounding whitepace should have been included in the substituted token.
            // So remove any extra whitespace that has appeared.
            for (Map.Entry<String, String> entry : tokens.entrySet()) {
                int i = output.indexOf(entry.getKey());
                if (i == -1) {
                    continue; // Shouldn't happen
                }

                while ((i > 0) && (output.charAt(i-1) == 32)) {
                    output.deleteCharAt(--i);
                }
                // Now remove any whitespace at the end.
                final int j = i + entry.getKey().length();
                while ((j < output.length()) && (output.charAt(j) == 32)) {
                    output.deleteCharAt(j);
                }
            }

            // 2nd pass is to replace the tokens.
            // It needs to be done in 2 passes otherwise contiguous tokens will remove whitespace from one another.
            for (Map.Entry<String, String> entry : tokens.entrySet()) {
                int i = output.indexOf(entry.getKey());
                if (i == -1) {
                    continue; // Shouldn't happen
                }

                //System.out.println("addingBack='" + entry.getValue() + "' in place of '" + entry.getKey()+ "'");

                output.delete(i, i + entry.getKey().length());
                output.insert(i, entry.getValue());
            }

            return output.toString();
        }
    }
}
