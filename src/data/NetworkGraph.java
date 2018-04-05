package data;

import java.awt.Color;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javax.swing.JFrame;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

//This class is for storing networks and their corresponding graphs.
public class NetworkGraph {
	Network network;
	Graph<GraphNode, GraphEdge> graph;
	
	public NetworkGraph(Network net)
	{
		this.network = net;
		buildNetwork();
	}
	
	public void buildNetwork()
	{
		if(network.isDirected)
			graph = new DirectedSparseGraph<GraphNode, GraphEdge>();
		else
			graph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		
		if(network.mode == 1)
			buildOneModeNetwork();
		else
			buildTwoModeNetwork();
	}
	public void setGraph(Graph<GraphNode, GraphEdge> aGraph) {
		this.graph = aGraph;
	}
	private void buildOneModeNetwork()
	{
		if(network.labels == null)
			createVertices();
		else
			createVertices(network.labels);
		createEdges();
	}

	private void buildTwoModeNetwork()
	{
		if(network.labels == null || network.labels[0] == null || network.labels[1] == null)
			create2mVertices();
		else
			createVertices(network.labels);
		createEdges();
	}
	
	
	//If the file doesn't contain headers, this function would be called to create vertices
	private void createVertices()
	{
		GraphNode gn;
		for(int i = 0; i < network.row; i++)
		{
			gn = new GraphNode(i+1, null);
			graph.addVertex(gn);
		}
	}
	//me: remove node from graph
	public void removeNode(String nodeName){
		GraphNode gn=findNode(nodeName);
		graph.removeVertex(gn);
	}
	private void create2mVertices()
	{
		GraphNode gn;
		for(int j = 0; j < network.column; j++)
		{
			gn = new GraphNode(j+1, null); //The label of i-th node in the first dimension of the graph has id equal to i+1.
			gn.type = 1;
			graph.addVertex(gn);
		}
		for(int j = 0; j < network.row; j++)
		{
			gn = new GraphNode(network.column + j +1, null); //The label of i-th node in the second dimension (mode) of the graph has id equal to columns+i+1.
			gn.type = 2;
			graph.addVertex(gn);
		}
	}
	
	//If the file contains headers this function would be called
	private void createVertices(String[][] labels)
	{
		GraphNode gn = null;
		if(network.mode == 1)
		{
			for(int j = 0; j < labels[0].length; j++)
			{
				if(network.labels[0][j] != null) //I think it would never be equal to null!!
					gn = new GraphNode(j+1, network.labels[0][j]);
				graph.addVertex(gn);
			}
		}
		else
		{
			for(int j = 0; j < labels[0].length; j++)
			{
				if(network.labels[0][j] != null) //I think it would never be equal to null!!
					gn = new GraphNode(j+1, network.labels[0][j]); //The label of i-th node in the first dimension of the graph has id equal to i+1.
				gn.type = 1;
				graph.addVertex(gn);
			}
			for(int j = 0; j < labels[1].length; j++)
			{
				if(network.labels[1][j] != null) //I think it would never be equal to null!!
					gn = new GraphNode(network.labels[0].length + j +1, network.labels[1][j]); //The label of i-th node in the second dimension (mode) of the graph has id equal to columns+i+1.
				gn.type = 2;
				graph.addVertex(gn);
			}
		}
	}
	
	
	private void createEdges()
	{
		GraphEdge ge;
		if(network.mode == 2) // If the network is a two-mode one, it should be undirected.
		{
			for(int i = 0; i < network.row; i++)
				for(int j = 0; j < network.column; j++)
				{
					if(network.matrix[i][j] != 0)
					{
						ge = new GraphEdge(network.matrix[i][j]);
						graph.addEdge(ge, findNode(j+1), findNode(i+1+network.column));
					}
				}
		}
		else if(network.isDirected)
		{
			for(int i = 0; i < network.row; i++)
				for(int j = 0; j < network.column; j++)
				{
					if(network.matrix[i][j] != 0)
					{
						ge = new GraphEdge(network.matrix[i][j]);
						graph.addEdge(ge, findNode(i+1), findNode(j+1), EdgeType.DIRECTED);
						
					}
				}
		}
		else
		{
			for(int i = 0; i < network.row; i++)
				for(int j = 0; j <= i; j++)
				{
					if(network.matrix[i][j] != 0)
					{
						ge = new GraphEdge(network.matrix[i][j]);
						graph.addEdge(ge, findNode(i+1), findNode(j+1));
					}
				}
		}
	}
	
	
	//This function would not show the edges that don't satisfy the weight threshold condition.
	private void createEdges(double threshold, int operator)
	{
		GraphEdge ge;
		boolean greater, less;
		if(network.mode == 2){
			for(int i = 0; i < network.row; i++)
				for(int j = 0; j < network.column; j++){
					if(network.matrix[i][j] != 0){
						greater = operator == 0 && network.matrix[i][j] > threshold;
						less = operator == 1 && network.matrix[i][j] < threshold;
						if(greater || less){
							ge = new GraphEdge(network.matrix[i][j]);
							graph.addEdge(ge, findNode(j+1), findNode(i+1+network.column));
						}
					}
				}
		}else if(network.isDirected){
			for(int i = 0; i < network.row; i++)
				for(int j = 0; j < network.column; j++){
					if(network.matrix[i][j] != 0){
						greater = operator == 0 && network.matrix[i][j] > threshold;
						less = operator == 1 && network.matrix[i][j] < threshold;
						if(greater || less){
							ge = new GraphEdge(network.matrix[i][j]);
							graph.addEdge(ge, findNode(i+1), findNode(j+1), EdgeType.DIRECTED);
						}
					}
				}
		}else{
			for(int i = 0; i < network.row; i++)
				for(int j = 0; j <= i; j++){
					if(network.matrix[i][j] != 0){
						greater = operator == 0 && network.matrix[i][j] > threshold;
						less = operator == 1 && network.matrix[i][j] < threshold;
						if(greater || less){
							ge = new GraphEdge(network.matrix[i][j]);
							graph.addEdge(ge, findNode(i+1), findNode(j+1));
						}
					}
				}
		}
//		System.out.println("cheteeeee?");
	}
	
	public void rankMetric(NodeMetric[] metrics)
	{
		if(metrics==null)
			return;
		float colTemp = (float) 1.0;

		String label;
		GraphNode gn;
		for(int i = 0; i < metrics.length; i++)
		{
			label = metrics[i].getNodeName();
			gn = this.findNode(label);
			gn.type = 3;
//			gn.color = colTemp;
			gn.color = Color.getHSBColor(0, colTemp, 1);
			colTemp = colTemp - (float)1.0 / metrics.length;
		}
	}
	
	public void groupNodes(Collection<Set<GraphNode>> groups)
	{
		RgbColor[] colors = RgbColor.getRainbow(groups.size(), 300);
		int i = 0;
		Color color;
		for(Set<GraphNode> group: groups)
		{
			color = new Color(colors[i].getR(), colors[i].getG(), colors[i].getB());
			for(GraphNode gn: group)
			{
				gn.type = 3;
				gn.color = color;
			}
			i++;
		}
	}
	
	public GraphNode findNode(int i) {
		for(GraphNode gn:graph.getVertices())
			if (gn.id == i)
				return gn;
		return null;
	}
	
	public GraphNode findNode(String name) {
		for(GraphNode gn:graph.getVertices())
			if (gn.getLabel().equals(name))
				return gn;
		return null;
	}
	
	public Network getNetwork() {
		return network;
	}
	
	public Graph<GraphNode, GraphEdge> getGraph() {
		return graph;
	}
	
	
	//In order to apply the weight threshold constraint for the graph, we first remove all the edges
	//Why don't we just remove the links that don't satisfy the condition?
	//		Because we want to apply the condition on the original graph. 
	//		If we just remove some links, the graph would not be the original one and the next constraint wouldn't have the desired property.
	public void hideLinks(double threshold, int operator)
	{
		Object[] gEdges = graph.getEdges().toArray();
		for (int i = 0; i < gEdges.length; i++)
			graph.removeEdge((GraphEdge)gEdges[i]);
			
		createEdges(threshold, operator);
	}
	
	public void saveNetwork()
	{
		JFrame frame = new JFrame("Save network");
		FileDialog fd = new FileDialog(frame , "Save as",FileDialog.SAVE);
		fd.setEnabled(true);
		fd.setVisible(true);
		fd.setAlwaysOnTop(true);
		 
		String path = fd.getDirectory();
		if (fd.getDirectory() == null || fd.getFile() == null)
			return;
		path = fd.getDirectory()+ "/"+fd.getFile();
		if(!path.endsWith(".csv"))
			path += ".csv";
		
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(path));
			
			if(network.labels != null)
			{
				fw.append(network.labels[0][0]);
				for(int i = 1; i < network.labels[0].length; i++)
					fw.append(", " + network.labels[0][i]);
				fw.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		double[][] matrix = new double[network.row][network.column];
		int fromNode, toNode;
		if (network.isDirected) {
			for (GraphEdge ge : graph.getEdges()) {
				fromNode = graph.getEndpoints(ge).getFirst().id - 1;
				toNode = graph.getEndpoints(ge).getSecond().id - 1;
				matrix[fromNode][toNode] = ge.weight;
			}
		}
		else{
			for (GraphEdge ge : graph.getEdges()) {
				fromNode = graph.getEndpoints(ge).getFirst().id - 1;
				toNode = graph.getEndpoints(ge).getSecond().id - 1;
				matrix[fromNode][toNode] = ge.weight;
				matrix[toNode][fromNode] = ge.weight;
			}
		}
		
		try{
			for(int i = 0; i < network.row; i++)
			{
				if(network.labels != null)
					fw.append(network.labels[1][i]+ ", ");
				fw.append(""+matrix[i][0]);
				for(int j = 1; j < network.column; j++)
					fw.append(", " + matrix[i][j]);
				fw.append("\n");
			}
			fw.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
}
