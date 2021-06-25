# jprops-annotations
Simple and lightweight property and configuration loading using elegant annotations in Java

```java

import jprops.configuration.InsufficientConfigurationException;
import jprops.configuration.PropertyLoader;
import jprops.configuration.annotations.LoadProperty;

class ConfigurationExample {

  /*
   * jprops-annotations injects values from a .properties file into fields.
   * Default is 'config.properties' in classpath
   */
  
  // Load property value by name
  @LoadProperty( "someString" )
  public static String someStringValue;
  
  // Define type of property value for conversion (default is String)
  @LoadProperty( value="intValue", type=ConfigProperty.Type.INT )
  private static int someIntValue;
  
  // Define required properties. An Exception will be thrown, in case it's not provided.
  @LoadProperty( value="required", required=true )
  protected static String requiredValue;
  
  // Define the URI or location of your properties file (default is 'config.properties')
  @LoadProperty( value="fromAnotherFile", res="application.properties" )
  public static String fromAnotherFile;
  
  // Use default values in case a property is not provided
  @LoadProperty( value="defaultValue", defaultValue="none", required=false )
  private static String defaultValue;
  
  // Or any other function in the programs lifecycle
  public static void main(String[] args) {
  
    // Load properties into class fields by passing those classes to the processor
    try {
      ConfigurationLoader.loadProperties(ConfigurationExample.class, AnotherExample.class, ...);
    } catch (...) {}
    
  }
  
}
```

## The Annotation

Annotated fields must be **static**. Their visibility is irrelevant.

```java
@LoadProperty(
  value="...",                // name of property
  defaultValue="...",         // default value in case property is not provided
  required=true|false,        // whether or not the property must be provided (will throw Exception if violated)
  res="...",                  // URI or location of .properties file (relative to classpath)
  type=ConfigProperty.Type.*  // Type to which the String value will be converted to
)

(public | private | protected) static (String | int | float | double | boolean) value; 
```
