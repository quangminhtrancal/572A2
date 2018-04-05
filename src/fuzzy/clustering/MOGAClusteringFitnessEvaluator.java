/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package fuzzy.clustering;

import java.util.*;

import org.jgap.*;
import org.jgap.impl.*;

/**
 * Fitness evaluator for multi objectives example.
 *
 * @author Klaus Meffert
 * @since 2.6
 */
public class MOGAClusteringFitnessEvaluator
    implements FitnessEvaluator {
  /** String containing the CVS revision. Read out via reflection!*/
  private final static String CVS_REVISION = "$Revision: 1.2 $";

  double[] m_Values;

  public MOGAClusteringFitnessEvaluator(double[] AttValues)
  {
	if (AttValues == null)
		throw new IllegalArgumentException("Not Valid Attribute Values Array!");
	m_Values = AttValues;
  }  
  /**
   * Not to be called in multi-objectives context!
   *
   * @param a_fitness_value1 ignored
   * @param a_fitness_value2 ignored
   * @return always a RuntimeException
   *
   * @author Klaus Meffert
   * @since 2.6
   */
  public boolean isFitter(final double a_fitness_value1,
                          final double a_fitness_value2) {
    throw new RuntimeException("Not supported for multi-objectives!");
  }

  public boolean isFitter(IChromosome a_chrom1, IChromosome a_chrom2) 
  {
	  Cluster[] a_Clusts1 = retrieveClusterInfoFromChromosome(a_chrom1);
	  Cluster[] a_Clusts2 = retrieveClusterInfoFromChromosome(a_chrom2);
	  
	  
	// Evaluate values to fill vector of multiobjectives with.
    double y1 = formula(1, a_Clusts1);
    List l = new Vector();
    l.add(new Double(y1));
    double y2 = formula(2, a_Clusts1);
    l.add(new Double(y2));
    double y3 = formula(3, a_Clusts1);
    l.add(new Double(y3));
    ((Chromosome)a_chrom1).setMultiObjectives(l);

    l.clear();
    y1 = formula(1, a_Clusts2);
    l.add(new Double(y1));
    y2 = formula(2, a_Clusts2);
    l.add(new Double(y2));
    y3 = formula(3, a_Clusts2);
    l.add(new Double(y3));
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
	private Cluster[] retrieveClusterInfoFromChromosome(IChromosome a_Chromosome)
	{
		Cluster[] myClusters;
		int numClusters = 0;
		Vector distinctClusts = new Vector();
		int anAllele;
		int newAllele;

		// This loop finds the distinct cluster labels and stores them in distinctClusts <Vector> 
		//System.out.print("Org. CH: "); 
		for (int geneIndex=0;geneIndex<a_Chromosome.size();geneIndex++)
		{
			anAllele = Integer.valueOf(a_Chromosome.getGenes()[geneIndex].getAllele().toString());
			//System.out.print(anAllele + " ");
			if (!distinctClusts.contains(anAllele))
				distinctClusts.add(anAllele);
		}
		
		// re-number the chromosome accoring to the distinct cluster labels
		//System.out.print("Ren. CH: "); 
		for (int geneIndex=0;geneIndex<a_Chromosome.size();geneIndex++)
		{
			anAllele = Integer.valueOf(a_Chromosome.getGenes()[geneIndex].getAllele().toString());
			newAllele = distinctClusts.indexOf(anAllele) + 1;
			a_Chromosome.getGenes()[geneIndex].setAllele(newAllele);
		}
		numClusters = max(a_Chromosome.getGenes());

		// A temporary array to store cluster elements
		double[][] temp = new double[numClusters][];

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
			if (temp[idx].length == 0)
				counter[idx] = 0;
		}
		// Retrieve elements of clusters by going through a loop over the chromose and the values in parallel 
		for (int geneIdx=0;geneIdx<a_Chromosome.size();geneIdx++)
		{
			clusterLabel = Integer.parseInt(a_Chromosome.getGene(geneIdx).getAllele().toString());
			temp[clusterLabel-1][counter[clusterLabel-1]++] = m_Values[geneIdx];
		}
		double avgSTD = 0;
		// Set each Cluster data elements and Calculate its standard deviation
		for (int idx=0;idx<myClusters.length;idx++)
		{
			myClusters[idx].setData(temp[idx]);
			myClusters[idx].standardDeviation();
			avgSTD = avgSTD + myClusters[idx].getSTD(); 
		}
		return myClusters;	
	}

  private double formula(int a_index, Cluster[] a_Clusts) 
  {
	  if (a_index == 1) 
	  {
		  return (-1.0) * totalWithinClusterVariation(a_Clusts);
	  }
	  else if (a_index == 2)
	  {
		  return totalAverageLinkage(a_Clusts);		  
	  }
	  else
		  return (-1.0) * a_Clusts.length;
  }
	private int max(Gene[] array)
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
	private double totalWithinClusterVariation(Cluster[] myClusters)
	{
		double op1 = 0, op2 = 0, sf = 0;
		
		for (int i=0;i<myClusters.length;i++)
		{
			sf = (myClusters[i].calculateSumFeature());
			if (myClusters[i].getNumElements()==0)
				System.out.println("STOP HERE!!! ---> " + myClusters[i]);
			op1 = op1 + (double)(1.0/myClusters[i].getNumElements()) * sf * sf;
			if (op1==0)
				System.out.println("STOP HERE!!! ---> " + myClusters[i]);
			for (int j=0;j<myClusters[i].getNumElements();j++)
			{
				op2 = op2 + (myClusters[i].getData()[j] * myClusters[i].getData()[j]);
			}
		}
		return (op2 - op1);
	}
	public double totalAverageLinkage(Cluster[] myClusters)
	{
		double avgLink = 0;
		for (int i=0;i<myClusters.length-1;i++)
		{
			for (int j=i+1;j<myClusters.length;j++)
			{
				avgLink = avgLink + averageLinkage(myClusters[i],myClusters[j]);
			}			
		}
		
		return (double)(avgLink/(myClusters.length * (myClusters.length-1) / 2.0));
	}
	private double averageLinkage(Cluster xClust, Cluster yClust)
	{
		double avgLink = 0;
		for (int i=0;i<xClust.getNumElements();i++)
		{
			for (int j=0;j<yClust.getNumElements();j++)
			{
				avgLink = avgLink + Math.abs(xClust.getData()[i] - yClust.getData()[j]);
			}
		}
		avgLink = (double)(1.0/(xClust.getNumElements()*yClust.getNumElements())) * avgLink;
		return avgLink;
	}
}
