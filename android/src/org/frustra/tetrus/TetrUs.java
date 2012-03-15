package org.frustra.tetrus;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TetrUs extends Activity {
	
	public static final String LOG_TAG = "TETRUS"; 
	
	private TetrUsGame view;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new TetrUsGame(this);
		setContentView(view.getPaintView());
	}
	
	private static final int START_MENU_ID = Menu.FIRST;
	private static final int STOP_MENU_ID = Menu.FIRST + 1;
	private static final int RESET_MENU_ID = Menu.FIRST + 2;
	private static final int EXIT_MENU_ID = Menu.FIRST + 3;
	
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, START_MENU_ID, 0, "Start").setShortcut('5', 's');
		menu.add(0, STOP_MENU_ID, 0, "Pause").setShortcut('6', 'p');
		menu.add(0, RESET_MENU_ID, 0, "Reset").setShortcut('7', 'r');
		menu.add(0, EXIT_MENU_ID, 0, "Exit").setShortcut('8', 'q');
		return true;
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case START_MENU_ID:
				view.setStarted(true);
				return true;
			case STOP_MENU_ID:
				view.setStarted(false);
				return true;
			case RESET_MENU_ID:
				view.resetGame();
				return true;
			case EXIT_MENU_ID:
				System.exit(0);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}