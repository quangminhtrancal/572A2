package GUI;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import data.Dataset;

public class DataImportFrame extends JFrame{
	/**
	 * Under Data -> Import Raw Dataset
	 */
	private static final long serialVersionUID = 1L;
	MainFrame parentFrame;
	
	DataImportFrame(MainFrame mf)
	{
		parentFrame = mf;
		this.setTitle("Import raw dataset");
		this.setBounds(100, 100, 290, 330);
		this.setSize(290, 330);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.setEnabled(true);
	}
	
	public void init()
	{
		JPanel jp = new JPanel();
		jp.setLayout(null);
		
		JLabel headerLabel = new JLabel();
		
		final Checkbox headerCheck = new Checkbox("File contains headers.", false, null);
		headerCheck.setBounds(10, 10, 210, 50);
		headerLabel.setBounds(10, 10, 210, 40);
		headerLabel.add(headerCheck);
		
		jp.add(headerLabel);
		
		JLabel delimiterLabel = new JLabel("File delimiter: ");
		delimiterLabel.setBounds(20, 75, 130, 30);
		jp.add(delimiterLabel);
		
		final ButtonGroup bg = new ButtonGroup();
		JRadioButton jrbSemi = new JRadioButton("Semicolon ;");
		jrbSemi.setActionCommand("semi");
		bg.add(jrbSemi);
		JRadioButton jrbComma = new JRadioButton("Comma ,");
		jrbComma.setActionCommand("comma");
		bg.add(jrbComma);
		JRadioButton jrbSpace = new JRadioButton("Space ");
		jrbSpace.setSelected(true);
		jrbSpace.setActionCommand("space");
		bg.add(jrbSpace);
		
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
	    radioPanel.add(jrbSpace);
	    radioPanel.add(jrbComma);
	    radioPanel.add(jrbSemi);
	    
	    
	    radioPanel.setBounds(160, 70, 150, 150);
	    jp.add(radioPanel);
	    
	    Button importBtn = new Button("Import");
	    importBtn.setBounds(180, 240, 70, 25);
	    jp.add(importBtn);
	    
	    importBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String delimiter;
				if(bg.getSelection().getActionCommand().equals("semi"))
					delimiter = ";";
				else if(bg.getSelection().getActionCommand().equals("comma"))
					delimiter = ",";
				else
					delimiter = " ";
				DataImportFrame.this.setAlwaysOnTop(false);
				openFileChooser(delimiter, headerCheck.getState());
				DataImportFrame.this.dispose();
			}
		});
	    this.add(jp);
	}
	
	private void openFileChooser(String delimiter, boolean hasNames)
	{
		 FileDialog fd = new FileDialog(this , "Open a Network File",FileDialog.LOAD);
		 fd.setFile("*.txt");
		 fd.setEnabled(true);
		 fd.setVisible(true);
		 fd.setAlwaysOnTop(true);
		 
		 String path = fd.getDirectory() + fd.getFile();
		 if(fd.getDirectory()!= null && fd.getFile() != null)
		 {
			 Dataset d = new Dataset();
			 if (d.openFile(path, delimiter, hasNames))
			 {
				parentFrame.rawData = d;
				parentFrame.statusBar.setText("Raw dataset imported");
				parentFrame.statusBar.revalidate();
				parentFrame.statusBar.repaint();

				parentFrame.enableConstructionMenus();
				parentFrame.disableNetworkMenus();
			 }
			 else
				 JOptionPane.showMessageDialog(this,
						    "Invalid Input",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
		 }
	}
}