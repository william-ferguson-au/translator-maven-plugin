package au.com.xandar.mavenplugin.translate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * OrderedTerms represents a persistent ordered set of terms.
 * <p/>
 * The <code>OrderedTerms</code> can be saved to a stream or loaded from a stream.
 * Each key and its corresponding value in the property list is a string.
 * <p/>
 * A property list can contain another property list as its "defaults"; this
 * second property list is searched if the property key is not found in the
 * original property list.
 * <p/>
 */
public final class OrderedTerms {

    private final Map<String, String> props = new LinkedHashMap<String, String>();

    /**
     * Calls the <tt>Hashtable</tt> method <code>put</code>. Provided for
     * parallelism with the <tt>getProperty</tt> method. Enforces use of strings
     * for property keys and values. The value returned is the result of the
     * <tt>Hashtable</tt> call to <code>put</code>.
     *
     * @param key   the key to be placed into this property list.
     * @param value the value corresponding to <tt>key</tt>.
     * @return the previous value of the specified key in this property list, or
     *         <code>null</code> if it did not have one.
     * @see #getProperty
     */
    public Object setProperty(String key, String value) {
        return props.put(key, value);
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns
     * <code>null</code> if the property is not found.
     *
     * @param key the property key.
     * @return the value in this property list with the specified key value.
     * @see #setProperty
     */
    public String getProperty(String key) {
        return props.get(key);
    }

    /**
     * Searches for the property with the specified key in this property list.
     * If the key is not found in this property list, the default property list,
     * and its defaults, recursively, are then checked. The method returns the
     * default value argument if the property is not found.
     *
     * @param key          the hashtable key.
     * @param defaultValue a default value.
     * @return the value in this property list with the specified key value.
     * @see #setProperty
     */
    public String getProperty(String key, String defaultValue) {
        final String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    /**
     * Returns an enumeration of all the keys in this property list, including
     * distinct keys in the default property list if a key of the same name has
     * not already been found from the main terms list.
     *
     * @return an enumeration of all the keys in this property list, including
     *         the keys in the default property list.
     * @throws ClassCastException if any key in this property list is not a string.
     * @see java.util.Enumeration
     * @see java.util.Properties#defaults
     */
    public Set<String> propertyNames() {
        return props.keySet();
    }
}
