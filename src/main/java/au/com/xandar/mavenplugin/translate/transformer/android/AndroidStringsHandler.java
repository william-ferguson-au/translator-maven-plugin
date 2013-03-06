package au.com.xandar.mavenplugin.translate.transformer.android;

import au.com.xandar.mavenplugin.translate.translator.Translator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * One use class tat receives notifications of Android String XML elements, translates those that require it
 * and accumulates a translated XML document which can be retrieved via {@link #getTranslatedDocument()}.
 */
final class AndroidStringsHandler extends DefaultHandler implements LexicalHandler {

    private final CharEscaper charEscaper = new CharEscaper();
    private static final char LINE_END = '\n';

    private final StringBuilder document = new StringBuilder();
    private final StringBuilder textToTranslate = new StringBuilder();
    private boolean translateThisText;

    private final String sourceLanguage;
    private final String targetLanguage;
    private final Translator translator;

    AndroidStringsHandler(Translator translator, String sourceLanguage, String targetLanguage) {
        this.translator = translator;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

    /**
     * @return the translated document.
     */
    public String getTranslatedDocument() {
        return document.toString();
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        document.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        document.append(LINE_END);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        document.append(LINE_END);
    }

    @Override
    public void startElement(String uri, String simpleName, String qualifiedName, Attributes attributes) throws SAXException {
        super.startElement(uri, simpleName, qualifiedName, attributes);
        final String elementName = "".equals(simpleName) ? qualifiedName : simpleName;
        document.append("<");
        document.append(elementName);
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                final String aName = ("".equals(attributes.getLocalName(i))) ? attributes.getQName(i) : attributes.getLocalName(i);
                document.append(" ");
                document.append(aName);
                document.append("=\"");
                document.append(attributes.getValue(i));
                document.append("\"");
            }
        }
        document.append(">");
        textToTranslate.setLength(0); // reset textToTranslate.

        // Switch on translation for the next piece of text if we just started a string or item element.
        translateThisText = "string".equals(elementName) || "item".equals(elementName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (translateThisText) {
            textToTranslate.append(ch, start, length);
        } else {
            document.append(ch, start, length);
        }
    }

    private CharSequence getTranslatedText(StringBuilder text) {

        charEscaper.removeBackslashes(text);
        final StringBuilder translatedText = new StringBuilder(translator.translate(text, sourceLanguage, targetLanguage));
        charEscaper.addBackslashes(translatedText);

        return translatedText;
    }

    @Override
    public void endElement(String uri, String simpleName, String qualifiedName) throws SAXException {
        super.endElement(uri, simpleName, qualifiedName);

        if (translateThisText) {
            //PrintHelper.printChars(textToTranslate);

            // Perform translation if required.
            final CharSequence translatedText = getTranslatedText(textToTranslate);
            document.append(translatedText);
            translateThisText = false;
        }

        final String elementName = "".equals(simpleName) ? qualifiedName : simpleName;
        document.append("</");
        document.append(elementName);
        document.append(">");
        //System.out.println("endElement=" + elementName);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        document.append(ch, start, length);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        super.skippedEntity(name);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        super.processingInstruction(target, data);
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        // do nothing
    }

    public void endDTD() throws SAXException {
        // do nothing
    }

    public void startEntity(String name) throws SAXException {
        // do nothing
    }

    public void endEntity(String name) throws SAXException {
        // do nothing
    }

    public void startCDATA() throws SAXException {
        // do nothing
    }

    public void endCDATA() throws SAXException {
        // do nothing
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        document.append("<!--");
        document.append(ch, start, length);
        document.append("-->");
    }
}