package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Transformer;

import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class ComplementGraphFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ComplementGraphFrame() {
		this.setTitle("Complement network");
		this.setSize(800, 650);
		this.setBounds(100, 100, 800, 650);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setEnabled(true);
	}

	public void findComplement(NetworkGraph netGraph) {
		Graph<GraphNode, GraphEdge> graph = this.complement(netGraph);

		CircleLayout<GraphNode, GraphEdge> layout = new CircleLayout<GraphNode, GraphEdge>(
				graph);
		VisualizationViewer<GraphNode, GraphEdge> vv;
		layout.setSize(new Dimension(700, 600));

		vv = new VisualizationViewer<GraphNode, GraphEdge>(layout);

		vv.getRenderContext().setVertexLabelTransformer(
				new ToStringLabeller<GraphNode>());
		Transformer<GraphNode, Paint> vertexPaint = new Transformer<GraphNode, Paint>() {
			@Override
			public Paint transform(GraphNode gn) {
				return Color.red;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);

		JScrollPane jsp = new JScrollPane(vv,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsp.add(vv);

		jsp.setViewportView(vv);
		jsp.setBounds(30, 30, 640, 600);
		jsp.setVisible(true);
		this.add(jsp);
		jsp.revalidate();
		jsp.repaint();
		super.repaint();
	}

	private Graph<GraphNode, GraphEdge> complement(NetworkGraph netGraph) {
		Graph<GraphNode, GraphEdge> graph = null;
		GraphNode[] nodes = new GraphNode[netGraph.getGraph().getVertexCount()];

		// creating an instance of graph
		if (netGraph.getNetwork().isDirected())
			graph = new DirectedSparseGraph<GraphNode, GraphEdge>();
		else
			graph = new UndirectedSparseGraph<GraphNode, GraphEdge>();

		// Adding nodes to the graph
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new GraphNode(netGraph.findNode(i + 1));
			graph.addVertex(nodes[i]);
		}

		// creating edges of the complement graph
		if (netGraph.getNetwork().isDirected()) {
			for(int i = 0; i < nodes.length; i++)
				for(int j = 0; j < nodes.length; j++)
					if(netGraph.getNetwork().getQuick(i,j) == 0)
						graph.addEdge(new GraphEdge(1), nodes[i], nodes[j], EdgeType.DIRECTED);
		} else{
			for(int i = 0; i < nodes.length; i++)
				for(int j = i; j < nodes.length; j++)
					if(netGraph.getNetwork().getQuick(i,j) == 0)
						graph.addEdge(new GraphEdge(1), nodes[i], nodes[j], EdgeType.UNDIRECTED);
		}
		return graph;
	}
}
