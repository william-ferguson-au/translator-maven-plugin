package au.com.xandar.mavenplugin.translate.transformer.android;

/**
 * Responsible for printing some text for debug.
 * <p/>
 * User: William
 * Date: 28/08/11
 * Time: 4:54 PM
 */
public class PrintHelper {
    public static void printChars(CharSequence text) {
        System.out.println("lineLength=" + text.length());
        System.out.println("line=" + text.toString());
        for (int i = 0; i < text.length(); i++) {
            System.out.println("i=" + i + "   char=" + text.charAt(i) + "   (int) char=" + (int) text.charAt(i));
        }
        System.out.println();
    }
}
