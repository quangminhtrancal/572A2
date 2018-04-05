package analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import data.GraphEdge;
import data.GraphNode;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * minimum spanning tree finder using Prim's algorithm found in
 * "Objects, Abstraction, Data Structures, and Design Using Java Version 5.0"
 * Elliot B. Koffman and Paul A.T. Wolfgang - Pg. 669
 * 
 *
 */
public class MinSpanningTreeFinder {
	public Graph<GraphNode, GraphEdge> findMinSTree (Graph<GraphNode, GraphEdge> graph, GraphNode start){
		Graph<GraphNode, GraphEdge> result = null;
		result = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		Set<GraphNode> nodesInTree = new HashSet<GraphNode>();
		Pair<GraphNode> p = null;
		ArrayList<GraphEdge> edges = prim(graph, start);
		for(GraphEdge e : edges){
			p = graph.getEndpoints(e);
			nodesInTree.add(p.getFirst());
			nodesInTree.add(p.getSecond());
			
		}
		for(GraphNode n: nodesInTree){
			result.addVertex(new GraphNode(n));
		}
		GraphNode beg=null, end=null;
	
		for(GraphEdge e: edges){
			p = graph.getEndpoints(e);
			beg = findInResult(p.getFirst(), result);
			end = findInResult(p.getSecond(), result);
			result.addEdge(e, beg, end, EdgeType.UNDIRECTED);
		}
		
		return result;
	}
	private GraphNode findInResult(GraphNode n, Graph<GraphNode, GraphEdge> result){
		for (GraphNode v: result.getVertices()){
			if (n.getID() == v.getID())
				return v;
		}
		return null;
	}
	public ArrayList<GraphEdge> prim(Graph<GraphNode, GraphEdge> graph, GraphNode start){
	
		ArrayList<GraphEdge> result = new ArrayList<GraphEdge>();
		int numVer = graph.getVertexCount();
		Set<GraphNode> vMinusS = new HashSet<GraphNode>(numVer);
		vMinusS.addAll(graph.getVertices());
		vMinusS.remove(start);
		Queue<GraphEdge> pQ = new PriorityQueue<GraphEdge>(numVer, GraphEdge.graphEdgeComparator);
		
		GraphNode current = start, dest;
		GraphEdge ge = null;
		Iterator<GraphEdge> iter;
		while(vMinusS.size()!= 0){
			iter = graph.getIncidentEdges(current).iterator();
			while(iter.hasNext()){
				ge = iter.next();
				dest = findEnd(ge, graph, current);
				if(vMinusS.contains(dest))
					pQ.add(ge);	
			}
			//Find the shortest edge whose source is IN S and destination is vMinusS
			dest = null;
			ge = null;
			do {
				ge = pQ.remove();
				dest = findEndvMinusS(ge, graph, vMinusS);
			} while (dest==null && pQ.size() != 0);
			if(dest == null && pQ.size() == 0)
				break;
			vMinusS.remove(dest);
			result.add(ge);
			current = dest;
		}
		return result;
		
	}
	
	//Given the graph and an edge from it, the function checks whether the edge has an end that has been seen previously. IF it does, the function returns the other end.
	private GraphNode findEndvMinusS(GraphEdge ge, Graph<GraphNode, GraphEdge> graph, Set<GraphNode> vMs){
		Pair<GraphNode> p = graph.getEndpoints(ge);
		if (vMs.contains(p.getFirst())&& !vMs.contains(p.getSecond())){
//			System.out.println(" S: " +(p.getSecond().getID()+1) + " D: " +(p.getFirst().getID()+1));
			return p.getFirst();
		}
		else if (!vMs.contains(p.getFirst())&& vMs.contains(p.getSecond())){
//			System.out.println(" S: " +(p.getFirst().getID()+1) + " D: " +(p.getSecond().getID()+1));
			return p.getSecond();
		}
		else {
//			System.out.println("endpoints of smallest already in S");
			return null;
		}
	}
	
	//Given one end of an edge, the function returns the other end!
	private GraphNode findEnd(GraphEdge ge, Graph<GraphNode, GraphEdge > graph, GraphNode current){
		Pair<GraphNode> p = graph.getEndpoints(ge);
		if (p.getFirst()==current)
			return p.getSecond();
		else
			return p.getFirst();
	}
	
}

