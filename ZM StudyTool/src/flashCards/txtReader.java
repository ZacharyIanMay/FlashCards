package flashCards;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class txtReader 
{
	private ArrayList<File> files;
	private ArrayList<String> chapterSelection, currentlySelected;
	private ArrayList<Integer> missingFiles;
	private File rep;
	private FileReader fr;
	private Boolean cont;

	/**
	 * generates a list of all chapters their filepath and the last chapter that should be added
	 * please name all text files as ch<number> such that chapter 1 would be ch1 and chapter 92 would be ch92
	 * @param filepath the filepath to the folder all chapter text files are listed in
	 * @param chapter the final chapter that should be included in the list to be studied.
	 */
	public txtReader(String filepath, int chapter)
	{
		//stores the expected location of the report file
		rep = new File(filepath + "\\report.txt");
		cont = true;
		
		//Initializes the array to store files
		missingFiles = new ArrayList<Integer>();
		files = new ArrayList<File>();
		chapterSelection = new ArrayList<String>();
		
		//adds files to either the list of missing files or the list of existent files
		for(int i = 1; i <= chapter; i++)
		{
			File f = new File(filepath + "\\ch" + i + ".txt");
			if(!f.exists()) 
			{
				missingFiles.add(i);
			}
			else
			{
				files.add(f);
				chapterSelection.add("" + i);
			}
		}
		
		//warns user of what files are missing
		if(missingFiles.size() > 0)
		{
			System.out.print("Warning: Chapter text files not found: " + missingFiles.get(0));
			for(int i = 1; i < missingFiles.size(); i++)
			{
				System.out.print(", " + missingFiles.get(i));
			}
			System.out.print("\n");
		}
		
		//selects all chapters to be used for a quiz.
		currentlySelected = chapterSelection;
	}
	
//	/**
//	 * attempt at using the constructor to get user input and generate the needed information, currently abandoned
//	 */
//	public txtReader()
//	{
//		missingFiles = null;
//		files = new ArrayList<File>();
//		JFileChooser fc = new JFileChooser();
//		fc.showOpenDialog(null);
//		for(File f : fc.getSelectedFiles())
//		{
//			files.add(f);
//		}
//	}
	
	/**
	 * displays the options available in the program then runs the selected option.
	 */
	public void Options()
	{
		String[] buttonsMM = {"List Chapters", "Quiz Standard", "Quiz Inverted", "Change Chapter Selection", "Report", "Exit"};
		int r = JOptionPane.showOptionDialog(null, "Select an Action:", "JQuiz", 1, JOptionPane.QUESTION_MESSAGE, null, buttonsMM, 0);
		run(r);
	}
	
	/**
	 * runs appropriate command
	 * @param r number associated to the command
	 */
	private void run(int r)
	{
		if(r == 0)
		{
			list();
		}
		else if(r == 1)
		{
			quiz(false);
		}
		else if(r == 2)
		{
			quiz(true);
		}
		else if(r == 3)
		{
			change();
		}
		else if(r == 4)
		{
			report();
		}
		else if(r == 5)
		{
			exit();
		}
		else
		{
			System.out.println("There is a button called exit you know.");
			exit();
		}
	}
	
	/**
	 * shows the currently selected chapters
	 */
	private void list() 
	{
		JOptionPane.showMessageDialog(null, currentlySelected.toString());
	}

	/**
	 * sets the continue boolean to false in order to end the loop.
	 */
	private void exit() 
	{
		cont = false;
	}
	
	/**
	 * returns if the program should continue running.
	 * @return if the program should continue running.
	 */
	public boolean Continue()
	{
		return cont;
	}

	/**
	 * Displays contents of report file in JOptionPane
	 */
	private void report() 
	{
		try {
			fr = new FileReader(rep);
			Scanner rep = new Scanner(fr);
			String report = "";
			while(rep.hasNextLine())
			{
				report += rep.nextLine() + "\n";
			}
			JOptionPane.showMessageDialog(null, report);
			rep.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "No report has yet been generated, please take at least one quiz.", "Error: Report file not found", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Changes the selected chapters based on user input
	 */
	private void change() 
	{
		if(chapterSelection.size() > 1)
		{
			String sel = JOptionPane.showInputDialog("Please enter the chapters you would like to include in the format: \"1, 3, 8\"");
			ArrayList<String> s = new ArrayList<String>();
			
			//parses input string for list of chapters to add to list
			s.add(sel.substring(0, 1));
			sel = sel.substring(1);
			while(sel.contains(","))
			{
				String str = sel.substring(2, 3);
				
				//validating that the chapter has an associated text file
				if(!missingFiles.contains(Integer.parseInt(str)))
				{
					s.add(str);
				}
				else
				{
					System.out.println("File " + str + " is missing from the given directory and can not be added.");
				}
				
				//updates string
				sel = sel.substring(3);
			}
			
			//stores list of chapters to quiz on
			currentlySelected = s;
		}
	}

	/**
	 * Creates a list of chapters randomizes them, gets their list of questions and quizzes the user on them in a random order. Then generates a new report or one based on previous reports
	 * @param inversion the option to be quizzed by being given either the questions(if false) or the answers(if true)
	 */
	private void quiz(boolean inversion)
	{
		//array list of chapter lists
		ArrayList<ArrayList<String>> chap = new ArrayList<ArrayList<String>>();
		//array list of chapter answer lists
		ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();
		
		for(int i = 0; i < files.size(); i++)
		{
			//checks if the current file is in the list to be quizzed on
			String fileName = files.get(i).getName();
			//boolean test = currentlySelected.contains(fileName.substring(2, fileName.indexOf(".txt")));
			if(currentlySelected.contains(fileName.substring(2, fileName.indexOf(".txt"))))
			{
				File f = files.get(i);
				//chapter list
				ArrayList<String> c = new ArrayList<String>();
				c.add(f.getName().substring(0, f.getName().indexOf(".txt")));
				//chapter answer list
				ArrayList<String> a = new ArrayList<String>();
				a.add(f.getName().substring(0, f.getName().indexOf(".txt")));
				try {
					fr = new FileReader(f);
					Scanner sc = new Scanner(fr);
					//adds answers and "questions" to proper lists
					while(sc.hasNextLine())
					{
						String temp = sc.nextLine();
						int l = seqLoc(temp);
						c.add(temp.substring(0, l).trim());
						a.add(temp.substring(l + 1).trim());
					}
					sc.close();
				} catch (FileNotFoundException e) {
					System.out.println(e.getMessage());
				}
				//adds lists to chapter list
				chap.add(c);
				//adds answer lists to answer list
				ans.add(a);
			}
		}
		
		//variables used in the report
		int questions = 0;
		int correctAns = 0;
		
		//determine the order of the quiz randomly.
		ArrayList<Integer> ord = new ArrayList<Integer>();
		
		//gets list of chapters locations
		for(int i = 0; i < chap.size(); i++)
		{
			ord.add(i);
		}
		for(int i = 0; i < chap.size(); i++)
		{
			//randomly selects a location from list
			int r1 = (int)(Math.random() * ord.size());
			ArrayList<String> curChap = chap.get(ord.get(r1));
			ArrayList<String> curAns = ans.get(ord.get(r1));
			ArrayList<Integer> chapOrd = new ArrayList<Integer>();
			
			//gets list of question locations
			for(int j = 1; j < curChap.size(); j++)
			{
				chapOrd.add(j);
			}
			
			//randomly selects a question then displays it, after removes location from list
			for(int j = 1; j < curChap.size(); j++)
			{
				int r2 = (int)(Math.random() * chapOrd.size());
				String q = curChap.get(chapOrd.get(r2));
				String a = curAns.get(chapOrd.get(r2));
				if(inversion)
				{
					String r =  JOptionPane.showInputDialog(null, a, curAns.get(0), JOptionPane.QUESTION_MESSAGE);
					if(r.equals(q))
					{
						JOptionPane.showMessageDialog(null, "Good Job.");
						correctAns++;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Incorrect. The correct answer was: " + q);
					}
				}
				else
				{
					String r = JOptionPane.showInputDialog(null, q, curAns.get(0), JOptionPane.QUESTION_MESSAGE);
					if(r.equals(a))
					{
						JOptionPane.showMessageDialog(null, "Good Job.");
						correctAns++;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Incorrect. The correct answer was: " + a);
					}
				}
				
				questions++;
				
				chapOrd.remove(r2);
			}
			
			//removes chapter location from list
			ord.remove(r1);
		}
		
		try {
			if(rep.exists()) {
				File report = rep.getAbsoluteFile();
				Scanner s = new Scanner(report);
				int counter = 0;
				String l1 = "";
				String l2 = "";
				while(s.hasNext())
				{
					if(counter % 2 == 0)
					{
						l1 = s.nextLine();
					}
					else
					{
						l2 = s.nextLine();
					}
					counter++;
				}
				s.close();
				PrintWriter pw = new PrintWriter(rep.getAbsolutePath());
				try
				{
				double ca = Double.parseDouble(l1.substring(15));
				double q = Double.parseDouble(l2.substring(27));
				ca += correctAns;
				q += questions;
				pw.println("****************************************");
				if(questions == 0)
				{
					pw.println("Accuracy: 0");
				}
				else
				{
					pw.println("Accuracy: " + ((ca / q) * 100));
				}
				pw.println("Total Correct: " + ca);
				pw.println("Total number of questions: " + q);
				}
				catch(IndexOutOfBoundsException i)
				{
					JOptionPane.showMessageDialog(null, "There has been an error in recovering data from the report.txt file. The file will be recreated using only your current quizzes results.", "Error: report.txt is empty", JOptionPane.ERROR_MESSAGE);
				}
				pw.close();
			}
			else
			{
				PrintWriter pw = new PrintWriter(rep.getAbsolutePath());
				pw.println("****************************************");
				if(questions == 0)
				{
					pw.println("Accuracy: 0");
				}
				else
				{
					pw.println("Accuracy: " + ((double)correctAns / (double)questions));
				}
				pw.println("Number Correct: " + correctAns);
				pw.println("Total number of questions: " + questions);
				pw.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * finds location of first "|"
	 * @param toSearch the string to search
	 * @return the index of the first "|"
	 */
	private int seqLoc(String toSearch) {
		return toSearch.indexOf("|");
	}

	public static void main(String[] args)
	{
//		//Test Case
//		txtReader errorExpected = new txtReader("C:\\Users\\ianma\\Desktop\\College\\Sp19\\fcTest", 1);
//		while(errorExpected.Continue())
//		{
//			errorExpected.Options();
//		}
		try
		{
			JOptionPane.showMessageDialog(null, "Please select the ch1.txt file");
			JFileChooser fc = new JFileChooser();
			fc.showOpenDialog(null);
			String path = "";
			path = fc.getSelectedFile().getAbsolutePath();
			path = path.substring(0, path.indexOf("ch1.txt"));
			System.out.println(path);
			String resp = JOptionPane.showInputDialog("What is the last chapter listed? (The one with the highest number)");
			int highChap = Integer.parseInt(resp);
			txtReader user = new txtReader(path, highChap);
			while(user.Continue())
			{
				user.Options();
			}
		}
		catch(NullPointerException n)
		{
			JOptionPane.showMessageDialog(null, "Errors may occur on later executions of the program, try not to exit the program early.", "Error: Early Exit", JOptionPane.ERROR_MESSAGE);
		}
	}
}
