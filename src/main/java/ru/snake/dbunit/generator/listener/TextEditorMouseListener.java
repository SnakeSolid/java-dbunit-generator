package ru.snake.dbunit.generator.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

/**
 * Text editor popup menu listener. Show default cut/copy/paste context menu.
 *
 * @author snake
 *
 */
public final class TextEditorMouseListener extends MouseAdapter {

	private final JPopupMenu popupMenu;

	/**
	 * Create new mouse listener to show default text popup menu.
	 *
	 * @param popupMenu
	 *            popup menu
	 */
	public TextEditorMouseListener(final JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	@Override
	public void mousePressed(final MouseEvent event) {
		if (event.isPopupTrigger()) {
			Object component = event.getSource();

			if (component instanceof JTextComponent) {
				JTextComponent textComponent = (JTextComponent) component;
				int x = event.getX();
				int y = event.getY();

				popupMenu.show(textComponent, x, y);
			}
		}
	}

}
