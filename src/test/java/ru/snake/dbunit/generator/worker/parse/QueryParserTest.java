package ru.snake.dbunit.generator.worker.parse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import ru.snake.dbunit.generator.worker.Query;

/**
 *
 * @author snake
 *
 */
public class QueryParserTest {

	@Test
	public void shouldParseEmptyWhenEmptyStringGiven() {
		List<Query> queries = QueryParser.parse("");

		assertThat(queries.size(), is(0));
	}

	@Test
	public void shoulParseQueryWhenNoCommentsGiven() {
		List<Query> queries = QueryParser.parse("select * from table_name");

		assertThat(queries.size(), is(1));
		assertThat(queries.get(0).getTableName(), nullValue());
		assertThat(queries.get(0).getQueryText(), is("select * from table_name"));
	}

	@Test
	public void shoulParseQueryWhenCommentsGiven() {
		List<Query> queries = QueryParser.parse("-- table_name\nselect * from table_name");

		assertThat(queries.size(), is(1));
		assertThat(queries.get(0).getTableName(), is("table_name"));
		assertThat(queries.get(0).getQueryText(), is("select * from table_name"));
	}

	@Test
	public void shoulParseEmptyWhenSemicolonsGiven() {
		List<Query> queries = QueryParser.parse(";;");

		assertThat(queries.size(), is(0));
	}

	@Test
	public void shoulParseQueryWhenSeveralQueriesGiven() {
		List<Query> queries = QueryParser.parse("-- table_a\nselect * from table_a;select * from table_b");

		assertThat(queries.size(), is(2));
		assertThat(queries.get(0).getTableName(), is("table_a"));
		assertThat(queries.get(0).getQueryText(), is("select * from table_a"));
		assertThat(queries.get(1).getTableName(), nullValue());
		assertThat(queries.get(1).getQueryText(), is("select * from table_b"));
	}

}
