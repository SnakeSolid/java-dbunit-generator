package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ColumnMapper {

	/**
	 * Returns column name.
	 *
	 * @return column name
	 */
	String getColumnName();

	/**
	 * Map single column value from result set to text representation of XML
	 * attribute.
	 *
	 * @param resultSet
	 *            result set
	 * @return string XML attribute
	 * @throws SQLException
	 *             if error occurred
	 */
	String map(ResultSet resultSet) throws SQLException;

}
