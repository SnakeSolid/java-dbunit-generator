package ru.snake.dbunit.generator.worker.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.snake.dbunit.generator.worker.Result;

/**
 * Create sequence of queries from single query. Query will be used as template.
 *
 * @author snake
 *
 */
public final class QueryTemplate {
	private static final Collection<Pattern> NO_SUBSTITUTE;

	private static final Collection<Pattern> TABLE_NAMES;

	private static final Set<String> SQL_KEYWORDS;

	private final String queryText;

	static {
		// Tables in query can't be substituted if
		NO_SUBSTITUTE = new ArrayList<>();
		NO_SUBSTITUTE.add(Pattern.compile("\\bwith\\s+", Pattern.CASE_INSENSITIVE));
		NO_SUBSTITUTE.add(Pattern.compile("\\bfrom\\s*\\(", Pattern.CASE_INSENSITIVE));
		NO_SUBSTITUTE.add(Pattern.compile("\\bjoin\\s*\\(", Pattern.CASE_INSENSITIVE));
		NO_SUBSTITUTE.add(Pattern.compile("\\(\\s*select\\b", Pattern.CASE_INSENSITIVE));

		// Table name patterns from SQL
		TABLE_NAMES = new ArrayList<>();
		TABLE_NAMES.add(Pattern.compile("\\bfrom\\s+(\\w+(\\.\\w+)?)(\\s+as)?(\\s+(\\w+))?", Pattern.CASE_INSENSITIVE));
		TABLE_NAMES.add(Pattern.compile("\\bjoin\\s+(\\w+(\\.\\w+)?)(\\s+as)?(\\s+(\\w+))?", Pattern.CASE_INSENSITIVE));

		// SQL keywords not allowed as table alias
		SQL_KEYWORDS = new HashSet<>();
		SQL_KEYWORDS.add("inner");
		SQL_KEYWORDS.add("left");
		SQL_KEYWORDS.add("right");
		SQL_KEYWORDS.add("cross");
		SQL_KEYWORDS.add("natural");
		SQL_KEYWORDS.add("join");
		SQL_KEYWORDS.add("using");
		SQL_KEYWORDS.add("on");
		SQL_KEYWORDS.add("where");
		SQL_KEYWORDS.add("having");
		SQL_KEYWORDS.add("group");
		SQL_KEYWORDS.add("order");
		SQL_KEYWORDS.add("limit");
		SQL_KEYWORDS.add("into");
		SQL_KEYWORDS.add("for");
	}

	/**
	 * Create new query template using given text.
	 *
	 * @param queryText
	 *            query text
	 */
	private QueryTemplate(final String queryText) {
		this.queryText = queryText;
	}

	/**
	 * Generate queries using given query as template. Returns error if query
	 * contains sub queries or does not contain `*` place holder.
	 *
	 * @return query list or error description
	 */
	private Result<List<Query>, String> generate() {
		for (Pattern pattern : NO_SUBSTITUTE) {
			if (pattern.matcher(queryText).find()) {
				return Result.error(getSubqueryError());
			}
		}

		int starIndex = queryText.indexOf('*');

		if (starIndex == -1) {
			return Result.error(getPlaceholderError());
		}

		List<Query> result = new ArrayList<>();
		String queryLeft = queryText.substring(0, starIndex);
		String queryRight = queryText.substring(starIndex + 1);

		for (Pattern pattern : TABLE_NAMES) {
			Matcher matcher = pattern.matcher(queryText);

			while (matcher.find()) {
				String tableName = matcher.group(1);
				String tableAlias = matcher.group(5);
				Query nextQuery;

				if (tableAlias == null || SQL_KEYWORDS.contains(tableAlias.toLowerCase())) {
					nextQuery = quildQuery(queryLeft, queryRight, tableName, tableName);
				} else {
					nextQuery = quildQuery(queryLeft, queryRight, tableName, tableAlias);
				}

				result.add(nextQuery);
			}
		}

		if (result.isEmpty()) {
			return Result.error(getEmptyTemplateError());
		}

		return Result.ok(result);
	}

	/**
	 * Creates error for no tables error.
	 *
	 * @return error description
	 */
	private String getEmptyTemplateError() {
		StringBuilder builder = new StringBuilder();
		builder.append("Building template failed. ");
		builder.append("NO table found in this query. ");
		builder.append("Query:\n");
		builder.append(queryText);

		return builder.toString();
	}

	/**
	 * Creates error description for missing placeholder error.
	 *
	 * @return error description
	 */
	private String getPlaceholderError() {
		StringBuilder builder = new StringBuilder();
		builder.append("Building template failed. ");
		builder.append("Query does not contain table placeholder `*`. ");
		builder.append("Query:\n");
		builder.append(queryText);

		return builder.toString();
	}

	/**
	 * Creates error description for too complicated query template.
	 *
	 * @return error description
	 */
	private String getSubqueryError() {
		StringBuilder builder = new StringBuilder();
		builder.append("Building template failed. ");
		builder.append("Query has subqueries or CTE's and can't be used for substitution. ");
		builder.append("Query:\n");
		builder.append(queryText);

		return builder.toString();
	}

	/**
	 * Build query using given template.
	 *
	 * @param queryLeft
	 *            left query part
	 * @param queryRight
	 *            right query part
	 * @param tableName
	 *            table name
	 * @param tableAlias
	 *            table alias
	 * @return query text
	 */
	private Query quildQuery(
		final String queryLeft,
		final String queryRight,
		final String tableName,
		final String tableAlias
	) {
		StringBuilder builder = new StringBuilder();
		builder.append(queryLeft);
		builder.append(tableAlias);
		builder.append(".*");
		builder.append(queryRight);

		return new Query(tableName, builder.toString());
	}

	/**
	 * Generate queries using given query template.
	 *
	 * @param query
	 *            query template
	 * @return query list or error
	 */
	public static Result<List<Query>, String> generate(final Query query) {
		return new QueryTemplate(query.getQueryText()).generate();
	}

	@Override
	public String toString() {
		return "QueryTemplate [queryText=" + queryText + "]";
	}

}
