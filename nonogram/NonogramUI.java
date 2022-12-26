package nonogram;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * A text-based user interface to a Nonogram puzzle.
 *
 */

public class NonogramUI {
	/**
	 * Default constructor
	 */



	public NonogramUI() {
		scan = new Scanner(System.in);					 // Scanner for user input
		Scanner fs = null;								 // Scanner for file input
		try {											 // Try to open the file
			fs = new Scanner(new File(NGFILE));			 // Open the file
		} 	catch (FileNotFoundException e) {			 // If the file is not found
			System.out.println(NGFILE + "not found");// Print an error message
		}


		puzzle = new Nonogram(fs);	// Create a new Nonogram puzzle
		movesHistory = new Stack<>();					 // Create a new stack for moves history
		movesPerPlayHistory = new Stack<>();			 // Create a new stack for moves per play history. E.g. Multi-moves are integers greater than 1.
	}

	public boolean puzzleSolve(){


  boolean isSolve=		puzzle.isSolved();

  return  isSolve;
	}

	/**
	 * A string representation of a Nonogram puzzle suitable for console display
	 * If showFullOnly is set, the unknown cells are shown as empty.
	 *
	 * @param showFullOnly show all non-full cells as empty
	 * @return a string representation of the puzzle for display
	 */
	public String display(boolean showFullOnly) {
		// collect the nums for the rows and columns
		int      numRows      = puzzle.getNumRows(); 		// Create a variable for the number of rows
		int      numCols      = puzzle.getNumCols();  		// Create a variable for the number of columns
		int[][] rowNums       = new int[numRows][]; 		// Create array for row numbers
		int[][] colNums       = new int[numCols][]; 		// Create array for column numbers
		int     maxRowNumsLen = 0; 							// Declare a variable for the maximum length of the row numbers
		int     maxColNumsLen = 0;  						// Declare a variable for the maximum length of the column numbers
		for (int row=0; row<numRows; row++) { 				// For each row
			rowNums[row] = puzzle.getRowNums(row);  		// Get the row numbers
			if (rowNums[row].length > maxRowNumsLen)  		// If the length of the row numbers is greater than the max
				maxRowNumsLen = rowNums[row].length;  		// Update the max length
		}
		for (int col=0; col<numRows; col++) {			 	// For each column
			colNums[col] = puzzle.getColNums(col);		 	// Get the column numbers
			if (colNums[col].length > maxColNumsLen)	 	// If the length of the column numbers is greater than the max
				maxColNumsLen = colNums[col].length;	 	// Update the max length
		}
		StringBuffer sb = new StringBuffer(); 			 	// Create a new string buffer to hold the puzzle
		sb.append(" ".repeat(2*maxRowNumsLen+4));	 	// Add the appropriate number of spaces to the buffer
		sb.append("-".repeat(numCols));					 	// Add the appropriate number of dashes to the buffer
		sb.append("\n");								 	// Add a new line to the buffer
		for (int i=0; i<maxColNumsLen; i++) {			 	// For each row of column numbers
			sb.append(" ".repeat(2*maxRowNumsLen+4)); // Add the appropriate number of spaces to the buffer
			for (int col=0; col<numCols; col++)				// For each column
				if (i < colNums[col].length)				// If the row is less than the length of the column numbers
					sb.append(numAsChar(colNums[col][i]));	// Add the number as a character to the buffer
				else										// Otherwise
					sb.append(" ");							// Add a space to the buffer
			sb.append("\n");								// Add a new line to the buffer
		}
		sb.append(" ".repeat(2*maxRowNumsLen+4));		// Add the appropriate number of spaces to the buffer
		sb.append("-".repeat(numCols));						// Add the appropriate number of dashes to the buffer
		sb.append("\n");									// Add a new line to the buffer
		sb.append(" ".repeat(2*maxRowNumsLen+4));		// Add the appropriate number of spaces to the buffer
		for (int col=0; col<numCols; col++)					// For each column
			sb.append(alertChar(false, col));			// Add the alert character to the buffer
		sb.append("\n");									// Add a new line to the buffer
		sb.append(" ".repeat(2*maxRowNumsLen+4));		// Add the appropriate number of spaces to the buffer
		for (int col=0; col<numCols; col++)					// For each column
			sb.append(numAsChar(col));						// Add the column number as a character to the buffer
		sb.append("\n\n");									// Add two new lines to the buffer

		//
		for (int row=0; row<numRows; row++) {				// For each row
			sb.append("[");									// Add a left bracket to the buffer
			for (int i=0; i<rowNums[row].length; i++) {		// For each row number
				sb.append(numAsChar(rowNums[row][i]));		// Add the row number as a character to the buffer
				if (i<rowNums[row].length-1)				// If the row number is not the last
					sb.append(" ");							// Add a space to the buffer
			}
			sb.append("]");									// Add a right bracket to the buffer
			sb.append(" ".repeat(2*(maxRowNumsLen-rowNums[row].length)));	// Add the appropriate number of spaces to the buffer

			sb.append(alertChar(true, row));				// Add the alert character to the buffer
			sb.append(numAsChar(row) + " ");				// Add the row number as a character to the buffer
			sb.append(seqAsChar(puzzle.getRowSequence(row), showFullOnly) + "\n");	// Add the row sequence as a character to the buffer
		}
		sb.append("\n");									// Add a new line to the buffer
		return sb.toString();								// Return the string buffer as a string
	}



	/**
	 * A string representation of a Nonogram puzzle suitable for console display
	 *
	 * @return a string representation of the puzzle for display
	 */
	public String display() {
		return display(false);
	}



	/**
	 * Provides the character to annotate each row to indicate if it is valid or solved
	 *
	 * @param isRow a switch to indicate this is a row (true) or column (false)
	 * @param idx the row or column number
	 * @return the character to display
	 */
	public char alertChar(boolean isRow, int idx) {
		if (isRow && (idx < 0 || idx >= puzzle.getNumRows()))
			throw new IllegalArgumentException("invalid idx for row (" + idx + ")");
		else if (!isRow && (idx < 0 || idx >= puzzle.getNumCols()))
			throw new IllegalArgumentException("invalid idx for col (" + idx + ")");
		if (isRow) {
			if (puzzle.isRowSolved(idx))
				return SOLVED_CHAR;
			else if (!puzzle.isRowValid(idx))
				return INVALID_CHAR;
			else
				return ' ';
		} else { // is col
			if (puzzle.isColSolved(idx))
				return SOLVED_CHAR;
			else if (!puzzle.isColValid(idx))
				return INVALID_CHAR;
			else
				return ' ';
		}
	}



	/**
	 * Main control loop.  This displays the puzzle, then enters a loop displaying a menu,
	 * getting the user command, executing the command, displaying the puzzle and checking
	 * if further moves are possible
	 */


	public void menu() {
		String command = "";
		System.out.println(display(puzzle.isSolved()));
		while (!command.equalsIgnoreCase("Quit") && !puzzle.isSolved())  {
			displayMenu();
			command = getCommand();
			execute(command);
			if (command.equalsIgnoreCase("Quit"))
				break;
			System.out.println(display(puzzle.isSolved()));
			if (puzzle.isSolved())
				System.out.println("Well done! You solved the puzzle!");
		}
	}

	public void menuP() {
//		String command = "";
		System.out.println(display(puzzle.isSolved()));
//		while (!command.equalsIgnoreCase("Quit") && !puzzle.isSolved())  {
			displayMenu();
//			command = getCommand();
//			execute(command);
//			if (command.equalsIgnoreCase("Quit"))
//				break;
//			System.out.println(display(puzzle.isSolved()));

			if (puzzle.isSolved())
				System.out.println("Well done! You solved the puzzle!");



//			break;
		}




	/**
	 * Display the user menu
	 */
	public void displayMenu()  {
		System.out.println("Commands are:");
		System.out.println("   Help               [H]");
		System.out.println("   Move               [M]");
		System.out.println("   Row multi move     [R]");
		System.out.println("   Col multi move     [C]");
		System.out.println("   Undo assignment    [U]");
		System.out.println("   Restart puzzle [Clear]");
		System.out.println("   Save to file    [Save]");
		System.out.println("   Load from file  [Load]");
		System.out.println("   To end program  [Quit]");
	}



	/**
	 * Get the user command
	 *
	 * @return the user command string
	 */
	public String getCommand() {
		System.out.print ("Enter command: ");
		return scan.nextLine();
	}



	/**
	 * Execute the user command string
	 *
	 * @param command the user command string
	 */
	public void execute(String command) {
		if (command.equalsIgnoreCase("Quit")) {
			System.out.println("Program closing down");
			System.exit(0);
		} else if (command.equalsIgnoreCase("H")) {
			help();
		} else if (command.equalsIgnoreCase("M")) {
			move();
		} else if (command.equalsIgnoreCase("R")) {
			rowMultiMove();
		} else if (command.equalsIgnoreCase("C")) {
			colMultiMove();
		} else if (command.equalsIgnoreCase("U")) {
			undo();
		} else if (command.equalsIgnoreCase("Clear")) {
			Clear();
		} else if (command.equalsIgnoreCase("Save")) {
			save();
		} else if (command.equalsIgnoreCase("Load")) {
			load();
		} else {
			System.out.println("Unknown command (" + command + ")");
		}
	}






	public void saveFromGUI(String fileName) {
		// Prompt user for file name


		// Try to assign file name to file then write to file
		try {
			FileWriter writer = new FileWriter(fileName);

			// write movesHistory
			writer.write(movesHistory.toString());

			// new line to separate movesHistory and movesPerPlayHistory
			writer.write("\n");

			// write movesPerPlayHistory
			writer.write(movesPerPlayHistory.toString());

			// close file
			writer.close();

			// Print success message
			System.out.println("File saved successfully");
		}
		// Catch exceptions
		catch (IOException e) {
			System.out.println("Error writing to file");
		}
	}




	/**
	 * Save the game. Writes 'movesHistory' and 'movesPerPlayHistory' to a file which can be loaded later
	 */
	public void save() {
		// Prompt user for file name
		System.out.println("Enter file name to save to: ");

		// Get file name using scanner
		String fileName = scan.nextLine();

		// Try to assign file name to file then write to file
		try {
			FileWriter writer = new FileWriter(fileName);

			// write movesHistory
			writer.write(movesHistory.toString());

			// new line to separate movesHistory and movesPerPlayHistory
			writer.write("\n");

			// write movesPerPlayHistory
			writer.write(movesPerPlayHistory.toString());

			// close file
			writer.close();

			// Print success message
			System.out.println("File saved successfully");
		}
		// Catch exceptions
		catch (IOException e) {
			System.out.println("Error writing to file");
		}
	}




	public void loadFromGui() {
		// Prompt user for file name


		String fileName=JOptionPane.showInputDialog("Enetr File Name");

		// Get file name using scanner


		// Try to assign file name to file then read from file
		try {
			FileReader reader = new FileReader(fileName);

			// Use scanner to read file
			Scanner fileScan = new Scanner(reader);

			// Get saved moves from the first line of file contents and assign to savedMoves
			String savedMoves = fileScan.nextLine();

			// Get saved moves per play from the second line of file contents and assign to savedMovesPerPlay
			String savedMovesPerPlay = fileScan.nextLine();

			// Extract numbers from savedMoves using stringToDigitQueue method
			Queue<Integer> extractedMoves = stringToDigitQueue(savedMoves);

			// Extract numbers from savedMovesPerPlay using stringTo stringToDigitStack method
			Stack<Integer> extractedMovesPerPlay = stringToDigitStack(savedMovesPerPlay);

			// Clear current history of moves and moves per play before assigning saved moves and moves per play
			movesHistory.clear();
			movesPerPlayHistory.clear();

			// Clear current puzzle
			puzzle.clear();

			// Assign extractedMovesPerPlay to movesPerPlayHistory
			movesPerPlayHistory = extractedMovesPerPlay;

			// Pop first three numbers from extractedMoves and assign to new Assign object. Reapply to puzzle. Repeat until extractedMoves is empty
			while (!extractedMoves.isEmpty()) {

				// Pop first three numbers from extractedMoves and assign to new Assign object
				Assign assign = new Assign(extractedMoves.remove(), extractedMoves.remove(), extractedMoves.remove());

				// Reapply assign to puzzle
				puzzle.setState(assign);

				// push the move onto the history stack
				movesHistory.push(assign);
			}
		}
		catch (IOException e) {
			System.out.println("Error reading from file");
			message("Error Reading File");

		}
	}

	public void message(String message){


		JOptionPane.showMessageDialog(new JOptionPane(), message);
	}



	/**
	 * Loads game from file.
	 * Retrieves saved moves and moves per play. Possible to undo all loaded single and multi moves.
	 */
	public void load() {
		// Prompt user for file name
		System.out.println("Enter file name to load from: ");

		// Get file name using scanner
		String fileName = scan.nextLine();

		// Try to assign file name to file then read from file
		try {
			FileReader reader = new FileReader(fileName);

			// Use scanner to read file
			Scanner fileScan = new Scanner(reader);

			// Get saved moves from the first line of file contents and assign to savedMoves
			String savedMoves = fileScan.nextLine();

			// Get saved moves per play from the second line of file contents and assign to savedMovesPerPlay
			String savedMovesPerPlay = fileScan.nextLine();

			// Extract numbers from savedMoves using stringToDigitQueue method
			Queue<Integer> extractedMoves = stringToDigitQueue(savedMoves);

			// Extract numbers from savedMovesPerPlay using stringTo stringToDigitStack method
			Stack<Integer> extractedMovesPerPlay = stringToDigitStack(savedMovesPerPlay);

			// Clear current history of moves and moves per play before assigning saved moves and moves per play
			movesHistory.clear();
			movesPerPlayHistory.clear();

			// Clear current puzzle
			puzzle.clear();

			// Assign extractedMovesPerPlay to movesPerPlayHistory
			movesPerPlayHistory = extractedMovesPerPlay;

			// Pop first three numbers from extractedMoves and assign to new Assign object. Reapply to puzzle. Repeat until extractedMoves is empty
			while (!extractedMoves.isEmpty()) {

				// Pop first three numbers from extractedMoves and assign to new Assign object
				Assign assign = new Assign(extractedMoves.remove(), extractedMoves.remove(), extractedMoves.remove());

				// Reapply assign to puzzle
				puzzle.setState(assign);

				// push the move onto the history stack
				movesHistory.push(assign);
			}
		}
		catch (IOException e) {
			System.out.println("Error reading from file");
		}
	}





	/**
	 * Extract numbers from string and return as stack
	 *
	 * @param string - the string to extract numbers from
	 * @return digits - the stack of extracted numbers
	 */
	public Stack<Integer> stringToDigitStack(String string) {

		// Create a new Stack to store the digits
		Stack<Integer> digits = new Stack<>();

		// Iterate through each character in the string
		for (int i = 0; i < string.length(); i++) {

			// Check if the current character is a digit
			if (Character.isDigit(string.charAt(i))) {

				// If it is a digit, convert it to an int and push it onto the stack
				digits.push(Character.getNumericValue(string.charAt(i)));
			}
		}
		// Return the stack of digits
		return digits;
	}





	/**
	 * Extracts numbers from a string and returns them as a queue of integers
	 */
	public Queue<Integer> stringToDigitQueue(String str) {
		// Create a new LinkedList to store the digits
		Queue<Integer> digits = new LinkedList<>();

		// Iterate through each character in the string
		for (int i = 0; i < str.length(); i++) {
			// Check if the current character is a digit
			if (Character.isDigit(str.charAt(i))) {
				// If it is a digit, convert it to an int and add it to the queue
				digits.add(Character.getNumericValue(str.charAt(i)));
			}
		}

		// Return the queue of digits
		return digits;
	}



	/**
	 * Clear the board - restart the puzzle
	 */
	public void Clear() {
		NonogramUI ui = new NonogramUI();
		ui.menu();
	}



	/**
	 * Display some useful help text on the puzzle
	 */
	public void help() {
		System.out.println("Nonogram is a puzzle where you must colour in/fill in the grid according to the patterns");
		System.out.println("of contiguous full cells given in the rows and columns.  Full cells are shown as '" + FULL_CHAR + "',");
		System.out.println("unknown cells as '" + UNKNOWN_CHAR + "', and cells you are sure are empty as '" + EMPTY_CHAR + "'.");
		System.out.println("If a row or column is invalid (doesn't match the pattern) this will be marked with a '" + INVALID_CHAR + "'; ");
		System.out.println("a solved row or column is marked with a '" + SOLVED_CHAR + "', but it may still be wrong because of the other");
		System.out.println("columns or rows - keep trying!");
		System.out.println("");
	}


	int setCol,setRow;
	char setChar;

	public void setState(int row,int col,char ch){

		this.setRow=row;
		this.setCol=col;
		this.setChar=ch;

	}


	private int getCol(){

		return  this.setCol;
	}
	private int getRow(){

		return this.setRow;
	}
	private char getChar(){

		return this.setChar;

	}

	/**
	 * Make a move
	 */


	private void move() {
		Assign userMove = getUserMove();
		if (userMove == null) {
			System.out.println("invalid user move");
			return;
		}
		// push the number '1' onto represent a single move
		movesPerPlayHistory.push(1);

		// push the move onto the history stack
		movesHistory.push(userMove);

		// make the move
		puzzle.setState(userMove);
	}


	private Assign getUserMove() {
		int row   = getInt("Enter row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
		if ((row<0) || (row>(puzzle.getNumRows()-1)))
			return null;
		int col   = getInt("Enter col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
		if ((col<0) || (col>(puzzle.getNumCols()-1)))
			return null;
		char c    = getChar("Enter state ('"+EMPTY_CHAR+"','"+FULL_CHAR+"' or '"+UNKNOWN_CHAR+"'): ");
		if (!isValidStateChar(c))
			return null;
		int state = stateFromChar(c);
		return new Assign(row, col, state);
	}



	public void moveFromGui() {
		Assign userMove = getUserMoveFromGui();
		if (userMove == null) {
			System.out.println("invalid user move");
			return;
		}
		// push the number '1' onto represent a single move
		movesPerPlayHistory.push(1);

		// push the move onto the history stack
		movesHistory.push(userMove);

		// make the move
		puzzle.setState(userMove);
	}



	/**
	 * Undo the last move
	 */
	public void undo() {

		// check if there are any moves to undo
		if (movesHistory.isEmpty() || movesPerPlayHistory.isEmpty()) {

			// if either stack is empty, print an error message and return to the menu
			System.out.println("No moves to undo");
			return;
		}
		// get the number of moves to undo
		int movesToUndo = movesPerPlayHistory.pop();

		// undo this number of moves
		for (int i = 0; i < movesToUndo; i++) {
			movesHistory.pop();
		}
		// clear the puzzle
		puzzle.clear();

		// reapply the moves left on the stack
		for (Assign move : movesHistory) {
			puzzle.setState(move);
		}
	}



	/**
	 * Get the user's move
	 *
	 * @return the user move
	 */
	public Assign getUserMoveFromGui() {
		int row   = getRow()-1;

//				getInt("Enter row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
		if ((row<0) || (row>(puzzle.getNumRows()-1)))
			return null;
		int col   = getCol()-1;

//				getInt("Enter col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
		if ((col<0) || (col>(puzzle.getNumCols()-1)))
			return null;
		char c    =getChar();

//				getChar("Enter state ('"+EMPTY_CHAR+"','"+FULL_CHAR+"' or '"+UNKNOWN_CHAR+"'): ");
		if (!isValidStateChar(c))
			return null;
		int state = stateFromChar(c);
		return new Assign(row, col, state);
	}


	/**
	 * Make a multi-column row move
	 */
	public void rowMultiMove() {

		ArrayList<Assign> list = getRowMultiUserMove();
		if (list == null) {
			System.out.println("invalid user move list");
			return;
		}
		// push the number of moves onto the stack
		movesPerPlayHistory.push(list.size());

		// push each move onto the history stack
		for (Assign eachMove : list) {
			movesHistory.push(eachMove);
		}

		// make the moves
		for (Assign a : list)
			puzzle.setState(a);
	}



	/**
	 * Get the user's multi-column row move
	 *
	 * @return the move as list of moves (or null on error)
	 */
	public ArrayList<Assign> getRowMultiUserMove() {
		int row = getInt("Enter row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
		if ((row<0) || (row>(puzzle.getNumRows()-1)))
			return null;
		int first = getInt("Enter first col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
		if ((first<0) || (first>(puzzle.getNumCols()-1)))
			return null;
		int last  = getInt("Enter last col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
		if ((last<0) || (last>(puzzle.getNumCols()-1)))
			return null;
		char c    = getChar("Enter state ('"+EMPTY_CHAR+"','"+FULL_CHAR+"' or '"+UNKNOWN_CHAR+"'): ");
		if (!isValidStateChar(c))
			return null;
		int state = stateFromChar(c);
		int start = (first <= last) ? first : last;
		int end   = (first <= last) ? last  : first;
		ArrayList<Assign> list = new ArrayList<>();
		for (int col=start; col<=end; col++)
			list.add(new Assign(row, col, state));
		return list;
	}



	/**
	 * Make a multi-row column move
	 */
	public void colMultiMove() {

		ArrayList<Assign> list = getColMultiUserMove();
		if (list == null) {
			System.out.println("invalid user move list");
			return;
		}
		// push the number of moves onto the stack
		movesPerPlayHistory.push(list.size());

		// push each move onto the history stack
		for (Assign eachMove : list) {
			movesHistory.push(eachMove);
		}

		// make the moves
		for (Assign a : list)
			puzzle.setState(a);
	}



	/**
	 * Get the user's multi-row column move
	 *
	 * @return the move as an array-list of moves (or null on error)
	 */
	public ArrayList<Assign> getColMultiUserMove() {
		int col = getInt("Enter col (0 to " + numAsChar(puzzle.getNumCols()-1) + "): ");
		if ((col<0) || (col>(puzzle.getNumCols()-1)))
			return null;
		int first = getInt("Enter first row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
		if ((first<0) || (first>(puzzle.getNumRows()-1)))
			return null;
		int last  = getInt("Enter last row (0 to " + numAsChar(puzzle.getNumRows()-1) + "): ");
		if ((last<0) || (last>(puzzle.getNumRows()-1)))
			return null;
		char c    = getChar("Enter state ('"+EMPTY_CHAR+"','"+FULL_CHAR+"' or '"+UNKNOWN_CHAR+"'): ");
		if (!isValidStateChar(c))
			return null;
		int state = stateFromChar(c);
		int start = (first <= last) ? first : last;
		int end   = (first <= last) ? last  : first;
		ArrayList<Assign> list = new ArrayList<>();
		for (int row=start; row<=end; row++)
			list.add(new Assign(row, col, state));
		return list;
	}



	/**
	 * Get an integer from the user.
	 * If the user enters a non-integer, then give an error message and prompt again.
	 *
	 * @param prompt a string to prompt the user
	 * @return the integer (or -1 on error)
	 */
	public int getInt(String prompt) {
		if (prompt == null)
			throw new IllegalArgumentException("prompt cannot be null");
		System.out.print (prompt);
		if (!scan.hasNext()) {
			scan.nextLine(); // clear the line
			return -1;
		}
		char c   = scan.next().charAt(0);

		// check user entry is a single digit
		if (Character.isDigit(c)) {

			// if so, convert entry to integer, clear the line and return the integer
			int  num = numFromChar(c);
			scan.nextLine(); // clear the line
			return num;
		}
		// else give error message and display prompt again
		System.out.println("Error: invalid character '" + c + "'");
		return getInt(prompt);
	}



	/**
	 * Get a character from the user
	 *
	 * @param prompt a string to prompt the user
	 * @return the character (or '?' on error)
	 */
	public char getChar(String prompt) {
		if (prompt == null)
			throw new IllegalArgumentException("prompt cannot be null");
		System.out.print (prompt);
		if (!scan.hasNext()) {
			scan.nextLine(); // clear the line
			return '?'; // sentinel, not equal to one of the established chars
		}
		char c = scan.next().charAt(0);
		scan.nextLine(); // clear the line
		return c;
	}



	/**
	 * Get a number from a character representation (0-9A-Za-z)
	 *
	 * @param c the character representation
	 * @return the integer representation (or -1 on error)
	 */
	public static int numFromChar(char c) {
		final String  regex = "[0-9A-Za-z]";
		final Pattern pat   = Pattern.compile(regex);
		if (!pat.matcher(""+c).matches())
			throw new IllegalArgumentException("c must be " + regex);
		if ((c >= '0') && (c <= '9'))
			return (int) (c-'0');
		else if ((c >= 'A') && (c <= 'Z'))
			return (int) (c-'A'+10);
		else if ((c >= 'a') && (c <= 'z'))
			return (int) (c -'a'+36);
		else
			return -1; // should never happen
	}



	/**
	 * Get a character representation (0-9A-Za-z) of a number
	 *
	 * @param i the integer (must be positive)
	 * @return the character representation (or '?' on error)
	 */
	public static char numAsChar(int i) {
		if (i < 0)
			throw new IllegalArgumentException("i must be >= 0 (" + i + ")");
		if ((i >= 0) && (i < 10))
			return (char) ('0'+i);
		else if ((i >= 10) && (i < 36))
			return (char) ('A'+i-10);
		else if ((i >= 36) && (i < 62))
			return (char) ('a'+i-36);
		else
			return '?';
	}



	/**
	 * Check if a character represents a valid cell state
	 *
	 * @param c the character
	 * @return true if this character represents a vaild cell state, otherwise false
	 */
	public static boolean isValidStateChar(char c) {
		if ((c == FULL_CHAR)  ||
				(c == EMPTY_CHAR) ||
				(c == UNKNOWN_CHAR))
			return true;
		else
			return false;
	}



	/**
	 * Convert a cell state sequence string into a string representation using the
	 * display characters. If showFullOnly is set, the unknown cells are shown as empty.
	 *
	 * @param seq the cell state sequence
	 * @param showFullOnly show all non-full cells as empty
	 * @return the sequence ready for display
	 */
	public static String seqAsChar(String seq, boolean showFullOnly) {
		if (seq == null)
			throw new IllegalArgumentException("seq cannot be null");
		if (seq.length() < Nonogram.MIN_SIZE)
			throw new IllegalArgumentException("seq cannot be shorter than " + Nonogram.MIN_SIZE);
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<seq.length(); i++) {
			int state = Nonogram.UNKNOWN;
			try {
				state = Integer.parseInt(seq.substring(i, i+1));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("seq contains non number (" + seq.charAt(i) + ") in seq["+ i +"]");
			}
			if (!Cell.isValidState(state))
				throw new IllegalArgumentException("invalid state (" + state + ") in seq["+ i +"]");
			if (!showFullOnly)
				sb.append(stateAsChar(state));
			else
				sb.append(state == Nonogram.FULL ? NonogramUI.FULL_CHAR : " ");
		}
		return sb.toString();
	}



	/**
	 * Convert a cell state sequence string into a string representation using the
	 * display characters.
	 *
	 * @param seq the cell state sequence
	 * @return the sequence ready for display
	 */
	public static String seqAsChar(String seq) {
		return seqAsChar(seq, false);
	}



	/**
	 * Convert an individual cell state into a display character
	 *
	 * @param state the cell state
	 * @return the character ready for display
	 */
	public static char stateAsChar(int state) {
		if (!Cell.isValidState(state))
			throw new IllegalArgumentException("invalid state (" + state + ")");
		if (state == Nonogram.FULL)
			return FULL_CHAR;
		else if (state == Nonogram.EMPTY)
			return EMPTY_CHAR;
		else
			return UNKNOWN_CHAR;
	}



	/**
	 * Convert a display character into a Nonogram cell state
	 *
	 * @param c the display character
	 * @return the cell state
	 */
	public static int stateFromChar(char c) {
		if (!isValidStateChar(c))
			throw new IllegalArgumentException("invalid state char (" + c + ")");
		if (c == FULL_CHAR)
			return Nonogram.FULL;
		else if (c == EMPTY_CHAR)
			return Nonogram.EMPTY;
		else
			return Nonogram.UNKNOWN;
	}



	public static void main(String[] args) {
		NonogramUI ui = new NonogramUI();
		ui.menu();
	}

	public Scanner scan = null;
	public Nonogram puzzle = null;
	public Stack<Assign> movesHistory = null;
	public Stack<Integer> movesPerPlayHistory = null;
	public static final String NGFILE   = "nons/tiny.non";
	public static final char EMPTY_CHAR   = 'X';
	public static final char FULL_CHAR    = '@'; // or use '\u2588'
	public static final char UNKNOWN_CHAR = '.';
	public static final char INVALID_CHAR = '?';
	public static final char SOLVED_CHAR  = '*';


}

