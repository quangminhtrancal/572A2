package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import org.apache.commons.collections15.Transformer;

import clustering.CPMClusterer;
import clustering.ModularityClusterer;
import clustering.WEdgeBetweennessClusterer;
import clustering.cpm.Community;
import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class HierarchicalZoomGraphFrame extends JFrame {
	
	HierarchicalZoomingPanel zoomingPanel;
	
	public HierarchicalZoomGraphFrame(NetworkGraph netGraph) {
		this.setTitle("Hierarchical Zoom");
		this.setBounds(100, 100, 1000, 710);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setEnabled(true);
		this.zoomingPanel = new HierarchicalZoomingPanel(this, netGraph);	
		
		this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	zoomingPanel.resetEvent();
		    	setVisible(false);
		    	dispose();
		    }
		});
	}
}


