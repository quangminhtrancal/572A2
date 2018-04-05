package GUI;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.Container;

import GUI.MainFrame.MetricType;
import analysis.MetricFinder;
import data.GraphNode;
import data.LinkMetrics;
import data.NodeMetric;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ReportPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JScrollPane scrollPane;
	JTable reportTable;
	MetricType metric;
	MainFrame mainFrame;
	MetricFinder metricFinder;

	public void repaint() {
		super.repaint();
	}

	ReportPanel(MetricType x, MetricFinder mf, MainFrame mFrame) {
		this.metric = x;
		this.metricFinder = mf;
		this.mainFrame = mFrame;
	}

	public void printMetricReport(NodeMetric[] results) {
		BoxLayout x = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.removeAll();
		this.setLayout(x);
		printTable(results);
		paintButtons();
	}

	// ***************************MUNIMA************************//
	public void printLinkMetricReport(LinkMetrics[] results) {
		BoxLayout x = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.removeAll();
		this.setLayout(x);
		printOurTable(results);
		paintButtons();
	}

	// ***************END MUNIMA***********************************//

	// This method creates and displays a table for the results
	public void printTable(NodeMetric[] results) {
		String[] headers = { "Node Name", "Score", "Remove Node" };
		Object[][] tableData = new Object[results.length][3];
		for (int j = 0; j < results.length; j++) {
			tableData[j][0] = results[j].getNodeName();
			tableData[j][1] = "" + round(results[j].getMetricValue(), 6);
			tableData[j][2] = false;
		}
		// me
		DefaultTableModel model = new DefaultTableModel(tableData, headers);
		reportTable = new JTable(model) {

			private static final long serialVersionUID = 1L;

			boolean[] canEdit = new boolean[] { false, false, true };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}

			/*
			 * @Override public Class getColumnClass(int column) { return
			 * getValueAt(0, column).getClass(); }
			 */
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				case 1:
					return String.class;
				case 2:
					return Boolean.class;
				default:
					return String.class;
				}
			}
		};
		// reportTable = new JTable(tableData, headers);
		reportTable.setLayout(null);
		// me
		reportTable.addMouseListener(new MouseAdapter() {

			public void mouseClicked(java.awt.event.MouseEvent e) {
				int row = reportTable.rowAtPoint(e.getPoint());
				int col = reportTable.columnAtPoint(e.getPoint());
				String rowID = reportTable.getValueAt(row, 0).toString();
				// If the true is selected from the checkbox
				if (reportTable.getValueAt(row, col).toString() == "true") {
					// ME: added reEvaluation on the specific calculated metric
					// + Test
					System.out.println(rowID);
					mainFrame.netPanel.netGraph.removeNode(rowID);
					if (mainFrame.typeSelected == GUI.MainFrame.MetricType.BETWEENNESS)
						mainFrame.netPanel
								.calculateMetric(MetricType.BETWEENNESS);
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.AUTHORITY)
						mainFrame.netPanel
								.calculateMetric(MetricType.AUTHORITY);
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.CLOSENESS)
						mainFrame.netPanel
								.calculateMetric(MetricType.CLOSENESS);
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.CLUSTERING_COEF)
						mainFrame.netPanel
								.calculateMetric(MetricType.CLUSTERING_COEF);
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.DEGREE)
						mainFrame.netPanel.calculateMetric(MetricType.DEGREE);
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.DENSITY)
						mainFrame.netPanel.calculateDensity();
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.EIGENVECTOR)
						mainFrame.netPanel
								.calculateMetric(MetricType.EIGENVECTOR);
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.HUB)
						mainFrame.netPanel.calculateMetric(MetricType.HUB);
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.RADUIS)
						mainFrame.netPanel.calculateMetric(MetricType.RADUIS);
					else if (mainFrame.typeSelected == GUI.MainFrame.MetricType.STRUCT_EQ)
						mainFrame.netPanel.findStructurallyEquivalents();
					mainFrame.netPanel.revalidate();
					mainFrame.netPanel.repaint();

				}
			}

		});
		scrollPane = new JScrollPane(reportTable);

		TableColumn col = reportTable.getColumnModel().getColumn(0);
		int width = 70;
		col.setPreferredWidth(width);
		col = reportTable.getColumnModel().getColumn(1);
		col.setPreferredWidth(width);

		reportTable.setEnabled(true);
		scrollPane.setBounds(5, 5, 250, 300);
		this.add(scrollPane);
		scrollPane.getViewport().setPreferredSize(new Dimension(250, 300));

	}

	// ///////////////////////////////// NEW - LINK
	// PREDICTION/////////////////////////////////////////////////
	public void printOurTable(LinkMetrics[] results) {
		String[] headers = { "Node Name1", "Node Name2", "Score" };
		Object[][] tableData = new Object[results.length][3];
		System.out.println(results.length);
		for (int j = 0; j < results.length; j++) {
			tableData[j][0] = results[j].getNode1();
			tableData[j][1] = results[j].getNode2();
			tableData[j][2] = "" + round(results[j].getMetrics(), 6);
		}
		// me
		DefaultTableModel model = new DefaultTableModel(tableData, headers);
		reportTable = new JTable(model) {

			private static final long serialVersionUID = 1L;

			boolean[] canEdit = new boolean[] { false, false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}

			/*
			 * @Override public Class getColumnClass(int column) { return
			 * getValueAt(0, column).getClass(); }
			 */
			@Override
			public Class getColumnClass(int column) {
				switch (column) {
				case 0:
					return String.class;
				case 1:
					return String.class;
				case 2:
					return String.class;
				default:
					return String.class;
				}
			}
		};
		// reportTable = new JTable(tableData, headers);
		reportTable.setLayout(null);
		// me
		scrollPane = new JScrollPane(reportTable);

		TableColumn col = reportTable.getColumnModel().getColumn(0);
		int width = 70;
		col.setPreferredWidth(width);
		col = reportTable.getColumnModel().getColumn(1);
		col.setPreferredWidth(width);

		reportTable.setEnabled(true);
		scrollPane.setBounds(5, 5, 250, 300);
		this.add(scrollPane);
		scrollPane.getViewport().setPreferredSize(new Dimension(250, 300));

	}

	// /////////////////////////////////// END
	// LINK//////////////////////////////////
	// This method rounds the number to 6 number of digits after the decimal
	// point
	private double round(double num, int precision) {
		double y = Math.pow(10, precision);
		num = num * y;
		int x = (int) num;
		return x / y;
	}
	// -------------------MINH ADD-----------------------
	public class BarChart extends JPanel {
		 
		  private double[] values;
		  private String[] labels;
		  private Color colors;
		  private String title;
		 
		  public BarChart(double[] values, String[] labels, Color colors, String title) {
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
		 
		      g.setColor(colors);
		      g.fillRect(valueP, valueQ, barWidth - 2, height);
		      g.setColor(Color.black);
		      g.drawRect(valueP, valueQ, barWidth - 2, height);
		 
		      int labelWidth = labelFontMetrics.stringWidth(labels[j]);
		      stringWidth = j * barWidth + (barWidth - labelWidth) / 2;
		      g.drawString(labels[j], stringWidth, stringHeight);
		    }
		  }
	}
	
	private void paintButtons() {
		// -------------------MINH ADD-----------------------
		Button bargraphBtn = new Button("Bar chart");
		bargraphBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		this.add(bargraphBtn);
		
		bargraphBtn.addActionListener(new ActionListener() {
			@SuppressWarnings("null")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int num_node=reportTable.getModel().getRowCount();
				String[] nodename = new String[num_node];
				double[] nodevalue= new double[num_node];
				for (int i = 0; i < num_node; i++) {
					nodename[i] = (String) reportTable.getModel().getValueAt(i, 0);

					nodevalue[i]= Double.parseDouble((String) reportTable.getModel().getValueAt(i, 1));
					//JOptionPane.showMessageDialog(null,nodename[i],Double.toString (nodevalue[i]),JOptionPane.INFORMATION_MESSAGE );
				}
				//NetworkPanel netPanel;
				//netPanel.loadbarGraph();
				JPanel netPnl = new JPanel();
				netPnl.setSize(700, 640);
				Color panelsBGColor = new Color(210, 210, 210);
				Border blackline = BorderFactory.createLineBorder(Color.green);
				Border raisedbevel = BorderFactory.createRaisedBevelBorder();
				TitledBorder titledBorder = BorderFactory.createTitledBorder(blackline, "Bar graph");
			//	titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(14.0f));
				netPnl.setBorder(BorderFactory.createCompoundBorder(raisedbevel, titledBorder));
				netPnl.setBackground(panelsBGColor);
				
				JFrame.setDefaultLookAndFeelDecorated(true);
				 
			    JFrame frame = new JFrame("Bar Chart Example");
			    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    frame.setSize(350, 300);
			 
			     BarChart bc = new BarChart(nodevalue, nodename, Color.green, "BAR CHART");
			 
			    frame.add(bc);
			    frame.setVisible(true);

			    ReportPanel.this.mainFrame.netPanel.add(netPnl);
			    ReportPanel.this.mainFrame.netPanel.loadbarGraph();
			}
		});
		
		//--------------FINISH ADDING ---------------------
		
		Button visualizeBtn = new Button("Visualize results");
		visualizeBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		this.add(visualizeBtn);
		visualizeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (ReportPanel.this.metric == MetricType.STRUCT_EQ)
					ReportPanel.this.mainFrame.netPanel.netGraph
							.groupNodes(metricFinder.groups);
				
				else
					ReportPanel.this.mainFrame.netPanel.netGraph
							.rankMetric(metricFinder.ranks);
				ReportPanel.this.mainFrame.netPanel.netPnl.revalidate();
				ReportPanel.this.mainFrame.netPanel.netPnl.repaint();
			}
		});

		Button saveBtn = new Button("Save report");
		saveBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		this.add(saveBtn);
		saveBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ReportPanel.this.saveResults();
			}
		});
	}

	public void printEquivalentsReport(Map<String, GraphNode[]> results,
			int vertexCount) {
		BoxLayout x = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(x);

		String[] headers = { "Group Number", "Node Names" };
		String[][] tableData = new String[vertexCount + 2 * results.size()][2];

		int i = 0;
		for (String groupNum : results.keySet()) {
			tableData[i][0] = groupNum + ":";
			tableData[i++][1] = "";
			for (int j = 0; j < results.get(groupNum).length; j++) {
				tableData[i][0] = "";
				tableData[i++][1] = results.get(groupNum)[j].getLabel();
			}
			tableData[i][0] = "";
			tableData[i++][1] = "";
		}

		reportTable = new JTable(tableData, headers);
		reportTable.setLayout(null);

		scrollPane = new JScrollPane(reportTable);

		TableColumn col = reportTable.getColumnModel().getColumn(0);
		col.setPreferredWidth(60);
		col = reportTable.getColumnModel().getColumn(1);
		col.setPreferredWidth(110);

		reportTable.setEnabled(false);
		scrollPane.setBounds(5, 5, 220, 300);
		this.add(scrollPane);
		scrollPane.getViewport().setPreferredSize(new Dimension(220, 300));

		paintButtons();
	}

	public void printDensityReport(double density) {
		BoxLayout x = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(x);

		String[] headers = { "Metric", "Value" };
		String[][] tableData = new String[1][2];

		tableData[0][0] = "Graph Density";
		tableData[0][1] = round(density, 8) + "";

		reportTable = new JTable(tableData, headers);
		reportTable.setLayout(null);

		scrollPane = new JScrollPane(reportTable);

		TableColumn col = reportTable.getColumnModel().getColumn(0);
		col.setPreferredWidth(60);
		col = reportTable.getColumnModel().getColumn(1);
		col.setPreferredWidth(110);

		reportTable.setEnabled(false);
		scrollPane.setBounds(5, 5, 220, 300);
		this.add(scrollPane);
		scrollPane.getViewport().setPreferredSize(new Dimension(220, 300));

		paintButtons();
	}

	public void saveResults() {
		JFrame frame = new JFrame("Save as");
		FileDialog fd = new FileDialog(frame, "Save as", FileDialog.SAVE);
		fd.setEnabled(true);
		fd.setVisible(true);
		fd.setAlwaysOnTop(true);

		String path = fd.getDirectory();
		if (fd.getDirectory() == null || fd.getFile() == null)
			return;
		path = fd.getDirectory() + "/" + fd.getFile();
		if (!path.endsWith(".csv"))
			path += ".csv";

		String line = this.metric + ", \n \n";
		line += reportTable.getModel().getColumnName(0) + ", "
				+ reportTable.getModel().getColumnName(1) + "\n";
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(path));
			fw.append(line);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < reportTable.getModel().getRowCount(); i++) {
			line = (String) reportTable.getModel().getValueAt(i, 0);
			line += ", ";
			line += (String) reportTable.getModel().getValueAt(i, 1);
			line += ", \n";
			JOptionPane.showMessageDialog(null,(String) reportTable.getModel().getValueAt(i, 0),(String) reportTable.getModel().getValueAt(i, 1),JOptionPane.INFORMATION_MESSAGE );

			try {
				fw.append(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
