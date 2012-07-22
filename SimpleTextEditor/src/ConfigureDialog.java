import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ConfigureDialog extends JFrame {
	public ConfigureDialog() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		
		JLabel lb = new JLabel("Path to LaTeX");
		JTextField tf = new JTextField();
		
		JButton btn = new JButton("Save");
		
		panel.add(lb);
		panel.add(tf);
		panel.add(btn);
		
		setContentPane(panel);
		setSize(300, 100);
		setVisible(true);
	}
}
