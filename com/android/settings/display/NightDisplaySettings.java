package com.android.settings.display;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.TwoStatePreference;
import android.widget.TimePicker;
import com.android.internal.app.NightDisplayController;
import com.android.internal.app.NightDisplayController.Callback;
import com.android.internal.app.NightDisplayController.LocalTime;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Calendar;
import java.util.TimeZone;

public class NightDisplaySettings
  extends SettingsPreferenceFragment
  implements NightDisplayController.Callback, Preference.OnPreferenceChangeListener
{
  private static final int DIALOG_END_TIME = 1;
  private static final int DIALOG_START_TIME = 0;
  private static final String KEY_NIGHT_DISPLAY_ACTIVATED = "night_display_activated";
  private static final String KEY_NIGHT_DISPLAY_AUTO_MODE = "night_display_auto_mode";
  private static final String KEY_NIGHT_DISPLAY_END_TIME = "night_display_end_time";
  private static final String KEY_NIGHT_DISPLAY_START_TIME = "night_display_start_time";
  private TwoStatePreference mActivatedPreference;
  private DropDownPreference mAutoModePreference;
  private NightDisplayController mController;
  private Preference mEndTimePreference;
  private Preference mStartTimePreference;
  private java.text.DateFormat mTimeFormatter;
  
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
  
  protected int getMetricsCategory()
  {
    return 488;
  }
  
  public void onActivated(boolean paramBoolean)
  {
    this.mActivatedPreference.setChecked(paramBoolean);
  }
  
  public void onAutoModeChanged(int paramInt)
  {
    this.mAutoModePreference.setValue(String.valueOf(paramInt));
    if (paramInt == 1) {}
    for (boolean bool = true;; bool = false)
    {
      this.mStartTimePreference.setVisible(bool);
      this.mEndTimePreference.setVisible(bool);
      return;
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getContext();
    this.mController = new NightDisplayController(paramBundle);
    this.mTimeFormatter = android.text.format.DateFormat.getTimeFormat(paramBundle);
    this.mTimeFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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
              NightDisplaySettings.-get0(NightDisplaySettings.this).setCustomStartTime(paramAnonymousTimePicker);
              return;
            }
            NightDisplaySettings.-get0(NightDisplaySettings.this).setCustomEndTime(paramAnonymousTimePicker);
          }
        }, localLocalTime.hourOfDay, localLocalTime.minute, bool);
      }
    }
    return super.onCreateDialog(paramInt);
  }
  
  public void onCreatePreferences(Bundle paramBundle, String paramString)
  {
    super.onCreatePreferences(paramBundle, paramString);
    addPreferencesFromResource(2131230782);
    this.mAutoModePreference = ((DropDownPreference)findPreference("night_display_auto_mode"));
    this.mStartTimePreference = findPreference("night_display_start_time");
    this.mEndTimePreference = findPreference("night_display_end_time");
    this.mActivatedPreference = ((TwoStatePreference)findPreference("night_display_activated"));
    this.mAutoModePreference.setEntries(new CharSequence[] { getString(2131691610), getString(2131691611), getString(2131691612) });
    this.mAutoModePreference.setEntryValues(new CharSequence[] { String.valueOf(0), String.valueOf(1), String.valueOf(2) });
    this.mAutoModePreference.setOnPreferenceChangeListener(this);
    this.mActivatedPreference.setOnPreferenceChangeListener(this);
  }
  
  public void onCustomEndTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    this.mEndTimePreference.setSummary(getFormattedTimeString(paramLocalTime));
  }
  
  public void onCustomStartTimeChanged(NightDisplayController.LocalTime paramLocalTime)
  {
    this.mStartTimePreference.setSummary(getFormattedTimeString(paramLocalTime));
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mAutoModePreference) {
      return this.mController.setAutoMode(Integer.parseInt((String)paramObject));
    }
    if (paramPreference == this.mActivatedPreference) {
      return this.mController.setActivated(((Boolean)paramObject).booleanValue());
    }
    return false;
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (paramPreference == this.mStartTimePreference)
    {
      showDialog(0);
      return true;
    }
    if (paramPreference == this.mEndTimePreference)
    {
      showDialog(1);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onStart()
  {
    super.onStart();
    this.mController.setListener(this);
    onActivated(this.mController.isActivated());
    onAutoModeChanged(this.mController.getAutoMode());
    onCustomStartTimeChanged(this.mController.getCustomStartTime());
    onCustomEndTimeChanged(this.mController.getCustomEndTime());
  }
  
  public void onStop()
  {
    super.onStop();
    this.mController.setListener(null);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\NightDisplaySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */