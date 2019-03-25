package ru.snake.dbunit.generator.worker.query;

/**
 * Data transfer object for single query and table name.
 *
 * @author snake
 *
 */
public final class Query {

	private final String tableName;

	private final String queryText;

	/**
	 * Create query from table name and query test.
	 *
	 * @param tableName
	 *            table name
	 * @param queryText
	 *            query text
	 */
	public Query(final String tableName, final String queryText) {
		this.tableName = tableName;
		this.queryText = queryText;
	}

	/**
	 * Returns table name or null.
	 *
	 * @return table name
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Returns query text.
	 *
	 * @return query text
	 */
	public String getQueryText() {
		return queryText;
	}

	@Override
	public String toString() {
		return "Query [tableName=" + tableName + ", queryText=" + queryText + "]";
	}

}
