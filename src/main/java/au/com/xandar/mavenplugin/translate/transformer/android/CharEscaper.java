package au.com.xandar.mavenplugin.translate.transformer.android;

/**
 * Responsible for escaping/unescaping back slashes within text such that it is suitable for either AndroidStrings or normal consumption.
 * <p/>
 * NB leaves \n intact, ie it doesn't remove the back slash in that instance.
 *
 * User: William
 * Date: 23/08/11
 * Time: 9:16 PM
 */
final class CharEscaper {

    private static final char LINE_END = '\n';

    /**
     * Looks for any chars escaped by a back slash (such as single and double quotes and back slashes)
     * and removes the back slash so that the text makes sense to the translator.
     *
     * @param text  StringBuilder from which to remove back slashes.
     */
    public void removeBackslashes(StringBuilder text) {
        boolean escaping = false;
        for (int i = 0; i < text.length(); ) {
            final char chr = text.charAt(i);

            if (escaping && (chr == 'n')) {
                // CR LF - Replace '\' 'n' with '\n'
                //text.deleteCharAt(i - 1);
                //text.deleteCharAt(i - 1);
                //text.insert(i - 1, LINE_END);

                // Ignore \n
                escaping = false;
                i++;
            } else if (escaping) {
                // Remove the backslash
                text.deleteCharAt(i - 1);
                escaping = false;
            } else if (chr == '\\') { // Found backslash
                escaping = true;
                i++;
            } else {
                escaping = false;
                i++;
            }
        }
    }

    /**
     * Looks for any chars that should be escaped by a back slash in an AndroidStrings file and inserts a backslash before them.
     *
     * @param text  StringBuilder into which to add back slashes.
     */
    public void addBackslashes(StringBuilder text) {

        //System.out.println("AndroidStrings-addBackslashes-start");
        //PrintHelper.printChars(text);

        for (int i = 0; i < text.length(); ) {
            final char chr = text.charAt(i);

            if ((chr == '\\') && (i+1 < text.length()) && (text.charAt(i+1) == 'n')) {
                // Found '\' + 'n' - ignore it.
                i++;
            } else if ((chr == '\'') || (chr == '\\') || (chr == '"')) {
                text.insert(i, '\\');
                i++;
                i++;
            } else {
                i++;
            }
        }
    }
}