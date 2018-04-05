package fuzzy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FuzzySet {
	private double from;
	private double to;
	private double R;
	String label;
	private double[] m_Data;
	String name;
	String pkg;

	
	public FuzzySet(double from, double to, double R, String label, String name, String pkg){
		this.from = from;
		this.R = R;
		this.to = to;
		this.label = label;
		this.name = name;
		this.pkg = pkg;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public FuzzySet(){
	}
	public double getFrom() {
		return from;
	}
	public void setFrom(double from) {
		this.from = from;
	}
	public double getTo() {
		return to;
	}
	public void setTo(double to) {
		this.to = to;
	}
	public double getR() {
		return R;
	}
	public void setR(double r) {
		R = r;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	
	public double[] getM_Data() {
		return m_Data;
	}

	public void setM_Data(double[] data) {
		m_Data = data.clone();
	}

	public double calculateDegreeOfMembership(double val){
		//To Do
		File outFile = new File("DegOfMembership.txt");
		PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter(outFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		int sel = 0;
		double DegOfMembership = 0;
		if (this.name == "triangle")
			sel = 0;
		else if (this.name == "trapezoid")
			sel = 1;
		
		switch (sel) 
		{
		case 0 : 
			if (val > this.from && val<=this.R)
				DegOfMembership = (val - this.from)/(this.R - this.from);
			else if (val > this.R && val<this.to)
				DegOfMembership = (this.to - val)/(this.to - this.R);
			else if (val <= this.from || val >= this.to)
				DegOfMembership = 0;
			break; 
		case 1 : 
			if (val < this.from) // the fisrt fuzzy set
			{
				out.println("the fisrt fuzzy set");
				if (val <= this.R)
					DegOfMembership = 1;
				else if (val > this.R && val < this.to)
					DegOfMembership = (this.to - val)/(this.to - this.R);
				else if (val >= this.to)
					DegOfMembership = 0;
			}
			else if (val > this.to) // the last fuzzy set
			{
				out.println("the last fuzzy set");
				if (val >= this.R)
					DegOfMembership = 1;
				else if (val > this.from && val < this.R)
					DegOfMembership = (val - this.from)/(this.R - this.from);
				else if (val <= this.from)
					DegOfMembership = 0;
			}
			break;
		default : 
			out.println("Error in calculating the degree of membership!");
		}
		out.close();		
		return DegOfMembership;
	}
	
	public String toString()
	{
		return "from: "+ from + "\n r: " + R+  "\n to:" + to;
	}
}
