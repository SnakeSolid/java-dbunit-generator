package ru.snake.dbunit.generator.worker;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

/**
 *
 * @author snake
 *
 */
public class ResultTest {

	@Test
	public void shouldReturnValue() {
		String value = "value";
		Result<String, String> result = Result.ok(value);

		assertThat(result.isOk(), is(true));
		assertThat(result.isError(), is(false));
		assertThat(result.getValue(), sameInstance(value));
		assertThat(result.getError(), nullValue());
	}

	@Test
	public void shouldReturnError() {
		String error = "error";
		Result<String, String> result = Result.error(error);

		assertThat(result.isOk(), is(false));
		assertThat(result.isError(), is(true));
		assertThat(result.getValue(), nullValue());
		assertThat(result.getError(), sameInstance(error));
	}

}
