package GUI;

import java.awt.Button;
import java.awt.Choice;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import analysis.MetricFinder;

import data.GraphEdge;
import data.GraphNode;
import data.NodeMetric;
import edu.uci.ics.jung.graph.Graph;


public class SearchMetricFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MainFrame parentFrame;
	int metricType = 0;
	boolean less = true;
	TextField thresholdField;
	NodeMetric[] result;
	
	SearchMetricFrame(MainFrame mf)
	{
		this.setTitle("Search by metrics");
		this.setBounds(100, 100, 320, 200);
		this.setSize(320, 200);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.setEnabled(true);
		this.parentFrame = mf;
	}
	public void init()
	{
		JPanel jp = new JPanel();
		
		metricType = 1;
		Choice metric = new Choice();
		metric.add("Betweenness");
		metric.add("Closeness");
		metric.add("Degree");
		metric.add("Eigenvector");
		metric.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(((String)e.getItem()).equals("Betweenness"))
					SearchMetricFrame.this.metricType = 1;
				else if(((String)e.getItem()).equals("Closeness"))
					SearchMetricFrame.this.metricType = 2;
				else if(((String)e.getItem()).equals("Degree"))
					SearchMetricFrame.this.metricType = 3;
				else if(((String)e.getItem()).equals("Eigenvector"))
					SearchMetricFrame.this.metricType = 4;
			}
		});
		
		Choice criteria = new Choice();
		criteria.add("Less than");
		criteria.add("Greater than");
		criteria.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(((String)e.getItem()).equals("Less than"))
					less = true;
				else if(((String)e.getItem()).equals("Greater than"))
					less = false;
			}
		});
		
		thresholdField = new TextField("0");

		Button findBtn = new Button("Search");
		findBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchMetricFrame.this.result = SearchMetricFrame.this.calculateResults(parentFrame.netPanel.netGraph.getGraph());
				if(result==null)
					return;
				SearchMetricFrame.this.parentFrame.netPanel.showSearchResults(SearchMetricFrame.this);
				SearchMetricFrame.this.dispose();
			}
		});
		
		JLabel label = new JLabel("Search for nodes with");
		label.setBounds(20, 30, 140, 22);
		jp.add(label);
		jp.setLayout(null);
		metric.setBounds(20, 65, 100, 30);
		jp.add(metric);
		criteria.setBounds(130, 65, 89, 20);
		jp.add(criteria);
		thresholdField.setBounds(235, 65, 45, 22);
		jp.add(thresholdField);
		findBtn.setBounds(230, 110, 50, 25);
		jp.add(findBtn);
		
		this.add(jp);
	}

	private NodeMetric[] calculateResults(Graph<GraphNode, GraphEdge> graph)
	{
		double threshold;
		try{
			threshold = Double.parseDouble(thresholdField.getText());
		} catch(NumberFormatException e){
			/////////////////////////////////////////////DO SOMETHING GOOD :D
			JOptionPane.showMessageDialog(null, "The number enterred should be in a double format!", "Alert",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
		
		NodeMetric[] metrics = null;
		switch (metricType) {
		case 1:
			metrics = new MetricFinder().findBetweenness(graph);
			break;
		case 2:
			metrics = new MetricFinder().findCloseness(graph);
			break;
		case 3:
			metrics = new MetricFinder().findDegree(graph);
			break;
		case 4:
			metrics = new MetricFinder().findEigenvector(graph);
			break;
		}
		
		ArrayList<NodeMetric> result = new ArrayList<NodeMetric>();
		boolean lessThan, greaterThan;
		for(int i = 0; i < metrics.length; i++)
		{
			lessThan = less && metrics[i].getMetricValue() < threshold;
			greaterThan = !less && metrics[i].getMetricValue() > threshold;
			if(lessThan || greaterThan)
				result.add(metrics[i]);
		}
		
		NodeMetric[] resultArray = new NodeMetric[result.size()];
		for(int i = 0; i < result.size(); i++)
			resultArray[i] = result.get(i);
		
		return resultArray;
	}
	
}
