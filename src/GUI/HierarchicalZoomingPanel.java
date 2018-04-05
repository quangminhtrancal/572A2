package GUI;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.commons.collections15.Transformer;
import org.jgap.gp.function.If;

import clustering.ModularityClusterer;
import clustering.ModularityClusterer.Cluster;
import clustering.MstClusterer;
import clustering.WEdgeBetweennessClusterer;
import data.GraphEdge;
import data.GraphNode;
import data.Network;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.cluster.VoltageClusterer;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class HierarchicalZoomingPanel extends JPanel {
	
	JPanel netPnl, controlPanel, algorithmPanel;
	final int MODULARITY = 1;
	final int BETWEEN = 2;
	final int MST = 3;
	final int VOLTAGE = 4;
	int maxNodesNumber;
	final int minNodesNumber = 1;
	int currentAlgorithm = BETWEEN;
	int numClusters;
	int previousClusterNum;
	List<Set<GraphNode>> mainClusters = new ArrayList<Set<GraphNode>>();
	List<Graph<GraphNode, GraphEdge>> graphHistoryZoom = new ArrayList<Graph<GraphNode, GraphEdge>>();
	int zoomCount = 0;
	boolean zoomedOut = false;
	Color panelsBGColor = new Color(210, 210, 210);
	//DefaultModalGraphMouse<GraphNode, GraphEdge> graphMouse;
	ScalingControl scaler = new CrossoverScalingControl();
	VisualizationViewer<GraphNode, GraphEdge> vv;
	Network clonedNetwork;
	NetworkGraph clonedNetworkGraph;
	NetworkGraph originalNetworkGraph;
	JRadioButton modularityButton;
	JRadioButton betweennessButton;
	JRadioButton mstButton;
	JRadioButton voltageButton;
	
	public HierarchicalZoomingPanel(HierarchicalZoomGraphFrame frame, NetworkGraph aGraph) {	
		originalNetworkGraph = aGraph;
		clonedNetwork = new Network(aGraph.getNetwork().getMatrix(), aGraph.getNetwork().getRow(), aGraph.getNetwork().getLabels(), aGraph.getNetwork().getMode());
		clonedNetwork.toggleNetworkDirection();
		clonedNetworkGraph = new NetworkGraph(clonedNetwork);
		
		frame.add(this);
		frame.setContentPane(this);
		
		TitledBorder titledBorder;
		Border blackline = BorderFactory.createLineBorder(Color.black);
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		
		{ //Initializing the panel for network visualization
			netPnl = new JPanel();
			netPnl.setSize(780, 640);
			titledBorder = BorderFactory.createTitledBorder(blackline, "Network");
		//	titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(14.0f));
			netPnl.setBorder(BorderFactory.createCompoundBorder(raisedbevel, titledBorder));
			netPnl.setBackground(panelsBGColor);
			this.add(netPnl);
		}

		this.setLayout(null);
		
		{ //Initializing the panel for controlling the network 
			controlPanel = new JPanel();
			titledBorder = BorderFactory.createTitledBorder(blackline, "Controls");
//			titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(14.0f));
			controlPanel.setBounds(780, 00, 200, 200);
			controlPanel.setBackground(panelsBGColor);
			controlPanel.setBorder(BorderFactory.createCompoundBorder(raisedbevel,
					titledBorder));
			this.add(controlPanel);
		}
		
		{ //Initializing the panel for displaying reports
			algorithmPanel = new JPanel();
			titledBorder = BorderFactory.createTitledBorder(blackline, "Clustering Algorithms");
	//		titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(14.0f));
			algorithmPanel.setBounds(780, 200, 200, 440);
			algorithmPanel.setBackground(panelsBGColor);
			algorithmPanel.setBorder(BorderFactory.createCompoundBorder(raisedbevel, titledBorder));
			this.add(algorithmPanel);
		}
		
		clusterZoomControlPanel(controlPanel);
		initClusterPanel(algorithmPanel);
		
		//graphMouse = new DefaultModalGraphMouse<GraphNode, GraphEdge>();
		
		maxNodesNumber = clonedNetworkGraph.getGraph().getVertexCount();
		numClusters = maxNodesNumber;
		clonedNetworkGraph.buildNetwork();
		setRadioButtonSelection();
		loadGraph();
	}
	
	public void clusterZoomControlPanel(JPanel controlPanel) {
		controlPanel.setLayout(null);
		Border blackline = BorderFactory.createLineBorder(Color.black);

		{
			JLabel zoomLabel = new JLabel();
			TitledBorder titledBorder = BorderFactory.createTitledBorder(
					blackline, "Zoom");
			zoomLabel.setBorder(titledBorder);
			zoomLabel.setBounds(20, 30, 100, 50);

			controlPanel.add(zoomLabel);

			Button zoomIn = new Button("+");
			zoomIn.setBounds(20, 20, 20, 20);
			zoomLabel.add(zoomIn);
			zoomIn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(clonedNetworkGraph.getGraph().getVertexCount() < maxNodesNumber) {
						if(!graphHistoryZoom.isEmpty()) {
							clonedNetworkGraph.setGraph(graphHistoryZoom.get(zoomCount - 1));
							graphHistoryZoom.remove(zoomCount - 1);
							zoomCount--;
							
							if(zoomedOut) {
								numClusters = previousClusterNum;
								zoomedOut = false;
							}
							
							loadGraph();
							scaler.scale(vv, 1.3f, vv.getCenter());
						}
					}
				}
			});

			Button zoomOut = new Button("-");
			zoomOut.setBounds(60, 20, 20, 20);
			zoomLabel.add(zoomOut);
			zoomOut.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(clonedNetworkGraph.getGraph().getVertexCount() > minNodesNumber) {
						zoomedOut = true;
						zoomCount++;
						graphHistoryZoom.add(clonedNetworkGraph.getGraph());
						
						previousClusterNum = numClusters;
						numClusters = (int)(numClusters - numClusters*0.20);
						reconstructGraph(currentAlgorithm);

						scaler.scale(vv, 1/1.3f, vv.getCenter());
					} 
				}
			});
		}
		
		Button resetBtn = new Button("Reset graph");
		resetBtn.setBounds(45, 165, 100, 20);
		controlPanel.add(resetBtn);
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetEvent();
				loadGraph();
			}
		});		
	}

	public void resetEvent()
	{
		currentAlgorithm = BETWEEN;
		setRadioButtonSelection();
		clonedNetworkGraph.setGraph(originalNetworkGraph.getGraph());
		numClusters = maxNodesNumber;
	}
	
	private void setRadioButtonSelection()
	{
	    if(currentAlgorithm == BETWEEN)
	    	betweennessButton.setSelected(true);
	    else if(currentAlgorithm == VOLTAGE)
	    	   voltageButton.setSelected(true);
	    else if(currentAlgorithm == MST)
	    	   mstButton.setSelected(true);
	    else if(currentAlgorithm == MODULARITY)
	    	   modularityButton.setSelected(true);	
	}
	
	private void initClusterPanel(JPanel algorithmPanel){
		modularityButton = new JRadioButton("Modularity");
		modularityButton.setActionCommand("Modularity");
		modularityButton.setBackground(panelsBGColor);
		
	    betweennessButton = new JRadioButton("Edge Betweenness");
	    betweennessButton.setActionCommand("Edge Betweenness");
	    betweennessButton.setBackground(panelsBGColor);
	    
	    voltageButton = new JRadioButton("Voltage");
	    voltageButton.setActionCommand("Voltage");
	    voltageButton.setBackground(panelsBGColor);
	    
	    mstButton = new JRadioButton("Minimum Spanning Tree");
	    mstButton.setActionCommand("Minimum Spanning Tree");
	    mstButton.setBackground(panelsBGColor);
	    
	    // Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(modularityButton);
	    group.add(betweennessButton);
	    group.add(voltageButton);
	    group.add(mstButton);
	    
	    algorithmPanel.add(modularityButton);
	    algorithmPanel.add(betweennessButton);
	    algorithmPanel.add(voltageButton);
	    algorithmPanel.add(mstButton);
	    
	    addAlgorithmActionListeners();
	    
	    Button resetAlgorithm = new Button("Apply algorithm");
	    resetAlgorithm.setBounds(45, 420, 100, 20);
	    algorithmPanel.add(resetAlgorithm);
		resetAlgorithm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clonedNetworkGraph.setGraph(originalNetworkGraph.getGraph());
				numClusters = maxNodesNumber;
				loadGraph();
			}
		});	    
	}

	private void addAlgorithmActionListeners() {
		modularityButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				currentAlgorithm = MODULARITY;
			}
		});
	    
	    betweennessButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentAlgorithm = BETWEEN;
			}
		});
	    
	    voltageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				currentAlgorithm = VOLTAGE;
			}
		});
	    
	    mstButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {	
				currentAlgorithm = MST;
			}
		});
	}
	
	public WEdgeBetweennessClusterer<GraphNode, GraphEdge> runClusterAlgorithmBetween()
	{
		WEdgeBetweennessClusterer<GraphNode, GraphEdge> generatedCluster = new WEdgeBetweennessClusterer<GraphNode, GraphEdge>(
				numClusters, new Transformer<GraphEdge, Number>() {
					@Override
					public Number transform(GraphEdge e) {
						return e.getWeight();
					}
				});
		
		return generatedCluster;
	}
	
	public VoltageClusterer<GraphNode, GraphEdge> runClusterAlgorithmVoltage()
	{
		VoltageClusterer<GraphNode, GraphEdge> generatedCluster = new VoltageClusterer<GraphNode, GraphEdge>(clonedNetworkGraph.getGraph(), numClusters);
		
		return generatedCluster;
	}
	
	public MstClusterer<GraphNode, GraphEdge> runClusterAlgorithmMST()
	{
		double tmpMaxWeight = Double.NEGATIVE_INFINITY;
		for (GraphEdge e : clonedNetworkGraph.getGraph().getEdges()) {
			if (e.getWeight() > tmpMaxWeight) {
				tmpMaxWeight = e.getWeight();
			}
		}
		final double maxWeight = tmpMaxWeight;
		MstClusterer<GraphNode, GraphEdge> generatedCluster = new MstClusterer<GraphNode, GraphEdge>(
				numClusters, new Transformer<GraphEdge, Number>() {
					@Override
					public Number transform(GraphEdge e) {
						return maxWeight - e.getWeight();
					}
				});
		
		return generatedCluster;
	}
	
	public ModularityClusterer<GraphNode, GraphEdge> runClusterAlgorithmModularity()
	{
		ModularityClusterer<GraphNode, GraphEdge> generatedCluster = new ModularityClusterer<GraphNode, GraphEdge>(
				numClusters, new Transformer<GraphEdge, Number>() {
					@Override
					public Number transform(GraphEdge e) {
						return e.getWeight();
					}
				});
		
		return generatedCluster;
	}
	
	public void reconstructGraph(int type)
	{
		List<Collection<GraphNode>> combinedNeigborsPerSet;
		Set<Set<GraphNode>> clusters = null;
		
		clusters = generateClusters(type);
		combinedNeigborsPerSet = computeNeigbors(clusters);

		clonedNetworkGraph.setGraph(buildCommunityGraph(clonedNetworkGraph, combinedNeigborsPerSet));
		
		loadGraph();

		mainClusters.clear();
	}

	private Set<Set<GraphNode>> generateClusters(int type)
	{	
		Set<Set<GraphNode>> c = null;
		
		switch(type)
		{
			case BETWEEN:
				WEdgeBetweennessClusterer<GraphNode, GraphEdge> generatedCluster1 = runClusterAlgorithmBetween();
				c = generatedCluster1.transform(clonedNetworkGraph.getGraph());
				break;
			// TODO fix commented out lines
			case VOLTAGE:
				VoltageClusterer<GraphNode, GraphEdge> generatedCluster2 = runClusterAlgorithmVoltage();
				c = convertCollectionToSet(generatedCluster2.cluster(numClusters));
				break;
			case MST:
				MstClusterer<GraphNode, GraphEdge> generatedCluster3 = runClusterAlgorithmMST();
				c = generatedCluster3.transform(clonedNetworkGraph.getGraph());
				break;
			case MODULARITY:
				ModularityClusterer<GraphNode, GraphEdge> generatedCluster4 = runClusterAlgorithmModularity();
				c = generatedCluster4.transform(clonedNetworkGraph.getGraph());
				break;
		}
		return c;
	}
	
	private Set<Set<GraphNode>> convertCollectionToSet(Collection<Set<GraphNode>> aCollection)
	{
		Set<Set<GraphNode>> tempSet = new HashSet<Set<GraphNode>>();
		
		for (Set<GraphNode> aSet : aCollection)
		{
			tempSet.add(aSet);
		}
		
		return tempSet;
	}
	
	private List<Collection<GraphNode>> computeNeigbors(Set<Set<GraphNode>> clusters)
	{
		List<Collection<GraphNode>> tempCombinedNeigborsPerSet = new ArrayList<Collection<GraphNode>>();
		
		int clusterIndex = 1;
		for (Set<GraphNode> oneSet : clusters) {
			mainClusters.add(oneSet);
			Collection<GraphNode> neighbors = new ArrayList<GraphNode>();
			clusterIndex++;
			for (GraphNode gn : oneSet) {
				Collection<GraphNode> tempNeighborCollection = clonedNetworkGraph.getGraph().getNeighbors(gn);
				for (GraphNode element : tempNeighborCollection) {
					if(!neighbors.contains(element))
						neighbors.add(element);
				}
			}
			
			for (GraphNode nodeElement : oneSet) {
				if(neighbors.contains(nodeElement))
					neighbors.remove(nodeElement);
			}
			tempCombinedNeigborsPerSet.add(neighbors);
		}
				
		return tempCombinedNeigborsPerSet;
	}
	
	private Graph<GraphNode, GraphEdge> buildCommunityGraph(NetworkGraph netGraph, List<Collection<GraphNode>> combinedNeigbors) {
		Graph<GraphNode, GraphEdge> newGraph = null;
		GraphNode[] nodes = new GraphNode[combinedNeigbors.size()];

		// creating an instance of graph
		if (netGraph.getNetwork().isDirected())
			newGraph = new DirectedSparseGraph<GraphNode, GraphEdge>();
		else
			newGraph = new UndirectedSparseGraph<GraphNode, GraphEdge>();

		// Adding nodes to the graph
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new GraphNode(netGraph.findNode(i + 1));
			newGraph.addVertex(nodes[i]);
		}

		// creating edges of the new graph
		if (netGraph.getNetwork().isDirected()) {
			for(int i = 0; i < nodes.length; i++)
				for(int j = 0; j < nodes.length; j++)
					if(i != j) {						
						addEdge(combinedNeigbors.get(i), nodes, newGraph, i, j, EdgeType.DIRECTED);
					}
			} else{
			for(int i = 0; i < nodes.length; i++)
				for(int j = i; j < nodes.length; j++)
					if(i != j) {
						addEdge(combinedNeigbors.get(i), nodes, newGraph, i, j, EdgeType.UNDIRECTED);
					}
		}
		return newGraph;
	}
	
	private void addEdge(Collection<GraphNode> c, GraphNode[] n, Graph<GraphNode, GraphEdge> g, int i, int j, EdgeType type)
	{
		for (GraphNode nodeElement : c) {
			if(mainClusters.get(j).contains(nodeElement)) {
				g.addEdge(new GraphEdge(1), n[i], n[j], type);
			}
		}
	}
	
	//This method visualizes the graph
		public void loadGraph()
		{		
			KKLayout<GraphNode, GraphEdge> layout = new KKLayout<GraphNode, GraphEdge>(clonedNetworkGraph.getGraph());
			
			// Sets the initial size of the space
			layout.setSize(new Dimension(400, 400));
			
			vv= new VisualizationViewer<GraphNode, GraphEdge>(layout);

			vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<GraphNode>());
			vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<GraphEdge>()
					{
				public String transform(GraphEdge edge)
				{
					return edge.getLabel(3);
				}
					});
			
			Transformer<GraphNode, Paint> vertexPaint = new Transformer<GraphNode, Paint>() {
				@Override
				public Paint transform(GraphNode gn) {
					return Color.RED;
				}
			};
			
			vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
			
			JScrollPane jsp = new JScrollPane(vv, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			jsp.add(vv);
			vv.scaleToLayout(scaler);
			
			//vv.setGraphMouse(graphMouse);
			//vv.addKeyListener(graphMouse.getModeKeyListener());
			
			jsp.setViewportView(vv);
			jsp.setBounds(30, 30, 720, 600);
			jsp.setVisible(true);
			this.netPnl.setLayout(null);
			this.netPnl.add(jsp);
			this.revalidate();
			this.repaint();
		}
}
