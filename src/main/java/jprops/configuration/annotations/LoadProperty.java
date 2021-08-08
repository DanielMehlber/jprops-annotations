package jprops.configuration.annotations;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation will be processed by PropertyLoader
 * and connects static fields to properties in a configuration file.
 *
 * @author danielmehlber
 *
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadProperty {

	/**
	 * These types are supported and can be parsed
	 * @author danielmehlber
	 */
    enum Type {
		STRING,
		INT,
		BOOLEAN,
		FLOAT,
		DOUBLE
	}

	/** name of property */
	String value();

	/** URI to the properties file (relative to classpath) */
	String res() default "config.properties";

	/** Whether or not this property is required (Exception will be thrown in case it's not provided) */
	boolean required() default false;

	/** Type of property for conversion */
	Type type() default Type.STRING;

	/** Default value in case this value is not provided (will be ignored when required=true) */
	String defaultValue() default "";

}
