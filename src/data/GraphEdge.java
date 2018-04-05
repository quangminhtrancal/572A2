package data;

import java.util.Comparator;

public class GraphEdge {
	double weight;
	String str;
	
	public GraphEdge(double wight){
		this.weight = wight;
	}
	
	public String getLabel(){
		return Double.toString(weight);
	}
	
	public String getLabel(int num){
		return Double.toString(setPrecision(num));
	}
	
	public String ToString(){
		return str;
	}

	public double getWeight() {
		return weight;
	}
	
	private double setPrecision(int num)
	{
		int temp1 = (int)(weight * (Math.pow(10, num)));
		return (double) temp1 / (Math.pow(10, num));
	}
	
	public static Comparator<GraphEdge> graphEdgeComparator = new Comparator<GraphEdge>() {     
		 public int compare(GraphEdge a, GraphEdge b) {
				return Double.valueOf(a.weight).compareTo(Double.valueOf(b.weight));
			}
	}; 
}
