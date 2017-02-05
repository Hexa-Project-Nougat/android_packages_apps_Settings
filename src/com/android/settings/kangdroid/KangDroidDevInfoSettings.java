/*
 * Copyright (C) 2017 Hexa-Project
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
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.telephony.CarrierConfigManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.HashMap;

public class KangDroidDevInfoSettings extends SettingsPreferenceFragment implements Indexable {
	private static final String LOG_TAG = "KangDroidDevInfoSettings";
	
	private static final String DEV_KANGDROID = "kdp_kangdroid";
	private static final String DEV_ANDRO_G = "kdp_andro_g";
	private static final String DEV_NAZUNAMOE = "kdp_nazunamoe";
	private static final String DEV_4SEASON = "kdp_4season";
	
    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.KANGDROID;
    }
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.kangdroid_dev_info);
		
		// TODO: Add some intent action when teammate's site is realdy
	}
}