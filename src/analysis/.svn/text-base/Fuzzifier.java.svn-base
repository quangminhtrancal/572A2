package analysis;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.jgap.*;

import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import data.NodeMetric;

import edu.uci.ics.jung.graph.Graph;
import fuzzy.FuzzySet;
import fuzzy.clustering.Cluster;
import fuzzy.clustering.MOGAClustering;

public class Fuzzifier {
	double[] initialData; // input
	double fuzzy_condition; // input
	double[] fuzzifiedData; // output
	FuzzySet[] fuzzySets;
	double fuzzy_cond_value;
	Fuzzifier BetweennessFuzzifier;
	Fuzzifier ClosenessFuzzifier;
	Fuzzifier EigenvectorFuzzifier;
	Fuzzifier DegreeFuzzifier;

	public void doFuzzifier(NetworkGraph netGraph, double[] threshold) {
		// 1. For each fuzzy criterion calculate degree of membership to the
		// specified fuzzy set
		Graph<GraphNode, GraphEdge> graph = netGraph.getGraph();
		MetricFinder mf = new MetricFinder();
		NodeMetric[] metric = mf.findBetweenness(graph);
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File("fuzzy_sets.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double[] betweenness = new double[metric.length];
		for (int i = 0; i < metric.length; i++)
			betweenness[netGraph.findNode(metric[i].getNodeName()).getID()] = metric[i].getMetricValue();

		metric = mf.findCloseness(graph);
		double[] closeness = new double[metric.length];
		for (int i = 0; i < metric.length; i++)
			closeness[netGraph.findNode(metric[i].getNodeName()).getID()] = metric[i].getMetricValue();

		metric = mf.findDegree(graph);
		double[] degree = new double[metric.length];
		for (int i = 0; i < metric.length; i++)
			degree[netGraph.findNode(metric[i].getNodeName()).getID()] = metric[i].getMetricValue();

		metric = mf.findEigenvector(graph);
		double[] eigenvector = new double[metric.length];
		for (int i = 0; i < metric.length; i++)
			eigenvector[netGraph.findNode(metric[i].getNodeName()).getID()] = metric[i].getMetricValue();

		// 1.1. First Criterion (Betweenness)
		BetweennessFuzzifier = new Fuzzifier(betweenness, threshold[0]);
		BetweennessFuzzifier.MOGAFuzzification();
		BetweennessFuzzifier.MapDataToDegreeOfMembership(BetweennessFuzzifier.FindSpecifiedFuzzySet());
		BetweennessFuzzifier.writeToFile(fw, "Betweenness");
		
		
		// 1.2. Second Criterion (Closeness)
		ClosenessFuzzifier = new Fuzzifier(closeness, threshold[1]);
		ClosenessFuzzifier.MOGAFuzzification();
		ClosenessFuzzifier.MapDataToDegreeOfMembership(ClosenessFuzzifier.FindSpecifiedFuzzySet());
		ClosenessFuzzifier.writeToFile(fw, "closeness");

		// 1.3. Second Criterion (Eigenvector)
		EigenvectorFuzzifier = new Fuzzifier(eigenvector, threshold[2]);
		EigenvectorFuzzifier.MOGAFuzzification();
		EigenvectorFuzzifier.MapDataToDegreeOfMembership(EigenvectorFuzzifier.FindSpecifiedFuzzySet());
		EigenvectorFuzzifier.writeToFile(fw, "eigenvector");

		// 1.4. Second Criterion (Degree)
		DegreeFuzzifier = new Fuzzifier(degree, threshold[3]);
		DegreeFuzzifier.MOGAFuzzification();
		DegreeFuzzifier.MapDataToDegreeOfMembership(DegreeFuzzifier.FindSpecifiedFuzzySet());
		DegreeFuzzifier.writeToFile(fw, "degree");

		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 2. The final results is the min of all fuzzifiedData
		NodeMetric[] nodeRanks = new NodeMetric[graph.getVertexCount()];
		
		int i = 0;
		for(GraphNode v: graph.getVertices())
			nodeRanks[i++] = new NodeMetric(v.getLabel(), 0);
		
		System.out.println("Ranking Scores:");
		for (i = 0; i < betweenness.length; i++) {
			nodeRanks[i].setMetricValue(Math.min(Math.min(
					BetweennessFuzzifier.fuzzifiedData[i],
					ClosenessFuzzifier.fuzzifiedData[i]), Math.min(
					EigenvectorFuzzifier.fuzzifiedData[i],
					DegreeFuzzifier.fuzzifiedData[i])));
			System.out.print(nodeRanks[i].getMetricValue() +"\t");
		}

		Arrays.sort(nodeRanks);
		
		// 3. nodes in the graph should be visually sorted based on RankingScore
		visualizeRanks(netGraph, nodeRanks);

	}

	public void writeToFile(FileWriter fw, String title)
	{
		try {
			fw.append("\n"+ title+"\n");
			for(int i = 0; i < fuzzySets.length; i++)
				fw.append(fuzzySets[i]+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void visualizeRanks(NetworkGraph netGraph, NodeMetric[] ranks)
	{
		float colTemp = (float) 1.0;
		int num = 0; //number of nodes with degree of membership greater than zero
		for(int i = 0; i < ranks.length; i++)
			if(ranks[i].getMetricValue() != 0)
				num ++;
			else
				break;
		
		for(int i = 0; i < num; i++)
		{
			netGraph.findNode(ranks[i].getNodeName()).setType(3);
			netGraph.findNode(ranks[i].getNodeName()).setColor(Color.getHSBColor(0, colTemp, 1));
			colTemp = colTemp - (float)1.0 / (num+2);
		}
		for(int i = num; i < ranks.length; i++)
		{
			netGraph.findNode(ranks[i].getNodeName()).setType(3);
			netGraph.findNode(ranks[i].getNodeName()).setColor(Color.white);
		}
	}
	
	public Vector<Integer> setRankingVector(double[] score) {
		Vector<Integer> ranking = new Vector<Integer>();
		for (int j = 0; j < score.length; j++) {
			double maximum = score[0];
			int maxId = 1;
			for (int i = 1; i < score.length; i++) {
				if (score[i] > maximum) {
					maximum = score[i]; // new maximum
					maxId = i + 1;
				}
			}
			score[maxId - 1] = -1;
			ranking.add(maxId);
			System.out.print(maxId + ", ");
		}
		return ranking;
	}

	public Fuzzifier(double[] initialData, double fuzzy_condition) {
		this.initialData = initialData;
		this.fuzzy_condition = fuzzy_condition;
	}
	
	public Fuzzifier()
	{
		
	}

	@SuppressWarnings("unchecked")
	public void MOGAFuzzification() {
		// use initialData and fill fuzzySets
		MOGAClustering a_MOGAClustering = new MOGAClustering(initialData);
		List<IChromosome> chroms = a_MOGAClustering.getClusterParetoOptimalSolutions();
		Cluster[] myClusts = a_MOGAClustering.getBestClusterSolution();
		fuzzy.fuzzification.MOGAFuzzification a_MOGAFuzzification = new fuzzy.fuzzification.MOGAFuzzification(
				a_MOGAClustering, initialData);
		fuzzySets = a_MOGAFuzzification.getBestFuzzySetsSolution(chroms);
	}

	public FuzzySet FindSpecifiedFuzzySet() {
		// use fuzzy_condition and find the fuzzyset with highest degree of
		// membership
		double min = initialData[0];
		double max = initialData[0];
		for (int i = 1; i < initialData.length; i++)
			if (initialData[i] < min)
				min = initialData[i];
		for (int i = 1; i < initialData.length; i++)
			if (initialData[i] > max)
				max = initialData[i];

		fuzzy_cond_value = (max - min) * fuzzy_condition + min;
		int specifiedFuzzySetIndex = 0;
		double maxDofM = fuzzySets[0]
				.calculateDegreeOfMembership(fuzzy_cond_value);
		for (int i = 1; i < fuzzySets.length; i++)
			if (fuzzySets[i].calculateDegreeOfMembership(fuzzy_cond_value) > maxDofM) {
				maxDofM = fuzzySets[i]
						.calculateDegreeOfMembership(fuzzy_cond_value);
				specifiedFuzzySetIndex = i;
			}
		return fuzzySets[specifiedFuzzySetIndex];
	}

	public void MapDataToDegreeOfMembership(FuzzySet specifiedFuzzySet) {
		fuzzifiedData = new double[initialData.length];
		// map initialData to DofM to the specified fuzzyset and fill
		// fuzzifiedData
		for (int i = 0; i < initialData.length; i++)
			fuzzifiedData[i] = specifiedFuzzySet
					.calculateDegreeOfMembership(initialData[i]);
	}
}
