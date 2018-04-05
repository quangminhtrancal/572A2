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

import analysis.MinSpanningTreeFinder;
import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class MinimumSpanningTreeFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	MainFrame parentFrame;
	String startLbl;
	JPanel jp;
	
	MinimumSpanningTreeFrame(MainFrame mf)
	{
		this.parentFrame = mf;
		
		this.setTitle("Find Minimum Spanning Tree");
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
		} else if (netGraph.getNetwork().isDirected()){
			JLabel label = new JLabel("Sorry! You must load an undirected network.");
			label.setBounds(20, 20, 300, 100);
			jp.add(label);
		}
		else
		{
			Choice start = new Choice();
			
			for (GraphNode gn : netGraph.getGraph().getVertices())
			{
				start.add(gn.getLabel());
			}
			
			startLbl = start.getItem(0);
			start.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					// TODO Auto-generated method stub
					MinimumSpanningTreeFrame.this.startLbl = (String)e.getItem();
				}
			});
			
		
			
			start.setBounds(30, 60, 100, 40);
			jp.add(start);
			
			
			JLabel sourceLble = new JLabel("Start:");
			
			sourceLble.setBounds(30, 30, 50, 40);
			
			jp.add(sourceLble);
			
			Button btn = new Button("Find tree");
			btn.setBounds(185, 100, 70, 25);
			jp.add(btn);
			
			btn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MinimumSpanningTreeFrame.this.visualizePath();
				}
			});
		}
	}
	
	private void visualizePath()
	{
		this.setTitle("Minimum Spanning Tree originating at "+startLbl);
		this.setSize(800, 600);
		
		GraphNode source = parentFrame.netPanel.netGraph.findNode(startLbl);
		
		
		Graph<GraphNode, GraphEdge> tree= (new MinSpanningTreeFinder()).findMinSTree(parentFrame.netPanel.netGraph.getGraph(), source);

		if(tree == null)
		{
			JOptionPane.showMessageDialog(this,
				    "",
				    "minimum spanning tree cannot be found",
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		drawTree(tree);
	}	
	
	
	public GraphNode findNode(int i, DirectedSparseGraph<GraphNode, GraphEdge> graph) {
		for(GraphNode gn:graph.getVertices())
			if (gn.getID() == i)
				return gn;
		return null;
	}
	
	private void drawTree(Graph<GraphNode, GraphEdge> tree)
	{
//		this.remove(this.getComponents()[0]);
		this.remove(jp);
		
		CircleLayout<GraphNode, GraphEdge> layout = new CircleLayout<GraphNode, GraphEdge>(tree);
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



