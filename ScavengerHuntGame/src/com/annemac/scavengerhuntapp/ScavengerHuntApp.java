package com.annemac.scavengerhuntapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.parse.*;



/**
 * Top level ScavengerHuntApplication declared as the application in the AndroidManifest.xml.
 * There is only one instance of this class created...by the android OS...upon
 * startup.
 */
public class ScavengerHuntApp extends Application {

	private static final String TAG = "ScavengerHuntApplication";
	private static final int START_WAIT_TIME = 500; // 3 seconds

	private static ScavengerHuntApp instance;

	public static ScavengerHuntApp getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public void initializeParse() {
		try {
			synchronized (this) {
			//	Log.i(TAG, "Parse.initialize");
				// initialize Parse
				Parse.initialize(this, getString(R.string.parse_app_id),
						getString(R.string.parse_client_id));
				ParseUser.enableAutomaticUser();
				ParseACL defaultACL = new ParseACL();
				//defaultACL.setPublicReadAccess(true);
				ParseACL.setDefaultACL(defaultACL, true);
				//Log.i(TAG, "Parse.initialize - done");
				// add some delay, to show the splash screen
				wait(START_WAIT_TIME);
   		        PushService.setDefaultPushCallback(this, MainMenuActivity.class);
			    //   ParseInstallation.getCurrentInstallation().saveInBackground();

				ParseInstallation installation = ParseInstallation.getCurrentInstallation();
				installation.put("user",ParseUser.getCurrentUser());
				installation.saveInBackground();


                //   Log.i(TAG, "Parse.initialize - done");
			}
		} catch (Exception ex) {
			//Log.e(TAG + "." + "Exception in initializeParse", ex.getMessage());
		}
	}
	
	public void showToast(Context context, String message) {
    Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
    toast.show();
  }
}

