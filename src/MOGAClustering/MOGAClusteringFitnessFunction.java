/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package MOGAClustering;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.Population;

/**
 * Sample fitness function for the multiobjective problem.
 * 
 * @author Klaus Meffert
 * @since 2.6
 */
public class MOGAClusteringFitnessFunction extends BulkFitnessFunction {
	/** String containing the CVS revision. Read out via reflection! */
	private final static String CVS_REVISION = "$Revision: 1.3 $";

	public static final int MAX_BOUND = 4000;

	public static final double MIN_X = -10;

	public static final double MAX_X = 10;

	private double[][] m_Values;

	// private Cluster[] clusters;

	public MOGAClusteringFitnessFunction(double[][] AttValues) {
		if (AttValues == null)
			throw new IllegalArgumentException(
					"Not Valid Attribute Values Array!");
		m_Values = AttValues;
	}

	/**
	 * Determine the fitness of the given Chromosome instance. The higher the
	 * returned value, the fitter the instance. This method should always return
	 * the same fitness value for two equivalent Chromosome instances.
	 * 
	 * @param a_subject
	 *            the population of chromosomes to evaluate
	 * 
	 * @author Klaus Meffert
	 * @since 2.6
	 */
	public void evaluate(Population a_subject) {
		Iterator it = a_subject.getChromosomes().iterator();
		while (it.hasNext()) {
			IChromosome a_chrom1 = (IChromosome) it.next();
			Cluster[] a_clusts1 = retrieveClusterInfoFromChromosome(a_chrom1);
			// evaluate values to fill vector of multiobjectives with
			double y1 = formula(1, a_clusts1);
			List l = new Vector();
			l.add(new Double(y1));
			double y2 = formula(2, a_clusts1);
			l.add(new Double(y2));
			double y3 = formula(3, a_clusts1);
			l.add(new Double(y3));
			((Chromosome) a_chrom1).setMultiObjectives(l);
		}
	}

	public static Vector getVector(IChromosome a_chrom) {
		Vector result = new Vector();
		List mo = ((Chromosome) a_chrom).getMultiObjectives();
		Double d = (Double) mo.get(0);
		result.add(d);
		d = (Double) mo.get(1);
		result.add(d);
		d = (Double) mo.get(2);
		result.add(d);
		return result;
	}

	private double formula(int a_index, Cluster[] a_Clusts) {
		if (a_index == 1) {
			return (-1.0) * totalWithinClusterVariation(a_Clusts);
		} else if (a_index == 2) {
			return totalAverageLinkage(a_Clusts);
		} else
			return (-1.0) * a_Clusts.length;
	}

	private Cluster[] retrieveClusterInfoFromChromosome(IChromosome a_Chromosome) {
		Cluster[] myClusters = null;
		int numClusters = 0;
		Vector distinctClusts = new Vector();
		double anAllele;
		double newAllele;

		// This loop finds the distinct cluster labels and stores them in
		// distinctClusts <Vector>
		// System.out.print("Org. CH: ");
		for (int geneIndex = 0; geneIndex < a_Chromosome.size(); geneIndex++) {
			anAllele = Double.valueOf(a_Chromosome.getGenes()[geneIndex]
					.getAllele().toString());
			// System.out.print(anAllele + " ");
			if (!distinctClusts.contains(anAllele))
				distinctClusts.add(anAllele);
		}

		// re-number the chromosome accoring to the distinct cluster labels
		// System.out.print("Ren. CH: ");
		for (int geneIndex = 0; geneIndex < a_Chromosome.size(); geneIndex++) {
			anAllele = Double.valueOf(a_Chromosome.getGenes()[geneIndex]
					.getAllele().toString());
			newAllele = distinctClusts.indexOf(anAllele) + 1;
			a_Chromosome.getGenes()[geneIndex].setAllele(newAllele);
		}
		numClusters = (int) max(a_Chromosome.getGenes());

		// A temporary array to store cluster elements
		double[][][] temp = new double[numClusters][][];

		// Create an array of Cluster type
		myClusters = new Cluster[numClusters];

		// Instantiate Cluster objects
		for (int i = 0; i < myClusters.length; i++)
			myClusters[i] = new Cluster();

		int clusterLabel;
		for (int geneIdx = 0; geneIdx < a_Chromosome.size(); geneIdx++) {
			clusterLabel = (int) Double.parseDouble(a_Chromosome
					.getGene(geneIdx).getAllele().toString());
			myClusters[clusterLabel - 1]
					.setNumElements(myClusters[clusterLabel - 1]
							.getNumElements() + 1);
			myClusters[clusterLabel - 1].setNumAttr(m_Values[0].length);
			if (clusterLabel == 0)
				System.out.println(" Cluster label is zero!");

			myClusters[clusterLabel - 1].setLabel(clusterLabel);
		}
		int[] counter = new int[numClusters];

		for (int idx = 0; idx < myClusters.length; idx++) {
			temp[idx] = new double[myClusters[idx].getNumElements()][myClusters[idx]
					.getNumAttr()];
			if (temp[idx].length == 0)
				counter[idx] = 0;
		}
		// Retrieve elements of clusters by going through a loop over the
		// chromose and the values in parallel
		for (int geneIdx = 0; geneIdx < a_Chromosome.size(); geneIdx++) {
			clusterLabel = (int) Double.parseDouble(a_Chromosome
					.getGene(geneIdx).getAllele().toString());
			temp[clusterLabel - 1][counter[clusterLabel - 1]++] = m_Values[geneIdx];
		}
		double avgSTD = 0;
		// Set each Cluster data elements and Calculate its standard deviation
		for (int idx = 0; idx < myClusters.length; idx++) {
			myClusters[idx].setData(temp[idx]);
			myClusters[idx].standardDeviation();
			avgSTD = avgSTD + myClusters[idx].getSTD();
		}
		return myClusters;
	}

	private double max(Gene[] array) {
		double max = 0;

		for (int i = 0; i < array.length; i++) {
			while (Double.parseDouble(array[i].getAllele().toString()) > max) {
				max = Double.parseDouble(array[i].getAllele().toString());
			}
		}
		return max;
	}

	private double totalWithinClusterVariation(Cluster[] myClusters) {
		double op1 = 0, op2 = 0, sf[];
		double sumXnd = 0, sumKd = 0;
		for (int i = 0; i < myClusters.length; i++) {
			sf = (myClusters[i].calculateSumFeature());
			// if (myClusters[i].getNumElements()==0)
			// System.out.println("STOP HERE!!! ---> " + myClusters[i]);
			if (myClusters[i].getNumElements() != 0) {
				sumKd = 0;
				for (int j = 0; j < myClusters[i].getNumAttr(); j++)
					sumKd += sf[j] * sf[j];
				op1 = op1 + (double) (1.0 / myClusters[i].getNumElements())
						* sumKd;
			}
			if (op1 == 0)
				throw new RuntimeException();
			// System.out.println("STOP HERE!!! ---> " + myClusters[i]);

			for (int j = 0; j < myClusters[i].getNumElements(); j++) {
				sumXnd = 0;
				for (int k = 0; k < myClusters[i].getNumAttr(); k++)
					sumXnd += myClusters[i].getData()[j][k]
							* myClusters[i].getData()[j][k];
				op2 = op2 + sumXnd;
			}
		}
		return (op2 - op1);
	}

	public double totalAverageLinkage(Cluster[] myClusters) {
		double avgLink = 0;
		for (int i = 0; i < myClusters.length - 1; i++) {
			for (int j = i + 1; j < myClusters.length; j++) {
				avgLink = avgLink
						+ averageLinkage(myClusters[i], myClusters[j]);
			}
		}

		return (double) (avgLink / (myClusters.length * (myClusters.length - 1) / 2.0));
	}

	private double averageLinkage(Cluster xClust, Cluster yClust) {
		double avgLink = 0;
		for (int i = 0; i < xClust.getNumElements(); i++) {
			for (int j = 0; j < yClust.getNumElements(); j++) {
				avgLink = avgLink
						+ findDistance(xClust.getData()[i], yClust.getData()[j]);
			}
		}
		avgLink = (double) (1.0 / (xClust.getNumElements() * yClust
				.getNumElements())) * avgLink;
		return avgLink;
	}

	private double findDistance(double[] p1, double[] p2) {
		double dist = 0;
		for (int i = 0; i < p1.length; i++)
			dist += (p1[i] - p2[i]) * (p1[i] - p2[i]);
		return Math.sqrt(dist);
	}

	/**
	 * @return deep clone of the current instance
	 * 
	 * @author Klaus Meffert
	 * @since 3.2
	 */
	public Object clone() {
		return new MOGAClusteringFitnessFunction(m_Values);
	}
}