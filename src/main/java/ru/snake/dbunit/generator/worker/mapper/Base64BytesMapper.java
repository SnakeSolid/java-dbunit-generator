package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public final class Base64BytesMapper implements ColumnMapper {

	private final String columnName;

	/**
	 * Creates new binary column to BASE64 mapper.
	 *
	 * @param columnName
	 *            column name
	 */
	public Base64BytesMapper(final String columnName) {
		this.columnName = columnName;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String map(final ResultSet resultSet) throws SQLException {
		byte[] value = resultSet.getBytes(columnName);

		if (resultSet.wasNull()) {
			return null;
		} else {
			String encoded = Base64.getEncoder().encodeToString(value);

			return encoded;
		}
	}

	@Override
	public String toString() {
		return "Base64BytesMapper [columnName=" + columnName + "]";
	}

}
