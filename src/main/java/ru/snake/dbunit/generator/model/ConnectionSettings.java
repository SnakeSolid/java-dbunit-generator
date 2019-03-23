package ru.snake.dbunit.generator.model;

import java.util.Collections;
import java.util.Map;

import ru.snake.dbunit.generator.config.TypeMapping;

public final class ConnectionSettings {

	private final String driverPath;

	private final String driverClass;

	private final Map<String, TypeMapping> typeMappers;

	private final String url;

	/**
	 * Creates new connection settings using given driver path and URL.
	 *
	 * @param driverPath
	 *            driver path
	 * @param driverClass
	 *            driver class name
	 * @param typeMappers
	 *            field mappers
	 * @param url
	 *            connection URL
	 */
	public ConnectionSettings(
		final String driverPath,
		final String driverClass,
		final Map<String, TypeMapping> typeMappers,
		final String url
	) {
		this.driverPath = driverPath;
		this.driverClass = driverClass;
		this.typeMappers = typeMappers;
		this.url = url;
	}

	public String getDriverPath() {
		return driverPath;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public Map<String, TypeMapping> getTypeMappers() {
		return Collections.unmodifiableMap(typeMappers);
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "ConnectionSettings [driverPath=" + driverPath + ", driverClass=" + driverClass + ", typeMappers="
				+ typeMappers + ", url=" + url + "]";
	}

}
