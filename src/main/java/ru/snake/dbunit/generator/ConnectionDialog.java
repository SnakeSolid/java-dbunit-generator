package ru.snake.dbunit.generator;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import ru.snake.dbunit.generator.action.CloseDialogAction;
import ru.snake.dbunit.generator.action.PrepareConnectionAction;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.listener.ChangeParametersTableListener;
import ru.snake.dbunit.generator.model.ConnectionParametersTableModel;
import ru.snake.dbunit.generator.model.ConnectionSettings;
import ru.snake.dbunit.generator.model.DriverListModel;

/**
 * Connection settings dialog.
 *
 * @author snake
 *
 */
public final class ConnectionDialog extends JDialog {

	private static final long serialVersionUID = -5094451147263755246L;

	private final Configuration config;

	private ConnectionSettings selectedConnection;

	/**
	 * Create new connection selection dialog.
	 *
	 * @param parent
	 *            parent frame
	 * @param config
	 *            configuration
	 */
	public ConnectionDialog(final MainFrame parent, final Configuration config) {
		super(parent, "Select connection...", true);

		this.config = config;
		this.selectedConnection = null;

		createComponents();
		pack();
	}

	/**
	 * Creates dialog components.
	 */
	private void createComponents() {
		GroupLayout layout = new GroupLayout(this.getContentPane());
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		DriverListModel driverListModel = new DriverListModel(this.config);
		ConnectionParametersTableModel parametersModel = new ConnectionParametersTableModel();
		ChangeParametersTableListener parametersListener = new ChangeParametersTableListener(
			this.config,
			driverListModel,
			parametersModel
		);

		driverListModel.addListDataListener(parametersListener);

		JLabel connectionLabel = new JLabel("Driver:");
		JComboBox<String> connectionList = new JComboBox<>(driverListModel);
		JLabel parametersLabel = new JLabel("Parameters:");
		JTable parametersTable = new JTable(parametersModel);
		parametersTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		JScrollPane parameterScroll = new JScrollPane(parametersTable);
		PrepareConnectionAction connectionAction = new PrepareConnectionAction(
			this,
			this.config,
			driverListModel,
			parametersModel,
			this::setSelectedConnection
		);
		CloseDialogAction closeAction = new CloseDialogAction(this);
		JButton saveButton = new JButton(connectionAction);
		JButton cancelButton = new JButton(closeAction);

		// @formatter:off
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addComponent(connectionLabel)
				.addComponent(connectionList)
				.addComponent(parametersLabel)
				.addComponent(parameterScroll)
				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
					.addComponent(saveButton)
					.addComponent(cancelButton))
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(connectionLabel)
				.addComponent(connectionList)
				.addComponent(parametersLabel)
				.addComponent(parameterScroll)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup()
					.addComponent(saveButton)
					.addComponent(cancelButton))
		);
		// @formatter:on

		layout.linkSize(SwingConstants.HORIZONTAL, saveButton, cancelButton);

		setLayout(layout);
	}

	/**
	 * Set selected connection. This method called from action.
	 *
	 * @param selectedConnection
	 *            selected connection
	 */
	private void setSelectedConnection(final ConnectionSettings selectedConnection) {
		this.selectedConnection = selectedConnection;
	}

	/**
	 * Returns true if connection selected, otherwise returns false.
	 *
	 * @return true if connection selected
	 */
	public boolean hasSelectedConnection() {
		return selectedConnection != null;
	}

	/**
	 * Returns selected connection or null.
	 *
	 * @return selected connection
	 */
	public ConnectionSettings getSelectedConnection() {
		return selectedConnection;
	}

}
