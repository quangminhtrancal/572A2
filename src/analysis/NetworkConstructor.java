package analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import MOGAClustering.MOGANetworkConstructor;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import data.Dataset;
import data.Network;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.util.KMeansClusterer;

public class NetworkConstructor {
	
	public NetworkGraph frequentMiner(Dataset rawData) {
		String userInput = JOptionPane.showInputDialog("Enter the minimum support: ");

		if (userInput == null) 
			return null;

		int minsup = 0;

		try {
			minsup = Integer.valueOf(userInput);
		} catch (Exception e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame, "Invalid value for the minimum support.");
			return null;
		}

		FrequentPatternMiner fpm = new FrequentPatternMiner(null,rawData.getMatrix(), minsup);

		double[][] fm = fpm.generateFrequentPatternNetwork();

		Network network = new Network(fm, rawData.getFeatureNum(), rawData.getHeaders(),1);
		NetworkGraph netGraph = new NetworkGraph(network);
		
		return netGraph;
	}
	
	public NetworkGraph frequentClosedMiner(Dataset rawData) {
		String userInput = JOptionPane.showInputDialog("Enter the minimum support: ");

		if (userInput == null) 
			return null;

		int minsup = 0;

		try {
			minsup = Integer.valueOf(userInput);
		} catch (Exception e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame,"Invalid value for the minimum support.");
			return null;
		}

		FrequentPatternMiner fpm = new FrequentPatternMiner(null, rawData.getMatrix(), minsup);

		double[][] fm = fpm.generateClosedFrequentPatternNetwork();

		Network network = new Network(fm, rawData.getFeatureNum(), rawData.getHeaders(),1);
		NetworkGraph netGraph = new NetworkGraph(network);
		
		return netGraph;
	}
	
	public NetworkGraph frequentMaximalMiner(Dataset rawData) {
		String userInput = JOptionPane.showInputDialog("Enter the minimum support: ");

		if (userInput == null) 
			return null;

		int minsup = 0;

		try {
			minsup = Integer.valueOf(userInput);
		} catch (Exception e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame,"Invalid value for the minimum support.");
			return null;
		}

		FrequentPatternMiner fpm = new FrequentPatternMiner(null, rawData.getMatrix(), minsup);

		double[][] fm = fpm.generateMaximalFrequentPatternNetwork();

		Network network = new Network(fm, rawData.getFeatureNum(), rawData.getHeaders(),1);
		NetworkGraph netGraph = new NetworkGraph(network);
		
		return netGraph;
	}
	
	public NetworkGraph kmeansClustering(Dataset rawData) {
		String message = "Enter the values of k in k-means clustering"
				+ "\n Type values separated by comma or ranges using hyphen."
				+ "\n For example, type 3,4,6 or 2-5";
		String kValues = JOptionPane.showInputDialog(message);
		ArrayList<Integer> values = new ArrayList<Integer>();


		try {
			if (kValues.contains(",")) {
				String[] sp = kValues.split(",");
				for (int i = 0; i < sp.length; i++)
					values.add(Integer.parseInt(sp[i]));
			} else if (kValues.contains("-")) {
				int start = Integer.parseInt(kValues.split("-")[0]);
				int end = Integer.parseInt(kValues.split("-")[1]);
				if(start>=end){
					JOptionPane.showMessageDialog(null, "Start should be less than or equal to end!", "Alert",
							JOptionPane.WARNING_MESSAGE);
					return null;
				}
				if(end>rawData.getNumOfObjects()){

					JOptionPane.showMessageDialog(null, "Start should be less than or equal to end!", "Alert",
							JOptionPane.WARNING_MESSAGE);
					return null;
				}
				for (int i = start; i < end + 1; i++)
					values.add(i);
			}else{
				if(Integer.parseInt(kValues)>rawData.getNumOfObjects()){
					JOptionPane.showMessageDialog(null, "The number of clusters should be less than or equal to the number of graph nodes ("+rawData.getNumOfObjects()+") !", "Alert",
							JOptionPane.WARNING_MESSAGE);
					return null;
				}
				values.add(Integer.parseInt(kValues));
			}
		} catch (NumberFormatException e) {
			JFrame frame = new JFrame();
			JOptionPane.showMessageDialog(frame,
					"Invalid input for values of k.");
			return null;
		}
		DoubleMatrix2D result = new SparseDoubleMatrix2D(rawData.getNumOfObjects(), rawData.getNumOfObjects());
		for(int i = 0; i < rawData.getNumOfObjects(); i++)
		{
			for(int j = 0; j < rawData.getNumOfObjects(); j++)
			{
				result.setQuick(i, j, 0);
			}
		}
		for (int i = 0; i < values.size(); i++) {
			cluster(values.get(i), rawData, result);
		}

		double[][] matrix = new double[rawData.getNumOfObjects()][rawData.getNumOfObjects()];
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
				if(i == j)
					matrix[i][j] = 0;
				else
					matrix[i][j] = result.getQuick(i, j)/(double)values.size();
		
		Network network = new Network(matrix, rawData.getNumOfObjects(), rawData.getHeaders(), 2);
		NetworkGraph netGraph = new NetworkGraph(network);
		
		return netGraph;
	}
	
	private void cluster(int k, Dataset rawData, DoubleMatrix2D matrix)
	{
		KMeansClusterer<Integer> clusterer = new KMeansClusterer<Integer>();
		HashMap<Integer, double[]> maps = new HashMap<Integer, double[]>();
		for (int i = 0; i < rawData.getNumOfObjects(); i++)
			maps.put(i, rawData.getRow(i));
		Collection<Map<Integer, double[]>> result =(Collection<Map<Integer, double[]>>) clusterer.cluster(maps, k);

		for(Map<Integer, double[]> oneCluster: result)
		{
			for(Integer y: oneCluster.keySet())
				for(Integer z: oneCluster.keySet())
					matrix.setQuick(y, z, matrix.get(y, z)+1);
		}
	}
	
	public NetworkGraph GAClustering(Dataset rawData) {
		MOGANetworkConstructor ganc = new MOGANetworkConstructor(rawData.getMatrix());
		Network network = new Network(ganc.createNetwork(), rawData.getNumOfObjects(), rawData.getHeaders(), 2);
		return new NetworkGraph(network);
	}
	
}
