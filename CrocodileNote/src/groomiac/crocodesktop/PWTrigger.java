package groomiac.crocodesktop;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class PWTrigger {
	final static Image logo32 = new ImageIcon(PWTrigger.class.getResource("CrocodileNote-32.png")).getImage();
	final static Image logo64 = new ImageIcon(PWTrigger.class.getResource("CrocodileNote-64.png")).getImage();
	
	final static ArrayList<Image> icos = new ArrayList<>();
	static{
		icos.add(logo32);
		icos.add(logo64);
	}

	public static void main(final StringResult sr) {
		if(Base.loaded()){
			sr.receive(null);
			return;
		}
		
		final JFrame jf = new JFrame(Base.appname + ": Password");
		
    	jf.setIconImages(icos);
		jf.getContentPane().setLayout(new FlowLayout()); 
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JTextField tf = new JPasswordField();
		tf.setPreferredSize(new Dimension(200, 25));
		JButton b = new JButton("OK");
		
		ActionListener act = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				jf.dispose();
				sr.receive(tf.getText());
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
