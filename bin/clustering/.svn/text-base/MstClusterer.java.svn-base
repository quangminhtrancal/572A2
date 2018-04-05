package clustering;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.shortestpath.MinimumSpanningForest2;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;

public class MstClusterer<V, E> implements
		Transformer<Graph<V, E>, Set<Set<V>>> {

	private Transformer<E, Number> edgeWeightTransformer;
	private int numClusters;

	public MstClusterer(int numClusters) {
		this.numClusters = numClusters;
		edgeWeightTransformer = new Transformer<E, Number>() {
			public Number transform(E e) {
				return 1;
			};
		};
	}

	public MstClusterer(int numClusters,
			Transformer<E, Number> edgeWeightTransformer) {
		this.numClusters = numClusters;
		this.edgeWeightTransformer = edgeWeightTransformer;
	}

	public Forest<V, E> getMinimumSpanningForest(Graph<V, E> graph) {
		MinimumSpanningForest2<V, E> prim = new MinimumSpanningForest2<V, E>(
				graph, new DelegateForest<V, E>(), DelegateTree
						.<V, E> getFactory(), new Transformer<E, Double>() {
					public Double transform(E e) {
						return edgeWeightTransformer.transform(e).doubleValue();
					}
				});
		return prim.getForest();
	}

	@Override
	public Set<Set<V>> transform(Graph<V, E> graph) {
		DelegateForest<V, E> msf = (DelegateForest<V, E>) getMinimumSpanningForest(graph);
		
		PriorityQueue<E> sortedEs = new PriorityQueue<E>(msf.getEdgeCount(),
				new Comparator<E>() {
					@Override
					public int compare(E e1, E e2) {
						double e1Weight = edgeWeightTransformer.transform(e1).doubleValue();
						double e2Weight = edgeWeightTransformer.transform(e2).doubleValue();
						if (e1Weight < e2Weight) {
							return 1;
						} else if (e1Weight > e2Weight) {
							return -1;
						} else {
							return 0;
						}
					}
				});
		for (E e : msf.getEdges()) {
			sortedEs.add(e);
		}
		int numEsToRemove = Math.min(numClusters - msf.getTrees().size(), msf
				.getEdgeCount());
		for (int i = 0; i < numEsToRemove; i++) {
			E e = sortedEs.poll();
			msf.removeEdge(e, false);
		}
		WeakComponentClusterer<V, E> wcc = new WeakComponentClusterer<V, E>();
		Set<Set<V>> clusters = wcc.transform(msf);
		
		return clusters;

	}
}
