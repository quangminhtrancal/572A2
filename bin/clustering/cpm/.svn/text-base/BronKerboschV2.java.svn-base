package clustering.cpm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * The Bron Kerbosch algorithm is for finding maximal cliques in an undirected graph.
 * See paper : Bron, Coen; Kerbosch, Joep (1973), "Algorithm 457: finding all cliques of an undirected graph", 
 * Commun. ACM (ACM) 16 (9): 575–577, doi:10.1145/362342.362367
 * See wikipedia: http://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
 *  
 * This is the version 2 of the algorithm which uses pivot selection
 * @author Dequan
 * @param <V> vertex type
 * @param <E> edge type
 */
public class BronKerboschV2<V, E> extends AbstractCliqueFinder<V,E>
{
    Random random = new Random();
    /**
     * constructor
     * @param graph the simple graph
     */
    public BronKerboschV2(UndirectedGraph<V, E> graph)
    {
        super(graph);
    }
    
    @Override
    protected void findCliques(List<V> R, List<V> P, List<V> X)
    {
        trace(R,P,X);
        indent ++;
        
        List<V> oldP = new ArrayList<V>(P);
     
        // if P and X are both empty:  report R as a maximal clique
        if (P.isEmpty() && X.isEmpty()) {
        	Clique<V> clique = new Clique<V>( new ArrayList<V>(R) );
            clique.setIndex( cliques.size() );
            cliques.add( clique );
            System.out.println( "Clique:" + R.size() + ":" + clique + ":" + clique.getSize() );
            
            indent --;
            return;
        } 
        
        // choose a pivot vertex u in set P, (a paper says should be P union X)
        // it has two options :
        // 1) a random vertex from P  (IK_RP)
        // 2) a vertex which has the largest degree in P ( IK_GP, performs better in random graphs )

        List<V> pivotList = P;
        
        if(!P.isEmpty() && X.isEmpty())
        	pivotList = P;
        else if(P.isEmpty() && !X.isEmpty())
        	pivotList = X;
        else
        {
        	pivotList = random.nextBoolean() ? P : X;
        }
        int pivotIdx = random.nextInt( pivotList.size() );
        V pivot = pivotList.get( pivotIdx );

        //System.out.println("Pivot:" + pivot );
        
        // for each vertex v in P
        for (V v : oldP) {
        	// if v is a neighbor of pivot vertex u then continue
        	if (graph.isNeighbor(v, pivot))
        	{
        		continue;
        	}

        	List<V> newP = new ArrayList<V>();
        	List<V> newX = new ArrayList<V>();

        	// Rnew = R union {v}
        	R.add(v);
        	// P = P − {v}
        	P.remove(v);

        	// Pnew = P intersect N[v]
        	for (V newCandidate : P) {
        		if (graph.isNeighbor(v, newCandidate))
        		{
        			newP.add(newCandidate);
        		} 
        	} 

        	// Xnew = X intersect N[v]
        	for (V newFound : X) {
        		if (graph.isNeighbor(v, newFound)) {
        			newX.add(newFound);
        		} 
        	} 

        	// recursive to find 
        	findCliques( R, newP, newX);
        	// of else

        	// X = X union {v}
        	X.add(v);
        	R.remove(v);
        }// end for
        
        indent --;
    }
}
