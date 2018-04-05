package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class InputDataGenerator {

	private Boolean containsHeader;
	private Boolean nonOccurenceCheck;
	private String fileName;
	private String directory;
	private String[] keywords;
	private Date startDate;
	private Date endDate;
	List<SocialMediaPost> posts;
	List<String> tweeters;

	public InputDataGenerator(String directory, String fileName, boolean containsHeader, boolean nonOccurenceCheck,
			String[] keywords, Date startDate, Date endDate) {
		this.containsHeader = containsHeader;
		this.nonOccurenceCheck = nonOccurenceCheck;
		this.directory = directory;
		this.fileName = fileName;
		this.keywords = keywords;
		this.startDate = startDate;
		this.endDate = endDate;
		posts = new ArrayList<SocialMediaPost>();
		loadDataFile();
	}

	private void loadDataFile() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Scanner scanner = new Scanner(new File(directory + "/" + fileName));
			Set<String> tweeterSet = new HashSet<String>();
			while (scanner.hasNext()) {
				String userName, date, time, line;
				StringBuilder tweet;

				try {
					userName = scanner.next();
					date = scanner.next();
					time = scanner.next();
					tweet = new StringBuilder(scanner.nextLine());

					while (scanner.hasNext()) {
						line = scanner.nextLine();
						if (line.startsWith("----------------------------------------------------"))
							break;
						tweet.append(" ");
						tweet.append(line);
					}
					Date postDate = dateFormat.parse(date + " " + time);
					if (startDate != null && postDate.before(startDate)) {
						continue;
					}
					if (endDate != null && postDate.after(endDate)) {
						continue;
					}

					SocialMediaPost post = new SocialMediaPost(userName, postDate, tweet.toString().toLowerCase());

					boolean occurence = post.buildKeywordOccurenceMatrix(keywords);
					if (!nonOccurenceCheck || occurence) {
						posts.add(post);
						tweeterSet.add(userName);
					}
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			tweeters = new ArrayList<String>(tweeterSet);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public void listEligiblePosts() {
		for (SocialMediaPost post : posts) {
			System.out.println(post);
		}
	}

	public List<String> createInputDataFiles(boolean keytermCoocurenceRelation, boolean tweeterCommonTermRelation,
			boolean tweeterKeytermRelation) {
		List<String> fileNames = new ArrayList<String>();
		if (keytermCoocurenceRelation) {
			fileNames.add(buildKeytermCoocurrenceFile());

		}
		if (tweeterCommonTermRelation || tweeterKeytermRelation) {
			int kLen = keywords.length;
			Map<String, int[]> map = new HashMap<String, int[]>();
			for (String tweeter : tweeters) {
				int[] totalOccurences = new int[kLen];
				for (int i = 0; i < kLen; i++) {
					totalOccurences[i] = 0;
				}
				map.put(tweeter, totalOccurences);
			}

			for (SocialMediaPost post : posts) {
				int[] matrix = post.getMatrix();
				int[] totalOccurences = map.get(post.getUserName());
				for (int i = 0; i < kLen; i++) {
					if (matrix[i] == 1)
						totalOccurences[i] = 1;
				}
				map.put(post.getUserName(), totalOccurences);
			}

			if (tweeterCommonTermRelation) {
				fileNames.add(buildTweeterCommonTermFile(map));
			}
			if (tweeterKeytermRelation) {
				fileNames.add(buildTweeterKeytermFile(map));
			}
		}

		return fileNames;
	}

	private String buildTweeterKeytermFile(Map<String, int[]> map) {
		Date today = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String fileSuffix = "tweeterKeyterm_" + df.format(today);

		int tLen = tweeters.size();
		int kLen = keywords.length;

		try {
			String fName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + fileSuffix + ".csv";
			FileWriter writer = new FileWriter(directory + "/" + fName);
			if (containsHeader) {
				for (String tweeter : tweeters) {
					writer.append(",");
					writer.append(tweeter);
				}
				writer.append("\n");
			}
			for (int i = 0; i < kLen; i++) {
				if (containsHeader) {
					writer.append(keywords[i] + ",");
				}
				for (int j = 0; j < tLen; j++) {
					writer.append(Integer.toString(map.get(tweeters.get(j))[i]));
					if (j != tLen - 1) {
						writer.append(",");
					}
				}
				writer.append("\n");
			}
			writer.flush();
			writer.close();
			return fName;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "ERROR";
	}

	private String buildTweeterCommonTermFile(Map<String, int[]> map) {
		Date today = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String fileSuffix = "tweeterCommon_" + df.format(today);

		int tLen = tweeters.size();
		int kLen = keywords.length;

		float[][] tweeterMatrix = new float[tLen][tLen];
		for (int i = 0; i < tLen; i++) {
			for (int j = 0; j < tLen; j++) {
				tweeterMatrix[i][j] = 0;
			}
		}

		for (int i = 0; i < tLen; i++) {
			for (int j = i + 1; j < tLen; j++) {
				int[] matrix1 = map.get(tweeters.get(i));
				int[] matrix2 = map.get(tweeters.get(j));
				float result = 0;
				for (int k = 0; k < kLen; k++) {
					if (matrix1[k] == 1 && matrix2[k] == 1) {
						result += 1;
					}
				}
				tweeterMatrix[i][j] = result;
			}
		}

		for (int i = 0; i < tLen; i++) {
			for (int j = i + 1; j < tLen; j++) {
				tweeterMatrix[i][j] = tweeterMatrix[i][j] / kLen;
				tweeterMatrix[j][i] = tweeterMatrix[i][j];
			}
		}

		try {
			String fName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + fileSuffix + ".csv";
			FileWriter writer = new FileWriter(directory + "/" + fName);
			if (containsHeader) {
				for (String tweeter : tweeters) {
					writer.append(",");
					writer.append(tweeter);
				}
				writer.append("\n");
			}
			for (int i = 0; i < tLen; i++) {
				if (containsHeader) {
					writer.append(tweeters.get(i) + ",");
				}
				for (int j = 0; j < tLen; j++) {
					writer.append(String.format("%.2f", tweeterMatrix[i][j]));
					if (j != tLen - 1) {
						writer.append(",");
					}
				}
				writer.append("\n");
			}
			writer.flush();
			writer.close();
			return fName;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "ERROR";
	}

	private String buildKeytermCoocurrenceFile() {
		Date today = Calendar.getInstance().getTime();
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String fileSuffix = "keytermCooc_" + df.format(today);

		int kLen = keywords.length;
		float[][] keytermMatrix = new float[kLen][kLen];
		for (int i = 0; i < kLen; i++) {
			for (int j = 0; j < kLen; j++) {
				keytermMatrix[i][j] = 0;
			}
		}

		for (SocialMediaPost post : posts) {
			for (int i = 0; i < kLen; i++) {
				for (int j = i + 1; j < kLen; j++) {
					if (post.getMatrixValue(i) == 1 && post.getMatrixValue(j) == 1) {
						keytermMatrix[i][j] = keytermMatrix[i][j] + 1;
					}
				}
			}
		}

		for (int i = 0; i < kLen; i++) {
			for (int j = i + 1; j < kLen; j++) {
				keytermMatrix[i][j] = keytermMatrix[i][j] / posts.size();
				keytermMatrix[j][i] = keytermMatrix[i][j];
			}
		}

		try {
			String fName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + fileSuffix + ".csv";
			FileWriter writer = new FileWriter(directory + "/" + fName);
			if (containsHeader) {
				for (String keyword : keywords) {
					writer.append(",");
					writer.append(keyword);
				}
				writer.append("\n");
			}
			for (int i = 0; i < kLen; i++) {
				if (containsHeader) {
					writer.append(keywords[i] + ",");
				}
				for (int j = 0; j < kLen; j++) {
					writer.append(String.format("%.2f", keytermMatrix[i][j]));
					if (j != kLen - 1) {
						writer.append(",");
					}
				}
				writer.append("\n");
			}
			writer.flush();
			writer.close();
			return fName;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "ERROR";

	}

}
