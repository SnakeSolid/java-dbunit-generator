package ru.snake.dbunit.generator.worker;

/**
 * Data transfer object for single query and table name.
 *
 * @author snake
 *
 */
public final class Query {

	private final String tableName;

	private final String query;

	/**
	 * Create query from table name and query test.
	 *
	 * @param tableName
	 *            table name
	 * @param query
	 *            query text
	 */
	public Query(final String tableName, final String query) {
		this.tableName = tableName;
		this.query = query;
	}

	public String getTableName() {
		return tableName;
	}

	public String getQuery() {
		return query;
	}

	@Override
	public String toString() {
		return "Query [tableName=" + tableName + ", query=" + query + "]";
	}

}
