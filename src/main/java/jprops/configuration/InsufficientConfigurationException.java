package jprops.configuration;


/**
 * This Exception is thrown when a required property is not provided
 * @author danielmehlber
 *
 */
@SuppressWarnings("serial")
public class InsufficientConfigurationException extends Exception {

	public InsufficientConfigurationException(final String file, final String propertyName) {
		super(String.format("Configuration is insufficient: file '%s' is missing a required property named '%s'", file, propertyName));
	}

}
