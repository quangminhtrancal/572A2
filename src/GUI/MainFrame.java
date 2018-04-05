package GUI;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import analysis.CliqueFinder;
import analysis.KruskalMST;
import analysis.NetworkConstructor;
import data.Dataset;
import data.GraphEdge;
import data.GraphNode;
import data.Network;
import data.NetworkGraph;
import data.Parser;
import edu.uci.ics.jung.graph.UndirectedGraph;

// This class is the main window of the program. It extends JFrame. There's always one instance of this class available while the program is running.
public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	NetworkPanel netPanel;
	Dataset rawData;
	StatusBar statusBar;
	ArrayList<Menu> menus = new ArrayList<Menu>();
	// ME: SALIM
	MetricType typeSelected;

	// Shermin: Added two new metrictypes: authority and hub
	public enum MetricType {
		BETWEENNESS, CLOSENESS, DEGREE, EIGENVECTOR, CLUSTERING_COEF, STRUCT_EQ, DENSITY, AUTHORITY, HUB, RADUIS, ORG
	}

	public static void main(String[] args) {
		new MainFrame();
	}

	public void setNetPanel(NetworkPanel net) {
		this.netPanel = net;
	}

	MainFrame() {
		this.setTitle("NetDriller");
		this.setSize(990, 710);
		this.setLocation(100, 100);
		this.setBounds(100, 100, 990, 710);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		this.setLayout(null);
		WelcomePanel wp = new WelcomePanel();
		wp.setBounds(300, 100, 500, 400);
		this.setResizable(false);
		this.add(wp);

		initMenues();
		toggleNetworkMenus();
		toggleConstructionMenus();
		statusBar = new StatusBar(this);
		statusBar
				.setText("No network imported                No raw dataset imported");
		statusBar.revalidate();
		statusBar.repaint();
		this.repaint();
	}

	public void reset() {
		Component[] cmpn = this.getContentPane().getComponents();
		for (int i = 0; i < cmpn.length; i++)
			this.remove(cmpn[i]);

		WelcomePanel wp = new WelcomePanel();
		wp.setBounds(300, 100, 500, 400);
		this.setResizable(false);
		this.add(wp);
		wp.revalidate();
		wp.repaint();
		this.repaint();

		initMenues();
		statusBar = new StatusBar(this);
		statusBar
				.setText("No network imported                No raw dataset imported");
		statusBar.revalidate();
		statusBar.repaint();
		this.repaint();
	}

	public void toggleNetworkMenus() {
		for (int i = 0; i < menus.size(); i++)
			if (!menus.get(i).getLabel().equals("Network Construction")
					&& !menus.get(i).getLabel().equals("Data"))
				menus.get(i).setEnabled(!menus.get(i).isEnabled());
	}

	public void enableNetworkMenus() {
		for (int i = 0; i < menus.size(); i++)
			if (!menus.get(i).getLabel().equals("Network Construction")
					&& !menus.get(i).getLabel().equals("Data")
					&& menus.get(i).getLabel().equals("Hierarchical Zooming"))
				menus.get(i).setEnabled(true);
	}

	public void disableNetworkMenus() {
		for (int i = 0; i < menus.size(); i++)
			if (!menus.get(i).getLabel().equals("Network Construction")
					&& !menus.get(i).getLabel().equals("Data"))
				menus.get(i).setEnabled(false);
	}

	public void toggleConstructionMenus() {
		for (int i = 0; i < menus.size(); i++)
			if (menus.get(i).getLabel().equals("Network Construction"))
				menus.get(i).setEnabled(!menus.get(i).isEnabled());
	}

	public void enableConstructionMenus() {
		for (int i = 0; i < menus.size(); i++)
			if (menus.get(i).getLabel().equals("Network Construction"))
				menus.get(i).setEnabled(true);
	}

	public void disableConstructionMenus() {
		for (int i = 0; i < menus.size(); i++)
			if (menus.get(i).getLabel().equals("Network Construction"))
				menus.get(i).setEnabled(false);
	}

	// This method initializes the main menus
	public void initMenues() {
		MenuBar menuBar = new MenuBar();
		this.setMenuBar(menuBar);

		{
			Menu dataMenu = new Menu("Data");
			menuBar.add(dataMenu);
			initDataMenu(dataMenu);
			menus.add(dataMenu);
		}

		{
			Menu constructionMenu = new Menu("Network Construction");
			menuBar.add(constructionMenu);
			initConstructionMenu(constructionMenu);
			menus.add(constructionMenu);
		}

		{
			Menu metricMenu = new Menu("Metrics");
			menuBar.add(metricMenu);
			initMetricMenur(metricMenu);
			menus.add(metricMenu);
		}

		{
			Menu analysisMenu = new Menu("Analysis");
			menuBar.add(analysisMenu);
			initAnalysisMenu(analysisMenu);
			menus.add(analysisMenu);
		}

		{
			Menu clusteringMenu = new Menu("Network Clustering");
			menuBar.add(clusteringMenu);
			initClusteringMenu(clusteringMenu);
			menus.add(clusteringMenu);
		}
		{
			Menu linkpMenu = new Menu("Link Prediction");
			menuBar.add(linkpMenu);
			initLinkPMenu(linkpMenu);
			menus.add(linkpMenu);
		}
		{
			Menu hierarchicalZoomingMenu = new Menu("Hierarchical Zooming");
			menuBar.add(hierarchicalZoomingMenu);
			initHierarchicalZoomingMenu(hierarchicalZoomingMenu);
			menus.add(hierarchicalZoomingMenu);
		}

		{
			Menu searchMenu = new Menu("Search");
			menuBar.add(searchMenu);
			initSearchMenu(searchMenu);
			menus.add(searchMenu);
		}

		// {
		// Menu dynamicMenu = new Menu("Dynamic Network");
		// menuBar.add(dynamicMenu);
		// }
	}

	// This method initializes the items of "Data" menu and sets their
	// actionListeners.
	public void initDataMenu(Menu dataMenu) {
		{
			MenuItem importItem = new MenuItem("Import A Network         ");
			dataMenu.add(importItem);
			importItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					boolean firstNetwork = true;
					JFrame jf = new JFrame("Import a Network");
					jf.setBounds(200, 200, 370, 350);
					jf.setAlwaysOnTop(true);
					if (netPanel != null && netPanel.netGraph != null)
						firstNetwork = false;
					netPanel = new NetworkPanel(MainFrame.this);
					NetworkImportPanel dip = new NetworkImportPanel(netPanel,
							jf, firstNetwork);
					jf.setVisible(true);
					jf.setSize(370, 350);
					//jf.setSize(550, 550);
					jf.add(dip);

				}
			});
		}
		// {
		// MenuItem timestampItem = new MenuItem("Import Timestamps");
		// dataMenu.add(timestampItem);
		// }

		{
			MenuItem rawDataItem = new MenuItem("Import Raw Dataset");
			dataMenu.add(rawDataItem);
			rawDataItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					new DataImportFrame(MainFrame.this).init();

				}
			});
		}
		{
			MenuItem prepareInputDataItem = new MenuItem("Parse Twitter Data");
			dataMenu.add(prepareInputDataItem);
			prepareInputDataItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean firstNetwork = true;
					JFrame jf = new JFrame("Import Data File");
					jf.setBounds(200, 200, 500, 550);
					jf.setAlwaysOnTop(true);
					if (netPanel != null && netPanel.netGraph != null)
						firstNetwork = false;
					netPanel = new NetworkPanel(MainFrame.this);
					InputDataGeneratorPanel dip = new InputDataGeneratorPanel(netPanel, jf, firstNetwork);
					jf.setVisible(true);
					jf.setSize(500, 550);
					jf.add(dip);
				}
			});
		}
		{
			Menu importAuthorItem = new Menu("Parse Papers Data");
			dataMenu.add(importAuthorItem);
			MenuItem importCoAuthor = new MenuItem("Generate Co-Author Network");
			MenuItem importTitleKeywords = new MenuItem(
					"Generate Paper-Keyword Network");
			MenuItem importAuthorKeyword = new MenuItem(
					"Generate Author-Keyword Network");
			importAuthorItem.add(importCoAuthor);
			importAuthorItem.add(importTitleKeywords);
			importAuthorItem.add(importAuthorKeyword);

			importCoAuthor.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					FileDialog fd = new FileDialog(MainFrame.this,"Open a Network File",
							FileDialog.LOAD);
					fd.setFile("*.json");
					fd.setEnabled(true);
					fd.setVisible(true);
					fd.setAlwaysOnTop(true);

					String path = fd.getDirectory() + fd.getFile();
					if (fd.getDirectory() != null && fd.getFile() != null) {
						Parser p=new Parser();
						p.parse(path, 1);
						Network net = new Network(1);
						net.loadNetwork(true, "./output.csv", true); 
						netPanel = new NetworkPanel(MainFrame.this);
						netPanel.netGraph = new NetworkGraph(net);
						netPanel.loadGraph();
						for (int i = 0; i < menus.size(); i++)
								menus.get(i).setEnabled(true);
					}

				}
			});
			importTitleKeywords.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					FileDialog fd = new FileDialog(MainFrame.this,"Open a Network File",
							FileDialog.LOAD);
					fd.setFile("*.json");
					fd.setEnabled(true);
					fd.setVisible(true);
					fd.setAlwaysOnTop(true);

					String path = fd.getDirectory() + fd.getFile();
					if (fd.getDirectory() != null && fd.getFile() != null) {
						Parser p=new Parser();
						p.parse(path, 2);
						Network net = new Network(2);
						net.loadNetwork(true, "./output.csv", true); 

						netPanel = new NetworkPanel(MainFrame.this);
						netPanel.netGraph = new NetworkGraph(net);
						netPanel.loadGraph();
						for (int i = 0; i < menus.size(); i++)
							menus.get(i).setEnabled(true);
					}

				}
			});
			importAuthorKeyword.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					FileDialog fd = new FileDialog(MainFrame.this,"Open a Network File",
							FileDialog.LOAD);
					fd.setFile("*.json");
					fd.setEnabled(true);
					fd.setVisible(true);
					fd.setAlwaysOnTop(true);

					String path = fd.getDirectory() + fd.getFile();
					if (fd.getDirectory() != null && fd.getFile() != null) {
						Parser p=new Parser();
						p.parse(path, 3);
						Network net = new Network(2);
						net.loadNetwork(true, "./output.csv", true); 

						netPanel = new NetworkPanel(MainFrame.this);
						netPanel.netGraph = new NetworkGraph(net);
						netPanel.loadGraph();
						for (int i = 0; i < menus.size(); i++)
							menus.get(i).setEnabled(true);
					}

				}
			});

		}
		{
			MenuItem saveItem = new MenuItem("Save Network");
			dataMenu.add(saveItem);
			saveItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (netPanel == null
							|| MainFrame.this.netPanel.netGraph == null) {
						JOptionPane.showMessageDialog(null,
								"The network is not yet defined!", "Alert",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					MainFrame.this.netPanel.netGraph.saveNetwork();
				}
			});
		}
		{
			MenuItem exportImage = new MenuItem("Export Network Image   ");
			dataMenu.add(exportImage);
			exportImage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (netPanel == null
							|| MainFrame.this.netPanel.netGraph == null) {
						JOptionPane.showMessageDialog(null,
								"The network is not yet defined!", "Alert",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					MainFrame.this.netPanel.exportNetworkPanel();

				}
			});
		}

		{
			MenuItem showMatrixItem = new MenuItem("Edit Network Matrix");
			dataMenu.add(showMatrixItem);
			showMatrixItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (MainFrame.this.netPanel != null
							&& MainFrame.this.netPanel.netGraph != null)
						new EditMatrixFrame(MainFrame.this.netPanel.netGraph)
								.showMatrix();
					else
						JOptionPane.showMessageDialog(null,
								"The network is not yet defined!", "Alert",
								JOptionPane.WARNING_MESSAGE);
				}
			});
		}

		// CPSC672 Incremental Network Construction
		{
			MenuItem showMatrixItem = new MenuItem("Modify Network Matrix");
			dataMenu.add(showMatrixItem);
			showMatrixItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fcr = new JFileChooser();
					JFileChooser fcn = new JFileChooser();
					fcr.setDialogTitle("Choose Rule File");
					fcn.setDialogTitle("Choose File to Modify");
					File file = null;
					BufferedReader br = null;
					FileReader fr = null;
					ArrayList<String> rules = new ArrayList();
					ArrayList<String> network = new ArrayList();
					String curString = "";
					int returnVal = 0;

					// Ask user to choose a rule file
					returnVal = fcr.showOpenDialog(MainFrame.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						file = fcr.getSelectedFile();
						try {
							fr = new FileReader(file);
							br = new BufferedReader(fr);

							while ((curString = br.readLine()) != null) {
								rules.add(curString);
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}

					} else {
						JOptionPane.showMessageDialog(null, "File Invalid",
								"Alert", JOptionPane.WARNING_MESSAGE);
					}

					// Ask user to choose a file to modify
					returnVal = fcn.showOpenDialog(MainFrame.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						file = fcn.getSelectedFile();
						try {
							fr = new FileReader(file);
							br = new BufferedReader(fr);

							while ((curString = br.readLine()) != null) {
								network.add(curString);
							}

							// Go through each rule in rule file
							for (int i = 0; i < rules.size(); i++) {
								String[] str = rules.get(i).split(",");

								// Remove node
								if (str.length == 2 && str[1].equals("0")) {
									int removedNode = Integer
											.parseInt(Character.toString(str[0]
													.charAt(0)));

									for (int j = 0; j < network.size(); j++) {
										String[] strN = network.get(j).split(
												",");
										strN[removedNode - 1] = ""; // empty the
																	// element
										String temp = "";
										for (int k = 0; k < strN.length; k++) {
											if (strN[k].equals(""))
												continue;

											if (k < strN.length - 2)
												temp = temp + strN[k] + ",";

											else if (k == strN.length - 2
													&& strN[strN.length - 1]
															.equals(""))
												temp = temp + strN[k];

											else if (k == strN.length - 2
													&& !strN[strN.length - 1]
															.equals(""))
												temp = temp + strN[k] + ",";

											else if (k == strN.length - 1)
												temp = temp + strN[k];

										}
										network.set(j, temp);
									}
									try{
									network.remove(removedNode - 1);
									}
									catch(Exception c){
										JOptionPane.showMessageDialog(null, "Couldn't modify the network! Can't remove node "+removedNode,
												"Alert", JOptionPane.WARNING_MESSAGE);
										return;
									}
								}
								// Remove link
								else if (str.length == 3 && str[2].equals("-1")) {
									int removedNode_1 = Integer
											.parseInt(Character.toString(str[0]
													.charAt(0)));
									int removedNode_2 = Integer
											.parseInt(Character.toString(str[1]
													.charAt(0)));

									String[] str_1 = network.get(
											removedNode_1 - 1).split(",");
									String[] str_2 = network.get(
											removedNode_2 - 1).split(",");

									// Set numbers to 0
									str_1[removedNode_2 - 1] = "0";
									str_2[removedNode_1 - 1] = "0";

									// re-form strings
									String temp = "";
									for (int k = 0; k < str_1.length; k++) {
										if (k == str_1.length - 1)
											temp = temp + str_1[k];
										else
											temp = temp + str_1[k] + ",";
									}
									network.set(removedNode_1 - 1, temp);
									temp = "";

									for (int k = 0; k < str_2.length; k++) {
										if (k == str_2.length - 1)
											temp = temp + str_2[k];
										else
											temp = temp + str_2[k] + ",";
									}
									try{
									network.set(removedNode_2 - 1, temp);
									}
									catch(Exception c){
										JOptionPane.showMessageDialog(null, "Couldn't modify the network! Can't remove link from node "+removedNode_2,
												"Alert", JOptionPane.WARNING_MESSAGE);
										return;
									}
								}
								// Add link
								else if (str.length == 3) {
									int addNode_1 = Integer.parseInt(Character
											.toString(str[0].charAt(0)));
									int addNode_2 = Integer.parseInt(Character
											.toString(str[1].charAt(0)));
									String weight = str[2];

									// Add link between existing nodes
									if (addNode_1 <= network.size()
											&& addNode_2 <= network.size()) {
										String[] str_1 = network.get(
												addNode_1 - 1).split(",");
										String[] str_2 = network.get(
												addNode_2 - 1).split(",");

										// Set numbers to weight
										str_1[addNode_2 - 1] = weight;
										str_2[addNode_1 - 1] = weight;

										// re-form strings
										String temp = "";
										for (int k = 0; k < str_1.length; k++) {
											if (k == str_1.length - 1)
												temp = temp + str_1[k];
											else
												temp = temp + str_1[k] + ",";
										}
										network.set(addNode_1 - 1, temp);
										temp = "";

										for (int k = 0; k < str_2.length; k++) {
											if (k == str_2.length - 1)
												temp = temp + str_2[k];
											else
												temp = temp + str_2[k] + ",";
										}
										network.set(addNode_2 - 1, temp);
									}

									// Add link between existing and new node
									if (addNode_1 <= network.size()
											&& addNode_2 == network.size() + 1) {
										int existingNode = addNode_1;

										for (int j = 0; j < network.size(); j++) {
											if (j == existingNode - 1)
												network.set(j, network.get(j)
														+ "," + weight);
											else
												network.set(j, network.get(j)
														+ ",0");
										}
										String[] newRow = new String[network
												.size() + 1];
										for (int k = 0; k < newRow.length; k++) {
											if (k == existingNode - 1)
												newRow[k] = weight;
											else
												newRow[k] = "0";
										}
										// re-form strings
										String temp = "";
										for (int l = 0; l < newRow.length; l++) {
											if (l == newRow.length - 1)
												temp = temp + newRow[l];
											else
												temp = temp + newRow[l] + ",";
										}
										try{
										network.add(temp);
										}
										catch(Exception c){
											JOptionPane.showMessageDialog(null, "Couldn't modify the network! Can't add a link ",
													"Alert", JOptionPane.WARNING_MESSAGE);
											return;
										}
									}
								}
							}
							// Write results to the file user wants to modify
							FileWriter fstream = new FileWriter(file, false);
							BufferedWriter out = new BufferedWriter(fstream);
							for (int i = 0; i < network.size(); i++) {
								out.write(network.get(i));
								out.newLine();
							}
							out.flush();
							out.close();
							fstream.close();
						}

						catch (IOException e1) {
							e1.printStackTrace();
						}

					} else {
						JOptionPane.showMessageDialog(null, "File Invalid",
								"Alert", JOptionPane.WARNING_MESSAGE);
					}
				}
			});
		}

		{
			// dataMenu.add(new JSeparator());
			dataMenu.addSeparator();
			MenuItem exitItem = new MenuItem("Exit");
			dataMenu.add(exitItem);
			exitItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					MainFrame.this.dispose();
					new StatusBar(MainFrame.this)
							.setText("Raw dataset imported");
				}
			});
		}

	}

	public void initConstructionMenu(Menu constructMenu) {
		{
			Menu frequentItemsets = new Menu("Based On Frequent Item Set  ");
			constructMenu.add(frequentItemsets);
			{
				MenuItem frequentItem = new MenuItem(
						"Network of frequent links");
				frequentItemsets.add(frequentItem);
				frequentItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (rawData != null) {
							netPanel = new NetworkPanel(MainFrame.this);
							netPanel.netGraph = new NetworkConstructor()
									.frequentMiner(rawData);
							netPanel.loadGraph();
							netPanel.revalidate();
							netPanel.repaint();
							enableNetworkMenus();
						}
					}
				});
			}
			{
				MenuItem closedFrequentItem = new MenuItem(
						"Network of maximal frequent links");
				frequentItemsets.add(closedFrequentItem);
				closedFrequentItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (rawData != null) {
							netPanel = new NetworkPanel(MainFrame.this);
							netPanel.netGraph = new NetworkConstructor()
									.frequentMaximalMiner(rawData);
							netPanel.loadGraph();
							netPanel.revalidate();
							netPanel.repaint();
							enableNetworkMenus();
						}
					}
				});
			}
			{
				MenuItem maximalFrequentItem = new MenuItem(
						"Network of closed frequent links");
				frequentItemsets.add(maximalFrequentItem);
				frequentItemsets.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (rawData != null) {
							netPanel = new NetworkPanel(MainFrame.this);
							netPanel.netGraph = new NetworkConstructor()
									.frequentClosedMiner(rawData);
							netPanel.loadGraph();
							netPanel.revalidate();
							netPanel.repaint();
							enableNetworkMenus();
						}
					}
				});
			}
		}
		{
			MenuItem kmeansItem = new MenuItem("Based On K-means Clustering");
			constructMenu.add(kmeansItem);
			kmeansItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (rawData != null) {
						netPanel = new NetworkPanel(MainFrame.this);
						netPanel.netGraph = new NetworkConstructor()
								.kmeansClustering(rawData);
						netPanel.loadGraph();
						netPanel.revalidate();
						netPanel.repaint();
						// toggleNetworkMenus();
						enableNetworkMenus();
					}
				}
			});
		}
		{
			/*
			 * MenuItem kmeansItem = new
			 * MenuItem("Based On GA K-means Clustering");
			 * constructMenu.add(kmeansItem); kmeansItem.addActionListener(new
			 * ActionListener() {
			 * 
			 * @Override public void actionPerformed(ActionEvent arg0) { if
			 * (rawData != null) { // long time = System.currentTimeMillis();
			 * netPanel = new NetworkPanel(MainFrame.this); netPanel.netGraph =
			 * new NetworkConstructor().GAClustering(rawData);
			 * netPanel.loadGraph(); netPanel.revalidate(); netPanel.repaint();
			 * toggleNetworkMenus(); //
			 * System.out.println(".................... " + //
			 * (System.currentTimeMillis() - time)); } } });
			 */
		}
	}

	// This method initializes the items of "Metric" menu and sets their
	// actionListeners.
	public void initMetricMenur(Menu metricMenu) {
		{
			Menu centralityMeasuresItem = new Menu("Centrality Measures    ");
			metricMenu.add(centralityMeasuresItem);

			{
				MenuItem betweennessItem = new MenuItem("Betweenness");
				centralityMeasuresItem.add(betweennessItem);
				betweennessItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						typeSelected = MetricType.BETWEENNESS;
						// MainFrame.this.netPanel.reportPanel = new JPanel();
						MainFrame.this.netPanel
								.calculateMetric(MetricType.BETWEENNESS);
						MainFrame.this.netPanel.revalidate();
						MainFrame.this.netPanel.repaint();
					}
				});
			}

			{
				MenuItem closenessItem = new MenuItem("Closeness");
				centralityMeasuresItem.add(closenessItem);
				closenessItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						// MainFrame.this.netPanel.reportPanel = new JPanel();

						typeSelected = MetricType.CLOSENESS;
						MainFrame.this.netPanel
								.calculateMetric(MetricType.CLOSENESS);
						MainFrame.this.netPanel.revalidate();
						MainFrame.this.netPanel.repaint();
					}
				});
			}

			{
				MenuItem degreeItem = new MenuItem("Degree");
				centralityMeasuresItem.add(degreeItem);
				degreeItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						typeSelected = MetricType.DEGREE;
						MainFrame.this.netPanel
								.calculateMetric(MetricType.DEGREE);
						MainFrame.this.netPanel.revalidate();
						MainFrame.this.netPanel.repaint();
					}
				});
			}

			{
				MenuItem eigenvectorItem = new MenuItem("Eigenvector");
				centralityMeasuresItem.add(eigenvectorItem);
				eigenvectorItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						typeSelected = MetricType.EIGENVECTOR;
						MainFrame.this.netPanel
								.calculateMetric(MetricType.EIGENVECTOR);
						MainFrame.this.netPanel.revalidate();
						MainFrame.this.netPanel.repaint();
					}
				});
			}
			{
				MenuItem authorityItem = new MenuItem("Authority");
				centralityMeasuresItem.add(authorityItem);
				authorityItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						typeSelected = MetricType.AUTHORITY;
						MainFrame.this.netPanel
								.calculateMetric(MetricType.AUTHORITY);
						MainFrame.this.netPanel.revalidate();
						MainFrame.this.netPanel.repaint();
					}
				});
			}
			{
				MenuItem authorityItem = new MenuItem("Hub");
				centralityMeasuresItem.add(authorityItem);
				authorityItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						typeSelected = MetricType.HUB;
						MainFrame.this.netPanel.calculateMetric(MetricType.HUB);
						MainFrame.this.netPanel.revalidate();
						MainFrame.this.netPanel.repaint();
					}
				});
			}

		}

		{
			MenuItem authorityItem = new MenuItem("Org Measure");
			metricMenu.add(authorityItem);
			authorityItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					typeSelected = MetricType.ORG;
					MainFrame.this.netPanel.calculateMetric(MetricType.ORG);
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
		}
		{
			MenuItem clusteringItem = new MenuItem("Clustering Coefficient");
			metricMenu.add(clusteringItem);
			clusteringItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					typeSelected = MetricType.CLUSTERING_COEF;
					MainFrame.this.netPanel
							.calculateMetric(MetricType.CLUSTERING_COEF);
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
		}

		{
			MenuItem equivalentItem = new MenuItem("Structurally Equivalent");
			metricMenu.add(equivalentItem);
			equivalentItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					typeSelected = MetricType.STRUCT_EQ;
					MainFrame.this.netPanel.findStructurallyEquivalents();
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
		}

		{
			MenuItem densityItem = new MenuItem("Graph Density");
			metricMenu.add(densityItem);
			densityItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					typeSelected = MetricType.DENSITY;
					MainFrame.this.netPanel.calculateDensity();
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
		}

		{
			MenuItem radiusDiaItem = new MenuItem("Graph Radius & Diameter");
			metricMenu.add(radiusDiaItem);
			radiusDiaItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					/* Compute the radius/diameter of the graph */

					typeSelected = MetricType.RADUIS;
					MainFrame.this.netPanel.calculateMetric(MetricType.RADUIS);
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
		}
	}

	// This method initializes the items of "Analysis" menu and sets their
	// actionListeners.
	public void initAnalysisMenu(Menu analysisMenu) {
		{
			MenuItem shortestPathItem = new MenuItem("Find Shortest Paths");
			analysisMenu.add(shortestPathItem);
			shortestPathItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new ShortestPathFrame(MainFrame.this);
				}
			});
		}

		{
			MenuItem minSpaTree = new MenuItem(
					"Find Minimum Spanning Tree (Prim)");
			analysisMenu.add(minSpaTree);
			minSpaTree.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new MinimumSpanningTreeFrame(MainFrame.this);
				}
			});
		}

		{
			MenuItem kruskalMST = new MenuItem(
					"Find Minimum spanning tree (kruskal)");
			analysisMenu.add(kruskalMST);
			kruskalMST.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int confirm = JOptionPane
							.showConfirmDialog(
									null,
									"The graph should be undirected! If it's directed this will treat it as an undirected graph!",
									"Confirm", JOptionPane.OK_CANCEL_OPTION);
					if (confirm == JOptionPane.OK_OPTION) {
						UndirectedGraph<GraphNode, GraphEdge> minSpanningTree = new KruskalMST()
								.findMST(MainFrame.this.netPanel.netGraph);
						// A frame for displaying the MST is created.
						new MinimumSpanningTreeWindow(minSpanningTree);
					}
				}
			});
		}

		{
			MenuItem bridgeItem = new MenuItem("Find Bridges");
			analysisMenu.add(bridgeItem);
			bridgeItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (MainFrame.this.netPanel != null
							&& MainFrame.this.netPanel.netGraph != null)
						new BridgeFrame(MainFrame.this.netPanel.netGraph)
								.showResults();
				}
			});
		}

		{
			MenuItem cliqueItem = new MenuItem("Find Cliques");
			analysisMenu.add(cliqueItem);
			cliqueItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new CliqueFinder()
							.find_cliques_pivot(MainFrame.this.netPanel.netGraph
									.getGraph());
					netPanel.netPnl.revalidate();
					netPanel.netPnl.repaint();
				}
			});
		}

		{
			Menu foldItem = new Menu("Fold 2-mode to 1-mode   ");
			analysisMenu.add(foldItem);
			{
				MenuItem rowFoldItem = new MenuItem("By Row");
				foldItem.add(rowFoldItem);
				rowFoldItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (netPanel.netGraph.getNetwork().getMode() != 2) {
							JOptionPane
									.showMessageDialog(
											MainFrame.this,
											"Folding can be applied on two-mode networks.",
											"Operation cannot be done",
											JOptionPane.ERROR_MESSAGE);
						} else
							new FoldFrame().foldNetwork(
									MainFrame.this.netPanel.netGraph, true);
					}
				});
			}

			{
				MenuItem columnFoldItem = new MenuItem("By Column   ");
				foldItem.add(columnFoldItem);
				columnFoldItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new FoldFrame().foldNetwork(
								MainFrame.this.netPanel.netGraph, false);
					}
				});
			}
		}

		{
			MenuItem showMatrixItem = new MenuItem("Show Matrix");
			analysisMenu.add(showMatrixItem);
			showMatrixItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (MainFrame.this.netPanel != null
							&& MainFrame.this.netPanel.netGraph != null)
						new ShowMatrixFrame()
								.showMatrix(MainFrame.this.netPanel.netGraph);
					else
						System.out.println("ooo"); // Dialog to give an
													// error..... (for all other
													// items as well!)
				}
			});
		}

		{
			MenuItem invertItem = new MenuItem("Invert Network");
			analysisMenu.add(invertItem);
			invertItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new InverseMatrixFrame()
							.findInverse(MainFrame.this.netPanel.netGraph);
				}
			});
		}

		{
			MenuItem complementItem = new MenuItem("Complement Network");
			analysisMenu.add(complementItem);
			complementItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new ComplementGraphFrame()
							.findComplement(MainFrame.this.netPanel.netGraph);
				}
			});
		}
	}

	// This method initializes the items of "Clustering" menu and sets their
	// actionListeners.
	public void initClusteringMenu(Menu clusteringMenu) {
		{
			MenuItem modularityItem = new MenuItem("Modularity");
			clusteringMenu.add(modularityItem);
			modularityItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MainFrame.this.netPanel.cluster(1);
				}
			});
		}

		{
			MenuItem betweennessItem = new MenuItem("Edge Betweenness   ");
			clusteringMenu.add(betweennessItem);
			betweennessItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MainFrame.this.netPanel.cluster(2);
				}
			});
		}

		{
			MenuItem voltageItem = new MenuItem("Voltage");
			clusteringMenu.add(voltageItem);
			voltageItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MainFrame.this.netPanel.cluster(3);
				}
			});
		}

		{
			MenuItem MSTItem = new MenuItem("Minimum Spanning Tree");
			clusteringMenu.add(MSTItem);
			MSTItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					MainFrame.this.netPanel.cluster(4);
				}
			});
		}

		{
			// 2011 12/18 Dequan added for CPM clustering
			MenuItem CPMItem = new MenuItem("Clique Percolation Method");
			clusteringMenu.add(CPMItem);
			CPMItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (netPanel != null && netPanel.netGraph != null) {
						if (netPanel.netGraph.getNetwork().getMode() != 1) {
							JOptionPane
									.showMessageDialog(
											MainFrame.this,
											"The algorithm only works in 1-mode network!",
											"Warning",
											JOptionPane.WARNING_MESSAGE);
							return;
						}

						netPanel.cluster(5);
					}
				}
			});
		}
	}

	// This method initializes the items of "Search" menu and sets their
	// actionListeners.
	public void initSearchMenu(Menu searchMenu) {
		{
			MenuItem searchMetricItem = new MenuItem(
					"Search by Network Metrics");
			searchMenu.add(searchMetricItem);
			searchMetricItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MainFrame.this.netPanel.searchMetrics();
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
		}

		{
			/*
			 * MenuItem fuzzySearchItem = new MenuItem("Fuzzy Search");
			 * searchMenu.add(fuzzySearchItem);
			 * fuzzySearchItem.addActionListener(new ActionListener() {
			 * 
			 * @Override public void actionPerformed(ActionEvent e) { new
			 * FuzzySearchFrame(MainFrame.this);
			 * MainFrame.this.netPanel.netPnl.revalidate();
			 * MainFrame.this.netPanel.netPnl.repaint(); } });
			 */
		}
	}

	/******************************* MUNIMA ******************************/
	// Added new initLinkPMenu
	public void initLinkPMenu(Menu linkpMenu) {
		{
			MenuItem linkpMetricItem = new MenuItem("Jaccard");
			linkpMenu.add(linkpMetricItem);
			linkpMetricItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// LinkPred lp=new LinkPred();
					// lp.PredLink();
					MainFrame.this.netPanel.calculateLink();
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
			/****************** MUNIMA ******************/

			// /////////// DICE -
			// Kashfia//////////////////////////////////////////
			MenuItem linkDiceItem = new MenuItem("Dice");
			linkpMenu.add(linkDiceItem);
			linkDiceItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MainFrame.this.netPanel.calculateDiceLink();
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
			// //////////////END
			// DICE///////////////////////////////////////////////

			// /////////// COMMON NEIGHBOR -
			// Kashfia//////////////////////////////////////////
			MenuItem linkCommonItem = new MenuItem("Common Neighbor");
			linkpMenu.add(linkCommonItem);
			linkCommonItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MainFrame.this.netPanel.calculateCommonLink();
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
			// //////////////END COMMON
			// NEIGHBOR///////////////////////////////////////////////

			// /////////// Adamic
			// Suchina//////////////////////////////////////////
			MenuItem linkAdamicItem = new MenuItem("Adamic/Adar");
			linkpMenu.add(linkAdamicItem);
			linkAdamicItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MainFrame.this.netPanel.calculateAdamicLink();
					MainFrame.this.netPanel.revalidate();
					MainFrame.this.netPanel.repaint();
				}
			});
			// //////////////END
			// Adamic///////////////////////////////////////////////

		}

	}

	// This method initializes the items of "Community Detection" menu and sets
	// their
	// actionListeners.
	public void initHierarchicalZoomingMenu(Menu hierarchicalZoomingMenu) {
		{
			MenuItem communityDisplayItem = new MenuItem(
					"Community Detection Display");
			hierarchicalZoomingMenu.add(communityDisplayItem);
			communityDisplayItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new HierarchicalZoomGraphFrame(
							MainFrame.this.netPanel.netGraph);
				}
			});
		}
	}
}
