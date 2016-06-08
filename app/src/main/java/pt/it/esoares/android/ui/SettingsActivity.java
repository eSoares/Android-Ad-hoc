package pt.it.esoares.android.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.psdev.licensesdialog.LicensesDialog;
import pt.it.esoares.android.olsrdeployer.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsActivity extends AppCompatActivity {
	private static final String SETTINGS_FRAGMENT_TAG = "settings fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);
		if (getFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG) == null) {
			getFragmentManager().beginTransaction().add(R.id.frame_settings, new SettingsFragment(), SETTINGS_FRAGMENT_TAG).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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


	public static class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_ip);
			bindPreferenceSummaryToValue(findPreference("ip_address"));
			bindPreferenceSummaryToValue(findPreference("ip_mask"));
			bindPreferenceSummaryToValue(findPreference("ip_address_gateway"));

			addPreferencesFromResource(R.xml.pref_network);
			bindPreferenceSummaryToValue(findPreference("network_name_text"));
			bindPreferenceSummaryToValue(findPreference("wep_password_text"));
			bindPreferenceSummaryToValue(findPreference("channel_list"));
			addPreferencesFromResource(R.xml.pref_olsr);
			addPreferencesFromResource(R.xml.pref_extras);
			addPreferencesFromResource(R.xml.pref_about);
			findPreference("button_license").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					new LicensesDialog.Builder(getActivity()).setNotices(R.raw.notices).build().show();
					return true;
				}
			});
		}
	}
}
