package analysis;

import java.util.ArrayList;
import java.util.HashMap;

import data.GraphEdge;
import data.GraphNode;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class BridgeFinder {
	public ArrayList<Pair<GraphNode>> findBridges(Graph<GraphNode, GraphEdge> graph)
	{
		GraphEdge[] edges = {};
		GraphEdge ge;
		
		ArrayList<Pair<GraphNode>> result = new ArrayList<Pair<GraphNode>>();
		UndirectedSparseGraph<GraphNode, GraphEdge> undirectedGraph = ConvertDirectedToUndirectedGraph(graph);
		edges = undirectedGraph.getEdges().toArray(edges);

		for(int i = 0; i < edges.length; i++){
			ge = edges[i];
			Pair<GraphNode> endNodes = undirectedGraph.getEndpoints(ge);
			GraphNode v1 = endNodes.getFirst();
			GraphNode v2 = endNodes.getSecond();
			undirectedGraph.removeEdge(ge);

			ArrayList<GraphNode> partition1 = GetPartition(v1, undirectedGraph);
			ArrayList<GraphNode> partition2 = GetPartition(v2, undirectedGraph);

			if (!Intersection(partition1, partition2)) {
				result.add(endNodes);
			}
			undirectedGraph.addEdge(ge, v1, v2, EdgeType.UNDIRECTED);
		}
		return result;
	}
	
	private UndirectedSparseGraph<GraphNode, GraphEdge> ConvertDirectedToUndirectedGraph(
			Graph<GraphNode, GraphEdge> graph) {
		UndirectedSparseGraph<GraphNode, GraphEdge> undirectedGraph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		HashMap<Integer, GraphNode> nodeMap = new HashMap<Integer, GraphNode>();
		GraphNode temp;
		
		for (GraphNode gn: graph.getVertices()){
			temp = new GraphNode(gn);
			nodeMap.put(gn.getID(), temp);
			undirectedGraph.addVertex(temp);
		}
		
		GraphNode v1, v2;
		for (GraphEdge ge: graph.getEdges()) {
			v1 = nodeMap.get(graph.getEndpoints(ge).getFirst().getID());
			v2 = nodeMap.get(graph.getEndpoints(ge).getSecond().getID());
			Pair<GraphNode> nodePair = new Pair<GraphNode>(v1, v2);
			undirectedGraph.addEdge(ge, nodePair);
		}
		return undirectedGraph;
	}
	
	private ArrayList<GraphNode> GetPartition(GraphNode node,
			Graph<GraphNode, GraphEdge> graph) {
		ArrayList<GraphNode> partition = new ArrayList<GraphNode>();
		DijkstraDistance<GraphNode, GraphEdge> d = new DijkstraDistance<GraphNode, GraphEdge>(
				graph);
		GraphNode[] nodes = {};
		nodes = graph.getVertices().toArray(nodes);
		for (int i = 0; i < nodes.length; i++) {
			if (d.getDistance(node, nodes[i]) != null)
				if (!partition.contains(nodes[i]))
					partition.add(nodes[i]);
		}
		return partition;
	}
	
	private boolean Intersection(ArrayList<GraphNode> a, ArrayList<GraphNode> b) {
		for (int i = 0; i < a.size(); i++)
			for (int j = 0; j < b.size(); j++)
				if (a.get(i).getID() == b.get(j).getID())
					return true;
		return false;
	}
}
