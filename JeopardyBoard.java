/**
 * An implementation of a graphical interface with which to play a game
 * of Jeopardy!.
 * 
 * @author Alexander Breen <alexander.breen@gmail.com>
 */
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import lib.madsr.*;

public class JeopardyBoard {
  
  public static final int WINDOW_WIDTH = 800;
  public static final int WINDOW_HEIGHT = 600;
  
  public static void main(String[] args) {
    Jeopardy game = new Jeopardy();
    
    if (JeopardyInterface.BOARD_HEIGHT != 5 || 
            JeopardyInterface.BOARD_WIDTH != 4) {
      throw new UnsupportedOperationException("board code must be altered " +
              "to fit new board width and/or height");
    }
    
    JFrame frame = new JFrame("Jeopardy!");
    frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    frame.setVisible(true);
    
    int width  = JeopardyInterface.BOARD_WIDTH;
    int height = JeopardyInterface.BOARD_HEIGHT;
    
    JButton[] categories = new JButton[width];
    
    for (int i = 0; i < width; i++) {
      categories[i] = new JButton(game.getCategoryName(i));
    }
    
    JButton[][] tiles = new JButton[height][width];
    
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        tiles[i][j] = new JButton("$" + JeopardyInterface.DOLLAR_AMOUNTS[i]);
        JBox.setSize(tiles[i][j], WINDOW_WIDTH / width - 4,
                WINDOW_HEIGHT / height - 4);
        tiles[i][j].setBackground(Color.BLUE);
        tiles[i][j].setForeground(Color.YELLOW);
        tiles[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
        tiles[i][j].setFont(new Font("DejaVu Serif", Font.BOLD, 32));
      }
    }
    
    
    JBox board =
            JBox.vbox(
              JBox.hbox(),
              JBox.hbox(tiles[0][0], tiles[0][1], tiles[0][2], tiles[0][3]),
              JBox.hbox(tiles[1][0], tiles[1][1], tiles[1][2], tiles[1][3]),
              JBox.hbox(tiles[2][0], tiles[2][1], tiles[2][2], tiles[2][3]),
              JBox.hbox(tiles[3][0], tiles[3][1], tiles[3][2], tiles[3][3]),
              JBox.hbox(tiles[4][0], tiles[4][1], tiles[4][2], tiles[4][3])
            );
    
    board.setSize(WINDOW_WIDTH - 20, WINDOW_HEIGHT - 20);
    frame.add(board);
    
  }
}
