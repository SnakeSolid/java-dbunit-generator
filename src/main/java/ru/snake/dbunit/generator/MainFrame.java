package ru.snake.dbunit.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.text.Document;

import ru.snake.dbunit.generator.action.ExecuteQueryAction;
import ru.snake.dbunit.generator.action.SelectConnectionAction;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.model.MainModel;

/**
 * MAin application frame.
 *
 * @author snake
 *
 */
public final class MainFrame extends JFrame {

	private static final int DEFAULT_DIVIDER_LOCATION = 350;

	private final Configuration config;

	private final MainModel model;

	private Action selectConnectionAction;

	private Action executeQueryAction;

	/**
	 * Creates new frame instance with given configuration settings.
	 *
	 * @param config
	 *            configuration settings
	 * @param model
	 *            internal state model
	 */
	public MainFrame(final Configuration config, final MainModel model) {
		super("DBUnit dataset generator");

		this.config = config;
		this.model = model;

		createActions();
		createComponents();
		pack();
	}

	/**
	 * Creates and initialize all actions.
	 */
	private void createActions() {
		selectConnectionAction = new SelectConnectionAction(this, this.config);
		executeQueryAction = new ExecuteQueryAction(this, this.config);
	}

	/**
	 * Create all required components on frame.
	 */
	private void createComponents() {
		JMenuBar menuBar = createMenuBar();
		JToolBar toolBar = createToolBar();
		JComponent editors = createEditor();

		setJMenuBar(menuBar);
		add(toolBar, BorderLayout.PAGE_START);
		add(editors, BorderLayout.CENTER);
		setPreferredSize(new Dimension(800, 600));
	}

	/**
	 * Creates new menu bar for from frame.
	 *
	 * @return menu bar
	 */
	private JMenuBar createMenuBar() {
		JMenu commandMenu = new JMenu("Command");
		commandMenu.add(selectConnectionAction);
		commandMenu.add(executeQueryAction);
		commandMenu.addSeparator();
		commandMenu.add("Exit");

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(commandMenu);

		return menuBar;
	}

	/**
	 * Creates application tool bar.
	 *
	 * @return initialized tool bar
	 */
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.add(selectConnectionAction);
		toolBar.add(executeQueryAction);

		return toolBar;
	}

	/**
	 * Creates query editor and result area.
	 *
	 * @return query editor and result area
	 */
	private JComponent createEditor() {
		Font font = getConfigFont();
		Document queryDocument = this.model.getQueryDocument();
		JTextArea queryText = new JTextArea(queryDocument);
		queryText.setFont(font);

		JScrollPane queryScroll = new JScrollPane(
			queryText,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);

		Document datasetDocument = this.model.getDatasetDocument();
		JTextArea datasetText = new JTextArea(datasetDocument);
		datasetText.setFont(font);
		datasetText.setEditable(false);

		JScrollPane datasetScroll = new JScrollPane(
			datasetText,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryScroll, datasetScroll);
		splitPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		splitPane.setDividerLocation(DEFAULT_DIVIDER_LOCATION);
		splitPane.setOneTouchExpandable(true);

		return splitPane;
	}

	/**
	 * Creates font for query editor and result area.
	 *
	 * @return font
	 */
	private Font getConfigFont() {
		String fontName = this.config.getFont().getName();
		int fontStyle = this.config.getFont().getStyle().asInt();
		int fontSize = this.config.getFont().getSize();
		Font font = new Font(fontName, fontStyle, fontSize);

		return font;
	}

	/**
	 * Returns this frame model.
	 *
	 * @return model
	 */
	public MainModel getModel() {
		return model;
	}

}