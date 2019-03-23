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
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import ru.snake.dbunit.generator.config.TypeMapping;
import ru.snake.dbunit.generator.model.ConnectionSettings;
import ru.snake.dbunit.generator.worker.mapper.AsciiStringMapper;
import ru.snake.dbunit.generator.worker.mapper.Base64BytesMapper;
import ru.snake.dbunit.generator.worker.mapper.ColumnMapper;
import ru.snake.dbunit.generator.worker.mapper.DummyStringMapper;
import ru.snake.dbunit.generator.worker.mapper.HexBytesMapper;
import ru.snake.dbunit.generator.worker.mapper.Utf8StringMapper;

/**
 * Background worker. Worker read queries from text, executes every query using
 * given connection setting. All retrieved data-sets will be converted to DBUnit
 * XML representation.
 *
 * @author snake
 *
 */
public final class BuildDatasetWorker extends SwingWorker<Result<String, String>, Void> {

	private final String queryText;

	private final ConnectionSettings connectionSettings;

	private final Document outputDocument;

	/**
	 * Create new worker to perform building data-set from given query list.
	 *
	 * @param queryText
	 *            string with queries
	 * @param connectionSettings
	 *            connection settings
	 * @param outputDocument
	 *            output document
	 */
	public BuildDatasetWorker(
		final String queryText,
		final ConnectionSettings connectionSettings,
		final Document outputDocument
	) {
		this.queryText = queryText;
		this.connectionSettings = connectionSettings;
		this.outputDocument = outputDocument;
	}

	@Override
	protected Result<String, String> doInBackground() throws Exception {
		List<Query> queries = new QuerySplitter(this.queryText).split();

		for (Query query : queries) {
			if (query.getTableName() == null) {
				StringBuilder builder = new StringBuilder();
				builder.append("Table for query not found. ");
				builder.append("Use single line comment (`-- schema.table`) to define table name. ");
				builder.append("Query:\n");
				builder.append(query.getQuery());

				return Result.error(builder.toString());
			}
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
					String tableData = getTableDataset(statement, query.getQuery(), query.getTableName());

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
	 * Generate and result XML data set for single query.
	 *
	 * @param statement
	 *            JDBC statement
	 * @param query
	 *            query string
	 * @param tableName
	 *            table name
	 * @return string with table data set
	 * @throws SQLException
	 *             if error occurred
	 */
	private String getTableDataset(final Statement statement, final String query, final String tableName)
			throws SQLException {
		StringBuilder tableData = new StringBuilder();

		try (ResultSet resultSet = statement.executeQuery(query)) {
			List<ColumnMapper> mappers = getMappers(resultSet);

			while (resultSet.next()) {
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
			}
		}

		if (tableData.length() == 0) {
			tableData.append("    <");
			tableData.append(tableName);
			tableData.append(" />\n");
		}

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
			showError(e);
		}
	}

	/**
	 * Unroll exception messages to multi-line string.
	 *
	 * @param exception
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

	/**
	 * Show exception message dialog.
	 *
	 * @param e
	 *            exception
	 */
	private void showError(final Exception e) {
		JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), null, JOptionPane.ERROR_MESSAGE);
	}

}
