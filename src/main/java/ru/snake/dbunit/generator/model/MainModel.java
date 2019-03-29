package ru.snake.dbunit.generator.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;

import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.listener.DocumentModifiedListener;

/**
 * Main frame internal state model. Contains connections settings and text
 * documents from query and result data set.
 *
 * @author snake
 *
 */
public class MainModel {

	private final Configuration config;

	private final Document queryDocument;

	private final Document datasetDocument;

	private final List<ConnectionListener> connectionListeners;

	private final List<EditorStateListener> editorStateListeners;

	private ConnectionSettings currentConnection;

	private boolean modified;

	private File file;

	/**
	 * Creates empty model instance with given configuration settings.
	 *
	 * @param config
	 *            configuration settings
	 */
	public MainModel(final Configuration config) {
		this.config = config;

		this.queryDocument = new PlainDocument();
		this.datasetDocument = new PlainDocument();
		this.connectionListeners = new ArrayList<>();
		this.editorStateListeners = new ArrayList<>();
		this.currentConnection = null;
		this.modified = false;
		this.file = null;

		this.queryDocument.addDocumentListener(new DocumentModifiedListener(this::setModified));
	}

	/**
	 * Set modified state for model.
	 *
	 * @param document
	 *            changed document
	 */
	private void setModified(final Document document) {
		if (document == queryDocument) {
			this.modified = true;

			fireEditorStateChanged();
		}
	}

	/**
	 * Returns true if query document in this model was modified, otherwise
	 * returns false.
	 *
	 * @return true if model modified
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Returns currently opened file. Can return {@code null} if file is new.
	 *
	 * @return opened file name
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns true if this model contains query loaded from file or already
	 * saved query.
	 *
	 * @return true if query has associated file
	 */
	public boolean hasFile() {
		return file != null;
	}

	/**
	 * Returns {@link Document} associated with query in current model.
	 *
	 * @return query document
	 */
	public Document getQueryDocument() {
		return queryDocument;
	}

	/**
	 * Returns {@link Document} associated with data set field in current model.
	 *
	 * @return data set document
	 */
	public Document getDatasetDocument() {
		return datasetDocument;
	}

	/**
	 * Set connection settings and fire connection changed event to all
	 * listeners.
	 *
	 * @param currentConnection
	 *            current connection
	 */
	public void setCurrentConnection(final ConnectionSettings currentConnection) {
		this.currentConnection = currentConnection;

		fireConnectionChanged();
	}

	/**
	 * Returns current connection setting. If connection settings not selected
	 * returns {@code null}.
	 *
	 * @return current connection settings
	 */
	public ConnectionSettings getCurrentConnection() {
		return currentConnection;
	}

	/**
	 * Adds new connection changed listener. Listener will be called when
	 * connection will be changed.
	 *
	 * @param listener
	 *            listener
	 */
	public void addConnectionListener(final ConnectionListener listener) {
		this.connectionListeners.add(listener);
	}

	/**
	 * Removes given connection changed listener from model.
	 *
	 * @param listener
	 *            listener
	 */
	public void removeConnectionListener(final ConnectionListener listener) {
		this.connectionListeners.remove(listener);
	}

	/**
	 * Fire connection changed event to all connection listeners.
	 */
	private void fireConnectionChanged() {
		for (ConnectionListener listener : this.connectionListeners) {
			listener.connectionChanged(this, this.currentConnection);
		}
	}

	/**
	 * Add another editor state listener. Listener will be called on next event.
	 *
	 * @param listener
	 *            listener
	 */
	public void addEditorStateListener(final EditorStateListener listener) {
		this.editorStateListeners.add(listener);
	}

	/**
	 * Remove listener from internal listener list.
	 *
	 * @param listener
	 *            listener
	 */
	public void removeEditorStateListener(final EditorStateListener listener) {
		this.editorStateListeners.remove(listener);
	}

	/**
	 * Fire editor state changed event to all listeners.
	 */
	private void fireEditorStateChanged() {
		for (EditorStateListener listener : this.editorStateListeners) {
			listener.editorStateChanged(this, modified, file);
		}
	}

	/**
	 * Reset query area content and current file. Fires editor changed event to
	 * all listeners.
	 *
	 * @throws BadLocationException
	 *             if error occurred
	 */
	public void setQueryStateNew() throws BadLocationException {
		int length = queryDocument.getLength();

		if (length > 0) {
			queryDocument.remove(0, length);
		}

		modified = false;
		file = null;

		fireEditorStateChanged();
	}

	/**
	 * Set current file name from query. Fires editor changed event to all
	 * listeners.
	 *
	 * @param savedFile
	 *            save file
	 */
	public void setQueryStateSaved(final File savedFile) {
		modified = false;
		file = savedFile;

		fireEditorStateChanged();
	}

	/**
	 * Set current file name and query editor content. Fires editor changed
	 * event to all listeners.
	 *
	 * @param loadedFile
	 *            loaded file
	 * @param text
	 *            query text
	 * @throws BadLocationException
	 *             if error occurred
	 */
	public void setQueryStateLoaded(final File loadedFile, final String text) throws BadLocationException {
		AttributeSet attributes = SimpleAttributeSet.EMPTY;
		int length = queryDocument.getLength();

		if (length > 0) {
			queryDocument.remove(0, length);
		}

		queryDocument.insertString(0, text, attributes);
		modified = false;
		file = loadedFile;

		fireEditorStateChanged();
	}

}
