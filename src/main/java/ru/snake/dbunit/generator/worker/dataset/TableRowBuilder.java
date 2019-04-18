package ru.snake.dbunit.generator.worker.dataset;

import java.util.ArrayList;
import java.util.Collections;
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
	public TableRow build() {
		return new TableRow(tableName, Collections.unmodifiableList(columnNames), Collections.unmodifiableList(values));
	}

	@Override
	public String toString() {
		return "TableRowBuilder [tableName=" + tableName + ", columnNames=" + columnNames + ", values=" + values + "]";
	}

}
