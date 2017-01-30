
/*
 * Copyright (C) 2016 The CyanogenMod project
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

import android.app.Activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManagerGlobal;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.kangdroid.KangDroidSeekBarPreference;

import java.util.List;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.providers.CMSettings;

public class KangDroidNavBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SystemSettings";

    private static final String KEY_NAVIGATION_BAR_LEFT = "navigation_bar_left";
	private static final String NAVBAR_DYNAMIC = "navbar_dynamic";

    private SwitchPreference mNavigationBarLeftPref;
	
	private SwitchPreference mNavbarDynamic;
	
	public static final int KEY_MASK_HOME = 0x01;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.kangdroid_nav_bar_settings);

        final Resources res = getResources();
		final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
		Activity activity = getActivity();

        mHandler = new Handler();
        // Navigation bar left
        mNavigationBarLeftPref = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_LEFT);
		
		mNavbarDynamic = (SwitchPreference) findPreference(NAVBAR_DYNAMIC);

        boolean isDynamic = Settings.System.getInt(resolver,
                Settings.System.NAVBAR_DYNAMIC, 0) == 1;
        mNavbarDynamic.setChecked(isDynamic);
        mNavbarDynamic.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		final ContentResolver resolver = getActivity().getContentResolver();
        if (preference.equals(mNavbarDynamic)) {
            boolean isDynamic = ((Boolean)newValue);
            Settings.System.putInt(resolver, Settings.System.NAVBAR_DYNAMIC,
                    isDynamic ? 1 : 0);
            Toast.makeText(getActivity(), R.string.restart_app_required,
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }
	
    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.KANGDROID;
    }
}
