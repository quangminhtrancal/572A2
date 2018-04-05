package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Transformer;

import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class FoldFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	FoldFrame()
	{
		this.setTitle("Folded network");
		this.setSize(800, 650);
		this.setBounds(100, 100, 800, 650);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setEnabled(true);	
	}
	
	public void foldNetwork(NetworkGraph netGraph, boolean foldByRow)
	{
		UndirectedSparseGraph<GraphNode, GraphEdge> graph;
		if(foldByRow)
			graph = folding_Row(netGraph);
		else
			graph = folding_column(netGraph);
		
		CircleLayout<GraphNode, GraphEdge> layout = new CircleLayout<GraphNode, GraphEdge>(graph);
		VisualizationViewer<GraphNode, GraphEdge> vv;
		layout.setSize(new Dimension(700, 600));

		
		vv= new VisualizationViewer<GraphNode, GraphEdge>(layout);

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<GraphNode>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<GraphEdge>()
				{
			public String transform(GraphEdge edge)
			{
				return edge.getLabel();
			}
				});
		Transformer<GraphNode, Paint> vertexPaint = new Transformer<GraphNode, Paint>() {
			@Override
			public Paint transform(GraphNode gn) {
				return Color.red;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);

		JScrollPane jsp = new JScrollPane(vv, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsp.add(vv);

		jsp.setViewportView(vv);
		jsp.setBounds(30, 30, 640, 600);
		jsp.setVisible(true);
		this.add(jsp);
		jsp.revalidate();
		jsp.repaint();
		super.repaint();
	}
	
	public UndirectedSparseGraph<GraphNode, GraphEdge> folding_Row(NetworkGraph netGraph) {
		int rows = netGraph.getNetwork().getRow();
		int columns = netGraph.getNetwork().getColumn();
		double[][] network = netGraph.getNetwork().getMatrix();
		double[][] matrix = new double[rows][rows];

		for(int i = 0; i < rows; i++)
			for(int j = 0; j < rows; j++)
			{
				for(int k = 0; k < columns; k++)
					matrix[i][j] += network[i][k] * network[j][k];
			}
		
		UndirectedSparseGraph<GraphNode, GraphEdge> graph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		HashMap<Integer, GraphNode> nodeMap = new HashMap<Integer, GraphNode>();
		GraphNode temp;
		for(int i = 0; i < rows; i++)
		{
			if(netGraph.getNetwork().getLabels() != null && netGraph.getNetwork().getLabels()[0] != null)
			{
				temp = new GraphNode(i+columns+1, netGraph.getNetwork().getLabels()[1][i]);
				nodeMap.put(i+columns+1, temp);
				graph.addVertex(temp);
			}
			else
			{
				temp = new GraphNode(i+columns+1, null);
				nodeMap.put(i+columns+1, temp);
				graph.addVertex(temp);
			}
		}
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < rows; j++)
				if(matrix[i][j] != 0)
					graph.addEdge(new GraphEdge(matrix[i][j]), nodeMap.get(i+columns+1), nodeMap.get(j+columns+1));
		
		return graph;
	}

	
	public UndirectedSparseGraph<GraphNode, GraphEdge> folding_column(NetworkGraph netGraph) {
		int columns = netGraph.getNetwork().getColumn();
		int rows = netGraph.getNetwork().getRow();
		double[][] network = netGraph.getNetwork().getMatrix();
		double[][] matrix = new double[columns][columns];

		for(int i = 0; i < columns; i++)
			for(int j = 0; j < columns; j++)
			{
				for(int k = 0; k < rows; k++)
					matrix[i][j] += network[k][i] * network[k][j];
			}
		
		UndirectedSparseGraph<GraphNode, GraphEdge> graph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		HashMap<Integer, GraphNode> nodeMap = new HashMap<Integer, GraphNode>();
		GraphNode temp;
		for(int i = 0; i < columns; i++)
		{
			if(netGraph.getNetwork().getLabels() != null && netGraph.getNetwork().getLabels()[0] != null)
			{
				temp = new GraphNode(i, netGraph.getNetwork().getLabels()[0][i]);
				nodeMap.put(i, temp);
				graph.addVertex(temp);
			}
			else
			{
				temp = new GraphNode(i, null);
				nodeMap.put(i, temp);
				graph.addVertex(temp);
			}
		}
		for(int i = 0; i < columns; i++)
			for(int j = 0; j < columns; j++)
				if(matrix[i][j] != 0)
					graph.addEdge(new GraphEdge(matrix[i][j]), nodeMap.get(i), nodeMap.get(j));
		
		return graph;
	}
}
