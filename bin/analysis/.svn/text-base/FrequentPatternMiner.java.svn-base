package analysis;

import java.util.TreeMap;
import java.util.Vector;

import data.ItemSet;

/**
 * The FrequentPatternMiner class mines a matrix (of the social network) to create a social network of the frequent patterns (matrix format).
 */
public class FrequentPatternMiner
{
	String[] columnHeaders;						// In order of columns
	TreeMap<String, Integer> headerNumbers;		// Holds the mapping of the header string and column number
	double[][] adjacencyMatrix;					// Has the form values[row][column]
	int minSupport;
	Vector<Vector<ItemSet>> frequentPatterns = new Vector<Vector<ItemSet>>();// First element contains frequent itemsets of size 1, second of size 2, third of size 3, etc...
	
    /**
     * Creates the FrequentPatternMiner object with the required information.
     * @param columnHeaders the column headings in order of columns
     * @param adjacencyMatrix the matrix of the network (form values[row][column])
     * @param minSupport the minimum support for a pattern to be frequent
     */
	public FrequentPatternMiner(String[] columnHeaders, double[][] adjacencyMatrix, int minSupport)
	{
		this.columnHeaders = columnHeaders;
		this.adjacencyMatrix = adjacencyMatrix;
		this.minSupport = minSupport;
		headerNumbers = new TreeMap<String, Integer>();
		
		// This try is needed as currently NetDriller does not take node names
		try
		{
			for (int i = 0; i < columnHeaders.length; i++)
				headerNumbers.put(columnHeaders[i], i);
		}
		catch (NullPointerException e)
		{
			String[] columns = new String[adjacencyMatrix[0].length];
			for (int i = 0; i < adjacencyMatrix[0].length; i++)
			{
				headerNumbers.put(Integer.toString(i), i);
				columns[i] = Integer.toString(i);
			}
			
			this.columnHeaders = columns;
		}
	}
	
    /**
     * Creates an adjacency matrix using the frequent patterns as links with weights indicating the number of frequent patterns.
     * Note: Columns remain the same columns as input
     * @return adjacency matrix of frequent patterns
     */
	public double[][] generateFrequentPatternNetwork()
	{
		Vector<ItemSet> itemsets = new Vector<ItemSet>();
		double[][] network = new double[columnHeaders.length][columnHeaders.length];
		
		for (String item : columnHeaders)
		{
			ItemSet itemset = new ItemSet();
			itemset.items.add(item);
			itemsets.add(itemset);
		}
		
		findFrequentPatterns(itemsets);
		
		for (Vector<ItemSet> frequentSets : frequentPatterns)
			for (ItemSet frequentSet : frequentSets)
				for (int i = 0; i < frequentSet.items.size(); i++)
					for (int j = i+1; j < frequentSet.items.size(); j++)
						if (j < network.length)
						{
							network[headerNumbers.get(frequentSet.items.get(i))][headerNumbers.get(frequentSet.items.get(j))] = network[headerNumbers.get(frequentSet.items.get(i))][headerNumbers.get(frequentSet.items.get(j))] + 1;
							network[headerNumbers.get(frequentSet.items.get(j))][headerNumbers.get(frequentSet.items.get(i))] = network[headerNumbers.get(frequentSet.items.get(j))][headerNumbers.get(frequentSet.items.get(i))] + 1;
						}
		
		return network;
	}
	
    /**
     * Creates an adjacency matrix using the closed frequent patterns as links with weights indicating the number of frequent patterns.
     * Note: Columns remain the same columns as input
     * @return adjacency matrix of frequent patterns
     */
	public double[][] generateClosedFrequentPatternNetwork()
	{
		Vector<ItemSet> itemsets = new Vector<ItemSet>();
		double[][] network = new double[columnHeaders.length][columnHeaders.length];
		
		for (String item : columnHeaders)
		{
			ItemSet itemset = new ItemSet();
			itemset.items.add(item);
			itemsets.add(itemset);
		}
		
		findFrequentPatterns(itemsets);
		
		for (int i = frequentPatterns.size()-1; i >= 0; i--)
		{
			Vector<ItemSet> frequentSets = frequentPatterns.get(i);
			for (int j = 0; j < frequentSets.size(); j++)
			{
				ItemSet itemset = frequentSets.get(j);
				for (int ii = i-1; ii >= 0; ii--)
				{
					if (ii < 0)
					{
						Vector<ItemSet> frequentSets2 = frequentPatterns.get(ii);
						for (int jj = 0; jj < frequentSets2.size(); jj++)
						{
							ItemSet itemset2 = frequentSets2.get(jj);
							if (isSubset(itemset2, itemset))
								if (itemset2.support == itemset.support)
									itemset2.items.clear();
						}
					}
				}
			}
		}
		
		for (Vector<ItemSet> frequentSets : frequentPatterns)
			for (ItemSet frequentSet : frequentSets)
				for (int i = 0; i < frequentSet.items.size(); i++)
					for (int j = i+1; j < frequentSet.items.size(); j++)
						if (j < network.length)
						{
							network[headerNumbers.get(frequentSet.items.get(i))][headerNumbers.get(frequentSet.items.get(j))] = network[headerNumbers.get(frequentSet.items.get(i))][headerNumbers.get(frequentSet.items.get(j))] + 1;
							network[headerNumbers.get(frequentSet.items.get(j))][headerNumbers.get(frequentSet.items.get(i))] = network[headerNumbers.get(frequentSet.items.get(j))][headerNumbers.get(frequentSet.items.get(i))] + 1;
						}
		
		return network;
	}
	
    /**
     * Creates an adjacency matrix using the maximal frequent patterns as links with weights indicating the number of frequent patterns.
     * Note: Columns remain the same columns as input
     * @return adjacency matrix of frequent patterns
     */
	public double[][] generateMaximalFrequentPatternNetwork()
	{
		Vector<ItemSet> itemsets = new Vector<ItemSet>();
		double[][] network = new double[columnHeaders.length][columnHeaders.length];
		
		for (String item : columnHeaders)
		{
			ItemSet itemset = new ItemSet();
			itemset.items.add(item);
			itemsets.add(itemset);
		}
		
		findFrequentPatterns(itemsets);
		
		for (int i = frequentPatterns.size()-1; i >= 0; i--)
		{
			Vector<ItemSet> frequentSets = frequentPatterns.get(i);
			for (int j = 0; j < frequentSets.size(); j++)
			{
				ItemSet itemset = frequentSets.get(j);
				for (int ii = i-1; ii >= 0; ii--)
				{
					Vector<ItemSet> frequentSets2 = frequentPatterns.get(ii);
					for (int jj = 0; jj < frequentSets2.size(); jj++)
					{
						ItemSet itemset2 = frequentSets2.get(jj);
						if (isSubset(itemset2, itemset))
							itemset2.items.clear();
					}
				}
			}
		}
		
		for (Vector<ItemSet> frequentSets : frequentPatterns)
			for (ItemSet frequentSet : frequentSets)
				for (int i = 0; i < frequentSet.items.size(); i++)
					for (int j = i+1; j < frequentSet.items.size(); j++)
						if (j < network.length)
						{
							network[headerNumbers.get(frequentSet.items.get(i))][headerNumbers.get(frequentSet.items.get(j))] = network[headerNumbers.get(frequentSet.items.get(i))][headerNumbers.get(frequentSet.items.get(j))] + 1;
							network[headerNumbers.get(frequentSet.items.get(j))][headerNumbers.get(frequentSet.items.get(i))] = network[headerNumbers.get(frequentSet.items.get(j))][headerNumbers.get(frequentSet.items.get(i))] + 1;
						}
		
		return network;
	}
	
    /**
     * Uses apriori algorithm to find frequent patterns recursively.
     * @param itemsets the itemsets to start mining with
     */
	public void findFrequentPatterns(Vector<ItemSet> itemsets)
	{
		Vector<ItemSet> frequentItemSets = new Vector<ItemSet>();
		
		for (ItemSet itemset : itemsets)
		{
			int support = findSupport(itemset);
			if (support >= minSupport)
			{
				itemset.support = support;
				frequentItemSets.add(itemset);
			}
		}
		
		if (frequentItemSets.size() > 0)
		{
			frequentPatterns.add(frequentItemSets);
			findFrequentPatterns(findAllCombinations(frequentItemSets, frequentPatterns.size()+1));
		}
	}
	
    /**
     * Determines the number of transactions which support a itemset.
     * @param itemset the itemset to find support for
     * @return support value of the itemset
     */
	public int findSupport(ItemSet itemset)
	{
		int support = 0;
		Vector<Integer> items = new Vector<Integer>();
		
		for (String item : itemset.items)
			items.add(headerNumbers.get(item));
		
		for (int i = 0; i < adjacencyMatrix.length; i++)
		{
			int numContainedItems = 0;
			for (Integer column : items)
				if (adjacencyMatrix[i][column] > 0)
					numContainedItems++;
			
			if (numContainedItems == itemset.items.size())
				support++;
		}
		
		return support;
	}
	
    /**
     * Finds all the k combinations of items from several itemsets.
     * @param itemsets the itemsets to containing the items
     * @param size the size of the combinations (k)
     * @return all the possible combinations of items within the itemsets
     */
	public Vector<ItemSet> findAllCombinations(Vector<ItemSet> itemsets, int size)
	{
		Vector<ItemSet> combinations = new Vector<ItemSet>();
		Vector<String> allItems = new Vector<String>();
		
		for (ItemSet itemset : itemsets)
			for (String item : itemset.items)
				if (!allItems.contains(item))
					allItems.add(item);
		
		// Modified code from http://stackoverflow.com/questions/3931775/java-simple-combination-of-a-set-of-element-of-k-order
	    for (int i = 0; i <= allItems.size() - size; i++)
	    {
	        for (int j = i + size - 1; j < allItems.size(); j++)
	        {
	            ItemSet is = new ItemSet();
	            for (int m = i; m < i + size - 1; m++)
	            	is.items.add(allItems.get(m));
	            is.items.add(allItems.get(j));
	            combinations.add(is);
	        }
	    }
		
		return combinations;
	}

    /**
     * Determines if one itemset is a subset of another itemset.
     * @param subSet first itemset that is assumed to be the subset
     * @param superSet second itemset that is assubmed to be the superset
     * @return true if the first itemset is a subset of the second itemset, false otherwise
     */
	public boolean isSubset(ItemSet subSet, ItemSet superSet)
	{
		boolean isSubset = false;
		int numOfSameItems = 0;
		
		if (subSet.items.size() < superSet.items.size())
			for (String item : subSet.items)
				for (String superItem : superSet.items)
					if (item == superItem)
						numOfSameItems++;
		
		if (numOfSameItems == subSet.items.size())
			isSubset = true;
		
		return isSubset;
	}
}
