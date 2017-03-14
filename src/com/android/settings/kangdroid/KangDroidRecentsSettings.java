/*
 * Copyright (C) 2014 The KangDroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.kangdroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.SearchIndexableResource;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import java.util.ArrayList;
import java.util.List;

public class KangDroidRecentsSettings extends SettingsPreferenceFragment implements Indexable, Preference.OnPreferenceChangeListener {

    private static final String IMMERSIVE_RECENTS = "immersive_recents";
	private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
	
    private static final String RECENTS_USE_OMNISWITCH = "recents_use_omniswitch";
    private static final String OMNISWITCH_START_SETTINGS = "omniswitch_start_settings";
	
	private static final String CLEAR_ALL_SWITCH = "show_clear_all_recents";
	private static final String RECENTS_MEMBAR = "systemui_recents_mem_display";
	private static final String SLIM_RECENTS = "use_slim_recents";
    // Package name of the omnniswitch app
    public static final String OMNISWITCH_PACKAGE_NAME = "org.omnirom.omniswitch";
    // Intent for launching the omniswitch settings actvity
    public static Intent INTENT_OMNISWITCH_SETTINGS = new Intent(Intent.ACTION_MAIN)
            .setClassName(OMNISWITCH_PACKAGE_NAME, OMNISWITCH_PACKAGE_NAME + ".SettingsActivity");

    private ListPreference mImmersiveRecents;
	private SwitchPreference mRecentsClearAll;
	private ListPreference mRecentsClearAllLocation;
	
    private SwitchPreference mRecentsUseOmniSwitch;
    private Preference mOmniSwitchSettings;
	private SwitchPreference mRecentClearAllSwitch;
	private SwitchPreference mRecentMemBar;
	private SwitchPreference mRecentSlim;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.kangdroid_recents_settings);
		
		PreferenceScreen prefSet = getPreferenceScreen();
		final ContentResolver resolver = getActivity().getContentResolver();
		
        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        mImmersiveRecents.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.RECENTS_FULL_SCREEN, 0)));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);
		
		mRecentClearAllSwitch = (SwitchPreference) findPreference(CLEAR_ALL_SWITCH);
		
        // clear all location
        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);
		
        mRecentsUseOmniSwitch = (SwitchPreference) findPreference(RECENTS_USE_OMNISWITCH);
		mRecentsUseOmniSwitch.setOnPreferenceChangeListener(this);
		mRecentMemBar = (SwitchPreference) findPreference(RECENTS_MEMBAR);

        mOmniSwitchSettings = (Preference) findPreference(OMNISWITCH_START_SETTINGS);
        mOmniSwitchSettings.setEnabled(mRecentsUseOmniSwitch.isChecked());
		
		mRecentSlim = (SwitchPreference) findPreference(SLIM_RECENTS);
		mRecentSlim.setOnPreferenceChangeListener(this);
		updatePreference();

    }
	
    public void updatePreference() {
        boolean slimRecent = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.USE_SLIM_RECENTS, 0) == 1;
		
        boolean omniSwitch = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_USE_OMNISWITCH, 0) == 1;

        if (slimRecent) {
			mRecentClearAllSwitch.setEnabled(false);
            mImmersiveRecents.setEnabled(false);
            mRecentsClearAllLocation.setEnabled(false);
			mRecentMemBar.setEnabled(false);
			mRecentsUseOmniSwitch.setEnabled(false);
        } else if (omniSwitch) {
            mImmersiveRecents.setEnabled(false);
			mRecentClearAllSwitch.setEnabled(false);
            mRecentsClearAllLocation.setEnabled(false);
			mRecentMemBar.setEnabled(false);
			mRecentSlim.setEnabled(false);
		} else {
			mRecentClearAllSwitch.setEnabled(true);
            mImmersiveRecents.setEnabled(true);
            mRecentsClearAllLocation.setEnabled(true);
			mRecentMemBar.setEnabled(true);
		}
    }
	
    @Override
    public void onResume() {
        super.onResume();
		updatePreference();
    }
	
    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mOmniSwitchSettings){
            startActivity(INTENT_OMNISWITCH_SETTINGS);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
		updatePreference();
        if (preference == mImmersiveRecents) {
            Settings.System.putInt(getContentResolver(), Settings.System.RECENTS_FULL_SCREEN,
                    Integer.valueOf((String) newValue));
            mImmersiveRecents.setValue(String.valueOf(newValue));
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            return true;
        } else if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) newValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference == mRecentsUseOmniSwitch) {
            boolean showing = ((Boolean)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.RECENTS_USE_OMNISWITCH,
                    showing ? 1 : 0);
			openOmniSwitchFirstTimeWarning();
			updatePreference();
        } else if (preference == mRecentSlim) {
            boolean useslim = ((Boolean)newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.USE_SLIM_RECENTS,
                    useslim ? 1 : 0);
			updatePreference();
        }
        return false;
    }
	
    private void openOmniSwitchFirstTimeWarning() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.omniswitch_first_time_title))
                .setMessage(getResources().getString(R.string.omniswitch_first_time_message))
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                }).show();
    }
	
    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.APPLICATION;
    }
	
    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.kangdroid_recents_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    return new ArrayList<String>();
                }
            };
}