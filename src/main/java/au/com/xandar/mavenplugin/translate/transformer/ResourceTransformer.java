package au.com.xandar.mavenplugin.translate.transformer;

import java.io.File;
import java.io.IOException;

/**
 * Responsible for transforming the relevant contents of a file into another language.
 * <p/>
 * User: William
 * Date: 28/08/11
 * Time: 9:20 AM
 */
public interface ResourceTransformer {
    public void setSourceFileEncoding(String sourceFileEncoding);
    public void setTargetFileEncoding(String targetFileEncoding);
    public void transform(File sourceFile, File targetFile, String targetLanguage) throws IOException;
}
