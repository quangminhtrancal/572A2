package fuzzy.clustering;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.IChromosomePool;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.BooleanGene;
import org.jgap.impl.ChromosomePool;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.*;
import org.jgap.impl.*;


import java.util.*;


public class MOGAClustering
{

	private class Center{
		int index;
		double value;
		double potential;
		
		public Center()
		{
		}
	}

	public class MOFitnessComparator
	implements java.util.Comparator 
	{

		public int compare(final Object a_chrom1, final Object a_chrom2) 
		{
			List v1 = ( (Chromosome) a_chrom1).getMultiObjectives();
			List v2 = ( (Chromosome) a_chrom2).getMultiObjectives();
			int size = v1.size();
			if (size != v2.size()) {
				throw new RuntimeException("Size of objectives inconsistent!");
			}
			boolean better1 = false;
			boolean better2 = false;
			for (int i = 0; i < size; i++) {
				double d1 = ( (Double) v1.get(i)).doubleValue();
				double d2 = ( (Double) v2.get(i)).doubleValue();
				if (d1 < d2) {
					better1 = true;
				}
				else if (d2 < d1) {
					better2 = true;
				}
			}
			if (better1) {
				if (better2) {
					return 0;
				}
				else {
					return 1;
				}
			}
			else {
				if (better2) {
					return -1;
				}
				else {
					return 0;
				}
			}
		}

		public int equal(IChromosome a_chrom1, IChromosome a_chrom2)
		{
			Gene[] geneSet1 = a_chrom1.getGenes();
			Gene[] geneSet2 = a_chrom2.getGenes();
			for (int i=0; i<geneSet1.length; i++)
			{
				if (geneSet1[i].getAllele() != geneSet2[i].getAllele())
					return 0;
			}
			return 1;

		}	
	}	

	public MOGAClustering()
	{
		log("Generating 50 random integers for an attribute...");
		double[] AttValues = new double[150];
		IntRandomRange mIntRandomRange = new IntRandomRange();
		for (int idx=0;idx<AttValues.length;idx++)
		{
			AttValues[idx] = mIntRandomRange.generateRandomInteger(1, 100);
			log("Generated " + (idx+1) + ": " + AttValues[idx]);
		}

		setAttValues(AttValues);
//		log("Upper bound for Num of Clusters: " + myList.size());
	}
	public MOGAClustering(double[] data)
	{
		setAttValues(data);
//		log("Upper bound for Num of Clusters: " + myList.size());
	}
	/** GA parameters
	 * 
	 */
	private Configuration m_GAConf;

	private FitnessFunction m_FitnessFunction;

	private Population m_Pop;

	private static Genotype m_Genotype;

	private int m_PopulationSize = 20;
	
	private int m_BestNumOfClusters;

	public int getM_BestNumOfClusters() {
		return m_BestNumOfClusters;
	}
	public void setM_BestNumOfClusters(int bestNumOfClusters) {
		m_BestNumOfClusters = bestNumOfClusters;
	}
	/**
	 * The total number of times we'll let the population evolve.
	 */
	private static final int MAX_ALLOWED_EVOLUTIONS = 20;

	/**
	 * holds the attribute values to be clustered
	 */
	private double[] m_AttValues;

	/**
	 * max number of clusters to generate
	 */
	private int m_MaxNumClusters;

	/**
	 * number of clusters to generate
	 */
	private int m_MinNumClusters = 2;

	/**
	 * holds the cluster centroids
	 */
	private double[] m_ClusterCentroids;

	/**
	 * Holds the standard deviations of the numeric attributes in each cluster
	 */
	private double[] m_ClusterStdDevs;

	/**
	 * The number of instances in each cluster
	 */
	private int [] m_ClusterSizes;

	/**
	 * Maximum number of iterations to be executed
	 */
	private int m_MaxIterations = 500;

	/**
	 * Keep track of the number of iterations completed before convergence
	 */
	private int m_Iterations = 0;

	/**
	 * Holds the squared errors for all clusters
	 */
	private double [] m_squaredErrors;

	/** the distance function used. */
	// protected DistanceFunction m_DistanceFunction = new EuclideanDistance();

	/**
	 * Preserve order of instances 
	 */
	private boolean m_PreserveOrder = false;

	/**
	 * Assignments obtained
	 */
	protected int[] m_Assignments = null;

	/** the default seed value */
	protected int m_SeedDefault = 1;


	// Data structures

	/** Class defining details of an interval.  */

	/**
	 * @param args
	 * @throws Exception 
	 */
	public Cluster[] generateBestClusterSolution()
	{
		generateParetoOptimalSolutions();
		removeDuplicateParetoOptimalSolutions();
		return popClustersFromChrom(findBestSolution(m_Genotype, m_AttValues), m_AttValues);
	}
//	public Cluster[] getBestClusterSolution()
//	{
//		return popClustersFromChrom(findBestSolution(m_Genotype, m_AttValues), m_AttValues);
//	}
	public Cluster[] getBestClusterSolution()
	{
		Cluster[] tmpCluster;
		Cluster[] finalCluster;
		tmpCluster = popClustersFromChrom(findBestSolution(m_Genotype, m_AttValues), m_AttValues);
		double[][] data = new double[m_AttValues.length][];
		for (int i=0; i<m_AttValues.length; i++)
		{
			data[i] = new double[1];
			data[i][0] = m_AttValues[i];
		}	
		AdaptedKMeans myKMeans = new AdaptedKMeans(data,tmpCluster,tmpCluster.length,500,99999); 
		myKMeans.runWithInitialClustersAssigned();
		KMeansCluster[] newCluster = myKMeans.getClusters();
		finalCluster = new Cluster[newCluster.length];
		for (int i=0; i<newCluster.length; i++)
		{
			finalCluster[i] = new Cluster();
			finalCluster[i].setLabel(i);
			finalCluster[i].setCenter(newCluster[i].getCenter()[0]);
			finalCluster[i].m_Data = new double[newCluster[i].getMemberIndexes().length];
			finalCluster[i].setNumElements(newCluster[i].getMemberIndexes().length);
			for (int j=0; j<finalCluster[i].m_Data.length; j++)
			{
				finalCluster[i].m_Data[j] = m_AttValues[newCluster[i].getMemberIndexes()[j]];
			}
		}
		return finalCluster;
	}
	public List getClusterParetoOptimalSolutions()
	{
		generateParetoOptimalSolutions();
		removeDuplicateParetoOptimalSolutions();		
		Cluster[] bestClusterSolution = popClustersFromChrom(findBestSolution(m_Genotype, m_AttValues), m_AttValues);
		m_BestNumOfClusters = bestClusterSolution.length;

		List chroms = m_Genotype.getPopulation().getChromosomes();
		List myChroms = new ArrayList(chroms);
		
		for (int i=0;i<myChroms.size();i++)
		{
			System.out.println("Pareto Solution " + (i+1) + ": ");
			printClusterSolution((IChromosome)myChroms.get(i), m_AttValues);
			System.out.println();
		}	
		return myChroms;
	}
	public void printClusterSolution(IChromosome a_Chrom, double[] AttValues)
	{
		Cluster[] tmpCluster = popClustersFromChrom(a_Chrom, AttValues);
		for (int j=0; j<tmpCluster.length; j++)
		{
			tmpCluster[j].printCluster();
		}
	}
	public void printClusterSolution(Cluster[] myClusters)
	{
		for (int j=0; j<myClusters.length; j++)
		{
			myClusters[j].printCluster();
		}
	}	
	public void printClusterSolution(Cluster[] myClusters,String filename)
	{
		File outFile = new File(filename);
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(outFile));
			for (int j=0; j<myClusters.length; j++)
			{
				out.print("Cluster " + myClusters[j].getLabel() + ": ");
				for (int i=0; i<myClusters[j].getNumElements(); i++)
					out.print(myClusters[j].getData()[i] + "  ");			
				out.println();
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	public void printParetoOptimalSolutions()
	{
		// Print all Pareto-optimal solutions.
		// -----------------------------------
		List chroms = m_Genotype.getPopulation().getChromosomes();
		int size = m_Genotype.getPopulation().getChromosomes().size();

		if (size < m_PopulationSize && size != 0)
		{
			MOFitnessComparator comp = new MOFitnessComparator();

			Collections.sort(chroms, comp);
			for (int k=0;k<chroms.size();k++) {
				Chromosome bestSolutionSoFar = (Chromosome) chroms.get(k);
				System.out.println(MOGAClusteringFitnessFunction.
						getVector(bestSolutionSoFar));
			}
			size = m_Genotype.getPopulation().getChromosomes().size();
			System.out.println("number of altenative solutions is (After Redundancy Elimination) : " + size);

			int i = 0;

			while (i<size) 
			{
				IChromosome a_Solution = m_Genotype.getPopulation().getChromosome(i);
				Gene[] genes = a_Solution.getGenes();
				for (int idx=0; idx<genes.length; idx++)
				{
					System.out.print(m_AttValues[idx] + " :" + genes[idx].getAllele() + " ");
				}
				System.out.println();
				i++;
			}	    	
		}
		else
			System.out.println("Pareto Optimal Solutions are not generated yet!");
	}	
	private void generateParetoOptimalSolutions()
	{
		try {
			initialPopulation();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Initializing Population is OVER...");
		for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) 
		{
			//System.out.println("------------- Begin ---- Evolvation "+ (i+1) + " -------------------------------");
			if (!uniqueChromosomes(m_Genotype.getPopulation())) 
			{
				throw new RuntimeException("Invalid state in generation "+i);
			}
			//myMokga.printPopulation();
			m_Genotype.evolve();

			//System.out.println("Fitness value of the best Chromosome in the current population: " + m_Genotype.getFittestChromosome().getFitnessValue());
			//System.out.println("Average Fitness value of the current population: " + myMokga.avgPopulationFitness(m_Genotype.getPopulation()));
			//System.out.println("------------- End ---- Evolvation "+ (i+1) + " -------------------------------");
		}
		removeNonParetoOptimalSolutions();		
	}
	private void generateParetoOptimalSolutions(boolean removeNonParetoFlag)
	{
		try {
			initialPopulation();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Initializing Population is OVER...");
		for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) 
		{
			//System.out.println("------------- Begin ---- Evolvation "+ (i+1) + " -------------------------------");
			if (!uniqueChromosomes(m_Genotype.getPopulation())) 
			{
				throw new RuntimeException("Invalid state in generation "+i);
			}
			//myMokga.printPopulation();
			m_Genotype.evolve();

			//System.out.println("Fitness value of the best Chromosome in the current population: " + m_Genotype.getFittestChromosome().getFitnessValue());
			//System.out.println("Average Fitness value of the current population: " + myMokga.avgPopulationFitness(m_Genotype.getPopulation()));
			//System.out.println("------------- End ---- Evolvation "+ (i+1) + " -------------------------------");
		}
		if (removeNonParetoFlag == true)
			removeNonParetoOptimalSolutions();		
	}
	private void removeNonParetoOptimalSolutions()
	{
		// Remove solutions that are not Pareto-optimal.
		// --------------------------------------------- 
		int size = m_Genotype.getPopulation().getChromosomes().size();
		int i = 0;
		boolean removed = false;
		MOFitnessComparator comp = new MOFitnessComparator();
		while (i<size-1) {
			IChromosome chrom1 = m_Genotype.getPopulation().getChromosome(i);
			int j = i + 1;
			while (j < size) {
				IChromosome chrom2 = m_Genotype.getPopulation().getChromosome(j);
				int res = comp.compare(chrom1, chrom2);
				if (res != 0) {
					if (res == -1) {
						m_Genotype.getPopulation().getChromosomes().remove(i);
						size--;
						removed = true;
						break;
					}
					else {
						m_Genotype.getPopulation().getChromosomes().remove(j);
						size--;
					}
				}
				else {
					j++;
				}
			}
			if (removed) {
				removed = false;
			}
			else {
				i++;
			}
		}

		size = m_Genotype.getPopulation().getChromosomes().size();
		System.out.println("number of altenative solutions is (Before Redundancy Elimination) : " + size);
	}
	private void removeDuplicateParetoOptimalSolutions()
	{

		// Remove duplicate solutions from Pareto-optimal solution set
		// ------------------------------------------------------------
		int size = m_Genotype.getPopulation().getChromosomes().size();
		int i = 0;
		boolean removed = false;
		MOFitnessComparator comp = new MOFitnessComparator();
		while (i<size-1) 
		{
			IChromosome chrom1 = m_Genotype.getPopulation().getChromosome(i);
			int j = i + 1;
			while (j < size) 
			{
				IChromosome chrom2 = m_Genotype.getPopulation().getChromosome(j);
				int res = comp.equal(chrom1, chrom2);
				if (res != 0) // Two chromosomes are the same. Eliminate one of them! 
				{
					m_Genotype.getPopulation().getChromosomes().remove(i);
					size--;
					removed = true;
					break;
				}
				else 
				{
					j++;
				}
			}

			if (removed) 
			{
				removed = false;
			}
			else 
			{
				i++;
			}

		}
	}

	public static void main(String[] args) throws Exception 
	{
		// TODO Auto-generated method stub
		MOGAClustering myMokga = new MOGAClustering();

		myMokga.initialPopulation();

		System.out.println("Initializing Population is OVER...");
		for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) 
		{
			//System.out.println("------------- Begin ---- Evolvation "+ (i+1) + " -------------------------------");
			if (!uniqueChromosomes(m_Genotype.getPopulation())) 
			{
				throw new RuntimeException("Invalid state in generation "+i);
			}
			//myMokga.printPopulation();
			m_Genotype.evolve();

			//System.out.println("Fitness value of the best Chromosome in the current population: " + m_Genotype.getFittestChromosome().getFitnessValue());
			//System.out.println("Average Fitness value of the current population: " + myMokga.avgPopulationFitness(m_Genotype.getPopulation()));
			//System.out.println("------------- End ---- Evolvation "+ (i+1) + " -------------------------------");
		}

		// Remove solutions that are not Pareto-optimal.
		// --------------------------------------------- 
		List chroms = m_Genotype.getPopulation().getChromosomes();
		int size = m_Genotype.getPopulation().getChromosomes().size();
		int i = 0;
		boolean removed = false;
		MOFitnessComparator comp = myMokga.new MOFitnessComparator();
		while (i<size-1) {
			IChromosome chrom1 = m_Genotype.getPopulation().getChromosome(i);
			int j = i + 1;
			while (j < size) {
				IChromosome chrom2 = m_Genotype.getPopulation().getChromosome(j);
				int res = comp.compare(chrom1, chrom2);
				if (res != 0) {
					if (res == -1) {
						m_Genotype.getPopulation().getChromosomes().remove(i);
						size--;
						removed = true;
						break;
					}
					else {
						m_Genotype.getPopulation().getChromosomes().remove(j);
						size--;
					}
				}
				else {
					j++;
				}
			}
			if (removed) {
				removed = false;
			}
			else {
				i++;
			}
		}

		size = m_Genotype.getPopulation().getChromosomes().size();
		System.out.println("number of altenative solutions is (Before Redundancy Elimination) : " + size);

		// Remove duplicate solutions from Pareto-optimal solution set
		// ------------------------------------------------------------
		chroms = m_Genotype.getPopulation().getChromosomes();
		size = m_Genotype.getPopulation().getChromosomes().size();
		i = 0;
		removed = false;
		while (i<size-1) 
		{
			IChromosome chrom1 = m_Genotype.getPopulation().getChromosome(i);
			int j = i + 1;
			while (j < size) 
			{
				IChromosome chrom2 = m_Genotype.getPopulation().getChromosome(j);
				int res = comp.equal(chrom1, chrom2);
				if (res != 0) // Two chromosomes are the same. Eliminate one of them! 
				{
					m_Genotype.getPopulation().getChromosomes().remove(i);
					size--;
					removed = true;
					break;
				}
				else 
				{
					j++;
				}
			}

			if (removed) 
			{
				removed = false;
			}
			else 
			{
				i++;
			}

		}


		// Print all Pareto-optimal solutions.
		// -----------------------------------
		Collections.sort(chroms, comp);
		for (int k=0;k<chroms.size();k++) {
			Chromosome bestSolutionSoFar = (Chromosome) chroms.get(k);
			System.out.println(MOGAClusteringFitnessFunction.
					getVector(bestSolutionSoFar));
		}
		size = m_Genotype.getPopulation().getChromosomes().size();
		System.out.println("number of altenative solutions is (After Redundancy Elimination) : " + size);

		i = 0;
		double[] myValues = myMokga.getAttValues();

		while (i<size) 
		{
			IChromosome a_Solution = m_Genotype.getPopulation().getChromosome(i);
			Gene[] genes = a_Solution.getGenes();
			for (int idx=0; idx<genes.length; idx++)
			{
				System.out.print(myValues[idx] + " :" + genes[idx].getAllele() + " ");
			}
			System.out.println();
			i++;
		}
		IChromosome bestSolution = myMokga.findBestSolution(m_Genotype, myMokga.getAttValues());
	}

	public IChromosome findBestSolution(Genotype myGenotype, double[] myValues)
	{

		int size = myGenotype.getPopulation().getChromosomes().size();
		int i = 0;

		double[][] voteMatrix = new double[size][6];
		IChromosome a_Solution = null;
		while (i<size) 
		{
			a_Solution = myGenotype.getPopulation().getChromosome(i);
			Cluster[] bestClsuterSolutionCandidate = popClustersFromChrom(a_Solution, myValues);

			// SD Index: the number of clusters that minimizes the index is an optimal value.
			voteMatrix[i][0] = calculateSD(bestClsuterSolutionCandidate);

			// S_Dbw Index: the number of clusters that minimizes the index is an optimal value.
			voteMatrix[i][1] = calculateS_Dbw(bestClsuterSolutionCandidate);

			// Dunn Index: the number of clusters that maximizes Dunn is taken as the optimal number of clusters.
			voteMatrix[i][2] = (-1) * Dunn(bestClsuterSolutionCandidate);

			// DB Index: when it has a small value it exhibits a good clustering.
			voteMatrix[i][3] = DB(bestClsuterSolutionCandidate);

			// Silhouette Index: the silhouette value is in the interval [–1, 1]; 
			// a value close to 1 means that the sample is assigned to a very appropriate cluster.
			voteMatrix[i][4] = (-1) * Silhouette(bestClsuterSolutionCandidate);

			// C Index: a small value of C indicates a good clustering.
			voteMatrix[i][5] = CIndex(bestClsuterSolutionCandidate);
			i++;
		}	    

		for (int k=0; k<voteMatrix.length; k++)
		{
			for (int j=0; j<voteMatrix[k].length; j++)
			{
				System.out.print(voteMatrix[k][j] + " ");
			}
			System.out.println();
		}

		// 6 is the number of validity indexes
		int[] bestSolutionIdxPerValidityIdx = new int[6];
		for (int idx=0; idx<bestSolutionIdxPerValidityIdx.length;idx++)
			bestSolutionIdxPerValidityIdx[idx]=0;

		for (int idxC=0; idxC<voteMatrix[0].length; idxC++)
		{
			for (int idxR=0; idxR<voteMatrix.length; idxR++)
			{
				if (voteMatrix[idxR][idxC] < voteMatrix[bestSolutionIdxPerValidityIdx[idxC]][idxC])
					bestSolutionIdxPerValidityIdx[idxC] = idxR;
			}
		}

		int[] numOfVotesPerSolutionIdx = new int[voteMatrix.length];

		for (int idx=0; idx<numOfVotesPerSolutionIdx.length; idx++)
			numOfVotesPerSolutionIdx[idx] = 0;

		for (int idx=0; idx<bestSolutionIdxPerValidityIdx.length; idx++)
			numOfVotesPerSolutionIdx[bestSolutionIdxPerValidityIdx[idx]] = numOfVotesPerSolutionIdx[bestSolutionIdxPerValidityIdx[idx]] + 1;

		int solutionIdxWithMaxVote = 0;

		for (int idx=0; idx<numOfVotesPerSolutionIdx.length; idx++)
			if (numOfVotesPerSolutionIdx[idx] > numOfVotesPerSolutionIdx[solutionIdxWithMaxVote])
				solutionIdxWithMaxVote = idx; 


		return myGenotype.getPopulation().getChromosome(solutionIdxWithMaxVote);
	}

	private double avgPopulationFitness(Population aPop)
	{
		double sum = 0;
		for(int i=0;i<aPop.size();i++)
		{
			//System.out.print("CH " + (i+1) + ": ");
			//for (int j=0;j<aPop.getChromosome(i).size();j++)
			//System.out.print(aPop.getChromosome(i).getGene(j).getAllele().toString() + " ");
			//System.out.println();
			//System.out.print("CH " + (i+1) + " fitness value: " + aPop.getChromosome(i).getFitnessValue() + " ");
			sum = sum + aPop.getChromosome(i).getFitnessValue();
			//System.out.println();
		}
		//System.out.println();
		return sum/aPop.size();
	}

	public double[] getAttValues()
	{
		return m_AttValues;
	}
	public void setAttValues(double[] attValues)
	{
		m_AttValues = attValues;
	}
	public int getMaxNumClusters()
	{
		return m_MaxNumClusters;
	}
	public void setMaxNumClusters(int maxNumClusters)
	{
		m_MaxNumClusters = maxNumClusters;
	}

	private int getNumIntervals()
	{
		double[] temp;
		temp = m_AttValues.clone();
		double sum = 0;
		double tempVal = 0;

		for (int x = 0; x < temp.length-1; x++)
		{
			for (int y = x+1; y < temp.length; y++)
			{
				if (temp[x] > temp[y])
				{ 
					tempVal = temp[x];
					temp[x] = temp[y];
					temp[y] = tempVal;
				}
			}
		}

		for (int index=1;index<temp.length;index++)
		{
			sum = sum + (temp[index] - temp[index-1]);
		}
		double avgIntervalRange = sum/(temp.length-1);
		System.out.println("Range for Intervals: " + avgIntervalRange);
		int numIntervals = 1;
		for (int index=1;index<temp.length;index++)
		{
			//System.out.println("(i): " + temp[index] + "             (i-1): " + temp[index-1]);
			if (temp[index] - temp[index-1]>avgIntervalRange)
				numIntervals = numIntervals + 1;
		}
		System.out.println("Number of Intervals: " + numIntervals);
		return numIntervals;
	}
	private void initialPopulation() throws Exception
	{
		double[] copyAttValues = m_AttValues.clone();
		List myList = new ArrayList();
		myList = subClust(copyAttValues, 0.3);

		Center a_Center = null;
		for (int idx=0; idx<myList.size(); idx++)
		{
			a_Center = (Center)myList.get(idx); 
			log(a_Center.index + " " + m_AttValues[a_Center.index]);

		}
		
		setMaxNumClusters(myList.size());
		
//		setMaxNumClusters(getNumIntervals());

		// Start with a DefaultConfiguration, which comes setup with the
		// most common settings.
		// -------------------------------------------------------------
		Configuration.reset();
		m_GAConf = new DefaultConfiguration();

		m_GAConf.removeNaturalSelectors(true);
		BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(
				m_GAConf, 0.95d);
		bestChromsSelector.setDoubletteChromosomesAllowed(true);
		m_GAConf.addNaturalSelector(bestChromsSelector, true);

		m_GAConf.reset();
		m_GAConf.setFitnessEvaluator(new MOGAClusteringFitnessEvaluator(m_AttValues));
		m_GAConf.setPreservFittestIndividual(false);
		m_GAConf.setKeepPopulationSizeConstant(false);

		// Set the fitness function we want to use, which is our
		// implemented FitnessFunction.
		// ---------------------------------------------------------
		BulkFitnessFunction m_FitnessFunction =
			new MOGAClusteringFitnessFunction(m_AttValues);
		m_GAConf.setBulkFitnessFunction(m_FitnessFunction);

		// Now we need to tell the Configuration object how we want our
		// Chromosomes to be setup. We do that by actually creating a
		// sample Chromosome and then setting it on the Configuration
		// object. We want our Chromosomes to each
		// have genes as many as the number of attribute values, one for each value. We want the
		// values (alleles) of those genes to be integers, which represent
		// the cluster to which the value belongs. We therefore use the
		// IntegerGene class to represent each of the genes. That class
		// also lets us specify a lower and upper bound, which we set
		// to sensible values for each cluster.
		// --------------------------------------------------------------
		IChromosome sampleChromosome = new Chromosome(m_GAConf, new BooleanGene(m_GAConf), m_AttValues.length);
		m_GAConf.setSampleChromosome(sampleChromosome);

		m_GAConf.setPopulationSize(m_PopulationSize);

		// Initialize the population with custom code
		m_Pop = new Population(m_GAConf,m_PopulationSize);

		generateInitialPopulation();
		//printPopulation();
	}
	private void generateInitialPopulation() throws Exception
	{
		IntRandomRange mIntRandomRange = new IntRandomRange();
		int anAllele;
		Gene[] newGenes;
		int numClusters;
		IChromosome chrom;
		int counter = 1;
		int anIndex = 0;

		for (int i=0;i< m_PopulationSize; i++)
		{
			// Constructing a new chromosome
			newGenes = new Gene[m_AttValues.length];
			//numClusters = mIntRandomRange.generateRandomInteger(m_MinNumClusters, m_MaxNumClusters);
			numClusters = m_MaxNumClusters;
			//log("Random generated Number of Clusters for this Chromosome: " + numClusters);


			// initialize the all Alleles to zero
			for (int geneIndex=0;geneIndex<newGenes.length;geneIndex++)
			{
				anAllele = 0;
				newGenes[geneIndex] = new IntegerGene(m_GAConf,0,0);
				newGenes[geneIndex].setAllele(anAllele);				
			}

			while (counter <= numClusters)
			{
				anIndex = mIntRandomRange.generateRandomInteger(0, newGenes.length-1);
				if (Integer.valueOf(newGenes[anIndex].getAllele().toString()) == 0)
				{
					anAllele = counter;
					newGenes[anIndex] = new IntegerGene(m_GAConf,counter,counter);
					newGenes[anIndex].setAllele(anAllele);				
					counter++;
				}
			}

			for (int geneIndex=0;geneIndex<newGenes.length;geneIndex++)
			{
				if (Integer.valueOf(newGenes[geneIndex].getAllele().toString()) == 0)
				{
					anAllele = mIntRandomRange.generateRandomInteger(1, numClusters);
					newGenes[geneIndex] = new IntegerGene(m_GAConf,1,numClusters);
					newGenes[geneIndex].setAllele(anAllele);
				}
				System.out.print(m_AttValues[geneIndex] + " :" + newGenes[geneIndex].getAllele() + " ");
			}
			System.out.println();
			// Initialize a new chromosome
			chrom = new Chromosome(m_GAConf);
			// Set generated genes for the new chromosome
			chrom.setGenes(newGenes);
			// Adding the new chromosome to the population
			m_Pop.addChromosome(chrom);
			//System.out.println("This chromosome fitness value is: " + m_FitnessFunction.getFitnessValue(chrom));
		}
		// Now we need to construct the Genotype. This could otherwise be
		// accomplished more easily by writing
		// "Genotype genotype = Genotype.randomInitialGenotype(...)"
		m_Genotype = new Genotype(m_GAConf,m_Pop);
	}
	private static void log(String aMessage)
	{
		System.out.println(aMessage);
	}
	public void printPopulation()
	{
		for (int i=0;i<m_PopulationSize;i++)
		{
			System.out.print("Chromosome " + (i+1) + " :");
			for (int j=0;j<m_AttValues.length;j++)
			{
				System.out.print(" " + m_Genotype.getPopulation().getChromosome(i).getGene(j).getAllele());			
			}
			System.out.println();
		}
	}
	/**
	 * @param a_pop the population to verify
	 * @return true if all chromosomes in the populationa are unique
	 *
	 * @author Klaus Meffert
	 * @since 3.3.1
	 */
	public static boolean uniqueChromosomes(Population a_pop) 
	{
		// Check that all chromosomes are unique
		for(int i=0;i<a_pop.size()-1;i++) {
			IChromosome c = a_pop.getChromosome(i);
			for(int j=i+1;j<a_pop.size();j++) {
				IChromosome c2 =a_pop.getChromosome(j);
				if (c == c2) {
					return false;
				}
			}
		}
		return true;
	}
	public static double getSD(Cluster[] a_solution)
	{
		return 0;
	}
	public Cluster[] popClustersFromChrom(IChromosome a_Chromosome, double[] myValues)
	{
		Cluster[] myClusters;
		int numClusters = 0;

		numClusters = getMaxLabelInChrom(a_Chromosome.getGenes());

		// A temporary array to store cluster elements
		double[][] temp = new double[numClusters][];
		int[][] elementIdx = new int[numClusters][];

		// Create an array of Cluster type
		myClusters = new Cluster[numClusters];

		// Instantiate Cluster objects
		for(int i=0;i<myClusters.length;i++)
			myClusters[i] = new Cluster();

		int clusterLabel;
		for (int geneIdx=0;geneIdx<a_Chromosome.size();geneIdx++)
		{
			clusterLabel = Integer.parseInt(a_Chromosome.getGene(geneIdx).getAllele().toString());
			myClusters[clusterLabel-1].setNumElements(myClusters[clusterLabel-1].getNumElements() + 1);
			if (clusterLabel == 0)
				System.out.println(" Cluster label is zero!");

			myClusters[clusterLabel-1].setLabel(clusterLabel);
		}
		int[] counter = new int[numClusters];

		for (int idx=0;idx<myClusters.length;idx++)
		{
			temp[idx] = new double[myClusters[idx].getNumElements()];
			elementIdx[idx] = new int[myClusters[idx].getNumElements()];
			if (temp[idx].length == 0)
				counter[idx] = 0;
		}
		// Retrieve elements of clusters by going through a loop over the chromosome and the values in parallel 
		for (int geneIdx=0;geneIdx<a_Chromosome.size();geneIdx++)
		{
			clusterLabel = Integer.parseInt(a_Chromosome.getGene(geneIdx).getAllele().toString());
			temp[clusterLabel-1][counter[clusterLabel-1]] = myValues[geneIdx];
			elementIdx[clusterLabel-1][counter[clusterLabel-1]++] = geneIdx;
		}
		double avgSTD = 0;
		// Set each Cluster data elements and Calculate its standard deviation
		for (int idx=0;idx<myClusters.length;idx++)
		{
			myClusters[idx].setData(temp[idx]);
			myClusters[idx].setCenter(myClusters[idx].centroid());
			myClusters[idx].setM_ElementIdx(elementIdx[idx]);
			avgSTD = avgSTD + myClusters[idx].standardDeviation(); 
		}
		return myClusters;	
	}
	public int getMaxLabelInChrom(Gene[] array)
	{
		int max=0,i;

		for (i = 0; i < array.length; i++)
		{
			while(Integer.parseInt(array[i].getAllele().toString()) > max)
			{
				max=Integer.parseInt(array[i].getAllele().toString());
			}
		}
		return max;
	}
	// The number of clusters that minimizes this validity index is an optimal value.
	private double calculateSD(Cluster[] myClust)
	{
		double Dis = 0;

		double DMax = Math.abs(myClust[0].centroid()- myClust[1].centroid());
		double DMin = Math.abs(myClust[0].centroid()- myClust[1].centroid());

		for (int i=0; i<myClust.length - 1; i++)
			for (int j=i+1; j<myClust.length; j++)
			{
				if (Math.abs(myClust[i].centroid()- myClust[j].centroid()) > DMax)
					DMax = Math.abs(myClust[i].centroid()- myClust[j].centroid());
				if (Math.abs(myClust[i].centroid()- myClust[j].centroid()) < DMin)
					DMin = Math.abs(myClust[i].centroid()- myClust[j].centroid());
			}	
		double sumRevCentroid = 0 ,tempSum = 0;

		for (int i=0; i<myClust.length; i++)
		{
			for (int j=0; j<myClust.length; j++)
				tempSum = tempSum + Math.abs(myClust[i].centroid()- myClust[j].centroid());
			sumRevCentroid = sumRevCentroid + ((double)1 / (double)tempSum);
		}
		Dis = ((double)DMax/(double)DMin) * sumRevCentroid;
		return Dis * scatt(myClust) + Dis; 
	}
	private double scatt(Cluster[] myClust)
	{
		double sumClusterVariance = 0;
		double dataVariance = 0;
		int numDataElements = 0;
		double sumData = 0;
		double mu = 0;
		double Scatt = 0;
		for (int i=0; i<myClust.length; i++)
		{
			sumClusterVariance = sumClusterVariance + myClust[i].variance();
			for (int j=0; j<myClust[i].getData().length; j++)
			{
				numDataElements = numDataElements + 1;
				sumData = sumData + myClust[i].getData()[j];
			}
		}
		mu = sumData / numDataElements; 
		for (int i=0; i<myClust.length; i++)
		{
			for (int j=0; j<myClust[i].getData().length; j++)
			{
				dataVariance = dataVariance + (myClust[i].getData()[j] - mu) * (myClust[i].getData()[j] - mu);
			}
		}
		dataVariance = dataVariance / numDataElements;
		Scatt = (1/myClust.length)*(sumClusterVariance/dataVariance);

		return Scatt;
	}

	private double calculateS_Dbw(Cluster[] myClust)
	{
		double Dens_bw = 0;
		double stdev = 0;
		double tempSum = 0;
		for (int i=0; i<myClust.length; i++)
		{
			tempSum = tempSum + myClust[i].standardDeviation();
		}
		stdev = ((double)1/(double)myClust.length)*tempSum;

		for (int i=0; i<myClust.length; i++)
		{
			tempSum = 0;
			for (int j=0; j<myClust.length; j++)
			{
				if (i!=j)
				{
					tempSum = tempSum + (double)density(myClust[i], myClust[j], stdev)/(double)Math.max(density(myClust[i], stdev), density(myClust[j], stdev));
				}
			}
			Dens_bw = Dens_bw + tempSum;
		}
		Dens_bw = Dens_bw / ((myClust.length-1)*myClust.length);

		return scatt(myClust) + Dens_bw;
	}
	private double density(Cluster clust1, Cluster clust2, double stdev)
	{
		double u = (clust1.centroid() + clust2.centroid()) /2;
		double density = 0;
		for(int i=0; i<clust1.getData().length; i++)
			if (Math.abs(clust1.getData()[i]-u) <= stdev)
				density = density + 1;
		for(int i=0; i<clust2.getData().length; i++)
			if (Math.abs(clust2.getData()[i]-u) <= stdev)
				density = density + 1;
		return density;
	}
	private double density(Cluster clust, double stdev)
	{
		double density = 0;
		for(int i=0; i<clust.getData().length; i++)
			if (Math.abs(clust.getData()[i]-clust.centroid()) <= stdev)
				density = density + 1;
		return density;
	}
	// The number of clusters that maximizes Dunn index is taken as the optimal number of clusters.	
	private double Dunn(Cluster[] myClusters)
	{
		double tempSum = 0;
		int counter = 0;
		double[] result = new double[myClusters.length * (myClusters.length -1)/2];
		for (int i=0; i<myClusters.length-1; i++)
		{
			for (int j=i+1; j<myClusters.length; j++)
			{
				tempSum = 0;
				for (int x=0; x<myClusters[i].getData().length; x++)
					for (int y=0; y<myClusters[j].getData().length; y++)
						tempSum = tempSum + Math.abs(myClusters[i].getData()[x]-myClusters[j].getData()[y]);

				tempSum = tempSum / (myClusters[i].getNumElements() * myClusters[j].getNumElements());
				result[counter] = tempSum/(maxInterClustDist(myClusters) * maxInterClustDist(myClusters));

				counter ++;
			}
		}
		double minVal = result[0];
		for (int i=1; i<result.length; i++)
		{
			if (result[i]<minVal)
				minVal = result[i]; 
		}
		return minVal;
	}
	private double maxInterClustDist(Cluster[] myClusters)
	{
		double sumDist;
		double maxDist = 0;
		double[] result = new double[myClusters.length];
		for (int i=0; i<myClusters.length; i++)
		{
			sumDist = 0;
			for (int j=0; j<myClusters[i].getData().length; j++)
			{
				sumDist = sumDist + Math.abs(myClusters[i].centroid()-myClusters[i].getData()[j]);
			}
			result[i]=sumDist/myClusters[i].getNumElements();
		}
		for (int i=0; i<result.length; i++)
			if (result[i]>maxDist)
				maxDist = result[i];

		return maxDist;
	}

	//The Davies-Bouldin index is a function of the ratio of the sum of within-cluster scatter to between cluster separation. When it has a small value it exhibits a good clustering.	
	private double DB(Cluster[] myClusters)
	{
		double max;
		double tmp = 0;
		double tmpSum = 0;

		for (int i=0; i<myClusters.length-1; i++)
		{
			max = 0;
			for (int j=i+1; j<myClusters.length; j++)
			{
				tmp = (myClusters[i].getSumToCentroid() + myClusters[j].getSumToCentroid())/Math.abs(myClusters[i].centroid()-myClusters[j].centroid());
				if (tmp > max)
					max = tmp;
			}
			tmpSum = tmpSum + max;
		}		

		return (tmpSum / myClusters.length);
	}

	// If silhouette value is close to 1, it means that sample is “well-clustered” and it was assigned to a very appropriate cluster. 
	// If silhouette value is about zero, it means that that sample could be assign to another closest cluster as well, and the sample lies equally far away from both clusters. 
	// If silhouette value is close to –1, it means that sample is “mis-classified” and is merely somewhere in between the clusters. 
	// The overall average silhouette width for the entire plot is simply the average of the S(i) for all objects in the whole dataset. 
	private double Silhouette(Cluster[] myClusters)
	{
		int[] closetsClust = findClosetClusters(myClusters);
		double a_i = 0;
		double b_i = 0;
		double s_i = 0;
		int numOfDataElements = 0;

		for (int i=0; i<myClusters.length; i++)
		{
			for (int j=0; j<myClusters[i].getData().length;j++)
			{
				numOfDataElements = numOfDataElements + 1;
				a_i = myClusters[i].avgDissimilarityOfElement(j);
				b_i = 0;
				for (int k=0; k<myClusters[closetsClust[i]].getData().length; k++)
				{
					b_i = b_i + Math.abs(myClusters[i].getData()[j] - myClusters[closetsClust[i]].getData()[k]);
				}
				s_i = s_i + (b_i - a_i) / (double) Math.max(a_i, b_i);
			}
		}
		return s_i / (double)numOfDataElements;
	}

	private int[] findClosetClusters(Cluster[] myClusters)
	{
		int[] closest = new int[myClusters.length];
		for (int i=0;i<closest.length-1;i++)
			closest[i] = i+1;
		closest[closest.length-1] = 0;

		for (int i=0; i<myClusters.length; i++)

			for (int j=0; j<myClusters.length; j++)
			{
				int k = closest[i];
				if (i!=j && Math.abs(myClusters[i].centroid()-myClusters[j].centroid()) < Math.abs(myClusters[i].centroid()-myClusters[k].centroid()))
					closest[i] = j;
			}
		return closest;
	}
	// This index (Hubert and Schultz) indicates a good clustering for small values of C.
	private double CIndex(Cluster[] myClusters)
	{
		double S_Min = 0;
		double S_Max = 0;
		double tmpS_Min = 0;
		double tmpS_Max = 0;
		double S = 0;
		double tmpS = 0;

		for (int i=0; i < myClusters.length; i++)
		{
			tmpS_Min = Math.abs(myClusters[i].getData()[0]-myClusters[i].centroid());
			tmpS_Max = 0;
			for (int j=0; j<myClusters[i].getData().length -1; j++)
			{
				for (int k=j+1; k<myClusters[i].getData().length; k++)
				{
					tmpS = Math.abs(myClusters[i].getData()[j]-myClusters[i].getData()[k]);
					S = S + tmpS;
					if (tmpS > tmpS_Max)
						tmpS_Max = tmpS;
					if (tmpS < tmpS_Min)
						tmpS_Min = tmpS;
				}
			}
			S_Max = S_Max + tmpS_Max; 
			S_Min = S_Min + tmpS_Min; 
		}

		return (S - S_Min)/(S_Max - S_Min);
	}
	public double getMinValue()
	{
		double min = m_AttValues[0];
		for (int i=0; i<m_AttValues.length; i++)
			if (m_AttValues[i]<min)
				min = m_AttValues[i];
		return min;			
	}
	public double getMaxValue()
	{
		double max = m_AttValues[0];
		for (int i=1; i<m_AttValues.length; i++)
			if (m_AttValues[i]>max)
				max = m_AttValues[i];
		return max;			
	}
	
//	SUBCLUST Locates data cluster centers using subtractive clustering.
//	
//	   SUBCLUST operates by finding the optimal data point to define a cluster
//	   center, based on the density of surrounding data points. All data points
//	   within the distance RADII of this point are then removed, in order to
//	   determine the next data cluster and its center. This process is repeated
//	   until all of the data is within the distance RADII of a cluster center.
//	
//	   [C] = SUBCLUST(X,RADII) clusters the data points in the M-by-N matrix X,
//	   where M is the number of data points and N is the number of coordinates
//	   (data dimensions) that specify each point. RADII has a value between 0 and 1
//	   and specifies the size of the cluster in each of the data dimensions,
//	   assuming the data fall within a unit hyperbox (range [0 1]). Specifying a
//	   smaller cluster radius will usually yield more (smaller) clusters in the
//	   data. When RADII is a scalar it is applied to all data dimensions, when it
//	   is a vector, it has one entry for each data dimension. The cluster centers
//	   are returned as rows of the matrix C. C has size J-by-N, if J clusters are
//	   required to cluster the data.
	
	public List subClust(double[] a_Values, double radius_a)
	{
		
		double max = getMaxValue();
		double min = getMinValue();
		
		for (int k=0; k<a_Values.length; k++)
			a_Values[k]=(a_Values[k]-min)/(max-min);
		
		double[] xPotetial = new double[a_Values.length];
		
		boolean findMore = true;
		
		double alpha = 4/(radius_a*radius_a);
		double radius_b = 1.5 * radius_a;
		double beta = 4/(radius_b*radius_b);
		int highestPotentialPoint;
		
//		double epsilon_a = 0.5;
//		double epsilon_b = 0.15;
		double epsilon_a = 0.3;
		double epsilon_b = 0.001;
		
		ArrayList centers = new ArrayList();
		
		for (int i=0; i<xPotetial.length; i++)
		{
			for (int j=0; j<a_Values.length; j++)
			{
				xPotetial[i] = xPotetial[i] + Math.exp((-1)* alpha * Math.abs(a_Values[i]-a_Values[j]) * Math.abs(a_Values[i]-a_Values[j]));
				//xPotetial[i] = xPotetial[i] + Math.exp(-10);
			}
		}
		highestPotentialPoint = getMaxValIndex(xPotetial);
		
		Center a_Center = new Center();
		a_Center.index = highestPotentialPoint;
		a_Center.value = a_Values[highestPotentialPoint];
		a_Center.potential = xPotetial[highestPotentialPoint];

		Center tmpCenter = null;
		
		double d_min;
		
		centers.add(a_Center);

		while (findMore)
		{
			tmpCenter = (Center)centers.get(centers.size()-1);
			for (int i=0; i<xPotetial.length; i++)
			{
				xPotetial[i] = xPotetial[i] - tmpCenter.potential * Math.exp((-1)* beta * Math.abs(a_Values[i]-tmpCenter.value) * Math.abs(a_Values[i]-tmpCenter.value));
			}		
			highestPotentialPoint = getMaxValIndex(xPotetial);
			
			if (xPotetial[highestPotentialPoint] > (epsilon_a * tmpCenter.potential))
			{
				a_Center = new Center();
				a_Center.index = highestPotentialPoint;
				a_Center.value = a_Values[highestPotentialPoint];
				a_Center.potential = xPotetial[highestPotentialPoint];
				centers.add(a_Center);
				continue;
			}
			else if (xPotetial[highestPotentialPoint] < (epsilon_b * tmpCenter.potential))
			{
				findMore = false;
				break;
			}
			else
			{
				d_min = Math.abs(tmpCenter.value - a_Values[highestPotentialPoint]);
				for (int k=1; k<centers.size();k++)
				{
					tmpCenter = (Center)centers.get(k);					
					if ((Math.abs(tmpCenter.value - a_Values[highestPotentialPoint]) <d_min))
					{
						d_min = Math.abs(tmpCenter.value - a_Values[highestPotentialPoint]);
					}
				}
				tmpCenter = (Center)centers.get(0);									
				if ((d_min/radius_a) + (xPotetial[highestPotentialPoint]/tmpCenter.potential) >= 1) 
				{
					a_Center = new Center();
					a_Center.index = highestPotentialPoint;
					a_Center.value = a_Values[highestPotentialPoint];
					a_Center.potential = xPotetial[highestPotentialPoint];
					centers.add(a_Center);
					continue;					
				}
				else
				{
					xPotetial[highestPotentialPoint] = 0;
					tmpCenter = (Center)centers.get(centers.size()-1);					
					highestPotentialPoint = tmpCenter.index;
				}
			}
		}
		return centers;
	}
	private int getMaxValIndex(double[] aValues)
	{
		int maxValIndex = 0;
		for (int i=1; i<aValues.length; i++)
		{
			if (aValues[i]>aValues[maxValIndex])
				maxValIndex = i;
		}
		return maxValIndex;
	}
	public Cluster[] sortClusterSolution(Cluster[] myClusterSolution)
	{
		Cluster[] sortedClusterSolution = new Cluster[myClusterSolution.length];
		int[] rank = new int[myClusterSolution.length];
		double[] centers = new double[myClusterSolution.length];
		for (int i=0; i<myClusterSolution.length; i++)
			centers[i] = myClusterSolution[i].getCenter();
		Arrays.sort(centers);
		for (int i=0; i<centers.length; i++)
		{
			for (int j=0; j<myClusterSolution.length; j++)
			{
				if (centers[i]==myClusterSolution[j].getCenter())
				{
					sortedClusterSolution[i] = new Cluster();
					sortedClusterSolution[i].setData(myClusterSolution[j].getData());
					sortedClusterSolution[i].setCenter(myClusterSolution[j].getCenter());
					sortedClusterSolution[i].setLabel(myClusterSolution[j].getLabel());
					sortedClusterSolution[i].setNumElements(myClusterSolution[j].getNumElements());
					sortedClusterSolution[i].setM_ElementIdx(myClusterSolution[j].getM_ElementIdx());
					break;
				}
			}
		}
		return sortedClusterSolution;
	}
}