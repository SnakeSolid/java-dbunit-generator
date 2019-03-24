package ru.snake.dbunit.generator.worker.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class HexBytesMapper implements ColumnMapper {

	private final String columnName;

	/**
	 * Creates new binary column to HEX mapper.
	 *
	 * @param columnName
	 *            column name
	 */
	public HexBytesMapper(final String columnName) {
		this.columnName = columnName;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public String map(final ResultSet resultSet) throws SQLException {
		byte[] value = resultSet.getBytes(columnName);

		if (resultSet.wasNull()) {
			return null;
		} else {
			return hexEncode(value);
		}
	}

	/**
	 * Returns HEX encoded string containing given byte array to string.
	 *
	 * @param value
	 *            bytes to encode
	 * @return HEX string
	 */
	private String hexEncode(final byte[] value) {
		StringBuilder builder = new StringBuilder(2 * value.length);

		for (byte b : value) {
			builder.append(Character.forDigit((b >> 4) & 0x0f, 16));
			builder.append(Character.forDigit((b >> 0) & 0x0f, 16));
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		return "HexBytesMapper [columnName=" + columnName + "]";
	}

}
