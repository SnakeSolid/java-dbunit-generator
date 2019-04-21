package ru.snake.dbunit.generator.worker;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ru.snake.dbunit.generator.config.NoTableMode;
import ru.snake.dbunit.generator.worker.query.Query;

/**
 *
 * @author snake
 *
 */
public class QueryFilterTest {

	@Test
	public void shouldReturnEmptyWhenSourceEmpty() {
		QueryFilter queryFilter = new QueryFilter(null, null, null);
		Result<List<Query>, String> result = queryFilter.filter(Arrays.asList());

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().isEmpty(), is(true));
	}

	@Test
	public void shouldReturnSingleQueryWhenOneQueryGiven() {
		QueryFilter queryFilter = new QueryFilter(null, null, null);
		Result<List<Query>, String> result = queryFilter.filter(Arrays.asList(new Query("table_1", "query_1")));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(1));
		assertThat(result.getValue().get(0).getTableName(), is("table_1"));
		assertThat(result.getValue().get(0).getQueryText(), is("query_1"));
	}

	@Test
	public void shouldSkipQueryWhenQueryHasPrefix() {
		QueryFilter queryFilter = new QueryFilter(null, null, "-");
		Result<List<Query>, String> result = queryFilter.filter(Arrays.asList(new Query("-table_1", "query_1")));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().isEmpty(), is(true));
	}

	@Test
	public void shouldReturnErrorWhenTableNullAndModeNull() {
		QueryFilter queryFilter = new QueryFilter(null, null, null);
		Result<List<Query>, String> result = queryFilter
			.filter(Arrays.asList(new Query(null, "select * from table_1 inner join table_2 using (id)")));

		assertThat(result.isError(), is(true));
	}

	@Test
	public void shouldReturnErrorWhenTableNullAndModeError() {
		QueryFilter queryFilter = new QueryFilter(NoTableMode.ERROR, null, null);
		Result<List<Query>, String> result = queryFilter
			.filter(Arrays.asList(new Query(null, "select * from table_1 inner join table_2 using (id)")));

		assertThat(result.isError(), is(true));
	}

	@Test
	public void shouldProcessTemplateWhenTableNullAndModeTemplate() {
		QueryFilter queryFilter = new QueryFilter(NoTableMode.TEMPLATE, null, null);
		Result<List<Query>, String> result = queryFilter
			.filter(Arrays.asList(new Query(null, "select * from table_1 inner join table_2 using (id)")));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(2));
		assertThat(result.getValue().get(0).getTableName(), is("table_1"));
		assertThat(
			result.getValue().get(0).getQueryText(),
			is("select table_1.* from table_1 inner join table_2 using (id)")
		);
		assertThat(result.getValue().get(1).getTableName(), is("table_2"));
		assertThat(
			result.getValue().get(1).getQueryText(),
			is("select table_2.* from table_1 inner join table_2 using (id)")
		);
	}

	@Test
	public void shouldProcessTemplateWhenTableTemplateAndModeNull() {
		QueryFilter queryFilter = new QueryFilter(null, "*", null);
		Result<List<Query>, String> result = queryFilter
			.filter(Arrays.asList(new Query("*", "select * from table_1 inner join table_2 using (id)")));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(2));
		assertThat(result.getValue().get(0).getTableName(), is("table_1"));
		assertThat(
			result.getValue().get(0).getQueryText(),
			is("select table_1.* from table_1 inner join table_2 using (id)")
		);
		assertThat(result.getValue().get(1).getTableName(), is("table_2"));
		assertThat(
			result.getValue().get(1).getQueryText(),
			is("select table_2.* from table_1 inner join table_2 using (id)")
		);
	}

	@Test
	public void shouldProcessTemplateWhenTableTemplateAndModeError() {
		QueryFilter queryFilter = new QueryFilter(NoTableMode.ERROR, "*", null);
		Result<List<Query>, String> result = queryFilter
			.filter(Arrays.asList(new Query("*", "select * from table_1 inner join table_2 using (id)")));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(2));
		assertThat(result.getValue().get(0).getTableName(), is("table_1"));
		assertThat(
			result.getValue().get(0).getQueryText(),
			is("select table_1.* from table_1 inner join table_2 using (id)")
		);
		assertThat(result.getValue().get(1).getTableName(), is("table_2"));
		assertThat(
			result.getValue().get(1).getQueryText(),
			is("select table_2.* from table_1 inner join table_2 using (id)")
		);
	}

	@Test
	public void shouldProcessTemplateWhenTableTemplateAndModeTemplate() {
		QueryFilter queryFilter = new QueryFilter(NoTableMode.TEMPLATE, "*", null);
		Result<List<Query>, String> result = queryFilter
			.filter(Arrays.asList(new Query("*", "select * from table_1 inner join table_2 using (id)")));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(2));
		assertThat(result.getValue().get(0).getTableName(), is("table_1"));
		assertThat(
			result.getValue().get(0).getQueryText(),
			is("select table_1.* from table_1 inner join table_2 using (id)")
		);
		assertThat(result.getValue().get(1).getTableName(), is("table_2"));
		assertThat(
			result.getValue().get(1).getQueryText(),
			is("select table_2.* from table_1 inner join table_2 using (id)")
		);
	}

	@Test
	public void shouldReturnErrorWhenTemplateError() {
		QueryFilter queryFilter = new QueryFilter(NoTableMode.TEMPLATE, null, null);
		Result<List<Query>, String> result = queryFilter
			.filter(Arrays.asList(new Query(null, "select id from table_1")));

		assertThat(result.isError(), is(true));
	}

	@Test
	public void shouldReturnAllQueriesWhenSeveralQueriesGiven() {
		QueryFilter queryFilter = new QueryFilter(null, null, null);
		Result<List<Query>, String> result = queryFilter
			.filter(Arrays.asList(new Query("table_1", "query_1"), new Query("table_2", "query_2")));

		assertThat(result.isOk(), is(true));
		assertThat(result.getValue().size(), is(2));
		assertThat(result.getValue().get(0).getTableName(), is("table_1"));
		assertThat(result.getValue().get(0).getQueryText(), is("query_1"));
		assertThat(result.getValue().get(1).getTableName(), is("table_2"));
		assertThat(result.getValue().get(1).getQueryText(), is("query_2"));
	}

}
