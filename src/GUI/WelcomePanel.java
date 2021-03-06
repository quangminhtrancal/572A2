package GUI;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class WelcomePanel extends JPanel{
	/**
	 * Main Panel for the MainFrame. It has just the image.
	 */
	private static final long serialVersionUID = 1L;

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image logo = Toolkit.getDefaultToolkit().getImage("NetDriller.jpg");
		g.drawImage (logo, 0, 0, getHeight(), getHeight(), this);
	}

}
