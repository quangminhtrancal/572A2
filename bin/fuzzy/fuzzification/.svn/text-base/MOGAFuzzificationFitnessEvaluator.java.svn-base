package fuzzy.fuzzification;

import java.util.List;
import java.util.Vector;

import org.jgap.Chromosome;
import org.jgap.FitnessEvaluator;
import org.jgap.IChromosome;

import fuzzy.FuzzySet;
import fuzzy.clustering.Cluster;

public class MOGAFuzzificationFitnessEvaluator implements FitnessEvaluator {

	double[] m_Values;

	public MOGAFuzzificationFitnessEvaluator(double[] AttValues)
	{
		if (AttValues == null)
			throw new IllegalArgumentException("Not Valid Attribute Values Array!");
		m_Values = AttValues;
	}  

	@Override
	public boolean isFitter(final double a_fitness_value1,
			final double a_fitness_value2) {
		throw new RuntimeException("Not supported for multi-objectives!");
	}


	@Override
	public boolean isFitter(IChromosome a_chrom1, IChromosome a_chrom2) 
	{
		FuzzySet[] a_FSets1 = retrieveFuzzySetsFromChromosome(a_chrom1);
		FuzzySet[] a_FSets2 = retrieveFuzzySetsFromChromosome(a_chrom2);


		// Evaluate values to fill vector of multiobjectives with.
		double y1 = formula(1, a_FSets1);
		List l = new Vector();
		l.add(new Double(y1));
		double y2 = formula(2, a_FSets1);
		l.add(new Double(y2));
		//double y3 = formula(3, a_FSets1);
		//l.add(new Double(y3));
		((Chromosome)a_chrom1).setMultiObjectives(l);

		l.clear();
		y1 = formula(1, a_FSets2);
		l.add(new Double(y1));
		y2 = formula(2, a_FSets2);
		l.add(new Double(y2));
		//y3 = formula(3, a_FSets2);
		//l.add(new Double(y3));
		((Chromosome)a_chrom2).setMultiObjectives(l);

		List v1 = ( (Chromosome) a_chrom1).getMultiObjectives();
		List v2 = ( (Chromosome) a_chrom2).getMultiObjectives();
		int size = v1.size();
		if (size != v2.size()) {
			throw new RuntimeException("Size of objectives inconsistent!");
		}
		boolean better = false;
		for (int i = 0; i < size; i++) {
			double d1 = ( (Double) v1.get(i)).doubleValue();
			double d2 = ( (Double) v2.get(i)).doubleValue();

			if (d1 > d2) {
				better = true;
			}
			else if (d1 < d2) {
				return false;
			}
		}
		return better;
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
}
