package com.oneplus.settings.better;

import android.app.Application;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceCategory;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.internal.app.NightDisplayController;
import com.android.internal.app.NightDisplayController.Callback;
import com.android.internal.app.NightDisplayController.LocalTime;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.fuelgauge.WallOfTextPreference;
import com.oneplus.settings.OneplusColorManager;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory;
import com.oneplus.settings.ui.OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener;
import java.util.Calendar;
import java.util.TimeZone;

public class OPNightMode
  extends SettingsPreferenceFragment
  implements NightDisplayController.Callback, Preference.OnPreferenceChangeListener, OPNightModeLevelPreferenceCategory.OPNightModeLevelPreferenceChangeListener
{
  private static final int AUTO_ACTIVATE_CUSTOMIZED_VALUE = 2;
  private static final int DIALOG_TURN_OFF_TIME = 1;
  private static final int DIALOG_TURN_ON_TIME = 0;
  private static final String KEY_AUTO_ACTIVATE = "auto_activate";
  private static final String KEY_NIGHT_MODE_ENABLED_OP = "night_mode_enabled";
  private static final String KEY_NIGHT_MODE_LEVEL_OP = "night_mode_level_op";
  private static final String KEY_NIGHT_MODE_SUMMARY = "night_mode_summary";
  private static final String KEY_SET_TIME = "set_time";
  private static final String KEY_TURN_OFF_TIME = "turn_off_time";
  private static final String KEY_TURN_ON_TIME = "turn_on_time";
  private static final int NEVER_AUTO_VALUE = 0;
  private static final String NIGHT_MODE_ENABLED = "night_mode_enabled";
  private static final int SUNRISE_SUNSET_VALUE = 1;
  private static final String TAG = "OPNightMode";
  private boolean isSupportReadingMode;
  private ListPreference mAutoActivatePreference;
  private OneplusColorManager mCM;
  private NightDisplayController mController;
  private WallOfTextPreference mNightModSummary;
  private SwitchPreference mNightModeEnabledPreference;
  private OPNightModeLevelPreferenceCategory mNightModeLevelPreferenceCategory;
  private ContentObserver mNightModeSeekBarContentObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      int i = Settings.System.getIntForUser(OPNightMode.-wrap0(OPNightMode.this), "oem_nightmode_progress_status", 103, -2);
      if (OPNightMode.-get1(OPNightMode.this) != null) {
        OPNightMode.-get1(OPNightMode.this).setSeekBarProgress(i);
      }
      if (Settings.System.getIntForUser(OPNightMode.-wrap0(OPNightMode.this), "reading_mode_status", 0, -2) != 1) {}
      for (paramAnonymousBoolean = true;; paramAnonymousBoolean = false)
      {
        if (OPNightMode.-get1(OPNightMode.this) != null) {
          OPNightMode.-get1(OPNightMode.this).setEnabled(paramAnonymousBoolean);
        }
        return;
      }
    }
  };
  private PreferenceCategory mSetTimePreferenceCategory;
  private java.text.DateFormat mTimeFormatter;
  private Preference mTurnOffTimePreference;
  private Preference mTurnOnTimePreference;
  
  private int convertAutoMode(int paramInt)
  {
    if (paramInt == 0) {
      return 0;
    }
    if (paramInt == 1) {
      return 2;
    }
    return 1;
  }
  
  private String getFormattedTimeString(NightDisplayController.LocalTime paramLocalTime)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeZone(this.mTimeFormatter.getTimeZone());
    localCalendar.set(11, paramLocalTime.hourOfDay);
    localCalendar.set(12, paramLocalTime.minute);
    localCalendar.set(13, 0);
    localCalendar.set(14, 0);
    return this.mTimeFormatter.format(localCalendar.getTime());
  }
  
  private void updateAutoActivateModePreferenceDescription(int paramInt)
  {
    if (this.mAutoActivatePreference != null)
    {
      CharSequence[] arrayOfCharSequence = this.mAutoActivatePreference.getEntries();
      this.mAutoActivatePreference.setSummary(arrayOfCharSequence[paramInt]);
    }
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onActivated(boolean paramBoolean)
  {
    this.mNightModeEnabledPreference.setChecked(paramBoolean);
    this.mNightModeLevelPreferenceCategory.setEnabled(paramBoolean);
  }
  
  public void onAutoModeChanged(int paramInt)
  {
    this.mAutoActivatePreference.setValue(String.valueOf(paramInt));
    if (paramInt == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mTurnOnTimePreference.setVisible(bool);
      this.mTurnOffTimePreference.setVisible(bool);
      return;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230800);
    paramBundle = getContext();
    this.isSupportReadingMode = paramBundle.getPackageManager().hasSystemFeature("oem.read_mode.support");
    this.mController = new NightDisplayController(paramBundle);
    this.mNightModeEnabledPreference = ((SwitchPreference)findPreference("night_mode_enabled"));
    this.mAutoActivatePreference = ((ListPreference)findPreference("auto_activate"));
    this.mSetTimePreferenceCategory = ((PreferenceCategory)findPreference("set_time"));
    this.mTurnOnTimePreference = findPreference("turn_on_time");
    this.mTurnOffTimePreference = findPreference("turn_off_time");
    this.mNightModSummary = ((WallOfTextPreference)findPreference("night_mode_summary"));
    this.mNightModeLevelPreferenceCategory = ((OPNightModeLevelPreferenceCategory)findPreference("night_mode_level_op"));
    if (this.mNightModeEnabledPreference != null) {
      this.mNightModeEnabledPreference.setOnPreferenceChangeListener(this);
    }
    if (this.mNightModeLevelPreferenceCategory != null) {
      this.mNightModeLevelPreferenceCategory.setOPNightModeLevelSeekBarChangeListener(this);
    }
    this.mAutoActivatePreference.setValue(String.valueOf(this.mController.getAutoMode()));
    this.mAutoActivatePreference.setOnPreferenceChangeListener(this);
    this.mTimeFormatter = android.text.format.DateFormat.getTimeFormat(paramBundle);
    this.mTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    updateAutoActivateModePreferenceDescription(convertAutoMode(this.mController.getAutoMode()));
    this.mCM = new OneplusColorManager(SettingsBaseApplication.mApplication);
    if (Settings.System.getIntForUser(getContentResolver(), "reading_mode_status", 0, -2) != 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mNightModeLevelPreferenceCategory.setEnabled(bool);
      if (this.isSupportReadingMode) {
        this.mNightModSummary.setSummary(SettingsBaseApplication.mApplication.getText(2131690458));
      }
      return;
    }
  }
  
  public Dialog onCreateDialog(final int paramInt)
  {
    if ((paramInt == 0) || (paramInt == 1))
    {
      if (paramInt == 0) {}
      for (NightDisplayController.LocalTime localLocalTime = this.mController.getCustomStartTime();; localLocalTime = this.mController.getCustomEndTime())
      {
        Context localContext = getContext();
        boolean bool = android.text.format.DateFormat.is24HourFormat(localContext);
        new TimePickerDialog(localContext, new TimePickerDialog.OnTimeSetListener()
        {
          public void onTimeSet(TimePicker paramAnonymousTimePicker, int paramAnonymousInt1, int paramAnonymousInt2)
          {
            paramAnonymousTimePicker = new NightDisplayController.LocalTime(paramAnonymousInt1, paramAnonymousInt2);
            if (paramInt == 0)
            {
              if (String.valueOf(OPNightMode.-get0(OPNightMode.this).getCustomEndTime()).equals(String.valueOf(paramAnonymousTimePicker)))
              {
                Toast.makeText(OPNightMode.-wrap1(OPNightMode.this), 2131690243, 1).show();
                return;
              }
              OPNightMode.-get0(OPNightMode.this).setCustomStartTime(paramAnonymousTimePicker);
              return;
            }
            if (String.valueOf(OPNightMode.-get0(OPNightMode.this).getCustomStartTime()).equals(String.valueOf(paramAnonymousTimePicker)))
            {
              Toast.makeText(OPNightMode.-wrap1(OPNightMode.this), 2131690243, 1).show();
              return;
            }
            OPNightMode.-get0(OPNightMode.this).setCustomEndTime(paramAnonymousTimePicker);
          }
        }, localLocalTime.hourOfDay, localLocalTime.minute, bool);
      }
    }
    return super.onCreateDialog(paramInt);
  }
  
  public void onCustomEndTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    this.mTurnOffTimePreference.setSummary(getFormattedTimeString(paramLocalTime));
  }
  
  public void onCustomStartTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    this.mTurnOnTimePreference.setSummary(getFormattedTimeString(paramLocalTime));
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    paramPreference = paramPreference.getKey();
    if ("night_mode_enabled".equals(paramPreference))
    {
      boolean bool = ((Boolean)paramObject).booleanValue();
      this.mController.setActivated(bool);
    }
    for (;;)
    {
      return true;
      if ("auto_activate".equals(paramPreference))
      {
        int i = Integer.parseInt((String)paramObject);
        this.mController.setAutoMode(i);
        updateAutoActivateModePreferenceDescription(convertAutoMode(this.mController.getAutoMode()));
      }
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    String str = paramPreference.getKey();
    if ("turn_on_time".equals(str))
    {
      showDialog(0);
      return true;
    }
    if ("turn_off_time".equals(str))
    {
      showDialog(1);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
  {
    if (!paramBoolean) {
      return;
    }
    if (this.isSupportReadingMode)
    {
      this.mCM.setColorBalance(132 - paramInt - 90);
      return;
    }
    this.mCM.setColorBalance(132 - paramInt - 56);
  }
  
  public void onStart()
  {
    super.onStart();
    this.mController.setListener(this);
    onActivated(this.mController.isActivated());
    onAutoModeChanged(this.mController.getAutoMode());
    onCustomStartTimeChanged(this.mController.getCustomStartTime());
    onCustomEndTimeChanged(this.mController.getCustomEndTime());
    getContentResolver().registerContentObserver(Settings.System.getUriFor("oem_nightmode_progress_status"), true, this.mNightModeSeekBarContentObserver, -2);
    getContentResolver().registerContentObserver(Settings.System.getUriFor("reading_mode_status"), true, this.mNightModeSeekBarContentObserver, -2);
  }
  
  public void onStartTrackingTouch(SeekBar paramSeekBar)
  {
    if ((!this.mController.isActivated()) && (this.isSupportReadingMode)) {}
  }
  
  public void onStop()
  {
    super.onStop();
    this.mController.setListener(null);
    getContentResolver().unregisterContentObserver(this.mNightModeSeekBarContentObserver);
  }
  
  public void onStopTrackingTouch(SeekBar paramSeekBar)
  {
    Settings.System.putIntForUser(getContentResolver(), "oem_nightmode_progress_status", paramSeekBar.getProgress(), -2);
    this.mCM.setColorBalance(65024);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\better\OPNightMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */