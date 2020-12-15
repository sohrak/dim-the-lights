package sohrakoff.cory.dimthelights;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

/**
 * This class contains the variables shared by the several game classes.
 * 
 * @author cory
 *
 */
public class GameState {

	public GameState( DimTheLights mainActivity, GameBoard gameBoard ) {
		this.mainActivity = mainActivity;
		this.gameBoard = gameBoard;
		
		// initial default set up
		lightStates = new boolean[Constants.DEFAULT_GAME][Constants.DEFAULT_GAME];
		newGame(Constants.DEFAULT_GAME);
		
		SharedPreferences data = mainActivity.getPreferences(Activity.MODE_PRIVATE);
		// hard coded high scores is ok :(
		String scores3 = data.getString("3x3", null);
		String scores4 = data.getString("4x4", null);
		String scores5 = data.getString("5x5", null);
		
		highScores = new int[Constants.NUMBER_OF_LEVELS][Constants.NUMBER_OF_HIGH_SCORES];
		highScorePlayers = new String[Constants.NUMBER_OF_LEVELS][Constants.NUMBER_OF_HIGH_SCORES];
		if ( scores3 == null && scores4 == null && scores5 == null ) {
			// Log.v("GameState", "No high scores saved");
			// set default high scores
			for ( int i = 0; i < Constants.NUMBER_OF_LEVELS; i++)
				for ( int j = 0; j < Constants.NUMBER_OF_HIGH_SCORES; j++) {
					highScores[i][j] = Constants.defaultHighScores[i][j];
					highScorePlayers[i][j] = Constants.defaultPlayerName;
				}
		}
		else // else need to load high scores
		{
			// Log.v("GameState", "High scores saved...retrieving");
			String[] scores = scores3.split(":");
			String[] names = data.getString("3x3names", null).split(":");
			for ( int i = 0; i < scores.length; i++ ) {
				highScores[0][i] = Integer.parseInt(scores[i]);
				highScorePlayers[0][i] = names[i];
			}
			
			scores = scores4.split(":");
			names = data.getString("4x4names", null).split(":");
			for ( int i = 0; i < scores.length; i++ ) {
				highScores[1][i] = Integer.parseInt(scores[i]);
				highScorePlayers[1][i] = names[i];
			}
			
			scores = scores5.split(":");
			names = data.getString("5x5names", null).split(":");
			for ( int i = 0; i < scores.length; i++ ) {
				highScores[2][i] = Integer.parseInt(scores[i]);
				highScorePlayers[2][i] = names[i];
			}
		}
	}
	
	public void newGame( int gameBoardSize ) {
		numberOfMoves = Constants.BEGINNING_NUMBER_MOVES;
		
		// reset title bar
		mainActivity.setTitle(R.string.app_name);
		
		// clear this message if there is one
		mainActivity.getNewGameMessage().setVisibility(View.GONE);
		
		if ( gameBoardSize == Constants.SAME_GAME ) {
			; // do nothing since game will be the same
		}
		else if ( this.gameBoardSize != gameBoardSize ) {
			this.gameBoardSize = gameBoardSize;
			lightStates = new boolean[gameBoardSize][gameBoardSize];
			
			numberOfLights = gameBoardSize*gameBoardSize;
			
			// need to reset scale since gameBoardSize has changed
			gameBoard.setResetScale(true);
		}
		
		// start with all lights on
		for ( int row = 0; row < this.gameBoardSize; row++ )
			for ( int col = 0; col < this.gameBoardSize; col++ )
				lightStates[row][col] = true;
		
		// New game is in play state
		currentGameState = Constants.GAME_PLAYING;
	}
	
	public void saveState( Bundle outState ) {
		outState.putInt("gameBoardSize", gameBoardSize);
		outState.putInt("currentGameState", currentGameState);
		outState.putInt("numberOfMoves", numberOfMoves);
		outState.putInt("score", score);

		boolean ar[] = new boolean[numberOfLights];
		int i = 0;
		for ( int row = 0; row < gameBoardSize; row++ )
			for ( int col = 0; col < gameBoardSize; col++ ) {
				ar[i++] = lightStates[row][col];
			}
		outState.putBooleanArray("lightState", ar);	
	}
	
	public void restoreState( Bundle inState ) {
		gameBoardSize = inState.getInt("gameBoardSize", Constants.DEFAULT_GAME);
		numberOfLights = gameBoardSize*gameBoardSize;
		numberOfMoves = inState.getInt("numberOfMoves", Constants.BEGINNING_NUMBER_MOVES);
		score = inState.getInt("score");
		
		if ( numberOfMoves != 0 )
			showScore();
		
		currentGameState = inState.getInt("currentGameState");
		
		boolean ar[] = inState.getBooleanArray("lightState");
		if ( ar != null ) {
			int i = 0;
			lightStates = new boolean[gameBoardSize][gameBoardSize];
			for ( int row = 0; row < gameBoardSize; row++ )
				for ( int col = 0; col < gameBoardSize; col++ ) {
					lightStates[row][col] = ar[i++];
				}
		}
		else { // Some sort of error
			// Log.e("GameState", "lightState was not found in saved game state.");
			// reset a default game
		}
		
    	if ( currentGameState == Constants.GAME_PLAYING && gameIsComplete() &&
    			!mainActivity.isHighScoresDialogOpen()) {
    		setCurrentGameState(Constants.GAME_COMPLETE);
    		mainActivity.getNewGameMessage().setVisibility(View.VISIBLE);
    	}
	}
	
	/**
	 * Switches selected light and surrounding lights on or off.
	 * 
	 * @param row The row of the tapped light.
	 * @param col The column of the tapped light.
	 */
	public void flipLights( int row, int col ) {
		lightStates[row][col] = !lightStates[row][col];
		
		if ( col-1 >= 0 )
			lightStates[row][col-1] = !lightStates[row][col-1];
		
		if ( col+1 < gameBoardSize )
			lightStates[row][col+1] = !lightStates[row][col+1];
		
		if ( row - 1 >= 0 )
			lightStates[row-1][col] = !lightStates[row-1][col];
		
		if ( row + 1 < gameBoardSize )
			lightStates[row+1][col] = !lightStates[row+1][col];
	}
	
	public boolean gameIsComplete() {
		boolean complete = true;
			
		outerloop:
		for ( int row = 0; row < gameBoardSize; row++ )
			for ( int col = 0; col < gameBoardSize; col++ )
				if ( lightStates[row][col] ) {
					// if any light is lit then break out and return false
					complete = false;
					break outerloop;
				}
		
		return complete;
	}
	
	public void incrementNumberOfMoves() {
		numberOfMoves++;
		showScore();
	}
	
	private void showScore() {
		// determine score
		switch (gameBoardSize) {
			case Constants.MENU_3X3:
				score = (int)(((double) Constants.MIN_MOVES_3X3 / (double) numberOfMoves)*100);
				break;
			case Constants.MENU_4X4:
				score = (int)(((double) Constants.MIN_MOVES_4X4 / (double) numberOfMoves)*100);
				break;
			case Constants.MENU_5X5:
				score = (int)(((double) Constants.MIN_MOVES_5X5 / (double) numberOfMoves)*100);
				break;	
		}
		
		String titleBar = "Moves: " + numberOfMoves + " (Score: " + score + "%)";
		// put number of moves and score in title bar
		mainActivity.setTitle(titleBar);
	}
	
	
	// TODO Add initials to high score 3 letters max
	// Maybe should force portrait only mode
	
	public String getHighScores() {
		// some values hard coded, shitty to do but it is easier this way
		String scores = "3x3 Game:";
		for ( int i = 0; i < Constants.NUMBER_OF_HIGH_SCORES; i++ ) {
			scores += "\n\t" + highScorePlayers[0][i] + "  (" + highScores[0][i] + " moves/" 
			+ (int)(((double) Constants.MIN_MOVES_3X3 / (double) highScores[0][i])*100) + "%)";
		}
		
		scores += "\n4x4 Game:";
		for ( int i = 0; i < Constants.NUMBER_OF_HIGH_SCORES; i++ ) {
			scores += "\n\t" +  highScorePlayers[1][i] + "  (" + highScores[1][i] + " moves/"  
			+ (int)(((double) Constants.MIN_MOVES_4X4 / (double) highScores[1][i])*100) + "%)";
		}
		
		scores += "\n5x5 Game:";
		for ( int i = 0; i < Constants.NUMBER_OF_HIGH_SCORES; i++ ) {
			scores += "\n\t" + highScorePlayers[2][i] + "  (" + highScores[2][i] + " moves/"  
			+ (int)(((double) Constants.MIN_MOVES_5X5 / (double) highScores[2][i])*100) + "%)";
		}
		
		return scores;
	}
	
	public int isHighScore() {
		switch (gameBoardSize) {
			case Constants.MENU_3X3:
				return checkHighScore(0);
			case Constants.MENU_4X4:
				return checkHighScore(1);
			case Constants.MENU_5X5:
				return checkHighScore(2);
	}
		
		return -1;
	}
	
	private int checkHighScore( int level ) {
		for ( int i = 0; i < Constants.NUMBER_OF_HIGH_SCORES; i++ ) {
			if ( numberOfMoves <= highScores[level][i] ) {
				// returns where score is
				return i;
			}
		}
		
		return -1;
	}
	
	public void enterHighScore( String initials, int pos ) {
		int level = gameBoardSize - 3;
		for ( int j = Constants.NUMBER_OF_HIGH_SCORES-1; j > pos; j-- ) {
			highScores[level][j] = highScores[level][j-1];
			highScorePlayers[level][j] = highScorePlayers[level][j-1];
		}
		highScorePlayers[level][pos] = initials;
		highScores[level][pos] = numberOfMoves;
		saveScores();
	}
	
	private void saveScores() {
		SharedPreferences data = mainActivity.getPreferences(Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = data.edit();
		
		String scores = "" + highScores[0][0];
		String names = "" + highScorePlayers[0][0];
		for ( int i = 1;  i < Constants.NUMBER_OF_HIGH_SCORES; i++ ) {
			scores += ":" + highScores[0][i];
			names += ":" + highScorePlayers[0][i];
		}
		editor.putString("3x3", scores);
		editor.putString("3x3names", names);
		
		scores = "" + highScores[1][0];
		names = "" + highScorePlayers[1][0];
		for ( int i = 1;  i < Constants.NUMBER_OF_HIGH_SCORES; i++ ) {
			scores += ":" + highScores[1][i];
			names += ":" + highScorePlayers[1][i];
		}
		editor.putString("4x4", scores);
		editor.putString("4x4names", names);
		
		scores = "" + highScores[2][0];
		names = "" + highScorePlayers[2][0];
		for ( int i = 1;  i < Constants.NUMBER_OF_HIGH_SCORES; i++ ) {
			scores += ":" + highScores[2][i];
			names += ":" + highScorePlayers[2][i];
		}
		editor.putString("5x5", scores);
		editor.putString("5x5names", names);
		
		editor.commit();
	}
	
	public void clearScores() {
		SharedPreferences data = mainActivity.getPreferences(Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = data.edit();
		editor.clear();
		editor.commit();
		
		// set default high scores
		for ( int i = 0; i < Constants.NUMBER_OF_LEVELS; i++)
			for ( int j = 0; j < Constants.NUMBER_OF_HIGH_SCORES; j++) {
				highScores[i][j] = Constants.defaultHighScores[i][j];
				highScorePlayers[i][j] = Constants.defaultPlayerName;
			}
	}
	
	public void setGameBoardSize(int gameBoardSize) {
		this.gameBoardSize = gameBoardSize;
	}

	public int getGameBoardSize() {
		return gameBoardSize;
	}

	public void setNumberOfLights(int numberOfLights) {
		this.numberOfLights = numberOfLights;
	}

	public int getNumberOfLights() {
		return numberOfLights;
	}

	public void setLightPositions(Rect[][] lightPositions) {
		this.lightPositions = lightPositions;
	}

	public Rect[][] getLightPositions() {
		return lightPositions;
	}

	public void setLightStates(boolean[][] lightStates) {
		this.lightStates = lightStates;
	}

	public boolean[][] getLightStates() {
		return lightStates;
	}

	public void setCurrentGameState(int currentGameState) {
		this.currentGameState = currentGameState;
	}

	public int getCurrentGameState() {
		return currentGameState;
	}
	
	public int getNumberOfMoves() {
		return numberOfMoves;
	}

	public int getScore() {
		return score;
	}

	// game board variables
	// initialize with default values
	private int gameBoardSize = Constants.DEFAULT_GAME;
	private int numberOfLights = gameBoardSize*gameBoardSize;
	
	private Rect[][] lightPositions;
	private boolean[][] lightStates; // on or off
	
	// game running state variables
	private int currentGameState;
	
	// scoring information
	private int numberOfMoves;
	private int score;
	
	private int[][] highScores;
	private String[][] highScorePlayers;
	// variables used to access the main activity and the game's custom view
	DimTheLights mainActivity;
	GameBoard gameBoard;
}
