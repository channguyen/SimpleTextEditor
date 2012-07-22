import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;


public class VimPlugin {
	private static final String VIM_MODE_EDIT       = "mode-edit";
	private static final String VIM_MODE_NAVIGATE   = "mode-navigate";
	private static final String VIM_NAVIGATE_RIGHT  = "navigate-right";
	private static final String VIM_NAVIGATE_LEFT   = "navigate-left";
	
	private JTextPane vimTextPane;
	
	public VimPlugin(JTextPane tp) {
		vimTextPane = tp;
	}
	
	public void resetKeyBinding() {
		vimTextPane.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0));
		vimTextPane.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
		vimTextPane.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0));
		vimTextPane.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0));
	}
	
	public void setKeyBinding() {
		InputMap im = vimTextPane.getInputMap();
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0), VIM_MODE_EDIT);	
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), VIM_MODE_NAVIGATE);	
		
		ActionMap actions = vimTextPane.getActionMap();
		actions.put(VIM_MODE_EDIT, new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				vimTextPane.setEditable(true);
			}
		});
		
		actions.put(VIM_MODE_NAVIGATE, new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				vimTextPane.setEditable(false);
			}
		});
		
		vimTextPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				if (!vimTextPane.isEditable()) {
					InputMap im = vimTextPane.getInputMap();
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), DefaultEditorKit.forwardAction);	
					im.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), DefaultEditorKit.backwardAction);	
				}
			}
		});
	}
	
}
