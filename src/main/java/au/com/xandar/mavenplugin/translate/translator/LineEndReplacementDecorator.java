package au.com.xandar.mavenplugin.translate.translator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for splitting up a String into line segments and translating each segment on its own.
 * <p/>
 * User: William
 * Date: 23/08/11
 * Time: 9:16 PM
 */
public final class LineEndReplacementDecorator implements Translator {

    private static final Pattern PATTERN = Pattern.compile("\\s*\\\\n\\s*");

    private final Translator translator;
    private boolean debug;

    public LineEndReplacementDecorator(Translator translator) {
        this.translator = translator;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String translate(CharSequence text, String sourceLanguage, String targetLanguage) {

        final StringBuilder output = new StringBuilder();

        int translateStart = 0;

        // Split into one line segments, and translate each in turn.
        final Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            // Replace found SF with generated untranslatable token.
            final String foundText = matcher.group();
            final int lineEndStart = matcher.start();
            final int lineEndFinish = matcher.end();

            // Get Text before this foundRegion, translate it and add it to output. Then add foundText to output.
            final CharSequence requiringTranslation = text.subSequence(translateStart, lineEndStart);
            if (debug) System.out.println("LineEndReplacementDecorator foundText='" + foundText + "'");
            if (debug) System.out.println("LineEndReplacementDecorator translateStart=" + translateStart + " lineEndStart=" + lineEndStart + " lineEndFinish=" + lineEndFinish);
            if (debug) System.out.println("LineEndReplacementDecorator translating='" + requiringTranslation + "'");
            final CharSequence translated = translator.translate(requiringTranslation, sourceLanguage, targetLanguage);
            if (debug) System.out.println("LineEndReplacementDecorator translated='" + translated + "'");
            output.append(translated);
            output.append(foundText);
            if (debug) System.out.println("LineEndReplacementDecorator output='" + output + "'");

            // increment translateStart
            translateStart = lineEndFinish;
        }

        // Now translate and append any remaining string.
        if (translateStart < text.length()) {
            final CharSequence requiringTranslation = text.subSequence(translateStart, text.length());
            if (debug) System.out.println("LineEndReplacementDecorator translating='" + requiringTranslation + "'");
            final CharSequence translated = translator.translate(requiringTranslation, sourceLanguage, targetLanguage);
            if (debug) System.out.println("LineEndReplacementDecorator translated='" + translated + "'");
            output.append(translated);
            if (debug) System.out.println("LineEndReplacementDecorator output='" + output + "'");
        } else {
            if (debug) System.out.println("LineEndReplacementDecorator unexpected translateStart(" + translateStart + ") >= text#length(" + text.length() + ")");
        }

        return output.toString();
    }
}
