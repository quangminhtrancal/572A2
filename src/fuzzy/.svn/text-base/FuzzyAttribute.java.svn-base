package fuzzy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import fuzzy.application.Fuzzy_App;


public class FuzzyAttribute {
	static int report = 0;
	Connection connection;
	String schema;
	String table;
	String name;
	ArrayList<FuzzySet> fuzzySets;
	ArrayList<FuzzyCondition> fuzzyConditions;
	double[] data;
	double minValue;
	double maxValue;
	int cardinality = 10;
	
	public FuzzyAttribute(Connection a_Connection, String a_Schema, String a_Table, String a_Name){
		this.connection = a_Connection;
		this.schema = a_Schema;
		this.table = a_Table;
		this.name = a_Name;
	}
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}
	private double findMinValue() {
		minValue = data[0];
		for (int i=0; i<data.length; i++)
			if (data[i]<minValue)
				minValue = data[i];
		return minValue;
	}
	public FuzzySet getFuzzySet(int i){
		return fuzzySets.get(i);
	}
	public double getMinValue() {
		return minValue;
	}
	private double findMaxValue() {
		maxValue = data[0];
		for (int i=0; i<data.length; i++)
			if (data[i]>maxValue)
				maxValue = data[i];
		return maxValue;
	}	
	public double getMaxValue() {
		return maxValue;
	}	
	public ArrayList<FuzzySet> getFuzzySets() {
		return fuzzySets;
	}

	public void setFuzzySets(ArrayList<FuzzySet> fuzzySets) {
		this.fuzzySets = fuzzySets;
	}
	public void setFuzzySets(FuzzySet[] set_fuzzySets) {
		fuzzySets = new ArrayList<FuzzySet>();
		double From = 0, R =0, To = 0;
		for(int i=0; i<set_fuzzySets.length; i++)
		{
			From = set_fuzzySets[i].getFrom();
			R = set_fuzzySets[i].getR();
			To = set_fuzzySets[i].getTo();
			fuzzySets.add(new FuzzySet(From,To,R,set_fuzzySets[i].getLabel(),set_fuzzySets[i].getName(),set_fuzzySets[i].getPkg()));			
		}
	}
//Commented By Negar: Because we don't have access XFuzzy Packages	
//	public void setFuzzySets(Type tp) {
//		fuzzySets = new ArrayList<FuzzySet>();
//		double From = 0, R =0, To = 0;
//		From = tp.getMembershipFunctions()[0].get()[1];
//		R = tp.getMembershipFunctions()[0].get()[2];
//		To = tp.getMembershipFunctions()[0].get()[3];
//		fuzzySets.add(new FuzzySet(From,To,R,tp.getMembershipFunctions()[0].label,tp.getMembershipFunctions()[0].name+"1",tp.getMembershipFunctions()[0].pkg));
//		int i;
//		for(i=1; i<tp.getMembershipFunctions().length-1; i++)
//		{
//			From = tp.getMembershipFunctions()[i].get()[0];
//			R = tp.getMembershipFunctions()[i].get()[1];
//			To = tp.getMembershipFunctions()[i].get()[2];
//			fuzzySets.add(new FuzzySet(From,To,R,tp.getMembershipFunctions()[i].label,tp.getMembershipFunctions()[i].name,tp.getMembershipFunctions()[i].pkg));			
//		}
//		From = tp.getMembershipFunctions()[i].get()[0];
//		R = tp.getMembershipFunctions()[i].get()[1];
//		To = tp.getMembershipFunctions()[i].get()[2];
//		fuzzySets.add(new FuzzySet(From,To,R,tp.getMembershipFunctions()[i].label,tp.getMembershipFunctions()[i].name+"2",tp.getMembershipFunctions()[i].pkg));			
//	}
//	public Type mapFuzzySetsToType() {
//		Type tp = new Type(this.name);
//		try{
//			int sel = 0;
//			int mfs = this.fuzzySets.size();
//			ParamMemFunc[] pmf = new ParamMemFunc[mfs];
//			//	case 0: /* TRIANGULOS EQUIESPACIADOS */
////			double[] param = new double[3];
////			ParamMemFunc nmf;
////			for (int i=0; i<mfs; i++)
////			{
////				nmf = new xfl_mf_triangle();
////				nmf.name = "triangle";
////				param[0] = this.fuzzySets.get(i).getFrom();
////				param[1] = this.fuzzySets.get(i).getR();
////				param[2] = this.fuzzySets.get(i).getTo();
////				nmf.label = this.fuzzySets.get(i).getLabel();
////				nmf.set(param);
////				pmf[i] = nmf;
////			}
////			tp.setMembershipFunctions(pmf);
////			Universe u = new Universe(this.minValue,this.maxValue,this.cardinality);
////			tp.setUniverse(u);
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.getMessage();
//		}
//		
//		try{
//			File outFile = new File("C:\\QueryBuilderTemp\\Trap-Type.txt");
//			PrintWriter out = new PrintWriter(new FileWriter(outFile));
//			int sel = 0;
//			int mfs = this.fuzzySets.size();
//			double r;
//			String prefix = "mf";
//			ParamMemFunc[] pmf = new ParamMemFunc[mfs];
//			Universe u = new Universe(this.minValue,this.maxValue,this.cardinality);
////		   case 1: /* TRIANGULOS CON "HOMBRERAS" */
//			    double[] param = new double[4];
//			    r = (this.maxValue-this.minValue)/(mfs+1);
//			    out.println("This is r: " + r);
//			    ParamMemFunc mf0 = new xfl_mf_trapezoid();
//			    param[0] = this.minValue-r; param[1] = this.minValue; param[2] = this.fuzzySets.get(0).getR(); param[3] = this.fuzzySets.get(0).getTo();
//			    mf0.label = prefix+"0"; 
//			    mf0.u = u;
//			    try { mf0.set(param); pmf[0] = mf0; } catch(XflException ex) {}
//			    param = new double[3];
//			    for(int i=1; i<mfs-1; i++) {
//			     param[0] = this.fuzzySets.get(i).getFrom();
//			     param[1] = this.fuzzySets.get(i).getR();
//			     param[2] = this.fuzzySets.get(i).getTo();
//			     ParamMemFunc nmf = new xfl_mf_triangle();
//			     nmf.label = prefix+i; 
//			     nmf.u = u;
//			     try { nmf.set(param); pmf[i] = nmf; } catch(XflException ex) {}
//			    }
//			    param = new double[4];
//			    ParamMemFunc mfl = new xfl_mf_trapezoid();
//			    param[0] = this.fuzzySets.get(mfs-1).getFrom(); param[1] = this.fuzzySets.get(mfs-1).getR(); param[2] = this.fuzzySets.get(mfs-1).getTo(); param[3] = this.fuzzySets.get(mfs-1).getTo()+r;
//			    mfl.label = prefix+(mfs-1); 
//			    mfl.u = u;
//			    try { mfl.set(param); pmf[mfs-1] = mfl; } catch(XflException ex) {}
//				tp.setMembershipFunctions(pmf);
//				tp.setUniverse(u);
//				out.println(tp.toXfl());
//				out.println(tp.getUniverse().min() + " " + tp.getUniverse().max() + " " + tp.getUniverse().card());
//				out.close();
//		}
//		catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.getMessage();
//		}
//		
//		
//		
//		
//		return tp;
//	}

	public ArrayList<FuzzyCondition> getFuzzyConditions() {
		return fuzzyConditions;
	}
	public void setFuzzyConditions(ArrayList<FuzzyCondition> set_fuzzyConditions) {
		fuzzyConditions = new ArrayList<FuzzyCondition>();
		for (int i=0; i<set_fuzzyConditions.size(); i++)
		{
			fuzzyConditions.add(new FuzzyCondition(set_fuzzyConditions.get(i).getFuzzyTerm(),set_fuzzyConditions.get(i).getOperator()));
		}
	}
	public void RetrieveData() throws SQLException, IOException{
		
		Statement stmt = connection.createStatement();
		Statement stmt2 = connection.createStatement();
		
		ResultSet rsCount;	
		rsCount = stmt2.executeQuery("SELECT COUNT(" + name + ") FROM " + schema + "." + table);
		rsCount.next();
		int a =rsCount.getInt(1);
		data = new double[rsCount.getInt(1)];		
		rsCount.close();
		
		double tmp;
		ResultSet rs;
		rs = stmt.executeQuery("SELECT " + name + " FROM " + schema + "." + table);
		
		File outFile = new File("C:\\QueryBuilderTemp\\att.txt");
		PrintWriter out = new PrintWriter(new FileWriter(outFile));
		out.println(a);
	
		int counter = 0;
		while (rs.next())
		{
			tmp = rs.getDouble(name);
			data[counter] = tmp;
			out.println(tmp);
			counter++;	
		}
		rs.close();
		out.close();
		this.minValue = findMinValue();
		this.maxValue = findMaxValue();
	}
	public String getSchemaName(){
		return schema;
	}
	public String getTableName(){
		return table;
	}
	
	public int getCardinality() {
		return cardinality;
	}
	public String getName(){
		return name;
	}
	
	public void AddToFuzzySets(double from, double to, double R,String label, String name, String pkg){
		fuzzySets.add(new FuzzySet(from,to,R,label,name,pkg));
	}
	
	public ArrayList<FuzzySet> GetFuzzySet(){
		return fuzzySets;
	}
	
	public FuzzySet[] CreateOptimizedFuzzysets(){
		FuzzySet[] result = null;
		try{
			Fuzzy_App a_Fuzzy_App = new Fuzzy_App();
//			result = a_Fuzzy_App.generateOpptimizedFuzzySets(data, "C:\\QueryBuilderTemp\\cluster_FQ.txt","C:\\QueryBuilderTemp\\FS_FQ.txt");
			result = a_Fuzzy_App.generateOpptimizedFuzzySets(a_Fuzzy_App.ArrayRemoveDuplicate(data), "C:\\QueryBuilderTemp\\cluster_FQ.txt","C:\\QueryBuilderTemp\\FS_FQ"+(report++)+".txt");
			System.out.println("hoooooo");
		}
		catch(Exception e){
			File outFile = new File("C:\\QueryBuilderTemp\\Ex.txt");
			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter(outFile));
				out.print(e.toString());
			} catch (IOException e1) {}
			out.close();
		}
		return result;
	}
	
	public FuzzySet[] CreateOptimizedFuzzysets(int n){
		FuzzySet[] result = null;
		try{
			Fuzzy_App a_Fuzzy_App = new Fuzzy_App();
//			result = a_Fuzzy_App.generateOpptimizedFuzzySets(data, n,"C:\\QueryBuilderTemp\\FS_FQ.txt");
			result = a_Fuzzy_App.generateOpptimizedFuzzySets(a_Fuzzy_App.ArrayRemoveDuplicate(data), n,"C:\\QueryBuilderTemp\\FS_FQ"+(report++)+".txt");
			System.out.println("hoooooo2222");
		}
		catch(Exception e){
			File outFile = new File("C:\\QueryBuilderTemp\\Ex.txt");
			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter(outFile));
				out.print(e.toString());
			} catch (IOException e1) {}
			out.close();
		}
		return result;
	}
	
	public double getDegreeOfMembership(String a_label, double a_value)
	{
		for (int index=0; index<this.fuzzySets.size(); index++)
		{
			if (a_label.equals(this.fuzzySets.get(index).getLabel()))
				return this.fuzzySets.get(index).calculateDegreeOfMembership(a_value);
		}
		return 0;
	}
	public double getDefuzzifiedValue(double a_Value)
	{
		int maxIndex = 0;
		for (int index=1; index<this.fuzzySets.size(); index++)
			if (this.fuzzySets.get(index).calculateDegreeOfMembership(a_Value)>this.fuzzySets.get(maxIndex).calculateDegreeOfMembership(a_Value))
				maxIndex = index;
		return this.fuzzySets.get(maxIndex).calculateDegreeOfMembership(a_Value);
	}
	public String getDefuzzifiedLabel(double a_Value)
	{
		int maxIndex = 0;
		for (int index=1; index<this.fuzzySets.size(); index++)
			if (this.fuzzySets.get(index).calculateDegreeOfMembership(a_Value)>this.fuzzySets.get(maxIndex).calculateDegreeOfMembership(a_Value))
				maxIndex = index;
		return this.fuzzySets.get(maxIndex).getLabel();
	}
}