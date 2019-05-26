package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.snake.dbunit.generator.config.TypeMapping;
import ru.snake.dbunit.generator.model.ConnectionSettings;

/**
 * Mappers builder. Build list of column mappers for all available
 * {@link ResultSet} columns.
 *
 * @author snake
 *
 */
public final class MapperBuilder {

	private final ConnectionSettings connectionSettings;

	/**
	 * Create new instance of mappers builder using given connection settings.
	 *
	 * @param connectionSettings
	 *            connection settings
	 */
	public MapperBuilder(final ConnectionSettings connectionSettings) {
		this.connectionSettings = connectionSettings;
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
	public List<ColumnMapper> buildMappers(final ResultSet resultSet) throws SQLException {
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
			return new DummyStringMapper(columnName, new XmlEscape());
		}

		switch (dataMapper) {
		case ASCII:
			return new AsciiStringMapper(columnName, new XmlEscape());

		case UTF8:
			return new Utf8StringMapper(columnName, new XmlEscape());

		case HEX:
			return new HexBytesMapper(columnName);

		case BASE64:
			return new Base64BytesMapper(columnName);

		case BASE64_WITH_PREFIX:
			return new Base64PrefixBytesMapper(columnName);

		default:
			throw new IllegalArgumentException("Data mapper " + dataMapper + " has no corresponding class.");
		}
	}

	@Override
	public String toString() {
		return "MapperBuilder [connectionSettings=" + connectionSettings + "]";
	}

}
