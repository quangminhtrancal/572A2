package analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import data.GraphEdge;
import data.GraphNode;
import data.LinkMetrics;
import data.Network;
import data.NodeMetric;
import edu.uci.ics.jung.algorithms.blockmodel.StructurallyEquivalent;
import edu.uci.ics.jung.algorithms.blockmodel.VertexPartition;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.metrics.Metrics;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.EigenvectorCentrality;
import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.Graph;

public class MetricFinder {
	public NodeMetric[] ranks;
	public Collection<Set<GraphNode>> groups;
	
	//**************************Munima****************************//
		public LinkMetrics[] findLink(Graph<GraphNode, GraphEdge> graph)
		{
			DegreeScorer<GraphNode> ranker = new DegreeScorer<GraphNode>(graph);
			NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
			LinkMetrics[] nodeLink=new LinkMetrics[graph.getVertexCount()*graph.getVertexCount()];
			 
			int i = 0;
			int j = 0;
			int l = 0;
			
			GraphNode[] oneSet;
			oneSet = new GraphNode[graph.getVertexCount()];

			for(GraphNode v: graph.getVertices()){
				nodeRanks[i] = new NodeMetric(v.getLabel(), graph.degree(v));
				oneSet[i]=v;
				i++;
			}
			
			for(i=0;i<oneSet.length-1;i++){
				String[] nb1=new String[graph.getNeighborCount(oneSet[i])];

				j=0;
				int total=0;
				for(GraphNode u: graph.getNeighbors(oneSet[i])){
					nb1[j]=u.getLabel();
					j++;
				}
				for(int k=i+1;k<oneSet.length;k++){
					String[] nb2=new String[graph.getNeighborCount(oneSet[k])];
					j=0;
					for(GraphNode u: graph.getNeighbors(oneSet[k])){
						nb2[j]=u.getLabel();
						j++;
					}
					double jacc;
					float cn=CommonN(nb1,nb2);
					float tn=(graph.getNeighborCount(oneSet[k])+graph.getNeighborCount(oneSet[i]));
					float tn1 = tn - cn;
					if (tn1<0)
						tn1 = -tn1;
					
					jacc=(cn/tn1);
					
					nodeLink[l]=new LinkMetrics();
					nodeLink[l].setNode1(oneSet[i].getLabel());
					nodeLink[l].setNode2(oneSet[k].getLabel());
					nodeLink[l].setMetrics(jacc);

					System.out.println(	"Jaccard Coefficient between "+nodeLink[i].getNode1()+" and "+nodeLink[i].getNode2()+" is - "+nodeLink[i].getMetrics());
					//System.out.println(	"Jaccard Coefficient between "+oneSet[i]+" and "+oneSet[k]+" is - "+nodeLink[i].getMetrics());
					l++;
				}
			}
			
			LinkMetrics[] nodeLink1=new LinkMetrics[l];
			
			for (i=0; i<l; i++)
				nodeLink1[i] = nodeLink[i];
				
			return nodeLink1;
		}
		
		public int CommonN(String[] n1,String[] n2){
		int max,min,total=0;
		for(int i=0;i<n1.length;i++){
			for(int j=0;j<n2.length;j++){
				if(n1[i].equals(n2[j]))
					total++;
			}
		
		}
		//System.out.println("Common for "+n1+" "+n2+" "+total);
		return total;
		}
		public double calculateNodeAdamic(Graph<GraphNode, GraphEdge> graph,GraphNode[] n1,GraphNode[] n2){
			double total=0;
			for(int i=0;i<n1.length;i++){
				for(int j=0;j<n2.length;j++){
					if(n1[i].equals(n2[j])){
						int wN=graph.getNeighborCount(n1[i]);
						total+=1/(double)wN;
					}
				}
			
			}
			//System.out.println("Common for "+n1+" "+n2+" "+total);
			return total;
			}
		
		//********************************Munima End**********************//
		
		///////////////////////////DICE - Kashfia////////////////////////////////////
		public LinkMetrics[] findDiceLink(Graph<GraphNode, GraphEdge> graph)
		{
			DegreeScorer<GraphNode> ranker = new DegreeScorer<GraphNode>(graph);
			NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
			LinkMetrics[] nodeLink=new LinkMetrics[graph.getVertexCount()*graph.getVertexCount()];
			
			int i = 0;
			int j = 0;
			int l = 0;

			GraphNode[] oneSet;
			oneSet = new GraphNode[graph.getVertexCount()];

			for(GraphNode v: graph.getVertices()){
				nodeRanks[i] = new NodeMetric(v.getLabel(), graph.degree(v));
				oneSet[i]=v;
				i++;
			}
			
			for(i=0;i<oneSet.length-1;i++){
				String[] nb1=new String[graph.getNeighborCount(oneSet[i])];

				j=0;
				int total=0;
				for(GraphNode u: graph.getNeighbors(oneSet[i])){
					nb1[j]=u.getLabel();
					j++;
				}
				for(int k=i+1;k<oneSet.length;k++){
					String[] nb2=new String[graph.getNeighborCount(oneSet[k])];
					j=0;
					for(GraphNode u: graph.getNeighbors(oneSet[k])){
						nb2[j]=u.getLabel();
						j++;
					}
					double dice;
					float cn=CommonN(nb1,nb2);
					float tn=(graph.getNeighborCount(oneSet[k])+graph.getNeighborCount(oneSet[i]));

					dice=((2*cn)/tn);
					nodeLink[l]=new LinkMetrics();
					nodeLink[l].setNode1(oneSet[i].getLabel());
					nodeLink[l].setNode2(oneSet[k].getLabel());
					nodeLink[l].setMetrics(dice);
					System.out.println(	"Dice Coefficient between "+nodeLink[l].getNode1()+" and "+nodeLink[l].getNode2()+" is - "+nodeLink[l].getMetrics());
					//System.out.println(	"Dice Coefficient between "+oneSet[i]+" and "+oneSet[k]+" is - "+nodeLink[i].getMetrics());
					l++;
				}
			}
				
			LinkMetrics[] nodeLink1=new LinkMetrics[l];
			
			for (i=0; i<l; i++)
				nodeLink1[i] = nodeLink[i];
			
			return nodeLink1;
		}
		////////////////////////////END DICE/////////////////////////////////////////////
		
		///////////////////////////COMMON NEIGHBOR - Kashfia////////////////////////////////////
		public LinkMetrics[] findCommonLink(Graph<GraphNode, GraphEdge> graph)
		{
			DegreeScorer<GraphNode> ranker = new DegreeScorer<GraphNode>(graph);
			NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
			LinkMetrics[] nodeLink=new LinkMetrics[graph.getVertexCount()*graph.getVertexCount()];
			
			int i = 0;
			int j = 0;
			int l = 0;

			GraphNode[] oneSet;
			oneSet = new GraphNode[graph.getVertexCount()];

			for(GraphNode v: graph.getVertices()){
				nodeRanks[i] = new NodeMetric(v.getLabel(), graph.degree(v));
				oneSet[i]=v;
				i++;
			}
			
			for(i=0;i<oneSet.length-1;i++){
				String[] nb1=new String[graph.getNeighborCount(oneSet[i])];

				j=0;
				int total=0;
				for(GraphNode u: graph.getNeighbors(oneSet[i])){
					nb1[j]=u.getLabel();
					j++;
				}
				for(int k=i+1;k<oneSet.length;k++){
					String[] nb2=new String[graph.getNeighborCount(oneSet[k])];
					j=0;
					for(GraphNode u: graph.getNeighbors(oneSet[k])){
						nb2[j]=u.getLabel();
						j++;
					}
					
					double cn=CommonN(nb1,nb2);
					
					nodeLink[l]=new LinkMetrics();
					nodeLink[l].setNode1(oneSet[i].getLabel());
					nodeLink[l].setNode2(oneSet[k].getLabel());
					nodeLink[l].setMetrics(cn);
					System.out.println("Common Neighbor Coefficient between "+nodeLink[l].getNode1()+" and "+nodeLink[l].getNode2()+" is - "+nodeLink[l].getMetrics());
					//System.out.println(	"Common Neighbor Coefficient between "+oneSet[i]+" and "+oneSet[k]+" is - "+nodeLink[i].getMetrics());
					l++;
				}
			}
			
			LinkMetrics[] nodeLink1=new LinkMetrics[l];
				
			for (i=0; i<l; i++)
				nodeLink1[i]=nodeLink[i];

			return nodeLink1;
		}
		////////////////////////////END COMMON NEIGHBOR/////////////////////////////////////////////
		
		
		
	///////////////////////////Adamic_adar - Suchina////////////////////////////////////
	public LinkMetrics[] findAdamicLink(Graph<GraphNode, GraphEdge> graph)
	{
		DegreeScorer<GraphNode> ranker = new DegreeScorer<GraphNode>(graph);
		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		LinkMetrics[] nodeLink=new LinkMetrics[graph.getVertexCount()*graph.getVertexCount()];

		int i = 0;
		int j = 0;
		int l = 0;

		GraphNode[] oneSet;
		oneSet = new GraphNode[graph.getVertexCount()];

		for(GraphNode v: graph.getVertices()){
			nodeRanks[i] = new NodeMetric(v.getLabel(), graph.degree(v));
			oneSet[i]=v;
			i++;
		}

		for(i=0;i<oneSet.length-1;i++){
			GraphNode[] nb1=new GraphNode[graph.getNeighborCount(oneSet[i])];
			j=0;
			for(GraphNode u: graph.getNeighbors(oneSet[i])){
				nb1[j]=u;
				j++;
			}
			for(int k=i+1;k<oneSet.length;k++){
				GraphNode[] nb2=new GraphNode[graph.getNeighborCount(oneSet[k])];
				j=0;
				for(GraphNode u: graph.getNeighbors(oneSet[k])){
					nb2[j]=u;
					j++;
				}
				double adamic=calculateNodeAdamic(graph,nb1,nb2);
	 
				
				nodeLink[l]=new LinkMetrics();
				nodeLink[l].setNode1(oneSet[i].getLabel());
				nodeLink[l].setNode2(oneSet[k].getLabel());
				nodeLink[l].setMetrics(adamic);
				System.out.println(	"Adamic Coefficient between "+nodeLink[l].getNode1()+" and "+nodeLink[l].getNode2()+" is - "+nodeLink[l].getMetrics());
				//System.out.println(	"Dice Coefficient between "+oneSet[i]+" and "+oneSet[k]+" is - "+nodeLink[i].getMetrics());
				l++;
			}
		}

		LinkMetrics[] nodeLink1=new LinkMetrics[l];

		for (i=0; i<l; i++)
			nodeLink1[i] = nodeLink[i];

		return nodeLink1;
	}
	 ///////////////////////&END Adamic/Adar/////////////////////////////////////////////

	
	public NodeMetric[] findBetweenness(Graph<GraphNode, GraphEdge> graph)
	{
		BetweennessCentrality<GraphNode, GraphEdge> ranker = new BetweennessCentrality<GraphNode, GraphEdge>(graph, true, false);
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();
//		ranker.printRankings(true, true);

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), ranker.getVertexRankScore(v));
		
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}
	
	public NodeMetric[] findEigenvector(Graph<GraphNode, GraphEdge> graph)
	{
		EigenvectorCentrality<GraphNode, GraphEdge> ranker = new EigenvectorCentrality<GraphNode, GraphEdge>(graph);
		ranker.evaluate();

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)ranker.getVertexScore(v));
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}
	
	public NodeMetric[] findDegree(Graph<GraphNode, GraphEdge> graph)
	{
		DegreeScorer<GraphNode> ranker = new DegreeScorer<GraphNode>(graph);
		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), ranker.getVertexScore(v));
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}

	public NodeMetric[] findCloseness(Graph<GraphNode, GraphEdge> graph)
	{
		ClosenessCentrality<GraphNode, GraphEdge> ranker = new ClosenessCentrality<GraphNode, GraphEdge>(graph);
		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), ranker.getVertexScore(v));
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}
	public NodeMetric[] findAuthority(Graph<GraphNode, GraphEdge> graph)
	{
		HITS<GraphNode, GraphEdge> ranker=new HITS<GraphNode, GraphEdge> (graph);
		
		ranker.evaluate();

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)ranker.getVertexScore(v).authority);
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}	
	public NodeMetric[] findHub(Graph<GraphNode, GraphEdge> graph)
	{
		HITS<GraphNode, GraphEdge> ranker=new HITS<GraphNode, GraphEdge> (graph);
		
		ranker.evaluate();

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)ranker.getVertexScore(v).hub);
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}	
	
	public NodeMetric[] findRadiusAndDiameter(Network network)
	{
		ShortestPathFinder spFinder = new ShortestPathFinder();
		boolean isDisconnected = false;
		
		double [] pathDistances = new double[network.getRow()];
		double maxDistance;
		
		for(int i = 0; i < network.getRow(); i++)
		{
			//Find longest of the shortest path distance from node i
			
			maxDistance = 0;
			for(int j = 0; j < network.getColumn(); j++)
			{
				if (i == j)  //No self-loops
					continue;
				
				int [] sp = spFinder.getShortestPath(network, i, j);
				
				if (sp == null)
				{
					isDisconnected = true;
					break;
				}
				
				double d = getPathLength(network, sp, i);
				if (d > maxDistance)
					maxDistance = d;
			}
			
			//Store the maximum shortest path distance
			pathDistances[i] = maxDistance;
		}
		
		NodeMetric[] result = new NodeMetric[2];
		if (isDisconnected)
		{
			result[0] = new NodeMetric("Radius", Double.MAX_VALUE);
			result[1] = new NodeMetric("Diameter", Double.MAX_VALUE);
		}
		else
		{
			Arrays.sort(pathDistances);
			//Minimum is the radius; Maximum is the diameter
			result[0] = new NodeMetric("Radius", pathDistances[0]);
			result[1] = new NodeMetric("Diameter", pathDistances[pathDistances.length - 1]);
		}
		return result;
	}
	
	private double getPathLength(Network network, int [] path, int source)
	{
		double d = network.getMatrix()[source][path[0]];
		for (int i = 0; i < path.length - 1; i++)
		{
			d = d + network.getMatrix()[path[i]][path[i + 1]];
		}
		return d;
	}
	
	public NodeMetric[] findClusteringCoeff(Graph<GraphNode, GraphEdge> graph)
	{
		Map<GraphNode, Double> coeff = Metrics.clusteringCoefficients(graph);

		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)coeff.get(v));
		Arrays.sort(nodeRanks);
			
		ranks = nodeRanks;
		return nodeRanks;
	}
	
	
	//Vertices i and j are structurally equivalent iff the set of i's neighbors is identical to the set of j's neighbors, with the exception of i and j themselves.
	public Map<String, GraphNode[]> findStructurallyEQ (Graph<GraphNode, GraphEdge> graph)
	{
		StructurallyEquivalent<GraphNode, GraphEdge> equivalents = new StructurallyEquivalent<GraphNode, GraphEdge>();
		VertexPartition<GraphNode, GraphEdge> equiv = equivalents.transform(graph);
		groups = equiv.getVertexPartitions();
		
		HashMap<String, GraphNode[]> resultMap = new HashMap<String, GraphNode[]>();
		int groupNum = 0;
		
		GraphNode[] oneSet;
		for(Set<GraphNode> eqSet: groups)
		{
			oneSet = new GraphNode[eqSet.size()];
			int i = 0;
			for(GraphNode gn : eqSet)
				oneSet[i++] = gn;
			resultMap.put("Group " + (groupNum++), oneSet);
		}
		return resultMap;
	}
	
	public double findDensity(Network network)
	{
		double sum = 0;
		for(int i = 0; i < network.getRow(); i++)
			for(int j = 0; j < network.getColumn(); j++)
				sum += network.getMatrix()[i][j];
		
		return (sum/(network.getColumn()*network.getRow()));
	}

	public NodeMetric[] findOrgMeasure(Graph<GraphNode, GraphEdge> graph)
	{
		System.out.println("Calculating betweeness centrality");
		BetweennessCentrality<GraphNode, GraphEdge> betweenessRanker = new BetweennessCentrality<GraphNode, GraphEdge>(graph, true, false);
		betweenessRanker.setRemoveRankScoresOnFinalize(false);
		betweenessRanker.evaluate();
		NodeMetric[] betweenessnodeRanks = new NodeMetric[graph.getVertexCount()];
		
		int i = 0;
		for(GraphNode v: graph.getVertices())
			betweenessnodeRanks[i++] = new NodeMetric(v.getLabel(), betweenessRanker.getVertexRankScore(v));
		
		Arrays.sort(betweenessnodeRanks);
		betweenessnodeRanks=normalizeValues(betweenessnodeRanks,graph);
		double betweenessOrg=findOrg(betweenessnodeRanks);
		
		System.out.println("Calculating degree centrality");
		DegreeScorer<GraphNode> degreeRanker = new DegreeScorer<GraphNode>(graph);
		NodeMetric[] degreenodeRanks = new NodeMetric[graph.getVertexCount()];
		i = 0;
		for(GraphNode v: graph.getVertices())
			degreenodeRanks[i++] = new NodeMetric(v.getLabel(), degreeRanker.getVertexScore(v));
		Arrays.sort(degreenodeRanks);
		degreenodeRanks=normalizeValues(degreenodeRanks,graph);
		double degreeOrg=findOrg(degreenodeRanks);
		
		System.out.println("Calculating closeness centrality");
		ClosenessCentrality<GraphNode, GraphEdge> closenessRanker = new ClosenessCentrality<GraphNode, GraphEdge>(graph);
		NodeMetric[] closenessnodeRanks = new NodeMetric[graph.getVertexCount()];
		 i = 0;
		for(GraphNode v: graph.getVertices())
			closenessnodeRanks[i++] = new NodeMetric(v.getLabel(), closenessRanker.getVertexScore(v));
		Arrays.sort(closenessnodeRanks);
		double closeneessOrg=findOrg(closenessnodeRanks);
		
		System.out.println("Calculating authority centrality");
		HITS<GraphNode, GraphEdge> authorityRanker=new HITS<GraphNode, GraphEdge> (graph);
		authorityRanker.evaluate();

		NodeMetric[] authoritynodeRanks = new NodeMetric[graph.getVertexCount()];
		i = 0;
		for(GraphNode v: graph.getVertices())
			authoritynodeRanks[i++] = new NodeMetric(v.getLabel(), (Double)authorityRanker.getVertexScore(v).authority);
		Arrays.sort(authoritynodeRanks);
		double authorityOrg=findOrg(authoritynodeRanks);
		
		double orgSum=authorityOrg+closeneessOrg+degreeOrg+betweenessOrg;
		orgSum=orgSum/10;
		
		NodeMetric returnMetric= new NodeMetric("ORG VALUE", orgSum);
		NodeMetric[]returnOrg=new NodeMetric[1];
		returnOrg[0]=returnMetric;
		
		return returnOrg; 

	}

	private double findOrg(NodeMetric[] nodeRanks) {
		double result=0.0;
		Arrays.sort(nodeRanks, Collections.reverseOrder());
		ArrayList<ArrayList<String>> groups=new ArrayList<ArrayList<String>>();

		for (int i = 0; i < nodeRanks.length-1; i++) {
			double xi=nodeRanks[i].getMetricValue();
			double xii=nodeRanks[i+1].getMetricValue();
			if(xii-xi<0.1){
				boolean existsinArray=false;
				for(ArrayList<String> testArrays:groups){
					if(testArrays.contains(nodeRanks[i].getNodeName())){
						existsinArray=true;
						testArrays.add(nodeRanks[i+1].getNodeName());
					}
				}
				if(!existsinArray){
					ArrayList<String> myList=new ArrayList<String>();
					myList.add(nodeRanks[i].getNodeName());
					myList.add(nodeRanks[i+1].getNodeName());
					groups.add(myList);
				}
			}
		}
		int weight =1;
		for(ArrayList<String> nodeGroups:groups){
			result+=nodeGroups.size()*weight;
			weight++;
		}
		result=result/(double)groups.size();
		return result;
	}

	private NodeMetric[] normalizeValues(NodeMetric[] ranks, Graph<GraphNode, GraphEdge> graph) {
		NodeMetric[] newRanks=new NodeMetric[graph.getVertexCount()];
		double maxValue=ranks[0].getMetricValue();
		int i = 0;
		for(NodeMetric rank :ranks){
			newRanks[i] = new NodeMetric(rank.getNodeName(), (Double)ranks[i].getMetricValue()/maxValue);
			i++;
			}
		
		return newRanks;
	}
	
}
