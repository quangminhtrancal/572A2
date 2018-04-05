package GUI;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import data.InputDataGenerator;

public class InputDataGeneratorPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean firstLoad = true;

	int mode = 1;
	boolean headers = false;
	boolean isDirected = false;

	Checkbox headerCheck = null;
	Checkbox nonOccurenceCheck = null;
	Checkbox startTimeCheck = null;
	JSpinner startTimeSpinner = null;
	JSpinner endTimeSpinner = null;
	Checkbox endTimeCheck = null;
	Checkbox keyTermCooccurenceRelation = null;
	Checkbox tweeterCommonTermRelation = null;
	Checkbox tweeterKeytermRelation = null;
	NetworkPanel parentPanel = null;
	JFrame frame = null;
	Button fileChooserButton = null;
	FileDialog fd = null;
	JTextArea keywordsTextArea = null;
	String[] keywords = null;

	InputDataGeneratorPanel(NetworkPanel parent, JFrame frame, boolean flag) {
		this.parentPanel = parent;
		this.frame = frame;
		this.firstLoad = flag;
		this.setLayout(null);

		initDataFileLabel();
		initKeyWordsLabel();
		initDateLabel();
		initOptionsLabel();
		initButtons();
	}

	private void initDataFileLabel() {
		JLabel dataFileLabel = new JLabel();
		Border blackline = BorderFactory.createLineBorder(Color.black);

		TitledBorder titledBorder = new TitledBorder(blackline, "Choose File");
		dataFileLabel.setBorder(titledBorder);
		dataFileLabel.setBounds(10, 10, 440, 60);

		initFileChooserComponents(dataFileLabel);
		this.add(dataFileLabel);

	}

	private void initKeyWordsLabel() {
		JLabel keywordsLabel = new JLabel();
		Border blackline = BorderFactory.createLineBorder(Color.black);

		TitledBorder titledBorder = new TitledBorder(blackline, "Enter keywords separated by comma or whitespace");
		keywordsLabel.setBorder(titledBorder);

		keywordsLabel.setBounds(10, 80, 440, 150);
		keywordsTextArea = new JTextArea();
		keywordsTextArea.setBounds(15, 20, 400, 100);
		keywordsTextArea.setLineWrap(true);

		headerCheck = new Checkbox("Include headers", false, null);
		headerCheck.setBounds(15, 125, 130, 20);

		nonOccurenceCheck = new Checkbox("Exclude posts not including keywords", false, null);
		nonOccurenceCheck.setBounds(150, 125, 285, 20);
		//nonOccurenceCheck.setBounds(15, 125, 285, 20);

		keywordsLabel.add(keywordsTextArea);
		keywordsLabel.add(headerCheck);
		keywordsLabel.add(nonOccurenceCheck);
		this.add(keywordsLabel);
	}

	private void initDateLabel() {
		JLabel dateLabel = new JLabel();
		Border blackline = BorderFactory.createLineBorder(Color.black);

		TitledBorder titledBorder = new TitledBorder(blackline, "Choose data start/end times");
		dateLabel.setBorder(titledBorder);
		dateLabel.setBounds(10, 240, 440, 100);

		startTimeCheck = new Checkbox("Start Date:");
		startTimeCheck.setBounds(5, 25, 100, 20);
		dateLabel.add(startTimeCheck);

		endTimeCheck = new Checkbox("End Date");
		endTimeCheck.setBounds(5, 65, 100, 20);
		dateLabel.add(endTimeCheck);

		startTimeSpinner = createTimeSpinner();
		startTimeSpinner.setBounds(110, 20, 200, 30);
		dateLabel.add(startTimeSpinner);

		endTimeSpinner = createTimeSpinner();
		endTimeSpinner.setBounds(110, 60, 200, 30);
		dateLabel.add(endTimeSpinner);

		startTimeCheck.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					startTimeSpinner.setEnabled(true);
				} else {
					startTimeSpinner.setEnabled(false);
				}
			}
		});

		endTimeCheck.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					endTimeSpinner.setEnabled(true);
				} else {
					endTimeSpinner.setEnabled(false);
				}
			}
		});

		this.add(dateLabel);
	}

	private void initOptionsLabel() {
		JLabel optionsLabel = new JLabel();
		Border blackline = BorderFactory.createLineBorder(Color.black);

		TitledBorder titledBorder = new TitledBorder(blackline, "Choose input data relations to generate");
		optionsLabel.setBorder(titledBorder);
		optionsLabel.setBounds(10, 350, 440, 120);

		keyTermCooccurenceRelation = new Checkbox("Generate keyterm co-occurence relation data", false, null);
		tweeterCommonTermRelation = new Checkbox("Generate tweeter common term relation data", false, null);
		tweeterKeytermRelation = new Checkbox("Generate tweeter/keyterm two-mode relation data", false, null);

		keyTermCooccurenceRelation.setBounds(10, 20, 362, 20);
		tweeterCommonTermRelation.setBounds(10, 50, 362, 20);
		tweeterKeytermRelation.setBounds(10, 80, 400, 20);

		optionsLabel.add(keyTermCooccurenceRelation);
		optionsLabel.add(tweeterCommonTermRelation);
		optionsLabel.add(tweeterKeytermRelation);

		this.add(optionsLabel);
	}

	private void initFileChooserComponents(JLabel label) {
		JLabel fileChooserLabel = new JLabel();
		fileChooserButton = new Button("Choose a file");
		fileChooserButton.setBounds(5, 5, 400, 30);
		fileChooserLabel.add(fileChooserButton);

		fileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				InputDataGeneratorPanel.this.isDirected = false;
				InputDataGeneratorPanel.this.headers = InputDataGeneratorPanel.this.headerCheck.getState();
				InputDataGeneratorPanel.this.openFileChooser();
			}
		});

		fileChooserLabel.setBounds(20, 30, 440, 40);
		this.add(fileChooserLabel);

	}

	private JSpinner createTimeSpinner() {
		JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "dd-MM-yy HH:mm");
		timeSpinner.setEditor(timeEditor);
		timeSpinner.setEnabled(false);

		return timeSpinner;
	}

	private void initButtons() {
		Button generateButton = new Button("Generate");
		Button cancelBtn = new Button("Cancel");

		generateButton.setBounds(160, 470, 90, 30);
		this.add(generateButton);

		cancelBtn.setBounds(260, 470, 60, 30);
		this.add(cancelBtn);

		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				InputDataGeneratorPanel.this.frame.dispose();
			}
		});
		cancelBtn.requestFocusInWindow();

		generateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					checkInput();

					Date startDate = null;
					Date endDate = null;
					if (startTimeCheck.getState()) {
						startDate = (Date) startTimeSpinner.getValue();
					}
					if (endTimeCheck.getState()) {
						endDate = (Date) endTimeSpinner.getValue();
					}
					InputDataGenerator inputDataGenerator = new InputDataGenerator(fd.getDirectory(), fd.getFile(),
							headerCheck.getState(), nonOccurenceCheck.getState(), keywords, startDate, endDate);
					// inputDataGenerator.listEligiblePosts();
					List<String> createdFileNames = inputDataGenerator.createInputDataFiles(
							keyTermCooccurenceRelation.getState(), tweeterCommonTermRelation.getState(),
							tweeterKeytermRelation.getState());
					StringBuilder message = new StringBuilder("Files created under ");
					message.append(fd.getDirectory());
					message.append(" are:");
					for (String str : createdFileNames) {
						message.append("\n");
						message.append(str);
					}

					keyTermCooccurenceRelation.setState(false);
					tweeterCommonTermRelation.setState(false);
					tweeterKeytermRelation.setState(false);

					JOptionPane.showMessageDialog(frame, message, "Files are created", JOptionPane.INFORMATION_MESSAGE);

				} catch (MyOwnException exception) {
					JOptionPane.showMessageDialog(frame, exception.message, "Invalid input", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	private void checkInput() throws MyOwnException {
		if (fd != null && fd.getDirectory() != null && fd.getFile() != null) {
			if (!keyTermCooccurenceRelation.getState() && !tweeterCommonTermRelation.getState()
					&& !tweeterKeytermRelation.getState()) {
				throw new MyOwnException("You should select at least one relation option");
			} else {
				populateKeywords();
				if (keywords == null || keywords.length == 0) {
					throw new MyOwnException("No keywords selected");
				}
			}
		} else {
			throw new MyOwnException("No data file is chosen");
		}
	}

	private void populateKeywords() {
		String text = keywordsTextArea.getText().toLowerCase().trim();
		if (text.isEmpty())
			return;
		keywords = text.split("\\s+|,");
	}

	private void openFileChooser() {
		fd = new FileDialog(frame, "Open a Network File", FileDialog.LOAD);
		fd.setFile("*.txt");
		fd.setEnabled(true);
		fd.setVisible(true);
		fd.setAlwaysOnTop(true);

		if (fd.getDirectory() != null && fd.getFile() != null) {
			if (this.fileChooserButton != null) {
				this.fileChooserButton.setLabel(fd.getFile() + " is selected");
			}
		} else {
			if (this.fileChooserButton != null) {
				this.fileChooserButton.setLabel("Choose Data File");
			}
		}
	}
}
