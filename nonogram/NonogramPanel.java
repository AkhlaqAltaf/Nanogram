package nonogram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Queue;
import java.util.*;


/**
 * Nonogram GUI Panel
 */

@SuppressWarnings({ "deprecation"})
public class NonogramPanel extends JPanel implements Observer {

    private  JLabel label;
    NonogramUI ui=new NonogramUI() ;
     public NonogramPanel() {


         System.out.println("Row"+row+"Column"+col);

         // Initialize scanner with nonogram game file as argument.


         try {
             scanFile = new Scanner(new File(GAME_FILE));
         }


         // If file not found display pop up error message.
         catch (FileNotFoundException e) {
             JOptionPane.showMessageDialog(null, "File not found: " + GAME_FILE);
         }



         // Instantiate new Nonogram puzzle and pass it puzzle file via scanner.
         nonogram = new Nonogram(scanFile);

         // Get puzzle size from file for setting up grid.
         int numRows = nonogram.getNumRows();
         int numCols = nonogram.getNumCols();

         // Add observer to puzzle so that panel can be updated when puzzle is updated.
         nonogram.addObserver(this);

         // Initialize grid and cells array with a GridLayout and PanelCell objects


         grid = new JPanel(new GridLayout(numRows+1, numCols+1));


         cells = new PanelCell[numRows+1][numCols+1];



         // Create PanelCell objects and add them to grid.
         for (row=0; row<=numRows; row++) {










             for (col=0; col<=numCols; col++) {




                 if (col==1){



                     switch (row){


                         case 1:
                             grid.add(new JLabel(String.valueOf("   [1  2]   ")));
                             break;


                         case 2:

                             grid.add(new JLabel(String.valueOf("  [2  2]   ")));
                             break;

                         case 3:
                             grid.add(new JLabel(String.valueOf("  [2]   ")));
                             break;

                         case 4:

                             grid.add(new JLabel(String.valueOf("  [1  1  1]   ")));

                             break;

                         case 5:

                             grid.add(new JLabel(String.valueOf("   [1]   ")));

                             break;

                     }



                 }






                if(row>0&&col>0){

                     cells[row][col] = new PanelCell(this, row, col);


                     grid.add(cells[row][col]);


                 }
                 else{




                     switch (col){

                 case 0:
                     grid.add(new JLabel(String.valueOf("     ")));
                     break;
                         case 1:
                             grid.add(new JLabel(String.valueOf("   [4]   ")));
                             break;
                         case 2:

                             grid.add(new JLabel(String.valueOf("  [2]   ")));
                             break;

                         case 3:
                             grid.add(new JLabel(String.valueOf("  [1]   ")));
                             break;

                         case 4:

                             grid.add(new JLabel(String.valueOf("  [2]   ")));

                             break;

                         case 5:

                             grid.add(new JLabel(String.valueOf("   [2  2]   ")));

                             break;

                     }




                 }




             }
         }

         // Set layout and add grid to panel.
         setLayout(new BorderLayout());
         add(grid, BorderLayout.NORTH);




         /////For Done Get Solution


         JButton done = new JButton("Done");

         done.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent ae) {

                 if(ui.puzzleSolve()){


                     JOptionPane.showMessageDialog(new JOptionPane(), "Well done! you solved... ");

                 }

                 else{

                     JOptionPane.showMessageDialog(new JOptionPane(), "You can't Solve it yet....");

                 }
             }
         });

         // Set up buttons & status bar
         JButton clear = new JButton("Clear");

         clear.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent ae) {

                 ui.undo();

             }
         });

         JButton save = new JButton("Save");
         save.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent ae) {

             String fileName=    JOptionPane.showInputDialog("Enter File Name");

             ui.saveFromGUI(fileName);



             }
         });

         JButton load= new JButton("Load");
         load.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent ae) {

               ui.loadFromGui();
             }
         });


         JButton help = new JButton("Help");
         help.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent ae) {
        JFrame frame=new JFrame();

                 JTextArea area=new JTextArea();
area.setText("Nonogram is a puzzle where you must colour in/fill in the grid according to the patterns" +
        "of contiguous full cells given in the rows and columns.  Full cells are shown as '" + FULL_CHAR + "'," +
        "unknown cells as '\" + UNKNOWN_CHAR + \"', and cells you are sure are empty as '\" + EMPTY_CHAR + \"'." +
        "If a row or column is invalid (doesn't match the pattern) this will be marked with a '\" + INVALID_CHAR + \"'; " +
        "a solved row or column is marked with a '\" + SOLVED_CHAR + \"', but it may still be wrong because of the other" +
        "columns or rows - keep trying!");
area.setLineWrap(true);
area.setSize(400,400);
area.setEditable(false);
frame.setSize(500,500);
frame.add(area);
frame.setVisible(true);



             }
         });


//         JButton rowMultiMove = new JButton("Row_Multi_Move");
//         rowMultiMove.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent ae) {
//                 String fileName=JOptionPane.showInputDialog("Enter File Name For Loading");
//
//                 ui.rowMultiMove();
//             }
//         });


//         JButton colMultiMove = new JButton("Column_Multi_move");
//         colMultiMove.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent ae) {
//                 String fileName=JOptionPane.showInputDialog("Enter File Name For Loading");
//
//                 ui.colMultiMove();
//             }
//         });




//         JButton quit = new JButton("Quit");
//         quit.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent ae) {
//                 String fileName=JOptionPane.showInputDialog("Enter File Name For Loading");
//
//                 ui.execute("Quit");
//             }
//         });




         JPanel center = new JPanel(new GridLayout(3,4));
         center.add(done,0);
         center.add(clear,1);
//         center.add(undo, 2);
         center.add(save,2);
         center.add(help,3);
//         center.add(move,5);
//         center.add(rowMultiMove,6);
//         center.add(colMultiMove,7);

         center.add(load);
//         center.add(quit,9);
         add(center, BorderLayout.CENTER);
         status = new JTextArea();


         add(new JScrollPane(status), BorderLayout.SOUTH);
     }
//Finish the code method for the class `NonogramPanel' A Java class for the graphical representation of a Nonogram game.

    @Override
    public void update(Observable o, Object arg) {
        if (arg == null)
            throw new NonogramException("arg (Cell) is null");
        Cell cell = (Cell) arg;
        if (!cell.isFull())
            throw new NonogramException("Cell has not yet been assigned a single number");
        cells[cell.getRow()][cell.getCol()].setText(cell.toString());

    }

    public void message(String message){

//        JOptionPane.showMessageDialog(new JOptionPane(),message);

    }















//    void clear() {
//        for (int row = 1; row<= nonogram.getNumRows(); row++)
//            for (int col = 1; col<= nonogram.getNumCols(); col++)
//                cells[row][col].clear();
//        nonogram = new Nonogram(scanFile);
//        nonogram.addObserver(this);
//        movesHistory = new Stack<>();
//    }
//    private void undo() {
//        if (!movesHistory.isEmpty()) {
//            movesHistory.pop();
//            nonogram.clear();
//            for (Assign move : movesHistory) {
//                nonogram.setState(move);
//            }
//        }
//    }



    private void save() {
        // Prompt user for file name
        System.out.println("Enter file name to save to: ");
    
        // Get file name using scanner
        String fileName = scanUserInput.nextLine();

        // Try to assign file name to file then write to file
        try {
            FileWriter writer = new FileWriter(fileName);

            // write movesHistory
            writer.write(movesHistory.toString());

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
    private void load() {
        // Prompt user for file name
        System.out.println("Enter file name to load from: ");

        // Get file name using scanner
        String fileName = scanFile.nextLine();


        // Try to assign file name to file then read from file
        try {
            FileReader reader = new FileReader(fileName);

            // Use scanner to read file
            Scanner fileScan = new Scanner(reader);

            // Get saved moves from the first line of file contents and assign to savedMoves
            String savedMoves = fileScan.nextLine();

            // Extract numbers from savedMoves using stringToDigitQueue method
            Queue<Integer> extractedMoves = stringToDigitQueue(savedMoves);

            // Clear current history of moves and moves per play before assigning saved moves and moves per play
            movesHistory.clear();

            // Clear current puzzle
            nonogram.clear();


            // Pop first three numbers from extractedMoves and assign to new Assign object. Reapply to puzzle. Repeat until extractedMoves is empty
            while (!extractedMoves.isEmpty()) {

                // Pop first three numbers from extractedMoves and assign to new Assign object
                Assign assign = new Assign(extractedMoves.remove(), extractedMoves.remove(), extractedMoves.remove());

                // Reapply assign to puzzle
                nonogram.setState(assign);

                // push the move onto the history stack
                movesHistory.push(assign);
            }
        }
        catch (IOException e) {
            System.out.println("Error reading from file");
        }
    }

    /**
     * Extracts numbers from a string and returns them as a queue of integers
     */

    private Queue<Integer> stringToDigitQueue(String str) {
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


    void makeMove(int row, int col, int state) {
        Assign userMove = new Assign(row, col, state);

        // push the move onto the history stack
        movesHistory.push(userMove);
        // make the move
        nonogram.setState(userMove);
    }

    /**
     * Sets the status bar to a given string
     *
     * @param s the new status
     */
    void setStatus(String s) {


        status.setText(s);


    }

    public static void main(String[] args) {
        JFrame         frame = new JFrame("Nonogram");



        NonogramPanel panel = new NonogramPanel();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    Scanner scanFile, scanUserInput;
    Nonogram nonogram;
    Stack<Assign> movesHistory;
    int row, col;
    private final JPanel grid;
    private final PanelCell[][] cells;
    private final JTextArea status;
    final String GAME_FILE = "nons/tiny.non";
    final char EMPTY_CHAR ='X';
    final char FULL_CHAR ='@';
    final char UNKNOWN_CHAR = '.';
    final char INVALID_CHAR = '?';
    final char SOLVED_CHAR = '*';
}