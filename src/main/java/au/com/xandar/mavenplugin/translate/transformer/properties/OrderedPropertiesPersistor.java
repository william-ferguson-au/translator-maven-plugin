package au.com.xandar.mavenplugin.translate.transformer.properties;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;

import au.com.xandar.mavenplugin.translate.OrderedTerms;

/**
 * OrderedPropertiesPersistor can load and stored OrderedTerms to and from a stream.
 */
final class OrderedPropertiesPersistor {

    private boolean includeDateHeader;

    public void setIncludeDateHeader(boolean includeDateHeader) {
        this.includeDateHeader = includeDateHeader;
    }

    /**
     * Reads a property list (key and element pairs) from the input character
     * stream in a simple line-oriented format.
     * <p/>
     * Properties are processed in terms of lines. There are two kinds of line,
     * <i>natural lines</i> and <i>logical lines</i>. A natural line is defined
     * as a line of characters that is terminated either by a set of line
     * terminator characters (<code>\n</code> or <code>\r</code> or
     * <code>\r\n</code>) or by the end of the stream. A natural line may be
     * either a blank line, a comment line, or hold all or some of a key-element
     * pair. A logical line holds all the data of a key-element pair, which may
     * be spread out across several adjacent natural lines by escaping the line
     * terminator sequence with a backslash character <code>\</code>. Note that
     * a comment line cannot be extended in this manner; every natural line that
     * is a comment must have its own comment indicator, as described below.
     * Lines are read from input until the end of the stream is reached.
     * <p/>
     * <p/>
     * A natural line that contains only white space characters is considered
     * blank and is ignored. A comment line has an ASCII <code>'#'</code> or
     * <code>'!'</code> as its first non-white space character; comment lines
     * are also ignored and do not encode key-element information. In addition
     * to line terminators, this format considers the characters space (
     * <code>' '</code>, <code>'&#92;u0020'</code>), tab (<code>'\t'</code>, <code>'&#92;u0009'</code>), and form feed
     * (<code>'\f'</code>, <code>'&#92;u000C'</code>) to be white space.
     * <p/>
     * <p/>
     * If a logical line is spread across several natural lines, the backslash
     * escaping the line terminator sequence, the line terminator sequence, and
     * any white space at the start of the following line have no affect on the
     * key or element values. The remainder of the discussion of key and element
     * parsing (when loading) will assume all the characters constituting the
     * key and element appear on a single natural line after line continuation
     * characters have been removed. Note that it is <i>not</i> sufficient to
     * only examine the character preceding a line terminator sequence to decide
     * if the line terminator is escaped; there must be an odd number of
     * contiguous backslashes for the line terminator to be escaped. Since the
     * input is processed from left to right, a non-zero even number of
     * 2<i>n</i> contiguous backslashes before a line terminator (or elsewhere)
     * encodes <i>n</i> backslashes after escape processing.
     * <p/>
     * <p/>
     * The key contains all of the characters in the line starting with the
     * first non-white space character and up to, but not including, the first
     * unescaped <code>'='</code>, <code>':'</code>, or white space character
     * other than a line terminator. All of these key termination characters may
     * be included in the key by escaping them with a preceding backslash
     * character; for example,
     * <p/>
     * <p/>
     * <code>\:\=</code>
     * <p/>
     * <p/>
     * would be the two-character key <code>":="</code>. Line terminator
     * characters can be included using <code>\r</code> and <code>\n</code>
     * escape sequences. Any white space after the key is skipped; if the first
     * non-white space character after the key is <code>'='</code> or
     * <code>':'</code>, then it is ignored and any white space characters after
     * it are also skipped. All remaining characters on the line become part of
     * the associated element string; if there are no remaining characters, the
     * element is the empty string <code>&quot;&quot;</code>. Once the raw
     * character sequences constituting the key and element are identified,
     * escape processing is performed as described above.
     * <p/>
     * <p/>
     * As an example, each of the following three lines specifies the key
     * <code>"Truth"</code> and the associated element value
     * <code>"Beauty"</code>:
     * <p/>
     * <p/>
     * <pre>
     * Truth = Beauty
     * Truth:Beauty
     * Truth			:Beauty
     * </pre>
     * <p/>
     * As another example, the following three lines specify a single property:
     * <p/>
     * <p/>
     * <pre>
     * fruits                           apple, banana, pear, \
     *                                  cantaloupe, watermelon, \
     *                                  kiwi, mango
     * </pre>
     * <p/>
     * The key is <code>"fruits"</code> and the associated element is:
     * <p/>
     * <p/>
     * <pre>
     * &quot;apple, banana, pear, cantaloupe, watermelon, kiwi, mango&quot;
     * </pre>
     * <p/>
     * Note that a space appears before each <code>\</code> so that a space will
     * appear after each comma in the final result; the <code>\</code>, line
     * terminator, and leading white space on the continuation line are merely
     * discarded and are <i>not</i> replaced by one or more other characters.
     * <p/>
     * As a third example, the line:
     * <p/>
     * <p/>
     * <pre>
     * cheeses
     * </pre>
     * <p/>
     * specifies that the key is <code>"cheeses"</code> and the associated
     * element is the empty string <code>""</code>.
     * <p/>
     * <p/>
     * <p/>
     * <a name="unicodeescapes"></a> Characters in keys and elements can be
     * represented in escape sequences similar to those used for character and
     * string literals (see <a href=
     * "http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3"
     * >&sect;3.3</a> and <a href=
     * "http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.10.6"
     * >&sect;3.10.6</a> of the <i>Java Language Specification</i>).
     * <p/>
     * The differences from the character escape sequences and Unicode escapes
     * used for characters and strings are:
     * <p/>
     * <ul>
     * <li>Octal escapes are not recognized.
     * <p/>
     * <li>The character sequence <code>\b</code> does <i>not</i> represent a
     * backspace character.
     * <p/>
     * <li>The method does not treat a backslash character, <code>\</code>,
     * before a non-valid escape character as an error; the backslash is
     * silently dropped. For example, in a Java string the sequence <code>"\z"</code> would
     * cause a compile time error. In contrast, this method silently drops the
     * backslash. Therefore, this method treats the two character sequence
     * <code>"\b"</code> as equivalent to the single character <code>'b'</code>.
     * <p/>
     * <li>Escapes are not necessary for single and double quotes; however, by
     * the rule above, single and double quote characters preceded by a
     * backslash still yield single and double quote characters, respectively.
     * <p/>
     * <li>Only a single 'u' character is allowed in a Uniocde escape sequence.
     * <p/>
     * </ul>
     * <p/>
     * The specified stream remains open after this method returns.
     *
     * @param reader the input character stream.
     * @throws java.io.IOException              if an error occurred when reading from the input stream.
     * @throws IllegalArgumentException if a malformed Unicode escape appears in the input.
     * @return OrderedProperties read by the Reader.
     */
    public OrderedTerms load(Reader reader) throws IOException {
        return load0(new PropertyLineReader(reader));
    }

    /**
     * Reads a property list (key and element pairs) from the input byte stream.
     * The input stream is in a simple line-oriented format as specified in
     * {@link #load(java.io.Reader) load(Reader)} and is assumed to use the ISO
     * 8859-1 character encoding; that is each byte is one Latin1 character.
     * Characters not in Latin1, and certain special characters, are represented
     * in keys and elements using <a href=
     * "http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.3"
     * >Unicode escapes</a>.
     * <p/>
     * The specified stream remains open after this method returns.
     *
     * @param inStream  InputStream from which to read the properties.
     * @throws java.io.IOException if an error occurred when reading from the input stream.
     * @return OrderedProperties read form the InputStream.
     */
    public OrderedTerms load(InputStream inStream) throws IOException {
        return load0(new PropertyLineReader(inStream));
    }

    private OrderedTerms load0(PropertyLineReader lr) throws IOException {

        final OrderedTerms props = new OrderedTerms();

        char[] convtBuf = new char[1024];
        int limit;

        while ((limit = lr.readLine()) >= 0) {
            int keyLen = 0;
            int valueStart = limit;
            boolean hasSep = false;

            // System.out.println("line=<" + new String(lineBuf, 0, limit) +
            // ">");
            boolean precedingBackslash = false;
            while (keyLen < limit) {
                final char c = lr.getLineBuffer()[keyLen];
                // need check if escaped.
                if ((c == '=' || c == ':') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    hasSep = true;
                    break;
                } else if ((c == ' ' || c == '\t' || c == '\f') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    break;
                }
                if (c == '\\') {
                    precedingBackslash = !precedingBackslash;
                } else {
                    precedingBackslash = false;
                }
                keyLen++;
            }
            while (valueStart < limit) {
                final char c = lr.getLineBuffer()[valueStart];
                if (c != ' ' && c != '\t' && c != '\f') {
                    if (!hasSep && (c == '=' || c == ':')) {
                        hasSep = true;
                    } else {
                        break;
                    }
                }
                valueStart++;
            }
            final String key = loadConvert(lr.getLineBuffer(), 0, keyLen, convtBuf);
            final String value = loadConvert(lr.getLineBuffer(), valueStart, limit - valueStart, convtBuf);
            props.setProperty(key, value);
        }

        return props;
    }

    /*
      * Converts encoded &#92;uxxxx to unicode chars and changes special saved
      * chars to their original forms
      */
    private String loadConvert(char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }
                    }
                    out[outLen++] = (char) value;
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = aChar;
            }
        }
        return new String(out, 0, outLen);
    }

    /*
      * Converts unicodes to encoded &#92;uxxxx and escapes special characters
      * with a preceding slash
      */
    private String saveConvert(String theString, boolean escapeSpace,
                               boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        final StringBuilder outBuffer = new StringBuilder(bufLen);

        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            // Handle common case first, selecting largest block that
            // avoids the specials below
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\t':
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                case '\n':
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                case '\r':
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                case '\f':
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >> 8) & 0xF));
                        outBuffer.append(toHex((aChar >> 4) & 0xF));
                        outBuffer.append(toHex(aChar & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    // TODO Remove once I have gleaned what info I can on writing clean output.
    private static void writeComments(BufferedWriter bw, String comments)
            throws IOException {
        bw.write("#");
        int len = comments.length();
        int current = 0;
        int last = 0;
        char[] uu = new char[6];
        uu[0] = '\\';
        uu[1] = 'u';
        while (current < len) {
            char c = comments.charAt(current);
            if (c > '\u00ff' || c == '\n' || c == '\r') {
                if (last != current)
                    bw.write(comments.substring(last, current));
                if (c > '\u00ff') {
                    uu[2] = toHex((c >> 12) & 0xf);
                    uu[3] = toHex((c >> 8) & 0xf);
                    uu[4] = toHex((c >> 4) & 0xf);
                    uu[5] = toHex(c & 0xf);
                    bw.write(new String(uu));
                } else {
                    bw.newLine();
                    if (c == '\r' && current != len - 1
                            && comments.charAt(current + 1) == '\n') {
                        current++;
                    }
                    if (current == len - 1
                            || (comments.charAt(current + 1) != '#' && comments
                            .charAt(current + 1) != '!'))
                        bw.write("#");
                }
                last = current + 1;
            }
            current++;
        }
        if (last != current)
            bw.write(comments.substring(last, current));
        bw.newLine();
    }

    /**
     * Writes this property list (key and element pairs) in this
     * <code>Properties</code> table to the output character stream in a format
     * suitable for using the {@link #load(java.io.Reader) load(Reader)} method.
     * <p/>
     * Properties from the defaults table of this <code>Properties</code> table
     * (if any) are <i>not</i> written out by this method.
     * <p/>
     * If the comments argument is not null, then an ASCII <code>#</code>
     * character, the comments string, and a line separator are first written to
     * the output stream. Thus, the <code>comments</code> can serve as an
     * identifying comment. Any one of a line feed ('\n'), a carriage return
     * ('\r'), or a carriage return followed immediately by a line feed in
     * comments is replaced by a line separator generated by the
     * <code>Writer</code> and if the next character in comments is not
     * character <code>#</code> or character <code>!</code> then an ASCII
     * <code>#</code> is written out after that line separator.
     * <p/>
     * Next, a comment line is always written, consisting of an ASCII
     * <code>#</code> character, the current date and time (as if produced by
     * the <code>toString</code> method of <code>Date</code> for the current
     * time), and a line separator as generated by the <code>Writer</code>.
     * <p/>
     * Then every entry in this <code>Properties</code> table is written out,
     * one per line. For each entry the key string is written, then an ASCII
     * <code>=</code>, then the associated element string. For the key, all
     * space characters are written with a preceding <code>\</code> character.
     * For the element, leading space characters, but not embedded or trailing
     * space characters, are written with a preceding <code>\</code> character.
     * The key and element characters <code>#</code>, <code>!</code>,
     * <code>=</code>, and <code>:</code> are written with a preceding backslash
     * to ensure that they are properly loaded.
     * <p/>
     * After the entries have been written, the output stream is flushed. The
     * output stream remains open after this method returns.
     * <p/>
     *
     * @param writer    Writer with which to sore the properties.
     * @param props     OrderedTerms to be stored.
     * @throws java.io.IOException if writing this property list to the specified output stream throws an <tt>IOException</tt>.
     */
    public void store(Writer writer, OrderedTerms props) throws IOException {
        store0((writer instanceof BufferedWriter) ? (BufferedWriter) writer
                : new BufferedWriter(writer), props, false);
    }

    /**
     * Writes this property list (key and element pairs) in this
     * <code>Properties</code> table to the output stream in a format suitable
     * for loading into a <code>Properties</code> table using the
     * {@link #load(java.io.Reader)} method.
     * <p/>
     * Properties from the defaults table of this <code>Properties</code> table
     * (if any) are <i>not</i> written out by this method.
     * <p/>
     * This method outputs the comments, properties keys and values in the same
     * format as specified in {@link #store(java.io.Writer, au.com.xandar.mavenplugin.translate.OrderedTerms)
     * store(Writer)}, with the following differences:
     * <ul>
     * <li>The stream is written using the ISO 8859-1 character encoding.
     * <p/>
     * <li>Characters not in Latin-1 in the comments are written as
     * <code>&#92;u</code><i>xxxx</i> for their appropriate unicode hexadecimal
     * value <i>xxxx</i>.
     * <p/>
     * <li>Characters less than <code>&#92;u0020</code> and characters greater
     * than <code>&#92;u007E</code> in property keys or values are written as
     * <code>&#92;u</code><i>xxxx</i> for the appropriate hexadecimal value
     * <i>xxxx</i>.
     * </ul>
     * <p/>
     * After the entries have been written, the output stream is flushed. The
     * output stream remains open after this method returns.
     * <p/>
     *
     * @param out       OutputStream in which to write the properties.
     * @param props     OrderedTerms to store.
     * @throws java.io.IOException if writing this property list to the specified output
     *                              stream throws an <tt>IOException</tt>.
     */
    public void store(OutputStream out, OrderedTerms props) throws IOException {
        store0(new BufferedWriter(new OutputStreamWriter(out, "8859_1")), props, true);
    }

    private void store0(BufferedWriter bw, OrderedTerms props, boolean escUnicode) throws IOException {
        if (includeDateHeader) {
            bw.write("#" + new Date().toString());
        }
        bw.newLine();

        synchronized (this) {
            for (String key : props.propertyNames()) {
                String val = props.getProperty(key, "");
                key = saveConvert(key, true, escUnicode);

                /*
                     * No need to escape embedded and trailing spaces for value,
                     * hence pass false to flag.
                     */
                val = saveConvert(val, false, escUnicode);
                bw.write(key + "=" + val);
                bw.newLine();
            }
        }
        bw.flush();
    }

    /**
     * Convert a nibble to a hex character
     *
     * @param nibble the nibble to convert.
     * @return Hex char that is equivalent to the supplied character.
     */
    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    /**
     * A table of hex digits
     */
    private static final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
}
