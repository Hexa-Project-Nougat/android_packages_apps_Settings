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
import com.android.settings.util.Helpers;

import com.android.internal.logging.MetricsProto.MetricsEvent;

import java.util.ArrayList;
import java.util.List;

public class KangDroidRecentsSettings extends SettingsPreferenceFragment implements Indexable, Preference.OnPreferenceChangeListener {
	private static final String RECENTS_TYPE = "navigation_bar_recents";
	private static final String SLIM_RECENTS_PREFERENCE = "slim_recents_settings_kdp";
	private static final String OMNISWITCH_RECENTS_PREFERENCE = "omni_switch_settings_kdp";
	private static final String AOSP_RECENTS_SETTINGS = "aosp_settings_kdp";
	
	private ListPreference mRecentsType;
	private PreferenceScreen mSlimRecents;
	private PreferenceScreen mOmniSwitch;
	private PreferenceScreen mAOSPRecents;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.kangdroid_recents_settings);
		mSlimRecents = (PreferenceScreen) findPreference(SLIM_RECENTS_PREFERENCE);
		mOmniSwitch = (PreferenceScreen) findPreference(OMNISWITCH_RECENTS_PREFERENCE);
		mAOSPRecents = (PreferenceScreen) findPreference(AOSP_RECENTS_SETTINGS);
		
        mRecentsType = (ListPreference) findPreference(RECENTS_TYPE);
        int type = Settings.System.getIntForUser(getActivity().getContentResolver(),
                            Settings.System.NAVIGATION_BAR_RECENTS, 0,
                            UserHandle.USER_CURRENT);
        mRecentsType.setValue(String.valueOf(type));
        mRecentsType.setSummary(mRecentsType.getEntry());
        mRecentsType.setOnPreferenceChangeListener(this);
        updatePreference(type);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentsType) {
            Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_RECENTS,
                    Integer.valueOf((String) newValue));
            int val = Integer.parseInt((String) newValue);
            if (val== 0 || val == 1) {
                Helpers.showSystemUIrestartDialog(getActivity());
            }
            mRecentsType.setValue(String.valueOf(newValue));
            mRecentsType.setSummary(mRecentsType.getEntry());
            updatePreference(val);
        } 
		 
        return false;
    }
	
    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.APPLICATION;
    }
	
    public void updatePreference(int type) {
        if(type == 0 || type == 2) { //aosp-grid enable
           mSlimRecents.setEnabled(false);
           mOmniSwitch.setEnabled(false);
		   mAOSPRecents.setEnabled(true);
            Settings.System.putInt(getContentResolver(), Settings.System.RECENTS_USE_OMNISWITCH,
				 	 0);
            Settings.System.putInt(getContentResolver(), Settings.System.USE_SLIM_RECENTS,
				 	 0);
        } else if (type == 3) { //slim-enable
           mSlimRecents.setEnabled(true);
           mOmniSwitch.setEnabled(false);
		   mAOSPRecents.setEnabled(false);
           Settings.System.putInt(getContentResolver(), Settings.System.RECENTS_USE_OMNISWITCH,
			 	 0);
           Settings.System.putInt(getContentResolver(), Settings.System.USE_SLIM_RECENTS,
			 	 1);
        } else if (type == 1) { //omniswitch-enable
           mSlimRecents.setEnabled(false);
           mOmniSwitch.setEnabled(true);
		   mAOSPRecents.setEnabled(false);
           Settings.System.putInt(getContentResolver(), Settings.System.RECENTS_USE_OMNISWITCH,
			 	 1);
           Settings.System.putInt(getContentResolver(), Settings.System.USE_SLIM_RECENTS,
			 	 0);
        }
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