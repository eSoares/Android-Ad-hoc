package pt.it.esoares.adhocdroid.ui;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import de.psdev.licensesdialog.LicensesDialog;
import pt.it.esoares.adhocdroid.R;

public class DeprecatedSettingsActivity extends PreferenceActivity {


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the device configuration dictates that a simplified, single-pane UI
	 * should be shown.
	 */
	private void setupSimplePreferencesScreen() {
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'Network' preferences.
		addPreferencesFromResource(R.xml.pref_network);

		// Add 'IP' preferences, and a corresponding header.
		addPreferencesFromResource(R.xml.pref_ip);


		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.

		// Network
		bindPreferenceSummaryToValue(findPreference("network_name_text"));
		bindPreferenceSummaryToValue(findPreference("wep_password_text"));
		bindPreferenceSummaryToValue(findPreference("channel_list"));

		bindPreferenceSummaryToValue(findPreference("ip_address"));
		bindPreferenceSummaryToValue(findPreference("ip_mask"));
		bindPreferenceSummaryToValue(findPreference("ip_address_gateway"));
		addPreferencesFromResource(R.xml.pref_extras);
		addPreferencesFromResource(R.xml.pref_about);
		findPreference("button_license").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new LicensesDialog.Builder(getBaseContext()).setNotices(R.raw.notices).build().show();
				return true;
			}
		});
	}


	/**
	 * A preference value change listener that updates the preference's summary to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary (line of text below
	 * the preference title) is updated to reflect the value. The summary is also immediately updated upon calling this method. The exact
	 * display format is dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager
				.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
	}
}
