package analysis;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import data.GraphEdge;
import data.GraphNode;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;


//The algorithm used to find cliques is Bron–Kerbosch algorithm. This algorithm is presented in the following paper:
// Bron, Coen; Kerbosch, Joep (1973), "Algorithm 457: finding all cliques of an undirected graph", Commun. ACM (ACM) 16 (9): 575–577,
public class CliqueFinder {
	UndirectedSparseGraph<GraphNode, GraphEdge> undGraph = null;
	HashSet<GraphNode> maximal_clique_set;
	HashSet<GraphNode> rSet = new HashSet<GraphNode>();
	
	public void find_cliques_pivot(Graph<GraphNode, GraphEdge> graph)
	{
		undGraph = new UndirectedSparseGraph<GraphNode, GraphEdge>();
		HashSet<GraphNode> nodes = new HashSet<GraphNode>();
		GraphNode gNode;
		
		for(GraphNode gn: graph.getVertices())
		{
			gNode = new GraphNode(gn);
			undGraph.addVertex(gNode);
			nodes.add(gNode);
		}
		for(GraphNode gn1: graph.getVertices())
			for(GraphNode gn2: graph.getVertices())
				if(gn1 != gn2 && graph.isNeighbor(gn1, gn2))
					undGraph.addEdge(new GraphEdge(1), findNode(gn1, undGraph), findNode(gn2, undGraph));
		
		findCliques_pivot(nodes, new HashSet<GraphNode>());
		visualizeClique_pivot(graph);
	}
	
	private void visualizeClique_pivot(Graph<GraphNode, GraphEdge> graph)
	{
		for(GraphNode v:maximal_clique_set)
		{
			findNode(v, graph).setColor(Color.blue);
			findNode(v, graph).setType(3);
		}
	}
	private GraphNode findNode(GraphNode gn, Graph<GraphNode, GraphEdge> graph)
	{
		for (GraphNode g: graph.getVertices())
			if(g.getLabel().equals(gn.getLabel()))
				return g;
		return null;
	}
	@SuppressWarnings("unchecked")
	public void findCliques_pivot(HashSet<GraphNode> p, HashSet<GraphNode> x)
	{
		if(p.size() == 0 && x.size() == 0)
		{
			if(maximal_clique_set == null)
				maximal_clique_set = (HashSet<GraphNode>)rSet.clone();
			else if (maximal_clique_set.size() < rSet.size())
				maximal_clique_set = (HashSet<GraphNode>)rSet.clone();
			return;
		}
		
		HashSet<GraphNode> pClone = (HashSet<GraphNode>)p.clone();
		HashSet<GraphNode> xClone = (HashSet<GraphNode>)x.clone();
		
		GraphNode u = null;
		if(!p.isEmpty() && x.isEmpty())
			u = (GraphNode)p.toArray()[0];
		else if(p.isEmpty() && !x.isEmpty())
			u = (GraphNode)x.toArray()[0];
		else
		{
			if(new Random().nextBoolean()) 
				u = (GraphNode)p.toArray()[0];
			else
				u = (GraphNode)x.toArray()[0];
		}
			
		HashSet<GraphNode> newP = new HashSet<GraphNode>(), newX = new HashSet<GraphNode>();
		for(Object v: pClone.toArray())
		{
			if(undGraph.isNeighbor(u, (GraphNode)v))
				continue;
			newP = new HashSet<GraphNode>();
			newX = new HashSet<GraphNode>();
			rSet.add((GraphNode)v);
			Collection<GraphNode> neigbors = undGraph.getNeighbors((GraphNode)v);
			Object[] nodesArr = pClone.toArray();
			for(Object gn: nodesArr)
				if(neigbors.contains(gn))
					newP.add((GraphNode)gn);
			nodesArr = xClone.toArray();
			for(Object gn: nodesArr)
				if(neigbors.contains(gn))
					newX.add((GraphNode)gn);

			findCliques_pivot(newP, newX);
			rSet.remove(v);
			pClone.remove(v);
			xClone.add((GraphNode)v);
		}
	}
}
