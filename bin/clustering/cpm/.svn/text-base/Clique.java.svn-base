
package clustering.cpm;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Representing Cliques
 * @author Dequan
 */
public class Clique<V>
{

    public Clique(List<V> vertices)
    {
        this.vertices = vertices;
        color = GuiUtil.getRandomColor();
    }

    public Clique()
    {
        vertices = new ArrayList<V>();
        color = GuiUtil.getRandomColor();
    }

    public void addVertex(V s)
    {
        vertices.add(s);
    }
    
    @Override
    public String toString()
    {
        String ret = index + "(" + getSize() + ") : ";
        for(int i=0; i< vertices.size(); i ++ ){
            V v = vertices.get(i);
        	ret += v.toString();
            if( i < vertices.size()-1 ){
            	ret += ",";
            }
        }
        
        return ret;
    }

    public List<V> getVertices()
    {
        return vertices;
    }

    public int getSize(){
    	return vertices==null ? 0 : vertices.size();
    }
    
    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public int getOverlap(Clique<V> c2 ){
    	int overlap = 0;
    	
    	if( getSize() ==0 || c2.getSize() ==0 ){
    		return 0;
    	}
    	
    	for(V v : c2.getVertices() ){
    		if( vertices.contains(v)){
    			overlap++;
    		}
    	}
    	
    	return overlap;
    }
    
    public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	private List<V> vertices;
    private int index = -1;
    private Color color;
    public boolean approximated;
    public static Color DEFAULT_COLOR = Color.RED;
}
