package data;

import java.awt.Color;

public class GraphNode {
	String label;
	int id;
	public int type;
//	float color;
	Color color;
	
	public GraphNode(int id, String label){
		this.id = id;
//		if (label == null)
//			this.label = "";
//		else
			this.label = label;
	}
	
	public GraphNode(GraphNode original)
	{
		this.label = original.label;
		this.id = original.id;
		this.type = original.type;
		this.color = original.color;
	}
	public String getLabel(){
		if(label == null)
			return "node "+id;
		else 
			return label;
	}
	@Override
	public String toString() {
		return getLabel();
	}
	
	public int getType() {
		return type;
	}

	public Color getColor(){
		return color;
	}
	public int getID()
	{
		return id-1;
	}
	public int getActualID()
	{
		return id;
	}
	public void setColor(Color c)
	{
		this.color = c;
	}
	public void setType(int type)
	{
		this.type = type;
	}
}
