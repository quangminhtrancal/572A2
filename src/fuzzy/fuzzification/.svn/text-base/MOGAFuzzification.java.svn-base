package fuzzy.fuzzification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.ArithmeticCrossoverOperator;
import org.jgap.impl.BestChromosomesSelector;
import org.jgap.impl.BooleanGene;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.DoubleGene;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.MutationOperator;



import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import fuzzy.FuzzySet;
import fuzzy.clustering.AdaptedKMeans;
import fuzzy.clustering.BasicKMeans;
import fuzzy.clustering.Cluster;
import fuzzy.clustering.IntRandomRange;
import fuzzy.clustering.KMeansCluster;
import fuzzy.clustering.MOGAClustering;
import fuzzy.clustering.MOGAClusteringFitnessEvaluator;
import fuzzy.clustering.MOGAClusteringFitnessFunction;
import fuzzy.clustering.MOGAClustering.MOFitnessComparator;


public class MOGAFuzzification {

	private double[] m_Data;
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
				double tmp1 = Double.valueOf(geneSet1[i].getAllele().toString());
				double tmp2 = Double.valueOf(geneSet2[i].getAllele().toString());
				if (tmp1 != tmp2)
					return 0;
			}
			return 1;

		}	
	}

	public MOGAFuzzification(MOGAClustering a_MOGAClustering, double[] data) {
		this.m_Data = data;
		this.m_MOGAClustering = a_MOGAClustering;

	}
	public MOGAFuzzification(MOGAClustering a_MOGAClustering) {
		m_MOGAClustering = a_MOGAClustering;
	}
	/**
	 * @param args
	 */

	private Configuration m_FSGAConf;

	private MOGAClustering m_MOGAClustering;
	
	public Configuration getM_FSGAConf() {
		return m_FSGAConf;
	}
	public void setM_FSGAConf(Configuration conf) {
		m_FSGAConf = conf;
	}
	private FitnessFunction m_FitnessFunction;

	private Population m_Pop;

	private static Genotype m_Genotype;

	private int m_PopulationSize = 20;

	private static final int MAX_ALLOWED_EVOLUTIONS = 50;
	
	private int m_MaxNumOfClusters;
	
	public int getM_MaxNumOfClusters() {
		return m_MaxNumOfClusters;
	}
	public void setM_MaxNumOfClusters(int maxNumOfClusters) {
		m_MaxNumOfClusters = maxNumOfClusters;
	}
	public int getM_PopulationSize() {
		return m_PopulationSize;
	}
	public void setM_PopulationSize(int populationSize) {
		m_PopulationSize = populationSize;
	}
	
	public static void main(String[] args) throws IOException, SAXException 
	{
		
		// TODO Auto-generated method stub
		MOGAClustering a_MOGAClustering = new MOGAClustering();
		MOGAFuzzification myMF = new MOGAFuzzification(a_MOGAClustering);

//		myMF.generateParetoOptimalSolutions();
		System.out.println("-------------------------------------------- Final Population -------------------------------------------------");		
//		myMF.printPopulation();
		// Remove solutions that are not Pareto-optimal.
		// --------------------------------------------- 
		List chroms = m_Genotype.getPopulation().getChromosomes();
		int size = m_Genotype.getPopulation().getChromosomes().size();
		int i = 0;
		boolean removed = false;
		MOFitnessComparator comp = myMF.new MOFitnessComparator();
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
		System.out.println();
		
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
			System.out.println(MOGAFuzzificationFitnessFunction.getVector(bestSolutionSoFar));
		}
		size = m_Genotype.getPopulation().getChromosomes().size();
		System.out.println("number of altenative solutions is (After Redundancy Elimination) : " + size);
		
		myMF.outFuzzySetsToXML(myMF.retrieveFuzzySetsFromChromosome((IChromosome)chroms.get(0)), "finalfuzzy.xml");
		

//		i = 0;
//
//		while (i<size) 
//		{
//			IChromosome a_Solution = m_Genotype.getPopulation().getChromosome(i);
//			Gene[] genes = a_Solution.getGenes();
//			for (int idx=0; idx<genes.length; idx++)
//			{
//				System.out.print(myValues[idx] + " :" + genes[idx].getAllele() + " ");
//			}
//			System.out.println();
//			i++;
//		}
		
	}
	public FuzzySet[] getBestFuzzySetsSolution(List<IChromosome> chroms)
	{
		generateParetoOptimalSolutions(chroms);
		removeDuplicateParetoOptimalSolutions();
		FuzzySet[] bestFuzzySetsSolution = retrieveFuzzySetsFromChromosome(findBestSolution(m_Genotype, m_Data));
		return bestFuzzySetsSolution;
	}	
	public FuzzySet[] getBestFuzzySetsSolution(int numFuzzySets)
	{
		generateParetoOptimalSolutions(numFuzzySets);
		removeDuplicateParetoOptimalSolutions();
		FuzzySet[] bestFuzzySetsSolution = retrieveFuzzySetsFromChromosome(findBestSolution(m_Genotype, m_Data));
		return bestFuzzySetsSolution;
	}	
	public Cluster[] generateKMeanSolution(int k)
	{
		Cluster[] finalCluster = null;
		try{
			File LogFile = new File("c:\\QueryBuilderTemp\\KMeanCenters.txt");
			PrintWriter pw = new PrintWriter(new FileWriter(LogFile));
			pw.println();
			double[][] data = new double[m_Data.length][];
			for (int i=0; i<m_Data.length; i++)
			{
				data[i] = new double[1];
				data[i][0] = m_Data[i];
			}	
			BasicKMeans myKMeans = new BasicKMeans(data,k,500,99999);
			myKMeans.run();
			KMeansCluster[] newCluster = myKMeans.getClusters();
			finalCluster = new Cluster[newCluster.length];
			for (int i=0; i<newCluster.length; i++)
			{
				finalCluster[i] = new Cluster();
				finalCluster[i].setLabel(i);
				finalCluster[i].setCenter(newCluster[i].getCenter()[0]);
				pw.println("Center " + i +": " + finalCluster[i].getCenter());
				finalCluster[i].m_Data = new double[newCluster[i].getMemberIndexes().length];
				finalCluster[i].setNumElements(newCluster[i].getMemberIndexes().length);
				for (int j=0; j<finalCluster[i].m_Data.length; j++)
				{
					finalCluster[i].m_Data[j] = m_Data[newCluster[i].getMemberIndexes()[j]];
				}
			}
			pw.close();
		}
		catch(Exception ex2){}		
		return finalCluster;
	}
	private void generateParetoOptimalSolutions(List<IChromosome> chroms)
	{
		try {
			initialPopulation(chroms);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) 
		{
			if (!uniqueChromosomes(m_Genotype.getPopulation())) 
			{
				throw new RuntimeException("Invalid state in generation "+i);
			}
			
			m_Genotype.evolve();
		}
		removeNonParetoOptimalSolutions();
	}
	private void generateParetoOptimalSolutions(int numFuzzySets)
	{
		try {
			initialPopulation(numFuzzySets);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < MAX_ALLOWED_EVOLUTIONS; i++) 
		{
			if (!uniqueChromosomes(m_Genotype.getPopulation())) 
			{
				throw new RuntimeException("Invalid state in generation "+i);
			}
			
			m_Genotype.evolve();
		}
		removeNonParetoOptimalSolutions();
	}
	private void initialPopulation(List<IChromosome> chroms) throws Exception
	{

		// chroms: A list containing chromosomes from the final population of MOGAClustering process
		
		// Find max number of clusters within the solutions in the pareto optimal set
		int maxNumOfClusters = 0;
		for (int k=0; k<chroms.size(); k++)
		{
			if (m_MOGAClustering.getMaxLabelInChrom(chroms.get(k).getGenes()) > maxNumOfClusters)
				maxNumOfClusters = m_MOGAClustering.getMaxLabelInChrom(chroms.get(k).getGenes());
		}
		setM_MaxNumOfClusters(maxNumOfClusters);

		// Start with a DefaultConfiguration, which comes setup with the
		// most common settings.
		// -------------------------------------------------------------
		m_FSGAConf.reset();
		m_FSGAConf = new DefaultConfiguration(m_Data);

		m_FSGAConf.getGeneticOperators().clear();

		m_FSGAConf.addGeneticOperator(new ArithmeticCrossoverOperator(m_FSGAConf));
		m_FSGAConf.addGeneticOperator(new MutationOperator(m_FSGAConf));


		m_FSGAConf.removeNaturalSelectors(true);
		BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(
				m_FSGAConf, 0.95d);
		bestChromsSelector.setDoubletteChromosomesAllowed(true);
		m_FSGAConf.addNaturalSelector(bestChromsSelector, true);

		m_FSGAConf.reset();
		m_FSGAConf.setFitnessEvaluator(new MOGAFuzzificationFitnessEvaluator(m_Data));
		m_FSGAConf.setPreservFittestIndividual(false);
		m_FSGAConf.setKeepPopulationSizeConstant(false);

		// Set the fitness function we want to use, which is our
		// implemented FitnessFunction.
		// ---------------------------------------------------------
		BulkFitnessFunction m_FitnessFunction =
			new MOGAFuzzificationFitnessFunction(m_Data);
		m_FSGAConf.setBulkFitnessFunction(m_FitnessFunction);

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
		IChromosome sampleChromosome = new Chromosome(m_FSGAConf, new BooleanGene(m_FSGAConf), 3*(maxNumOfClusters-2)+4+1); 
		m_FSGAConf.setSampleChromosome(sampleChromosome);

		m_FSGAConf.setPopulationSize(m_PopulationSize);

		// Initialize the population with custom code
		m_Pop = new Population(m_FSGAConf,m_PopulationSize);
		
		// Retrieve a chromosome from the final (pareto optimal set of solutions of MOGAClustering
		// Convert the chromosome to a cluster solution
		// encode a choromosome that represents fuzzy sets generated from a cluster solution
		// Add the chromosome to the initial population

		Cluster[] a_ClusterSolution = null;
		for (int k=0; k<chroms.size(); k++)
		{
			a_ClusterSolution = m_MOGAClustering.popClustersFromChrom((IChromosome)chroms.get(k), m_Data);
			m_Pop.addChromosome(constructFuzzyChromosome(a_ClusterSolution));
			
			
//			for (int j=0; j < m_Pop.getChromosome(k).size(); j++)
//				System.out.print((j+1) + ": " + m_Pop.getChromosome(k).getGene(j).getAllele()+ "  ");
//			System.out.println();
			
		}
		
		IChromosome a_FuzzyChrom = null;
		IntRandomRange mIntRandomRange = new IntRandomRange();
		int randomIndexOutOfBestClusteringSolutions = 0;
		
		a_ClusterSolution = m_MOGAClustering.popClustersFromChrom((IChromosome)chroms.get(0), m_Data); 
		a_FuzzyChrom = constructFuzzyChromosome(a_ClusterSolution);
		outFuzzySetsToXML(retrieveFuzzySetsFromChromosome(a_FuzzyChrom), "initialfuzzy.xml");
		
		for (int k=chroms.size(); k<m_PopulationSize; k++)
		{
			randomIndexOutOfBestClusteringSolutions = mIntRandomRange.generateRandomInteger(0, chroms.size()-1);
			a_ClusterSolution = m_MOGAClustering.popClustersFromChrom((IChromosome)chroms.get(randomIndexOutOfBestClusteringSolutions), m_Data);
			a_FuzzyChrom = constructFuzzyChromosome(a_ClusterSolution);
			m_Pop.addChromosome(constructRandomFuzzyChromosome(a_FuzzyChrom));
//			for (int j=0; j < m_Pop.getChromosome(k).size(); j++)
//				System.out.print((j+1) + ": " + m_Pop.getChromosome(k).getGene(j).getAllele()+ "  ");
//			System.out.println();
		}

		m_Genotype = new Genotype(m_FSGAConf,m_Pop);
	}
	private void initialPopulation(int numFuzzySets) throws InvalidConfigurationException
	{

		setM_MaxNumOfClusters(numFuzzySets);

		// Start with a DefaultConfiguration, which comes setup with the
		// most common settings.
		// -------------------------------------------------------------
		m_FSGAConf.reset();
		m_FSGAConf = new DefaultConfiguration(m_Data);

		m_FSGAConf.getGeneticOperators().clear();

		m_FSGAConf.addGeneticOperator(new ArithmeticCrossoverOperator(m_FSGAConf));
		m_FSGAConf.addGeneticOperator(new MutationOperator(m_FSGAConf));


		m_FSGAConf.removeNaturalSelectors(true);
		BestChromosomesSelector bestChromsSelector = new BestChromosomesSelector(
				m_FSGAConf, 0.95d);
		bestChromsSelector.setDoubletteChromosomesAllowed(true);
		m_FSGAConf.addNaturalSelector(bestChromsSelector, true);

		m_FSGAConf.reset();
		m_FSGAConf.setFitnessEvaluator(new MOGAFuzzificationFitnessEvaluator(m_Data));
		m_FSGAConf.setPreservFittestIndividual(false);
		m_FSGAConf.setKeepPopulationSizeConstant(false);

		// Set the fitness function we want to use, which is our
		// implemented FitnessFunction.
		// ---------------------------------------------------------
		BulkFitnessFunction m_FitnessFunction =
			new MOGAFuzzificationFitnessFunction(m_Data);
		m_FSGAConf.setBulkFitnessFunction(m_FitnessFunction);

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
		IChromosome sampleChromosome = new Chromosome(m_FSGAConf, new BooleanGene(m_FSGAConf), 3*(numFuzzySets-2)+4+1); 
		m_FSGAConf.setSampleChromosome(sampleChromosome);

		m_FSGAConf.setPopulationSize(m_PopulationSize);

		// Initialize the population with custom code
		m_Pop = new Population(m_FSGAConf,m_PopulationSize);
		
		// Retrieve a chromosome from the final (pareto optimal set of solutions of MOGAClustering
		// Convert the chromosome to a cluster solution
		// encode a choromosome that represents fuzzy sets generated from a cluster solution
		// Add the chromosome to the initial population

		Cluster[] a_ClusterSolution = generateKMeanSolution(numFuzzySets);
		IChromosome a_FuzzyChrom = constructFuzzyChromosome(a_ClusterSolution);
		outFuzzySetsToXML(retrieveFuzzySetsFromChromosome(a_FuzzyChrom), "initialfuzzy.xml");
		
		m_Pop.addChromosome(a_FuzzyChrom);
			
		for (int k=1; k<m_PopulationSize; k++)
		{
			m_Pop.addChromosome(constructRandomFuzzyChromosome(a_FuzzyChrom));
		}

		m_Genotype = new Genotype(m_FSGAConf,m_Pop);

		for (int i=0;i<m_PopulationSize;i++)
		{
			System.out.print("Chromosome " + (i+1) + " :");
			for (int j=0;j<m_Genotype.getPopulation().getChromosome(i).size();j++)
			{
				System.out.print(" " + m_Genotype.getPopulation().getChromosome(i).getGene(j).getAllele());			
			}
			System.out.println();
		}
		
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
	// This function generates a new chromosome from a given chromosome by making random ranges in the given chromosome
	// without changing S, the fist gene, and also without changing the center of fuzzy sets, R.
	private IChromosome constructRandomFuzzyChromosome(IChromosome a_Chrom)
	{
		IChromosome chrom = null;
		Gene[] newGenes = new Gene[3 * (getM_MaxNumOfClusters() - 2) + 4 + 1];
		double lowerBound = 0;
		double upperBound = 0;
		
		double anAllele;
		
		// initialize the all Alleles to zero
		for (int geneIndex=0;geneIndex<newGenes.length;geneIndex++)
		{
			anAllele = 0;
			try {
				newGenes[geneIndex] = new DoubleGene(m_FSGAConf,0,0);
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newGenes[geneIndex].setAllele(anAllele);				
		}
		
		anAllele = Double.valueOf(a_Chrom.getGene(0).getAllele().toString());
		int numOfEffectiveGenes = (int)(3 * (anAllele - 2) + 4 + 1);
		try {
			newGenes[0] = new DoubleGene(m_FSGAConf,anAllele,anAllele);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newGenes[0].setAllele(anAllele);
		int k = 1;
		IntRandomRange an_IntRandomRange = new IntRandomRange();
		
	    double r = (getMaxVal()-getMinVal())/(getM_MaxNumOfClusters()+1);
		
		
		while (k<numOfEffectiveGenes)
		{
			
			if (k==1)
			{

				anAllele = Double.valueOf(a_Chrom.getGene(k).getAllele().toString());
				if (anAllele==getMinVal())
					anAllele = anAllele + r;			
				try {
					newGenes[k] = new DoubleGene(m_FSGAConf,anAllele,anAllele);
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[k].setAllele(anAllele);
				k++;

				lowerBound = 0;
				upperBound = Double.valueOf(a_Chrom.getGene(k + 2).getAllele().toString())- Double.valueOf(a_Chrom.getGene(k-1).getAllele().toString());
				anAllele = an_IntRandomRange.generateRandomDouble(lowerBound,upperBound);
				try {
					newGenes[k] = new DoubleGene(m_FSGAConf,anAllele,anAllele);
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[k].setAllele(anAllele);
				k++;

			}
			else if (k==numOfEffectiveGenes -2)
			{
				double R2 = Double.valueOf(a_Chrom.getGene(k+1).getAllele().toString());
				double R1 = Double.valueOf(a_Chrom.getGene(k-2).getAllele().toString());
				double b1 = Double.valueOf(newGenes[k-1].getAllele().toString());
				lowerBound =  R2 - R1 - b1;
				upperBound = Double.valueOf(a_Chrom.getGene(k+1).getAllele().toString()) - Double.valueOf(a_Chrom.getGene(k-2).getAllele().toString());
				anAllele = an_IntRandomRange.generateRandomDouble(lowerBound, upperBound);
				try {
					newGenes[k] = new DoubleGene(m_FSGAConf,anAllele,anAllele);
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[k].setAllele(anAllele);
				k++;
				anAllele = Double.valueOf(a_Chrom.getGene(k).getAllele().toString());				
				try {
					newGenes[k] = new DoubleGene(m_FSGAConf,anAllele,anAllele);
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[k].setAllele(anAllele);
				k++;				
			}
			else
			{
				upperBound =  Double.valueOf(a_Chrom.getGene(k+1).getAllele().toString()) - Double.valueOf(a_Chrom.getGene(k-2).getAllele().toString());
				double R2 = Double.valueOf(a_Chrom.getGene(k+1).getAllele().toString());
				double R1 = Double.valueOf(a_Chrom.getGene(k-2).getAllele().toString());
				double b1 = Double.valueOf(newGenes[k-1].getAllele().toString());
				lowerBound =  R2 - R1 - b1;
				anAllele = an_IntRandomRange.generateRandomDouble(lowerBound,upperBound);

				try {
					newGenes[k] = new DoubleGene(m_FSGAConf,anAllele,anAllele);
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[k].setAllele(anAllele);
				k++;

				anAllele = Double.valueOf(a_Chrom.getGene(k).getAllele().toString());
				try {
					newGenes[k] = new DoubleGene(m_FSGAConf,anAllele,anAllele);
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[k].setAllele(anAllele);
				k++;

				lowerBound = 0;
				upperBound = Double.valueOf(a_Chrom.getGene(k+2).getAllele().toString()) - Double.valueOf(a_Chrom.getGene(k-1).getAllele().toString());
				anAllele = an_IntRandomRange.generateRandomDouble(lowerBound,upperBound);
				try {
					newGenes[k] = new DoubleGene(m_FSGAConf,anAllele,anAllele);
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[k].setAllele(anAllele);
				k++;
			}
		}
		// Initialize a new chromosome
		try {
			chrom = new Chromosome(m_FSGAConf);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Set generated genes for the new chromosome
		try {
			chrom.setGenes(newGenes);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		return chrom;
	}
	// The input to this function is a cluster solution. First clusters get sorted within the cluster solution based on their labels. 
	// The function builds a choromosome according to the initial fuzzy sets
	// obtained form the cluster solution, each cluster is represented by a fuzzy set, and each fuzzy set in the choromosome is represented
	// by (base-left,R,base-right).
	private IChromosome constructFuzzyChromosome(Cluster[] a_ClusterSolution)
	{
		IChromosome chrom = null;
		Gene[] newGenes = new Gene[3 * (getM_MaxNumOfClusters() - 2) + 4 + 1];
		double anAllele;

		// initialize the all Alleles to zero
		for (int geneIndex=0;geneIndex<newGenes.length;geneIndex++)
		{
			anAllele = 0;
			try {
				newGenes[geneIndex] = new DoubleGene(m_FSGAConf,0,0);
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newGenes[geneIndex].setAllele(anAllele);				
		}

//		List<Cluster> list = Arrays.asList(a_ClusterSolution);
//		Collections.sort(list);

		a_ClusterSolution = m_MOGAClustering.sortClusterSolution(a_ClusterSolution);
		
		
		try{
    		File LogFile = new File("c:\\QueryBuilderTemp\\SortedKMeanCenters.txt");
    		PrintWriter pw = new PrintWriter(new FileWriter(LogFile));
    		pw.println();
    		for (int i=0; i < a_ClusterSolution.length; i++)
    			pw.println("Center " + i + ": " + a_ClusterSolution[i].getCenter());
    		pw.close();
    	}
    	catch(Exception ex2){}		
		
		
		anAllele = 0;

		
		anAllele = a_ClusterSolution.length;
		try {
			newGenes[0] = new DoubleGene(m_FSGAConf,a_ClusterSolution.length,a_ClusterSolution.length);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newGenes[0].setAllele(anAllele);
		
		
		int counter = 1;
		for (int k=0;k<a_ClusterSolution.length;k++) 
		{
			if (k == 0)
			{
				anAllele = a_ClusterSolution[k+1].getCenter() - a_ClusterSolution[k].getCenter();
				try {
					newGenes[counter] = new DoubleGene(m_FSGAConf,a_ClusterSolution[k].getCenter(),a_ClusterSolution[k].getCenter());
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//newGenes[counter].setAllele(list.get(k).centroid());
				newGenes[counter].setAllele(anAllele);
				counter++;

				anAllele = a_ClusterSolution[k].getCenter();
				try {
					newGenes[counter] = new DoubleGene(m_FSGAConf,a_ClusterSolution[k+1].getCenter() - a_ClusterSolution[k].getCenter(),a_ClusterSolution[k+1].getCenter() - a_ClusterSolution[k].getCenter());
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[counter].setAllele(anAllele);
				counter++;
			}
			else if (k == a_ClusterSolution.length - 1)
			{
				anAllele = a_ClusterSolution[k].getCenter() - a_ClusterSolution[k-1].getCenter();
				try {
					newGenes[counter] = new DoubleGene(m_FSGAConf,a_ClusterSolution[k].getCenter() - a_ClusterSolution[k-1].getCenter(),a_ClusterSolution[k].getCenter() - a_ClusterSolution[k-1].getCenter());
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				newGenes[counter].setAllele(anAllele);
				counter++;

				anAllele = a_ClusterSolution[k].getCenter(); 
				try {
					newGenes[counter] = new DoubleGene(m_FSGAConf,a_ClusterSolution[k].getCenter(),a_ClusterSolution[k].getCenter());
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[counter].setAllele(anAllele);
			}	
			else
			{
				anAllele = a_ClusterSolution[k].getCenter() - a_ClusterSolution[k-1].getCenter();
				try {
					newGenes[counter] = new DoubleGene(m_FSGAConf,a_ClusterSolution[k].getCenter() - a_ClusterSolution[k-1].getCenter(),a_ClusterSolution[k].getCenter() - a_ClusterSolution[k-1].getCenter());
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[counter].setAllele(anAllele);
				counter++;

				anAllele = a_ClusterSolution[k].getCenter();
				try {
					newGenes[counter] = new DoubleGene(m_FSGAConf,a_ClusterSolution[k].getCenter(),a_ClusterSolution[k].getCenter());
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[counter].setAllele(anAllele);
				counter++;

				anAllele = a_ClusterSolution[k+1].getCenter() - a_ClusterSolution[k].getCenter();
				try {
					newGenes[counter] = new DoubleGene(m_FSGAConf,a_ClusterSolution[k+1].getCenter() - a_ClusterSolution[k].getCenter(),a_ClusterSolution[k+1].getCenter() - a_ClusterSolution[k].getCenter());
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[counter].setAllele(anAllele);				
				counter++;
			}
		}
		// Initialize a new chromosome
		try {
			chrom = new Chromosome(m_FSGAConf);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Set generated genes for the new chromosome
		try {
			chrom.setGenes(newGenes);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return chrom;
	}
	public void transferClusterToFuzzySet(Cluster[] myClusters)
	{
		ArrayList<FuzzySet> myArrayList = new ArrayList();
		FuzzySet aFuzzySet = null;

		double[] tempChrom = new double[(myClusters.length - 2)*3 + 4];

		Cluster tmpCluster = null;
		List<Cluster> list = Arrays.asList(myClusters);
		Collections.sort(list);

		int counter = 0;
		for (int k=0;k<list.size();k++) 
		{
			// System.out.println("centroid " + k + ": " + list.get(k).centroid());
			if (k == 0)
			{
				tempChrom[counter] = list.get(k).centroid();
				counter++;
				tempChrom[counter] = list.get(k+1).centroid() - list.get(k).centroid();
				counter++;
			}
			else if (k == list.size()-1)
			{
				tempChrom[counter] = list.get(k).centroid() - list.get(k-1).centroid();
				counter++;
				tempChrom[counter] = list.get(k).centroid();
			}	
			else
			{
				tempChrom[counter] = list.get(k).centroid() - list.get(k-1).centroid();
				counter++;
				tempChrom[counter] = list.get(k).centroid();
				counter++;
				tempChrom[counter] = list.get(k+1).centroid() - list.get(k).centroid();				
				counter++;
			}
		}
	}
	public void printPopulation()
	{
//		for (int i=0;i<m_PopulationSize;i++)
//		{
//			System.out.print("Chromosome " + (i+1) + " :");
//			for (int j=0;j<m_Pop.getChromosome(0).size();j++)
//			{
//				System.out.print(" " + m_Genotype.getPopulation().getChromosome(i).getGene(j).getAllele());			
//			}
//			System.out.println();
//		}
	}	
	private double getMinVal()
	{
		if (m_MOGAClustering == null)
		{
			System.out.println("Values set is empty!");
			return -1;
		}
		else
			return m_MOGAClustering.getMinValue();
	}
	private double getMaxVal()
	{
		if (m_MOGAClustering == null)
		{
			System.out.println("Values set is empty!");
			return -1;
		}
		else
			return m_MOGAClustering.getMaxValue();
	}
	public void outToXML() throws IOException, SAXException
	{
		FileOutputStream fos = new FileOutputStream("c:\\QueryBuilderTemp\\fuzzy.xml");
		// XERCES 1 or 2 additionnal classes.
		OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
		of.setIndent(1);
		of.setIndenting(true);
		of.setDoctype(null,"users.dtd");
		XMLSerializer serializer = new XMLSerializer(fos,of);

		// SAX2.0 ContentHandler.
		ContentHandler hd = serializer.asContentHandler();
		hd.startDocument();
		
		// Processing instruction sample.
		//hd.processingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"users.xsl\"");
		
		// USER attributes.
		AttributesImpl atts = new AttributesImpl();
		// USERS tag.
		hd.startElement("","","USERS",atts);
		// USER tags.
		String[] id = {"PWD122","MX787","A4Q45"};
		String[] type = {"customer","manager","employee"};
		String[] desc = {"Tim@Home","Jack&Moud","John D'oé"};
		for (int i=0;i<id.length;i++)
		{
		  atts.clear();
		  atts.addAttribute("","","ID","CDATA",id[i]);
		  atts.addAttribute("","","TYPE","CDATA",type[i]);
		  hd.startElement("","","USER",atts);
		  hd.characters(desc[i].toCharArray(),0,desc[i].length());
		  hd.endElement("","","USER");
		}
		hd.endElement("","","USERS");
		hd.endDocument();
		fos.close();		
	}
	public void outFuzzySetsToXML(FuzzySet[] myFuzzySets, String filename)
	{
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
		// XERCES 1 or 2 additionnal classes.
		OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
		of.setIndent(1);
		of.setIndenting(true);
		of.setDoctype(null,"users.dtd");
		XMLSerializer serializer = new XMLSerializer(fos,of);

		// SAX2.0 ContentHandler.
		ContentHandler hd = null;
		hd = serializer.asContentHandler();
		hd.startDocument();
		
		// Processing instruction sample.
		//hd.processingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"users.xsl\"");
		
		// USER attributes.
		AttributesImpl atts = new AttributesImpl();
		// USERS tag.
		hd.startElement("","","FUZZIFY",atts);
		// USER tags.
		for (int i=0;i<myFuzzySets.length;i++)
		{
		  atts.clear();
		  atts.addAttribute("","","LABEL","CDATA",myFuzzySets[i].getLabel());
		  hd.startElement("","","TERM",atts);
		  
		  hd.startElement("","","FROM",null);
		  hd.characters(Double.toString(myFuzzySets[i].getFrom()).toCharArray(),0,Double.toString(myFuzzySets[i].getFrom()).length());
		  hd.endElement("","","FROM");
		  
		  hd.startElement("","","R",null);
		  hd.characters(Double.toString(myFuzzySets[i].getR()).toCharArray(),0,Double.toString(myFuzzySets[i].getR()).length());
		  hd.endElement("","","R");
		  
		  hd.startElement("","","TO",null);
		  hd.characters(Double.toString(myFuzzySets[i].getTo()).toCharArray(),0,Double.toString(myFuzzySets[i].getTo()).length());
		  hd.endElement("","","TO");
		  
		  hd.endElement("","","TERM");
		}
		hd.endElement("","","FUZZIFY");
		hd.endDocument();
		fos.close();	
		}
		catch (Exception e)
		{
			e.getMessage();
		}
	}
	private FuzzySet[] retrieveFuzzySetsFromChromosome(IChromosome a_Chromosome)
	{
//		System.out.println("TEST:"+a_Chromosome.size());
		int numFuzzysets = (a_Chromosome.size() + 1) /3;
		FuzzySet[] myFuzzySets = new FuzzySet[numFuzzysets];

		double anAllele;
		int k=1, counter=0;
		
	    double r = (getMaxVal()-getMinVal())/(numFuzzysets+1);
		File LogFile = new File("LastTrap.txt");
		PrintWriter pw = null;

//		try{
		try {
			pw = new PrintWriter(new FileWriter(LogFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (k<a_Chromosome.size())
		{
			if (k==1)
			{
				myFuzzySets[counter] = new FuzzySet();
				myFuzzySets[counter].setName("trapezoid1");
				myFuzzySets[counter].setPkg("pkg");				

				myFuzzySets[counter].setFrom(getMinVal());

				anAllele = Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				pw.println("0" + Double.valueOf(a_Chromosome.getGene(k-1).getAllele().toString()));
				pw.println("-1." + Double.valueOf(a_Chromosome.getGene(k).getAllele().toString()));
				pw.println("-2." + Double.valueOf(a_Chromosome.getGene(k+1).getAllele().toString()));
				pw.println("-3." + Double.valueOf(a_Chromosome.getGene(k+2).getAllele().toString()));
				pw.println("-4." + Double.valueOf(a_Chromosome.getGene(k+3).getAllele().toString()));
				
				if (anAllele==getMinVal())
					anAllele = Math.min(anAllele + r, anAllele + (Double.valueOf(a_Chromosome.getGene(k+3).getAllele().toString()) - getMinVal())/2);
				myFuzzySets[counter].setR((anAllele));
				k++;

				anAllele = myFuzzySets[counter].getR() + Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				
				pw.println("1." + Double.valueOf(a_Chromosome.getGene(k+2).getAllele().toString()));
				pw.println("2." + anAllele + " " + r);
				if (anAllele <= myFuzzySets[counter].getR())
					anAllele = Math.min(anAllele + r, Double.valueOf(a_Chromosome.getGene(k+2).getAllele().toString()));
				myFuzzySets[counter].setTo(anAllele);
				k++;
				counter++;
			}
			else if (k == a_Chromosome.size() - 2)
			{
				myFuzzySets[counter] = new FuzzySet();
				myFuzzySets[counter].setName("trapezoid2");
				myFuzzySets[counter].setPkg("pkg");				
				anAllele = Double.valueOf(a_Chromosome.getGene(k+1).getAllele().toString()) - Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
//				if (anAllele == myFuzzySets[counter-1].getTo() || anAllele < myFuzzySets[counter-1].getR())
            		pw.println();
            		pw.println("initial value of r: " + r);
            		pw.println("initial value of FROM: " + anAllele);
            		pw.println("initial value of TO counter -1 : " + myFuzzySets[counter-1].getTo());
            		pw.println("initial value of R counter -1 : " + myFuzzySets[counter-1].getR());
            		
            	if (anAllele == myFuzzySets[counter-1].getTo())
					anAllele = Math.max((anAllele - r),(myFuzzySets[counter-1].getR()));
        		pw.println("updated value of FROM: " + anAllele);
				myFuzzySets[counter].setFrom(anAllele);
				k++;

				anAllele = Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				if (anAllele==getMaxVal())
					anAllele = Math.max(anAllele - r, myFuzzySets[counter-1].getTo());
				myFuzzySets[counter].setR((anAllele));
				k++;

				myFuzzySets[counter].setTo(getMaxVal());
			}
			else
			{
				myFuzzySets[counter] = new FuzzySet();
				myFuzzySets[counter].setName("triangle");
				myFuzzySets[counter].setPkg("pkg");				
				anAllele = Double.valueOf(a_Chromosome.getGene(k+1).getAllele().toString()) - Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				if (anAllele == myFuzzySets[counter-1].getTo())
				{
					anAllele = Math.max(anAllele - r,myFuzzySets[counter-1].getR());
				}
				myFuzzySets[counter].setFrom(anAllele);
				k++;
				
				anAllele = Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				myFuzzySets[counter].setR((anAllele));
				k++;
				
				anAllele = anAllele + Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				if (anAllele == myFuzzySets[counter].getR())
					anAllele = Math.min(anAllele + r, Double.valueOf(a_Chromosome.getGene(k+2).getAllele().toString()));
				myFuzzySets[counter].setTo(anAllele);
				k++;
				counter++;
			}
    	}
		pw.close();			
//		}
//    	catch(Exception ex2){}
		int[] support = supportOfFuzzySets(myFuzzySets);
		double[][] temp = new double[myFuzzySets.length][];
		for (int i=0; i<temp.length; i++)
			temp[i] = new double[support[i]];
		
		for (int i=0; i<myFuzzySets.length; i++)
		{
			counter = 0;
			for (int j=0; j<m_Data.length; j++)
			{
				if (myFuzzySets[i].getFrom()<=m_Data[j] && m_Data[j]<=myFuzzySets[i].getTo())
				{
					temp[i][counter] = m_Data[j];
					counter++;
				}
			}
		}
		for (int i=0; i<myFuzzySets.length; i++){
			myFuzzySets[i].setLabel("mf"+i);
			myFuzzySets[i].setM_Data(temp[i]);
		}
		return myFuzzySets;
	}
	private double[][] calculateMembershipMatrix(FuzzySet[] fuzzySets)
	{
		double[][] U_Matrix = new double[m_Data.length][];
		for (int i=0; i<U_Matrix.length; i++)
		{
			U_Matrix[i] = new double[fuzzySets.length];
			for (int j=0; j<fuzzySets.length; j++)
			{
				U_Matrix[i][j] = fuzzySets[j].calculateDegreeOfMembership(m_Data[i]);
			}			
		}
		return U_Matrix;
	}
	private int[] supportOfFuzzySets(FuzzySet[] myFuzzySets)
	{
		int[] support = new int[myFuzzySets.length];
		double totalSupport = 0;
		for (int j=0; j<myFuzzySets.length; j++)
			support[j] = 0;
		for (int i=0; i<m_Data.length; i++) {
			for (int j=0; j<myFuzzySets.length; j++){
				if(myFuzzySets[j] == null) {
					System.out.println("myFuzzySets["+j+"] is null");
				}else {
				if (myFuzzySets[j].getFrom()<=m_Data[i] && m_Data[i]<=myFuzzySets[j].getTo()){
					support[j] = support[j] + 1;
				}
				}
			}
		}
		return support;
	}
	public IChromosome findBestSolution(Genotype myGenotype, double[] myValues)
	{
		int solutionIdxWithMaxVote = 0;
		return myGenotype.getPopulation().getChromosome(solutionIdxWithMaxVote);
	}
	public IChromosome findBestSolution2(Genotype myGenotype, double[] myValues)
	{
		int size = myGenotype.getPopulation().getChromosomes().size();
		int i = 0;

		double[][] voteMatrix = new double[size][6];
		IChromosome a_Solution = null;
		while (i<size) 
		{
			a_Solution = myGenotype.getPopulation().getChromosome(i);
			FuzzySet[] bestFuzzySetSolutionCandidate = retrieveFuzzySetsFromChromosome(a_Solution);  
				
			voteMatrix[i][0] = Partition_Entropy_Coefficient(bestFuzzySetSolutionCandidate);

			voteMatrix[i][1] = (-1) * Xie_Beni_Index(bestFuzzySetSolutionCandidate);

			voteMatrix[i][2] = (-1) * Fukuyama_Sugeno_Index(bestFuzzySetSolutionCandidate,2);

			voteMatrix[i][3] = (-1) * Fuzzy_Hyper_Volume(bestFuzzySetSolutionCandidate,2);

			voteMatrix[i][4] = (-1) * Average_Partition_Density(bestFuzzySetSolutionCandidate,2);

			voteMatrix[i][5] = Partition_Density_Index(bestFuzzySetSolutionCandidate,2);
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
	private double Partition_Coefficient(FuzzySet[] myFuzzySets)
	{
		double sum = 0;
		for (int i=0; i<m_Data.length; i++)
		{
			for (int j=0; j<myFuzzySets.length; j++)
			{
				sum = sum + Math.pow(myFuzzySets[j].calculateDegreeOfMembership(m_Data[i]),2);
			}			
		}
		return (1/m_Data.length) * sum;
	}
	private double Partition_Entropy_Coefficient(FuzzySet[] myFuzzySets)
	{
		double sum = 0;
		double mf_value = 0;
		double log_value = 0;
		
		for (int i=0; i<m_Data.length; i++)
		{
			for (int j=0; j<myFuzzySets.length; j++)
			{
				mf_value = myFuzzySets[j].calculateDegreeOfMembership(m_Data[i]);
				log_value = Math.log10(mf_value);
				sum = sum - mf_value * log_value;
			}			
		}		
		return (1/m_Data.length) * sum;
	}
	private double Xie_Beni_Index(FuzzySet[] myFuzzySets)
	{
		double Xie_Beni = 0;
		for (int i=0; i<myFuzzySets.length; i++)
		{
			for (int j=0; j<m_Data.length; j++)
			{
				Xie_Beni = Xie_Beni + Math.pow(myFuzzySets[i].calculateDegreeOfMembership(m_Data[j]),2) * Math.pow(m_Data[j]-myFuzzySets[i].getR(), 2);
			}			
		}		
		double d_min = Math.abs(myFuzzySets[0].getR()-myFuzzySets[1].getR());
		for (int i=0; i<myFuzzySets.length-1; i++)
			for (int j=i+1; j<myFuzzySets.length; j++)
			{
				if (Math.abs(myFuzzySets[i].getR()-myFuzzySets[j].getR()) < d_min)
					d_min = Math.abs(myFuzzySets[i].getR()-myFuzzySets[j].getR());
			}
		Xie_Beni = Xie_Beni /(m_Data.length * d_min);
		return Xie_Beni;
	}
	private double Fukuyama_Sugeno_Index(FuzzySet[] myFuzzySets, int m)
	{
		double FSm = 0;
		double v = 0;
		
		for (int i=0; i<m_Data.length; i++)
			v = v + m_Data[i];

		v = v / m_Data.length;
		
		for (int i=0; i<m_Data.length; i++)
		{
			for (int j=0; j<myFuzzySets.length; j++)
			{
				FSm = FSm + Math.pow(myFuzzySets[j].calculateDegreeOfMembership(m_Data[i]), m) * Math.pow(m_Data[i] - myFuzzySets[j].getR(), 2) - Math.pow(myFuzzySets[j].getR() - v, 2);
			}			
		}		
		return FSm;
	}
	private double Fuzzy_Hyper_Volume(FuzzySet[] myFuzzySets, int m)
	{
		double sigma_1 = 0;
		double sigma_2 = 0;
		double V_j = 0;
		double mf_value = 0;

		for (int j=0; j<myFuzzySets.length; j++)
		{
			sigma_1 = 0;
			sigma_2 = 0;
			for (int i=0; i<m_Data.length; i++)
			{
				mf_value = Math.pow(myFuzzySets[j].calculateDegreeOfMembership(m_Data[i]), m);
				sigma_1 = sigma_1 +  mf_value * Math.pow(m_Data[i]-myFuzzySets[j].getR(), 2);
				sigma_2 = sigma_2 + mf_value;
			}
			V_j = V_j + Math.sqrt(Math.abs(sigma_1/sigma_2));
		}		
		
		return V_j;
	}
	private double Average_Partition_Density(FuzzySet[] myFuzzySets, int m)
	{
		double sigma_1 = 0;
		double S_j = 0;
		double V_j = 0;
		double mf_value = 0;
		double PA = 0;

		for (int j=0; j<myFuzzySets.length; j++)
		{
			sigma_1 = 0;
			S_j = 0;
			for (int i=0; i<m_Data.length; i++)
			{
				mf_value = Math.pow(myFuzzySets[j].calculateDegreeOfMembership(m_Data[i]), m);
				sigma_1 = sigma_1 +  mf_value * Math.pow(m_Data[i]-myFuzzySets[j].getR(), 2);
				S_j = S_j + mf_value;
			}
			V_j = Math.sqrt(Math.abs(sigma_1/S_j));
			PA = PA + (S_j/V_j);
		}
		PA = (1/myFuzzySets.length) * PA;
		return PA;
	}
	private double Partition_Density_Index(FuzzySet[] myFuzzySets, int m)
	{
		double PD = 0;
		double mf_value = 0;
		for (int j=0; j<myFuzzySets.length; j++)
		{
			for (int i=0; i<m_Data.length; i++)
			{
				mf_value = mf_value + Math.pow(myFuzzySets[j].calculateDegreeOfMembership(m_Data[i]), m);
			}
		}
		PD = mf_value / Fuzzy_Hyper_Volume(myFuzzySets, m);
		return PD;
	}
}