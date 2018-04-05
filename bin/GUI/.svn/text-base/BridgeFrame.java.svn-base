package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Transformer;

import analysis.BridgeFinder;
import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class BridgeFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	NetworkGraph netGrph;
	BridgeFrame(NetworkGraph netGraph)
	{
		this.netGrph = netGraph;
		
		this.setTitle("Bridges (Bridges are shown in blue color)");
		this.setSize(800, 650);
		this.setBounds(100, 100, 800, 650);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setEnabled(true);	
	}
	
	public void showResults()
	{
		UndirectedSparseGraph<GraphNode, GraphEdge> graph = createGraph();
		
		CircleLayout<GraphNode, GraphEdge> layout = new CircleLayout<GraphNode, GraphEdge>(graph);
		VisualizationViewer<GraphNode, GraphEdge> vv;
		layout.setSize(new Dimension(700, 600));

		
		vv= new VisualizationViewer<GraphNode, GraphEdge>(layout);

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<GraphNode>());
		Transformer<GraphNode, Paint> vertexPaint = new Transformer<GraphNode, Paint>() {
			@Override
			public Paint transform(GraphNode gn) {
				return Color.red;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		
		Transformer<GraphEdge, Paint> edgePaint = new Transformer<GraphEdge, Paint>() {
			@Override
			public Paint transform(GraphEdge ge) {
				if(ge.getWeight() == 0)
					return Color.black;
				else
					return Color.blue;
			}
		};
		vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);

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

	private UndirectedSparseGraph<GraphNode, GraphEdge> createGraph() {
		ArrayList<Pair<GraphNode>> bridges = new BridgeFinder().findBridges(netGrph.getGraph());
		HashMap<Integer, GraphNode> nodeMap = new HashMap<Integer, GraphNode>();
		
		UndirectedSparseGraph<GraphNode, GraphEdge> graph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		GraphNode temp;
		GraphEdge tempEdge;
		
		for(GraphNode gn:netGrph.getGraph().getVertices())
		{
			temp = new GraphNode(gn);
			nodeMap.put(gn.getID(), temp);
			graph.addVertex(temp);
		}
		
		for(GraphEdge ge: netGrph.getGraph().getEdges())
		{
			tempEdge = new GraphEdge(0);
			graph.addEdge(tempEdge, nodeMap.get(netGrph.getGraph().getEndpoints(ge).getFirst().getID()), nodeMap.get(netGrph.getGraph().getEndpoints(ge).getSecond().getID()), EdgeType.UNDIRECTED);
		}
		
		for(Pair<GraphNode> pgn: bridges)
		{
			if(graph.isNeighbor(nodeMap.get(pgn.getFirst().getID()), nodeMap.get(pgn.getSecond().getID())))
			{
				graph.removeEdge(graph.findEdge(nodeMap.get(pgn.getFirst().getID()), nodeMap.get(pgn.getSecond().getID())));
				graph.addEdge(new GraphEdge(1), nodeMap.get(pgn.getFirst().getID()), nodeMap.get(pgn.getSecond().getID()), EdgeType.UNDIRECTED);
			}
		}
		
		return graph;
	}
}

