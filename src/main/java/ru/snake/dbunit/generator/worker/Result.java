package ru.snake.dbunit.generator.worker;

/**
 * Result type. Can contain only one value or error.
 *
 * @author snake
 *
 * @param <T>
 *            value type
 * @param <E>
 *            error type
 */
public final class Result<T, E> {

	private final T value;

	private final E error;

	/**
	 * Create new result containing value.
	 *
	 * @param value
	 *            value
	 * @return new result
	 * @param <T>
	 *            value type
	 * @param <E>
	 *            error type
	 */
	public static <T, E> Result<T, E> ok(final T value) {
		return new Result<>(value, null);
	}

	/**
	 * Create new result containing error.
	 *
	 * @param error
	 *            error
	 * @return new result
	 * @param <T>
	 *            value type
	 * @param <E>
	 *            error type
	 */
	public static <T, E> Result<T, E> error(final E error) {
		return new Result<>(null, error);
	}

	/**
	 * Create new result from value and error values.
	 *
	 * @param value
	 *            value
	 * @param error
	 *            error
	 */
	private Result(final T value, final E error) {
		this.value = value;
		this.error = error;
	}

	public boolean isOk() {
		return this.value != null;
	}

	public boolean isError() {
		return this.error != null;
	}

	public T getValue() {
		return value;
	}

	public E getError() {
		return error;
	}

	@Override
	public String toString() {
		return "Result [value=" + value + ", error=" + error + "]";
	}

}
