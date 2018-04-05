package GUI;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.commons.collections15.Transformer;

import GUI.MainFrame.MetricType;
import GUI.NetworkImportPanel.FileType;
import analysis.MetricFinder;
import clustering.CommunityDetection;
import data.GraphEdge;
import data.GraphNode;
import data.NetworkGraph;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


//This class is the panel for displaying networks and their options. 
//It has three different panels inside: one for visualizing the network, one for controls like zooming, and one for displaying different reports about the network. 
public class NetworkPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MainFrame parentFrame;
	NetworkGraph netGraph;
	public List <NetworkGraph>  netgraphlist;
	public int index=0;
	int minRow = 1000000;
	int search = 0;
	
	JPanel netPnl, controlPanel, reportPanel;
	ScalingControl scaler = new CrossoverScalingControl();
	VisualizationViewer<GraphNode, GraphEdge> vv;
	DefaultModalGraphMouse<GraphNode, GraphEdge> graphMouse;
	
	
	public void repaint()
	{
		super.repaint();
		for(Component c: this.getComponents())
			c.repaint();
	}
	
	
	public NetworkPanel(MainFrame frame) {
		this.parentFrame = frame;
		netgraphlist = new ArrayList();
		frame.add(this);
		frame.setContentPane(this);
		
		
		TitledBorder titledBorder;
		Color panelsBGColor = new Color(210, 210, 210);
		Border blackline = BorderFactory.createLineBorder(Color.black);
		Border raisedbevel = BorderFactory.createRaisedBevelBorder();
		
		{ //Initializing the panel for network visualization
			netPnl = new JPanel();
			netPnl.setSize(700, 640);
			titledBorder = BorderFactory.createTitledBorder(blackline, "Network");
		//	titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(14.0f));
			netPnl.setBorder(BorderFactory.createCompoundBorder(raisedbevel, titledBorder));
			netPnl.setBackground(panelsBGColor);
			this.add(netPnl);
		
		}

		this.setLayout(null);
		
		{ //Initializing the panel for controlling the network 
			controlPanel = new JPanel();
			titledBorder = BorderFactory.createTitledBorder(blackline, "Controls");
//			titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(14.0f));
			controlPanel.setBounds(700, 00, 280, 200);
			controlPanel.setBackground(panelsBGColor);
			controlPanel.setBorder(BorderFactory.createCompoundBorder(raisedbevel,
					titledBorder));
			this.add(controlPanel);
		}
		
		{ //Initializing the panel for displaying reports
			reportPanel = new JPanel();
			titledBorder = BorderFactory.createTitledBorder(blackline, "Reports");
	//		titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(14.0f));
			reportPanel.setBounds(700, 200, 280, 440);
			reportPanel.setBackground(panelsBGColor);
			reportPanel.setBorder(BorderFactory.createCompoundBorder(raisedbevel, titledBorder));
			this.add(reportPanel);
		}

		initControlPanel(controlPanel);
		
		graphMouse = new DefaultModalGraphMouse<GraphNode, GraphEdge>();
	}

	public void initControlPanel(JPanel controlPanel) {
		controlPanel.setLayout(null);
		Border blackline = BorderFactory.createLineBorder(Color.green);
		

		{
			JLabel zoomLabel = new JLabel();
			TitledBorder titledBorder = BorderFactory.createTitledBorder(
					blackline, "Zoom");
			zoomLabel.setBorder(titledBorder);
			zoomLabel.setBounds(20, 30, 100, 50);

			controlPanel.add(zoomLabel);

			Button zoomIn = new Button("+");
			zoomIn.setBounds(20, 20, 20, 20);
			zoomLabel.add(zoomIn);
			zoomIn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					scaler.scale(vv, 1.1f, vv.getCenter());
				}
			});

			Button zoomOut = new Button("-");
			zoomOut.setBounds(60, 20, 20, 20);
			zoomLabel.add(zoomOut);
			zoomOut.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					scaler.scale(vv, 1/1.1f, vv.getCenter());
				}
			});

			Choice combo = new Choice();
			combo.add("Transforming");
			combo.add("Picking");
			combo.setEnabled(true);
			combo.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					if(arg0.getItem().equals("Transforming"))
					{
						graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
					}
					else
					{
						graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
					}
				}
			});

			combo.setBounds(150, 50, 100, 40);
			controlPanel.add(combo);
		}

		{
			JLabel hideLinksLabel = new JLabel();
			TitledBorder titledBorder = BorderFactory.createTitledBorder(
					blackline, "Hide links with weights");
			hideLinksLabel.setBorder(titledBorder);
			
			hideLinksLabel.setBounds(20, 90, 240, 60);
			controlPanel.add(hideLinksLabel);
			
			Choice combo = new Choice();
			combo.add("Less than");
			combo.add("Greater than");
			combo.setEnabled(true);
			
			combo.setBounds(20, 25, 100, 40);
			hideLinksLabel.add(combo);

			TextField thresholdTextBox = new TextField("0");
			thresholdTextBox.setBounds(135, 25, 30, 20);
			hideLinksLabel.add(thresholdTextBox);
			
			Button applyBtn = new Button("Apply");
			applyBtn.setBounds(180, 25, 40, 20);
			hideLinksLabel.add(applyBtn);
			
			
			applyBtn.addActionListener(new MyActionListener(thresholdTextBox, this, combo));
		}
		
		final Button prevBtn = new Button("Previous");
		prevBtn.setBounds(30, 165, 60, 20);
		controlPanel.add(prevBtn);		
		
		final Button nextBtn = new Button("Next");
		nextBtn.setBounds(98, 165, 60, 20);
		controlPanel.add(nextBtn);
		
		prevBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("index bwforw:" + index);
				index = (index - 1 + netgraphlist.size()) % netgraphlist.size();
				System.out.println("index after:" + index);
				netGraph = netgraphlist.get(index);
				//search in the open files for the largest network
				//if(netGraph.getNetwork().getRow() > minRow)
					//minRow = netGraph.getNetwork().getRow();
				//System.out.println(minRow);
				//end of new code
				loadGraph();
			}
			
		});
		
		
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				index = (index + 1+netgraphlist.size())%netgraphlist.size();
				netGraph = netgraphlist.get(index);
				//search in the open files for the tiniest network
				if(netGraph.getNetwork().getRow() < minRow)
					minRow = netGraph.getNetwork().getRow();
				System.out.println(minRow);
				//"search" is used to count the number of networks searched
				search = search + 1;
				//when all the networks have been searched, if the next one is bigger then the minimum then 
				//the color of the extra nodes is changed to Blue (changing its type to 1)
				if (search >= netgraphlist.size() && netGraph.getNetwork().getRow() > minRow)
					for(int i=minRow+1; i<= netGraph.getNetwork().getRow();i++)
						netGraph.findNode(i).type = 1;
				//end of new code
				loadGraph();
			}
		});
		
		Button resetBtn = new Button("Reset graph");
		resetBtn.setBounds(170, 165, 100, 20);
		controlPanel.add(resetBtn);
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				netGraph.buildNetwork();
				loadGraph();
			}
		});
	}
	
	//This method visualizes the graph
	public void loadGraph()
	{
//		CircleLayout<GraphNode, GraphEdge> layout = new CircleLayout<GraphNode, GraphEdge>(netGraph.getGraph());
		KKLayout<GraphNode, GraphEdge> layout = new KKLayout<GraphNode, GraphEdge>(netGraph.getGraph());

		// Sets the initial size of the space
		layout.setSize(new Dimension(400, 400));

		
		vv= new VisualizationViewer<GraphNode, GraphEdge>(layout);

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<GraphNode>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<GraphEdge>()
				{
			public String transform(GraphEdge edge)
			{
				return edge.getLabel(3);
			}
				});
		Transformer<GraphNode, Paint> vertexPaint = new Transformer<GraphNode, Paint>() {
			@Override
			public Paint transform(GraphNode gn) {
				if (gn.getType()== 1) 
					return Color.BLUE;
				else if (gn.getType() == 3)
					return gn.getColor();
				else
					return Color.RED;
					
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);


		JScrollPane jsp = new JScrollPane(vv, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsp.add(vv);
		vv.scaleToLayout(scaler);
		
		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());

		jsp.setViewportView(vv);
		jsp.setBounds(30, 30, 640, 600);
		jsp.setVisible(true);
		this.netPnl.setLayout(null);
		this.netPnl.add(jsp);
		this.revalidate();
		this.repaint();
	}
	
	// MINH ADD -------------------------------------------

	public class BarChart extends JPanel {
		 
		  private double[] values;
		  private String[] labels;
		  private Color[] colors;
		  private String title;
		 
		  public BarChart(double[] values, String[] labels, Color[] colors, String title) {
		    this.labels = labels;
		    this.values = values;
		    this.colors = colors;
		    this.title = title;
		  }
		 
		  public void paintComponent(Graphics g) {
		    super.paintComponent(g);
		    if (values == null || values.length == 0) {
		      return;
		    }
		 
		    double minValue = 0;
		    double maxValue = 0;
		    for (int i = 0; i < values.length; i++) {
		      if (minValue > values[i]) {
		        minValue = values[i];
		      }
		      if (maxValue < values[i]) {
		        maxValue = values[i];
		      }
		    }
		 
		    Dimension dim = getSize();
		    int panelWidth = dim.width;
		    int panelHeight = dim.height;
		    int barWidth = panelWidth / values.length;
		 
		    Font titleFont = new Font("Book Antiqua", Font.BOLD, 15);
		    FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);
		 
		    Font labelFont = new Font("Book Antiqua", Font.PLAIN, 14);
		    FontMetrics labelFontMetrics = g.getFontMetrics(labelFont);
		 
		    int titleWidth = titleFontMetrics.stringWidth(title);
		    int stringHeight = titleFontMetrics.getAscent();
		    int stringWidth = (panelWidth - titleWidth) / 2;
		    g.setFont(titleFont);
		    g.drawString(title, stringWidth, stringHeight);
		 
		    int top = titleFontMetrics.getHeight();
		    int bottom = labelFontMetrics.getHeight();
		    if (maxValue == minValue) {
		      return;
		    }
		    double scale = (panelHeight - top - bottom) / (maxValue - minValue);
		    stringHeight = panelHeight - labelFontMetrics.getDescent();
		    g.setFont(labelFont);
		    for (int j = 0; j < values.length; j++) {
		      int valueP = j * barWidth + 1;
		      int valueQ = top;
		      int height = (int) (values[j] * scale);
		      if (values[j] >= 0) {
		        valueQ += (int) ((maxValue - values[j]) * scale);
		      } else {
		        valueQ += (int) (maxValue * scale);
		        height = -height;
		      }
		 
		      g.setColor(colors[j]);
		      g.fillRect(valueP, valueQ, barWidth - 2, height);
		      g.setColor(Color.black);
		      g.drawRect(valueP, valueQ, barWidth - 2, height);
		 
		      int labelWidth = labelFontMetrics.stringWidth(labels[j]);
		      stringWidth = j * barWidth + (barWidth - labelWidth) / 2;
		      g.drawString(labels[j], stringWidth, stringHeight);
		    }
		  }
	}
	
	
	public void loadbarGraph()
	{
//		CircleLayout<GraphNode, GraphEdge> layout = new CircleLayout<GraphNode, GraphEdge>(netGraph.getGraph());
		//KKLayout<GraphNode, GraphEdge> layout = new KKLayout<GraphNode, GraphEdge>(netGraph.getGraph());

		// Sets the initial size of the space
		//layout.setSize(new Dimension(400, 400));

		
		//vv= new VisualizationViewer<GraphNode, GraphEdge>(layout);
		/*
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<GraphNode>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<GraphEdge>()
				{
			public String transform(GraphEdge edge)
			{
				return edge.getLabel(3);
			}
				});
		Transformer<GraphNode, Paint> vertexPaint = new Transformer<GraphNode, Paint>() {
			@Override
			public Paint transform(GraphNode gn) {
				if (gn.getType()== 1) 
					return Color.BLUE;
				else if (gn.getType() == 3)
					return gn.getColor();
				else
					return Color.GREEN;
					
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		*/
		
		String title = "My Title";
	    double[] values = new double[]{1,2,3,4,5};
	    String[] labels = new String[]{"A","B","C","D","E"};
	    Color[] colors = new Color[]{
	        Color.red,
	        Color.orange,
	        Color.yellow,
	        Color.green,
	        Color.blue
	    };
	    BarChart vv = new BarChart(values, labels, colors, title);

	    this.parentFrame.add(vv);
	    this.parentFrame.setVisible(true);
	    
	    //vv.add(bc);
		JScrollPane jsp = new JScrollPane(vv, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		//JScrollPane jsp = new JScrollPane(bc, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsp.add(vv);
		//jsp.add(bc);
		//vv.scaleToLayout(scaler);
		
		//vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());

		jsp.setViewportView(vv);
		//jsp.setViewportView(bc);
		jsp.setBounds(30, 30, 640, 600);
		jsp.setVisible(true);
		this.netPnl.setLayout(null);
		this.netPnl.add(jsp);
		this.revalidate();
		this.repaint();
	}
	
	// -------FINISH ADDING --------------------------------
	
	
	
	//This method applies link weight thresholds
	public void hideLinks(double threshold, int operator)
	{
		netGraph.hideLinks(threshold, operator);
	}
	//This method prints what kind of report is being displayed in the report panel
	private void printReportTitle(String measure)
	{
		JLabel title = new JLabel(measure);
		title.setBounds(2,2,200, 40);
		title.setSize(200, 40);
		title.setForeground(Color.darkGray);
		this.reportPanel.add(title);
	}
	
	//This method would call appropriate functions to calculate different centrality measures and displays the reports
	public void calculateMetric(MetricType metric)
	{
		if (this.netGraph != null) {
			ReportPanel reportPnl;
			MetricFinder mf;
			switch (metric) {
			case BETWEENNESS:
				this.resetComponents();
				printReportTitle("Betweenness");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.BETWEENNESS, mf, parentFrame);
				reportPnl.printMetricReport(mf.findBetweenness(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
				break;
			case CLOSENESS:
				this.resetComponents();
				printReportTitle("Closeness");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.CLOSENESS, mf, parentFrame);
				reportPnl.printMetricReport(mf.findCloseness(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
				break;
			case DEGREE:
				this.resetComponents();
				printReportTitle("Degree");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.DEGREE, mf, parentFrame);
				reportPnl.printMetricReport(mf.findDegree(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
				break;
			case EIGENVECTOR:
				this.resetComponents();
				printReportTitle("Eigenvector");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.EIGENVECTOR, mf, parentFrame);
				reportPnl.printMetricReport(mf.findEigenvector(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
				break;
			case AUTHORITY:
				this.resetComponents();
				printReportTitle("Authority");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.AUTHORITY, mf, parentFrame);
				reportPnl.printMetricReport(mf.findAuthority(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
				break;
			case HUB:
				this.resetComponents();
				printReportTitle("Hub");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.HUB, mf, parentFrame);
				reportPnl.printMetricReport(mf.findHub(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
				break;
			case CLUSTERING_COEF:
				this.resetComponents();
				printReportTitle("Clustering Coefficient");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.CLUSTERING_COEF, mf, parentFrame);
				reportPnl.printMetricReport(mf.findClusteringCoeff(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
				break;
			case RADUIS:
				this.resetComponents();
				printReportTitle("Clustering Coefficient");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.CLUSTERING_COEF, mf, parentFrame);
				reportPnl.printMetricReport(mf.findRadiusAndDiameter(this.netGraph.getNetwork()));
				this.reportPanel.add(reportPnl);
				break;
			case ORG:
				this.resetComponents();
				printReportTitle("ORG Measure");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.ORG, mf, parentFrame);
				reportPnl.printMetricReport(mf.findOrgMeasure(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
				break;
			}
		}
	}
	
	public void resetComponents()
	{
		Component[] components = this.reportPanel.getComponents();
		for(int i = 0; i < components.length; i++)
			this.reportPanel.remove(components[i]);
	}
	
	public void findStructurallyEquivalents()
	{
		this.resetComponents();
		printReportTitle("Structurally Equivalents");
		MetricFinder mf = new MetricFinder();
		ReportPanel reportPnl = new ReportPanel(MetricType.STRUCT_EQ, mf, parentFrame);
		reportPnl.printEquivalentsReport(mf.findStructurallyEQ(this.netGraph.getGraph()), netGraph.getGraph().getVertexCount());
		this.reportPanel.add(reportPnl);
	}
	
	public void calculateDensity()
	{
		this.resetComponents();
		printReportTitle("Graph Density");
		MetricFinder mf = new MetricFinder();
		ReportPanel reportPnl = new ReportPanel(MetricType.DENSITY, mf, parentFrame);
		reportPnl.printDensityReport(mf.findDensity(this.netGraph.getNetwork()));
		this.reportPanel.add(reportPnl);
	}
	//******************************MUNIMA*********************************//
		public void calculateLink()
		{
			if (this.netGraph != null) {
				ReportPanel reportPnl;
				MetricFinder mf;

				this.resetComponents();
				printReportTitle("Link Prediction - Jaccard");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.DEGREE, mf, parentFrame);
				reportPnl.printLinkMetricReport(mf.findLink(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
			}
		}
		///********************************MUNIMA END**************************//
		
		//////////////////////////DICE - Kashfia////////////////////////////////////
		public void calculateDiceLink()
		{
			if (this.netGraph != null) {
				ReportPanel reportPnl;
				MetricFinder mf;

				this.resetComponents();
				printReportTitle("Link Prediction - Dice");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.DEGREE, mf, parentFrame);
				reportPnl.printLinkMetricReport(mf.findDiceLink(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
			}
		}
		/////////////////////////END DICE//////////////////////////////////////////////
		
		//////////////////////////COMMON NEIGHBOR - Kashfia////////////////////////////////////
		public void calculateCommonLink()
		{
			if (this.netGraph != null) {
				ReportPanel reportPnl;
				MetricFinder mf;

				this.resetComponents();
				printReportTitle("Link Prediction - Common Neighbor");
				mf = new MetricFinder();
				reportPnl = new ReportPanel(MetricType.DEGREE, mf, parentFrame);
				reportPnl.printLinkMetricReport(mf.findCommonLink(this.netGraph.getGraph()));
				this.reportPanel.add(reportPnl);
			}
		}
	    /////////////////////////END COMMON NEIGHBOR//////////////////////////////////////////////
		
	/////////////////////////////////////Adamic Suchina////////////////////////////////////
	public void calculateAdamicLink()
	{
	if (this.netGraph != null) {
	ReportPanel reportPnl;
	MetricFinder mf;

	this.resetComponents();
	printReportTitle("Link Prediction - Adamic/Adar");
	mf = new MetricFinder();
	reportPnl = new ReportPanel(MetricType.DEGREE, mf, parentFrame);
	reportPnl.printLinkMetricReport(mf.findAdamicLink(this.netGraph.getGraph()));
	this.reportPanel.add(reportPnl);
	}
	}
	////////////////////////////////END Adamic//////////////////////////////////////////////
	public void cluster(int type)
	{
		switch(type)
		{
		case 1:
			new CommunityDetection().modularityCluster(this.netGraph.getGraph());
			break;
		case 2:
			new CommunityDetection().betweennessCluster(this.netGraph.getGraph());
			break;
		case 3:
			new CommunityDetection().voltageCluster(this.netGraph.getGraph());
			break;
		case 4:
			new CommunityDetection().mstCluster(this.netGraph.getGraph());
			break;
		case 5:
			new CommunityDetection().cpmCluster(this.netGraph.getGraph());
			break;
		}
		this.revalidate();
		this.repaint();
	}
	
	public void searchMetrics()
	{
		SearchMetricFrame smf = new SearchMetricFrame(parentFrame);
		smf.init();
		
	}
	public void showSearchResults(SearchMetricFrame smf)
	{
		this.resetComponents();
		printReportTitle("Search results");  //Show the metric type too!
		ReportPanel reportPnl = new ReportPanel(MetricType.DEGREE, new MetricFinder(), parentFrame);
		reportPnl.printMetricReport(smf.result);
		this.reportPanel.add(reportPnl);
		this.revalidate();
		this.repaint();
	}
	
	public void exportNetworkPanel() {
		// get some info from the graph image
		int width = vv.getSize().width;
		int height = vv.getSize().height;
		Color bg = getBackground();
		// allow the user to pick location and name of the file
		FileDialog fd = new FileDialog(parentFrame, "Save as", FileDialog.SAVE);
		fd.setFile(".png");
		fd.setEnabled(true);
		fd.setVisible(true);
		fd.setAlwaysOnTop(true);

		String path = fd.getDirectory();
		String file = path + "/" + fd.getFile();
		// create Graphics2D object
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_BGR);
		Graphics2D graphics = bi.createGraphics();
		graphics.setColor(bg);
		graphics.fillRect(0, 0, width, height);
		vv.paint(graphics);
		// try saving the image
		try {
			ImageIO.write(bi, "png", new File(file));
		} catch (Exception e) {
			e.printStackTrace();
			if (fd.getFile() == null) {
				JOptionPane.showMessageDialog(this, "Image was not saved!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}

		}

	}
}

class MyActionListener implements ActionListener
{
	TextField txtField;
	NetworkPanel networkPnl;
	Choice combo;
	MyActionListener(TextField txtField, NetworkPanel pnl, Choice combo)
	{
		this.txtField = txtField;
		this.networkPnl = pnl;
		this.combo = combo;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		try
		{
			int operator = combo.getSelectedItem() == "Less than" ? 0 : 1;
			networkPnl.hideLinks(Double.parseDouble(txtField.getText()), operator);
			networkPnl.loadGraph();
//			networkPnl.netPnl.revalidate();
//			networkPnl.netPnl.repaint();
			networkPnl.revalidate();
			networkPnl.repaint();
		}catch(NumberFormatException exp){
			int operator = 0;
			networkPnl.hideLinks(0, operator);
			txtField.setText("");
			networkPnl.repaint();
		}
	}
}
