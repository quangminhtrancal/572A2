package data;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/***
 * 
 * 	@author Arkady (Eric) Eidelberg ( ninuson123@gmail.com )
 *	@author Samuel Wong ( sawong@ucalgary.ca )
 *
 *	This is a utility to produce data sets for the NetDriller program based on specific requirements.
 *	These requirements are outlined under the print_instructions method.
 *	This is the work of our assignment 2 for the CPSC 572/672 in the University of Calgary for the Winter 2017 Semester.
 *	Course instructor: Dr. Rokne
 *
 *	Additional comments:
 *	In order to produce the least amount of duplicated nodes we have converted all keywords and author names to upper case in the parsing of the papers.
 *	Take a look at the Paper.java constructor to change this behavior.
 *
 */
public class Parser 
{
	public static int COAUTHORED_PAPERS_MODE = 1;
	public static int TILTES_AND_KEYWORDS_MODE = 2;
	public static int AUTHORS_AND_KEYWORDS_MODE = 3;
	
	public static boolean DEBUG = false; // Set this to true to see print-out of all the global-maps in all modes.
	
	public void parse(String file_path,int mode)
	{	
		// Check that the input file provided is actually a good file and can be parsed as JSON.
		String json = parse_input_file(file_path);
		if (json.trim().isEmpty())
		{
			System.out.println("The input file was empty. Please follow provided instructions.");
			print_instructions_and_exit_program();
		}
		
		// Check if the output file name parameter was provided
		String output_name = "output.csv";
		
		// Test the JSON file to see the content is properly parsed JSON and contains the proper fields required from the input
		JSONParser parser = new JSONParser();
		List<Paper> papers = new ArrayList<Paper>(); 
		try 
		{
			// This is the outermost JSON array that contains a list of papers
			JSONArray array = (JSONArray) parser.parse(json);
			
			for (Object obj : array)
			{
				JSONObject json_obj = (JSONObject)obj;
				try
				{
					Paper temp = new Paper(json_obj);
					papers.add(temp);
				}
				catch (Exception e)
				{
					System.out.println("Was unable to parse parts of the input JSON. Please follow instructions.");
					System.out.println("Error at: " + json_obj.toJSONString());
					print_instructions_and_exit_program();
				}
			}
		} 
		catch (ParseException e) 
		{
			System.out.println("The input file contained inproper JSON. Please follow provided instructions.");
			print_instructions_and_exit_program();
		}
		
		System.out.println("The file " + file_path + " was successfuly parsed!");
		
		// Try to open the output file
		File output_file = new File(output_name);
		
		if (mode == COAUTHORED_PAPERS_MODE)
		{
			// Do the co-authored papers mode
			System.out.println("Creating a dataset based on co-authored papers: " + output_name);
			do_co_authors(output_file, papers);
		}
		else if (mode == TILTES_AND_KEYWORDS_MODE)
		{
			// Do the titles and keywords mode
			System.out.println("Creating a dataset based on titles and keywords: " + output_name);
			do_titles_and_keywords(output_file, papers);
		}
		else if (mode == AUTHORS_AND_KEYWORDS_MODE)
		{
			// Do the authors and keywords mode
			System.out.println("Creating a dataset based on authors and titles: " + output_name);
			do_authors_and_keywords(output_file, papers);
		}
		else
		{
			System.out.println("Invalide operation mode, please indicate with a single digit as the first parameter which mode to run the Parser utility. Please follow provided instructions.");
			print_instructions_and_exit_program();
		}
	}
	
	/***
	 * 
	 * This method creates a dataset for a weighted graph.
	 * 
	 * Nodes are authors and edges between nodes indicate co-authored papers. 
	 * The weight of the edge is the number of such co-authored papers.
	 * 
	 * @param output_file - This is the file to which the resulting dataset will be written in a csv format.
	 * @param papers - An ArrayList of all the Paper(s) (container class for input) that were provided as input.
	 * 
	 */
	private static void do_co_authors(File output_file, List<Paper> papers)
	{
		try
		{
			// A hashtable where the key is an author and the value is a counter hashtable 
			// In the counter hashtable the co-authors names are the keys and their occurrences are the values.  
			Hashtable<String, Hashtable<String, Integer>> golbal_map = new Hashtable<String, Hashtable<String, Integer>>(); 
			
			// This is used to generate the output file
			HashSet<String> all_authors = new HashSet<String>();
			
			for(Paper paper : papers)
			{
				for(String author : paper.authors)
				{
					all_authors.add(author);
					
					// For every author in the authors of any given paper
					ArrayList<String> co_authors = new ArrayList<String>();
					co_authors.addAll(paper.authors);
					co_authors.remove(author);
					
					// This hashtable counts how many times each co_author appeared for each author
					Hashtable<String, Integer> all_co_authors_counter = new Hashtable<String, Integer>();
					
					// Add the occurrence of this co-authorship into the global map
					for(String co_author : co_authors)
					{
						// Check if this isn't the first time we see this author - get the old counter if so
						if (golbal_map.containsKey(author))
						{
							all_co_authors_counter = golbal_map.get(author);
						}
						
						// Check how many this co-authoer appeared for this author - if it's null, this is the first time
						Integer num_of_co_authored_papers = all_co_authors_counter.get(co_author);
						int new_num = 1;
						if (num_of_co_authored_papers != null)
						{
							new_num = num_of_co_authored_papers + 1;
						}
						
						all_co_authors_counter.put(co_author, new Integer(new_num));	
					}
					
					// Update the global counter for this give author
					golbal_map.put(author, all_co_authors_counter);
				}
			}
			
			// Display number of co-authors for each author if DEBUG is set on
			if (DEBUG)
			{
				for(String author : golbal_map.keySet())
				{
					System.out.println("For the author " + author);
					
					Hashtable<String, Integer> co_author_counts = golbal_map.get(author);
					for(String co_author : co_author_counts.keySet())
					{
						System.out.println("\t" + co_author + " has co-authored " + co_author_counts.get(co_author) + " papers.");
					}
				}
			}
			// Write the file out
			write_out(golbal_map, all_authors, all_authors, output_file);
			
			// The given output file should loads properly in NetDriller.
			// To do so, under import graph, choose "One Mode", "Undirected", "CSV file" and mark the "The file contains headers" checkbox. 
		}
		catch (Exception e)
		{
			System.out.println("Was not able to write to output file. Please check the file can be created!");
			e.getMessage();
			e.printStackTrace();
		}
	}
	
	/***
	 * 	 
	 * This method creates a dataset for a weighted graph.
	 * 
	 * Nodes are keywords of papers and edges between nodes are co-occurrences of these keywords in papers.
	 * The weight of the edge indicates the number of times these two keywords co-occurred.
	 * 
	 * @param output_file - This is the file to which the resulting dataset will be written in a csv format.
	 * @param papers - An ArrayList of all the Paper(s) (container class for input) that were provided as input.
	 * 
	 */
	private static void do_titles_and_keywords(File output_file, List<Paper> papers)
	{
		try
		{
			// A hashtable where the key is a keyword and the value is a counter hashtable 
			// In the counter hashtable the co-keywords are the keys and their occurrences are the values. 
			Hashtable<String, Hashtable<String, Integer>> golbal_map = new Hashtable<String, Hashtable<String, Integer>>(); 
			
			// This is used to generate the output file
			HashSet<String> all_keywords = new HashSet<String>();
			
			for(Paper paper : papers)
			{
				for(String keyword : paper.key_words)
				{
					all_keywords.add(keyword);
					
					// For every author in the authors of any given paper
					ArrayList<String> co_keywords = new ArrayList<String>();
					co_keywords.addAll(paper.key_words);
					co_keywords.remove(keyword);
					
					// This hashtable counts how many times each co_keywords appeared for each keyword
					Hashtable<String, Integer> all_co_keywords_counter = new Hashtable<String, Integer>();
					
					// Add the occurrence of this co-authorship into the global map
					for(String co_keyword : co_keywords)
					{
						// Check if this isn't the first time we see this keyword - get the old counter if so
						if (golbal_map.containsKey(keyword))
						{
							all_co_keywords_counter = golbal_map.get(keyword);
						}
						
						// Check how many this co-keyword appeared for this keyword - if it's null, this is the first time
						Integer num_of_co_keywords = all_co_keywords_counter.get(co_keyword);
						int new_num = 1;
						if (num_of_co_keywords != null)
						{
							new_num = num_of_co_keywords + 1;
						}
						
						all_co_keywords_counter.put(co_keyword, new Integer(new_num));	
					}
					
					// Update the global counter for this give author
					golbal_map.put(keyword, all_co_keywords_counter);
				}
			}
			
			// Display number of co-keywords for each keyword if DEBUG is set on
			if (DEBUG)
			{
				for(String keyword : golbal_map.keySet())
				{
					System.out.println("For the keyword " + keyword);
					
					Hashtable<String, Integer> co_keywords_counts = golbal_map.get(keyword);
					for(String co_keyword : co_keywords_counts.keySet())
					{
						System.out.println("\t" + co_keyword + " has co-keyworded in " + co_keywords_counts.get(co_keyword) + " papers.");
					}
				}
			}
			
			// Write the file out
			write_out(golbal_map, all_keywords, all_keywords, output_file);
			
			// The given output file should loads properly in NetDriller.
			// To do so, under import graph, choose "One Mode", "Undirected", "CSV file" and mark the "The file contains headers" checkbox. 
		}
		catch (Exception e)
		{
			System.out.println("Was not able to write to output file. Please check the file can be created!");
			e.getMessage();
			e.printStackTrace();
		}
	}
	
	/***
	 * This method creates a dataset for a two-mode weighted graph.
	 * 
	 * Nodes of the first type are authors. Nodes of the second type are keywords.
	 * Edges between the two types of nodes are keywords in papers that authors wrote.
	 * The weight of the edge indicates the number of times the author has used the connected keyword.
	 * 
	 * @param output_file - This is the file to which the resulting dataset will be written in a csv format.
	 * @param papers - An ArrayList of all the Paper(s) (container class for input) that were provided as input.
	 * 
	 */
	private static void do_authors_and_keywords(File output_file, List<Paper> papers)
	{
		try
		{
			// A hashtable where the key is an author and the value is a counter hashtable 
			// In the counter hashtable the keywords are the keys and their occurrences are the values. 
			Hashtable<String, Hashtable<String, Integer>> golbal_map = new Hashtable<String, Hashtable<String, Integer>>(); 
			
			// These are used to generate the output file
			HashSet<String> all_authors = new HashSet<String>();
			HashSet<String> all_keywords = new HashSet<String>();
			
			for(Paper paper : papers)
			{
				for(String author : paper.authors)
				{
					all_authors.add(author);
					
					// For every author in the authors of any given paper
					ArrayList<String> keywords = new ArrayList<String>();
					keywords.addAll(paper.key_words);
					
					// This hashtable counts how many times each keyword appeared for each author
					Hashtable<String, Integer> all_keywords_counter = new Hashtable<String, Integer>();
					
					// Add the occurrence of this  into the global map
					for(String keyword : keywords)
					{
						all_keywords.add(keyword);
						
						// Check if this isn't the first time we see this keyword - get the old counter if so
						if (golbal_map.containsKey(author))
						{
							all_keywords_counter = golbal_map.get(author);
						}
						
						// Check how many this keyword appeared for this author - if it's null, this is the first time
						Integer num_of_keywords = all_keywords_counter.get(keyword);
						int new_num = 1;
						if (num_of_keywords != null)
						{
							new_num = num_of_keywords + 1;
						}
						
						all_keywords_counter.put(keyword, new Integer(new_num));	
					}
					
					// Update the global counter for this give author
					golbal_map.put(author, all_keywords_counter);
				}
			}
			
			// Display number of keywords for each author if DEBUG is on
			if (DEBUG)
			{
				for(String author : golbal_map.keySet())
				{
					System.out.println("For the author named " + author + " we had the following keywords:");
					
					Hashtable<String, Integer> keywords_counts = golbal_map.get(author);
					for(String keyword : keywords_counts.keySet())
					{
						System.out.println("\tThe keyword '" + keyword + "' has appeared in " + keywords_counts.get(keyword) + " papers.");
					}
				}
			}
			
			// Write the file out
			write_out(golbal_map, all_keywords, all_authors, output_file);
			
			// The given output file should loads properly in NetDriller.
			// To do so, under import graph, choose "Two Mode", "Undirected", "CSV file" and mark the "The file contains headers" checkbox. 
		}

			
		catch (Exception e)
		{
			System.out.println("Was not able to write to output file. Please check the file can be created!");
			e.getMessage();
			e.printStackTrace();
		}
	}
	
	/***
	 * 
	 * This method writes out a CSV file.
	 * 
	 * @param golbal_map - this is the global map with the relationships from one of the three methods above
	 * @param column_headers - These are the unique column headers (keywords or authors)
	 * @param row_start - These are the unique rows (keywords or authors)
	 * @param output_file - This is the output file that was opened when the utility was validating user parameters input.
	 */
	
	public static void write_out(Hashtable<String, Hashtable<String, Integer>> golbal_map, HashSet<String> column_headers, HashSet<String> row_start, File output_file )
	{		
		try 
		{
			// Write CSV file
			FileWriter writer = new FileWriter(output_file, false); // Overwrites any other output file!
			BufferedWriter bw = new BufferedWriter(writer);
			
			// Create all the columns
			for (String column_heading : column_headers)
			{
				bw.write("," + column_heading.replaceAll(",", ""));
			}
			bw.newLine();
			
			// Fill the rest of the CSV file
			for (String row_heading : row_start)
			{
	
				bw.write(row_heading.replaceAll(",", ""));
				
				Hashtable<String, Integer> items_with_relationship = golbal_map.get(row_heading);
				for (String column_heading : column_headers)
				{
					Integer value = items_with_relationship.get(column_heading);
					if (value == null)
					{
						value = 0;
					}
					
					bw.write("," + value.toString());
				}
				
				bw.newLine();
			}
			
			System.out.println("CSV file was created successfuly at " + output_file.getAbsolutePath());
		
			// Close writers
	
			 bw.flush();
			 bw.close();
			 writer.close();
		}
		 catch (IOException e1) 
		{
			System.out.println("Error opening writer for the file. Please make sure the file path has permissions to write.");
			System.exit(0);
		}
	}
	
	
	/***
	 * 
	 * @param file_path - the full path to the input file
	 * @return a string that includes the entire content from the input file
	 */
	public static String parse_input_file(String file_path)
	{
		String json = "";
		try 
		{
			json = new String(Files.readAllBytes(Paths.get(file_path)), StandardCharsets.UTF_8);
			
		} 
		catch (FileNotFoundException e) 
		{
			// File not found
			System.out.println("The indicated file was not found. Please make sure the file " + file_path + " exists at the specified path.");
			print_instructions_and_exit_program();
		}
		catch (Exception e) 
		{
			// File not found
			System.out.println("There was an error raeding and/or closing the file. Make sure the file " + file_path + " exists and that the proper permissions are given to it.");
			print_instructions_and_exit_program();
		}
		
		return json;
	}
	
	/***
	 * This method prints out the instructions of the program
	 */
	public static void print_instructions_and_exit_program()
	{
		System.out.println("Please user the proper command line parameters:");
		System.out.println("java -jar Parser.jar <MODE_PARAMETER> <INPUT_FILE_NAME> [Optional: <OUTPUT_FILE_NAME> - defaults to output.csv]");
		System.out.println("Mode parameters:");
		System.out.println("1 - will produce a dataset of authors with relationship based on number of coauthored papers from the input file");
		System.out.println("2 - will produce a dataset of keywords from titles of papers where the relationship between keywords is based on their co-occurrence in same title.");
		System.out.println("3 - will produce a dataset for weighted two-mode network between authors and keywords");
		System.out.println("Input file has to be a in JSON format that adheres to the following schema:");
		System.out.println("[ { \"authors\": [\"name_1\", \"name_2\"], \"title\": \"title_of_article\", \"venue\": \"name_of_venue\", \"year\": 1988, \"keywords\": [\"keyword1\", \"keyword2\"] } , <ADDITIONAL ARTICLES FOLLOWING THE SAME JSON SCHEMA> ]");
		System.out.println("Full usage example:");
		System.out.println("java -jar Parser.jar 1 input.json");
		System.out.println("java -jar Parser.jar 1 input.json spacial_output_name.csv");
		System.exit(0);
	}
	
    /**
     * * Checks if a string is a valid path.
     * Null safe.
     * Based on this stack overflow question: 
     * http://stackoverflow.com/questions/468789/is-there-a-way-in-java-to-determine-if-a-path-is-valid-without-attempting-to-cre
     *  
     * Calling examples:
     *    path("c:/test");      //returns true
     *    path("c:/te:t");      //returns false
     *    path("c:/te?t");      //returns false
     *    path("c/te*t");       //returns false
     *    path("good.txt");     //returns true
     *    path("not|good.txt"); //returns false
     *    path("not:good.txt"); //returns false
     */
    public static boolean check_path(String path) {

        try 
        {
            Paths.get(path);

        }
        catch (InvalidPathException |  NullPointerException ex) 
        {
            return false;
        }

        return true;
    }
}
