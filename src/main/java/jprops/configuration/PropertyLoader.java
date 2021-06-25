package jprops.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.stream.Stream;

import jprops.configuration.annotations.LoadProperty;

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

		final LinkedList<Field> fields = new LinkedList<>();

		/*
		 * Collect all fields of classes which are static and annotated with @LoadProperty
		 */
		for (final Class<?> c : classes) {
			// Stream of fields of class
			Stream.of(c.getDeclaredFields())
				// only fields annotated with @LoadProperty
				.filter(field -> field.isAnnotationPresent(LoadProperty.class))
				// only static fields
				.filter(field -> java.lang.reflect.Modifier.isStatic(field.getModifiers()))
				// append them to list
				.forEach(field -> fields.add(field));
		}


		final HashMap<String, Properties> loadedProperties = new HashMap<>();
		/*
		 *  Iterate over fields and read configurations
		 */
		for (final Field field : fields) {
			field.setAccessible(true);
			final LoadProperty annotation = field.getAnnotation(LoadProperty.class);

			final String file = annotation.res();
			Properties props = null;

			/*
			 * Get requested properties file
			 */
			if (loadedProperties.containsKey(file)) {
				// CASE 1: properties have aleady been loaded
				props = loadedProperties.get(file);
			} else {
				// CASE 2: properties are not loaded yet, try to load them
				props = loadFile(file); // may fail
				// store in case this resource in requested again (see CASE 1)
				loadedProperties.put(file, props);
			}

			/*
			 * Retrieve value from properties
			 */
			String value = null;
			if(props.containsKey(annotation.value())) {
				// value is contained by properties, retrieve get it
				value = (String) props.get(annotation.value());
			} else if (annotation.required()) {
				// Oh no, it's required to be set in properties, but it isn't!
				// The configuration must be insufficient
				throw new InsufficientConfigurationException(file, annotation.value());
			} else {
				// value is not set in properties and not required to be, use default value
				value = annotation.defaultValue();
			}

			/*
			 * Now set the field to the retrieved value
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
		final InputStream input = PropertyLoader.class.getClassLoader().getResourceAsStream(file);

		if (input == null)
			throw new FileNotFoundException(String.format("cannot load properties file '%s': File not found", file));
		props.load(input);

		return props;
	}

}
