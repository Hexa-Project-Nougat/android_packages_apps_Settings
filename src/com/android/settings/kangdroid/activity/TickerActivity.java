package com.android.settings.kangdroid.activity;

import android.app.Activity;
import android.os.Bundle;
import com.crdroid.settings.fragments.Ticker;

public class TickerActivity extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  // TODO Auto-generated method stub
  super.onCreate(savedInstanceState);
  
  getFragmentManager().beginTransaction().replace(android.R.id.content,
                new Ticker()).commit();
 }

}