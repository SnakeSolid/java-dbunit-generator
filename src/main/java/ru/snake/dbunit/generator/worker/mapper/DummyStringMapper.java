package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class DummyStringMapper implements ColumnMapper {

	private final String columnName;

	private final XmlEscape escape;

	/**
	 * Creates new dummy text column mapper.
	 *
	 * @param columnName
	 *            column name
	 * @param escape
	 *            XML escape
	 */
	public DummyStringMapper(final String columnName, final XmlEscape escape) {
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
			return escapeXml(value);
		}
	}

	/**
	 * Escape XML special characters.
	 *
	 * @param value
	 *            value
	 * @return XML safe value
	 */
	private String escapeXml(final String value) {
		StringBuilder builder = new StringBuilder(value.length());

		for (char ch : value.toCharArray()) {
			if (escape.isEscapeableChar(ch)) {
				builder.append(escape.escapeChar(ch));
			} else {
				builder.append((char) ch);
			}
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "DummyStringMapper [columnName=" + columnName + ", escape=" + escape + "]";
	}

}
