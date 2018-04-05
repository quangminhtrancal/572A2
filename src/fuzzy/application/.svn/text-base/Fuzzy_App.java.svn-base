package fuzzy.application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgap.IChromosome;

import fuzzy.FuzzySet;
import fuzzy.clustering.Cluster;
import fuzzy.clustering.IntRandomRange;
import fuzzy.clustering.MOGAClustering;
import fuzzy.fuzzification.MOGAFuzzification;

public class Fuzzy_App {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Fuzzy_App a_FuzzyApp = new Fuzzy_App();
		InputOutputFile myInOut = new InputOutputFile(args);
		myInOut.fileName2 = args[0];
		myInOut.inputDouDataSet();
		
		for (int i=0; i<myInOut.douDataArray.length; i++)
		{
			for (int j=0; j<myInOut.douDataArray[i].length; j++)
			{
				System.out.print(myInOut.douDataArray[i][j]);
				System.out.print(" ");
			}
		System.out.println();
		}		
		double[] data = new double[myInOut.douDataArray.length];

		for (int i=0; i<myInOut.douDataArray.length; i++)
			data[i] = myInOut.douDataArray[i][0];
		
//		double[] data = a_FuzzyApp.generateRandomData();
//		a_FuzzyApp.generateInitialClusters(data);
//		a_FuzzyApp.generateOpptimizedFuzzySets(a_FuzzyApp.ArrayRemoveDuplicate(data), "C://QueryBuilderTemp//cluster.txt", "C://QueryBuilderTemp//FS.XML");
		a_FuzzyApp.generateOpptimizedFuzzySets(a_FuzzyApp.ArrayRemoveDuplicate(data), 3, "C://QueryBuilderTemp//FS.XML");
//		a_FuzzyApp.generateOpptimizedFuzzySets(data, 3, "C://QueryBuilderTemp//FS.XML");
	}

	public void generateInitialClusters(double[] data)
	{
		MOGAClustering a_MOGAClustering = new MOGAClustering(data);
		Cluster[] myClusts = a_MOGAClustering.generateBestClusterSolution();
		a_MOGAClustering.printClusterSolution(myClusts,"C://QueryBuilderTemp//cluster.txt");
	}
	public void generateInitialClusters(double[] data,String filename)
	{
		MOGAClustering a_MOGAClustering = new MOGAClustering(data);
		Cluster[] myClusts = a_MOGAClustering.generateBestClusterSolution();
		a_MOGAClustering.printClusterSolution(myClusts,filename);
	}
	
	public FuzzySet[] generateOpptimizedFuzzySets(double[] data,String clust_filename,String fs_filename)
	{
		MOGAClustering a_MOGAClustering = new MOGAClustering(data);
		List<IChromosome> chroms = a_MOGAClustering.getClusterParetoOptimalSolutions();
		Cluster[] myClusts = a_MOGAClustering.getBestClusterSolution();
		a_MOGAClustering.printClusterSolution(myClusts,clust_filename);
		MOGAFuzzification a_MOGAFuzzification = new MOGAFuzzification(a_MOGAClustering,data);
		FuzzySet[] myFS = a_MOGAFuzzification.getBestFuzzySetsSolution(chroms);
		a_MOGAFuzzification.outFuzzySetsToXML(myFS, fs_filename);
		return myFS;
	}
	public FuzzySet[] generateOpptimizedFuzzySets(double[] data, int numFuzzySets,String fs_filename)
	{
		MOGAClustering a_MOGAClustering = new MOGAClustering(data);
		MOGAFuzzification a_MOGAFuzzification = new MOGAFuzzification(a_MOGAClustering,data);
		FuzzySet[] myFS = a_MOGAFuzzification.getBestFuzzySetsSolution(numFuzzySets);
		a_MOGAFuzzification.outFuzzySetsToXML(myFS, fs_filename);
		return myFS;
	}
	public double[] generateRandomData()
	{
		double[] AttValues = new double[150];
		IntRandomRange mIntRandomRange = new IntRandomRange();
		for (int idx=0;idx<AttValues.length;idx++)
		{
			AttValues[idx] = mIntRandomRange.generateRandomInteger(1, 100);
			log("Generated " + (idx+1) + ": " + AttValues[idx]);
		}
		return AttValues;
	}
	private static void log(String aMessage)
	{
		System.out.println(aMessage);
	}

	public double[] ArrayRemoveDuplicate(double[] myData)
	{
		System.out.println("Original array         : " + Arrays.toString(myData));

		//
		// Convert it to list as we need the list object to create a set object.
		// A set is a collection object that cannot have a duplicate values, so
		// by converting the array to a set the duplicate value will be removed.
		//
		Double[] data = new Double[myData.length];
		for (int i=0; i<data.length; i++)
			data[i] = Double.valueOf(myData[i]);
		
		List<Double> list = Arrays.asList(data);
		Set<Double> set = new HashSet<Double>(list);

		System.out.print("Remove duplicate result: ");

		//
		// Create an array to convert the Set back to array. The Set.toArray()
		// method copy the value in the set to the defined array.
		//
		Double[] result = new Double[set.size()];

		set.toArray(result);
		for ( Double s : result) 
		{
			System.out.print(s + ", ");
		}
		double[] myResult = new double[result.length];
		for (int i=0; i<myResult.length; i++)
			myResult[i] = result[i];
		return myResult;
	}
}