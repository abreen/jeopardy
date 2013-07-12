/**
 * A specification for code that runs a Jeopardy! game.
 * 
 * @author Alexander Breen <alexander.breen@gmail.com>
 */
public interface JeopardyInterface {
  
  public static final int BOARD_WIDTH = 4;
  
  /**
   * Height of the board, not counting the extra row containing the
   * category titles.
   */
  public static final int BOARD_HEIGHT = 5;
  
  public static final int NUMBER_OF_TEAMS = 4;
  
  /**
   * Whether the game should ask for teach team to come up
   * with a name.
   */
  public static final boolean USE_TEAM_NAMES = false;
  
  /**
   * The dollar amounts of the questions on each row, where the nth
   * row on the board corresponds to the n - 1st element in this array.
   */
  public static final int[] DOLLAR_AMOUNTS = {100, 200, 300, 400, 500};
  
  /**
   * Given a board coordinate, return the answer string. Remember: answer
   * comes first!
   * 
   * @param x The x-coordinate of the tile
   * @param y The y-coordinate of the tile
   * @return The answer string
   */
  public String getAnswer(int x, int y);
  
  /**
   * Given a board coordinate, return whether this tile is a Daily Double
   * tile.
   * 
   * @param x The x-coordinate of the tile
   * @param y The y-coordinate of the tile
   * @return True if the tile is a Daily Double tile
   */
  public boolean isDailyDouble(int x, int y);
  
  /**
   * Awards the specified dollar amount to the specified team.
   * 
   * @param team The team to which to award the money
   * @param amount The dollar amount to award
   */
  public void correct(int team, int amount);
  
  /**
   * Takes the specified dollar amount from the specified team.
   * 
   * @param team The team from which to take the money
   * @param amount The dollar amount to take
   */
  public void incorrect(int team, int amount);
  
  /**
   * Given an x-coordinate on the board, return the name of the category.
   * @param x The x-coordinate on the board
   * @return The string name of the category
   */
  public String getCategoryName(int x);
  
  
}
