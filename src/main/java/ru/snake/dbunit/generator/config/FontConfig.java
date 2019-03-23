package ru.snake.dbunit.generator.config;

/**
 * Font configuration settings - name, size and style.
 *
 * @author snake
 *
 */
public class FontConfig {

	private static final int DEFAULT_SIZE = 12;

	private String name;

	private FontStyle style;

	private int size;

	/**
	 * Create default font configuration.
	 */
	public FontConfig() {
		this.name = "Monospaced";
		this.style = FontStyle.PLAIN;
		this.size = DEFAULT_SIZE;
	}

	public String getName() {
		return name;
	}

	public FontStyle getStyle() {
		return style;
	}

	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "FontConfig [name=" + name + ", style=" + style + ", size=" + size + "]";
	}

}
