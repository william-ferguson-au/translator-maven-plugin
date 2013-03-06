package au.com.xandar.mavenplugin.translate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.plugin.logging.Log;

/**
 * Parses a term by pushing snippets into an EventProcessor.
 */
final class TermParser {

    private final Log log;

    public TermParser(Log log) {
        this.log = log;
    }

    private Log getLog() {
        return log;
    }

    /**
     * Parse term, notifying the EventProcessor of content and markup that is found.
     *
     * @param bytes             Array of byte to be translated.
     * @param eventProcessor    EventProcessor to notify as content and mark up is found.
     * @throws java.io.IOException if the stream could not be parsed.
     */
    public void parse(byte[] bytes, EventProcessor eventProcessor) throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(bytes);
        final StringBuilder sb = new StringBuilder();
        int ch = 0;
        while (ch != -1) {
            if (!isMarkup(ch)) {
                ch = inputStream.read();
            }
            if (ch == -1) {
                break; // EOF so stop.
            }

            if ((char) ch == '$') {
                onContentEnd(sb, eventProcessor);
                ch = skipMarkup('$', ']', inputStream, eventProcessor);
                continue;
            }
            if ((char) ch == '<') {
                onContentEnd(sb, eventProcessor);
                ch = skipMarkup('<', '>', inputStream, eventProcessor);
                continue;
            }
            if ((char) ch == '{') {
                onContentEnd(sb, eventProcessor);
                ch = skipMarkup('{', '}', inputStream, eventProcessor);
                continue;
            }

            // If the next character is content then add it to the String buffer.
            // Else attempt to translate whatever is in the String buffer.
            if (isContent(ch)) {
                sb.append((char) ch);
            } else {
                onContentEnd(sb, eventProcessor);
                ch = skipToContent(ch, inputStream, eventProcessor);
                if (ch != -1 && !isMarkup(ch)) {
                    sb.append((char) ch);
                }
            }
        }
        onContentEnd(sb, eventProcessor);
        inputStream.close();
    }

    /**
     * Called when we have reached the end of a block of content.
     * <p>
     *     Notifies the EventProcessor of a new piece of content to add to the translation.
     * </p>
     * @param sb                StringBuilder holding the content that has been found.
     * @param eventProcessor    EventProcessor to notify
     * @throws IOException if the translation cannot be processed.
     */
    private void onContentEnd(StringBuilder sb, EventProcessor eventProcessor) throws IOException {
        if (sb.length() > 0) {
            final String content = sb.toString();
            final String translatedText = eventProcessor.foundContent(content);
            getLog().debug("\nBEFORE[" + content + "]");
            getLog().debug("AFTER[" + translatedText + "]");
            sb.setLength(0);
        }
    }

    /**
     * Skip to the next block of content or mark up.
     *
     * @param ch                Current character.
     * @param inputStream       InputStream from which to read characters.
     * @param eventProcessor    EventProcessor to notify of non-content that was read.
     * @return Character that was last read.
     * @throws IOException if a character failed to be read.
     */
    private int skipToContent(int ch, InputStream inputStream, EventProcessor eventProcessor) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append((char) ch);
        while (true) {
            ch = inputStream.read();
            if (ch == -1) {
                break;
            }
            if (isContent(ch) || isMarkup(ch)) {
                break;
            } else {
                sb.append((char) ch);
            }
        }
        eventProcessor.foundMarkup(sb.toString());
        return ch;
    }

    /**
     * Skips all characters until the end (mark up) character is found.
     *
     * @param start             Current character.
     * @param end               Character at which to stop reading, ie the end of the mark up.
     * @param inputStream       InputStream from which to read characters.
     * @param eventProcessor    EventProcessor to notify about the mark up that was found.
     * @return the last character read.
     * @throws IOException if a character failed to be read.
     */
    private int skipMarkup(int start, int end, InputStream inputStream, EventProcessor eventProcessor) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append((char) start);
        int ch;
        while (true) {
            ch = inputStream.read();
            if (ch == -1) {
                break;
            }
            sb.append((char) ch);
            if (ch == end) {
                break;
            }
        }
        eventProcessor.foundMarkup(sb.toString());
        return ch;
    }

    /**
     * Also allow diacritics.
     *
     * @param c Character to test as content.
     * @return true if the character is determined to be content.
     */
    private boolean isContent(int c) {
        if (Character.isLetterOrDigit(c)) {
            return true;
        }
        return (c == ' ' || c == '\t' || c == '\'' || c == ',' || c == '.' || c == '?' || c == '!');
    }

    private boolean isMarkup(int c) {
        return (c == '{' || c == '<' || c == '$');
    }
}