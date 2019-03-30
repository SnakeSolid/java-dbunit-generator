package ru.snake.dbunit.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

import ru.snake.dbunit.generator.action.CloseFrameAction;
import ru.snake.dbunit.generator.action.ExecuteQueryAction;
import ru.snake.dbunit.generator.action.ExportFileAction;
import ru.snake.dbunit.generator.action.NewFileAction;
import ru.snake.dbunit.generator.action.OpenFileAction;
import ru.snake.dbunit.generator.action.RedoAction;
import ru.snake.dbunit.generator.action.SaveAsFileAction;
import ru.snake.dbunit.generator.action.SaveFileAction;
import ru.snake.dbunit.generator.action.SelectConnectionAction;
import ru.snake.dbunit.generator.action.UndoAction;
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

	private static final float CARET_ASPECT_RATIO = 0.1f;

	private static final int DEFAULT_DIVIDER_LOCATION = 350;

	private final Configuration config;

	private final MainModel model;

	private Action newFileAction;

	private Action openFileAction;

	private Action saveFileAction;

	private Action saveAsFileAction;

	private Action exportFileAction;

	private Action selectConnectionAction;

	private Action executeQueryAction;

	private Action closeFrameAction;

	private JTextComponent queryText;

	private JTextComponent datasetText;

	private JSplitPane splitPane;

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
	}

	/**
	 * Creates and initialize all actions.
	 */
	private void createActions() {
		JFileChooser queryChooser = createQueryChooser();
		JFileChooser datasetChooser = createDatasetChooser();

		newFileAction = new NewFileAction(this, model, queryChooser);
		openFileAction = new OpenFileAction(this, model, queryChooser);
		saveFileAction = new SaveFileAction(this, model, queryChooser);
		saveAsFileAction = new SaveAsFileAction(this, model, queryChooser);
		exportFileAction = new ExportFileAction(this, model, datasetChooser);
		selectConnectionAction = new SelectConnectionAction(this, this.config);
		executeQueryAction = new ExecuteQueryAction(this, this.config);
		closeFrameAction = new CloseFrameAction(this, model, queryChooser);
	}

	/**
	 * Creates new {@link JFileChooser} for data set files.
	 *
	 * @return file chooser
	 */
	private JFileChooser createDatasetChooser() {
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("DBUnit dataset files (*.xml)", "xml");
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);

		return chooser;
	}

	/**
	 * Creates new {@link JFileChooser} for query files.
	 *
	 * @return file chooser
	 */
	private JFileChooser createQueryChooser() {
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Query files (*.sql; *.txt)", "sql", "txt");
		chooser.addChoosableFileFilter(filter);
		chooser.setFileFilter(filter);

		return chooser;
	}

	/**
	 * Create all required components on frame.
	 */
	private void createComponents() {
		JMenuBar menuBar = createMenuBar();
		JToolBar toolBar = createToolBar();
		JComponent editors = createEditors();

		setJMenuBar(menuBar);
		add(toolBar, BorderLayout.PAGE_START);
		add(editors, BorderLayout.CENTER);
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));

		pack();

		queryText.requestFocusInWindow();
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
		fileMenu.add(exportFileAction);
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
		toolBar.addSeparator();
		toolBar.add(exportFileAction);

		return toolBar;
	}

	/**
	 * Creates query editor and result area.
	 *
	 * @return query editor and result area
	 */
	private JComponent createEditors() {
		Font font = getConfigFont();
		StyledDocument queryDocument = this.model.getQueryDocument();
		queryText = new JTextPane(queryDocument);
		queryText.putClientProperty("caretAspectRatio", CARET_ASPECT_RATIO);
		queryText.setFont(font);

		JScrollPane queryScroll = new JScrollPane(
			queryText,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);

		Document datasetDocument = this.model.getDatasetDocument();
		datasetText = new JTextArea(datasetDocument);
		datasetText.setBackground(UIManager.getColor("control"));
		datasetText.setFont(font);
		datasetText.setEditable(false);

		JScrollPane datasetScroll = new JScrollPane(
			datasetText,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, queryScroll, datasetScroll);
		splitPane.setDividerLocation(DEFAULT_DIVIDER_LOCATION);

		initUndoManager(queryText);
		initFullPopupMenu(queryText);
		initShortPopupMenu(datasetText);

		return splitPane;
	}

	/**
	 * Creates and set short context menu (only copy and select all actions) for
	 * given component.
	 *
	 * @param textComponent
	 *            text component
	 */
	private void initShortPopupMenu(final JTextComponent textComponent) {
		ActionMap actionMap = textComponent.getActionMap();
		Action copy = actionMap.get(DefaultEditorKit.copyAction);
		Action selectAll = actionMap.get(DefaultEditorKit.selectAllAction);

		copy.putValue(Action.NAME, "Copy");
		selectAll.putValue(Action.NAME, "Select all");

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(copy);
		popupMenu.add(selectAll);

		MouseListener popupListener = new TextEditorMouseListener(popupMenu);
		textComponent.addMouseListener(popupListener);
	}

	/**
	 * Creates and set full context menu (cut, copy, paste and select all
	 * actions) for given component.
	 *
	 * @param textComponent
	 *            text component
	 */
	private void initFullPopupMenu(final JTextComponent textComponent) {
		ActionMap actionMap = textComponent.getActionMap();
		Action cut = actionMap.get(DefaultEditorKit.cutAction);
		Action copy = actionMap.get(DefaultEditorKit.copyAction);
		Action paste = actionMap.get(DefaultEditorKit.pasteAction);
		Action selectAll = actionMap.get(DefaultEditorKit.selectAllAction);

		cut.putValue(Action.NAME, "Cut");
		copy.putValue(Action.NAME, "Copy");
		paste.putValue(Action.NAME, "Paste");
		selectAll.putValue(Action.NAME, "Select all");

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(cut);
		popupMenu.add(copy);
		popupMenu.add(paste);
		popupMenu.addSeparator();
		popupMenu.add(selectAll);

		MouseListener popupListener = new TextEditorMouseListener(popupMenu);
		textComponent.addMouseListener(popupListener);
	}

	/**
	 * Creates undo and redo actions for given text component.
	 *
	 * @param textComponent
	 *            text component
	 */
	private void initUndoManager(final JTextComponent textComponent) {
		UndoManager undoManager = new UndoManager();
		TextUndoManager textManager = new TextUndoManager(undoManager);
		Document document = textComponent.getDocument();
		Action undoAction = new UndoAction(this, document, textManager);
		Action redoAction = new RedoAction(this, document, textManager);
		document.addUndoableEditListener(textManager);

		InputMap inputMap = textComponent.getInputMap();
		inputMap.put(KeyStroke.getKeyStroke("control Z"), "Undo");
		inputMap.put(KeyStroke.getKeyStroke("control Y"), "Redo");

		ActionMap actionMap = textComponent.getActionMap();
		actionMap.put("Undo", undoAction);
		actionMap.put("Redo", redoAction);
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
