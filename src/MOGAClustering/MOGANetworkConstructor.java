package MOGAClustering;

import java.util.List;

import org.jgap.Gene;
import org.jgap.IChromosome;

public class MOGANetworkConstructor {
	double[][] data;
	
	public MOGANetworkConstructor(double[][] data) {
		this.data = data;
	}
	
	public double[][] createNetwork()
	{
		MOGAClustering mogac = new MOGAClustering(data);
		
		double[][] network = null;
		
		try{
			@SuppressWarnings("rawtypes")
			List x = mogac.getClusterParetoOptimalSolutions();
			
			network = new double[data.length][data.length];
			Gene[] genes;
			double denum = 1.0 / x.size();
			for(Object solution:x)
			{
				genes = ((IChromosome)solution).getGenes();
				for(int i = 0; i < genes.length; i++)
				{
					for(int j = i+1; j < genes.length; j++)
					{
						if((Double)genes[i].getAllele() - (Double)genes[j].getAllele() == 0)
						{
							network[i][j] = network[i][j] + denum;
							network[j][i] = network[j][i] + denum;
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("ekh  " + data.length);
		return network;
	}
}
