package groomiac.crocodesktop;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class PWTriggerCreate {
	
	public static void main(final StringResult sr) {
		final JFrame jf = new JFrame(Base.appname + ": Create password");
		
		jf.setIconImages(PWTrigger.icos);
		jf.getContentPane().setLayout(new FlowLayout()); 
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel jl = new JLabel("Password: ");
		jf.add(jl);
		
		final JTextField tf = new JPasswordField();
		tf.setPreferredSize(new Dimension(200, 25));
		
		final JTextField tf2 = new JPasswordField();
		tf2.setPreferredSize(new Dimension(200, 25));
		
		JButton b = new JButton("OK");
		
		ActionListener act = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String s1 = tf.getText();
				String s2 = tf2.getText();
				
				if(s1 == null || s2 == null || s1.length() == 0 || s2.length() == 0)
					return;
				
				int minlen = 8;
				
				if(s1.equals(s2) && s1.length() >= minlen){
					jf.dispose();
					sr.receive(tf.getText());
				}
				else{
					tf.setText("");
					tf2.setText("");
					tf.requestFocus();
					
					if(s1.length() < minlen)
						JOptionPane.showMessageDialog(null, "The password should be at least " + minlen + " characters long!");
					else
						JOptionPane.showMessageDialog(null, "Passwords do not match!");
				}
			}
		};
		
		b.addActionListener(act);
		tf.addActionListener(act);
		tf2.addActionListener(act);
		
		jf.add(tf);
		
		jl = new JLabel("Re-type: ");
		jf.add(jl);

		jf.add(tf2);
		jf.add(b);
		
		jf.pack();
		
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		
		if(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK)){
			JOptionPane.showMessageDialog(null, "CAPS-Lock is active!");
		}
	}
}
