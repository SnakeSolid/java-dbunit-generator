package ru.snake.dbunit.generator.worker;

import java.util.ArrayList;
import java.util.List;

public class QuerySplitter {

	private final String text;

	public QuerySplitter(final String text) {
		this.text = text;
	}

	public List<Query> split() {
		List<Query> result = new ArrayList<>();
		String tableName = null;
		StringBuilder builder = new StringBuilder();

		for (String line : this.text.split("[\\r\\n]+")) {
			if (line.contentEquals(";")) {
				String queryString = builder.toString();

				result.add(new Query(tableName, queryString));

				builder = new StringBuilder();
			} else if (line.startsWith("-- ")) {
				tableName = line.substring(3).toUpperCase();
			} else {
				builder.append(line);
				builder.append('\n');
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return "QuerySplitter [text=" + text + "]";
	}

}
