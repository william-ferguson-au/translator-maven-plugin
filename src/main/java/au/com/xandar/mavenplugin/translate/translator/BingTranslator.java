package au.com.xandar.mavenplugin.translate.translator;


import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Responsible translating text using the Bing translation API.
 * <p/>
 *
 * User: William
 * Date: 25/10/11
 *
 * See http://code.google.com/p/microsoft-translator-java-api/
 */
public final class BingTranslator implements Translator {

    private boolean debug;
    private int nrRemoteCalls = 0;
    private int nrCallsBeforePause = 100;
    private int millisToPause = 5000;

    public BingTranslator() {
        Translate.setHttpReferrer("http://localhost");
    }

    public void setApiKey(String apiKey) {
        Translate.setKey(apiKey);
    }

    public void setNrCallsBeforePause(int nrCallsBeforePause) {
        this.nrCallsBeforePause = nrCallsBeforePause;
    }

    public void setMillisToPause(int millisToPause) {
        this.millisToPause = millisToPause;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String translate(CharSequence text, String sourceLanguage, String targetLanguage) {
        final Language sourceLang = Language.fromString(sourceLanguage);
        final Language targetLang = Language.fromString(targetLanguage);

        // Replace any \n with an untranslatable LINE_END_TOKEN
        if (debug) System.out.println("Translating [" + sourceLang + "] : '" + text + "'");

        if ("".equals(text)) {
            return ""; // GoogleTranslate throws an Exception when trying to translate an empty string.
        }

        if (++nrRemoteCalls % nrCallsBeforePause ==0){
            System.out.print("\r pausing "+ millisToPause /1000+" seconds every "+ nrCallsBeforePause +" calls to google");
            try {
                Thread.sleep(millisToPause);
            } catch (InterruptedException e) {
                throw new TranslationException("Interrupted waiting ", e);
            }
            System.out.print("\r resuming...");
        }

        try {
            final String output = Translate.execute(text.toString(), sourceLang, targetLang);
            if (debug) System.out.println("To [" + targetLang + "] : '" + output + "'");
            if (debug) System.out.println();
            //printChars(output);
            return output;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new TranslationException("Could not translate : '" + text + "'", e);
        }
    }

    private void printChars(CharSequence text) {
        for (int i = 0; i < text.length(); i++) {
            final char ch = text.charAt(i);
            System.out.println("i=" + i + "   chr=" + ch + "   (int) ch=" + (int) ch);
        }
        System.out.println("end\n");
    }

}
