package ru.snake.dbunit.generator.worker;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Builder for whole data set.
 *
 * @author snake
 *
 */
public final class DatasetBuilder {

	private final Set<String> tableNames;

	private final Map<String, Set<String>> tableRows;

	/**
	 * Create empty data set builder.
	 */
	public DatasetBuilder() {
		this.tableNames = new LinkedHashSet<>();
		this.tableRows = new HashMap<>();
	}

	/**
	 * Ensure that table will be added to result even if it has no rows.
	 *
	 * @param tableName
	 *            table name
	 */
	public void ensureTable(final String tableName) {
		tableNames.add(tableName);
	}

	/**
	 * Add next row to table. Rows within same table are unique.
	 *
	 * @param tableName
	 *            table name
	 * @param tableRow
	 *            table row
	 */
	public void pushRow(final String tableName, final String tableRow) {
		tableRows.computeIfAbsent(tableName, e -> new LinkedHashSet<>()).add(tableRow);
	}

	/**
	 * Build whole XML data set. Data set will contains only distinct rows.
	 *
	 * @return XML data set
	 */
	public String build() {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		builder.append("<dataset>\n");

		boolean isFirst = true;

		for (String tableName : tableNames) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append("\n");
			}

			Set<String> rows = tableRows.getOrDefault(tableName, Collections.emptySet());

			if (rows.isEmpty()) {
				builder.append("    <");
				builder.append(tableName);
				builder.append(" />\n");
			}

			for (String row : rows) {
				builder.append("    ");
				builder.append(row);
				builder.append("\n");
			}
		}

		builder.append("</dataset>\n");

		return builder.toString();
	}

	@Override
	public String toString() {
		return "DatasetBuilder [tableNames=" + tableNames + ", tableRows=" + tableRows + "]";
	}

}
