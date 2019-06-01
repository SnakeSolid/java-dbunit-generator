package ru.snake.dbunit.generator.model;

import java.util.Collections;
import java.util.Map;

import ru.snake.dbunit.generator.config.TableNameCase;
import ru.snake.dbunit.generator.config.TypeMapping;

/**
 * Connection settings from worker. Settings contain field mappings and driver
 * settings.
 *
 * @author snake
 *
 */
public final class ConnectionSettings {

	private final String driverPath;

	private final String driverClass;

	private final TableNameCase tableNameCase;

	private final Map<String, TypeMapping> typeMappers;

	private final String url;

	/**
	 * Creates new connection settings using given driver path and URL.
	 *
	 * @param driverPath
	 *            driver path
	 * @param driverClass
	 *            driver class name
	 * @param tableNameCase
	 *            table name case
	 * @param typeMappers
	 *            field mappers
	 * @param url
	 *            connection URL
	 */
	public ConnectionSettings(
		final String driverPath,
		final String driverClass,
		final TableNameCase tableNameCase,
		final Map<String, TypeMapping> typeMappers,
		final String url
	) {
		this.driverPath = driverPath;
		this.driverClass = driverClass;
		this.tableNameCase = tableNameCase;
		this.typeMappers = typeMappers;
		this.url = url;
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
	public Map<String, TypeMapping> getTypeMappers() {
		return Collections.unmodifiableMap(typeMappers);
	}

	/**
	 * Returns JDBC connection URL.
	 *
	 * @return connection URL
	 */
	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "ConnectionSettings [driverPath=" + driverPath + ", driverClass=" + driverClass + ", tableNameCase="
				+ tableNameCase + ", typeMappers=" + typeMappers + ", url=" + url + "]";
	}

}
