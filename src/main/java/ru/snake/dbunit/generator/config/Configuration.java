package ru.snake.dbunit.generator.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration setting. Contains join method and table configuration.
 *
 * @author snake
 *
 */
public final class Configuration {

	private FontConfig font;

	private Map<String, DriverConfig> drivers;

	/**
	 * Create empty configuration instance.
	 */
	public Configuration() {
		this.font = new FontConfig();
		this.drivers = new HashMap<>();
	}

	public FontConfig getFont() {
		return font;
	}

	public Map<String, DriverConfig> getDrivers() {
		return drivers;
	}

	@Override
	public String toString() {
		return "Configuration [font=" + font + ", drivers=" + drivers + "]";
	}

}
