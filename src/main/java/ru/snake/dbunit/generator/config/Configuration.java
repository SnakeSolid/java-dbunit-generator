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

	private String dummyTableName;

	private TableNameCase tableNameCase;

	private Map<String, DriverConfig> drivers;

	/**
	 * Create empty configuration instance.
	 */
	public Configuration() {
		this.font = new FontConfig();
		this.dummyTableName = null;
		this.tableNameCase = null;
		this.drivers = new HashMap<>();
	}

	/**
	 * Returns font settings.
	 *
	 * @return font settings
	 */
	public FontConfig getFont() {
		return font;
	}

	/**
	 * Returns dummy table name.
	 *
	 * @return dummy table name
	 */
	public String getDummyTableName() {
		return dummyTableName;
	}

	/**
	 * Returns table name case change policy.
	 *
	 * @return table name case change mode
	 */
	public TableNameCase getTableNameCase() {
		return tableNameCase;
	}

	/**
	 * Returns map with driver configurations.
	 *
	 * @return driver map
	 */
	public Map<String, DriverConfig> getDrivers() {
		return drivers;
	}

	@Override
	public String toString() {
		return "Configuration [font=" + font + ", dummyTableName=" + dummyTableName + ", tableNameCase=" + tableNameCase
				+ ", drivers=" + drivers + "]";
	}

}
