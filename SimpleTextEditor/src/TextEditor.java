import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class TextEditor extends JPanel {
	private static final String APP_NAME = "LaTeX Text Editor";

	private JFrame frame;
	private JTextPane topTextPane;
	private JTextPane bottomTextPane;

	private JMenuBar menubar;
	private JToolBar toolbar;
	private JTabbedPane tabpane;

	private JFileChooser fc;

	private UndoManager undoManager;
	private UndoAction undoAction;
	private RedoAction redoAction;
	private StyledDocument styledDocument;

	private VimPlugin vimPlugin;

	private String pathToLatex = "C:\\Program Files (x86)\\MiKTeX 2.9\\miktex\\bin\\pdflatex.exe";
	private String pathToPdf = "C:\\Program Files (x86)\\Adobe\\Reader 9.0\\Reader\\AcroRd32.exe";
	private File currentFile = null;

	private JPanel textEditorPanel;

	public TextEditor(JFrame f) {
		frame = f;
		textEditorPanel = new JPanel();
		textEditorPanel.setLayout(new BoxLayout(textEditorPanel, BoxLayout.Y_AXIS));

		buildMenu();
		buildToolbar();
		buildTopAndBottomTextPane();
		setupStyledDocument();
		buildTabPane();

		undoManager = new UndoManager();

		// add Vim plugin to topTextPane
		vimPlugin = new VimPlugin(topTextPane);
		fc = buildFileChooser();
	}

	private JFileChooser buildFileChooser() {
		JFileChooser fc = new JFileChooser(".");
		// create a LaTeX filter
		FileFilter filter = new ExtensionFileFilter("LaTeX extensions", new String[] { "tex" });
		// set filter
		fc.setFileFilter(filter);
		return fc;
	}

	private void buildTabPane() {
		tabpane = new JTabbedPane();
		tabpane.add("filename", this);
	}

	private void setupStyledDocument() {
		// get the document from topTextPane
		styledDocument = topTextPane.getStyledDocument();
		// add an undoable edit listener to it
		styledDocument.addUndoableEditListener(new TextEditorUndoableEditListener());
	}

	private void buildTopAndBottomTextPane() {
		// add top JTextPane
		topTextPane = new JTextPane();
		topTextPane.setFont(new Font("MS Sans Serif", 1, 12));
		// create a slider and hook it up with tpTop
		JScrollPane topSlider = new JScrollPane(topTextPane);
		topSlider.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		topSlider.setPreferredSize(new Dimension(800, 500));

		// add bottom JTextPane
		bottomTextPane = new JTextPane();
		bottomTextPane.setFont(new Font("MS Sans Serif", 1, 10));

		// create a slider and hook it up with tpBottom
		JScrollPane bottomSlider = new JScrollPane(bottomTextPane);
		bottomSlider.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		bottomSlider.setPreferredSize(new Dimension(800, 100));

		add(topSlider);
		add(bottomSlider);
	}

	public void buildToolbar() {
		toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);

		/**
		 * [Toolbar] Bold
		 **/
		final JButton boldButton = new JButton(new ImageIcon("ic_bold_16.png"));
		boldButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Style style = styledDocument.addStyle("bold", null);
				StyleConstants.setBold(style, true);
				int start = topTextPane.getSelectionStart();
				int end = topTextPane.getSelectionEnd();
				styledDocument.setCharacterAttributes(start, end, style, true);
			}
		});
		toolbar.add(boldButton);
		// end

		/**
		 * [Toolbar] Italic
		 **/
		final JButton italicButton = new JButton(new ImageIcon("ic_italic_16.png"));
		italicButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Style style = styledDocument.addStyle("italic", null);
				StyleConstants.setItalic(style, true);
				int start = topTextPane.getSelectionStart();
				int end = topTextPane.getSelectionEnd();
				styledDocument.setCharacterAttributes(start, end, style, true);
			}
		});
		toolbar.add(italicButton);
		// end

		/**
		 * [Toolbar] Underline
		 **/
		final JButton underlineButton = new JButton(new ImageIcon("ic_underline_16.png"));
		toolbar.add(underlineButton);
		underlineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Style style = styledDocument.addStyle("underline", null);
				StyleConstants.setUnderline(style, true);
				int start = topTextPane.getSelectionStart();
				int end = topTextPane.getSelectionEnd();
				styledDocument.setCharacterAttributes(start, end, style, true);
			}
		});
		toolbar.add(underlineButton);
		// end

		/**
		 * [Toolbar] Font
		 **/
		final JButton fontButton = new JButton(new ImageIcon("ic_font_size_16.png"));
		fontButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFontChooser fontChooser = new JFontChooser();
				int result = fontChooser.showDialog(TextEditor.this);
				if (result == JFontChooser.OK_OPTION) {
					Font font = fontChooser.getSelectedFont();
					MutableAttributeSet attrs = topTextPane.getInputAttributes();
					StyleConstants.setFontFamily(attrs, font.getFamily());
					StyleConstants.setFontSize(attrs, font.getSize());
					StyleConstants.setItalic(attrs, (font.getStyle() & Font.ITALIC) != 0);
					StyleConstants.setBold(attrs, (font.getStyle() & Font.BOLD) != 0);
					int start = topTextPane.getSelectionStart();
					int end = topTextPane.getSelectionEnd();
					styledDocument.setCharacterAttributes(start, end, attrs, false);
				}
			}
		});
		toolbar.add(fontButton);
		// end

		/**
		 * [Toolbar] Color
		 **/
		final JButton colorButton = new JButton(new ImageIcon("ic_color_chooser_16.png"));
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Color color = JColorChooser.showDialog(TextEditor.this, "Choose a color", Color.black);
				MutableAttributeSet attrs = topTextPane.getInputAttributes();
				StyleConstants.setForeground(attrs, color);
				int start = topTextPane.getSelectionStart();
				int end = topTextPane.getSelectionEnd();
				styledDocument.setCharacterAttributes(start, end, attrs, false);
			}
		});
		toolbar.add(colorButton);

		/**
		 * [Toolbar] Vim
		 **/
		final JButton vimButton = new JButton(new ImageIcon("ic_vim_16.png"));
		vimButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				vimPlugin.setKeyBinding();
			}
		});
		toolbar.add(vimButton);

		this.add(toolbar, BorderLayout.NORTH);
	}

	public void buildMenu() {
		// create a menubar
		menubar = new JMenuBar();

		JMenuItem item = null;

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		// [File] New
		buildMenuItemNew(item, fileMenu);
		// [File] Open
		buildMenuItemOpen(item, fileMenu);
		// [File] Save
		buildMenuItemSave(item, fileMenu);
		// [File] Quit
		buildMenuItemQuit(item, fileMenu);

		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		// [Edit] Cut
		buildMenuItemCut(item, editMenu);
		// [Edit] Copy
		buildMenuItemCopy(item, editMenu);
		// [Edit] Paste
		buildMenuItemPaste(item, editMenu);
		// [Edit] Undo
		buildMenuItemUndo(editMenu);
		// [Edit] Redo
		buildMenuItemRedo(editMenu);
		// [Edit] Select All
		buildMenuItemSelectAll(item, editMenu);
		// [Edit] Find/Replace
		buildMenuItemFindReplace(item, editMenu);

		JMenu projectMenu = new JMenu("Project");
		projectMenu.setMnemonic(KeyEvent.VK_P);
		// [Project] Build
		buildMenuItemBuild(item, projectMenu);
		// [Project] Run
		buildMenuItemRun(item, projectMenu);
		// [Project] Configure
		buildMenuItemConfigure(item, projectMenu);
		
		/**
		 * --------------- - Help menu - ---------------
		 **/
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		// [Help] Content 
		buildMenuItemContent(item, helpMenu);
		// [Help] About 
		buildMenuItemAbout(item, helpMenu);
		
		// add 4 menus to menubar
		menubar.add(fileMenu);
		menubar.add(editMenu);
		menubar.add(projectMenu);
		menubar.add(helpMenu);
		frame.setJMenuBar(menubar);
	}

	private void buildMenuItemNew(JMenuItem item, JMenu menu) {
		item = new JMenuItem("New");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});
		menu.add(item);
	}

	private void buildMenuItemOpen(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Open");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int status = fc.showOpenDialog(TextEditor.this);
				if (status == JFileChooser.APPROVE_OPTION) {
					// get selected file
					File file = fc.getSelectedFile();
					// set the current file name
					currentFile = file;
					// get document of top JTextPane
					Document document = null;
					ObjectInputStream instream = null;
					try {
						instream = new ObjectInputStream(new FileInputStream(file));
					}
					catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					try {
						document = (Document) instream.readObject();
					}
					catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					}

					topTextPane.setDocument(document);
					tabpane.setTitleAt(0, currentFile.getName());
				}
			}
		});
		menu.add(item);
	}

	private void buildMenuItemSave(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Save");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// show save dialog and get status
				int status = fc.showSaveDialog(TextEditor.this);
				File file = null;
				if (status == JFileChooser.APPROVE_OPTION) {
					file = new File(fc.getSelectedFile().getAbsolutePath());
					if (file == null)
						return;

					if (file.exists()) {
						int ret = JOptionPane.showConfirmDialog(TextEditor.this, "Do you want to override existing file?");
						if (ret == JOptionPane.NO_OPTION)
							return;
					}

					// write document file to keep format
					try {
						ObjectOutputStream outstream = new ObjectOutputStream(new FileOutputStream(file));
						outstream.writeObject(topTextPane.getDocument());
					}
					catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					catch (IOException e) {
						e.printStackTrace();
					}

					// write as text file for building process
					BufferedWriter outfile;
					try {
						outfile = new BufferedWriter(new FileWriter(file.getAbsolutePath() + ".src"));
						outfile.write(topTextPane.getDocument().getText(0, topTextPane.getDocument().getLength()));
						outfile.flush();
						outfile.close();
					}
					catch (IOException e) {
						System.err.println("(save) write failed");
					}
					catch (BadLocationException e) {
						System.err.println("(save) offset of document is out of bound");
					}

					// set current file name for build
					currentFile = file;
					// set the title of tab pane
					tabpane.setTitleAt(0, currentFile.getName());
					System.out.println(currentFile.getAbsolutePath());
				}
			}
		});
		menu.add(item);
	}

	private void buildMenuItemQuit(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Quit");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
			}
		});
		menu.add(item);
	}

	private void buildMenuItemCut(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Cut");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				topTextPane.cut();
			}
		});
		menu.add(item);
	}

	private void buildMenuItemCopy(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Copy");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				topTextPane.copy();
			}
		});
		menu.add(item);
	}

	private void buildMenuItemPaste(JMenuItem item, JMenu menu) {
		// [Edit] Paste
		item = new JMenuItem("Paste");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				topTextPane.paste();
			}
		});
		menu.add(item);
	}

	private void buildMenuItemUndo(JMenu menu) {
		undoAction = new UndoAction();
		menu.add(undoAction);
	}

	private void buildMenuItemRedo(JMenu menu) {
		redoAction = new RedoAction();
		menu.add(redoAction);
	}

	private void buildMenuItemSelectAll(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Select All");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				topTextPane.selectAll();
			}
		});
		menu.add(item);
	}

	private void buildMenuItemFindReplace(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Find/Replace");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FindReplaceDialog dialog = new FindReplaceDialog(TextEditor.this);
			}
		});
		menu.add(item);
	}

	private void buildMenuItemBuild(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Build");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!currentFile.equals("")) {
					// (new BuildThread(pathToLatex, currentFile.getName() + ".src")).start();
					ProcessBuilder pb = new ProcessBuilder(pathToLatex, currentFile.getName() + ".src");
					try {
						Process p = pb.start();
					}
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					File logFile = new File(currentFile.getName() + ".log");
					System.out.println(logFile.getAbsolutePath());
					int i = 0;
					while (!logFile.exists()) {
						System.out.println(i++);
					}
					try {
						System.out.println("Did you go here?");
						StringBuilder sb = new StringBuilder();
						String line;
						System.out.println(logFile.getName());
						FileInputStream in = new FileInputStream(logFile.getName());
						BufferedReader br = new BufferedReader(new InputStreamReader(in));
						try {
							while ((line = br.readLine()) != null) {
								sb.append(line);
							}
						}
						catch (IOException e) {
							System.err.print("(build) read from file fail");
						}

						try { // add text to document
							bottomTextPane.getDocument().insertString(0, sb.toString(), bottomTextPane.getStyle("small"));
						}
						catch (BadLocationException e) {
							System.err.print("(build) insert text to document fail");
						}
					}
					catch (FileNotFoundException e) {
						System.err.print("(build) file not found");
					}
				}
				else {
					System.err.println("(build) can't build, file doesn't exists!");
				}

			}
		});
		menu.add(item);
	}

	private void buildMenuItemRun(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Run");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, KeyEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String file = currentFile + ".pdf";
				(new PdfThread(pathToPdf, file)).start();
			}
		});
		menu.add(item);
	}

	// end

	private void buildMenuItemConfigure(JMenuItem item, JMenu menu) {
		item = new JMenuItem("Configure");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, KeyEvent.CTRL_MASK));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int ret = fc.showOpenDialog(TextEditor.this);
				if (ret == JFileChooser.APPROVE_OPTION) {
					// get selected file
					File file = fc.getSelectedFile();
					pathToLatex = file.getAbsolutePath();
				}
			}
		});
		menu.add(item);
	}
	
	private void buildMenuItemContent(JMenuItem item, JMenu menu) {
			item = new JMenuItem("Content");
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, KeyEvent.CTRL_MASK));
			menu.add(item);
	}

	private void buildMenuItemAbout(JMenuItem item, JMenu menu) {
			item = new JMenuItem("About");
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK));
			menu.add(item);
	}


	private class TextEditorUndoableEditListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			undoManager.addEdit(e.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}

	private class UndoAction extends AbstractAction {
		public UndoAction() {
			super("Undo");
			setEnabled(false);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent arg0) {
			try {
				undoManager.undo();
			}
			catch (CannotUndoException e) {
				System.err.println("(error) unable to undo");
			}

			updateUndoState();
			redoAction.updateRedoState();
		}

		protected void updateUndoState() {
			if (undoManager.canUndo()) {
				setEnabled(true);
				putValue(Action.NAME, undoManager.getUndoPresentationName());
			}
			else {
				setEnabled(false);
				putValue(Action.NAME, "Undo");
			}
		}
	}

	private class RedoAction extends AbstractAction {
		public RedoAction() {
			super("Redo");
			setEnabled(false);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent arg0) {
			try {
				undoManager.redo();
			}
			catch (CannotRedoException e) {
				System.err.println("(error) unable to redo");
			}
			updateRedoState();
			undoAction.updateUndoState();
		}

		protected void updateRedoState() {
			if (undoManager.canRedo()) {
				setEnabled(true);
				putValue(Action.NAME, undoManager.getRedoPresentationName());
			}
			else {
				setEnabled(false);
				putValue(Action.NAME, "Redo");
			}
		}
	}

	public JTextPane getTopTextPane() {
		return topTextPane;
	}

	public Document getDocument() {
		return styledDocument;
	}

	private String getLogFileName(String filename) {
		return filename.substring(0, filename.indexOf('.')) + ".src.log";
	}

	public static void buildGUI() {
		// create a frame
		JFrame f = new JFrame(APP_NAME);

		// create a panel
		TextEditor panel = new TextEditor(f);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		// add panel to frame
		f.setContentPane(panel.tabpane);

		// set frame properties
		f.setSize(850, 710);
		f.setResizable(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
