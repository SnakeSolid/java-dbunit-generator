package ru.snake.dbunit.generator.listener;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

/**
 * Text editor popup menu listener. Show default cut/copy/paste context menu.
 *
 * @author snake
 *
 */
public final class TextEditorMouseListener extends MouseAdapter {

	private final Action cut;

	private final Action copy;

	private final Action paste;

	private final JPopupMenu popupMenu;

	/**
	 * Create new mouse listener to show default text popup menu.
	 */
	public TextEditorMouseListener() {
		cut = new DefaultEditorKit.CutAction();
		cut.putValue(Action.NAME, "Cut");

		copy = new DefaultEditorKit.CopyAction();
		copy.putValue(Action.NAME, "Copy");

		paste = new DefaultEditorKit.PasteAction();
		paste.putValue(Action.NAME, "Paste");

		popupMenu = new JPopupMenu();
		popupMenu.add(cut);
		popupMenu.add(copy);
		popupMenu.add(paste);
	}

	@Override
	public void mousePressed(final MouseEvent event) {
		if (event.isPopupTrigger()) {
			Component component = event.getComponent();

			if (component instanceof JTextComponent) {
				JTextComponent textComponent = (JTextComponent) component;
				boolean editable = textComponent.isEditable();

				paste.setEnabled(editable);
			}

			int x = event.getX();
			int y = event.getY();

			popupMenu.show(component, x, y);
		}
	}

}
