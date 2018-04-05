package clustering;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.collections15.Transformer;

import clustering.cpm.Community;

import data.GraphEdge;
import data.GraphNode;
import data.RgbColor;
import edu.uci.ics.jung.algorithms.cluster.VoltageClusterer;
import edu.uci.ics.jung.graph.Graph;

public class CommunityDetection {
	public void modularityCluster(Graph<GraphNode, GraphEdge> graph) {
		String userInput = JOptionPane
				.showInputDialog("Please enter the number of passes: ");
		if (userInput == null) {
			return;
		}
		int numPasses = 0;
		try {
			numPasses = Integer.valueOf(userInput);
		} catch (Exception e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame,
					"Invalid value for the number of passes.");
		}
		ModularityClusterer<GraphNode, GraphEdge> mc = new ModularityClusterer<GraphNode, GraphEdge>(
				numPasses, new Transformer<GraphEdge, Number>() {
					@Override
					public Number transform(GraphEdge e) {
						return e.getWeight();
					}
				});

		Set<Set<GraphNode>> clusters = mc.transform(graph);
		colorNodes(clusters);
	}

	public void betweennessCluster(Graph<GraphNode, GraphEdge> graph) {
		String userInput = JOptionPane.showInputDialog("Please enter the number of clusters: ");
		if (userInput == null) {
			return;
		}
		int numClusters = 0;
		try {
			numClusters = Integer.valueOf(userInput);
			if(numClusters>graph.getVertexCount()){

				JOptionPane.showMessageDialog(null, "The number of clusters should be less than or equal to the number of graph nodes! <=("+graph.getVertexCount()+")", "Alert",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		} catch (Exception e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Invalid value for the number of clusters.");
		}
		WEdgeBetweennessClusterer<GraphNode, GraphEdge> webc = new WEdgeBetweennessClusterer<GraphNode, GraphEdge>(
				numClusters, new Transformer<GraphEdge, Number>() {
					@Override
					public Number transform(GraphEdge e) {
						return e.getWeight();
					}
				});
		Set<Set<GraphNode>> clusters = webc.transform(graph);
		colorNodes(clusters);
	}
	
	public void voltageCluster(Graph<GraphNode, GraphEdge> graph) {
		String userInput = JOptionPane.showInputDialog("Please enter the number of candidate clusters: ");
		if (userInput == null) {
			return;
		}
		int numCandidates = 0;
		try {
			numCandidates = Integer.valueOf(userInput);
			if(numCandidates>graph.getVertexCount()){

				JOptionPane.showMessageDialog(null, "The number of clusters should be less than or equal to the number of graph nodes! <=("+graph.getVertexCount()+")", "Alert",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		} catch (Exception e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame,"Invalid value for the number of candidate clusters.");
		}
		VoltageClusterer<GraphNode, GraphEdge> vc = new VoltageClusterer<GraphNode, GraphEdge>(graph, numCandidates);
		Collection<Set<GraphNode>> clusters = vc.cluster(numCandidates);
		
		colorNodes(clusters);
	}
	
	public void mstCluster(Graph<GraphNode, GraphEdge> graph) {
		String userInput = JOptionPane.showInputDialog("Please enter the number of clusters: ");
		if (userInput == null) {
			return;
		}
		int numClusters = 0;
		try {
			numClusters = Integer.valueOf(userInput);
			if(numClusters>graph.getVertexCount()){

				JOptionPane.showMessageDialog(null, "The number of clusters should be less than or equal to the number of graph nodes! <=("+graph.getVertexCount()+")", "Alert",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			
		} catch (Exception e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Invalid value for the number of clusters.");
		}

		double tmpMaxWeight = Double.NEGATIVE_INFINITY;
		for (GraphEdge e : graph.getEdges()) {
			if (e.getWeight() > tmpMaxWeight) {
				tmpMaxWeight = e.getWeight();
			}
		}
		final double maxWeight = tmpMaxWeight;
		MstClusterer<GraphNode, GraphEdge> mstc = new MstClusterer<GraphNode, GraphEdge>(
				numClusters, new Transformer<GraphEdge, Number>() {
					@Override
					public Number transform(GraphEdge e) {
						return maxWeight - e.getWeight();
					}
				});
		Set<Set<GraphNode>> clusters = mstc.transform(graph);
		colorNodes(clusters);
	}
	
	public void cpmCluster(Graph<GraphNode, GraphEdge> graph) {
		
		CPMClusterer clusterer = new CPMClusterer();
		// use CPM algorithm
		int k = 3;
		
		// get params
		String paramK = JOptionPane.showInputDialog(
					            "Please input the k for CPM algorithm (k>=3).\nEg: k=3 means use the 3-clique for percolation",
					            "3");
						
		if(paramK==null || paramK.trim().length() < 1 ){
			JOptionPane.showMessageDialog( null,
					"K input not valid", "Warning",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		k = Integer.valueOf(paramK);
		if(k>graph.getVertexCount()){

			JOptionPane.showMessageDialog(null, "The number of clusters should be less than or equal to the number of graph nodes! <=("+graph.getVertexCount()+")", "Alert",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		// run cluster
		List<Community> clusters = clusterer.cluster(graph, k);
		
		if( clusters != null ){
			System.out.println("CPM Clustering success.");
		}else{
			System.out.println("CPM Clustering failed.");
		}
	}

	private void colorNodes(Collection<Set<GraphNode>> clusters)
	{
		RgbColor[] colors = RgbColor.getRainbow(clusters.size(), 300);
		int i = 0;
		Color color;
		for (Set<GraphNode> oneSet : clusters) {
			color = new Color(colors[i].getR(), colors[i].getG(), colors[i].getB());
			for (GraphNode gn : oneSet) {
				gn.setType(3);
				gn.setColor(color);
			}
			i++;
		}
	}
}
