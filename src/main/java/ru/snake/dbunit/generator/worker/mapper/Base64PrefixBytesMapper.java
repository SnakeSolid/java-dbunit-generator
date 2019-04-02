package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public final class Base64PrefixBytesMapper implements ColumnMapper {

	private final String columnName;

	/**
	 * Creates new binary column to BASE64 mapper with prefix {@code [BASE64]}.
	 *
	 * @param columnName
	 *            column name
	 */
	public Base64PrefixBytesMapper(final String columnName) {
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

			return "[BASE64]" + encoded;
		}
	}

	@Override
	public String toString() {
		return "Base64PrefixBytesMapper [columnName=" + columnName + "]";
	}

}
