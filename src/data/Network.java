package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import cern.colt.list.DoubleArrayList;

public class Network {
	public boolean isDirected() {
		return isDirected;
	}
	double[][] matrix;
	String[][] labels;
	int mode;
	int row, column;
	boolean isDirected;
	
	public Network (int mode)
	{
		this.mode = mode;
		if(mode == 2)
			isDirected = false;
	}
	public void toggleNetworkDirection()
	{
		if(isDirected == true)
			isDirected = false;
		else
			isDirected = true;
	}
	// For network construction from raw data
	public Network(double[][] data, int row, String[][] labels, int type)
	{
		matrix = data;
		this.row = row;
		this.column = row;
		this.isDirected = false;
		this.mode = 1;
		
		
		if (labels != null)
		{
			this.labels = new String[2][row];

			if(type == 1)
				for(int i = 0; i < column; i++)
				{
					this.labels[0][i] = labels[0][i];
					this.labels[1][i] = labels[0][i];
				}
			else
				for (int i = 0; i < row; i++) {
					this.labels[0][i] = labels[1][i];
					this.labels[1][i] = labels[1][i];
			}
		}
	}
	
	public void setMatrix(double[][] m, int r)
	{
		this.matrix = m;
		row = r; 
		column = r;
		
	}
	public int loadNetwork(boolean directed, String filePath, boolean containsHeader)
	{
		if(mode == 1)
			isDirected = directed;
		if(filePath.endsWith(".csv"))
			return loadExcelFile(filePath, containsHeader);
		else
			return loadTextFile(filePath, containsHeader);
	}
	
	private int loadTextFile(String filePath, boolean containsHeader)
	{
		BufferedReader reader = null;
		try {
			new FileReader(new File(filePath));
			reader = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (containsHeader) {
			labels = new String[2][];
			labels[0] = getHeaders(reader, " ");	
			if(labels == null)
				return 2; 
		}
		
		List<DoubleArrayList> rows = readLines(reader, containsHeader, " ");
		if(rows == null)
			return 2;
		
		row = rows.size();
		matrix = new double[row][column];
		for (int i = 0; i < matrix.length; i++)
		{
			for(int j = 0; j < rows.get(i).size(); j++)
			{
				matrix[i][j] = rows.get(i).get(j);
			}
		}
		return checkValidity();
	}
	
	// Returns 2 if the file is not in acceptable format
	private int loadExcelFile(String filePath, boolean containsHeader) {
		BufferedReader reader = null;
		try {
			new FileReader(new File(filePath));
			reader = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (containsHeader) {
			labels = new String[2][];
			labels[0] = getHeaders(reader, ",");
			if (labels == null)
				return 2;
		}

		List<DoubleArrayList> rows = readLines(reader, containsHeader, ",");
		if (rows == null)
			return 2;
		// if(containsHeader)
		// column -= 1;

		row = rows.size();
		matrix = new double[row][column];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				matrix[i][j] = rows.get(i).get(j);
			}
		}
		return checkValidity();
	}
	
	//If the file contains headers, the function would read and tokenize them
	private String[] getHeaders(BufferedReader reader, String delimiter)
	{
		String currentLine = null;
		
		ArrayList<String> headers = null;
		
		headers = new ArrayList<String>();
		try {
			currentLine = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (currentLine != null) {
			StringTokenizer tokenizer = new StringTokenizer(
					currentLine, delimiter);
			if (tokenizer.countTokens() != 0) {
				column = tokenizer.countTokens();
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					headers.add(token.trim());
				}
				String[] result = new String[headers.size()];
				for(int i = 0; i < result.length; i++)
					result[i] = headers.get(i);
				return result;
			}
		}
		return null;
	}
	
	//Reads data lines in excel files
	private List<DoubleArrayList> readLines(BufferedReader reader, boolean headers, String delimiter)
	{
		String currentLine;
		List<DoubleArrayList> rows = new ArrayList<DoubleArrayList>();
		ArrayList<String> hdrs = new ArrayList<String>();
		try {
			while ((currentLine = reader.readLine()) != null) {
				StringTokenizer tokenizer = new StringTokenizer(currentLine,delimiter);
				if (tokenizer.countTokens() == 0) {
					break;
				}
				DoubleArrayList currentRow = new DoubleArrayList();
				if(headers)
				{
					hdrs.add(tokenizer.nextToken());
				}
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (token.equals("") || token.equals(" "))
						continue;
					try{
					currentRow.add(Double.parseDouble(token));
					} catch (NumberFormatException nfe){
						return null;
					}
				}
				if(column != 0 && column != currentRow.size())
					return null;
				column = currentRow.size();
				rows.add(currentRow);
			}
			if(hdrs != null && labels != null){
			labels[1] = new String[hdrs.size()];
			for(int i = 0; i < labels[1].length; i++)
				labels[1][i] = hdrs.get(i);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rows;
	}
	
	private int checkValidity()
	{
		if (mode == 1 && row != column)
			return 3;
		if(mode == 1 && !isDirected)
		{
			for(int i = 0; i < row; i++)
				for(int j = 0; j < i; j++)
					if(matrix[i][j] != matrix[j][i])
						return 4;
		}
		return 0;
	}
	
	public double getQuick(int i, int j)
	{
		return matrix[i][j];
	}
	public double[][] getMatrix() {
		return matrix;
	}
	public String[][] getLabels() {
		return labels;
	}
	public int getMode() {
		return mode;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	
}