package data;

public class LinkMetrics {
String node1;
String node2;
double metricvalue;

public String getNode1(){
	return node1;
}
public String getNode2(){
	return node2;
}
public void setNode1(String node1){
	this.node1=node1;
}
public void setNode2(String node2){
	this.node2=node2;
}
public void setMetrics(double met){
	this.metricvalue=met;
}
public double getMetrics(){
	return(metricvalue);
}
}
