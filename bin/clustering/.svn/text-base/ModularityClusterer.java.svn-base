package clustering;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class ModularityClusterer<V, E> implements Transformer<Graph<V, E>, Set<Set<V>>> {

	private long kTime;
	private long levelUpTime;
	private long addRemoveTime;
	private long getCandidateDestClustersTime;
	private long k_inTime;
	
	private double initialModularity;
	private double finalModularity;
	private double sumModularityGain;

	private Transformer<E, Number> edgeWeightTransformer;
	private int numberOfPasses;
	private int maxClusterSize;
	private double twoM;

	public ModularityClusterer(int numberOfPasses) {
		this(numberOfPasses, Integer.MAX_VALUE);
	}

	public ModularityClusterer(int numberOfPasses,
			Transformer<E, Number> edgeWeightTransformer) {

		this(numberOfPasses, Integer.MAX_VALUE, edgeWeightTransformer);
	}

	public ModularityClusterer(int numberOfPasses, int maxClusterSize) {
		this.numberOfPasses = numberOfPasses;
		edgeWeightTransformer = new Transformer<E, Number>() {
			public Number transform(E e) {
				return 1;
			};
		};
		this.maxClusterSize = maxClusterSize;
	}

	public ModularityClusterer(int numberOfPasses, int maxClusterSize,
			Transformer<E, Number> edgeWeightTransformer) {

		this.numberOfPasses = numberOfPasses;
		this.edgeWeightTransformer = edgeWeightTransformer;
		this.maxClusterSize = maxClusterSize;
	}
	
	public double getInitialModularity() {
		return initialModularity;
	}

	public double getFinalModularity() {
		return finalModularity;
	}

	public double getModularityGain() {
		if (twoM == 0) {
			return 0;
		}
		return sumModularityGain * 2 / twoM;
	}

	public int getMaxClusterSize() {
		return maxClusterSize;
	}

	public void setMaxClusterSize(int maxClusterSize) {
		this.maxClusterSize = maxClusterSize;
	}

	public void printTimes() {
		DecimalFormat dFormatter = new DecimalFormat();
		dFormatter.setGroupingSize(3);
//		System.out.println("kTime: " + dFormatter.format(kTime / MILLION)
//				+ " ms");
//		System.out.println("levelUpTime: "
//				+ dFormatter.format(levelUpTime / MILLION) + " ms");
//		System.out.println("addRemoveTime: "
//				+ dFormatter.format(addRemoveTime / MILLION) + " ms");
//		System.out.println("getCandidateDestClustersTime: "
//				+ dFormatter.format(getCandidateDestClustersTime / MILLION)
//				+ " ms");
//		System.out.println("k_inTime: " + dFormatter.format(k_inTime / MILLION)
//				+ " ms");
	}
	
	private double getModularity(List<Cluster> clusters){
		double sumSum_in = 0;
		double sumSquareSum_tot = 0;
		
		for(Cluster c : clusters){
			sumSum_in += c.sum_in;
			sumSquareSum_tot += Math.pow(c.sum_tot, 2);
		}
		
		double modularity = (sumSum_in - sumSquareSum_tot / twoM) / twoM;
		
		return modularity;
	}
	
	private Graph<Cluster, Edge> initializeClusteringGraph(Graph<V, E> graph) {

		Graph<Cluster, Edge> clusteringGraph = new UndirectedSparseGraph<Cluster, Edge>();
		HashMap<V, Cluster> vcMap = new HashMap<V, Cluster>();

		for (V v : graph.getVertices()) {
			Cluster c = new Cluster(v);
			clusteringGraph.addVertex(c);
			vcMap.put(v, c);
		}

		for (E e : graph.getEdges()) {
			Pair<V> endPoints = graph.getEndpoints(e);
			Cluster c1 = vcMap.get(endPoints.getFirst());
			Cluster c2 = vcMap.get(endPoints.getSecond());
			Edge we = new Edge(edgeWeightTransformer.transform(e));
			clusteringGraph.addEdge(we, c1, c2);
		}

		return clusteringGraph;
	}

	private double selfLoopConsolidatedWeight(Cluster superC,
			Graph<Cluster, Edge> clusteringGraph) {

		double result = 0;
		Set<Cluster> superCSubs = superC.getSubClusters();
		for (Cluster c : superCSubs) {
			Collection<Cluster> neighbors = clusteringGraph.getNeighbors(c);
			for (Cluster neighbor : neighbors) {
				if (superCSubs.contains(neighbor)) {
					double weight = clusteringGraph.findEdge(c, neighbor)
							.getWeight();
					result += weight;
				}
			}
		}
		return result;
	}

	private double consolidatedWeight(Cluster superC1, Cluster superC2,
			Graph<Cluster, Edge> clusteringGraph) {

		if (superC1 == superC2) {
			return selfLoopConsolidatedWeight(superC1, clusteringGraph);
		}

		double result = 0;
		for (Cluster c1 : superC1.getSubClusters()) {
			for (Cluster c2 : superC2.getSubClusters()) {

				Edge e = clusteringGraph.findEdge(c1, c2);
				if (e != null) {
					result += e.getWeight();
				}
			}
		}
		return result;
	}

	private double k(Cluster c, Graph<Cluster, Edge> graph) {
		long t1 = System.nanoTime();
		try {
			double result = 0;

			for (Edge e : graph.getIncidentEdges(c)) {
				result += e.getWeight();
			}
			return result;
		} finally {
			long t2 = System.nanoTime();
			kTime += t2 - t1;
		}
	}

	private void updateTwoM(Graph<Cluster, Edge> graph) {
		twoM = 0;
		for (Edge e : graph.getEdges()) {
			Pair<Cluster> endPoints = graph.getEndpoints(e);

			if (endPoints.getFirst() == endPoints.getSecond()) {
				twoM += e.getWeight();
			} else {
				twoM += 2 * e.getWeight();
			}
		}
	}

	private Set<Cluster> getCandidateDestClusters(Cluster c,
			Graph<Cluster, Edge> graph, Map<Cluster, Cluster> superClusterMap) {
		long t1 = System.nanoTime();
		try {
			HashSet<Cluster> candidateDests = new HashSet<Cluster>();
			for (Cluster neighbor : graph.getNeighbors(c)) {
				Cluster neighborCluster = superClusterMap.get(neighbor);
				if (neighborCluster == superClusterMap.get(c)) {
					continue;
				}
				if (neighborCluster.getVertexCount() + c.getVertexCount() <= maxClusterSize) {
					candidateDests.add(neighborCluster);
				}
			}
			return candidateDests;
		} finally {
			long t2 = System.nanoTime();
			getCandidateDestClustersTime += t2 - t1;
		}
	}
	
	
	private Graph<Cluster, Edge> levelUpGraph(List<Cluster> superClusters,
			Graph<Cluster, Edge> clusteringGraph) {
		// System.out.println("levelUpGraph()");
		long t1 = System.nanoTime();
		try {
			Graph<Cluster, Edge> graph = new UndirectedSparseGraph<Cluster, Edge>();
			for (Cluster superC : superClusters) {
				graph.addVertex(superC);
			}
			for (Cluster superC1 : superClusters) {
				// System.out.println("Cluster: " + superC1 + " neighbors: "
				// + superC1.getNeighborClusters());
				for (Cluster superC2 : superC1.getNeighborClusters()) {

					if (graph.findEdge(superC1, superC2) != null) {
						continue;
					}

					double weight = consolidatedWeight(superC1, superC2,
							clusteringGraph);

					if (weight > 0) {
						graph.addEdge(new Edge(weight), superC1, superC2);
					}
				}
			}
			return graph;
		} finally {
			long t2 = System.nanoTime();
			levelUpTime += t2 - t1;
		}
	}

	@Override
	public Set<Set<V>> transform(Graph<V, E> graph) {

		Graph<Cluster, Edge> clusteringGraph = initializeClusteringGraph(graph);
		updateTwoM(clusteringGraph);
		LinkedList<Cluster> superClusters = null;
		sumModularityGain = 0;

		for (int pass = 0; pass < numberOfPasses; pass++) {

//			System.out.println("Graph size: vertices: "
//					+ clusteringGraph.getVertexCount() + ", Edges: "
//					+ clusteringGraph.getEdgeCount() + " weight: " + twoM / 2);

			boolean graphChangedInPass = false;

			HashMap<Cluster, Cluster> superClusterMap = new HashMap<Cluster, Cluster>();
			superClusters = new LinkedList<Cluster>();

			for (Cluster c : clusteringGraph.getVertices()) {
				Cluster superC = new Cluster();
				superClusters.add(superC);
				superClusterMap.put(c, superC);
			}

			for (Cluster c : clusteringGraph.getVertices()) {
				Cluster superC = superClusterMap.get(c);
				superC.add(c, clusteringGraph, superClusterMap);
			}
			
			if(pass == 0){
				initialModularity = getModularity(superClusters);
			}
			
			int reset = 0;
			boolean graphChangedSinceReset;

			do {
				//				long t1 = System.nanoTime();

				graphChangedSinceReset = false;
//				System.out.println("pass " + pass + " reset " + reset);
				reset++;
				int moveCount = 0;

				for (Cluster c : clusteringGraph.getVertices()) {

					double maxModularityGain = 0;
					Cluster maxDestC = null;
					Cluster superC1 = superClusterMap.get(c);

					Set<Cluster> candidateDests = getCandidateDestClusters(c,
							clusteringGraph, superClusterMap);

					double currentK_in = superC1.k_in(c, clusteringGraph);
					double k_c = k(c, clusteringGraph);

					for (Cluster superC2 : candidateDests) {

						assert superC2 != superC1;

						double modularityGain = superC2
								.k_in(c, clusteringGraph)
								+ ((superC1.sum_tot - superC2.sum_tot - k_c) * k_c)
								/ twoM;

						// System.out.println(c + " source: " + superC1
						// + " dest: " + superC2 + " gain: "
						// + modularityGain);

						if (modularityGain > maxModularityGain) {
							maxModularityGain = modularityGain;
							maxDestC = superC2;
						}
					}

					maxModularityGain -= currentK_in;

					if (maxModularityGain > 0) {

						moveCount++;

						superC1.remove(c, clusteringGraph, superClusterMap);
						if (superC1.getSubClusterCount() == 0) {
							superClusters.remove(superC1);
						}

						superClusterMap.put(c, maxDestC);
						maxDestC.add(c, clusteringGraph, superClusterMap);
						graphChangedInPass = true;
						graphChangedSinceReset = true;
						sumModularityGain += maxModularityGain;

					}
				}

//				long t2 = System.nanoTime();

//				System.out.println("Time: " + (t2 - t1) / MILLION + " ms");
//				System.out.println("k_inTime: " + k_inTime / MILLION + " ms");
//				System.out.println("Number of moves: " + moveCount);
//				System.out.println("sumModularityGain: " + sumModularityGain);

			} while (graphChangedSinceReset);

			if (!graphChangedInPass) {
				break;
			}
			clusteringGraph = levelUpGraph(superClusters, clusteringGraph);
			// System.out.println("level up: " + clusteringGraph);
		}
		Set<Set<V>> result = new HashSet<Set<V>>();
		for (Cluster c : superClusters) {
			result.add(c.getVertexSet());
		}
		
		finalModularity = getModularity(superClusters);
		
		return result;
	}

	public final class Cluster {

		private Set<Cluster> subClusters;
		private double sum_tot;
		private double sum_in;
		private Map<Cluster, Integer> neighborClusterMap;
		private int level;
		private V vertex;
		private int vertexCount;
		
		public Cluster() {
			subClusters = new HashSet<Cluster>();
			neighborClusterMap = new HashMap<Cluster, Integer>();
		}

		public Cluster(V v) {
			this.vertex = v;
			level = 0;
			vertexCount = 1;
		}

		public int getSubClusterCount() {
			return getSubClusters().size();
		}

		public Set<Cluster> getSubClusters() {
			return Collections.unmodifiableSet(subClusters);
		}

		public V getVertex() {
			return vertex;
		}

		public int getLevel() {
			return level;
		}

		public Set<Cluster> getNeighborClusters() {
			return Collections.unmodifiableSet(neighborClusterMap.keySet());
		}

		public boolean isSingleVertexCluster() {
			return level == 0;
		}

		public int rGetVertexCount() {
			if (getLevel() == 0) {
				return 1;
			}
			if (getLevel() == 1) {
				return subClusters.size();
			}
			int vCount = 0;
			for (Cluster subC : subClusters) {
				vCount += subC.rGetVertexCount();
			}
			return vCount;
		}

		public int getVertexCount() {
			// assert vertexCount == rGetVertexCount();
			return vertexCount;
		}

		public Set<V> getVertexSet() {
			Set<V> result = new HashSet<V>();
			if (isSingleVertexCluster()) {
				result.add(vertex);
				return result;
			}
			for (Cluster c : getSubClusters()) {
				result.addAll(c.getVertexSet());
			}
			return result;
		}

		private void addNeighborCluster(Cluster neighbor) {
			Integer linkCount = neighborClusterMap.get(neighbor);
			if (linkCount == null) {
				linkCount = 0;
			}
			neighborClusterMap.put(neighbor, ++linkCount);
		}

		private void removeNeighborCluster(Cluster neighbor) {
			Integer linkCount = neighborClusterMap.get(neighbor);
			if (linkCount == null) {
				throw new IllegalArgumentException(neighbor
						+ " is not a neighbor of " + this);
			}
			if (--linkCount == 0) {
				neighborClusterMap.remove(neighbor);
			} else {
				neighborClusterMap.put(neighbor, linkCount);
			}
		}

		private void addNeighborClusters(Cluster subC,
				Graph<Cluster, Edge> graph,
				Map<Cluster, Cluster> superClusterMap) {
			for (Cluster subCNeighbor : graph.getNeighbors(subC)) {
				Cluster neighbor = superClusterMap.get(subCNeighbor);
				if (neighbor.getSubClusterCount() == 0) {
					continue;
				}
				assert neighbor.getSubClusters().contains(subCNeighbor) : "this: "
						+ this
						+ ", subC: "
						+ subC
						+ ", subCNeighbor: "
						+ subCNeighbor + ", neighbor: " + neighbor;
				addNeighborCluster(neighbor);
				neighbor.addNeighborCluster(this);

			}
		}

		private void removeNeighborClusters(Cluster subC,
				Graph<Cluster, Edge> graph,
				Map<Cluster, Cluster> superClusterMap) {

			for (Cluster subCNeighbor : graph.getNeighbors(subC)) {
				Cluster neighbor = superClusterMap.get(subCNeighbor);
				// if (neighbor == this) {
				// continue;
				// }
				removeNeighborCluster(neighbor);
				neighbor.removeNeighborCluster(this);
			}
		}

		private void add(Cluster subC, Graph<Cluster, Edge> graph,
				Map<Cluster, Cluster> superClusterMap) {
			long t1 = System.nanoTime();

			if (subClusters.size() == 0) {
				this.level = subC.level + 1;
			}
			assert subC.level + 1 == level;

			subClusters.add(subC);
			// addK_in(subC, graph);
			vertexCount += subC.vertexCount;

			sum_tot += k(subC, graph);
			sum_in += k_in(subC, graph);
			
			addNeighborClusters(subC, graph, superClusterMap);

			long t2 = System.nanoTime();
			addRemoveTime += t2 - t1;
		}

		private void remove(Cluster subC, Graph<Cluster, Edge> graph,
				Map<Cluster, Cluster> superClusterMap) {
			long t1 = System.nanoTime();

			if (!subClusters.contains(subC)) {
				throw new IllegalArgumentException(subC + " is not in " + this
						+ ".");
			}

			subClusters.remove(subC);
			// removeK_in(subC, graph);
			vertexCount -= subC.getVertexCount();

			sum_tot -= k(subC, graph);
			sum_in -= k_in(subC, graph);
			
			removeNeighborClusters(subC, graph, superClusterMap);

			long t2 = System.nanoTime();
			addRemoveTime += t2 - t1;
		}

		private double k_in(Cluster c, Graph<Cluster, Edge> graph) {
			long t1 = System.nanoTime();
			try {
				double result = 0;

				int subClusterCount = subClusters.size();
				int cNeighborCount = graph.getNeighborCount(c);

				if (cNeighborCount < subClusterCount) {
					for (Cluster neighbor : graph.getNeighbors(c)) {
						if (subClusters.contains(neighbor)) {
							result += graph.findEdge(c, neighbor).getWeight();
						}
					}
				} else {
					for (Cluster c2 : subClusters) {
						Edge e = graph.findEdge(c, c2);
						if (e != null) {
							result += e.getWeight();
						}
					}
				}

				if (!subClusters.contains(c)) {
					Edge e = graph.findEdge(c, c);
					if (e != null) {
						result += e.getWeight();
					}
				}

				return result;
			} finally {
				long t2 = System.nanoTime();
				k_inTime += t2 - t1;
			}
		}

		@Override
		public String toString() {
			if (isSingleVertexCluster()) {
				return vertex == null ? "[]" : vertex.toString();
			}
			return getSubClusters().toString();
		}

	}

	private static final class Edge {
		private double weight;

		Edge(Number weight) {
			this.weight = weight.doubleValue();
		}

		public double getWeight() {
			return weight;
		}

		public String toString() {
			return String.valueOf(weight);
			// return "WeightedEdge [weight=" + weight + "]";
		}
	}
}
