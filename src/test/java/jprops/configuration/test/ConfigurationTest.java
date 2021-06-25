package configuration.properties.test;
import org.junit.jupiter.api.Test;

import configuration.properties.ConfigurationLoader;
import configuration.properties.annotations.ConfigProperty;

public class ConfigurationTest {

	@ConfigProperty("name")
	public static String name;
	
	@ConfigProperty(
		value="email", 
		uri="config.properties",
		required = true
	) public static String email;
	
	@Test
	public void test() throws Exception {
		ConfigurationLoader.loadProperties(ConfigurationTest.class);
	}
}
