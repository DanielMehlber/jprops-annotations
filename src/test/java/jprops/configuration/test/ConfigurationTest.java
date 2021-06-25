package jprops.configuration.test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import jprops.configuration.InsufficientConfigurationException;
import jprops.configuration.PropertyLoader;
import jprops.configuration.annotations.LoadProperty;

public class ConfigurationTest {

	private static class SimpleAnnotationTest {
		@LoadProperty("someValue")
		public static String someValue;
		
		@LoadProperty("anotherValue")
		public static String anotherValue;
		
		public static boolean check() {
			return someValue.equals("someValue") 
					&& anotherValue.equals("anotherValue");
		}
	}
	
	private static class AdvancedAnnotationTest {
		@LoadProperty(value="someValue", required=true)
		public static String someRequiredValue;
		
		@LoadProperty(value="intValue", type=LoadProperty.Type.INT, required=true)
		public static int intValue;
		
		@LoadProperty(value="floatValue", type=LoadProperty.Type.FLOAT, required=true)
		public static float floatValue;
		
		public static boolean check() {
			return someRequiredValue.equals("someValue")
					&& intValue == 123
					&& floatValue == 1.23f;
		}
	}
	
	private static class DefaultValueTest {
		
		@LoadProperty("valueNotProvided")
		public static String valueNotProvided; // default should be ""
		
		@LoadProperty(value="valueNotProvided", defaultValue="default")
		public static String valueNotProvidedButDefaultValue;
		
		public static boolean check() {
			return valueNotProvided.isEmpty()
					&& valueNotProvidedButDefaultValue.equals("default");
		}
	}
	
	private static class DifferentFileTest {
		@LoadProperty(value="fromAnotherFile", res="another.properties", required=true)
		public static String fromAnotherFile;
		
		public static boolean check() {
			return fromAnotherFile.equals("fromAnotherFile");
		}
	}
	
	private static class RequiredButNotProvidedTest {
		@LoadProperty(value="notProvided", required=true)
		public static String notProvided;
	}
	
	@Test
	public void testSimpleAnnotations() throws Exception {
		PropertyLoader.loadProperties(SimpleAnnotationTest.class);
		assertTrue(SimpleAnnotationTest.check(), "wrong values");
	}
	
	@Test
	public void testAdvancedAnnotations() throws Exception {
		PropertyLoader.loadProperties(AdvancedAnnotationTest.class);
		assertTrue(AdvancedAnnotationTest.check(), "wrong values");
	}
	
	@Test
	public void testDefaultValues() throws Exception {
		PropertyLoader.loadProperties(DefaultValueTest.class);
		assertTrue(DefaultValueTest.check(), "wrong values");
	}
	
	@Test
	public void testValuesFromAnotherFile() throws Exception {
		PropertyLoader.loadProperties(DifferentFileTest.class);
		assertTrue(DifferentFileTest.check(), "wrong values");
	}
	
	@Test
	public void testRequiredButNotProvided()  {
		
		try {
			PropertyLoader.loadProperties(RequiredButNotProvidedTest.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Wrong error: File must be found");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("Wrong error: there must not be an illegal argument");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Wrong error: there must not be an illegal access");
		} catch (InsufficientConfigurationException e) {
			// right error
		} catch (IOException e) {
			e.printStackTrace();
			fail("Wrong error: there must not be an IO Exception");
		}
	
		
	}
	
	
}
