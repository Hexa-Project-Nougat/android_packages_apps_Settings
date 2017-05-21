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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.provider.Settings;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.kangdroid.KangDroidSeekBarPreference;
import com.android.settings.cyanogenmod.SecureSettingSwitchPreference;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.util.rr.PackageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class KangDroidQuickSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Indexable {
	private static final String PREF_QSLOCK = "lockscreen_qs_disabled";
	private static final String QS_CAT = "qs_main_category";
	private static final int MY_USER_ID = UserHandle.myUserId();

    private static final String DEFAULT_PACKAGE = "com.android.systemui";
	
	private SecureSettingSwitchPreference mQsLock;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.kangdroid_qs_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
		
		final LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());
		PreferenceCategory qscat = (PreferenceCategory) findPreference(QS_CAT);
		
        mQsLock = (SecureSettingSwitchPreference) prefScreen.findPreference(PREF_QSLOCK);
        if (!lockPatternUtils.isSecure(MY_USER_ID)) {
            qscat.removePreference(mQsLock);
        }

        String settingHeaderPackage = Settings.System.getString(getContentResolver(),
                Settings.System.STATUS_BAR_DAYLIGHT_HEADER_PACK);
        if (settingHeaderPackage == null) {
            settingHeaderPackage = DEFAULT_PACKAGE;
        }

         List<String> entries = new ArrayList<String>();
         List<String> values = new ArrayList<String>();

    }
	
    @Override
    public void onResume() {
        super.onResume();
    }
	
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return false;
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
                    sir.xmlResId = R.xml.kangdroid_qs_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    return new ArrayList<String>();
                }
            };
}