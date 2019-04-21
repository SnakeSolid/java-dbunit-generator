package ru.snake.dbunit.generator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper safe for read properties from jar file.
 *
 * @author snake
 *
 */
public final class ApplicationProperties {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationProperties.class);

	/**
	 * Key for application name property.
	 */
	public static final String PROPEPTY_NAME = "name";

	/**
	 * Key for application version property.
	 */
	public static final String PROPEPTY_VERSION = "version";

	/**
	 * Hide public constructor.
	 */
	private ApplicationProperties() {
	}

	/**
	 * Read application properties from resource. Returns system properties if
	 * resource available, otherwise returns empty properties. Never returns
	 * {@code null}.
	 *
	 * @return application properties
	 */
	public static Properties readApplicationProperties() {
		Properties properties = new Properties();

		try (InputStream resourceStream = ClassLoader.getSystemResourceAsStream("application.properties")) {
			properties.load(resourceStream);
		} catch (IOException e) {
			LOG.warn("Failed to read application propertiaes", e);
		}

		return properties;
	}

}
