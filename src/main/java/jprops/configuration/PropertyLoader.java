package jprops.configuration;

import jprops.configuration.annotations.LoadProperty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Loads configuration and "injects" them into annotated static class fields
 *
 * @author danielmehlber
 */
public class PropertyLoader {

	/**
	 * Collects all annotated static fields and loads their values from property files.
	 *
	 * Fields must be static and NOT final. Their visibility is irrelevant.
	 *
	 * @param classes All classes containing annotated static fields
	 * @throws InsufficientConfigurationException A required property is not defined
	 * @throws IllegalArgumentException Something else went wrong
	 * @throws IllegalAccessException Field may be final
	 * @throws IOException cannot load properties file
	 * @throws FileNotFoundException Path to properties file is not valid or file does not exist
	 */
	public static void loadProperties(final Class<?>... classes) throws InsufficientConfigurationException, FileNotFoundException,IllegalArgumentException, IllegalAccessException, IOException {

		// all fields being static and annotated with @LoadProperty will be collected in here
		final LinkedList<Field> fields = new LinkedList<>();

		/*
		 * Collect all fields of classes which are static and annotated with @LoadProperty
		 */
		for (final Class<?> c : classes) {
			// fields of current class as stream
			Stream.of(c.getDeclaredFields())
				// filter fields annotated with @LoadProperty
				.filter(field -> field.isAnnotationPresent(LoadProperty.class))
				// filter static fields
				.filter(field -> java.lang.reflect.Modifier.isStatic(field.getModifiers()))
				// add them to list
				.forEach(fields::add);
		}


		final HashMap<String, Properties> propertiesCache = new HashMap<>();
		/*
		 *  Iterate over fields and read configurations
		 */
		for (final Field field : fields) {

			// make non-puplic fields accessible
			field.setAccessible(true);
			final LoadProperty annotation = field.getAnnotation(LoadProperty.class);

			// read resource attribute from fields annotation
			final String file = annotation.res();
			Properties props;

			/*
			 * Get requested file (*.properites).
			 */
			if (propertiesCache.containsKey(file)) {
				// CASE 1: properties-file has already been loaded
				props = propertiesCache.get(file);
			} else {
				// CASE 2: properties are not loaded yet, try to load file
				props = loadFile(file); // may fail
				// store in case this resource in requested again (in CASE 1)
				propertiesCache.put(file, props);
			}

			/*
			 * Retrieve value from properties
			 */
			String value;
			if(props.containsKey(annotation.value())) {
				// value is defined in file, retreive it
				value = (String) props.get(annotation.value());
			} else if (annotation.required()) {
				// Oh no, it's a required value, but not defined in file!
				// this configuration is insufficient
				throw new InsufficientConfigurationException(file, annotation.value());
			} else {
				// the value is neither defined in file, nor required. Just use the default value.
				value = annotation.defaultValue();
			}

			/*
			 * Now set the current field to its retrieved value
			 */
			switch (annotation.type()) {
			case BOOLEAN:
				field.set(null, Boolean.parseBoolean(value));
				break;
			case DOUBLE:
				field.set(null, Double.parseDouble(value));
				break;
			case FLOAT:
				field.set(null, Float.parseFloat(value));
				break;
			case INT:
				field.set(null, Integer.parseInt(value));
				break;
			case STRING:
				field.set(null, value);
				break;
			default:
				break;
			}
		}

	}

	/**
	 * Loads file containing properties
	 *
	 * @param file String path to file relative to classpath
	 * @return Properties
	 * @throws FileNotFoundException path is not valid or file does not exist
	 * @throws IOException Cannot load file
	 */
	private static Properties loadFile(final String file) throws FileNotFoundException,  IOException {
		final Properties props = new Properties();

		// load file relative to classpath
		final InputStream input = PropertyLoader.class.getClassLoader().getResourceAsStream(file);

		if (input == null)
			throw new FileNotFoundException(String.format("cannot load properties file '%s': File not found", file));
		props.load(input);

		return props;
	}

}
