package ru.snake.dbunit.generator.worker.dataset;

import java.util.List;

/**
 * Class represents single table row including all column names and values.
 *
 * @author snake
 *
 */
public final class TableRow {

	private final String tableName;

	private final List<String> columnNames;

	private final List<String> values;

	/**
	 * Creates new instance using given table name and column values.
	 *
	 * @param tableName
	 *            table name
	 * @param columnNames
	 *            column names
	 * @param values
	 *            values
	 */
	public TableRow(final String tableName, final List<String> columnNames, final List<String> values) {
		this.tableName = tableName;
		this.columnNames = columnNames;
		this.values = values;
	}

	/**
	 * Returns table name related with this row.
	 *
	 * @return table name
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns {@code true} if row has no values, otherwise {@code false}.
	 *
	 * @return true if row has no values
	 */
	public boolean isEmpty() {
		return columnNames.isEmpty() && values.isEmpty();
	}

	/**
	 * Creates string representing XML element value. Element name will be same
	 * as table name, column names will be attributes.
	 *
	 * @return XML element as string
	 */
	public String toXmlString() {
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
		return "TableRow [tableName=" + tableName + ", columnNames=" + columnNames + ", values=" + values + "]";
	}

}
