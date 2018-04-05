package clustering;



import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import clustering.cpm.AbstractCliqueFinder;
import clustering.cpm.BronKerboschV1;
import clustering.cpm.BronKerboschV2;
import clustering.cpm.Clique;
import clustering.cpm.Community;
import clustering.cpm.GuiUtil;
import data.GraphEdge;
import data.GraphNode;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Find Communities using Clique Percolation Method
 * Related Paper: Gergely Palla, Imre Derenyi, Illes Farkas, Tamas Vicsek, (2005), 
 * Uncovering the overlapping community structure of complex networks in nature and society, Nature 435, 814
 * @author Dequan
 *
 */
public class CPMClusterer  {
	public static final String NAME = CPMClusterer.class.getSimpleName();

	public List<Community> cluster(Graph<GraphNode,GraphEdge> graph, int k){

		if( graph == null ){
			showMsg( NAME + " :graph is null.");
			return null;
		}

		
		log( NAME + " started using CPM Algorithm, k=" + k);

		////////////////////////////////////////////
		// Step 1. Find all maximal cliques
		int minimumSize = 3;
		log( NAME + " : minimum clique size= " + minimumSize);
		
		UndirectedGraph<GraphNode,GraphEdge> undGraph = null;
		try{
			undGraph = (UndirectedGraph<GraphNode,GraphEdge>) graph;
		}catch(Exception e){}
		
		// need to convert directed graph to undirected graph first
		if( undGraph == null ){
			//showMsg("The graph should be undirected graph !");
			//return null;
			log("graph is not undirected, convert to undirected first.");
			undGraph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
			for(GraphNode gn: graph.getVertices()){
				undGraph.addVertex(gn);
			}
			
			for(GraphEdge edge: graph.getEdges()){
				Pair<GraphNode> nodes = graph.getEndpoints(edge);
				//if(! undGraph.isNeighbor(nodes.getFirst(), nodes.getSecond())){
				//	undGraph.addEdge(new GraphEdge(1), nodes.getFirst(), nodes.getSecond()  );
				//}
				undGraph.addEdge(edge, graph.getIncidentVertices(edge) );
			}
		}
				
		Set<GraphNode> nodesForCliques = new HashSet<GraphNode>();
		
		// use bron-kerbosch algorithm
		int bkVersion = 1;
		AbstractCliqueFinder<GraphNode,GraphEdge> cliqueFinder = bkVersion ==1 ? new BronKerboschV1( undGraph  ): new BronKerboschV2( undGraph  );
		List<Clique<GraphNode>> cliques = cliqueFinder.getAllMaximalCliques();
		List<Clique<GraphNode>> coloredCliques = new ArrayList<Clique<GraphNode>>();
		
		// set clique index , 1 -based
		int idx =1;
		for(Clique<GraphNode> c: cliques){
			boolean needToColor = c.getVertices().size() >= minimumSize ;
			// only nodes size >= 3 will be considered
			if( ! needToColor ){
				continue;
			}
			
			c.setIndex(idx++);
			// add to colored list
			coloredCliques.add(c);
			nodesForCliques.addAll(c.getVertices());
		}
		
		// constuct the graph for colored cliques
		UndirectedGraph<GraphNode,GraphEdge> coloredCliquesGraph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		for(GraphNode gn: nodesForCliques){
			GraphNode gNode = new GraphNode(gn.getActualID(),  gn.getLabel()  );
			coloredCliquesGraph.addVertex(gNode);
		}
		
		for(GraphNode gn1: nodesForCliques){
			for(GraphNode gn2: nodesForCliques){
				if(gn1 != gn2 && graph.isNeighbor(gn1, gn2)){
					coloredCliquesGraph.addEdge(new GraphEdge(1), GuiUtil.findVertex(gn1.getLabel(), coloredCliquesGraph), GuiUtil.findVertex(gn2.getLabel(), coloredCliquesGraph));
				}
			}	
		}
		
		// color the cliques
		for(Clique<GraphNode> c: coloredCliques){
			Color color = GuiUtil.getRandomColor( c.getIndex() );
			// set colors 
			for(GraphNode v: c.getVertices() ){
				GraphNode targetNode = GuiUtil.findVertex( v.getLabel(), coloredCliquesGraph);
				c.setColor(color);
				targetNode.setColor(color);
				//log("Set node " + v.id +  "/" + c.getIndex() + "to color " + c.getColor() );
				// check if this is shared node between two cliques 
				int neighborCount = coloredCliquesGraph.getNeighborCount(targetNode);
				if( neighborCount > c.getVertices().size()-1){
						targetNode.setColor(Color.RED);
				}		
			}
		}
		
		log( "# Nodes: " + graph.getVertexCount() + ", # Edges:" + graph.getEdgeCount() +  ", Cliques found (" + coloredCliques.size() + "):\n");
		for(Clique c:  coloredCliques){
	        log(c.toString() );
	    }	
		
		////////////////////////////////////////////
		// step 2. construct clique-clique overlap matrix
		int size = cliques.size();
		DoubleMatrix2D overlapMatrix = null;
		
		try{
			overlapMatrix= new SparseDoubleMatrix2D(size, size);
		}catch(Exception e){
			e.printStackTrace();
			log( NAME + ": Error in constructing overlap matrix:" + e.getMessage() );
			return null;
		}
		
		// for storing the clique-clique graph
		UndirectedGraph<Clique<GraphNode>,GraphEdge> cliquesIndexGraph = new UndirectedSparseGraph<Clique<GraphNode>, GraphEdge>();
		
		for(int i=0; i < size; i++ ){
			Clique<GraphNode> c1 = cliques.get(i);
			cliquesIndexGraph.addVertex( c1 );
			
			for(int j=0; j < size; j++){
				Clique<GraphNode> c2 = cliques.get(j);
				int overlap = 0;
				if( i == j ){
					overlap = c1.getSize();
				}else{
					overlap = c1.getOverlap(c2);
				}
				overlapMatrix.setQuick(i, j, overlap );
			}
		}
		
		log( "Clique-Clique overlap matrix:\n" + overlapMatrix.toString() );
		
		////////////////////////////////////////////
		// step 3, remove entries less than k-1 in clique-clique overlap matrix
		for(int i=0; i < size; i++ ){
			for(int j=0; j < size; j++){
				double value = overlapMatrix.get(i, j);
				if( value < k-1){
					overlapMatrix.setQuick(i, j, 0 );
				}else{
					cliquesIndexGraph.addEdge(new GraphEdge(value), findCliqueVertex(i+1, cliquesIndexGraph), findCliqueVertex(j+1, cliquesIndexGraph) );
				}
			}
		}
		
		//log( "After remove entries less than " + (k-1) + ",Clique-Clique overlap matrix:\n" + overlapMatrix.toString() );
		
		// constuct the graph for colored communities
		UndirectedGraph<GraphNode,GraphEdge> coloredCommunitiesGraph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		for(GraphNode gn: coloredCliquesGraph.getVertices()  ){
			GraphNode gNode = new GraphNode( gn.getActualID(),  gn.getLabel()  );
			coloredCommunitiesGraph.addVertex(gNode);
		}
		
		for(GraphNode gn1: coloredCliquesGraph.getVertices()){
			for(GraphNode gn2: coloredCliquesGraph.getVertices()){
				if(gn1 != gn2 && coloredCliquesGraph.isNeighbor(gn1, gn2)){
					coloredCommunitiesGraph.addEdge(new GraphEdge(1), GuiUtil.findVertex(gn1.getLabel(), coloredCommunitiesGraph), GuiUtil.findVertex(gn2.getLabel(), coloredCommunitiesGraph));
				}
			}	
		}

		////////////////////////////////////////////
		// step 4, find connected components of the resulting matrix
		WeakComponentClusterer<Clique<GraphNode>, GraphEdge> cluster = new WeakComponentClusterer<Clique<GraphNode>, GraphEdge>();
		Set<Set<Clique<GraphNode>>> cliqueCommunities = cluster.transform(cliquesIndexGraph);
		
		List<Community> communities = new ArrayList<Community>( cliqueCommunities.size() );

		idx = 1;
		// set index, 1-based, color the communities
		for(Set<Clique<GraphNode>> cliqueSet :  cliqueCommunities){
			Community<GraphNode,GraphEdge> community = new Community<GraphNode,GraphEdge>();
			community.setCliques(cliqueSet);
			// set community index, 1 - based
			community.setIndex( idx );
			communities.add(community);
			Color color = GuiUtil.getRandomColor(idx);
			community.setColor(color);
			// set colors
			for(GraphNode v: community.getVertices() ){
				GraphNode targetNode = GuiUtil.findVertex( v.getLabel(), coloredCommunitiesGraph );
				// check the shared node between two communities
				if( targetNode.getColor() == null ){
					targetNode.setColor(color);
				}else{
					targetNode.setColor(Color.RED);
				}
			}
			
			idx ++;
		}
		
		log("Communities found:" + communities.size() );
		for(Community c: communities){
			log( c.getVerticesString() );
		}
		
		// save communities
		
		//show graph		
		GuiUtil.showGraph( coloredCommunitiesGraph, null, null, communities, cliques   );
		
		return communities;
	} 
	
	public Clique findCliqueVertex(int nodeId, Graph<Clique<GraphNode>,GraphEdge> g) {
	    for (Clique n: g.getVertices())
			if( n.getIndex() == nodeId)
				return n;
		return null;
	}
	
	public void showMsg(String msg){
		JOptionPane.showMessageDialog( null,
				msg, "Warning",
				JOptionPane.WARNING_MESSAGE);
	}
	
	public void log(String msg){
		System.out.println(msg);
	}
}
