package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class Utf8StringMapper implements ColumnMapper {

	private final String columnName;

	private final XmlEscape escape;

	/**
	 * Creates new text columns to UTF-8 mapper.
	 *
	 * @param columnName
	 *            column name
	 * @param escape
	 *            XML escape
	 */
	public Utf8StringMapper(final String columnName, final XmlEscape escape) {
		this.columnName = columnName;
		this.escape = escape;
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
			} else if (escape.isEscapeableChar(ch)) {
				builder.append(escape.escapeChar(ch));
			} else {
				builder.append(ch);
			}
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "Utf8StringMapper [columnName=" + columnName + ", escape=" + escape + "]";
	}

}
