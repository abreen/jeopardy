/*
 * Jeopardy.java
 * Author: Cody Doucette
 *
 * Description: A program that emulates the Jeopardy! game show. A file of
 * the following format should be in the same directory as Jeopardy.java for
 * this game to run correctly:
 *
 * <num categories>
 * <category title>
 * #
 * <$100 question for this category>
 * #
 * <$200 question for this category>
 * ...
 * #
 * <$500 question for this category>
 * #
 * <category title>
 * ...
 * #
 * <final jeopardy question>
 */

import java.util.*;
import java.io.*;

public class Jeopardy {

	private static class Question {

		private String question;
		private int value;
		private int visible;
		private boolean dailyDouble;

		public Question(String question, int value) {
			this.question = question;
			this.value = value;
		}

		public void setVisibility(int v) {
			this.visible = v;
		}

		public void setDD() {
			this.dailyDouble = true;
		}

		public boolean isDailyDouble() {
			return this.dailyDouble;
		}

		public int visible() {
			return this.visible;
		}

		public void printQuestion() {
			System.out.println(this.question);
		}

		public int getValue() {
			return this.value;
		}
	}

	private static final int Q_PER_CAT = 5;
	private static final int TIME_PER_QUESTION = 45000;
	private static final String TMP = "jeopardy_tmp.txt";
	private static final String ESC = "\033[";

	private static final String QUESTIONS = "questions.txt";
	private static final String SOLUTIONS = "solutions.txt";

	private static Question[][] board;
	private static String[][] solutions;
	private static String[] categories;
	private static int[] teams = new int[4];
	private static String finalJeopardyTopic;
	private static String finalJeopardy;
	private static String finalJeopardySolution;

	public static void printPadded(String s, int n) {

		int numSpacesLeft = (n - s.length()) / 2;
		int numSpacesRight = (n - s.length()) / 2;
		if (numSpacesLeft + numSpacesRight + s.length() != n)
			numSpacesLeft++;

		for (int i = 0; i < numSpacesLeft; i++) {
			System.out.print(" ");
		}
		System.out.print(s);
		for (int i = 0; i < numSpacesRight; i++) {
			System.out.print(" ");
		}
	}

	public static boolean boardEmpty() {
		for (int i = 0; i < Q_PER_CAT; i++) {
			for (int j = 0; j < categories.length; j++) {
				if (board[i][j].visible == 1)
					return false;
			}
		}
		return true;
	}

	public static void finalJeopardy() {

		Scanner input = new Scanner(System.in);
		int[] wagers = new int[4];

		System.out.print(ESC + "2J");
		System.out.println("It's time for Final Jeopardy!");
		System.out.println();
		System.out.println("Topic: " + finalJeopardyTopic);
		System.out.println();

		for (int i = 0; i < 4; i++) {
			if (teams[i] <= 0)
				continue;
			System.out.println("Enter wager for team " + i + " (must be no" +
				" greater than " + teams[i] + "): ");
			int wager = input.nextInt();
			while (wager > teams[i]) {
				wager = input.nextInt();
			}
			wagers[i] = wager;
		}

		System.out.print(ESC + "2J");
		System.out.println(finalJeopardy);
		System.out.println();
		do {
			System.out.print("Display solution (y/n)?: ");
		} while (!input.next().equalsIgnoreCase("y"));
		System.out.println();
		System.out.println();
		System.out.println(finalJeopardySolution);
		System.out.println();

		for (int i = 0; i < 4; i++) {
			if (teams[i] <= 0)
				continue;
			System.out.println("Did team " + i + " answer correctly?: ");
			if (input.next().equalsIgnoreCase("y"))
				teams[i] += wagers[i];
			else if (teams[i] > 0)
				teams[i] -= wagers[i];
		}

		System.out.println();
		System.out.println("The final scores are:");
		printScores();

	}

	public static void dailyDouble(Question q, int category, int amount) {

		Scanner input = new Scanner(System.in);

		System.out.print(ESC + "2J");
		System.out.println();
		printScores();

		System.out.println("Daily double!");
		System.out.println();
		System.out.println();
		System.out.print("Which team selected the question?: ");
		int team = input.nextInt();
		int wager;
		if (teams[team] > 0) {
			System.out.print("How much does team " + team + " want to" +
			" wager?: ");
			wager = input.nextInt();
		} else {
			wager = q.getValue();
		}

		System.out.print(ESC + "2J");
		System.out.println();
		q.printQuestion();

		do {
			System.out.print("Display solution (y/n)?: ");
		} while (!input.next().equalsIgnoreCase("y"));
		System.out.println();
		System.out.println();
		System.out.println(solutions[amount / 100 - 1][category]);
		System.out.println();
		System.out.println();

		System.out.print("Did team " + team + " answer correctly " +
			"(y/n)?: ");
		if (input.next().equalsIgnoreCase("y"))
			teams[team] += wager;
		else if (teams[team] > 0)
			teams[team] -= wager;
	}

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);

		try {
			parseQuestions(QUESTIONS);
		} catch (FileNotFoundException e) { };

		try {
			parseSolutions(SOLUTIONS);
		} catch (FileNotFoundException e) { };

		try {
			parseState();
		} catch (FileNotFoundException e) {
			for (int i = 0; i < 4; i++) {
				teams[i] = 0;
			}

			for (int i = 0; i < Q_PER_CAT; i++) {
				for (int j = 0; j < categories.length; j++) {
					board[i][j].setVisibility(1);
				}
			}
		}

		if (boardEmpty()) {
			finalJeopardy();
			return;
		}

		System.out.print(ESC + "2J");
		printScores();
		printBoard();
		System.out.println();
		System.out.println();

		System.out.print("Pick a category number and dollar amount: ");			int category = input.nextInt();
		int amount = input.nextInt();

		while (board[amount / 100 - 1][category].visible() != 1) {
			System.out.print("Pick a category number and a " +
				" dollar amount: ");
			category = input.nextInt();
			amount = input.nextInt();
		}

		Question q = board[amount / 100 - 1][category];
		q.setVisibility(0);

		if (q.isDailyDouble()) {
			dailyDouble(q, category, amount);
			updateGame();
			return;
		}

		System.out.print(ESC + "2J");
		q.printQuestion();

		do {
			System.out.print("Display solution (y/n)?: ");
		} while (!input.next().equalsIgnoreCase("y"));
		System.out.println();
		System.out.println();
		System.out.println(solutions[amount / 100 - 1][category]);
		System.out.println();
		System.out.println();

		while (true) {
			System.out.print("Deduct points from which team " +
				"(or -1 to continue)?: ");
			int team = input.nextInt();
			if (team == -1)
				break;
			if (team < 0 || team >= teams.length)
				continue;
			teams[team] -= q.getValue();
		}

		while (true) {
			System.out.print("Award points to which team " +
				"(or -1 to continue)?: ");
			int team = input.nextInt();
			if (team == -1)
				break;
			if (team < 0 || team >= teams.length)
				continue;
			teams[team] += q.getValue();
		}

		updateGame();
		System.out.println();
		System.out.print(ESC + "2J");
	}

	private static void printScores() {
		for (int i = 0; i < 4; i++) {
			System.out.println("Team " + i + ": $" + teams[i]);
		}
		System.out.println();
	}

	private static void printBoard() {

		for (int i = 0; i < categories.length; i++) {
			System.out.print("---------------");
		}
		System.out.println();

		for (int i = 0; i < categories.length; i++) {
			printPadded("Cat " + i, 15);
		}
		System.out.println();

		for (int i = 0; i < categories.length; i++) {
			printPadded(categories[i], 15);
		}
		System.out.println();

		for (int i = 0; i < Q_PER_CAT; i++) {
			for (int j = 0; j < categories.length; j++) {
				String display = "";
				if (board[i][j].visible() == 1)
					display = "$" + (i + 1) * 100;

				printPadded(display, 15);
			}
			System.out.println();
		}
		System.out.println();
		for (int i = 0; i < categories.length; i++) {
			System.out.print("---------------");
		}
		System.out.println();

	}

	private static void updateGame() {

		try {
			File f = new File(TMP);
			f.delete();
		} catch (Exception e) { };

		try {
			PrintWriter writer = new PrintWriter(TMP, "UTF-8");

			for (int i = 0; i < 4; i++) {
				writer.println(teams[i]);
			}

			for (int i = 0; i < Q_PER_CAT; i++) {
				for (int j = 0; j < categories.length; j++) {
					writer.println(board[i][j].visible());
				}
			}
			writer.close();
		} catch (Exception e) { };
	}

	private static void parseState()
		throws FileNotFoundException {

		Scanner input = new Scanner(new File(TMP));

		for (int i = 0; i < 4; i++) {
			teams[i] = input.nextInt();
		}

		for (int i = 0; i < Q_PER_CAT; i++) {
			for (int j = 0; j < categories.length; j++) {
				board[i][j].setVisibility(input.nextInt());
			}
		}

	}

	private static void parseQuestions(String filename)
		throws FileNotFoundException {

		Scanner input = new Scanner(new File(filename));
		input.useDelimiter("\t|\n|\n\r|\r");
		int numCategories = input.nextInt();

		board = new Question[Q_PER_CAT][numCategories];
		categories = new String[numCategories];

		for (int i = 0; i < numCategories; i++) {
			categories[i] = input.next();
			input.nextLine();
			input.nextLine();

			input.useDelimiter("\n|\n\r|\r");
			for (int j = 0; j < Q_PER_CAT; j++) {
				String q = "";
				String line;
				while (true) {
					line = input.nextLine();
					if (line.equals("#"))
						break;
					q += line + "\n";
				}
				board[j][i] = new Question(q, 100 * (j + 1));
			}
		}
		String q = "";
		String line;
		while (true) {
			line = input.nextLine();
			if (line.equals("#"))
				break;
			q += line + "\n";
		}
		finalJeopardyTopic = q;

		while (true) {
			line = input.nextLine();
			if (line.equals("#"))
				break;
			q += line + "\n";
		}
		finalJeopardy = q;

		int n = Q_PER_CAT * categories.length;
		board[0][1].setDD();
		board[3][1].setDD();
	}

	private static void parseSolutions(String filename)
		throws FileNotFoundException {

		Scanner input = new Scanner(new File(filename));
		int numCategories = input.nextInt();

		solutions = new String[Q_PER_CAT][numCategories];

		for (int i = 0; i < numCategories; i++) {
			String category = input.next();
			input.nextLine();
			input.nextLine();

			for (int j = 0; j < Q_PER_CAT; j++) {
				String q = "";
				String line;
				while (true) {
					line = input.nextLine();
					if (line.equals("#"))
						break;
					q += line;
				}
				solutions[j][i] = q;
			}
		}
		String q = "";
		String line;
		while (true) {
			line = input.nextLine();
			if (line.equals("#"))
				break;
			q += line + "\n";
		}
		finalJeopardySolution = q;
	}
}
