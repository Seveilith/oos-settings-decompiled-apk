package com.android.settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.TimeZone;

public class DateTimeSettingsSetupWizard
  extends Activity
  implements View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener, PreferenceFragment.OnPreferenceStartFragmentCallback
{
  private static final String EXTRA_INITIAL_AUTO_DATETIME_VALUE = "extra_initial_auto_datetime_value";
  private static final String TAG = DateTimeSettingsSetupWizard.class.getSimpleName();
  private CompoundButton mAutoDateTimeButton;
  private DatePicker mDatePicker;
  private InputMethodManager mInputMethodManager;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      DateTimeSettingsSetupWizard.-wrap0(DateTimeSettingsSetupWizard.this);
    }
  };
  private TimeZone mSelectedTimeZone;
  private TimePicker mTimePicker;
  private SimpleAdapter mTimeZoneAdapter;
  private Button mTimeZoneButton;
  private ListPopupWindow mTimeZonePopup;
  private boolean mUsingXLargeLayout;
  
  private boolean isAutoDateTimeEnabled()
  {
    try
    {
      int i = Settings.Global.getInt(getContentResolver(), "auto_time");
      return i > 0;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException) {}
    return true;
  }
  
  private void showTimezonePicker(int paramInt)
  {
    View localView = findViewById(paramInt);
    if (localView == null)
    {
      Log.e(TAG, "Unable to find zone picker anchor view " + paramInt);
      return;
    }
    this.mTimeZonePopup = new ListPopupWindow(this, null);
    this.mTimeZonePopup.setWidth(localView.getWidth());
    this.mTimeZonePopup.setAnchorView(localView);
    this.mTimeZonePopup.setAdapter(this.mTimeZoneAdapter);
    this.mTimeZonePopup.setOnItemClickListener(this);
    this.mTimeZonePopup.setModal(true);
    this.mTimeZonePopup.show();
  }
  
  private void updateTimeAndDateDisplay()
  {
    if (!this.mUsingXLargeLayout) {
      return;
    }
    Calendar localCalendar = Calendar.getInstance();
    this.mTimeZoneButton.setText(localCalendar.getTimeZone().getDisplayName());
    this.mDatePicker.updateDate(localCalendar.get(1), localCalendar.get(2), localCalendar.get(5));
    this.mTimePicker.setCurrentHour(Integer.valueOf(localCalendar.get(11)));
    this.mTimePicker.setCurrentMinute(Integer.valueOf(localCalendar.get(12)));
  }
  
  public void initUiForXl()
  {
    boolean bool3 = true;
    Object localObject = TimeZone.getDefault();
    this.mSelectedTimeZone = ((TimeZone)localObject);
    this.mTimeZoneButton = ((Button)findViewById(2131362097));
    this.mTimeZoneButton.setText(((TimeZone)localObject).getDisplayName());
    this.mTimeZoneButton.setOnClickListener(this);
    localObject = getIntent();
    boolean bool1;
    if (((Intent)localObject).hasExtra("extra_initial_auto_datetime_value"))
    {
      bool1 = ((Intent)localObject).getBooleanExtra("extra_initial_auto_datetime_value", false);
      this.mAutoDateTimeButton = ((CompoundButton)findViewById(2131362099));
      this.mAutoDateTimeButton.setChecked(bool1);
      this.mAutoDateTimeButton.setOnCheckedChangeListener(this);
      this.mTimePicker = ((TimePicker)findViewById(2131362104));
      localObject = this.mTimePicker;
      if (!bool1) {
        break label238;
      }
    }
    label238:
    for (boolean bool2 = false;; bool2 = true)
    {
      ((TimePicker)localObject).setEnabled(bool2);
      this.mDatePicker = ((DatePicker)findViewById(2131362101));
      localObject = this.mDatePicker;
      bool2 = bool3;
      if (bool1) {
        bool2 = false;
      }
      ((DatePicker)localObject).setEnabled(bool2);
      this.mDatePicker.setCalendarViewShown(false);
      DateTimeSettings.configureDatePicker(this.mDatePicker);
      this.mInputMethodManager = ((InputMethodManager)getSystemService("input_method"));
      ((Button)findViewById(2131362013)).setOnClickListener(this);
      localObject = (Button)findViewById(2131362143);
      if (localObject != null) {
        ((Button)localObject).setOnClickListener(this);
      }
      return;
      bool1 = isAutoDateTimeEnabled();
      break;
    }
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    boolean bool2 = true;
    int i;
    if (paramCompoundButton == this.mAutoDateTimeButton)
    {
      paramCompoundButton = getContentResolver();
      if (!paramBoolean) {
        break label101;
      }
      i = 1;
      Settings.Global.putInt(paramCompoundButton, "auto_time", i);
      paramCompoundButton = this.mTimePicker;
      if (!paramBoolean) {
        break label106;
      }
    }
    label101:
    label106:
    for (boolean bool1 = false;; bool1 = true)
    {
      paramCompoundButton.setEnabled(bool1);
      paramCompoundButton = this.mDatePicker;
      bool1 = bool2;
      if (paramBoolean) {
        bool1 = false;
      }
      paramCompoundButton.setEnabled(bool1);
      if (paramBoolean)
      {
        paramCompoundButton = getCurrentFocus();
        if (paramCompoundButton != null)
        {
          this.mInputMethodManager.hideSoftInputFromWindow(paramCompoundButton.getWindowToken(), 0);
          paramCompoundButton.clearFocus();
        }
      }
      return;
      i = 0;
      break;
    }
  }
  
  public void onClick(View paramView)
  {
    switch (paramView.getId())
    {
    default: 
      return;
    case 2131362097: 
      showTimezonePicker(2131362097);
      return;
    case 2131362013: 
      if ((this.mSelectedTimeZone != null) && (!TimeZone.getDefault().equals(this.mSelectedTimeZone)))
      {
        Log.i(TAG, "Another TimeZone is selected by a user. Changing system TimeZone.");
        ((AlarmManager)getSystemService("alarm")).setTimeZone(this.mSelectedTimeZone.getID());
      }
      if (this.mAutoDateTimeButton != null)
      {
        paramView = getContentResolver();
        if (!this.mAutoDateTimeButton.isChecked()) {
          break label199;
        }
      }
      break;
    }
    label199:
    for (int i = 1;; i = 0)
    {
      Settings.Global.putInt(paramView, "auto_time", i);
      if (!this.mAutoDateTimeButton.isChecked())
      {
        DateTimeSettings.setDate(this, this.mDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        DateTimeSettings.setTime(this, this.mTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
      }
      setResult(-1);
      finish();
      return;
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    boolean bool = true;
    requestWindowFeature(1);
    super.onCreate(paramBundle);
    setContentView(2130968680);
    if (findViewById(2131362097) != null)
    {
      this.mUsingXLargeLayout = bool;
      if (!this.mUsingXLargeLayout) {
        break label83;
      }
      initUiForXl();
    }
    for (;;)
    {
      this.mTimeZoneAdapter = ZonePicker.constructTimezoneAdapter(this, false, 2130968681);
      if (!this.mUsingXLargeLayout) {
        findViewById(2131362093).setSystemUiVisibility(4194304);
      }
      return;
      bool = false;
      break;
      label83:
      findViewById(2131362013).setOnClickListener(this);
    }
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    paramAdapterView = ZonePicker.obtainTimeZoneFromItem(paramAdapterView.getItemAtPosition(paramInt));
    if (this.mUsingXLargeLayout)
    {
      this.mSelectedTimeZone = paramAdapterView;
      paramView = Calendar.getInstance(paramAdapterView);
      if (this.mTimeZoneButton != null) {
        this.mTimeZoneButton.setText(paramAdapterView.getDisplayName());
      }
      this.mDatePicker.updateDate(paramView.get(1), paramView.get(2), paramView.get(5));
      this.mTimePicker.setCurrentHour(Integer.valueOf(paramView.get(11)));
      this.mTimePicker.setCurrentMinute(Integer.valueOf(paramView.get(12)));
    }
    for (;;)
    {
      this.mTimeZonePopup.dismiss();
      return;
      ((AlarmManager)getSystemService("alarm")).setTimeZone(paramAdapterView.getID());
      ((DateTimeSettings)getFragmentManager().findFragmentById(2131362095)).updateTimeAndDateDisplay(this);
    }
  }
  
  public void onPause()
  {
    super.onPause();
    unregisterReceiver(this.mIntentReceiver);
  }
  
  public boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference)
  {
    showTimezonePicker(2131362094);
    return true;
  }
  
  public void onResume()
  {
    super.onResume();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIME_TICK");
    localIntentFilter.addAction("android.intent.action.TIME_SET");
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    registerReceiver(this.mIntentReceiver, localIntentFilter, null, null);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DateTimeSettingsSetupWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */