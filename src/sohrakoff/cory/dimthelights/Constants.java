package sohrakoff.cory.dimthelights;

/**
 * This interface contains the constants that are used by the game and it's UI 
 * components.
 * 
 * @author cory
 * 
 */
public interface Constants {

	// constants for the options menu
	public static final int MENU_HIGH_SCORES = 0;
	public static final int MENU_HOW_TO_PLAY = 1;
	public static final int MENU_ABOUT = 2;
	public static final int MENU_3X3 = 3;
	public static final int MENU_4X4 = 4;
	public static final int MENU_5X5 = 5;
	
	// default game choice will be a 4x4 game
	public static final int DEFAULT_GAME = MENU_4X4;
	
	// used to create new game that is the same as the last one
	public static final int SAME_GAME = -1;
	
	// game running state constants
	public static final int GAME_PLAYING = 6;
	public static final int GAME_COMPLETE = 7;
	public static final int GAME_PAUSED = 8;
	
	// spacing between lights on game board (in pixels)
	public static final int LIGHT_SPACING = 5;
	
	// state beginning number of moves
	public static final int BEGINNING_NUMBER_MOVES = 0;
	
	// minimum number of moves to solve the puzzles
	public static final int MIN_MOVES_3X3 = 5;
	public static final int MIN_MOVES_4X4 = 4;
	public static final int MIN_MOVES_5X5 = 15;
	
	// set high scores array size
	public static final int NUMBER_OF_LEVELS = 3;
	public static final int NUMBER_OF_HIGH_SCORES = 3; // NUMBER OF SCORES TO KEEP FOR EACH LEVEL
	public static final int[][] defaultHighScores = { {2*MIN_MOVES_3X3,2*MIN_MOVES_3X3+1,2*MIN_MOVES_3X3+2}, 
													  {2*MIN_MOVES_4X4,2*MIN_MOVES_4X4+1,2*MIN_MOVES_4X4+2}, 
													  {2*MIN_MOVES_5X5,2*MIN_MOVES_5X5+1,2*MIN_MOVES_5X5+2} };
	public static final String defaultPlayerName = "CTS";
}
