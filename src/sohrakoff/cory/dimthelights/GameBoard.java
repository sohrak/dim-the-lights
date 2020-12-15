package sohrakoff.cory.dimthelights;

import android.R.color;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * This class defines the view that draws the game grid and handles the input events
 * on the grid.
 * 
 * @author cory
 *
 */
public class GameBoard extends View {
	
	// reference to main Activity
	DimTheLights mainActivity;
	
	// references to on and off lights
	Drawable offLight;
	Drawable onLight;
	
	// boolean indicates to the view whether to reset the game board scale
	// before drawing the view
	// initially true since scale does need to be set before first draw
	private boolean resetScale = true;
	
	public void setResetScale(boolean resetScale) {
		this.resetScale = resetScale;
	}

	public GameBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// need to focusable to accept Touch Events
		setFocusable(true);
		
		// set up reference to mainActivity
		mainActivity = (DimTheLights) context;
		
		offLight = (Drawable) mainActivity.getResources().getDrawable(R.drawable.dark);
		onLight = (Drawable) mainActivity.getResources().getDrawable(R.drawable.light);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if ( resetScale == true ) {
			setViewScale();
			resetScale = false; // scale reset
		}
		
		// Clear the screen before redrawing
		canvas.drawColor(color.background_dark);
		
		switch(mainActivity.getGameState().getCurrentGameState()){
			case Constants.GAME_COMPLETE:
				; // nothing extra happens so it just falls through
			case Constants.GAME_PLAYING:
				drawPlayingState(canvas);
				break;
			// Logic for other states not implemented yet.
			default:
				break;
		}
		
		super.onDraw(canvas);
	}
	
	private void drawPlayingState(Canvas canvas) {
		GameState gameState = mainActivity.getGameState();
		boolean[][] lightStates = gameState.getLightStates();
		Rect[][] lightPos = gameState.getLightPositions();
		int gameBoardSize = gameState.getGameBoardSize();
		
		for ( int row = 0; row < gameBoardSize; row++ )
			for ( int col = 0; col < gameBoardSize; col++ )
			{
				// if light is on
				if ( lightStates[row][col] == true ) {
					onLight.setBounds(lightPos[row][col]);
					onLight.draw(canvas);
				}
				else // if light is off
				{
					offLight.setBounds(lightPos[row][col]);
					offLight.draw(canvas);			
				}
			}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int touchAction = event.getAction();
		
		if (touchAction == MotionEvent.ACTION_DOWN) {
			switch (mainActivity.getGameState().getCurrentGameState()) {
				case Constants.GAME_PLAYING:
					// Log.v("GameBoard", "GAME_PLAYING:ACTION_DOWN");
					handleScreenTouch((int)event.getX(), (int)event.getY());
					return true;
				case Constants.GAME_COMPLETE:
					// Log.v("GameBoard", "GAME_COMPLETE:ACTION_DOWN");
					mainActivity.getGameState().newGame(Constants.SAME_GAME);
					invalidate();
					return true;
				// Other game states are not implemented yet.
				default:
					return true;
			}
		}
		
		return super.onTouchEvent(event);
	}
	
	/**
	 * Sets up the scaling info so that the game board will display properly for
	 * the current size of the GameBoard view.
	 */
	private void setViewScale() {
		int height = getHeight();
		int width = getWidth();
		
		int gameBoardSize = mainActivity.getGameState().getGameBoardSize();
		
		int lightSize; // determined by smallest (width or height)
		if ( width <= height ) {
			// gameBoardSize + 2 because we want at least LIGHT_SPACING padding on each side of screen
			lightSize = (width-(gameBoardSize+2)*Constants.LIGHT_SPACING) / gameBoardSize;
		}
		else {
			lightSize = (height-(gameBoardSize+2)*Constants.LIGHT_SPACING) / gameBoardSize;
		}
		
		int horizontalScreenPadding = ((width-(gameBoardSize-1)*Constants.LIGHT_SPACING) - (lightSize*gameBoardSize)) / 2;
		int verticalScreenPadding = ((height-(gameBoardSize-1)*Constants.LIGHT_SPACING) - (lightSize*gameBoardSize)) / 2;
		
		Rect lightPositions[][] = new Rect[gameBoardSize][gameBoardSize];
			
		for ( int row = 0; row < gameBoardSize; row++ )
			for ( int col = 0; col < gameBoardSize; col++ ) {
				int left = horizontalScreenPadding + col*(Constants.LIGHT_SPACING + lightSize);
				int top = verticalScreenPadding + row*(Constants.LIGHT_SPACING + lightSize) ;
				lightPositions[row][col] = new Rect(left, top, left+lightSize, top+lightSize);
			}
		mainActivity.getGameState().setLightPositions(lightPositions);
	}
	
	/**
	 * Handles an ACTION_DOWN Touch Event when the game state is GAME_PLAYING
	 * @param X	The X position of the touch.
	 * @param Y The Y position of the touch.
	 */
	private void handleScreenTouch(int X, int Y) {
		GameState gameState = mainActivity.getGameState();
		
		boolean squareTapped = false;
		int row;
		int col = 0;
		
		int gameBoardSize = gameState.getGameBoardSize();
		Rect[][] lightPos = gameState.getLightPositions();
		
		outerLoop:
		for ( row = 0; row < gameBoardSize; row++ )
			for ( col = 0; col < gameBoardSize; col++ )
				if ( lightPos[row][col].contains(X, Y) ) {
						squareTapped = true;
						break outerLoop;
				}
		
		if ( squareTapped ) {
			gameState.flipLights(row, col);
			gameState.incrementNumberOfMoves();
			invalidate();
		}
		
		if (gameState.gameIsComplete()) {
			int pos;
			if ((pos = gameState.isHighScore()) != -1) {
				showHighScoreDialog(pos);
			}
			else {
				showGameCompleteDialog();
			}
			// Log.v("GameBoard", "The game is finished");
		}
	}
	
	private void showGameCompleteDialog() {
		AlertDialog ad = new AlertDialog.Builder(this.getContext())
		.setTitle(R.string.game_complete)
		.setMessage("Moves: " + mainActivity.getGameState().getNumberOfMoves() + " (Score: " + 
				mainActivity.getGameState().getScore() + "%)")
		.setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mainActivity.getGameState().newGame(Constants.SAME_GAME);
				invalidate();
			}
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				mainActivity.getGameState().setCurrentGameState(Constants.GAME_COMPLETE);
				mainActivity.getNewGameMessage().setVisibility(View.VISIBLE);
			}
			
		})
		.create();
		ad.show();
	}
	
	public void showHighScoreDialog(int p) {
		mainActivity.setHighScoresDialogOpen(true);
		
		final int pos = p;
		
		final EditText editText = new EditText(this.getContext());
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(3);
		editText.setFilters(FilterArray);
		editText.setHint("Enter initials to save score");
		
		final AlertDialog ad = new AlertDialog.Builder(this.getContext())
		.setTitle(R.string.game_complete)
		.setMessage("Moves: " + mainActivity.getGameState().getNumberOfMoves() + " (Score: " + 
				mainActivity.getGameState().getScore() + "%)\n\nNEW HIGH SCORE!")
		.setView(editText)
		.setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String s = editText.getText().toString();
				if ( !s.equals("") ) {
					mainActivity.getGameState().enterHighScore(s, pos);
				}
				mainActivity.getGameState().newGame(Constants.SAME_GAME);
				invalidate();
				mainActivity.setHighScoresDialogOpen(false);
			}
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				mainActivity.getGameState().setCurrentGameState(Constants.GAME_COMPLETE);
				mainActivity.getNewGameMessage().setVisibility(View.VISIBLE);
				mainActivity.setHighScoresDialogOpen(false);
			}
			
		})
		.create();
		
		editText.setOnKeyListener( new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ( keyCode == KeyEvent.KEYCODE_ENTER) {
					String s = editText.getText().toString();
					if ( !s.equals("") ) {
						mainActivity.getGameState().enterHighScore(s, pos);
					}
					mainActivity.getGameState().newGame(Constants.SAME_GAME);
					invalidate();
					mainActivity.setHighScoresDialogOpen(false);
					ad.dismiss();
					return true;
				}
				return false;
			}
			
		});
		ad.show();
	}
}
