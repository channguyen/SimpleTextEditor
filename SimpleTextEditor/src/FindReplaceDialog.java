import java.awt.Color;
import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class FindReplaceDialog extends JFrame {
	private JTextField findTextField;
	private JTextField replaceTextField;
	private JLabel findLabel;
	private JLabel replaceLabel;
	private JButton okButton;
	private JButton cancelButton;
	private TextEditor textEditor;

	public FindReplaceDialog(TextEditor te) {
		textEditor = te;
		findLabel = new JLabel("Find");
		replaceLabel = new JLabel("Replace");
		findTextField = new JTextField();
		replaceTextField = new JTextField();
		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FindReplaceDialog.this.setVisible(false);
			}
		});

		findTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent arg0) {
				removeHighlights(textEditor.getTopTextPane());
			}

			public void insertUpdate(DocumentEvent arg0) {
				addHightlights(findTextField, textEditor.getTopTextPane());
			}

			public void changedUpdate(DocumentEvent arg0) {
				
			}
		});

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String find = findTextField.getText();
				String replacement = replaceTextField.getText();
				String currentText = textEditor.getTopTextPane().getText();

				if (!currentText.contains(find)) {
					JOptionPane.showMessageDialog(FindReplaceDialog.this, "\"" + find + "\"" + "not found");
				}
				else {
					currentText = currentText.replace(find, replacement);
				}
				
				textEditor.getTopTextPane().setText(currentText);
			}
		});

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		panel.add(findLabel);
		panel.add(findTextField);
		panel.add(replaceLabel);
		panel.add(replaceTextField);
		panel.add(okButton);
		panel.add(cancelButton);

		setContentPane(panel);
		setSize(300, 100);
		setResizable(false);
		setVisible(true);
	}

	public void removeHighlights(JTextPane tp) {
		Highlighter hilite = tp.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (int i = 0; i < hilites.length; i++) {
			if (hilites[i].getPainter() instanceof DefaultHighlightPainter) {
				hilite.removeHighlight(hilites[i]);
			}
		}
	}
	
	public void addHightlights(JTextField findTextField, JTextPane tp) {
		try {
			Highlighter hl = tp.getHighlighter();
			DefaultHighlightPainter painter = new DefaultHighlightPainter(Color.YELLOW);
			String find = findTextField.getText();
			String currentText = tp.getText();

			int pos = 0;
			while ((pos = currentText.indexOf(find, pos)) >= 0) {
				hl.addHighlight(pos, pos + find.length(), painter);
				pos += find.length();
			}
		}
		catch (BadLocationException e1) {
			System.err.println("(error) hightlight wrong position");
		}
	}
}
