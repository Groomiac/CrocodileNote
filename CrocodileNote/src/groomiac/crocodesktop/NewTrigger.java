package groomiac.crocodesktop;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class NewTrigger {
	public static void main(final StringResult sr) {
		final JFrame jf = new JFrame("New Folder");
		
    	jf.setIconImages(PWTrigger.icos);
		jf.getContentPane().setLayout(new FlowLayout()); 
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		final JTextField tf = new JTextField();
		tf.setPreferredSize(new Dimension(200, 25));
		JButton b = new JButton("OK");
		
		ActionListener act = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String tmp = tf.getText();
				if(tmp == null || tmp.length() == 0) return;
				
				jf.dispose();
				sr.receive(tmp);
			}
			
		};

		b.addActionListener(act);
		tf.addActionListener(act);
		
		jf.add(tf);
		jf.add(b);
		
		jf.pack();
		
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		
	}
}
