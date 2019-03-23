package ru.snake.dbunit.generator.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import ru.snake.dbunit.generator.config.Configuration;

public class MainModel {

	private final Configuration config;

	private final Document queryDocument;

	private final Document datasetDocument;

	private final List<ConnectionListener> connectionListeners;

	private ConnectionSettings currentConnection;

	/**
	 * Creates empty model instance with given configuration settings.
	 *
	 * @param config
	 *            configuration settings
	 */
	public MainModel(Configuration config) {
		this.config = config;

		this.queryDocument = new PlainDocument();
		this.datasetDocument = new PlainDocument();
		this.connectionListeners = new ArrayList<>();
		this.currentConnection = null;
	}

	public Configuration getConfig() {
		return config;
	}

	public Document getQueryDocument() {
		return queryDocument;
	}

	public Document getDatasetDocument() {
		return datasetDocument;
	}

	public void setCurrentConnection(ConnectionSettings currentConnection) {
		this.currentConnection = currentConnection;

		fireConnectionChanged();
	}

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
	public void fireConnectionChanged() {
		for (ConnectionListener listener : this.connectionListeners) {
			listener.connectionChanged(this, this.currentConnection);
		}
	}

}
