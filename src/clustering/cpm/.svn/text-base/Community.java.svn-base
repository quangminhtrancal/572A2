
package clustering.cpm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import data.GraphNode;


/**
 * Representing a community found 
 * @author Dequan
 *
 */
public class Community<V,E>
{

    public Community()
    {
        this( false );
    }

    public Community(boolean approximated)
    {
    	isCliqueBased = true;
    	vertices = new ArrayList<V>();
        cliques = new TreeSet<Clique<V>>();
        labelColor = Color.WHITE;
        color = GuiUtil.getRandomColor();
        this.approximated = approximated;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setNewRandomColor()
    {
        color = GuiUtil.getRandomColor();
    }

    public void addVertex(V s)
    {
        vertices.add(s);
    }

    public List<V> getVertices()
    {
        return vertices;
    }

    public void addClique(Clique c)
    {
        cliques.add(c);
    }
    
    public void setCliques(Set<Clique<V>> value)
    {
    	isCliqueBased = true;
        cliques = value;
        vertices = new ArrayList<V>();
        Set<String> allNodes = new HashSet<String>();
        for(Clique c: cliques ){
        	//vertices.addAll( c.getVertices() );
        	// ignore the duplicate vertices
        	for( Object v :c.getVertices() ){
        		V node = (V)v;
        		if( ! allNodes.contains(node.toString()) ){
        			allNodes.add(node.toString());
        			vertices.add( node );
        		}
        	}
        }
    }
    
    public void setVertices(List<V> vertices)
    {
    	isCliqueBased = false;
        this.vertices = vertices;
    }
    
    public Set getCliques()
    {
        return cliques;
    }

    public int getSize()
    {
    	return vertices==null ? 0 : vertices.size();
    }

    public String getVerticesString()
    {
        String ret = index + "(" + vertices.size() + ") : ";
        for(Iterator i = vertices.iterator(); i.hasNext();)
            ret += i.next().toString() + ",";
        return ret;
    }
    
    public String getCliquesString()
    {
        String ret = index + "(" + cliques.size() + "):";
        for(Iterator<Clique<V>> i = cliques.iterator(); i.hasNext();)
            ret += i.next().getIndex() + ",";
        return ret;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int i)
    {
        index = i;
    }
    

    private List<V> vertices;
    private Set<Clique<V>> cliques;
    private int index;
    private Color color;
    public Color labelColor;
    public boolean approximated;
    private boolean isCliqueBased;
    public static Color DEFAULT_COLOR = Color.RED;
}
