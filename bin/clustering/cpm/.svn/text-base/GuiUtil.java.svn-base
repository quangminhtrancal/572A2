package clustering.cpm;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.MapTransformer;
import data.GraphEdge;
import data.GraphNode;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 * GUI utility functions
 * @author Dequan
 *
 */
public class GuiUtil {
	/**
	 * for choosing the layout 
	 * @author Dequan
	 */
	private static final class LayoutChooser implements ActionListener
    {
        private final JComboBox jcb;
        private final VisualizationViewer vv;
        private final Graph g;
        
        private LayoutChooser(JComboBox jcb, VisualizationViewer vv, Graph g)
        {
            super();
            this.jcb = jcb;
            this.vv = vv;
            this.g = g;
        }

        public void actionPerformed(ActionEvent arg0)
        {
            Object[] constructorArgs =
                { };

            Class<? extends Layout> layoutC = 
                (Class<? extends Layout>) jcb.getSelectedItem();
//            Class lay = layoutC;
            try
            {
                Constructor<? extends Layout> constructor = layoutC.getConstructor(new Class[] {Graph.class});
                Object o = constructor.newInstance( g );
                Layout l = (Layout) o;
                l.setInitializer(vv.getGraphLayout());
                l.setSize(vv.getSize());
                
                // Do a transition 
				LayoutTransition lt = new LayoutTransition(vv, vv.getGraphLayout(), l);
				Animator animator = new Animator(lt);
				animator.start();
				vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
				vv.repaint();  
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
	public static void centerOnScreen(JFrame frame){
		// adjust the frame window to the center of screen
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		frame.setSize(screenWidth / 2, screenHeight / 2);
		frame.setLocation(screenWidth / 4, screenHeight / 4);
	}
	
	public static void showGraph(Graph g){
		showGraph(g, null, null, null, null );
	}
	
	/**
	 * Show a graph in a new window 
	 * @param graph
	 */
	public static void showGraph(final Graph graph, final Map<GraphNode,Paint> vertexPaints, final Map<GraphEdge,Paint> edgePaints, final List<Community> communities, final List<Clique<GraphNode>> cliques){
		Layout layout = new FRLayout( graph );
		//layout.setSize(new Dimension(800,800));

		final VisualizationViewer vv = new VisualizationViewer( layout );
		//Sets the viewing area size
		//vv.setPreferredSize(new Dimension(800,800));
		
		// Default mouse behavior
		DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();
		vv.setGraphMouse(graphMouse);
		
		//Setup up a new vertex to paint transformer...
		Transformer<GraphNode,Paint> defaultVertexPaint = new Transformer<GraphNode,Paint>() {
			public Paint transform(GraphNode n) {
				return n.getColor();
			}
		};
		
		
		// set vertex/edge style
		if( vertexPaints == null ){
			vv.getRenderContext().setVertexFillPaintTransformer(defaultVertexPaint);
		}else{
			vv.getRenderContext().setVertexFillPaintTransformer(MapTransformer.<GraphNode,Paint>getInstance(vertexPaints));
		}
		if( edgePaints != null ){
			vv.getRenderContext().setEdgeDrawPaintTransformer(MapTransformer.<GraphEdge,Paint>getInstance(edgePaints));

			vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<GraphEdge,Stroke>() {
			    protected final Stroke THIN = new BasicStroke(1);
			    protected final Stroke THICK= new BasicStroke(2);
			    public Stroke transform(GraphEdge e)
			    {
				Paint c = edgePaints.get(e);
				if (c == Color.LIGHT_GRAY)
				    return THIN;
				else 
				    return THICK;
			    }
			});
		}
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		//vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.S);
        
		// create scaling controls 
        final ScalingControl scaler = new CrossoverScalingControl();
        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });
        JButton reset = new JButton("reset");
        reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Layout<Integer,Number> layout = vv.getGraphLayout();
				layout.initialize();
				Relaxer relaxer = vv.getModel().getRelaxer();
				if(relaxer != null) {
					relaxer.stop();
					relaxer.prerelax();
					relaxer.relax();
				}
			}});
        
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(((DefaultModalGraphMouse<Integer,Number>)vv.getGraphMouse()).getModeListener());
        
        
        // create a jpanel 
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add( new GraphZoomScrollPane(vv), BorderLayout.CENTER);
        Class[] combos = getCombos();
        final JComboBox jcb = new JComboBox(combos);
        // use a renderer to shorten the layout name presentation
        jcb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String valueString = value.toString();
                valueString = valueString.substring(valueString.lastIndexOf('.')+1);
                return super.getListCellRendererComponent(list, valueString, index, isSelected,
                        cellHasFocus);
            }
        });
        
        JButton showGraphBtn = new JButton("show whole graph");
        showGraphBtn.addActionListener(new LayoutChooser(jcb, vv, graph ));
        
        jcb.addActionListener(new LayoutChooser(jcb, vv, graph ) );
        jcb.setSelectedItem(FRLayout.class);

        JPanel control_panel = new JPanel(new GridLayout(2,1));
        JPanel topControls = new JPanel();
        JPanel bottomControls = new JPanel();
        control_panel.add(topControls);
        control_panel.add(bottomControls);
        rightPanel.add(control_panel, BorderLayout.NORTH);
        
        // add graph controls 
        topControls.add(jcb);
        topControls.add(showGraphBtn);
        topControls.add(new JLabel("Communities:" + communities.size() +", Cliques:" + cliques.size()) );
        bottomControls.add(plus);
        bottomControls.add(minus);
        bottomControls.add(modeBox);
        bottomControls.add(reset);
        
        
        //  add communities controls
        MyTreeNode rootTreeNode = new MyTreeNode("Result");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootTreeNode);
        final JTree treeResult = new JTree(treeModel);
        treeResult.setCellRenderer( new MyTreeCellRenderer() );
        if( cliques != null ){
        	MyTreeNode cliquesNode = new MyTreeNode("Cliques(" + cliques.size() + ")" );
        	rootTreeNode.add(cliquesNode);
        	for( Clique c : cliques )
        	{
        		MyTreeNode cNode = new MyTreeNode( new Integer( c.getIndex()) + ".clique(" + c.getSize() +")", false);
        		cliquesNode.add(cNode);
        	}
        }
        if( communities!= null ){
        	
        	MyTreeNode communitiesNode = new MyTreeNode("Communities(" + communities.size() + ")");
        	rootTreeNode.add(communitiesNode);
        	for( Community c : communities )
        	{
        		MyTreeNode cNode = new MyTreeNode( new Integer( c.getIndex()) + ".community(" + c.getSize() +")", false);
        		communitiesNode.add(cNode);
        	}
        }
        treeResult.addTreeSelectionListener( new TreeSelectionListener()
    	{
    	    		public void valueChanged(TreeSelectionEvent e){
    	    			TreePath[] tps = treeResult.getSelectionPaths();
    	    			TreePath tp = null;
    	                if(tps != null && tps.length > 0)
    	                {
    	                    // get community/clique by selection path
    	                    for(int k = 0; k < tps.length; k++)
    	                    {
    	                        tp = tps[k];
    	                        Object path[] = tp.getPath();
    	                        if(path == null || path.length <= 2)
    	                            continue;
    	                        
    	                        String szNode = path[2].toString();
    	                        Graph smallGraph = new UndirectedSparseGraph();
    	                        List<GraphNode> vertices = new ArrayList<GraphNode>();
    	                        int index = Integer.valueOf( szNode.substring( 0, szNode.indexOf(".")) );
    	                        if( szNode.indexOf(".comm") >= 1 ){
    	                        	// community
    	                        	Community<GraphNode,GraphEdge> c = communities.get( index -1 );
    	                        	System.out.println( c.getVerticesString() );
    	                        	vertices = c.getVertices();
    	                        }else if(szNode.indexOf(".clique") >= 1){
    	                        	// clique
    	                        	Clique<GraphNode> c = cliques.get( index -1 );
    	                        	System.out.println( c.toString()  );
    	                        	vertices = c.getVertices();
    	                        }
    	                        Color color = GuiUtil.getRandomColor(index);
    	                        // construct the graph 
    	                        for(GraphNode vertex: vertices){
	                        		smallGraph.addVertex(vertex);
	                        		vertex.setColor(  color );
	                        	}
	                        	for(GraphNode gn1: vertices){
	                    			for(GraphNode gn2: vertices){
	                    				if(gn1 != gn2 && graph.isNeighbor( findVertex(gn1.getLabel(),graph), findVertex(gn2.getLabel(),graph))){
	                    					smallGraph.addEdge(new GraphEdge(1), gn1, gn2 );
	                    				}
	                    			}	
	                    		}
    	                        // show the new small graph
    	                        Layout l = new FRLayout( smallGraph );
    	                        l.setInitializer(vv.getGraphLayout());
    	                        l.setSize(vv.getSize());
    	                        
    	                        // Do a transition 
    	        				LayoutTransition lt = new LayoutTransition(vv, vv.getGraphLayout(), l);
    	        				Animator animator = new Animator(lt);
    	        				animator.start();
    	        				vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
    	        				vv.repaint(); 
    	                    }
    	    		}//end if
    	    	}
    		}// end listener 
    	);
        // expand 
        treeResult.scrollPathToVisible(new TreePath( rootTreeNode.getLastLeaf().getPath()));
        
		// show frame
		JFrame jf = new JFrame();
		if(  communities == null && cliques == null  ){
			jf.getContentPane().add( rightPanel );
		}else{
			// show communities tree
			JSplitPane splitPane = new JSplitPane();
			splitPane.setLeftComponent( new JScrollPane(treeResult) );
			splitPane.setRightComponent(rightPanel);
			splitPane.setDividerLocation(150);
			jf.getContentPane().add( splitPane );
		}
		jf.setTitle("CPM Community Detection Algorithm - By Dequan Zhou.");
		
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GuiUtil.centerOnScreen(jf);
		jf.pack();
		jf.setVisible(true);	
	}

    @SuppressWarnings("unchecked")
    private static Class<? extends Layout>[] getCombos()
    {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        layouts.add(KKLayout.class);
        layouts.add(FRLayout.class);
        layouts.add(CircleLayout.class);
        layouts.add(SpringLayout.class);
        layouts.add(SpringLayout2.class);
        layouts.add(ISOMLayout.class);
        return layouts.toArray(new Class[0]);
    }
	/**
	 * save content in a file, if need to show,
	 * open a new window, and show the content
	 * @param string
	 * @throws IOException 
	 */
	public static boolean saveContentInFile(String content, String filePath, boolean needToShow) {
		File tempFile = null;
		PrintWriter out = null;
        boolean isOk = false;
        
		try {
			if( filePath == null ){
				tempFile = File.createTempFile("kdd-tmp", "");
			}else{
				tempFile = new File(filePath);
			}
			
			out = new PrintWriter(tempFile, "UTF-8");
            out.write(content);
            out.flush();
            
            // open in notepad
            if( needToShow ){
            	Runtime.getRuntime().exec("notepad " + tempFile.getAbsolutePath() );
            }
            
            isOk = true;
		} catch (IOException e) {
			e.printStackTrace();
			isOk = false;
		}finally{
			out.close();
		}
		
		return isOk;
	}
	/**
	 * find vertex according to id
	 * @param nodeId
	 * @param g
	 * @return
	 */
//	public static GraphNode findVertex(int nodeId, Graph<GraphNode,GraphEdge> g) {
//	    for (GraphNode n: g.getVertices())
//			if( n.id == nodeId)
//				return n;
//		return null;
//	}
	/**
	 * find vertex according to label
	 * @param nodeId
	 * @param g
	 * @return
	 */
	public static GraphNode findVertex(String label, Graph<GraphNode,GraphEdge> g) {
	    for (GraphNode n: g.getVertices())
			if( n.getLabel().equals(label))
				return n;
		return null;
	}
	
	public static Color getRandomColor(){
		return COLORS[(int)Math.floor(Math.random() * (double)COLORS.length)];
	}
	public static Color getRandomColor(int seed){
		return COLORS[ seed % COLORS.length];
	}
	
	public static Color COLORS[] = {
        new Color(Color.HSBtoRGB(0.0F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.05F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.1F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.15F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.2F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.25F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.3F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.35F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.4F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.45F, 0.9F, 0.9F)), 
        new Color(Color.HSBtoRGB(0.5F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.55F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.6F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.65F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.7F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.75F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.8F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.85F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.9F, 0.9F, 0.9F)), new Color(Color.HSBtoRGB(0.95F, 0.9F, 0.9F)), 
        new Color(Color.HSBtoRGB(1.0F, 0.9F, 0.9F))
    };

	
}
