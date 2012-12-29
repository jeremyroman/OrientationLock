package com.jeremyroman.orientationlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences preferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		boolean startOnBoot = preferences.getBoolean("start_on_boot", true);
		
		if (startOnBoot) {
			context.startService(new Intent(context, OrientationLockService.class));
		}
	}
}
