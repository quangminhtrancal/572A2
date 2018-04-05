package GUI;

import java.awt.Button;
import java.awt.Color;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import analysis.Fuzzifier;


public class FuzzySearchFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MainFrame parentFrame;
	String[] sliderName = {"Betweenness Centrality", "Closeness Centrality", "Eigenvector Centrality", "Degree Centrality"};
	TextField[] thresholds = new TextField[sliderName.length];
	StatusBar sb;
	
	FuzzySearchFrame(MainFrame mf)
	{
		this.setTitle("Fuzzy Search");
		this.setBounds(100, 100, 540, 600);
		this.setSize(540, 600);
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.setEnabled(true);
		this.setLayout(null);
		this.setResizable(false);
		this.parentFrame = mf;
		init();
		
		sb = new StatusBar(this);
		sb.setText("Please wait while the results are being calculated");
		sb.revalidate();
		sb.repaint();
		sb.setVisible(false);
	}
	public void init()
	{
		JPanel jp = new JPanel();
		jp.setLayout(null);
		jp.setBounds(0, 0, 540, 520);
		final int featureNum = sliderName.length;
		for(int i = 0; i < featureNum; i++) {
			JLabel label = new JLabel(sliderName[i]+":");
			label.setBounds(30, (int)((float)450/(float)featureNum)*i+65, 150, 25);
			final JSlider sdr = new JSlider(JSlider.HORIZONTAL, 0,100, 50);
			sdr.setBounds(180, (int)((float)450/(float)featureNum)*i+65, 250, 16);
			sdr.setMinimum(0);
			sdr.setMaximum(105);
			sdr.setForeground(new Color(255,0,125));
			sdr.setValue(50);
			final TextField t = new TextField();
			t.setBounds(450, (int)((float)450/(float)featureNum)*i+62, 35, 25);
			t.setText("0.5");
			thresholds[i] = t;
			t.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String value = t.getText();
					if(Double.parseDouble(value) > 1){
						t.setText("1");
						sdr.setValue(100);
					}else if(Double.parseDouble(value) < 0){
						t.setText("0");
						sdr.setValue(0);
					}else{
						sdr.setValue((int)(Double.parseDouble(value)*100));
					}
						
				}
			});
			sdr.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// TODO Auto-generated method stub
					t.setText(new Double((double) sdr.getValue() / 100).toString());
				}
			});
			jp.add(label);
			jp.add(sdr);
			jp.add(t);
		}
			
		Button btn = new Button("Calculate");
		btn.setBounds(370, (int)((float)450/(float)featureNum)*featureNum+30, 100, 30);
		jp.add(btn);
		this.add(jp);
		
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sb.setVisible(true);
				sb.revalidate();
				sb.repaint();
				Fuzzifier f = new Fuzzifier();
				double[] threshold = new double[featureNum];
				for(int i = 0; i < featureNum; i++)
					threshold[i] = Double.parseDouble(thresholds[i].getText());
				f.doFuzzifier(parentFrame.netPanel.netGraph, threshold);
				parentFrame.netPanel.netPnl.revalidate();
				parentFrame.netPanel.netPnl.repaint();
				FuzzySearchFrame.this.dispose();
			}
		});
	}
	
	
}
