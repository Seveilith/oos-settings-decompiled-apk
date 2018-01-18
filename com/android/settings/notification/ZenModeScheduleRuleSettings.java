package com.android.settings.notification;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AutomaticZenRule;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.ScheduleInfo;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class ZenModeScheduleRuleSettings
  extends ZenModeRuleSettingsBase
{
  public static final String ACTION = "android.settings.ZEN_MODE_SCHEDULE_RULE_SETTINGS";
  private static final String KEY_DAYS = "days";
  private static final String KEY_END_TIME = "end_time";
  private static final String KEY_EXIT_AT_ALARM = "exit_at_alarm";
  private static final String KEY_START_TIME = "start_time";
  private AlertDialog mAlertDialog;
  private final SimpleDateFormat mDayFormat = new SimpleDateFormat("EEE");
  private Preference mDays;
  private TimePickerPreference mEnd;
  private SwitchPreference mExitAtAlarm;
  private ZenModeConfig.ScheduleInfo mSchedule;
  private TimePickerPreference mStart;
  
  private void showDaysDialog()
  {
    this.mAlertDialog = new AlertDialog.Builder(this.mContext).setTitle(2131693290).setView(new ZenModeScheduleDaysSelection(this.mContext, this.mSchedule.days)
    {
      protected void onChanged(int[] paramAnonymousArrayOfInt)
      {
        if (ZenModeScheduleRuleSettings.this.mDisableListeners) {
          return;
        }
        if (Arrays.equals(paramAnonymousArrayOfInt, ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).days)) {
          return;
        }
        if (ZenModeScheduleRuleSettings.DEBUG) {
          Log.d("ZenModeSettings", "days.onChanged days=" + Arrays.asList(new int[][] { paramAnonymousArrayOfInt }));
        }
        ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).days = paramAnonymousArrayOfInt;
        ZenModeScheduleRuleSettings.this.updateRule(ZenModeConfig.toScheduleConditionId(ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this)));
      }
    }).setOnDismissListener(new DialogInterface.OnDismissListener()
    {
      public void onDismiss(DialogInterface paramAnonymousDialogInterface)
      {
        ZenModeScheduleRuleSettings.-wrap1(ZenModeScheduleRuleSettings.this);
      }
    }).setPositiveButton(2131692908, null).show();
  }
  
  private void updateDays()
  {
    for (;;)
    {
      int i;
      int j;
      try
      {
        int[] arrayOfInt = this.mSchedule.days;
        if ((arrayOfInt != null) && (arrayOfInt.length > 0))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          Calendar localCalendar = Calendar.getInstance();
          i = 0;
          if (i < ZenModeScheduleDaysSelection.DAYS.length)
          {
            int k = ZenModeScheduleDaysSelection.DAYS[i];
            j = 0;
            if (j >= arrayOfInt.length) {
              break label171;
            }
            if (k != arrayOfInt[j]) {
              break label178;
            }
            localCalendar.set(7, k);
            if (localStringBuilder.length() > 0) {
              localStringBuilder.append(this.mContext.getString(2131693295));
            }
            localStringBuilder.append(this.mDayFormat.format(localCalendar.getTime()));
            break label171;
          }
          if (localStringBuilder.length() > 0)
          {
            this.mDays.setSummary(localStringBuilder);
            this.mDays.notifyDependencyChange(false);
            return;
          }
        }
        this.mDays.setSummary(2131693291);
        this.mDays.notifyDependencyChange(true);
        return;
      }
      catch (IllegalStateException localIllegalStateException)
      {
        localIllegalStateException.printStackTrace();
        return;
      }
      label171:
      i += 1;
      continue;
      label178:
      j += 1;
    }
  }
  
  private void updateEndSummary()
  {
    if (this.mSchedule.startHour * 60 + this.mSchedule.startMinute >= this.mSchedule.endHour * 60 + this.mSchedule.endMinute)
    {
      i = 1;
      if (i == 0) {
        break label62;
      }
    }
    label62:
    for (int i = 2131693319;; i = 0)
    {
      this.mEnd.setSummaryFormat(i);
      return;
      i = 0;
      break;
    }
  }
  
  protected int getEnabledToastText()
  {
    return 2131693275;
  }
  
  protected int getMetricsCategory()
  {
    return 144;
  }
  
  protected String getZenModeDependency()
  {
    return this.mDays.getKey();
  }
  
  protected void onCreateInternal()
  {
    addPreferencesFromResource(2131230896);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    this.mDays = localPreferenceScreen.findPreference("days");
    this.mDays.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        ZenModeScheduleRuleSettings.-wrap0(ZenModeScheduleRuleSettings.this);
        return true;
      }
    });
    FragmentManager localFragmentManager = getFragmentManager();
    this.mStart = new TimePickerPreference(getPrefContext(), localFragmentManager);
    this.mStart.setKey("start_time");
    this.mStart.setTitle(2131693317);
    this.mStart.setCallback(new ZenModeScheduleRuleSettings.TimePickerPreference.Callback()
    {
      public boolean onSetTime(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (ZenModeScheduleRuleSettings.this.mDisableListeners) {
          return true;
        }
        if (!ZenModeConfig.isValidHour(paramAnonymousInt1)) {
          return false;
        }
        if (!ZenModeConfig.isValidMinute(paramAnonymousInt2)) {
          return false;
        }
        if ((paramAnonymousInt1 == ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).startHour) && (paramAnonymousInt2 == ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).startMinute)) {
          return true;
        }
        if (ZenModeScheduleRuleSettings.DEBUG) {
          Log.d("ZenModeSettings", "onPrefChange start h=" + paramAnonymousInt1 + " m=" + paramAnonymousInt2);
        }
        ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).startHour = paramAnonymousInt1;
        ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).startMinute = paramAnonymousInt2;
        ZenModeScheduleRuleSettings.this.updateRule(ZenModeConfig.toScheduleConditionId(ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this)));
        return true;
      }
    });
    localPreferenceScreen.addPreference(this.mStart);
    this.mStart.setDependency(this.mDays.getKey());
    this.mEnd = new TimePickerPreference(getPrefContext(), localFragmentManager);
    this.mEnd.setKey("end_time");
    this.mEnd.setTitle(2131693318);
    this.mEnd.setCallback(new ZenModeScheduleRuleSettings.TimePickerPreference.Callback()
    {
      public boolean onSetTime(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (ZenModeScheduleRuleSettings.this.mDisableListeners) {
          return true;
        }
        if (!ZenModeConfig.isValidHour(paramAnonymousInt1)) {
          return false;
        }
        if (!ZenModeConfig.isValidMinute(paramAnonymousInt2)) {
          return false;
        }
        if ((paramAnonymousInt1 == ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).endHour) && (paramAnonymousInt2 == ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).endMinute)) {
          return true;
        }
        if (ZenModeScheduleRuleSettings.DEBUG) {
          Log.d("ZenModeSettings", "onPrefChange end h=" + paramAnonymousInt1 + " m=" + paramAnonymousInt2);
        }
        ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).endHour = paramAnonymousInt1;
        ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).endMinute = paramAnonymousInt2;
        ZenModeScheduleRuleSettings.this.updateRule(ZenModeConfig.toScheduleConditionId(ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this)));
        return true;
      }
    });
    localPreferenceScreen.addPreference(this.mEnd);
    this.mEnd.setDependency(this.mDays.getKey());
    this.mExitAtAlarm = ((SwitchPreference)localPreferenceScreen.findPreference("exit_at_alarm"));
    this.mExitAtAlarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this).exitAtAlarm = ((Boolean)paramAnonymousObject).booleanValue();
        ZenModeScheduleRuleSettings.this.updateRule(ZenModeConfig.toScheduleConditionId(ZenModeScheduleRuleSettings.-get0(ZenModeScheduleRuleSettings.this)));
        return true;
      }
    });
  }
  
  public void onDestroy()
  {
    try
    {
      if ((this.mAlertDialog != null) && (this.mAlertDialog.isShowing()))
      {
        this.mAlertDialog.dismiss();
        this.mAlertDialog = null;
      }
      super.onDestroy();
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  protected boolean setRule(AutomaticZenRule paramAutomaticZenRule)
  {
    ZenModeConfig.ScheduleInfo localScheduleInfo = null;
    if (paramAutomaticZenRule != null) {
      localScheduleInfo = ZenModeConfig.tryParseScheduleConditionId(paramAutomaticZenRule.getConditionId());
    }
    this.mSchedule = localScheduleInfo;
    return this.mSchedule != null;
  }
  
  protected void updateControlsInternal()
  {
    updateDays();
    this.mStart.setTime(this.mSchedule.startHour, this.mSchedule.startMinute);
    this.mEnd.setTime(this.mSchedule.endHour, this.mSchedule.endMinute);
    this.mExitAtAlarm.setChecked(this.mSchedule.exitAtAlarm);
    updateEndSummary();
  }
  
  private static class TimePickerPreference
    extends Preference
  {
    private Callback mCallback;
    private final Context mContext;
    private int mHourOfDay;
    private int mMinute;
    private int mSummaryFormat;
    
    public TimePickerPreference(Context paramContext, final FragmentManager paramFragmentManager)
    {
      super();
      this.mContext = paramContext;
      setPersistent(false);
      setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
      {
        public boolean onPreferenceClick(Preference paramAnonymousPreference)
        {
          paramAnonymousPreference = new ZenModeScheduleRuleSettings.TimePickerPreference.TimePickerFragment();
          paramAnonymousPreference.pref = ZenModeScheduleRuleSettings.TimePickerPreference.this;
          paramAnonymousPreference.show(paramFragmentManager, ZenModeScheduleRuleSettings.TimePickerPreference.class.getName());
          return true;
        }
      });
    }
    
    private void updateSummary()
    {
      Object localObject = Calendar.getInstance();
      ((Calendar)localObject).set(11, this.mHourOfDay);
      ((Calendar)localObject).set(12, this.mMinute);
      String str = android.text.format.DateFormat.getTimeFormat(this.mContext).format(((Calendar)localObject).getTime());
      localObject = str;
      if (this.mSummaryFormat != 0) {
        localObject = this.mContext.getResources().getString(this.mSummaryFormat, new Object[] { str });
      }
      setSummary((CharSequence)localObject);
    }
    
    public void setCallback(Callback paramCallback)
    {
      this.mCallback = paramCallback;
    }
    
    public void setSummaryFormat(int paramInt)
    {
      this.mSummaryFormat = paramInt;
      updateSummary();
    }
    
    public void setTime(int paramInt1, int paramInt2)
    {
      if ((this.mCallback == null) || (this.mCallback.onSetTime(paramInt1, paramInt2)))
      {
        this.mHourOfDay = paramInt1;
        this.mMinute = paramInt2;
        updateSummary();
        return;
      }
    }
    
    public static abstract interface Callback
    {
      public abstract boolean onSetTime(int paramInt1, int paramInt2);
    }
    
    public static class TimePickerFragment
      extends DialogFragment
      implements TimePickerDialog.OnTimeSetListener
    {
      public ZenModeScheduleRuleSettings.TimePickerPreference pref;
      
      public Dialog onCreateDialog(Bundle paramBundle)
      {
        int i;
        if ((this.pref != null) && (ZenModeScheduleRuleSettings.TimePickerPreference.-get0(this.pref) >= 0) && (ZenModeScheduleRuleSettings.TimePickerPreference.-get1(this.pref) >= 0))
        {
          j = 1;
          paramBundle = Calendar.getInstance();
          if (j == 0) {
            break label84;
          }
          i = ZenModeScheduleRuleSettings.TimePickerPreference.-get0(this.pref);
          label45:
          if (j == 0) {
            break label94;
          }
        }
        label84:
        label94:
        for (int j = ZenModeScheduleRuleSettings.TimePickerPreference.-get1(this.pref);; j = paramBundle.get(12))
        {
          return new TimePickerDialog(getActivity(), this, i, j, android.text.format.DateFormat.is24HourFormat(getActivity()));
          j = 0;
          break;
          i = paramBundle.get(11);
          break label45;
        }
      }
      
      public void onTimeSet(TimePicker paramTimePicker, int paramInt1, int paramInt2)
      {
        if (this.pref != null) {
          this.pref.setTime(paramInt1, paramInt2);
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeScheduleRuleSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */