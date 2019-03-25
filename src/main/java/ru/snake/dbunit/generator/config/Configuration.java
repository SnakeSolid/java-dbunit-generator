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

	private String templateTableName;

	private String skipTablePrefix;

	private TableNameCase tableNameCase;

	private Map<String, DriverConfig> drivers;

	/**
	 * Create empty configuration instance.
	 */
	public Configuration() {
		this.font = new FontConfig();
		this.templateTableName = null;
		this.skipTablePrefix = null;
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
	 * Returns template table name.
	 *
	 * @return template table name
	 */
	public String getTemplateTableName() {
		return templateTableName;
	}

	/**
	 * Returns skip query table name prefix.
	 *
	 * @return skip table name
	 */
	public String getSkipTablePrefix() {
		return skipTablePrefix;
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
		return "Configuration [font=" + font + ", templateTableName=" + templateTableName + ", skipTablePrefix="
				+ skipTablePrefix + ", tableNameCase=" + tableNameCase + ", drivers=" + drivers + "]";
	}

}
