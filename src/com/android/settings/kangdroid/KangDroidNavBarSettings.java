
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
import com.android.internal.utils.du.ActionConstants;
import com.android.internal.utils.du.Config;
import com.android.internal.utils.du.DUActionUtils;
import com.android.internal.utils.du.Config.ButtonConfig;
import com.android.settings.kangdroid.KangDroidSeekBarPreference;

import java.util.List;

import cyanogenmod.hardware.CMHardwareManager;
import cyanogenmod.providers.CMSettings;

public class KangDroidNavBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SystemSettings";

    private static final String KEY_NAVIGATION_BAR_LEFT = "navigation_bar_left";
    private static final String CATEGORY_NAVBAR = "navigation_bar_category";
	private static final String NAVBAR_VISIBILITY = "navbar_visibility";
    private static final String KEY_NAVBAR_MODE = "navbar_mode";
    private static final String KEY_AOSP_NAVBAR_SETTINGS = "aosp_navbar_settings";
    private static final String KEY_FLING_NAVBAR_SETTINGS = "fling_settings";
    private static final String KEY_SMARTBAR_SETTINGS = "smartbar_settings";
    private static final String KEY_NAVIGATION_BAR_SIZE = "navigation_bar_size";
	
    private static final String KEY_NAVIGATION_HEIGHT_PORT = "navbar_height_portrait";
    private static final String KEY_NAVIGATION_HEIGHT_LAND = "navbar_height_landscape";
    private static final String KEY_NAVIGATION_WIDTH = "navbar_width";
	private static final String NAVBAR_DYNAMIC = "navbar_dynamic";

    private SwitchPreference mDisableNavigationKeys;
    private SwitchPreference mNavigationBarLeftPref;
	
    private SwitchPreference mNavbarVisibility;
    private ListPreference mNavbarMode;
    private PreferenceScreen mFlingSettings;
    private PreferenceScreen mSmartbarSettings;
	
    private KangDroidSeekBarPreference mBarHeightPort;
    private KangDroidSeekBarPreference mBarHeightLand;
    private KangDroidSeekBarPreference mBarWidth;
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
		
		final int deviceKeys = getResources().getInteger(
			                com.android.internal.R.integer.config_deviceHardwareKeys);
		final boolean hasHomeKey = (deviceKeys & KEY_MASK_HOME) != 0;

        // Navigation bar left
        mNavigationBarLeftPref = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_LEFT);
		
		mNavbarDynamic = (SwitchPreference) findPreference(NAVBAR_DYNAMIC);
		
        mNavbarVisibility = (SwitchPreference) findPreference(NAVBAR_VISIBILITY);
        mNavbarMode = (ListPreference) findPreference(KEY_NAVBAR_MODE);
        mFlingSettings = (PreferenceScreen) findPreference(KEY_FLING_NAVBAR_SETTINGS);
        mSmartbarSettings = (PreferenceScreen) findPreference(KEY_SMARTBAR_SETTINGS);

        boolean showing = Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.NAVIGATION_BAR_VISIBLE,
                DUActionUtils.hasNavbarByDefault(getActivity()) ? 1 : 0) != 0;
        updateBarVisibleAndUpdatePrefs(showing);
        mNavbarVisibility.setOnPreferenceChangeListener(this);

        int mode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.NAVIGATION_BAR_MODE,
                0);

        // Smartbar moved from 2 to 0, deprecating old navbar
        if (mode == 2) {
            mode = 0;
        }
        updateBarModeSettings(mode);
        mNavbarMode.setOnPreferenceChangeListener(this);
		
        int size = Settings.Secure.getIntForUser(getContentResolver(),
                Settings.Secure.NAVIGATION_BAR_HEIGHT, 100, UserHandle.USER_CURRENT);
        mBarHeightPort = (KangDroidSeekBarPreference) findPreference(KEY_NAVIGATION_HEIGHT_PORT);
        mBarHeightPort.setValue(size);
        mBarHeightPort.setOnPreferenceChangeListener(this);

           int sizeone = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.NAVIGATION_BAR_WIDTH, 100, UserHandle.USER_CURRENT);
            mBarWidth = (KangDroidSeekBarPreference) findPreference(KEY_NAVIGATION_WIDTH);
            mBarWidth.setValue(sizeone);
            mBarWidth.setOnPreferenceChangeListener(this);
			
           int sizetwo = Settings.Secure.getIntForUser(getContentResolver(),
                    Settings.Secure.NAVIGATION_BAR_HEIGHT_LANDSCAPE, 100, UserHandle.USER_CURRENT);
            mBarHeightLand = (KangDroidSeekBarPreference) findPreference(KEY_NAVIGATION_HEIGHT_LAND);
            mBarHeightLand.setValue(sizetwo);
            mBarHeightLand.setOnPreferenceChangeListener(this);

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
        if (preference == mNavbarMode) {
	            int mode = Integer.parseInt(((String) newValue).toString());
	            Settings.Secure.putInt(getContentResolver(),
	                    Settings.Secure.NAVIGATION_BAR_MODE, mode);
	            updateBarModeSettings(mode);
	            return true;
        } else if (preference == mNavbarVisibility) {
	            boolean showing = ((Boolean)newValue);
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.NAVIGATION_BAR_VISIBLE,
                    showing ? 1 : 0);
            updateBarVisibleAndUpdatePrefs(showing);
	            return true;
        } else if (preference == mBarHeightPort) {
            int val = (Integer) newValue;
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.NAVIGATION_BAR_HEIGHT, val, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mBarHeightLand) {
            int val = (Integer) newValue;
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.NAVIGATION_BAR_HEIGHT_LANDSCAPE, val, UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mBarWidth) {
            int val = (Integer) newValue;
            Settings.Secure.putIntForUser(getContentResolver(),
                    Settings.Secure.NAVIGATION_BAR_WIDTH, val, UserHandle.USER_CURRENT);
            return true;
        } else if (preference.equals(mNavbarDynamic)) {
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
	
    private void updateBarModeSettings(int mode) {
        mNavbarMode.setValue(String.valueOf(mode));
        mSmartbarSettings.setEnabled(mode == 0);
        mSmartbarSettings.setSelectable(mode == 0);
        mFlingSettings.setEnabled(mode == 1);
        mFlingSettings.setSelectable(mode == 1);
    }

    private void updateBarVisibleAndUpdatePrefs(boolean showing) {
        mNavbarVisibility.setChecked(showing);
		mNavbarDynamic.setEnabled(mNavbarVisibility.isChecked());
    }
}
