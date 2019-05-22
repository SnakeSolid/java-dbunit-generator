package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class Utf8StringMapper implements ColumnMapper {

	private final String columnName;

	/**
	 * Creates new text columns to UTF-8 mapper.
	 *
	 * @param columnName
	 *            column name
	 */
	public Utf8StringMapper(final String columnName) {
		this.columnName = columnName;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String map(final ResultSet resultSet) throws SQLException {
		String value = resultSet.getString(columnName);

		if (resultSet.wasNull()) {
			return null;
		} else {
			return escapeControl(value);
		}
	}

	/**
	 * Escape all control characters using numeric encoding.
	 *
	 * @param value
	 *            value
	 * @return UTF-8 string
	 */
	private String escapeControl(final String value) {
		StringBuilder builder = new StringBuilder(value.length());

		for (char ch : value.toCharArray()) {
			if (ch < 32) {
				builder.append("&#x");
				builder.append(Character.forDigit((ch >> 4) & 0x0f, 16));
				builder.append(Character.forDigit((ch >> 0) & 0x0f, 16));
				builder.append(";");
			} else if (ch == '"') {
				builder.append("&quot;");
			} else if (ch == '&') {
				builder.append("&amp;");
			} else if (ch == '\'') {
				builder.append("&apos;");
			} else if (ch == '<') {
				builder.append("&lt;");
			} else if (ch == '>') {
				builder.append("&gt;");
			} else {
				builder.append((char) ch);
			}
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "Utf8StringMapper [columnName=" + columnName + "]";
	}

}
