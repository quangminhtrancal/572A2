package GUI;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import data.GraphEdge;
import data.GraphNode;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * 
 * @author Mohammadreza
 * @Date 2011/12/17 This class is responsible for showing the minimum spanning
 *       tree.
 */
public class MinimumSpanningTreeWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * This is the private constructor. It's the basic constructor. other
	 * constructors use this.
	 */
	private MinimumSpanningTreeWindow() {
		this.setTitle("Minimum Spanning Tree");
		this.setSize(800, 650);
		this.setBounds(100, 100, 800, 650);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setEnabled(true);
	}

	/**
	 * Constructs the frame and shows the results if the minimum spanning tree
	 * is an undirected graph.
	 */
	public MinimumSpanningTreeWindow(
			UndirectedGraph<GraphNode, GraphEdge> minSpanningTree) {
		this();
		this.showResults(minSpanningTree);
	}

	/**
	 * Constructs the frame and shows the results if the minimum spanning tree
	 * is a forest.
	 */
	public MinimumSpanningTreeWindow(Forest<GraphNode, GraphEdge> forest) {
		this();
		showResults(forest);
	}

	/**
	 * Method is responsible for showing the Minimum Spanning Tree in case it's a forest.
	 * It creates a TreeLayout based on the forest and passes it to the drawLayout method.
	 */
	private void showResults(Forest<GraphNode, GraphEdge> forest) {
		TreeLayout<GraphNode, GraphEdge> layout = new TreeLayout<GraphNode, GraphEdge>(
				forest);
		drawLayout(layout);
	}
	
	/**
	 * this method is responsible for drawing the layout on the frame.
	 * @param layout
	 * the layout to be drawn.
	 */
	private void drawLayout(Layout<GraphNode, GraphEdge> layout) {
		VisualizationViewer<GraphNode, GraphEdge> vv;
		vv = new VisualizationViewer<GraphNode, GraphEdge>(layout);
		vv.getRenderContext().setVertexLabelTransformer(
				new ToStringLabeller<GraphNode>());
		vv.getRenderContext().setEdgeLabelTransformer(
				new ToStringLabeller<GraphEdge>() {
					public String transform(GraphEdge edge) {
						return edge.getLabel(3);
					}
				});
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

	/**
	 * Method is responsible for showing the Minimum Spanning Tree in case it's an undirected graph.
	 * It creates an ISOMLayout based on the graph and passes it to the drawLayout method.
	 */
	public void showResults(UndirectedGraph<GraphNode, GraphEdge> graph) {
		ISOMLayout<GraphNode, GraphEdge> layout = new ISOMLayout<GraphNode, GraphEdge>(
				graph);
		layout.setSize(new Dimension(700, 600));
		drawLayout(layout);
	}

}
