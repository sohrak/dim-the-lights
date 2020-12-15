package sohrakoff.cory.dimthelights;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * This is the main class for the game. It contains code for the menu as well as 
 * saving and restoring the game state.
 * 
 * @author cory
 *
 */
public class DimTheLights extends Activity {
	
	private GameState gameState;
	private GameBoard gameBoard;
	private TextView newGameMessage;
	
	private boolean highScoresDialogOpen;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // get gameBoard
        gameBoard = (GameBoard) findViewById(R.id.gameBoard);
        
        // get newGameMessage
        newGameMessage = (TextView) findViewById(R.id.newGameMessage);
        
        // create gameState
        gameState = new GameState(this, gameBoard);
        
        // restore game state or set up the default state
        if ( savedInstanceState != null ) {
        	highScoresDialogOpen = savedInstanceState.getBoolean("highScoresDialogOpen", false);
        	newGameMessage.setVisibility(savedInstanceState.getInt("newGameMessageVisibility", View.GONE));
        	gameState.restoreState(savedInstanceState);
        	
        	if (highScoresDialogOpen) {
        		gameBoard.showHighScoreDialog(gameState.isHighScore());
        	}
        }
        else {
        	// default game is already set up
        	highScoresDialogOpen = false;
        }
    }

	public boolean isHighScoresDialogOpen() {
		return highScoresDialogOpen;
	}

	public void setHighScoresDialogOpen(boolean highScoresDialogOpen) {
		this.highScoresDialogOpen = highScoresDialogOpen;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Constants.MENU_3X3, 0, "3x3 Game")
			.setIcon(R.drawable.option3icon);
		menu.add(0, Constants.MENU_4X4, 1, "4x4 Game")
			.setIcon(R.drawable.option4icon);
		menu.add(0, Constants.MENU_5X5, 2, "5x5 Game")
			.setIcon(R.drawable.option5icon);
		menu.add(0, Constants.MENU_HIGH_SCORES, 3, "High Scores")
			.setIcon(R.drawable.scoresicon);
		menu.add(0, Constants.MENU_HOW_TO_PLAY, 4, "How To Play")
			.setIcon(R.drawable.howtoicon);
		menu.add(0, Constants.MENU_ABOUT, 5, "About")
			.setIcon(R.drawable.abouticon);
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch( itemId ) {
			case Constants.MENU_3X3:
				gameState.newGame(Constants.MENU_3X3);
				gameBoard.invalidate();
				return true;
			case Constants.MENU_4X4:
				gameState.newGame(Constants.MENU_4X4);
				gameBoard.invalidate();
				return true;
			case Constants.MENU_5X5:
				gameState.newGame(Constants.MENU_5X5);
				gameBoard.invalidate();
				return true;
			case Constants.MENU_HIGH_SCORES:
				showHighScores();
				return true;
			case Constants.MENU_HOW_TO_PLAY:
				showHowToPlayDialog();
				return true;
			case Constants.MENU_ABOUT:
				showAboutDialog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		gameState.saveState(outState);
		
		outState.putInt("newGameMessageVisibility",newGameMessage.getVisibility());
		outState.putBoolean("highScoresDialogOpen", highScoresDialogOpen);
		
		super.onSaveInstanceState(outState);
	}
	
	private void showAboutDialog() {
		AlertDialog ad = new AlertDialog.Builder(this)
		.setTitle(R.string.app_name)
		.setMessage(R.string.copyright)
		.setPositiveButton(R.string.OK, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}	
		})
		.create();
		ad.show();
	}
	
	private void showHowToPlayDialog() {
		AlertDialog ad = new AlertDialog.Builder(this)
		.setTitle(R.string.how_to_play_title)
		.setMessage(R.string.how_to_play_message)
		.setPositiveButton(R.string.OK, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}	
		})
		.create();
		ad.show();
	}
	
	private void showHighScores() {
		AlertDialog ad = new AlertDialog.Builder(this)
		.setTitle(R.string.high_scores_title)
		.setMessage(gameState.getHighScores())
		.setPositiveButton(R.string.OK, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}	
		})
		.setNegativeButton(R.string.clear, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				gameState.clearScores();
			}	
		})
		.create();
		ad.show();	
	}


	public GameState getGameState() {
		return gameState;
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}
	
    public TextView getNewGameMessage() {
		return newGameMessage;
	}
    
}