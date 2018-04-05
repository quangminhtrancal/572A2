package data;

import java.util.Date;

public class SocialMediaPost {

	private String userName;
	private Date submissionDate;
	private String[] entry;
	private int[] matrix;

	public SocialMediaPost(String userName, Date submissionDate, String post) {
		this.userName = userName;
		this.submissionDate = submissionDate;
		this.entry = post.split("\\s+");
	}

	public boolean doesKeywordExist(String keyword) {
		for (String str : entry) {
			if (str.contains(keyword))
				return true;
		}
		return false;
	}

	public boolean buildKeywordOccurenceMatrix(String[] keywords) {
		boolean result = false;
		matrix = new int[keywords.length];
		for (int i = 0; i < keywords.length; i++) {
			matrix[i] = doesKeywordExist(keywords[i]) ? 1 : 0;
			if (matrix[i] == 1)
				result = true;
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(userName);
		builder.append(" ");
		builder.append(submissionDate);
		builder.append(" ");
		for (String str : entry) {
			builder.append(str);
			builder.append(" ");
		}
		builder.append("\n");
		for (int i : matrix) {
			builder.append(i);
			builder.append(" ");
		}
		return builder.toString();
	}

	public int getMatrixValue(int index) {
		return matrix[index];
	}

	public int[] getMatrix() {
		return matrix;
	}

	public String getUserName() {
		return userName;
	}

}
