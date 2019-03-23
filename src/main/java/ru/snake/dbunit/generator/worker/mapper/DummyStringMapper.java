package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class DummyStringMapper implements ColumnMapper {

	private final String columnName;

	/**
	 * Creates new dummy text column mapper.
	 *
	 * @param columnName
	 *            column name
	 */
	public DummyStringMapper(final String columnName) {
		this.columnName = columnName;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String map(final ResultSet resultSet) throws SQLException {
		String value = resultSet.getString(columnName);

		if (resultSet.wasNull()) {
			return null;
		} else {
			return value;
		}
	}

	@Override
	public String toString() {
		return "DummyStringMapper [columnName=" + columnName + "]";
	}

}
