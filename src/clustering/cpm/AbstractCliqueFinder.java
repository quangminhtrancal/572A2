package clustering.cpm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * abstract class for Clique Finder
 * @author Dequan
 *
 * @param <V> vertex type
 * @param <E> edge type
 */
abstract public class AbstractCliqueFinder<V, E>
{
    protected final UndirectedGraph<V, E> graph;
    protected List<Clique<V>> cliques;
    protected int indent = 0;

    /**
     * constructor
     * @param graph the simple graph
     */
    public AbstractCliqueFinder(UndirectedGraph<V, E> graph)
    {
        this.graph = graph;
    }

    /**
     * Get all maximal cliques of the graph. A maximal clique 
     * @return Collection of cliques
     */
    public List<Clique<V>> getAllMaximalCliques()
    {
        cliques = new ArrayList<Clique<V>>();
        // the compsub Set representing the potential cliques
        List<V> R = new ArrayList<V>();
        // the candidates Set representing the candidates
        List<V> P = new ArrayList<V>();
        // the not Set representing the nodes that are already processed
        List<V> X = new ArrayList<V>();
        
        // P is initialized for containing all vertices
        P.addAll(graph.getVertices());
        
        // find all the cliques
        findCliques(R, P, X);
        
        return cliques;
    }

    /**
     * Get the biggest maximal cliques of the graph.
     * @return Collection of cliques
     */
    public Collection<Clique<V>> getBiggestMaximalCliques()
    {
        // find all maximal cliques
        getAllMaximalCliques();

        int max = 0;
        Collection<Clique<V>> biggestMaximalCliques = new ArrayList<Clique<V>>();
        
        for (Clique<V> clique : cliques) {
            if (max < clique.getSize()) {
                max = clique.getSize();
            }
        }
        for (Clique<V> clique : cliques) {
            if (max == clique.getSize()) {
                biggestMaximalCliques.add(clique);
            }
        }
        return biggestMaximalCliques;
    }
    /**
     * Find all the cliques 
     * @param R the compsub Set representing the potential cliques
     * @param P the candidates Set representing the candidates
     * @param X the not Set representing the nodes that are already processed
     */
    abstract protected void  findCliques(List<V> R, List<V> P, List<V> X);

    /**
     * check if a node in X is connected to all nodes in P, if that is the case,
     * should finish the recursive algorithm 
     * @param P
     * @param X
     * @return
     */
    protected boolean isEnd(List<V> P, List<V> X)
    {
        boolean end = false;
        int count;
        for (V found : X) {
            count = 0;
            for (V candidate : P) {
                if (graph.isNeighbor(found, candidate)) {
                    count++;
                } 
            } 
            if (count == P.size()) {
                end = true;
            }
        }
        return end;
    }
    
    /**
     * print the R,P,X
     * @param R
     * @param P
     * @param X
     */
    public void trace(List<V> R, List<V> P, List<V> X){
      String szR = "R: {";
      String szP = "P: {";
      String szX = "X: {";
      
      for(V v: R){
        szR += v + ", ";
      }  
      szR += "}, ";
      for(V v: P){
        szP += v + ", ";
      }  
      szP += "}, ";
      for(V v: X){
        szX += v + ", ";
      }  
      szX += "}";
      System.out.print( indent + "-BK( " );
      System.out.print( szR );
      System.out.print( szP );
      System.out.print( szX );
      System.out.println( " );" );
    }
    
    /**
     * return the result as a string that can be printed as output
     * @return
     */
    public String getResult(){
      StringBuilder sb = new StringBuilder("Maximal Cliques (" + cliques.size() + "):\n");
      for(Clique c:  cliques){
        sb.append(c.toString());
        sb.append("\n"); 
      }
      return sb.toString();
    }
}
