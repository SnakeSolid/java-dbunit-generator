package ru.snake.dbunit.generator.worker.parse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 *
 * @author snake
 *
 */
public class QueryTokenizerTest {

	@Test
	public void shouldParseEmptyWhenEmptyStringGiven() {
		List<Token> tokens = QueryTokenizer.tokenize("");

		assertThat(tokens.size(), is(0));
	}

	@Test
	public void shouldParseCodeWhenOnlyCodeGiven() {
		List<Token> tokens = QueryTokenizer.tokenize("select from table_name");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.CODE));
		assertThat(tokens.get(0).getValue(), is("select from table_name"));
	}

	@Test
	public void shouldParseCommentWhenOnlyCommentGiven() {
		List<Token> tokens = QueryTokenizer.tokenize("-- comment line");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.COMMENT));
		assertThat(tokens.get(0).getValue(), is("-- comment line"));
	}

	@Test
	public void shouldParseStringWhenOnlyStringGiven() {
		List<Token> tokens = QueryTokenizer.tokenize("'string with '' quote'");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.CODE));
		assertThat(tokens.get(0).getValue(), is("'string with '' quote'"));
	}

	@Test
	public void shouldParseQueryWhenEndsWithString() {
		List<Token> tokens = QueryTokenizer.tokenize("select *\nfrom table_name\nwhere name = 'test'");

		assertThat(tokens.size(), is(2));
		assertThat(tokens.get(0).getType(), is(TokenType.CODE));
		assertThat(tokens.get(0).getValue(), is("select *\nfrom table_name\nwhere name = "));
		assertThat(tokens.get(1).getType(), is(TokenType.CODE));
		assertThat(tokens.get(1).getValue(), is("'test'"));
	}

	@Test
	public void shouldParseQueryWhenEndsWithSemicolon() {
		List<Token> tokens = QueryTokenizer.tokenize("select *\nfrom table_name;");

		assertThat(tokens.size(), is(2));
		assertThat(tokens.get(0).getType(), is(TokenType.CODE));
		assertThat(tokens.get(0).getValue(), is("select *\nfrom table_name"));
		assertThat(tokens.get(1).getType(), is(TokenType.SEMICOLON));
	}

	@Test
	public void shouldParseQueryWhenEndsWithComment() {
		List<Token> tokens = QueryTokenizer.tokenize("select *\nfrom table_name\n-- line comment");

		assertThat(tokens.size(), is(2));
		assertThat(tokens.get(0).getType(), is(TokenType.CODE));
		assertThat(tokens.get(0).getValue(), is("select *\nfrom table_name\n"));
		assertThat(tokens.get(1).getType(), is(TokenType.COMMENT));
		assertThat(tokens.get(1).getValue(), is("-- line comment"));
	}

	@Test
	public void shouldParseCommentWhenEndsWithCode() {
		List<Token> tokens = QueryTokenizer.tokenize("-- line comment\nselect *\nfrom table_name");

		assertThat(tokens.size(), is(2));
		assertThat(tokens.get(0).getType(), is(TokenType.COMMENT));
		assertThat(tokens.get(0).getValue(), is("-- line comment"));
		assertThat(tokens.get(1).getType(), is(TokenType.CODE));
		assertThat(tokens.get(1).getValue(), is("\nselect *\nfrom table_name"));
	}

	@Test
	public void shouldParseQueriesWhenSeparatedBySemicolon() {
		List<Token> tokens = QueryTokenizer.tokenize("select *\nfrom table_a;select *\nfrom table_b");

		assertThat(tokens.size(), is(3));
		assertThat(tokens.get(0).getType(), is(TokenType.CODE));
		assertThat(tokens.get(0).getValue(), is("select *\nfrom table_a"));
		assertThat(tokens.get(1).getType(), is(TokenType.SEMICOLON));
		assertThat(tokens.get(2).getType(), is(TokenType.CODE));
		assertThat(tokens.get(2).getValue(), is("select *\nfrom table_b"));
	}

	@Test
	public void shouldParseCodeWhenStringInside() {
		List<Token> tokens = QueryTokenizer.tokenize("select * from table_a where 'test' = name");

		assertThat(tokens.size(), is(3));
		assertThat(tokens.get(0).getType(), is(TokenType.CODE));
		assertThat(tokens.get(0).getValue(), is("select * from table_a where "));
		assertThat(tokens.get(1).getType(), is(TokenType.CODE));
		assertThat(tokens.get(1).getValue(), is("'test'"));
		assertThat(tokens.get(2).getType(), is(TokenType.CODE));
		assertThat(tokens.get(2).getValue(), is(" = name"));
	}

	@Test
	public void shouldParseStringWhenItInvalid() {
		List<Token> tokens = QueryTokenizer.tokenize("'string with ''");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.CODE));
		assertThat(tokens.get(0).getValue(), is("'string with ''"));
	}

}
