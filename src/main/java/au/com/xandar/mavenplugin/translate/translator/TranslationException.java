package au.com.xandar.mavenplugin.translate.translator;

/**
 * Represents a failure to translate.
 * <p/>
 * I'd like to say "a failure to communicate", but this class is neither Cool Hand Luke nor the Captain.
 *
 * User: William
 * Date: 22/08/11
 * Time: 8:06 PM
 */
public class TranslationException extends RuntimeException {

    public TranslationException(String msg, Throwable th) {
        super(msg, th);
    }
}
