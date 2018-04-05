package analysis;

import java.util.Vector;

import data.Network;

public class ShortestPathFinder {
	public int[] dijkstra(double[][] adjList, int source)
	{
		double[] update = new double[adjList.length];
		DJKNode[] nodes = new DJKNode[adjList.length];
		int[] sequence = new int[adjList.length];
		
		for(int i = 0; i < adjList.length; i++)
			nodes[i] = new DJKNode(i);
		DJKNode current = nodes[source];
		update = adjList[source].clone();
		
		
		for(int i = 0; i < adjList.length; i++)
			if(adjList[source][i] == 0)
				update[i] = Float.MAX_VALUE;
			else	
				sequence[i] = source;
		update[source] = 0;
		//sequence[0] = source;
		
		double minDist = Float.MAX_VALUE;
		DJKNode nextNode;
		
		//Finding which node should be expanded after the source!
		current.checked = true; 
		for(int i = 0; i < adjList.length ; i++)
			if(update[i] != 0 && i != source && minDist > update[i])
			{
				minDist = update[i];
				current = nodes[i];
			}
		
		for(int i = 1; i < adjList.length; i++)
		{
			nextNode = null;
			if(minDist == Float.MAX_VALUE)
				return null;
			minDist = Float.MAX_VALUE;
			for(int j = 0; j < adjList.length; j++)
			{
				if(! nodes[j].checked)
				{
					if(adjList[current.id][j] != 0 && update[j] > update[current.id] + adjList[current.id][j])
					{
						update[j] = update[current.id] + adjList[current.id][j];
						sequence[j] = current.id;
					}
				}
			}
			current.checked = true;
			for(int j = 0; j < adjList.length; j++)
				if(!nodes[j].checked && update[j] < minDist)
				{
					minDist = update[j];
					nextNode = nodes[j];
				}
			
			current.next = nextNode;
			current = nextNode;
		}
		
		for(int i = 0 ; i < sequence.length; i++)
			if(update[i] == Float.MAX_VALUE)
				sequence[i] = Integer.MAX_VALUE;
		return sequence;
	}
	
	public int[] getShortestPath(Network network, int source, int destination)
	{
		int[] nextNodes = this.dijkstra(network.getMatrix(), source);
		if(nextNodes == null)
			return null;
		Vector<Integer> nodes = new Vector<Integer>();
		
		int node = destination;
		while(nextNodes[node] != Float.MAX_VALUE && node != source)
		{
			nodes.add(node);
			node = nextNodes[node];
		}
		
		if(nextNodes[node] == Float.MAX_VALUE)
			return null;
		int[] result = new int[nodes.size()];
		for(int i = 0; i < result.length; i++)
			result[i] = nodes.get(result.length-1-i);
		return result;
	}
//	public static void main(String[] args) {
//		double[][] arr = {{0.0, 0.0, 50.0, 49.0, 16.0, 0.0, 18.0, 5.0, 42.0, 0.0}, {2.0, 1.0, 29.0, 41.0, 0.0, 0.0, 37.0, 0.0, 0.0, 50.0}, {43.0, 0.0, 20.0, 12.0, 37.0, 0.0, 20.0, 0.0, 5.0, 0.0}, {16.0, 12.0, 0.0, 22.0, 0.0, 0.0, 43.0, 0.0, 0.0, 7.0}, {46.0, 0.0, 0.0, 20.0, 2.0, 23.0, 43.0, 0.0, 24.0, 0.0}, {0.0, 1.0, 47.0, 0.0, 0.0, 5.0, 43.0, 24.0, 8.0, 0.0}, {0.0, 0.0, 13.0, 22.0, 0.0, 18.0, 0.0, 37.0, 0.0, 0.0}, {0.0, 25.0, 0.0, 0.0, 7.0, 22.0, 46.0, 31.0, 5.0, 50.0}, {39.0, 36.0, 0.0, 49.0, 34.0, 0.0, 0.0, 0.0, 30.0, 49.0}, {0.0, 0.0, 6.0, 3.0, 44.0, 0.0, 0.0, 0.0, 0.0, 45.0}};
//		
//		Network n = new Network(1);
//		n.setMatrix(arr, 10);
//		int[] ekh = new ShortestPathFinder().getShortestPath(n, 0, 3);
//	}
}

class DJKNode
{
	boolean checked = false;
	DJKNode next;
	int id;
	
	public DJKNode(int i) {
		id = i;
	}
}
