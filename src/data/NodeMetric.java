package data;

public class NodeMetric implements Comparable<NodeMetric>{

	String nodeName;
	double metricValue;
	
	
	public NodeMetric(String nodeName, double metricValue) {
		super();
		this.nodeName = nodeName;
		this.metricValue = metricValue;
	}


	@Override
	public int compareTo(NodeMetric arg0) {
		if (this.metricValue > arg0.metricValue)
			return -1;
		if (this.metricValue < arg0.metricValue)
			return 1;
		return 0;
	}


	public String getNodeName() {
		return nodeName;
	}


	public double getMetricValue() {
		return metricValue;
	}


	public void setMetricValue(double metricValue) {
		this.metricValue = metricValue;
	}

	
}
