package GUI;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import data.NetworkGraph;

public class ShowMatrixFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ShowMatrixFrame()
	{
		this.setTitle("Network matrix");
		this.setSize(700, 650);
		this.setBounds(100, 100, 700, 650);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setEnabled(true);	
	}
	
	public void showMatrix(NetworkGraph netGrph)
	{
		String[] headers = new String[netGrph.getNetwork().getColumn()+1];
		headers[0] = "";
		for(int i = 0; i < netGrph.getNetwork().getColumn(); i++)
			headers[i+1] = netGrph.findNode(i+1).getLabel();
		
		String[][] data = new String[netGrph.getNetwork().getRow()][netGrph.getNetwork().getColumn()+1];
		for(int i = 0; i < netGrph.getNetwork().getRow(); i++)
		{
			if(netGrph.getNetwork().getMode() == 2)
				data[i][0] = netGrph.findNode(i+netGrph.getNetwork().getColumn()+1).getLabel();
			else
				data[i][0] = netGrph.findNode(i+1).getLabel();
			for(int j = 0; j < netGrph.getNetwork().getColumn(); j++)
			{
				data[i][j+1] = netGrph.getNetwork().getQuick(i, j) + "";
			}
		}
		
		JTable matrixTable = new JTable(data, headers);
//		matrixTable.setLayout(null);
		matrixTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		int width = 55;
		for(int i = 0; i < data[0].length; i++)
		{
			TableColumn col = matrixTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(width);
			col.setMinWidth(width);
			col.setMaxWidth(width);
		}
		JScrollPane scrollPane = new JScrollPane(matrixTable);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		matrixTable.setEnabled(false);
		scrollPane.setBounds(5, 5, 500, 500);
		this.add(scrollPane);
		scrollPane.getViewport().setPreferredSize(new Dimension(500, 300));
	}
}
