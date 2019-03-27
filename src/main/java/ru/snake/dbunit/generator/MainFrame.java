package ru.snake.dbunit.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;

import ru.snake.dbunit.generator.action.CloseFrameAction;
import ru.snake.dbunit.generator.action.ExecuteQueryAction;
import ru.snake.dbunit.generator.action.NewFileAction;
import ru.snake.dbunit.generator.action.OpenFileAction;
import ru.snake.dbunit.generator.action.SaveAsFileAction;
import ru.snake.dbunit.generator.action.SaveFileAction;
import ru.snake.dbunit.generator.action.SelectConnectionAction;
import ru.snake.dbunit.generator.config.Configuration;
import ru.snake.dbunit.generator.listener.TextEditorMouseListener;
import ru.snake.dbunit.generator.model.MainModel;

/**
 * MAin application frame.
 *
 * @author snake
 *
 */
public final class MainFrame extends JFrame {

	private static final long serialVersionUID = 1006049148286250839L;

	private static final int PREFERRED_WIDTH = 800;

	private static final int PREFERRED_HEIGHT = 600;

	private static final int BORDER_PADDING = 8;

	private static final int DEFAULT_DIVIDER_LOCATION = 350;

	private final Configuration config;

	private final MainModel model;

	private Action newFileAction;

	private Action openFileAction;

	private Action saveFileAction;

	private Action saveAsFileAction;

	private Action selectConnectionAction;

	private Action executeQueryAction;

	private Action closeFrameAction;

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
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Query files", "sql", "txt");
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);

		newFileAction = new NewFileAction(this, model);
		openFileAction = new OpenFileAction(this, model, chooser);
		saveFileAction = new SaveFileAction(this, model, chooser);
		saveAsFileAction = new SaveAsFileAction(this, model, chooser);
		selectConnectionAction = new SelectConnectionAction(this, this.config);
		executeQueryAction = new ExecuteQueryAction(this, this.config);
		closeFrameAction = new CloseFrameAction(this);
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
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
	}

	/**
	 * Creates new menu bar for from frame.
	 *
	 * @return menu bar
	 */
	private JMenuBar createMenuBar() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		fileMenu.add(newFileAction);
		fileMenu.add(openFileAction);
		fileMenu.add(saveFileAction);
		fileMenu.add(saveAsFileAction);
		fileMenu.addSeparator();
		fileMenu.add(closeFrameAction);

		JMenu connectionMenu = new JMenu("Connection");
		connectionMenu.setMnemonic('C');
		connectionMenu.add(selectConnectionAction);
		connectionMenu.add(executeQueryAction);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(connectionMenu);

		return menuBar;
	}

	/**
	 * Creates application tool bar.
	 *
	 * @return initialized tool bar
	 */
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
		toolBar.setFloatable(false);
		toolBar.add(newFileAction);
		toolBar.add(openFileAction);
		toolBar.add(saveFileAction);
		toolBar.addSeparator();
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
		MouseListener popupListener = new TextEditorMouseListener();
		Document queryDocument = this.model.getQueryDocument();
		JTextArea queryText = new JTextArea(queryDocument);
		queryText.addMouseListener(popupListener);
		queryText.setFont(font);

		JScrollPane queryScroll = new JScrollPane(
			queryText,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);

		Document datasetDocument = this.model.getDatasetDocument();
		JTextArea datasetText = new JTextArea(datasetDocument);
		datasetText.addMouseListener(popupListener);
		datasetText.setBackground(UIManager.getColor("control"));
		datasetText.setFont(font);
		datasetText.setEditable(false);

		JScrollPane datasetScroll = new JScrollPane(
			datasetText,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryScroll, datasetScroll);
		splitPane
			.setBorder(BorderFactory.createEmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
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
