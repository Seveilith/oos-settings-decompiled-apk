package com.oneplus.settings.timer.timepower;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.view.ContextThemeWrapper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import java.lang.reflect.Array;

public class TimepowerSettingsFragment
  extends SettingsPreferenceFragment
  implements TimePickerDialog.OnTimeSetListener, Preference.OnPreferenceChangeListener
{
  private static final String EXTRA_DISPLAY_TIME = "display_time";
  private static final String EXTRA_HOUR = "hour";
  private static final String EXTRA_IS_24HOUR = "24hour";
  private static final String EXTRA_MINUTE = "minute";
  private static final String EXTRA_POWER_STATE = "power_state";
  private static final String EXTRA_POWER_TYPE = "power_type";
  private static final int ITEM_COUNT = 2;
  private static final String KEY_POWER_OFF_SETTINGS = "oneplus_power_off_settings";
  private static final String KEY_POWER_ON_SETTINGS = "oneplus_power_on_settings";
  private static final int POWER_OFF_TYPE = 1;
  private static final int POWER_ON_TYPE = 0;
  private static final String PREFERENCE_POWER_OFF_SETTINGS = "power_off_settings";
  private static final String PREFERENCE_POWER_OFF_STATE = "power_off_switch";
  private static final String PREFERENCE_POWER_ON_SETTINGS = "power_on_settings";
  private static final String PREFERENCE_POWER_ON_STATE = "power_on_switch";
  private static final int REQUEST_CODE_POWER_OFF = 1;
  private static final int REQUEST_CODE_POWER_ON = 0;
  private static final String TAG = "TimepowerSettingsFragment";
  private static boolean misCheckedPoweroff = false;
  private static boolean misCheckedPoweron = false;
  private int mCode;
  private boolean mDlgVisible = false;
  private TimepowerPreference mPowerOffPref;
  private Preference mPowerOffPreference;
  private SwitchPreference mPowerOffStatePref;
  private TimepowerPreference mPowerOnPref;
  private Preference mPowerOnPreference;
  private SwitchPreference mPowerOnStatePref;
  private boolean mPowerState;
  private boolean[][] mStateArray = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { 2, 2 });
  private int[][] mTimeArray = (int[][])Array.newInstance(Integer.TYPE, new int[] { 2, 2 });
  private TimePicker mTimePicker;
  DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener()
  {
    public void onDismiss(DialogInterface paramAnonymousDialogInterface)
    {
      TimepowerSettingsFragment.-set0(TimepowerSettingsFragment.this, false);
    }
  };
  
  private void ReturnData(int paramInt1, int paramInt2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("power_state", this.mPowerState);
    localBundle.putInt("hour", paramInt1);
    localBundle.putInt("minute", paramInt2);
    Intent localIntent = new Intent();
    localIntent.putExtras(localBundle);
    returnNewTimeSetResult(this.mCode, localIntent);
  }
  
  private static int boolToInt(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 1;
    }
    return 0;
  }
  
  private String formatTime(int paramInt1, int paramInt2)
  {
    if (is24Hour()) {
      return String.format("%1$02d", new Object[] { Integer.valueOf(paramInt1) }) + ":" + String.format("%1$02d", new Object[] { Integer.valueOf(paramInt2) });
    }
    String str2 = getString(2131690246);
    String str1;
    int i;
    if (paramInt1 >= 12)
    {
      str2 = getString(2131690247);
      str1 = str2;
      i = paramInt1;
      if (paramInt1 > 12)
      {
        i = paramInt1 - 12;
        str1 = str2;
      }
    }
    for (;;)
    {
      return str1 + String.format("%1$02d", new Object[] { Integer.valueOf(i) }) + ":" + String.format("%1$02d", new Object[] { Integer.valueOf(paramInt2) });
      str1 = str2;
      i = paramInt1;
      if (paramInt1 == 0)
      {
        i = 12;
        str1 = str2;
      }
    }
  }
  
  private Intent getEditIntent(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, String paramString)
  {
    if ((paramInt1 != 0) && (paramInt1 != 1)) {
      return null;
    }
    Intent localIntent = new Intent();
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("24hour", is24Hour());
    localBundle.putBoolean("power_state", paramBoolean);
    localBundle.putString("display_time", paramString);
    localBundle.putInt("hour", paramInt2);
    localBundle.putInt("minute", paramInt3);
    localBundle.putInt("power_type", paramInt1);
    localIntent.putExtras(localBundle);
    return localIntent;
  }
  
  public static boolean getPowerOnOffStatus(String paramString)
  {
    if (paramString.equals("PowerOnFlag")) {
      return misCheckedPoweron;
    }
    if (paramString.equals("PowerOffFlag")) {
      return misCheckedPoweroff;
    }
    return false;
  }
  
  private Intent getTimeSettingsIntent(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      return null;
    }
    int k = this.mStateArray[0][0];
    Log.i("TIMER", this.mPowerOnPreference.getSummary().toString());
    String str = this.mPowerOnPreference.getSummary().toString();
    int i = this.mTimeArray[0][0];
    int j = this.mTimeArray[0][1];
    if (paramInt == 1)
    {
      k = this.mStateArray[1][0];
      this.mPowerOffPref.getTitle().toString();
      Log.i("TIMER", this.mPowerOffPreference.getSummary().toString());
      str = this.mPowerOffPreference.getSummary().toString();
      i = this.mTimeArray[1][0];
      j = this.mTimeArray[1][1];
    }
    return getEditIntent(paramInt, i, j, k, str);
  }
  
  private void init()
  {
    readData();
    int i = this.mStateArray[0][1];
    String str1 = formatTime(this.mTimeArray[0][0], this.mTimeArray[0][1]);
    int j = this.mStateArray[1][1];
    String str2 = formatTime(this.mTimeArray[1][0], this.mTimeArray[1][1]);
    this.mPowerOnStatePref = ((SwitchPreference)findPreference("power_on_switch"));
    this.mPowerOnStatePref.setChecked(i);
    this.mPowerOnStatePref.setOnPreferenceChangeListener(this);
    this.mPowerOffStatePref = ((SwitchPreference)findPreference("power_off_switch"));
    this.mPowerOffStatePref.setChecked(j);
    this.mPowerOffStatePref.setOnPreferenceChangeListener(this);
    this.mPowerOnPref = ((TimepowerPreference)findPreference("power_on_settings"));
    this.mPowerOnPref.setTitle(str1);
    this.mPowerOnPref.setViewClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (TimepowerSettingsFragment.-get0(TimepowerSettingsFragment.this)) {
          return;
        }
        TimepowerSettingsFragment.-wrap1(TimepowerSettingsFragment.this, TimepowerSettingsFragment.-wrap0(TimepowerSettingsFragment.this, 0), 0);
      }
    });
    this.mPowerOnPreference = findPreference("oneplus_power_on_settings");
    this.mPowerOnPreference.setSummary(str1);
    this.mPowerOnPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        if (TimepowerSettingsFragment.-get0(TimepowerSettingsFragment.this)) {
          return true;
        }
        TimepowerSettingsFragment.-wrap1(TimepowerSettingsFragment.this, TimepowerSettingsFragment.-wrap0(TimepowerSettingsFragment.this, 0), 0);
        return false;
      }
    });
    this.mPowerOffPref = ((TimepowerPreference)findPreference("power_off_settings"));
    this.mPowerOffPref.setTitle(str2);
    this.mPowerOffPref.setViewClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (TimepowerSettingsFragment.-get0(TimepowerSettingsFragment.this)) {
          return;
        }
        TimepowerSettingsFragment.-wrap1(TimepowerSettingsFragment.this, TimepowerSettingsFragment.-wrap0(TimepowerSettingsFragment.this, 1), 1);
      }
    });
    this.mPowerOffPreference = findPreference("oneplus_power_off_settings");
    this.mPowerOffPreference.setSummary(str2);
    this.mPowerOffPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        if (TimepowerSettingsFragment.-get0(TimepowerSettingsFragment.this)) {
          return true;
        }
        TimepowerSettingsFragment.-wrap1(TimepowerSettingsFragment.this, TimepowerSettingsFragment.-wrap0(TimepowerSettingsFragment.this, 1), 1);
        return false;
      }
    });
    removePreference("power_on_settings");
    removePreference("power_off_settings");
  }
  
  private static boolean intToBool(int paramInt)
  {
    boolean bool = false;
    if (paramInt != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean is24Hour()
  {
    return DateFormat.is24HourFormat(getActivity());
  }
  
  private void readData()
  {
    String str1 = Settings.System.getString(getContentResolver(), "def_timepower_config");
    if (str1 == null) {
      return;
    }
    int j = 0;
    int i = 0;
    while (j <= 6)
    {
      String str2 = str1.substring(j, j + 6);
      this.mTimeArray[i][0] = Integer.parseInt(str2.substring(0, 2));
      this.mTimeArray[i][1] = Integer.parseInt(str2.substring(2, 4));
      this.mStateArray[i][0] = intToBool(Integer.parseInt(str2.substring(4, 5)));
      this.mStateArray[i][1] = intToBool(Integer.parseInt(str2.substring(5, 6)));
      j += 6;
      i += 1;
    }
  }
  
  private void returnNewTimeSetResult(int paramInt, Intent paramIntent)
  {
    paramIntent = paramIntent.getExtras();
    String str;
    int n;
    if (paramIntent != null)
    {
      int k = paramIntent.getInt("hour");
      int m = paramIntent.getInt("minute");
      int j = 0;
      int i = 1;
      if (paramInt == 1)
      {
        j = 1;
        i = 0;
      }
      if ((k == this.mTimeArray[i][0]) && (m == this.mTimeArray[i][1]))
      {
        Toast.makeText(getActivity(), getString(2131690243), 0).show();
        return;
      }
      this.mTimeArray[j][0] = paramIntent.getInt("hour");
      this.mTimeArray[j][1] = paramIntent.getInt("minute");
      str = formatTime(this.mTimeArray[j][0], this.mTimeArray[j][1]);
      this.mStateArray[j][0] = paramIntent.getBoolean("power_state");
      n = this.mStateArray[j][1];
      if (paramInt != 0) {
        break label211;
      }
      this.mPowerOnPref.setTitle(str);
      this.mPowerOnPreference.setSummary(str);
    }
    label211:
    do
    {
      while (n == 0) {
        if (paramInt == 0)
        {
          updateState(0, true);
          this.mPowerOnStatePref.setChecked(true);
          return;
          if (paramInt == 1)
          {
            this.mPowerOffPref.setTitle(str);
            this.mPowerOffPreference.setSummary(str);
          }
        }
        else
        {
          updateState(1, true);
          this.mPowerOffStatePref.setChecked(true);
          return;
        }
      }
      writeData();
      if (this.mCode == 0)
      {
        getActivity().sendBroadcast(new Intent("com.android.settings.POWER_OP_ON"));
        setPowerOn();
        return;
      }
    } while (this.mCode != 1);
    getActivity().sendBroadcast(new Intent("com.android.settings.action.REQUEST_POWER_OFF"));
  }
  
  private void setPowerOn()
  {
    long[] arrayOfLong = new long[2];
    arrayOfLong = SettingsUtil.getNearestTime(Settings.System.getString(getActivity().getContentResolver(), "def_timepower_config"));
    Log.d("TimepowerSettingsFragment", "setPowerOn writeData: " + arrayOfLong[0]);
    Object localObject = new Intent("com.android.settings.POWER_OP_ON");
    AlarmManager localAlarmManager = (AlarmManager)getActivity().getSystemService("alarm");
    localObject = PendingIntent.getBroadcast(getActivity(), 0, (Intent)localObject, 134217728);
    localAlarmManager.setExact(0, arrayOfLong[0], (PendingIntent)localObject);
  }
  
  private void startDialogForResult(Intent paramIntent, int paramInt)
  {
    if (paramIntent == null) {
      return;
    }
    this.mCode = paramInt;
    this.mTimePicker = new TimePicker(new ContextThemeWrapper(getActivity(), 2131821463));
    paramIntent = paramIntent.getExtras();
    this.mPowerState = paramIntent.getBoolean("power_state");
    boolean bool = paramIntent.getBoolean("24hour");
    paramInt = paramIntent.getInt("hour");
    int i = paramIntent.getInt("minute");
    int j = paramIntent.getInt("power_type");
    if (j == 0) {}
    for (;;)
    {
      this.mTimePicker.setIs24HourView(Boolean.valueOf(bool));
      this.mTimePicker.setCurrentHour(Integer.valueOf(paramInt));
      this.mTimePicker.setCurrentMinute(Integer.valueOf(i));
      new TimeSetDialogListener();
      paramIntent = new TimePickerDialog(getActivity(), this, paramInt, i, bool);
      paramIntent.setOnDismissListener(this.onDismissListener);
      paramIntent.show();
      this.mDlgVisible = true;
      return;
      if (j != 1) {}
    }
  }
  
  private void updateState(int paramInt, boolean paramBoolean)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      return;
    }
    this.mStateArray[paramInt][1] = paramBoolean;
    writeData();
    if ((paramInt == 0) && (paramBoolean))
    {
      misCheckedPoweron = true;
      getActivity().sendBroadcast(new Intent("com.android.settings.POWER_OP_ON"));
    }
    do
    {
      return;
      if ((paramInt == 1) && (paramBoolean))
      {
        long[] arrayOfLong = new long[2];
        new Bundle().putLong("trigger_time", arrayOfLong[1]);
        misCheckedPoweroff = true;
        getActivity().sendBroadcast(new Intent("com.android.settings.action.REQUEST_POWER_OFF"));
        return;
      }
      if ((paramInt == 1) && (!paramBoolean))
      {
        misCheckedPoweroff = false;
        getActivity().sendBroadcast(new Intent("com.android.settings.POWER_CANCEL_OP_OFF"));
        return;
      }
    } while ((paramInt != 0) || (paramBoolean));
    misCheckedPoweron = false;
    getActivity().sendBroadcast(new Intent("com.android.settings.POWER_OP_ON"));
  }
  
  private void writeData()
  {
    String str1 = new String("");
    int i = 0;
    while (i < 2)
    {
      String str2 = String.format("%1$02d", new Object[] { Integer.valueOf(this.mTimeArray[i][0]) }) + String.format("%1$02d", new Object[] { Integer.valueOf(this.mTimeArray[i][1]) }) + String.format("%1$01d", new Object[] { Integer.valueOf(boolToInt(this.mStateArray[i][0])) }) + String.format("%1$01d", new Object[] { Integer.valueOf(boolToInt(this.mStateArray[i][1])) });
      str1 = str1 + str2;
      i += 1;
    }
    Log.d("TimepowerSettingsFragment", "writeData: " + str1);
    Settings.System.putString(getContentResolver(), "def_timepower_config", str1);
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230819);
    init();
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool = false;
    if ((paramPreference instanceof SwitchPreference)) {
      bool = ((Boolean)paramObject).booleanValue();
    }
    paramPreference = paramPreference.getKey();
    if ("power_on_switch".equals(paramPreference))
    {
      updateState(0, bool);
      return true;
    }
    if ("power_off_switch".equals(paramPreference))
    {
      updateState(1, bool);
      return true;
    }
    return false;
  }
  
  public void onTimeSet(TimePicker paramTimePicker, int paramInt1, int paramInt2)
  {
    ReturnData(paramInt1, paramInt2);
    this.mDlgVisible = false;
  }
  
  private class TimeSetDialogListener
    implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener
  {
    public TimeSetDialogListener() {}
    
    public void onCancel(DialogInterface paramDialogInterface)
    {
      Log.i("TimepowerSettingsFragment", "=========TimeSetDialogListener CANCEL=======");
      paramDialogInterface.cancel();
      TimepowerSettingsFragment.-set0(TimepowerSettingsFragment.this, false);
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (paramInt == -1) {}
      for (;;)
      {
        onCancel(paramDialogInterface);
        return;
        if (paramInt != -2) {}
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\timer\timepower\TimepowerSettingsFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */