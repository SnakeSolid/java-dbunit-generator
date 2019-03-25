package ru.snake.dbunit.generator.worker;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import ru.snake.dbunit.generator.Message;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.config.TableNameCase;
import ru.snake.dbunit.generator.config.TypeMapping;
import ru.snake.dbunit.generator.model.ConnectionSettings;
import ru.snake.dbunit.generator.worker.mapper.AsciiStringMapper;
import ru.snake.dbunit.generator.worker.mapper.Base64BytesMapper;
import ru.snake.dbunit.generator.worker.mapper.ColumnMapper;
import ru.snake.dbunit.generator.worker.mapper.DummyStringMapper;
import ru.snake.dbunit.generator.worker.mapper.HexBytesMapper;
import ru.snake.dbunit.generator.worker.mapper.Utf8StringMapper;
import ru.snake.dbunit.generator.worker.parse.QueryParser;
import ru.snake.dbunit.generator.worker.query.Query;
import ru.snake.dbunit.generator.worker.query.QueryTemplate;

/**
 * Background worker. Worker read queries from text, executes every query using
 * given connection setting. All retrieved data-sets will be converted to DBUnit
 * XML representation.
 *
 * @author snake
 *
 */
public final class BuildDatasetWorker extends SwingWorker<Result<String, String>, Void> {

	private final Configuration config;

	private final String queryText;

	private final ConnectionSettings connectionSettings;

	private final Document outputDocument;

	/**
	 * Create new worker to perform building data-set from given query list.
	 *
	 * @param config
	 *            configuration settings
	 * @param queryText
	 *            string with queries
	 * @param connectionSettings
	 *            connection settings
	 * @param outputDocument
	 *            output document
	 */
	public BuildDatasetWorker(
		final Configuration config,
		final String queryText,
		final ConnectionSettings connectionSettings,
		final Document outputDocument
	) {
		this.config = config;
		this.queryText = queryText;
		this.connectionSettings = connectionSettings;
		this.outputDocument = outputDocument;
	}

	@Override
	protected Result<String, String> doInBackground() throws Exception {
		List<Query> allQueries = QueryParser.parse(this.queryText);
		List<Query> queries = new ArrayList<>();

		if (allQueries.isEmpty()) {
			return Result.error("Dataset must have at least one query.");
		}

		for (Query query : allQueries) {
			if (query.getTableName() == null) {
				StringBuilder builder = new StringBuilder();
				builder.append("Table for query not defined. ");
				builder.append("Use single line comment (`-- schema.table`) to define table name. ");
				builder.append("Query:\n");
				builder.append(query.getQueryText());

				return Result.error(builder.toString());
			} else if (isSkippedQuery(query)) {
				continue;
			} else if (isTemplateQuery(query)) {
				Result<List<Query>, String> generatedResult = QueryTemplate.generate(query);

				if (generatedResult.isError()) {
					return Result.error(generatedResult.getError());
				}

				queries.addAll(generatedResult.getValue());
			} else {
				queries.add(query);
			}
		}

		if (queries.isEmpty()) {
			return Result.error("Dataset must have at least one executable 	query.");
		}

		String driverClassName = connectionSettings.getDriverClass();
		String connectionUrl = connectionSettings.getUrl();
		URL url = new URL(connectionSettings.getDriverPath());
		URL[] urls = new URL[] { url };

		try (URLClassLoader classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader())) {
			Class<?> driverClass = classLoader.loadClass(driverClassName);

			if (Driver.class.isAssignableFrom(driverClass)) {
				registerWrappedDriver(driverClass);
			} else {
				return Result.error("Driver class " + driverClassName + " does not implement java.sql.Driver.");
			}

			StringBuilder dataset = new StringBuilder();
			boolean isFirst = true;

			dataset.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			dataset.append("<dataset>\n");

			try (Connection connection = DriverManager.getConnection(connectionUrl);
					Statement statement = connection.createStatement()) {
				for (Query query : queries) {
					if (isSkippedQuery(query)) {
						continue;
					}

					String tableData = getTableDataset(statement, query);

					if (!isFirst) {
						dataset.append("\n");
					} else {
						isFirst = false;
					}

					dataset.append(tableData);
				}
			}

			dataset.append("</dataset>\n");

			return Result.ok(dataset.toString());
		}
	}

	/**
	 * Returns true if this query table name starts with the same as skip table
	 * name in configuration.
	 *
	 * @param query
	 *            query
	 * @return true if name is dummy
	 */
	private boolean isTemplateQuery(final Query query) {
		String tableName = getQueryTableName(query);
		String templateName = config.getTemplateTableName();

		if (templateName == null) {
			return false;
		}

		return tableName.equals(templateName);
	}

	/**
	 * Returns true if this query table name starts with the same as skip table
	 * name in configuration.
	 *
	 * @param query
	 *            query
	 * @return true if name is dummy
	 */
	private boolean isSkippedQuery(final Query query) {
		String tableName = getQueryTableName(query);
		String dummyName = config.getSkipTablePrefix();

		if (dummyName == null) {
			return false;
		}

		return tableName.startsWith(dummyName);
	}

	/**
	 * Generate and result XML data set for single query.
	 *
	 * @param statement
	 *            JDBC statement
	 * @param query
	 *            query
	 * @return string with table data set
	 * @throws SQLException
	 *             if error occurred
	 */
	private String getTableDataset(final Statement statement, final Query query) throws SQLException {
		StringBuilder tablrDatabaset = new StringBuilder();
		Set<String> distinctRows = new HashSet<String>();
		String queryString = query.getQueryText();
		String tableName = getQueryTableName(query);

		try (ResultSet resultSet = statement.executeQuery(queryString)) {
			List<ColumnMapper> mappers = getMappers(resultSet);

			while (resultSet.next()) {
				String tableRow = getTableRow(resultSet, tableName, mappers);

				if (distinctRows.add(tableRow)) {
					tablrDatabaset.append(tableRow);
				}
			}
		}

		if (distinctRows.isEmpty()) {
			return "    <" + tableName + " />\n";
		}

		return tablrDatabaset.toString();
	}

	/**
	 * Returns query table name with expected in configuration case. If case not
	 * defined - table name will not be changed.
	 *
	 * @param query
	 *            query
	 * @return table name
	 */
	private String getQueryTableName(final Query query) {
		String tableName = query.getTableName();
		TableNameCase nameCase = config.getTableNameCase();

		if (nameCase == null) {
			return tableName;
		}

		switch (nameCase) {
		case UPPER:
			return tableName.toUpperCase();

		case LOWER:
			return tableName.toLowerCase();

		default:
			throw new IllegalArgumentException("Unexpected table case: " + nameCase);
		}
	}

	/**
	 * Generate string representation of single table row.
	 *
	 * @param resultSet
	 *            result set
	 * @param tableName
	 *            table name
	 * @param mappers
	 *            column mappers
	 * @return table row string
	 * @throws SQLException
	 *             if error occurred
	 */
	private String getTableRow(final ResultSet resultSet, final String tableName, final List<ColumnMapper> mappers)
			throws SQLException {
		StringBuilder tableData = new StringBuilder();
		tableData.append("    <");
		tableData.append(tableName);

		for (ColumnMapper mapper : mappers) {
			String value = mapper.map(resultSet);

			if (value != null) {
				tableData.append(" ");
				tableData.append(mapper.getColumnName());
				tableData.append("=\"");
				tableData.append(escapeControlCharacters(value));
				tableData.append("\"");
			}
		}

		tableData.append(" />\n");

		return tableData.toString();
	}

	/**
	 * Creates list of {@link ColumnMapper} using result set metadata.
	 *
	 * @param resultSet
	 *            result set
	 * @return list of mappers
	 * @throws SQLException
	 *             if error occurred
	 */
	private List<ColumnMapper> getMappers(final ResultSet resultSet) throws SQLException {
		ResultSetMetaData metadata = resultSet.getMetaData();
		List<ColumnMapper> result = new ArrayList<>();

		for (int index = 1; index <= metadata.getColumnCount(); index += 1) {
			String typeName = metadata.getColumnTypeName(index);
			String columnName = metadata.getColumnName(index);
			ColumnMapper mapper = getMapperByType(typeName, columnName);

			result.add(mapper);
		}

		return result;
	}

	/**
	 * Escapes XML control characters from string.
	 *
	 * @param value
	 *            value
	 * @return XML safe value
	 */
	private Object escapeControlCharacters(final String value) {
		String result = value;
		result = result.replace("\"", "&quot;");
		result = result.replace("&", "&amp;");
		result = result.replace("'", "&apos;");
		result = result.replace("<", "&lt;");
		result = result.replace(">", "&gt;");

		return result;
	}

	/**
	 * Unregister all available drivers from {@link DriverManager}. Wraps driver
	 * to {@link DriverWrapper} and register it in {@link DriverManager}.
	 *
	 * @param driverClass
	 *            driver class
	 * @throws InstantiationException
	 *             if instance not created
	 * @throws IllegalAccessException
	 *             if constructor private
	 * @throws SQLException
	 *             if error occurred
	 */
	private void registerWrappedDriver(final Class<?> driverClass)
			throws InstantiationException, IllegalAccessException, SQLException {
		Driver driver = (Driver) driverClass.newInstance();
		DriverWrapper wrapper = new DriverWrapper(driver);

		Enumeration<Driver> drivers = DriverManager.getDrivers();

		while (drivers.hasMoreElements()) {
			Driver oldDriver = drivers.nextElement();

			DriverManager.deregisterDriver(oldDriver);
		}

		DriverManager.registerDriver(wrapper);
	}

	/**
	 * Returns column mapper over given column and corresponding to given type.
	 *
	 * @param typeName
	 *            type name
	 * @param columnName
	 *            column name
	 * @return column mapper
	 */
	private ColumnMapper getMapperByType(final String typeName, final String columnName) {
		TypeMapping dataMapper = connectionSettings.getTypeMappers().get(typeName);

		if (dataMapper == null) {
			return new DummyStringMapper(columnName);
		}

		switch (dataMapper) {
		case ASCII:
			return new AsciiStringMapper(columnName);

		case UTF8:
			return new Utf8StringMapper(columnName);

		case HEX:
			return new HexBytesMapper(columnName);

		case BASE64:
			return new Base64BytesMapper(columnName);

		default:
			throw new IllegalArgumentException("Data mapper " + dataMapper + " has no corresponding class.");
		}
	}

	@Override
	protected void done() {
		Result<String, String> result;

		try {
			result = get();
		} catch (InterruptedException | ExecutionException e) {
			result = Result.error(unrollMessages(e));
		}

		try {
			AttributeSet attributes = SimpleAttributeSet.EMPTY;
			int length = this.outputDocument.getLength();
			this.outputDocument.remove(0, length);

			if (result.isError()) {
				this.outputDocument.insertString(0, result.getError(), attributes);
			} else {
				this.outputDocument.insertString(0, result.getValue(), attributes);
			}
		} catch (BadLocationException e) {
			Message.showError(e);
		}
	}

	/**
	 * Unroll exception messages to multi-line string.
	 *
	 * @param exception
	 *            exception
	 * @return formatted message
	 */
	private String unrollMessages(final Exception exception) {
		StringBuilder builder = new StringBuilder();
		Throwable cause = exception.getCause();

		builder.append("Error > ");
		builder.append(exception.getLocalizedMessage());

		if (cause != null) {
			builder.append("\n  Caused by > ");
			builder.append(cause.getLocalizedMessage());
		}

		for (Throwable suppressed : exception.getSuppressed()) {
			builder.append("\n  Suppressed > ");
			builder.append(suppressed.getSuppressed());
		}

		return builder.toString();
	}

}
