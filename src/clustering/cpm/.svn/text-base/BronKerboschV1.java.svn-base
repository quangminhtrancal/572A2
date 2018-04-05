package clustering.cpm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.uci.ics.jung.graph.UndirectedGraph;

/**
 * The Bron Kerbosch algorithm is for finding maximal cliques in an undirected graph.
 * See paper : Bron, Coen; Kerbosch, Joep (1973), "Algorithm 457: finding all cliques of an undirected graph", 
 * Commun. ACM (ACM) 16 (9): 575–577, doi:10.1145/362342.362367
 * See wikipedia: http://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
 *  
 * This is the version 1 of the algorithm which does not use pivot selection
 * @author Dequan
 * @param <V> vertex type
 * @param <E> edge type
 */
public class BronKerboschV1<V, E> extends AbstractCliqueFinder<V,E>
{
    /**
     * constructor
     * @param graph the simple graph
     */
    public BronKerboschV1(UndirectedGraph<V, E> graph)
    {
        super(graph);
    }
    
    @Override
    protected void findCliques(List<V> R, List<V> P, List<V> X)
    {
        trace(R,P,X);
        indent ++;
        
        List<V> oldP = new ArrayList<V>(P);
        
        if (!isEnd(P, X)) {
            // for each vertex v in P
            for (V v : oldP) {
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

                // if P and X are both empty:  report R as a maximal clique
                if (newP.isEmpty() && newX.isEmpty()) {
                    Clique<V> clique = new Clique<V>( new ArrayList<V>(R) );
                    clique.setIndex( cliques.size() );
                    cliques.add( clique );
                    System.out.println( "Clique:" + R.size() + ":" + clique + ":" + clique.getSize() );
                } 
                else {
                    // recursive to find 
                    findCliques( R, newP, newX);
                } // of else

                // X = X union {v}
                X.add(v);
                R.remove(v);
            }// end for  
        }// end if
        indent --;
    }
}
