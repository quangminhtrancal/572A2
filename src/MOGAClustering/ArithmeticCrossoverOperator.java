package MOGAClustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.jgap.*;
import org.jgap.impl.DoubleGene;

public class ArithmeticCrossoverOperator 
extends BaseGeneticOperator implements Comparable{

	/**
	 * The current crossover rate used by this crossover operator (mutual
	 * exclusive to m_crossoverRatePercent and m_crossoverRateCalc).
	 */
	private int m_crossoverRate;

	/**
	 * Crossover rate in percentage of population size (mutual exclusive to
	 * m_crossoverRate and m_crossoverRateCalc).
	 */
	private double m_crossoverRatePercent;

	/**
	 * Calculator for dynamically determining the crossover rate (mutual exclusive
	 * to m_crossoverRate and m_crossoverRatePercent)
	 */
	private IUniversalRateCalculator m_crossoverRateCalc;

	/**
	 * true: x-over before and after a randomly chosen x-over point
	 * false: only x-over after the chosen point.
	 */
	private boolean m_allowFullCrossOver;

	/**
	 * true: also x-over chromosomes with age of zero (newly created chromosomes)
	 */
	private boolean m_xoverNewAge;


	/**
	 * Constructs a new instance of this CrossoverOperator without a specified
	 * crossover rate, this results in dynamic crossover rate being turned off.
	 * This means that the crossover rate will be fixed at populationsize/2.<p>
	 * Attention: The configuration used is the one set with the static method
	 * Genotype.setConfiguration.
	 *
	 * @throws InvalidConfigurationException
	 *
	 * @author Chris Knowles
	 * @since 2.0
	 */
	public ArithmeticCrossoverOperator()
	throws InvalidConfigurationException {
		super(Genotype.getStaticConfiguration());
		init();
	}

	/**
	 * Constructs a new instance of this CrossoverOperator without a specified
	 * crossover rate, this results in dynamic crossover rate being turned off.
	 * This means that the crossover rate will be fixed at populationsize/2.
	 *
	 * @param a_configuration the configuration to use
	 * @throws InvalidConfigurationException
	 *
	 * @author Klaus Meffert
	 * @since 3.0
	 */

	public ArithmeticCrossoverOperator(Configuration a_configuration)
	throws InvalidConfigurationException {
		super(a_configuration);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * Initializes certain parameters.
	 *
	 * @author Klaus Meffert
	 * @since 3.3.2
	 */
	protected void init() {
		// Set the default crossoverRate.
		// ------------------------------
		m_crossoverRate = 6;
		m_crossoverRatePercent = -1;
		setCrossoverRateCalc(null);
		setAllowFullCrossOver(true);
		setXoverNewAge(true);
	}

	/**
	 * Sets the crossover rate calculator.
	 *
	 * @param a_crossoverRateCalculator the new calculator
	 *
	 * @author Chris Knowles
	 * @since 2.0
	 */
	private void setCrossoverRateCalc(final IUniversalRateCalculator
			a_crossoverRateCalculator) {
		m_crossoverRateCalc = a_crossoverRateCalculator;
		if (a_crossoverRateCalculator != null) {
			m_crossoverRate = -1;
			m_crossoverRatePercent = -1d;
		}
	}

	/**
	 * @param a_allowFullXOver x-over before and after a randomly chosen point
	 *
	 * @author Klaus Meffert
	 * @since 3.3.2
	 */
	public void setAllowFullCrossOver(boolean a_allowFullXOver) {
		m_allowFullCrossOver = a_allowFullXOver;
	}

	/**
	 * @param a_xoverNewAge true: also x-over chromosomes with age of zero (newly
	 * created chromosomes)
	 *
	 * @author Klaus Meffert
	 * @since 3.3.2
	 */
	public void setXoverNewAge(boolean a_xoverNewAge) {
		m_xoverNewAge = a_xoverNewAge;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	/**
	 * Does the crossing over.
	 *
	 * @param a_population the population of chromosomes from the current
	 * evolution prior to exposure to crossing over
	 * @param a_candidateChromosomes the pool of chromosomes that have been
	 * selected for the next evolved population
	 *
	 * @author Neil Rotstan
	 * @author Klaus Meffert
	 * @since 2.0
	 */

	public void operate(final Population a_population,
			final List a_candidateChromosomes) {

		// Work out the number of crossovers that should be performed.
		// -----------------------------------------------------------
		int size = Math.min(getConfiguration().getPopulationSize(),
				a_population.size());
		int numCrossovers = 0;
		if (m_crossoverRate >= 0) {
			numCrossovers = size / m_crossoverRate;
		}
		else if (m_crossoverRateCalc != null) {
			numCrossovers = size / m_crossoverRateCalc.calculateCurrentRate();
		}
		else {
			numCrossovers = (int) (size * m_crossoverRatePercent);
		}
		RandomGenerator generator = getConfiguration().getRandomGenerator();
		IGeneticOperatorConstraint constraint = getConfiguration().
		getJGAPFactory().getGeneticOperatorConstraint();
		// For each crossover, grab two random chromosomes, pick a random
		// locus (gene location), and then swap that gene and all genes
		// to the "right" (those with greater loci) of that gene between
		// the two chromosomes.
		// --------------------------------------------------------------
		int index1, index2;
		for (int i = 0; i < numCrossovers; i++) {
			index1 = generator.nextInt(size);
			index2 = generator.nextInt(size);
			IChromosome chrom1 = a_population.getChromosome(index1);
			IChromosome chrom2 = a_population.getChromosome(index2);
			// Verify that crossover is allowed.
			// ---------------------------------
			if (!isXoverNewAge() && chrom1.getAge() < 1 && chrom2.getAge() < 1) {
				// Crossing over two newly created chromosomes is not seen as helpful
				// here.
				// ------------------------------------------------------------------
				continue;
			}
			if (constraint != null) {
				List v = new Vector();
				v.add(chrom1);
				v.add(chrom2);
				if (!constraint.isValid(a_population, v, this)) {
					// Constraint forbids crossing over.
					// ---------------------------------
					continue;
				}
			}
			// Clone the chromosomes.
			// ----------------------
			IChromosome firstMate = (IChromosome) chrom1.clone();
			IChromosome secondMate = (IChromosome) chrom2.clone();
			// Cross over the chromosomes.
			// ---------------------------
			doCrossover(firstMate, secondMate, a_candidateChromosomes, generator, a_population);
		}
	}
	protected void doCrossover(IChromosome firstMate, IChromosome secondMate,
			List a_candidateChromosomes,
			RandomGenerator generator, final Population a_population) {

		double alpha = 0.3;
		Gene[] firstGenes = firstMate.getGenes();
		Gene[] secondGenes = secondMate.getGenes();

		Gene gene1;
		Gene gene2;
		Object firstAllele;
		Object secondAllele;
		
		Gene[] newGenes1 = new Gene[firstGenes.length];
		Gene[] newGenes2 = new Gene[secondGenes.length];
		
		try{
			firstMate.setGenes(newGenes1);
			secondMate.setGenes(newGenes2);
		}catch(Exception e){
			e.printStackTrace();
		}

		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("CrossOver.log", true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			out.write("------------ First Mate (Before)-----------------");
			out.newLine();
			for (int i=0; i<firstMate.size(); i++)
			{
				out.write(firstGenes[i].getAllele() + "  ");
				out.write("");
			}
			out.newLine();
			out.write("------------ Second Mate (Before)-----------------");
			out.newLine();
			for (int i=0; i<secondMate.size(); i++)
			{
				out.write(secondGenes[i].getAllele() + "  ");
			}
			out.newLine();		
		}
			catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		int effectiveLength = firstGenes.length;

		// Generate new genes using arithmetic crossover operation
		// ---------------
		for (int j = 0; j < effectiveLength; j++) {
			gene1 = firstGenes[j];
			gene2 = secondGenes[j];

			firstAllele = alpha * Double.valueOf(gene1.getAllele().toString())
					+ (1 - alpha)* Double.valueOf(gene2.getAllele().toString());
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString())
					+ (1 - alpha)* Double.valueOf(gene1.getAllele().toString());

			try {
				newGenes1[j] = new DoubleGene(gene1.getConfiguration());
				newGenes1[j].setAllele(firstAllele);
				newGenes2[j] = new DoubleGene(gene2.getConfiguration());
				newGenes2[j].setAllele(secondAllele);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			out.write("------------ First Mate (After)-----------------");
			out.newLine();
			for (int i=0; i<effectiveLength; i++)
			{
				out.write(newGenes1[i].getAllele() + "  ");
			}
			out.newLine();		
			out.write("------------ Second Mate (After)-----------------");
			out.newLine();		
			for (int i=0; i<effectiveLength; i++)
			{
				out.write(newGenes2[i].getAllele() + "  ");
			}
			out.newLine();		
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Add the modified chromosomes to the candidate pool so that
		// they'll be considered for natural selection during the next
		// phase of evolution.
		// -----------------------------------------------------------
		a_candidateChromosomes.add(firstMate);
		a_candidateChromosomes.add(secondMate);
	}
	protected void doCrossover_new_old(IChromosome firstMate, IChromosome secondMate,
			List a_candidateChromosomes,
			RandomGenerator generator, final Population a_population) {

		double alpha = 0.5;
		Gene[] firstGenes = firstMate.getGenes();
		Gene[] secondGenes = secondMate.getGenes();

		Gene gene1;
		Gene gene2;
		Object firstAllele;
		Object secondAllele;

		//      gene 0 is the number of fuzzy sets in each chromosome

		gene1 = firstGenes[0];
		gene2 = secondGenes[0];

		File outFile = new File("C:\\CrossOver.log");
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(outFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.println("------------ First Mate (Before)-----------------");
		for (int i=0; i<firstMate.size(); i++)
		{
			out.print(firstMate.getGenes()[i].getAllele() + "  ");
		}
		out.println();		
		out.println("------------ Second Mate (Before)-----------------");
		for (int i=0; i<firstMate.size(); i++)
		{
			out.print(firstMate.getGenes()[i].getAllele() + "  ");
		}
		out.println();		

		int firstMateNumOfClusters = (int)(alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString()));
		int secondMateNumOfClusters = (int)(alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString()));


		int firstGenesEffectiveLength = 3 * (firstMateNumOfClusters - 2) + 4 + 1;
		int secondGenesEffectiveLength = 3 * (secondMateNumOfClusters - 2) + 4 + 1;

		// Generate new genes using arithmetic crossover operation
		// ---------------
		if (firstGenesEffectiveLength <= secondGenesEffectiveLength)
		{	
			//			for gene 2 which is a R
			gene1 = firstGenes[2];
			gene2 = secondGenes[2];

			firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
			firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, 2));
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
			secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, 2));
			gene1.setAllele(firstAllele);
			gene2.setAllele(secondAllele);

			//			for gene 1 which is a right side base gene
			gene1 = firstGenes[1];
			gene2 = secondGenes[1];
			firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
			firstAllele = mapToRange(Double.valueOf(firstGenes[2].getAllele().toString()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, 1));
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
			secondAllele = mapToRange(Double.valueOf(secondGenes[2].getAllele().toString()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, 1));
			gene1.setAllele(firstAllele);
			gene2.setAllele(secondAllele);

			for (int j=3; j<firstGenesEffectiveLength-2; j=j+3)
			{
				// R
				gene1 = firstGenes[j+1];
				gene2 = secondGenes[j+1];
				firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
				firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
						Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j+1));
				secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
				secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
						Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j+1));
				gene1.setAllele(firstAllele);
				gene2.setAllele(secondAllele);


				// FROM range
				gene1 = firstGenes[j];
				gene2 = secondGenes[j];
				firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
				firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), Double.valueOf(firstGenes[j+1].getAllele().toString()),
						Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j));
				secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
				secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), Double.valueOf(secondGenes[j+1].getAllele().toString()),
						Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j));
				gene1.setAllele(firstAllele);
				gene2.setAllele(secondAllele);

				// TO range
				gene1 = firstGenes[j+2];
				gene2 = secondGenes[j+2];
				firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
				firstAllele = mapToRange(Double.valueOf(firstGenes[j+1].getAllele().toString()), getMaxValue(getConfiguration().getM_Values()),
						Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j+2));
				secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
				secondAllele = mapToRange(Double.valueOf(secondGenes[j+1].getAllele().toString()), getMaxValue(getConfiguration().getM_Values()),
						Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j+2));
				gene1.setAllele(firstAllele);
				gene2.setAllele(secondAllele);
			}

			//			for gene (length -1) which is a R
			gene1 = firstGenes[firstGenesEffectiveLength-1];
			gene2 = secondGenes[firstGenesEffectiveLength-1];

			firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
			firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, firstGenesEffectiveLength-1));
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
			secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, firstGenesEffectiveLength-1));
			gene1.setAllele(firstAllele);
			gene2.setAllele(secondAllele);

			//			for gene (length - 2) which is a right side base gene
			gene1 = firstGenes[firstGenesEffectiveLength-2];
			gene2 = secondGenes[firstGenesEffectiveLength-2];

			firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
			firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()),Double.valueOf(firstGenes[firstGenesEffectiveLength-1].getAllele().toString()),
					Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, firstGenesEffectiveLength-2));
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
			secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()),Double.valueOf(secondGenes[firstGenesEffectiveLength-1].getAllele().toString()),
					Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, firstGenesEffectiveLength-2));
			gene1.setAllele(firstAllele);
			gene2.setAllele(secondAllele);
		}
		else if ((firstGenesEffectiveLength > secondGenesEffectiveLength))
		{
			//			for gene 2 which is a R
			gene1 = firstGenes[2];
			gene2 = secondGenes[2];

			firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
			firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, 2));
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
			secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, 2));
			gene1.setAllele(firstAllele);
			gene2.setAllele(secondAllele);

			//			for gene 1 which is a right side base gene
			gene1 = firstGenes[1];
			gene2 = secondGenes[1];
			firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
			firstAllele = mapToRange(Double.valueOf(firstGenes[2].getAllele().toString()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, 1));
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
			secondAllele = mapToRange(Double.valueOf(secondGenes[2].getAllele().toString()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, 1));
			gene1.setAllele(firstAllele);
			gene2.setAllele(secondAllele);

			for (int j=3; j<secondGenesEffectiveLength-2; j=j+3)
			{
				// R
				gene1 = firstGenes[j+1];
				gene2 = secondGenes[j+1];
				firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
				firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
						Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j+1));
				secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
				secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
						Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j+1));
				gene1.setAllele(firstAllele);
				gene2.setAllele(secondAllele);


				// FROM range
				gene1 = firstGenes[j];
				gene2 = secondGenes[j];
				firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
				firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), Double.valueOf(firstGenes[j+1].getAllele().toString()),
						Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j));
				secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
				secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), Double.valueOf(secondGenes[j+1].getAllele().toString()),
						Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j));
				gene1.setAllele(firstAllele);
				gene2.setAllele(secondAllele);

				// TO range
				gene1 = firstGenes[j+2];
				gene2 = secondGenes[j+2];
				firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
				firstAllele = mapToRange(Double.valueOf(firstGenes[j+1].getAllele().toString()), getMaxValue(getConfiguration().getM_Values()),
						Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j+2));
				secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
				secondAllele = mapToRange(Double.valueOf(secondGenes[j+1].getAllele().toString()), getMaxValue(getConfiguration().getM_Values()),
						Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, j+2));
				gene1.setAllele(firstAllele);
				gene2.setAllele(secondAllele);
			}

			//			for gene (length -1) which is a R
			gene1 = firstGenes[secondGenesEffectiveLength-1];
			gene2 = secondGenes[secondGenesEffectiveLength-1];

			firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
			firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, secondGenesEffectiveLength-1));
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
			secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()), getMaxValue(getConfiguration().getM_Values()),
					Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, secondGenesEffectiveLength-1));
			gene1.setAllele(firstAllele);
			gene2.setAllele(secondAllele);

			//			for gene (length - 2) which is a right side base gene
			gene1 = firstGenes[secondGenesEffectiveLength-2];
			gene2 = secondGenes[secondGenesEffectiveLength-2];

			firstAllele  = alpha * Double.valueOf(gene1.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene2.getAllele().toString());
			firstAllele = mapToRange(getMinValue(getConfiguration().getM_Values()),Double.valueOf(firstGenes[secondGenesEffectiveLength-1].getAllele().toString()),
					Double.valueOf(firstAllele.toString()), getGeneMaxValueInCurrentPop(a_population, secondGenesEffectiveLength-2));
			secondAllele = alpha * Double.valueOf(gene2.getAllele().toString()) + (1 - alpha) * Double.valueOf(gene1.getAllele().toString());						
			secondAllele = mapToRange(getMinValue(getConfiguration().getM_Values()),Double.valueOf(secondGenes[secondGenesEffectiveLength-1].getAllele().toString()),
					Double.valueOf(secondAllele.toString()), getGeneMaxValueInCurrentPop(a_population, secondGenesEffectiveLength-2));
			gene1.setAllele(firstAllele);
			gene2.setAllele(secondAllele);
		}

		out.println("------------ First Mate (After)-----------------");
		for (int i=0; i<firstMate.size(); i++)
		{
			out.print(firstMate.getGenes()[i].getAllele() + "  ");
		}
		out.println();		
		out.println("------------ Second Mate (After)-----------------");
		for (int i=0; i<firstMate.size(); i++)
		{
			out.print(firstMate.getGenes()[i].getAllele() + "  ");
		}
		out.println();		
		double[] temp = getConfiguration().getM_Values();

		out.println("------------ Values-----------------");
		for (int i=0; i<temp.length; i++)
		{
			out.print(temp[i] + "  ");
		}
		out.println();		

		// Add the modified chromosomes to the candidate pool so that
		// they'll be considered for natural selection during the next
		// phase of evolution.
		// -----------------------------------------------------------
		a_candidateChromosomes.add(firstMate);
		a_candidateChromosomes.add(secondMate);
		out.close();


	}
	private double mapToRange(double minVal, double maxVal, double Vs, double VsMax)
	{
		return minVal + (Vs/VsMax)*(maxVal - minVal);
	}

	// returns the maximum value of a particular gene located at a specific index of chromosomes in the population
	private double getGeneMaxValueInCurrentPop(Population a_population, int index)
	{
		double max = 0, tmp = 0;
		for (int i=0; i<a_population.getChromosomes().size(); i++)
		{
			tmp = Double.valueOf(a_population.getChromosome(i).getGene(index).getAllele().toString()); 
			if ( tmp > max)
				max = tmp;
		}
		return max;
	}
	private double getMaxValue(double[] values)
	{
		double max = 0;
		for (int i=0; i<values.length; i++)
		{
			if ( values[i] > max)
				max = values[i];
		}
		return max;
	}
	private double getMinValue(double[] values)
	{
		double min = values[0];
		for (int i=1; i<values.length; i++)
		{
			if ( values[i] < min)
				min = values[i];
		}
		return min;
	}
	protected void doCrossover_old(IChromosome firstMate, IChromosome secondMate,
			List a_candidateChromosomes,
			RandomGenerator generator) {
		Gene[] firstGenes = firstMate.getGenes();
		Gene[] secondGenes = secondMate.getGenes();
		int locus = generator.nextInt(firstGenes.length);
		// Swap the genes.
		// ---------------
		Gene gene1;
		Gene gene2;
		Object firstAllele;
		for (int j = locus; j < firstGenes.length; j++) {
			// Make a distinction for ICompositeGene for the first gene.
			// ---------------------------------------------------------
			if (firstGenes[j] instanceof ICompositeGene) {
				// Randomly determine gene to be considered.
				// -----------------------------------------
				int index1 = generator.nextInt(firstGenes[j].size());
				gene1 = ( (ICompositeGene) firstGenes[j]).geneAt(index1);
			}
			else {
				gene1 = firstGenes[j];
			}
			// Make a distinction for the second gene if CompositeGene.
			// --------------------------------------------------------
			if (secondGenes[j] instanceof ICompositeGene) {
				// Randomly determine gene to be considered.
				// -----------------------------------------
				int index2 = generator.nextInt(secondGenes[j].size());
				gene2 = ( (ICompositeGene) secondGenes[j]).geneAt(index2);
			}
			else {
				gene2 = secondGenes[j];
			}
			firstAllele = gene1.getAllele();
			gene1.setAllele(gene2.getAllele());
			gene2.setAllele(firstAllele);
		}
		// Add the modified chromosomes to the candidate pool so that
		// they'll be considered for natural selection during the next
		// phase of evolution.
		// -----------------------------------------------------------
		a_candidateChromosomes.add(firstMate);
		a_candidateChromosomes.add(secondMate);
	}

	/**
	 * @return a_xoverNewAge true: also x-over chromosomes with age of zero (newly
	 * created chromosomes)
	 *
	 * @author Klaus Meffert
	 * @since 3.3.2
	 */
	public boolean isXoverNewAge() {
		return m_xoverNewAge;
	}
}
