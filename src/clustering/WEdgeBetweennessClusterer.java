package clustering;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An algorithm for computing clusters (community structure) in graphs based on
 * edge betweenness. The betweenness of an edge is defined as the extent to
 * which that edge lies along shortest paths between all pairs of nodes.
 * 
 * This algorithm works by iteratively following the 2 step process:
 * <ul>
 * <li>Compute edge betweenness for all edges in current graph
 * <li>Remove edge with highest betweenness
 * </ul>
 * <p>
 * Running time is: O(kmn) where k is the number of edges to remove, m is the
 * total number of edges, and n is the total number of vertices. For very sparse
 * graphs the running time is closer to O(kn^2) and for graphs with strong
 * community structure, the complexity is even lower.
 * <p>
 * This algorithm is a slight modification of the algorithm discussed below in
 * that the number of edges to be removed is parameterized.
 * 
 * @author Scott White
 * @author Tom Nelson (converted to jung2)
 * @see "Community structure in social and biological networks by Michelle Girvan and Mark Newman"
 */
public class WEdgeBetweennessClusterer<V, E> implements
		Transformer<Graph<V, E>, Set<Set<V>>> {
	// private int mNumEdgesToRemove;
	// private Map<E, Pair<V>> edges_removed;
	private int numClusters;
	private Map<E, Number> edgeWeights;
	private Transformer<E, Number> edgeWTransformer;

	/**
	 * Constructs a new clusterer for the specified graph.
	 * 
	 * @param numEdgesToRemove
	 *            the number of edges to be progressively removed from the graph
	 */
	/*
	 * public WEdgeBetweennessClusterer(int numEdgesToRemove, Map<E, Number>
	 * edgeWeights) { mNumEdgesToRemove = numEdgesToRemove; edges_removed = new
	 * LinkedHashMap<E, Pair<V>>(); this.edgeWeights = edgeWeights; }
	 */

	public WEdgeBetweennessClusterer(int numClusters, Map<E, Number> edgeWeights) {
		// mNumEdgesToRemove = numEdgesToRemove;
		// edges_removed = new LinkedHashMap<E, Pair<V>>();
		this.numClusters = numClusters;
		this.edgeWeights = edgeWeights;
	}
	
	public WEdgeBetweennessClusterer(int numClusters, Transformer<E, Number> edgeWTransformer){
		this.numClusters = numClusters;
		this.edgeWTransformer= edgeWTransformer;
	}
	
	public WEdgeBetweennessClusterer(int numClusters){
		this.numClusters = numClusters;
		edgeWTransformer = new Transformer<E, Number>() {
			public Number transform(E e) {
				return 1;
			}
		};
	}
	/**
	 * Finds the set of clusters which have the strongest "community structure".
	 * The more edges removed the smaller and more cohesive the clusters.
	 * 
	 * @param graph
	 *            the graph
	 */
	/*
	 * public Set<Set<V>> transform(Graph<V,E> graph) {
	 * 
	 * if (mNumEdgesToRemove < 0 || mNumEdgesToRemove > graph.getEdgeCount()) {
	 * throw new IllegalArgumentException("Invalid number of edges passed in.");
	 * }
	 * 
	 * edges_removed.clear();
	 * 
	 * for (int k=0;k<mNumEdgesToRemove;k++) { BetweennessCentrality<V,E> bc =
	 * new BetweennessCentrality<V,E>(graph); bc.setEdgeWeights(edgeWeights);
	 * bc.setRemoveRankScoresOnFinalize(false); bc.evaluate(); E to_remove =
	 * null; double score = 0; for (E e : graph.getEdges()){ if
	 * (bc.getEdgeRankScore(e) > score) { to_remove = e; score =
	 * bc.getEdgeRankScore(e); } } System.out.println("remove: " +
	 * graph.getEndpoints(to_remove)); edges_removed.put(to_remove,
	 * graph.getEndpoints(to_remove)); graph.removeEdge(to_remove); }
	 * 
	 * WeakComponentClusterer<V,E> wcSearch = new WeakComponentClusterer<V,E>();
	 * Set<Set<V>> clusterSet = wcSearch.transform(graph);
	 * 
	 * for (Map.Entry<E, Pair<V>> entry : edges_removed.entrySet()) { Pair<V>
	 * endpoints = entry.getValue(); graph.addEdge(entry.getKey(),
	 * endpoints.getFirst(), endpoints.getSecond()); } return clusterSet; }
	 */
	public Set<Set<V>> transform(Graph<V, E> graph){
		if (numClusters < 0 || numClusters > graph.getVertexCount()) {
			throw new IllegalArgumentException(
					"Invalid number of edges passed in.");
		}
		
		if(edgeWeights == null){
			edgeWeights = new HashMap<E, Number>();
			for(E e: graph.getEdges()){
				edgeWeights.put(e, edgeWTransformer.transform(e));
			}
		}
		WeakComponentClusterer<V, E> wcSearch = new WeakComponentClusterer<V, E>();
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Graph<V, E>> graphType = (Class<? extends Graph<V, E>>) graph
					.getClass();
			Graph<V, E> clusteredGraph = graphType.newInstance();
			for (V v : graph.getVertices()) {
				clusteredGraph.addVertex(v);
			}
			for (E e : graph.getEdges()) {
				Pair<V> endPoints = graph.getEndpoints(e);
				clusteredGraph.addEdge(e, endPoints.getFirst(), endPoints
						.getSecond());
			}
		
		Set<Set<V>> clusterSet = wcSearch.transform(graph);

		while (clusterSet.size() < numClusters) {
			BetweennessCentrality<V, E> bc = new BetweennessCentrality<V, E>(
					clusteredGraph);
			bc.setEdgeWeights(edgeWeights);
			bc.setRemoveRankScoresOnFinalize(false);
			bc.evaluate();
			E to_remove = null;
			double score = 0;
			for (E e : clusteredGraph.getEdges()) {
				if (bc.getEdgeRankScore(e) > score) {
					to_remove = e;
					score = bc.getEdgeRankScore(e);
				}
			}
//			System.out.println("remove: "
//					+ to_remove/*clusteredGraph.getEndpoints(to_remove)*/);
			clusteredGraph.removeEdge(to_remove);
			clusterSet = wcSearch.transform(clusteredGraph);
		}
		
		return clusterSet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wcSearch.transform(graph);
	}

	/**
	 * Retrieves the list of all edges that were removed (assuming extract(...)
	 * was previously called). The edges returned are stored in order in which
	 * they were removed.
	 * 
	 * @return the edges in the original graph
	 */
	/*
	 * public List<E> getEdgesRemoved() { return new
	 * ArrayList<E>(edges_removed.keySet()); }
	 */
}
