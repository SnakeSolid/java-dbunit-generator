package ru.snake.dbunit.generator.document.parse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class SqlTokenizerTest {

	@Test
	public void shouldParseEmptyWhenEmptyStringGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("");

		assertThat(tokens.size(), is(0));
	}

	@Test
	public void shouldParseKeywordWhenOnlyKeywordGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("select");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.KEYWORD));
		assertThat(tokens.get(0).getValue(), is("select"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(6));
	}

	@Test
	public void shouldParseTokenWhenOnlyTokenGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("table_name");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(0).getValue(), is("table_name"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(10));
	}

	@Test
	public void shouldParseCodeWhenOnlyNumberGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("123");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(0).getValue(), is("123"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(3));
	}

	@Test
	public void shouldParseCommentWhenOnlyCommentGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("-- comment");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.COMMENT));
		assertThat(tokens.get(0).getValue(), is("-- comment"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(10));
	}

	@Test
	public void shouldParseStringWhenOnlyStringGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("'string'");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.STRING));
		assertThat(tokens.get(0).getValue(), is("'string'"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(8));
	}

	@Test
	public void shouldParseStringWhenOnlyApostropheGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("'");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.STRING));
		assertThat(tokens.get(0).getValue(), is("'"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(1));
	}

	@Test
	public void shouldParseStringWhenContainsExcapeGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("'i''m string'");

		assertThat(tokens.size(), is(1));
		assertThat(tokens.get(0).getType(), is(TokenType.STRING));
		assertThat(tokens.get(0).getValue(), is("'i''m string'"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(13));
	}

	@Test
	public void shouldParseQueryWhenStringInsindeGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("select 'string' from dual");

		assertThat(tokens.size(), is(7));
		assertThat(tokens.get(0).getType(), is(TokenType.KEYWORD));
		assertThat(tokens.get(0).getValue(), is("select"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(6));
		assertThat(tokens.get(1).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(1).getValue(), is(" "));
		assertThat(tokens.get(1).getOffset(), is(6));
		assertThat(tokens.get(1).getLength(), is(1));
		assertThat(tokens.get(2).getType(), is(TokenType.STRING));
		assertThat(tokens.get(2).getValue(), is("'string'"));
		assertThat(tokens.get(2).getOffset(), is(7));
		assertThat(tokens.get(2).getLength(), is(8));
		assertThat(tokens.get(3).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(3).getValue(), is(" "));
		assertThat(tokens.get(3).getOffset(), is(15));
		assertThat(tokens.get(3).getLength(), is(1));
		assertThat(tokens.get(4).getType(), is(TokenType.KEYWORD));
		assertThat(tokens.get(4).getValue(), is("from"));
		assertThat(tokens.get(4).getOffset(), is(16));
		assertThat(tokens.get(4).getLength(), is(4));
		assertThat(tokens.get(5).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(5).getValue(), is(" "));
		assertThat(tokens.get(5).getOffset(), is(20));
		assertThat(tokens.get(5).getLength(), is(1));
		assertThat(tokens.get(6).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(6).getValue(), is("dual"));
		assertThat(tokens.get(6).getOffset(), is(21));
		assertThat(tokens.get(6).getLength(), is(4));
	}

	@Test
	public void shouldParseQueryWhenCommentInsindeGiven() {
		List<Token> tokens = SqlTokenizer.tokenize("select * -- all\nfrom dual");

		assertThat(tokens.size(), is(7));
		assertThat(tokens.get(0).getType(), is(TokenType.KEYWORD));
		assertThat(tokens.get(0).getValue(), is("select"));
		assertThat(tokens.get(0).getOffset(), is(0));
		assertThat(tokens.get(0).getLength(), is(6));
		assertThat(tokens.get(1).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(1).getValue(), is(" * "));
		assertThat(tokens.get(1).getOffset(), is(6));
		assertThat(tokens.get(1).getLength(), is(3));
		assertThat(tokens.get(2).getType(), is(TokenType.COMMENT));
		assertThat(tokens.get(2).getValue(), is("-- all"));
		assertThat(tokens.get(2).getOffset(), is(9));
		assertThat(tokens.get(2).getLength(), is(6));
		assertThat(tokens.get(3).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(3).getValue(), is("\n"));
		assertThat(tokens.get(3).getOffset(), is(15));
		assertThat(tokens.get(3).getLength(), is(1));
		assertThat(tokens.get(4).getType(), is(TokenType.KEYWORD));
		assertThat(tokens.get(4).getValue(), is("from"));
		assertThat(tokens.get(4).getOffset(), is(16));
		assertThat(tokens.get(4).getLength(), is(4));
		assertThat(tokens.get(5).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(5).getValue(), is(" "));
		assertThat(tokens.get(5).getOffset(), is(20));
		assertThat(tokens.get(5).getLength(), is(1));
		assertThat(tokens.get(6).getType(), is(TokenType.OTHER));
		assertThat(tokens.get(6).getValue(), is("dual"));
		assertThat(tokens.get(6).getOffset(), is(21));
		assertThat(tokens.get(6).getLength(), is(4));
	}

}
