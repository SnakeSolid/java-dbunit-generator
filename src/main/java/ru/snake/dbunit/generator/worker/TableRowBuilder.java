package ru.snake.dbunit.generator.worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for single data set row.
 *
 * @author snake
 *
 */
public final class TableRowBuilder {

	private final String tableName;

	private final List<String> columnNames;

	private final List<String> values;

	/**
	 * Create new row builder for given table name.
	 *
	 * @param tableName
	 *            table name
	 */
	public TableRowBuilder(final String tableName) {
		this.tableName = tableName;
		this.columnNames = new ArrayList<>();
		this.values = new ArrayList<>();
	}

	/**
	 * Push next column to builder.
	 *
	 * @param columnName
	 *            column name
	 * @param value
	 *            value
	 */
	public void push(final String columnName, final String value) {
		columnNames.add(columnName);
		values.add(value);
	}

	/**
	 * Build XML row from table and available columns.
	 *
	 * @return XML row
	 */
	public String build() {
		StringBuilder builder = new StringBuilder();
		builder.append("<");
		builder.append(tableName);

		for (int index = 0; index < columnNames.size(); index += 1) {
			builder.append(' ');
			builder.append(columnNames.get(index));
			builder.append("=\"");
			builder.append(escapeControlCharacters(values.get(index)));
			builder.append('"');
		}

		builder.append(" />");

		return builder.toString();
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

	@Override
	public String toString() {
		return "TableRowBuilder [tableName=" + tableName + ", columnNames=" + columnNames + ", values=" + values + "]";
	}

}
