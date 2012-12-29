package com.jeremyroman.orientationlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		startService(new Intent(this, OrientationLockService.class));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.stop_service) {
			stopService(new Intent(this, OrientationLockService.class));
			Toast.makeText(this, R.string.stop_service_toast,
					Toast.LENGTH_SHORT).show();
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public static class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref);
		}
	}
}
