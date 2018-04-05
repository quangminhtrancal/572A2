package GUI;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;

public class EditMatrixFrame extends JFrame{
	NetworkGraph netGraph;
	public EditMatrixFrame(NetworkGraph netGraph)
	{
		this.netGraph = netGraph;
		this.setTitle("Network matrix");
		this.setSize(700, 650);
		this.setBounds(100, 100, 700, 650);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setEnabled(true);	
	}
	
	public void showMatrix()
	{
		String[] headers = new String[netGraph.getNetwork().getColumn()+1];
		headers[0] = "";
		for(int i = 0; i < netGraph.getNetwork().getColumn(); i++)
			headers[i+1] = netGraph.findNode(i+1).getLabel();
		
		String[][] data = new String[netGraph.getNetwork().getRow()][netGraph.getNetwork().getColumn()+1];
		int fromNode, toNode;
		if (netGraph.getNetwork().isDirected()) {
			for (GraphEdge ge : netGraph.getGraph().getEdges()) {
				fromNode = netGraph.getGraph().getEndpoints(ge).getFirst().getID();
				toNode = netGraph.getGraph().getEndpoints(ge).getSecond().getID();
				data[fromNode][toNode+1] = ""+ ge.getWeight();
			}
		}
		else{
			for (GraphEdge ge : netGraph.getGraph().getEdges()) {
				fromNode = netGraph.getGraph().getEndpoints(ge).getFirst().getID();
				toNode = netGraph.getGraph().getEndpoints(ge).getSecond().getID();
				data[fromNode][toNode+1] = ge.getWeight()+"";
				data[toNode][fromNode+1] = ge.getWeight()+"";
			}
		}
		
		for(int i = 0; i < netGraph.getNetwork().getRow(); i++)
		{
			data[i][0] = headers[i+1];
			for(int j = 0; j < netGraph.getNetwork().getColumn(); j++)
			{
				if(data[i][j] == "")
					data[i][j] = "0";
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
		scrollPane.setBounds(5, 5, 500, 500);
		this.add(scrollPane);
		scrollPane.getViewport().setPreferredSize(new Dimension(500, 300));
	}
}
