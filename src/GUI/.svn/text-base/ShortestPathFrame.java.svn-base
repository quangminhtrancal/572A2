package GUI;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Transformer;

import analysis.ShortestPathFinder;
import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class ShortestPathFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MainFrame parentFrame;
	String sourceLbl, destLbl;
	JPanel jp;
	
	ShortestPathFrame(MainFrame mf)
	{
		this.parentFrame = mf;
		
		this.setTitle("Find shortest path");
		this.setAlwaysOnTop(true);
		this.setBounds(100, 100, 300, 200);
		this.setVisible(true);	
		this.setSize(300, 200);
		
		this.chooseNodes();
	}
	
	private void chooseNodes()
	{
		jp = new JPanel();
		this.add(jp);
		
		NetworkGraph netGraph = parentFrame.netPanel.netGraph;
		jp.setLayout(null);
		
//		System.out.println(netGraph.getNetwork().getRow() +" x " + netGraph.getNetwork().getColumn() + "     "+netGraph.getNetwork().getMode());
		if(netGraph.getNetwork().getRow() != netGraph.getNetwork().getColumn() || netGraph.getNetwork().getMode() == 2)
		{
			JLabel label = new JLabel("Sorry! You must load a one-mode network.");
			label.setBounds(20, 20, 300, 100);
			jp.add(label);
		}
		else
		{
			Choice source = new Choice();
			Choice dest = new Choice();
			
			for (GraphNode gn : netGraph.getGraph().getVertices())
			{
				source.add(gn.getLabel());
				dest.add(gn.getLabel());
			}
			
			sourceLbl = source.getItem(0);
			destLbl = dest.getItem(0);
			source.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					ShortestPathFrame.this.sourceLbl = (String)e.getItem();
				}
			});
			
			dest.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					ShortestPathFrame.this.destLbl = (String)e.getItem();
				}
			});
			
			source.setBounds(30, 60, 100, 40);
			jp.add(source);
			dest.setBounds(160, 60, 100, 40);
			jp.add(dest);
			
			JLabel sourceLbl = new JLabel("From:");
			JLabel destLbl = new JLabel("To:");
			sourceLbl.setBounds(30, 30, 50, 40);
			destLbl.setBounds(160, 30, 50, 40);
			jp.add(sourceLbl);
			jp.add(destLbl);
			
			Button btn = new Button("Find path");
			btn.setBounds(185, 100, 70, 25);
			jp.add(btn);
			
			btn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ShortestPathFrame.this.visualizePath();
				}
			});
		}
	}
	
	private void visualizePath()
	{
		this.setTitle("Path from "+sourceLbl + " to " + destLbl);
		this.setSize(800, 600);
		
		GraphNode source = parentFrame.netPanel.netGraph.findNode(sourceLbl);
		
		
		int[] path = new ShortestPathFinder().getShortestPath(parentFrame.netPanel.netGraph.getNetwork(), source.getID(), parentFrame.netPanel.netGraph.findNode(destLbl).getID());

		if(path == null)
		{
			JOptionPane.showMessageDialog(this,
				    "The nodes are not connected.",
				    "No path found",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		DirectedSparseGraph<GraphNode, GraphEdge> result = new DirectedSparseGraph<GraphNode, GraphEdge>();
		
		result.addVertex(new GraphNode(source));
		for(int i = 0; i < path.length; i++)
			result.addVertex(new GraphNode(parentFrame.netPanel.netGraph.findNode(path[i]+1)));
		
		GraphNode current = findNode(source.getLabel(), result);
		for(int i = 0; i < path.length; i++)
		{
			result.addEdge(new GraphEdge(parentFrame.netPanel.netGraph.getNetwork().getQuick(current.getID(),path[i])), current, findNode(path[i], result), EdgeType.DIRECTED);
			current = findNode(path[i], result);
		}
		drawPath(result);
	}	
	private GraphNode findNode(String label, DirectedSparseGraph<GraphNode, GraphEdge> g)
	{
		for(GraphNode gn:g.getVertices())
			if(gn.getLabel().equals(label))
				return gn;
		return null;
	}
	
	public GraphNode findNode(int i, DirectedSparseGraph<GraphNode, GraphEdge> graph) {
		for(GraphNode gn:graph.getVertices())
			if (gn.getID() == i)
				return gn;
		return null;
	}
	
	private void drawPath(DirectedSparseGraph<GraphNode, GraphEdge> graph)
	{
//		this.remove(this.getComponents()[0]);
		this.remove(jp);
		
		CircleLayout<GraphNode, GraphEdge> layout = new CircleLayout<GraphNode, GraphEdge>(graph);
		VisualizationViewer<GraphNode, GraphEdge> vv;
		// Sets the initial size of the space
		layout.setSize(new Dimension(400, 400));

		
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
}

class MyItemListener implements ItemListener
{
	Choice x;
	
	MyItemListener(Choice c)
	{
		this.x = c;
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		x.remove((String)e.getItem());
	}
	
}
