package fuzzy.fuzzification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.Population;
import org.jgap.impl.DoubleGene;

import fuzzy.FuzzySet;
import fuzzy.clustering.Cluster;
import fuzzy.clustering.MOGAClusteringFitnessFunction;

public class MOGAFuzzificationFitnessFunction extends BulkFitnessFunction
{

	private double[] m_Values;

	public MOGAFuzzificationFitnessFunction(double[] AttValues)
	{
		if (AttValues == null)
			throw new IllegalArgumentException("Not Valid Attribute Values Array!");
		m_Values = AttValues;
	}

	/**
	 * Determine the fitness of the given Chromosome instance. The higher the
	 * returned value, the fitter the instance. This method should always
	 * return the same fitness value for two equivalent Chromosome instances.
	 *
	 * @param a_subject the population of chromosomes to evaluate
	 *
	 * @author Klaus Meffert
	 * @since 2.6
	 */
	public void evaluate(Population a_subject) {

//		System.out.println(" ----- Before ----- ");
//		printPopulation(a_subject);
		mapPopulationSchema(a_subject,m_Values);
//		System.out.println(" ----- After ----- ");
//		printPopulation(a_subject);
		Iterator it = a_subject.getChromosomes().iterator();
		while (it.hasNext()) {
			IChromosome a_chrom1 = (IChromosome) it.next();
			FuzzySet[] a_FuzzySets = retrieveFuzzySetsFromChromosome(a_chrom1);
			// evaluate values to fill vector of multiobjectives with
			double y1 = formula(1, a_FuzzySets);
			List l = new Vector();
			l.add(new Double(y1));
			double y2 = formula(2, a_FuzzySets);
			l.add(new Double(y2));
			//double y3 = formula(3, a_FuzzySets);
			//l.add(new Double(y3));      
			( (Chromosome) a_chrom1).setMultiObjectives(l);
		}
	}
	public void printPopulation(Population a_subject)
	{
		for (int i=0;i<a_subject.size();i++)
		{
			System.out.print("Chromosome " + (i+1) + " :");
			for (int j=0;j<a_subject.getChromosome(0).size();j++)
			{
				System.out.print(" " + a_subject.getChromosome(i).getGene(j).getAllele());			
			}
			System.out.println();
		}
	}		
	private void mapPopulationSchema(Population a_subject, double[] values)
	{
		double min = getMinValue(values);
		double max = getMaxValue(values);
		double[] geneMaxValueArray = new double[a_subject.getChromosome(0).size()];
		Gene[] genes;
		Gene a_gene;
		Object an_Allele;
			
		IChromosome chrom;
		IChromosome newChrom = null;

		for (int i=0; i<a_subject.getChromosome(0).size(); i++)
			geneMaxValueArray[i] = getGeneMaxValueInCurrentPop(a_subject, i);
		
		for (int i=0; i<a_subject.size(); i++)
		{
			Gene[] newGenes = new Gene[a_subject.getChromosome(0).getGenes().length];
			//= a_subject.getChromosome(i).getGenes();
			chrom = (IChromosome) a_subject.getChromosome(i);
			genes = chrom.getGenes(); 
			
			try {
				newChrom = new Chromosome(a_subject.getConfiguration());
			} catch (InvalidConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			//			for gene 0 which is the number of fuzzy sets
			a_gene = genes[0];
			an_Allele = a_gene.getAllele();

			try {
				newGenes[0] = new DoubleGene(a_subject.getConfiguration(),Double.valueOf(an_Allele.toString()),Double.valueOf(an_Allele.toString()));
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newGenes[0].setAllele(an_Allele);
			
			//			for gene 2 which is a R
			a_gene = genes[2];
			an_Allele = mapGeneValueToRange(min, max, Double.valueOf(a_gene.getAllele().toString()), geneMaxValueArray[2]);

			try {
				newGenes[2] = new DoubleGene(a_subject.getConfiguration(),Double.valueOf(an_Allele.toString()),Double.valueOf(an_Allele.toString()));
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newGenes[2].setAllele(an_Allele);
						
			//			for gene 1 which is a right side base gene
			a_gene = genes[1];
			an_Allele = mapGeneValueToRange(Double.valueOf(genes[2].getAllele().toString()), max, Double.valueOf(a_gene.getAllele().toString()), geneMaxValueArray[1]);
			try {
				newGenes[1] = new DoubleGene(a_subject.getConfiguration(),Double.valueOf(an_Allele.toString()),Double.valueOf(an_Allele.toString()));
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			newGenes[1].setAllele(an_Allele);
			
			
			for (int j=3; j<genes.length-2; j=j+3)
			{
				// R
				a_gene = genes[j+1];
				an_Allele = mapGeneValueToRange(min,max, Double.valueOf(a_gene.getAllele().toString()), geneMaxValueArray[j+1]);
				try {
					newGenes[j+1] = new DoubleGene(a_subject.getConfiguration(),Double.valueOf(an_Allele.toString()),Double.valueOf(an_Allele.toString()));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				newGenes[j+1].setAllele(an_Allele);
//				a_gene.setAllele(an_Allele);

				// FROM range
				a_gene = genes[j];
				an_Allele = mapGeneValueToRange(min, Double.valueOf(genes[j+1].getAllele().toString()),
						Double.valueOf(a_gene.getAllele().toString()), geneMaxValueArray[j]);
				try {
					newGenes[j] = new DoubleGene(a_subject.getConfiguration(),Double.valueOf(an_Allele.toString()),Double.valueOf(an_Allele.toString()));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[j].setAllele(an_Allele);
//				a_gene.setAllele(an_Allele);

				// TO range
				a_gene = genes[j+2];
				an_Allele = mapGeneValueToRange(Double.valueOf(genes[j+1].getAllele().toString()), max,
						Double.valueOf(a_gene.getAllele().toString()), geneMaxValueArray[j+2]);
				try {
					newGenes[j+2] = new DoubleGene(a_subject.getConfiguration(),Double.valueOf(an_Allele.toString()),Double.valueOf(an_Allele.toString()));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				newGenes[j+2].setAllele(an_Allele);				
//				a_gene.setAllele(an_Allele);			
			}
			//			for gene (length -1) which is a R
			a_gene = genes[genes.length-1];
			an_Allele = mapGeneValueToRange(min, max, Double.valueOf(a_gene.getAllele().toString()), geneMaxValueArray[genes.length-1]);
			try {
				newGenes[newGenes.length-1] = new DoubleGene(a_subject.getConfiguration(),Double.valueOf(an_Allele.toString()),Double.valueOf(an_Allele.toString()));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			newGenes[newGenes.length-1].setAllele(an_Allele);				
			
//			a_gene.setAllele(an_Allele);

			//			for gene (length - 2) which is a right side base gene
			a_gene = genes[genes.length-2];
			an_Allele = mapGeneValueToRange(min,Double.valueOf(genes[genes.length-1].getAllele().toString()),
					Double.valueOf(a_gene.getAllele().toString()), geneMaxValueArray[genes.length-2]);
			
			try {
				newGenes[newGenes.length-2] = new DoubleGene(a_subject.getConfiguration(),Double.valueOf(an_Allele.toString()),Double.valueOf(an_Allele.toString()));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			newGenes[newGenes.length-2].setAllele(an_Allele);				
//			a_gene.setAllele(an_Allele);
			a_subject.getChromosomes().remove(i);
			try {
				newChrom.setGenes(newGenes);
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			a_subject.addChromosome(chrom);
		}
	}

	private double mapGeneValueToRange(double minVal, double maxVal, double Vs, double VsMax)
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

	public static Vector getVector(IChromosome a_chrom) {
		Vector result = new Vector();
		List mo = ( (Chromosome) a_chrom).getMultiObjectives();
		Double d = (Double) mo.get(0);
		result.add(d);
		d = (Double) mo.get(1);
		result.add(d);
		//d = (Double) mo.get(2);
		//result.add(d);
		return result;
	}
	
	private double formula(int a_index, FuzzySet[] a_FuzzySets) 
	{
		if (a_index == 1) 
		{
			return (-1.0) * averageSupportOfFuzzySets(a_FuzzySets);
		}
		else if (a_index == 2)
		{
			return averageSizeOfOverlap(a_FuzzySets);		  
		}
		else
			return 0;
	}
	
	private FuzzySet[] retrieveFuzzySetsFromChromosome(IChromosome a_Chromosome)
	{
		int numFuzzysets = (a_Chromosome.size() + 1) /3;
		FuzzySet[] myFuzzySets = new FuzzySet[numFuzzysets];

		double anAllele;
		int k=1, counter=0;

		while (k<a_Chromosome.size())
		{
			if (k==1)
			{
				myFuzzySets[counter] = new FuzzySet();
				myFuzzySets[counter].setFrom(getMinValue());
				anAllele = Double.valueOf(a_Chromosome.getGene(k+1).getAllele().toString()) + Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				myFuzzySets[counter].setTo(anAllele);
				k++;
				anAllele = Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				myFuzzySets[counter].setR((anAllele));
				k++;
				counter++;
			}
			else if (k == a_Chromosome.size() - 2)
			{
				myFuzzySets[counter] = new FuzzySet();
				anAllele = Double.valueOf(a_Chromosome.getGene(k+1).getAllele().toString()) - Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				myFuzzySets[counter].setFrom(anAllele);
				k++;
				anAllele = Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				myFuzzySets[counter].setR((anAllele));
				k++;
				myFuzzySets[counter].setTo(getMaxValue());
			}
			else
			{
				myFuzzySets[counter] = new FuzzySet();
				anAllele = Double.valueOf(a_Chromosome.getGene(k+1).getAllele().toString()) - Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				myFuzzySets[counter].setFrom(anAllele);
				k++;
				anAllele = Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				myFuzzySets[counter].setR((anAllele));
				k++;
				anAllele = anAllele + Double.valueOf(a_Chromosome.getGene(k).getAllele().toString());
				myFuzzySets[counter].setTo(anAllele);
				k++;
				counter++;
			}
		}
		int[] support = supportOfFuzzySets(myFuzzySets);
		double[][] temp = new double[myFuzzySets.length][];
		for (int i=0; i<temp.length; i++)
			temp[i] = new double[support[i]];
		
		for (int i=0; i<myFuzzySets.length; i++)
		{
			counter = 0;
			for (int j=0; j<m_Values.length; j++)
			{
				if (myFuzzySets[i].getFrom()<=m_Values[j] && m_Values[j]<=myFuzzySets[i].getTo())
				{
					temp[i][counter] = m_Values[j];
					counter++;
				}
			}
		}
		for (int i=0; i<myFuzzySets.length; i++)
			myFuzzySets[i].setM_Data(temp[i]);
		
		return myFuzzySets;
	}
	private int sizeOfOverlap(FuzzySet a_FS1, FuzzySet a_FS2)
	{
		int sizeOfOL = 0;
		for (int i=0; i<a_FS1.getM_Data().length; i++)
			for (int j=0; j<a_FS2.getM_Data().length; j++)
				if (a_FS1.getM_Data()[i] == a_FS2.getM_Data()[j])
					sizeOfOL = sizeOfOL + 1;
		return sizeOfOL;
	}
	private int totalSizeOfOverlap(FuzzySet[] myFuzzySet)
	{
		int totalSizeOfOL = 0;
		for (int i=0; i<myFuzzySet.length - 1; i++)
			for (int j=i; j<myFuzzySet.length; j++)
				totalSizeOfOL = sizeOfOverlap(myFuzzySet[i], myFuzzySet[j]);
		return totalSizeOfOL;
	}
	private double averageSizeOfOverlap(FuzzySet[] myFuzzySet)
	{
		int totalSizeOfOL = 0;
		int numOfPossibleOLs = (myFuzzySet.length * (myFuzzySet.length - 1)) /2;
		for (int i=0; i<myFuzzySet.length - 1; i++)
			for (int j=i; j<myFuzzySet.length; j++)
				totalSizeOfOL = sizeOfOverlap(myFuzzySet[i], myFuzzySet[j]);
		return (double)totalSizeOfOL / (double)numOfPossibleOLs;
	}
	private double getMinValue()
	{
		double min = m_Values[0];
		for (int i=0; i<m_Values.length; i++)
			if (m_Values[i]<min)
				min = m_Values[i];
		return min;			
	}
	private double getMaxValue()
	{
		double max = m_Values[0];
		for (int i=1; i<m_Values.length; i++)
			if (m_Values[i]>max)
				max = m_Values[i];
		return max;			
	}
	private int[] supportOfFuzzySets(FuzzySet[] myFuzzySets)
	{
		int[] support = new int[myFuzzySets.length];
		double totalSupport = 0;
		for (int j=0; j<myFuzzySets.length; j++)
			support[j] = 0;
		for (int i=0; i<m_Values.length; i++)
			for (int j=0; j<myFuzzySets.length; j++)
				if (myFuzzySets[j].getFrom()<=m_Values[i] && m_Values[i]<=myFuzzySets[j].getTo())
					support[j] = support[j] + 1;
		return support;
	}
	private double averageSupportOfFuzzySets(FuzzySet[] myFuzzySets)
	{
		int[] support = new int[myFuzzySets.length];
		double totalSupport = 0;
		for (int j=0; j<myFuzzySets.length; j++)
			support[j] = 0;
		for (int i=0; i<m_Values.length; i++)
			for (int j=0; j<myFuzzySets.length; j++)
				if (myFuzzySets[j].getFrom()<=m_Values[i] && m_Values[i]<=myFuzzySets[j].getTo())
					support[j] = support[j] + 1;
		for (int j=0; j<support.length; j++)
			totalSupport = totalSupport + support[j];
		return totalSupport / myFuzzySets.length;
	}
	public Object clone() 
	{
		return new MOGAFuzzificationFitnessFunction(m_Values);
	}
}