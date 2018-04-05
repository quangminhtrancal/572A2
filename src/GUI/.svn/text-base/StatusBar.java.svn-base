package GUI;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class StatusBar extends JLabel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StatusBar(JFrame parent) {
		int height = parent.getHeight();
		int width = parent.getWidth();
		
		this.setSize(width-5, 25);
		this.setBounds(0, height-70, width-5, 25);
		
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		this.setBorder(raisedetched);
		
		this.setFont(new Font("Tahoma", Font.PLAIN, 11));
		parent.add(this);
	}
	
	public StatusBar(JPanel parent) {
		int height = parent.getHeight();
		int width = parent.getWidth();
		
		this.setSize(width-5, 25);
		this.setBounds(0, height, width-5, 25);
		
		Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		this.setBorder(raisedetched);
		
		this.setFont(new Font("Tahoma", Font.PLAIN, 11));
		parent.add(this);
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setEnabled(true);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();
		jp.setSize(310, 310);
		jp.setLayout(null);
		frame.add(jp);
		new StatusBar(jp);
	}
	
	public void setText(String text)
	{
		super.setText("    "+text);
	}
}
