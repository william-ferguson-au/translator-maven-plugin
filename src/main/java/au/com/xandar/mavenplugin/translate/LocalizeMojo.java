package au.com.xandar.mavenplugin.translate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import au.com.xandar.mavenplugin.translate.transformer.ResourceTransformer;
import au.com.xandar.mavenplugin.translate.transformer.android.AndroidStringsTransformer;
import au.com.xandar.mavenplugin.translate.translator.BingTranslator;
import au.com.xandar.mavenplugin.translate.translator.EmptyStringTranslator;
import au.com.xandar.mavenplugin.translate.translator.GoogleTranslator;
import au.com.xandar.mavenplugin.translate.translator.Translator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Generate Language files.
 *
 * @goal localize
 */
public final class LocalizeMojo extends AbstractMojo {

    /**
     * Folder in which the files to be translated can be found.
     *
     * @parameter
     * @required
     */
    private File sourceFolder;

    /**
     * File patterns to apply within sourceFolder of the files to translate.
     *
     * @parameter
     * @required
     */
    private String[] sourceFileSets;

    /**
     * Folder in which to place the translated files.
     * <p>
     *     Translated files will be offset the same amount within the target folder as they are within the source folder.
     * </p>
     *
     * @parameter default-value="${basedir}/target/translated"
     * @required
     */
    private File targetFolder;

    /**
     * @parameter default-value="en"
     * @required
     */
    private String sourceLanguage;

    /**
     * The languages to which to translate.
     *
     * @parameter
     * @required
     */
    private String[] targetLanguages;

    /**
     * The type of file being translated.
     * <p>
     * Valid values are propertiesFile, textFile or androidStrings.
     * </p>
     *
     * @parameter
     * @required
     */
    private TranslationType translationType;

    /**
     * File encoding of the source file.
     * <p/>
     * Supports: ASCII
     * Cp1252
     * ISO8859_1, ISO-8859_1
     * UTF8, UTF-8
     * UTF-16
     * UTF-16BE, UnicodeBigUnmarked
     * UTF-16LE, UnicodeLittleUnmarked
     * UnicodeBig
     * UnicodeLittle
     * See http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html
     *
     * @parameter default-value="ISO8859_1"
     * @required
     */
    private String sourceFileEncoding;


    /**
     * File encoding for the generated files.
     * <p/>
     * Supports: ASCII
     * Cp1252
     * ISO8859_1, ISO-8859_1
     * UTF8, UTF-8
     * UTF-16
     * UTF-16BE, UnicodeBigUnmarked
     * UTF-16LE, UnicodeLittleUnmarked
     * UnicodeBig
     * UnicodeLittle
     * See http://java.sun.com/j2se/1.3/docs/guide/intl/encoding.doc.html
     *
     * @parameter default-value="ISO8859_1"
     * @required
     */
    private String targetFileEncoding;


    /**
     * (Optional)
     * If this option is enabled, then this mojo will create
     * the empty shell resource files for each translation
     * language defined.  These shell files will include the
     * property keys but will contain no translated values,
     * all the value will be empty.  This feature can be used
     * when you are ready to generate empty shell files to
     * distribute to a translation service/company.
     * <p/>
     *
     * @parameter
     */
    private boolean createEmptyFiles;


    // TranslatorService specific params.
    /**
     * Translation service to use. Options are 'Google' (paid service since Oct-2011), 'Bing'.
     *
     * @parameter default-value="Google"
     * @required
     */
    private String translationService;

    /**
     * API Key for the translation service.
     *
     * You can sign up for the Bing one here http://www.bing.com/developers/createapp.aspx
     * The Google API Key is available from Google APIs https://code.google.com/apis/console
     *
     * @parameter
     */
    private String apiKey;

    /**
     * Maximum number of calls to google before pauzing.
     * Google will block the translation service if too many calls are made from on source within a short period.
     * To prevent this, the plugin will pause for "pauzeSeconds" after " maxCalls".
     *
     * @parameter expression="1500"
     */
    private int maxCalls;

    /**
     * Pause in seconds after maxCalls have been made
     *
     * @parameter expression="180"
     */
    private int pauseSeconds;


    // PropertyFile transformer params

    /**
     * (Optional)
     * Include a data header in the generated property files.
     * <p/>
     * Setting this to 'false' ensures your generated files are unchanged, if nothing functionally changes.
     *
     * @parameter default-value="true"
     */
    //private boolean includeDateHeader;


    public LocalizeMojo() {
        // This is the constructor used my maven-plugin-plugin.
    }

    private LocalizeMojo(File outputFolder) {

        // This constructor is just here to remove the compiler warnings for all the attributes above that are not explicilty set in Java code.
        // A better solution would be to use Java5 annotations to denote params etc.
        // There are a couple of attempts to do this, but nothing seems 100%.

        this.maxCalls = 0;
        this.pauseSeconds = 0;

        this.translationType = null;
        this.sourceLanguage = null;
        this.targetLanguages = null;
        this.sourceFileEncoding = null;
        this.targetFileEncoding = null;
        this.sourceFolder = null;
        this.sourceFileSets = null;
        this.targetFolder = outputFolder;

        this.createEmptyFiles = false;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {

        final Translator translator = getTranslator();
        final ResourceTransformer transformer = getTransformer(translator);

        final Collection<File> files = getFilesToProcess();
        getLog().info(files.size() + " files being translated into " + targetLanguages.length + " languages");

        getLog().debug("sourceLanguage: [" + sourceLanguage + "]");
        for (final File sourceFile : files) {
            for (String targetLanguage : targetLanguages) {
                final File targetFile = getDestinationFile(sourceFile, targetLanguage);
                getLog().info("sourceFile: [" + sourceFile.getName() + "]   language=" + targetLanguage);
                try {
                    transformer.transform(sourceFile, targetFile, targetLanguage);
                } catch (IOException e) {
                    throw new MojoExecutionException("Could not transform " + sourceFile + " to " + targetLanguage, e);
                }
            }
        }
        getLog().info("");
    }

    private ResourceTransformer getTransformer(Translator translator) throws MojoExecutionException {
        // TODO return a Transformer based upon the translationType (textFile, propertyFile, androidStrings).
        if (translationType.equals(TranslationType.propertiesFile)) {
        } else if (translationType.equals(TranslationType.textFile)) {
        } else if (translationType.equals(TranslationType.androidStrings)) {
            final ResourceTransformer transformer = new AndroidStringsTransformer(sourceLanguage, translator);
            transformer.setSourceFileEncoding(sourceFileEncoding);
            transformer.setTargetFileEncoding(targetFileEncoding);
            return transformer;
        }

        throw new MojoExecutionException("TranslationType '" + translationType + "' is not supported");
    }

    private Translator getTranslator() throws MojoExecutionException {
        if (createEmptyFiles) {
            return new EmptyStringTranslator();
        }

        // return a Translator based upon the translationService.
        if ("Bing".equals(translationService)) {
            final BingTranslator translator = new BingTranslator();
            translator.setApiKey(apiKey);
            //googleTranslator.setNrCallsBeforePause(maxCalls);
            //googleTranslator.setMillisToPause(1000 * pauseSeconds);
            return  translator;
        } else if ("Google".equals(translationService)) {
            final GoogleTranslator googleTranslator = new GoogleTranslator();
            //googleTranslator.setNrCallsBeforePause(maxCalls);
            //googleTranslator.setMillisToPause(1000 * pauseSeconds);
            googleTranslator.setApiKey(apiKey);
            return  googleTranslator;
        } else {
            throw new MojoExecutionException("Invalid TranslationService : " + translationService);
        }
    }

    private Collection<File> getFilesToProcess() {
        // TODO Find all the files to process.
        final Collection<File> files = new ArrayList<File>();
        for (final String fileName : sourceFileSets) {
            files.add(new File(sourceFolder, fileName));
        }
        return files;
    }

    /**
     * The returned File will have th same offset within the destination folder as the source has within the source folder.
     *
     * @param sourceFile    File for which to generate output.
     * @param language      Language for which output should be generated.
     * @return File in which the output should be generated.
     */
    private File getDestinationFile(File sourceFile, String language) {
        final String sourceFolderPath = sourceFolder.getAbsolutePath();
        final String sourceFilePath = sourceFile.getAbsolutePath();
        final String relativeFilePath = sourceFilePath.substring(sourceFolderPath.length() + 1);

        getLog().debug("sourceFolder=" + sourceFolderPath);
        getLog().debug("sourceFile  =" + sourceFilePath);
        getLog().debug("relativePath=" + relativeFilePath);

        final File translationTypeFolder = new File(targetFolder, translationType.name());
        final File outputFolder = new File(translationTypeFolder, "values-" + language);
        final File outputFile = new File(outputFolder, relativeFilePath);

        getLog().debug("outputFolder=" + targetFolder.getAbsolutePath());
        getLog().debug("outputFile  =" + outputFile.getAbsolutePath());

        return outputFile;
    }

/*
    private TermPersistor getTermPersistor() throws MojoExecutionException {
        if (translationType.equals(TranslationType.propertiesFile)) {
            final PropertyTermPersistor propertyTermPersistor = new PropertyTermPersistor();
            propertyTermPersistor.setIncludeDateHeader(includeDateHeader);
            propertyTermPersistor.setSourceFileEncoding(sourceFileEncoding);
            propertyTermPersistor.setTargetFileEncoding(targetFileEncoding);
            return propertyTermPersistor;
        } else if (translationType.equals(TranslationType.textFile)) {
            final TextFileTermPersistor persistor = new TextFileTermPersistor();
            persistor.setSourceFileEncoding(sourceFileEncoding);
            persistor.setTargetFileEncoding(targetFileEncoding);
            return persistor;
        }

        throw new MojoExecutionException("TranslationType '" + translationType + "' is not supported");
    }

    private void translateFile(OrderedTerms sourceTerms, File sourceFile) throws IOException, MojoExecutionException {

        final TermParser parser = new TermParser(getLog());

        getLog().debug("sourceFile: [" + sourceFile + "]");
        getLog().debug("sourceLanguage: [" + sourceLanguage + "]");

        for (String destLanguage : targetLanguages) {
            final File outputFile = getDestinationFile(sourceFile, destLanguage);
            getLog().debug("destinationFile [" + outputFile + "]");

            // create new output terms object
            final OrderedTerms outputTerms = new OrderedTerms();
            for (String msgKey : sourceTerms.propertyNames()) {
                final String msgVal = sourceTerms.getProperty(msgKey);
                if (createEmptyFiles) {
                    outputTerms.setProperty(msgKey, "");
                } else {
                    // perform translation
                    final EventProcessor eventProcessor = new EventProcessor(translator, sourceLanguage, destLanguage, msgKey, outputTerms);
                    parser.parse(msgVal.getBytes(), eventProcessor);
                }
            }

            final File parentFolder = outputFile.getParentFile();
            if (!parentFolder.exists() && !parentFolder.mkdirs()) {
                throw new MojoExecutionException("Could not create folder : " + parentFolder);
            }
            persistor.writeTerms(outputFile, outputTerms);
        }
    }

*/
}