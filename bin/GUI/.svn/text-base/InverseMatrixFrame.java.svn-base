package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Transformer;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class InverseMatrixFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InverseMatrixFrame() {
		this.setTitle("Inverse network");
		this.setSize(800, 650);
		this.setBounds(100, 100, 800, 650);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setEnabled(true);	
	}
	
	public void findInverse(NetworkGraph netGraph)
	{
		DirectedSparseGraph<GraphNode, GraphEdge> graph = this.inverse(netGraph);
		
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
	
	private DirectedSparseGraph<GraphNode, GraphEdge> inverse(NetworkGraph netGrph)
	{
		if(netGrph.getNetwork().getColumn() != netGrph.getNetwork().getRow() || netGrph.getNetwork().getMode() == 2)
			return null;
		
		double[][] original = netGrph.getNetwork().getMatrix();
		Algebra algebra = new Algebra();
		
		DirectedSparseGraph<GraphNode, GraphEdge> graph = new DirectedSparseGraph<GraphNode, GraphEdge>();
		DoubleMatrix2D matrix = algebra.inverse(new SparseDoubleMatrix2D(original));
		GraphNode[] nodes = new GraphNode[netGrph.getGraph().getVertexCount()];
		for(int i = 0; i < netGrph.getNetwork().getRow(); i++)
		{
			nodes[i] = new GraphNode(netGrph.findNode(i+1)); //I'm not sure if they have to be the same! Do the actors have anything to do with actors of the original graph!?!
			graph.addVertex(nodes[i]);
		}
		
		for(int i = 0; i < netGrph.getNetwork().getRow(); i++)
			for(int j = 0; j < netGrph.getNetwork().getRow(); j++)
				graph.addEdge(new GraphEdge(matrix.getQuick(i, j)), nodes[i], nodes[j]);
		
		return graph;
	}
}
