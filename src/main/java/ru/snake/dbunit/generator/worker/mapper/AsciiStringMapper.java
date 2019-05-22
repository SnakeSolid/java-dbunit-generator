package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class AsciiStringMapper implements ColumnMapper {

	private final String columnName;

	/**
	 * Creates new text columns to ASCII mapper.
	 *
	 * @param columnName
	 *            column name
	 */
	public AsciiStringMapper(final String columnName) {
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
			return escapeNonAscii(value);
		}
	}

	/**
	 * Escape non ASCII characters using numeric encoding.
	 *
	 * @param value
	 *            value
	 * @return ASCII value
	 */
	private String escapeNonAscii(final String value) {
		StringBuilder builder = new StringBuilder(value.length());

		for (char ch : value.toCharArray()) {
			if (ch < 32 || ch > 127) {
				builder.append("&#x");

				if (ch >= 256) {
					builder.append(Character.forDigit((ch >> 12) & 0x0f, 16));
					builder.append(Character.forDigit((ch >> 8) & 0x0f, 16));
				}

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
		return "AsciiStringMapper [columnName=" + columnName + "]";
	}

}
