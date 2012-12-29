package com.jeremyroman.orientationlock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

public class OrientationLockService extends Service {
	public static final String ACTION_TOGGLE = "ACTION_TOGGLE";
	private static final String LOG_TAG = "OrientationLockService";
	private static final int NOTIFICATION_ID = 0;

	private NotificationManager mNotificationManager;
	private WindowManager mWindowManager;
	private PendingIntent mPendingIntent;
	private ContentObserver mContentObserver =
			new ContentObserver(new Handler(Looper.getMainLooper())) {
				@Override
				public boolean deliverSelfNotifications() {
					return true;
				}
				
				@Override
				public void onChange(boolean selfChange) {
					refreshNotification();
				}
				
				@Override
				public void onChange(boolean selfChange, Uri uri) {
					refreshNotification();
				}
			};

	@Override
	public void onCreate() {
		super.onCreate();

		Intent toggleIntent = new Intent(this, OrientationLockService.class);
		toggleIntent.setAction(ACTION_TOGGLE);
		mPendingIntent = PendingIntent.getService(this,
				0 /* requestCode */, toggleIntent, 0 /* flags */);
		mNotificationManager = (NotificationManager)
				getSystemService(Context.NOTIFICATION_SERVICE);
		mWindowManager = (WindowManager)
				getSystemService(Context.WINDOW_SERVICE);
		refreshNotification();
		getContentResolver().registerContentObserver(
				Settings.System.CONTENT_URI, true, mContentObserver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mContentObserver);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (ACTION_TOGGLE.equals(intent.getAction())) {
			toggleOrientationLock();
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void toggleOrientationLock() {
		try {
			int currentSetting = Settings.System.getInt(getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION);
			Settings.System.putInt(getContentResolver(),
					Settings.System.USER_ROTATION,
					mWindowManager.getDefaultDisplay().getRotation());
			Settings.System.putInt(getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION,
					1 - currentSetting);
		} catch (SettingNotFoundException e) {
			Log.e(LOG_TAG, "accelerometer rotation setting not found");
		}
	}

	private void refreshNotification() {
		boolean orientationLocked;
		try {
			orientationLocked = Settings.System.getInt(getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION) == 0;
		} catch (SettingNotFoundException e) {
			Log.e(LOG_TAG, "accelerometer rotation setting not found");
			return;
		}
		String title = getString(orientationLocked ?
				R.string.orientation_locked_title :
				R.string.orientation_unlocked_title);
		String text = getString(orientationLocked ?
				R.string.orientation_locked_text :
				R.string.orientation_unlocked_text);
		Notification notification = new NotificationCompat.Builder(this)
				.setContentTitle(title)
				.setContentText(text)
				.setSmallIcon(android.R.drawable.ic_menu_always_landscape_portrait)
				.setOngoing(true)
				.setContentIntent(mPendingIntent)
				.setWhen(0)
				.build();
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}
}
