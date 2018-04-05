package analysis;

import java.util.HashMap;
import java.util.HashSet;

import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * This class is responsible for finding the minimum spanning tree using
 * kruskal's algorithm.
 * 
 * @author Mohammadreza Rahimi
 * @Date 2011/12/19
 * @version 1.0.0
 */
public class KruskalMST {

	/**
	 * This method will find the minimum spanning tree using the kruskal's
	 * algorithm.
	 * 
	 * @param netGraph
	 * @return
	 */
	public UndirectedGraph<GraphNode, GraphEdge> findMST(
			NetworkGraph netGraph) {
		// ititializations
		// this hash map assigns each tree in the forest a tree number
		HashMap<GraphNode, Integer> mapper = new HashMap<GraphNode, Integer>();
		// this hash set contains all the vertices in the graph
		HashSet<GraphNode> vertices = new HashSet<GraphNode>();
		// this hash set contains all the edges in the graph
		HashSet<GraphEdge> edges = new HashSet<GraphEdge>();
		// first I added all the edges to the edge set, then I added all the end
		// points of those edges to the vertices set
		edges.addAll(netGraph.getGraph().getEdges());
		for (GraphEdge e : edges) {
			vertices.add(netGraph.getGraph().getEndpoints(e).getFirst());
			vertices.add(netGraph.getGraph().getEndpoints(e).getSecond());
		}
		// considering each vertice as a tree
		int cnt = 0;
		for (GraphNode v : vertices)
			mapper.put(v, cnt++);

		// algorithm starts
		HashSet<GraphEdge> treeEdges = new HashSet<GraphEdge>();
		// algorithm finishes when all the edges are considered. or
		// when the number of edges added to the tree is |V|-1. that is we have
		// one tree covering all vertices
		while (!edges.isEmpty()) {
			// minimum edge of the graph that is not considered before is added
			// to selected;
			GraphEdge minEdge = selectMinEdge(edges);
			Pair<GraphNode> pair = netGraph.getGraph().getEndpoints(minEdge);
			// if this edge connects two different trees
			if (mapper.get(pair.getFirst()) != mapper.get(pair.getSecond())) {
				// it's added to the tree.
				treeEdges.add(minEdge);
				int map = mapper.get(pair.getSecond());
				// two trees's are merged, in fact tree-id of one tree is set
				// for all the vertices of the other one
				for (GraphNode g : mapper.keySet())
					if (mapper.get(g) == map)
						mapper.put(g, mapper.get(pair.getFirst()));
			}
			// the second termination condition
			if (treeEdges.size() == vertices.size() - 1)
				break;
			// the selected edge is removed from the set of edges. this means we
			// have considered that edge.
			edges.remove(minEdge);
		}
		// a new graph is made for the tree
		UndirectedSparseGraph<GraphNode, GraphEdge> minSpanningGraph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		for (GraphNode n : vertices)
			minSpanningGraph.addVertex(n);
		for (GraphEdge e : treeEdges) {
			Pair<GraphNode> pair = netGraph.getGraph().getEndpoints(e);
			minSpanningGraph.addEdge(e, pair.getFirst(), pair.getSecond());
		}
		return minSpanningGraph;
	}

	/**
	 * returns the minimum edge in a set of edges passed to the method.
	 * 
	 * @param edges
	 * @return
	 */
	private GraphEdge selectMinEdge(HashSet<GraphEdge> edges) {
		double selectedWeight = Double.POSITIVE_INFINITY;
		GraphEdge selectedEdge = null;
		for (GraphEdge edge : edges) {
			if (edge.getWeight() < selectedWeight) {
				selectedEdge = edge;
				selectedWeight = edge.getWeight();
			}
		}
		return selectedEdge;
	}

}