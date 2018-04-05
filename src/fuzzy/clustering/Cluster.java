package fuzzy.clustering;

import java.util.Comparator;


public class Cluster implements Comparable<Cluster>
{
	private int m_NumElements;
	private double m_STD;
	private int m_Label;
	public double[] m_Data;
	public int[] m_ElementIdx;
	private double m_Center;

	
	public int[] getM_ElementIdx() {
		return m_ElementIdx;
	}

	public void setM_ElementIdx(int[] elementIdx) {
		m_ElementIdx = elementIdx;
	}

	public int compareTo(Cluster c) {
        return (int)(this.centroid() - c.centroid());
    }	
	
	public Cluster()
	{
		setLabel(0);
		setNumElements(0);
	}
	public Cluster(int numElements, double std, int label)
	{
		m_NumElements = numElements;
		m_STD = std;
		m_Label = label;
	}
	public void setNumElements(int NumElements)
	{
		m_NumElements = NumElements;
	}
	public int getNumElements()
	{
		return m_NumElements;
	}	
	public double getCenter() {
		return m_Center;
	}

	public void setCenter(double center) {
		this.m_Center = center;
	}

	public double getSTD()
	{
		return m_STD;
	}
	public void setLabel(int label)
	{
		m_Label = label;
	}
	public int getLabel()
	{
		return m_Label;
	}
	public void setData(double[] data)
	{
		m_Data = data.clone();
	}
	public double[] getData()
	{
		return m_Data;
	}
	public double standardDeviation()
	{
		double mean = 0;
		double sum = 0; 
		double squareSum = 0;
		for (int i=0;i<m_NumElements;i++)
		{
			sum = sum + m_Data[i];
		}
		mean = sum/m_NumElements;
		sum = 0;
		for (int i=0;i<m_NumElements;i++)
		{
			squareSum = squareSum + (m_Data[i] - mean)*(m_Data[i] - mean);
		}
		return (Math.sqrt(squareSum/m_NumElements));
	}
	public double variance()
	{
		double mean = 0;
		double sum = 0; 
		double squareSum = 0;
		for (int i=0;i<m_NumElements;i++)
		{
			sum = sum + m_Data[i];
		}
		mean = sum/m_NumElements;
		sum = 0;
		for (int i=0;i<m_NumElements;i++)
		{
			squareSum = squareSum + (m_Data[i] - mean)*(m_Data[i] - mean);
		}
		return(squareSum/m_NumElements);
	}
	public double centroid()
	{
		double sum = 0; 
		for (int i=0;i<this.getNumElements();i++)
		{
			sum = sum + this.getData()[i];
		}
		return sum/this.getNumElements();
	}
	
	// Calculate SF (sum of the d-th feature of all the patterns in cluster k); here d is equal to 1
	public double calculateSumFeature()
	{
		double sum = 0;
		//System.out.print("Cluster Label is: " + m_Label + " --- Elements ---> ");
		for (int i=0;i<m_NumElements;i++)
		{
			sum = sum + m_Data[i];
			//System.out.print(m_Data[i] + " ");
		}
		//System.out.println( "   SUM is: " + sum); 
		return sum;
	}	
	public double getSumToCentroid()
	{
		double sumD =0;
		for (int i=0;i<m_NumElements;i++)
		{
			sumD = sumD + Math.abs(m_Data[i]-centroid());
		}
		return sumD;
	}
	public double avgDissimilarityOfElement(int elementIdx)
	{
		double avgDis = 0;
		if (elementIdx > m_NumElements)
			System.out.println("There is not such an element in the cluster!");
		else if (m_NumElements ==0 )
			System.out.println("This cluster is empty!");
		else if (m_NumElements ==1 )
		{
			//System.out.println("There is only one element in the cluster!");
			return 0;
		}
		else
		{
			for (int i=0; i<m_Data.length;i++)
			{
				if (i!=elementIdx)
					avgDis = avgDis + Math.abs(m_Data[i]-m_Data[elementIdx]);
			}
		}
		return avgDis/(m_NumElements-1);
	}
	public void printCluster()
	{
		System.out.print("Cluster " + this.m_Label + ": ");
		for (int i=0; i<this.m_NumElements; i++)
			System.out.print(this.m_Data[i] + "  ");			
		System.out.println();
	}
}