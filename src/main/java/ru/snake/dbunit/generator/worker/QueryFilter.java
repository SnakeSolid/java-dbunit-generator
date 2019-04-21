package ru.snake.dbunit.generator.worker;

import java.util.ArrayList;
import java.util.List;

import ru.snake.dbunit.generator.config.NoTableMode;
import ru.snake.dbunit.generator.worker.query.Query;
import ru.snake.dbunit.generator.worker.query.QueryTemplate;

/**
 * Query list filter. Produce list of simple query from source queries. Source
 * queries can contain skipped queries, templates and simple queries. Result
 * will contain error or simple query list.
 *
 * @author snake
 *
 */
public final class QueryFilter {

	private final NoTableMode noTableMode;

	private final String templateName;

	private final String skipPrefix;

	/**
	 * Create new table filter.
	 *
	 * @param noTableMode
	 *            no table name mode
	 * @param templateName
	 *            template table name
	 * @param skipPrefix
	 *            skip table name prefix
	 */
	public QueryFilter(final NoTableMode noTableMode, final String templateName, final String skipPrefix) {
		this.noTableMode = noTableMode;
		this.templateName = templateName;
		this.skipPrefix = skipPrefix;
	}

	/**
	 * Process given queries and create new list with simple query. Every query
	 * in result will include table name. If query contains skip marker it will
	 * be removed from result list. If query is template it will be split to
	 * simple queries.
	 *
	 * @param queries
	 *            queries to filter
	 * @return valid query list
	 */
	public Result<List<Query>, String> filter(final List<Query> queries) {
		List<Query> result = new ArrayList<>();

		for (Query query : queries) {
			if (isSkippedQuery(query)) {
				continue;
			} else if (isTemplateQuery(query)) {
				Result<List<Query>, String> generatedResult = QueryTemplate.generate(query);

				if (generatedResult.isError()) {
					return Result.error(generatedResult.getError());
				}

				result.addAll(generatedResult.getValue());
			} else if (isInvalidQuery(query)) {
				StringBuilder builder = new StringBuilder();
				builder.append("Table for query not defined. ");
				builder.append("Use single line comment (`-- schema.table`) to define table name. ");
				builder.append("Query:\n");
				builder.append(query.getQueryText());

				return Result.error(builder.toString());
			} else {
				result.add(query);
			}
		}

		return Result.ok(result);
	}

	/**
	 * Returns true if this query table name starts with the same as skip table
	 * name in configuration.
	 *
	 * @param query
	 *            query
	 * @return true if name is dummy
	 */
	private boolean isInvalidQuery(final Query query) {
		String tableName = query.getTableName();

		return tableName == null;
	}

	/**
	 * Returns true if this query table name starts with the same as skip table
	 * name in configuration.
	 *
	 * @param query
	 *            query
	 * @return true if name is dummy
	 */
	private boolean isTemplateQuery(final Query query) {
		String tableName = query.getTableName();

		if (tableName == null) {
			return noTableMode == NoTableMode.TEMPLATE;
		}

		if (templateName == null) {
			return false;
		}

		return tableName.equals(templateName);
	}

	/**
	 * Returns true if this query table name starts with the same as skip table
	 * name in configuration.
	 *
	 * @param query
	 *            query
	 * @return true if name is dummy
	 */
	private boolean isSkippedQuery(final Query query) {
		String tableName = query.getTableName();

		if (tableName == null || skipPrefix == null) {
			return false;
		}

		return tableName.startsWith(skipPrefix);
	}

	@Override
	public String toString() {
		return "QueryFilter [noTableMode=" + noTableMode + ", templateName=" + templateName + ", skipPrefix="
				+ skipPrefix + "]";
	}

}
