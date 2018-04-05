package MOGAClustering;

public class Cluster //implements Comparable<Cluster>
{
	private int m_NumElements;
	private int m_NumAttr;
	private double m_STD;
	private int m_Label;
	private double[][] m_Data;

//	public int compareTo(Cluster c) {
//        return (int)(this.centroid() - c.centroid());
//    }	
	
	public Cluster()
	{
		setLabel(0);
		setNumElements(0);
	}
	public Cluster(int numElements, int numAttr, double std, int label)
	{
		m_NumElements = numElements;
		m_NumAttr = numAttr;
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
	
	public int getNumAttr() {
		return m_NumAttr;
	}

	public void setNumAttr(int m_NumAttr) {
		this.m_NumAttr = m_NumAttr;
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
	public void setData(double[][] data)
	{
		m_Data = data.clone();
	}
	public double[][] getData()
	{
		return m_Data;
	}
	public double standardDeviation()
	{
		double mean[] = new double[m_NumAttr];
		double sum[] = new double[m_NumAttr]; 
		double squareSum = 0;
		for (int j = 0; j < m_NumAttr; j++)
		{
			for (int i=0;i<m_NumElements;i++)
			{
				sum[j] = sum[j] + m_Data[i][j];
			}
			mean[j] = sum[j]/m_NumElements;
		}
		double distance = 0;
		for (int i=0;i<m_NumElements;i++)
		{
			distance = 0;
			for(int j = 0; j < m_NumAttr; j++)
				distance += (m_Data[i][j] - mean[j]) * (m_Data[i][j] - mean[j]);
			squareSum += Math.sqrt(distance);
		}
		return (Math.sqrt(squareSum/m_NumElements));
	}
	public double variance()
	{
		double mean[] = new double[m_NumAttr];
		double sum[] = new double[m_NumAttr]; 
		double squareSum = 0;
		for (int j = 0; j < m_NumAttr; j++)
		{
			for (int i=0;i<m_NumElements;i++)
			{
				sum[j] = sum[j] + m_Data[i][j];
			}
			mean[j] = sum[j]/m_NumElements;
		}
		double distance = 0;
		for (int i=0;i<m_NumElements;i++)
		{
			distance = 0;
			for(int j = 0; j < m_NumAttr; j++)
				distance += (m_Data[i][j] - mean[j]) * (m_Data[i][j] - mean[j]);
			squareSum += Math.sqrt(distance);
		}
		return(squareSum/m_NumElements);
	}
	public double[] centroid()
	{
		double[] sum = new double[m_NumAttr]; 
		for (int i=0;i<this.getNumElements();i++)
		{
			for(int j = 0; j < this.getNumAttr(); j++)
				sum[j] = sum[j] + this.getData()[i][j];
		}
		for(int i = 0; i < sum.length; i++)
			sum[i] = sum [i]/this.getNumElements();
		return sum;
	}
	
	// Calculate SF (sum of the d-th feature of all the patterns in cluster k); here d is equal to 1
	public double[] calculateSumFeature()
	{
		double sum[] = new double[this.getNumAttr()];
		//System.out.print("Cluster Label is: " + m_Label + " --- Elements ---> ");
		for (int i=0;i<m_NumElements;i++)
		{
			for(int j = 0; j < m_NumAttr; j++)
				sum[j] = sum[j] + m_Data[i][j];
			//System.out.print(m_Data[i] + " ");
		}
		//System.out.println( "   SUM is: " + sum); 
		return sum;
	}	
	public double getSumToCentroid()
	{
		double sumD = 0;
		for (int i=0;i<m_NumElements;i++)
			sumD = sumD + findDistance(m_Data[i], centroid());
		return sumD/m_NumElements; ////////////////////////// I added the division. http://machaon.karanagai.com/validation_algorithms.html says that it's the "average distance"...
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
			return avgDis;
		}
		else
		{
			for (int i=0; i<m_Data.length;i++)
			{
				if (i!=elementIdx)
					avgDis = avgDis + findDistance(m_Data[i], m_Data[elementIdx]);
			}
		}
		avgDis = avgDis / (m_NumElements-1); 
		return avgDis;
	}
	public void printCluster()
	{
		System.out.print("Cluster " + this.m_Label + ": ");
		for (int i=0; i<this.m_NumElements; i++)
		{
			System.out.print("[");
			for(int j = 0; j < this.m_Data[i].length-1; j++)
				System.out.print(this.m_Data[i][j] + ", ");
			System.out.print(this.m_Data[i][m_Data[i].length-1]);
			System.out.print("], ");
		}
		System.out.println();
	}
	
	private double findDistance(double[] p1, double[] p2)
	{
		double dist = 0;
		for(int i = 0; i < p1.length; i++)
			dist += (p1[i] - p2[i]) * (p1[i] - p2[i]);
		return Math.sqrt(dist);
	}
}