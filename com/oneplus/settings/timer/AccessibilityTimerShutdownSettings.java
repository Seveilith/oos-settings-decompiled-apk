package com.oneplus.settings.timer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Calendar;

public class AccessibilityTimerShutdownSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final String ACC_TIMERDOWN_TIMESETTINGS_KEY = "accessibility_timer_startup_device_settings";
  private static final String ACC_TIMERUP_TIMESETTINGS_KEY = "accessibility_timer_startup_device_settings";
  private static final String ACC_TIMER_SHUTDOWN_KEY = "accessibility_timer_shutdown_device";
  private static final String ACC_TIMER_STARTUP_KEY = "accessibility_timer_startup_device";
  private AlarmManager am;
  private Calendar c;
  private Intent intent;
  private SwitchPreference mShutdownPreference;
  private SwitchPreference mStartupPreference;
  private Preference mTimeDownSettingsPreference;
  private Preference mTimeUpSettingsPreference;
  private PendingIntent pIntent;
  
  private void initView()
  {
    boolean bool2 = false;
    this.mStartupPreference = ((SwitchPreference)findPreference("accessibility_timer_startup_device"));
    this.mStartupPreference.setOnPreferenceClickListener(this);
    SwitchPreference localSwitchPreference = this.mStartupPreference;
    if (Settings.System.getInt(getActivity().getContentResolver(), "oem_startup_timer", 1) == 0)
    {
      bool1 = false;
      localSwitchPreference.setChecked(bool1);
      this.mShutdownPreference = ((SwitchPreference)findPreference("accessibility_timer_shutdown_device"));
      this.mShutdownPreference.setOnPreferenceClickListener(this);
      localSwitchPreference = this.mShutdownPreference;
      if (Settings.System.getInt(getActivity().getContentResolver(), "oem_shutdown_timer", 1) != 0) {
        break label142;
      }
    }
    label142:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      localSwitchPreference.setChecked(bool1);
      this.mTimeUpSettingsPreference = findPreference("accessibility_timer_startup_device_settings");
      this.mTimeUpSettingsPreference.setOnPreferenceClickListener(this);
      this.mTimeDownSettingsPreference = findPreference("accessibility_timer_startup_device_settings");
      this.mTimeDownSettingsPreference.setOnPreferenceClickListener(this);
      return;
      bool1 = true;
      break;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230783);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    int j = 1;
    int i = 1;
    if (paramPreference.getKey().equals("accessibility_timer_shutdown_device"))
    {
      paramPreference = getActivity().getContentResolver();
      if (!this.mStartupPreference.isChecked()) {
        i = 0;
      }
      Settings.System.getInt(paramPreference, "oem_shutdown_timer", i);
    }
    do
    {
      return false;
      if (paramPreference.getKey().equals("accessibility_timer_startup_device"))
      {
        paramPreference = getActivity().getContentResolver();
        i = j;
        if (!this.mStartupPreference.isChecked()) {
          i = 0;
        }
        Settings.System.getInt(paramPreference, "oem_startup_timer", i);
        return false;
      }
      if (paramPreference.getKey().equals("accessibility_timer_startup_device_settings"))
      {
        this.c.setTimeInMillis(System.currentTimeMillis());
        i = this.c.get(11);
        j = this.c.get(12);
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
        {
          public void onTimeSet(TimePicker paramAnonymousTimePicker, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).setTimeInMillis(System.currentTimeMillis());
            AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).set(11, paramAnonymousInt1);
            AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).set(12, paramAnonymousInt2);
            AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).set(13, 0);
            AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).set(14, 0);
            AccessibilityTimerShutdownSettings.-set1(AccessibilityTimerShutdownSettings.this, new Intent("com.android.settings.action.REQUEST_POWER_ON"));
            AccessibilityTimerShutdownSettings.-set2(AccessibilityTimerShutdownSettings.this, PendingIntent.getBroadcast(AccessibilityTimerShutdownSettings.this.getActivity(), 0, AccessibilityTimerShutdownSettings.-get2(AccessibilityTimerShutdownSettings.this), 0));
            AccessibilityTimerShutdownSettings.-set0(AccessibilityTimerShutdownSettings.this, (AlarmManager)AccessibilityTimerShutdownSettings.-wrap0(AccessibilityTimerShutdownSettings.this, "alarm"));
            AccessibilityTimerShutdownSettings.-get0(AccessibilityTimerShutdownSettings.this).set(0, AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).getTimeInMillis(), AccessibilityTimerShutdownSettings.-get4(AccessibilityTimerShutdownSettings.this));
            AccessibilityTimerShutdownSettings.-get0(AccessibilityTimerShutdownSettings.this).setRepeating(0, AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).getTimeInMillis(), 10000L, AccessibilityTimerShutdownSettings.-get4(AccessibilityTimerShutdownSettings.this));
            AccessibilityTimerShutdownSettings.-get3(AccessibilityTimerShutdownSettings.this).setSummary("设置的闹钟时间为:" + paramAnonymousInt1 + ":" + paramAnonymousInt2);
          }
        }, i, j, true).show();
        return true;
      }
    } while (!paramPreference.getKey().equals("accessibility_timer_startup_device_settings"));
    this.c.setTimeInMillis(System.currentTimeMillis());
    i = this.c.get(11);
    j = this.c.get(12);
    new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
    {
      public void onTimeSet(TimePicker paramAnonymousTimePicker, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).setTimeInMillis(System.currentTimeMillis());
        AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).set(11, paramAnonymousInt1);
        AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).set(12, paramAnonymousInt2);
        AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).set(13, 0);
        AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).set(14, 0);
        AccessibilityTimerShutdownSettings.-set1(AccessibilityTimerShutdownSettings.this, new Intent("com.android.settings.action.REQUEST_POWER_OFF"));
        AccessibilityTimerShutdownSettings.-set2(AccessibilityTimerShutdownSettings.this, PendingIntent.getBroadcast(AccessibilityTimerShutdownSettings.this.getActivity(), 0, AccessibilityTimerShutdownSettings.-get2(AccessibilityTimerShutdownSettings.this), 0));
        AccessibilityTimerShutdownSettings.-set0(AccessibilityTimerShutdownSettings.this, (AlarmManager)AccessibilityTimerShutdownSettings.-wrap0(AccessibilityTimerShutdownSettings.this, "alarm"));
        AccessibilityTimerShutdownSettings.-get0(AccessibilityTimerShutdownSettings.this).set(0, AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).getTimeInMillis(), AccessibilityTimerShutdownSettings.-get4(AccessibilityTimerShutdownSettings.this));
        AccessibilityTimerShutdownSettings.-get0(AccessibilityTimerShutdownSettings.this).setRepeating(0, AccessibilityTimerShutdownSettings.-get1(AccessibilityTimerShutdownSettings.this).getTimeInMillis(), 10000L, AccessibilityTimerShutdownSettings.-get4(AccessibilityTimerShutdownSettings.this));
        AccessibilityTimerShutdownSettings.-get3(AccessibilityTimerShutdownSettings.this).setSummary("设置的闹钟时间为:" + paramAnonymousInt1 + ":" + paramAnonymousInt2);
      }
    }, i, j, true).show();
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    initView();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\AccessibilityTimerShutdownSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */