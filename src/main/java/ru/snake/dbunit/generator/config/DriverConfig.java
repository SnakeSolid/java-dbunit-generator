package ru.snake.dbunit.generator.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JDBC driver configuration settings.
 *
 * @author snake
 *
 */
public final class DriverConfig {

	private String driverPath;

	private String driverClass;

	private String url;

	private TableNameCase tableNameCase;

	private Map<String, TypeMapping> typeMappings;

	private List<String> parameters;

	/**
	 * Create empty driver settings.
	 */
	public DriverConfig() {
		this.driverPath = null;
		this.driverClass = null;
		this.url = null;
		this.tableNameCase = null;
		this.typeMappings = Collections.emptyMap();
		this.parameters = Collections.emptyList();
	}

	/**
	 * Return path to JDBC driver library.
	 *
	 * @return path to driver
	 */
	public String getDriverPath() {
		return driverPath;
	}

	/**
	 * Returns JDBC driver class name.
	 *
	 * @return driver class name
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * Returns JDBC connection URL.
	 *
	 * @return connection URL
	 */
	public String getUrl() {
		return url;
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
	 * Returns JDBC type name to corresponding column mapper map.
	 *
	 * @return type name to column mapper map
	 */
	public Map<String, TypeMapping> getTypeMappings() {
		return typeMappings;
	}

	/**
	 * Returns list of available connection parameters.
	 *
	 * @return list of connection parameter
	 */
	public List<String> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return "DriverConfig [driverPath=" + driverPath + ", driverClass=" + driverClass + ", url=" + url
				+ ", tableNameCase=" + tableNameCase + ", typeMappings=" + typeMappings + ", parameters=" + parameters
				+ "]";
	}

}
