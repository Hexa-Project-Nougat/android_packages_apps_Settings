package com.android.settings.kangdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import com.android.settings.rr.StatusBarWeather;

public class StatusBarWeatherActivity extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  // TODO Auto-generated method stub
  super.onCreate(savedInstanceState);
  
  getFragmentManager().beginTransaction().replace(android.R.id.content,
                new StatusBarWeather()).commit();
 }

}