package ru.snake.dbunit.generator.worker.query;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import ru.snake.dbunit.generator.worker.Result;

/**
 *
 * @author snake
 *
 */
public class QueryTemplateTest {

	@Test
	public void shouldReturnErrorIfQueryEmpty() {
		Result<List<Query>, String> result = QueryTemplate.generate(query(""));

		assertThat(result.isError(), is(true));
	}

	@Test
	public void shouldReturnErrorIfNoTables() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select *"));

		assertThat(result.isError(), is(true));
	}

	@Test
	public void shouldReturnErrorIfMissedPlaceholder() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select a from table"));

		assertThat(result.isError(), is(true));
	}

	@Test
	public void shouldReturnErrorIfWithPresent() {
		Result<List<Query>, String> result = QueryTemplate
			.generate(query("with raw as ( select 1 ) select * from raw"));

		assertThat(result.isError(), is(true));
	}

	@Test
	public void shouldReturnErrorIfSubqueryPresent() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select * from raw where b in ( select 1 )"));

		assertThat(result.isError(), is(true));
	}

	@Test
	public void shouldReturnQueryIfOneTableName() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select * from table_a"));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(1));
		assertThat(result.getValue().get(0).getTableName(), is("table_a"));
		assertThat(result.getValue().get(0).getQueryText(), is("select table_a.* from table_a"));
	}

	@Test
	public void shouldReturnQueryIfOneTableNameSchema() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select * from me.table_a"));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(1));
		assertThat(result.getValue().get(0).getTableName(), is("me.table_a"));
		assertThat(result.getValue().get(0).getQueryText(), is("select me.table_a.* from me.table_a"));
	}

	@Test
	public void shouldReturnQueryIfOneTableNameAlias() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select * from table_a a"));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(1));
		assertThat(result.getValue().get(0).getTableName(), is("table_a"));
		assertThat(result.getValue().get(0).getQueryText(), is("select a.* from table_a a"));
	}

	@Test
	public void shouldReturnQueryIfOneTableNameSchemaAlias() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select * from me.table_a a"));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(1));
		assertThat(result.getValue().get(0).getTableName(), is("me.table_a"));
		assertThat(result.getValue().get(0).getQueryText(), is("select a.* from me.table_a a"));
	}

	@Test
	public void shouldReturnQueryIfOneTableNameAsAlias() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select * from table_a as a"));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(1));
		assertThat(result.getValue().get(0).getTableName(), is("table_a"));
		assertThat(result.getValue().get(0).getQueryText(), is("select a.* from table_a as a"));
	}

	@Test
	public void shouldReturnQueryIfOneTableNameSchemaAsAlias() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select * from me.table_a as a"));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(1));
		assertThat(result.getValue().get(0).getTableName(), is("me.table_a"));
		assertThat(result.getValue().get(0).getQueryText(), is("select a.* from me.table_a as a"));
	}

	@Test
	public void shouldReturnQueryIfKeywordAlias() {
		Result<List<Query>, String> result = QueryTemplate.generate(query("select * from table_a where id = 3"));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(1));
		assertThat(result.getValue().get(0).getTableName(), is("table_a"));
		assertThat(result.getValue().get(0).getQueryText(), is("select table_a.* from table_a where id = 3"));
	}

	@Test
	public void shouldReturnQueriesIfThreeTables() {
		Result<List<Query>, String> result = QueryTemplate.generate(
			query(
				"select * from me.table_a inner join me.table_b b using ( id ) "
						+ "left outer join me.table_c as c on ( b.id = c.id )"
			)
		);

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(3));
		assertThat(result.getValue().get(0).getTableName(), is("me.table_a"));
		assertThat(
			result.getValue().get(0).getQueryText(),
			is(
				"select me.table_a.* from me.table_a inner join me.table_b b using ( id ) "
						+ "left outer join me.table_c as c on ( b.id = c.id )"
			)
		);
		assertThat(result.getValue().get(1).getTableName(), is("me.table_b"));
		assertThat(
			result.getValue().get(1).getQueryText(),
			is(
				"select b.* from me.table_a inner join me.table_b b using ( id ) "
						+ "left outer join me.table_c as c on ( b.id = c.id )"
			)
		);
		assertThat(result.getValue().get(2).getTableName(), is("me.table_c"));
		assertThat(
			result.getValue().get(2).getQueryText(),
			is(
				"select c.* from me.table_a inner join me.table_b b using ( id ) "
						+ "left outer join me.table_c as c on ( b.id = c.id )"
			)
		);
	}

	/**
	 * Create new query with given text and empty table name.
	 *
	 * @param text
	 *            query text
	 * @return query
	 */
	private static Query query(final String text) {
		return new Query(null, text);
	}

}
