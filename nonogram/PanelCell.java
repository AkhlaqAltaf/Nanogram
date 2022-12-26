package nonogram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class PanelCell extends JButton {

    Assign assign;
    NonogramUI ui =new NonogramUI();


    public PanelCell(NonogramPanel p, int r, int c) {




//        if (p == null)
//            throw new IllegalArgumentException("p cannot be null");
//        if ((r < 0) || (r >= p.getWidth()))
//            throw new IllegalArgumentException("r invalid, must be 0 <= r < " + p.;
//        if ((c < 0) || (c >= p.getHeight()))
//            throw new IllegalArgumentException("c invalid, must be 0 <= c < " + p.getNumCols());




        panel = p;
        row = r;
        col = c;


//        this.state = Nonogram.UNKNOWN;

        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {



                if(getBackground().equals(Color.red)){

                    move(row,col,'.');
                    setBackground(Color.white);

                }

//                else if(getText().equals("X")){
//                    System.out.println("ROw:"+row+"col"+col);
//
//                    setText("");
//
//
//
//                    setBackground(Color.red);
//
//
//
//                }
//
//                else if(getBackground().equals(Color.white)){
//                    System.out.println("ROw:"+row+"col"+col);
//                    setText("X");
//
//                }

                else{

                    move(row,col,'X');

//                    System.out.println("ROw:"+row+"col"+col);

                    setBackground(Color.red);
                }






                System.out.println("row:"+r +"Col:"+c);
            }
        });

        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                requestFocusInWindow();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setStatus("");
            }
        });


        addKeyListener(new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {


            char c = Character.toUpperCase(e.getKeyChar());

            if (c != FULL_CHAR || c != EMPTY_CHAR || c != UNKNOWN_CHAR) {

                panel.setStatus("player must use " + EMPTY_CHAR + " or " + FULL_CHAR + " or " + UNKNOWN_CHAR);
                return;
            }

//            if (!panel.isValidAssign(row, col, charToInt(c))){
//                panel.setStatus("invalid user move");
//                return;
//            }

            panel.makeMove(row, col, charToInt(c));
        }
        });

        setHorizontalAlignment(CENTER);
        setMargin(new Insets(5,5,5,5));
        setForeground(Color.black);
        setFont(font);
        setPreferredSize(new Dimension(30,30));
    }



    public Component getButton() {

        return null;

    }


    public void move(int row,int col,char ch) {
        ui.setState(row,col,ch);
        ui.moveFromGui();

    }







    public void clear() {
        setText("");
    }

    public static int charToInt(char c) {
        return (c == EMPTY_CHAR) ? Nonogram.EMPTY : (c == FULL_CHAR) ? Nonogram.FULL : Nonogram.UNKNOWN;
    }


    public static Font font = new Font("Dialog", Font.BOLD, 20);
    private NonogramPanel panel;
    private int            row;
    private int            col;


    private static final char EMPTY_CHAR   = 'X';
    private static final char FULL_CHAR    = '@'; // or use '\u2588'
    private static final char UNKNOWN_CHAR = '.';
    private static final char INVALID_CHAR = '?';
    private static final char SOLVED_CHAR  = '*';

}
