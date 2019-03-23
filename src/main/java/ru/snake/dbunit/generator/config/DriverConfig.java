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
public class DriverConfig {

	private String driverPath;

	private String driverClass;

	private String url;

	private Map<String, TypeMapping> typeMappings;

	private List<String> parameters;

	/**
	 * Create empty driver settings.
	 */
	public DriverConfig() {
		this.driverPath = null;
		this.driverClass = null;
		this.url = null;
		this.typeMappings = Collections.emptyMap();
		this.parameters = Collections.emptyList();
	}

	public String getDriverPath() {
		return driverPath;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getUrl() {
		return url;
	}

	public Map<String, TypeMapping> getTypeMappings() {
		return typeMappings;
	}

	public List<String> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return "DriverConfig [driverPath=" + driverPath + ", driverClass=" + driverClass + ", url=" + url
				+ ", typeMappings=" + typeMappings + ", parameters=" + parameters + "]";
	}

}
