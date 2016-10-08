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

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.SeekBarPreference;

import java.util.ArrayList;
import java.util.List;

public class KangDroidPowerMenuSettings extends SettingsPreferenceFragment implements Indexable, Preference.OnPreferenceChangeListener {

	private static final String PREF_ON_THE_GO_ALPHA = "on_the_go_alpha";
	
	private SeekBarPreference mOnTheGoAlphaPref;	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.kangdroid_power_menu);
		
        mOnTheGoAlphaPref = (SeekBarPreference) findPreference(PREF_ON_THE_GO_ALPHA);
		int otgalpha = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.ON_THE_GO_ALPHA, 50);
		mOnTheGoAlphaPref.setProgress(otgalpha);
        mOnTheGoAlphaPref.setOnPreferenceChangeListener(this);

    }
	
    @Override
    public void onResume() {
        super.onResume();
    }
	
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mOnTheGoAlphaPref) {
			int otgalpha = (Integer) newValue;
			Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ON_THE_GO_ALPHA, otgalpha);
            return true;
        }
        return false;
    }
	
    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.TESTING;
    }
}