package com.cooking.mycuisine;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;

public class SplashScreenActivity extends Activity {

	Thread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
	}

	@Override
	protected void onResume() {

		super.onResume();
		thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);

					goToNextScreen();

				} catch (InterruptedException e) {
				}
			}
		};

		thread.start();
	}

	protected void goToNextScreen() {

		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
