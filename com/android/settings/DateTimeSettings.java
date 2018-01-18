package com.android.settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.dashboard.SummaryLoader.SummaryProvider;
import com.android.settings.dashboard.SummaryLoader.SummaryProviderFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtils.EnforcedAdmin;
import com.android.settingslib.datetime.ZoneGetter;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTimeSettings
  extends SettingsPreferenceFragment
  implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, Preference.OnPreferenceChangeListener, Indexable
{
  private static final int DIALOG_DATEPICKER = 0;
  private static final int DIALOG_TIMEPICKER = 1;
  protected static final String EXTRA_IS_FIRST_RUN = "firstRun";
  private static final String HOURS_12 = "12";
  private static final String HOURS_24 = "24";
  private static final String KEY_AUTO_TIME = "auto_time";
  private static final String KEY_AUTO_TIME_ZONE = "auto_zone";
  private static final long MIN_DATE = 1194220800000L;
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new DateTimeSearchIndexProvider(null);
  public static final SummaryLoader.SummaryProviderFactory SUMMARY_PROVIDER_FACTORY = new SummaryLoader.SummaryProviderFactory()
  {
    public SummaryLoader.SummaryProvider createSummaryProvider(Activity paramAnonymousActivity, SummaryLoader paramAnonymousSummaryLoader)
    {
      return new DateTimeSettings.SummaryProvider(paramAnonymousActivity, paramAnonymousSummaryLoader);
    }
  };
  private OPRestrictedSwitchPreference mAutoTimePref;
  private SwitchPreference mAutoTimeZonePref;
  private Preference mDatePref;
  private Calendar mDummyDate;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = DateTimeSettings.this.getActivity();
      if (paramAnonymousContext != null) {
        DateTimeSettings.this.updateTimeAndDateDisplay(paramAnonymousContext);
      }
    }
  };
  private Preference mTime24Pref;
  private Preference mTimePref;
  private Preference mTimeZone;
  
  static void configureDatePicker(DatePicker paramDatePicker)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.clear();
    localCalendar.set(1970, 0, 1);
    paramDatePicker.setMinDate(localCalendar.getTimeInMillis());
    localCalendar.clear();
    localCalendar.set(2037, 11, 31);
    paramDatePicker.setMaxDate(localCalendar.getTimeInMillis());
  }
  
  private boolean getAutoState(String paramString)
  {
    boolean bool = false;
    try
    {
      int i = Settings.Global.getInt(getContentResolver(), paramString);
      if (i > 0) {
        bool = true;
      }
      return bool;
    }
    catch (Settings.SettingNotFoundException paramString) {}
    return false;
  }
  
  private void initUI()
  {
    boolean bool3 = false;
    boolean bool4 = getAutoState("auto_time");
    boolean bool1 = getAutoState("auto_time_zone");
    this.mAutoTimePref = ((OPRestrictedSwitchPreference)findPreference("auto_time"));
    this.mAutoTimePref.setOnPreferenceChangeListener(this);
    Object localObject = RestrictedLockUtils.checkIfAutoTimeRequired(getActivity());
    this.mAutoTimePref.setDisabledByAdmin((RestrictedLockUtils.EnforcedAdmin)localObject);
    boolean bool2 = getActivity().getIntent().getBooleanExtra("firstRun", false);
    this.mDummyDate = Calendar.getInstance();
    this.mAutoTimePref.setChecked(bool4);
    this.mAutoTimeZonePref = ((SwitchPreference)findPreference("auto_zone"));
    this.mAutoTimeZonePref.setOnPreferenceChangeListener(this);
    if ((Utils.isWifiOnly(getActivity())) || (bool2))
    {
      getPreferenceScreen().removePreference(this.mAutoTimeZonePref);
      bool1 = false;
    }
    this.mAutoTimeZonePref.setChecked(bool1);
    this.mTimePref = findPreference("time");
    this.mTime24Pref = findPreference("24 hour");
    this.mTimeZone = findPreference("timezone");
    this.mDatePref = findPreference("date");
    if (bool2) {
      getPreferenceScreen().removePreference(this.mTime24Pref);
    }
    localObject = this.mTimePref;
    if (bool4)
    {
      bool2 = false;
      ((Preference)localObject).setEnabled(bool2);
      localObject = this.mDatePref;
      if (!bool4) {
        break label261;
      }
      bool2 = false;
      label231:
      ((Preference)localObject).setEnabled(bool2);
      localObject = this.mTimeZone;
      if (!bool1) {
        break label266;
      }
    }
    label261:
    label266:
    for (bool1 = bool3;; bool1 = true)
    {
      ((Preference)localObject).setEnabled(bool1);
      return;
      bool2 = true;
      break;
      bool2 = true;
      break label231;
    }
  }
  
  private boolean is24Hour()
  {
    return android.text.format.DateFormat.is24HourFormat(getActivity());
  }
  
  private void set24Hour(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (String str = "24";; str = "12")
    {
      Settings.System.putString(localContentResolver, "time_12_24", str);
      return;
    }
  }
  
  static void setDate(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(1, paramInt1);
    localCalendar.set(2, paramInt2);
    localCalendar.set(5, paramInt3);
    long l = Math.max(localCalendar.getTimeInMillis(), 1194220800000L);
    if (l / 1000L < 2147483647L) {
      ((AlarmManager)paramContext.getSystemService("alarm")).setTime(l);
    }
  }
  
  static void setTime(Context paramContext, int paramInt1, int paramInt2)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(11, paramInt1);
    localCalendar.set(12, paramInt2);
    localCalendar.set(13, 0);
    localCalendar.set(14, 0);
    long l = Math.max(localCalendar.getTimeInMillis(), 1194220800000L);
    if (l / 1000L < 2147483647L) {
      ((AlarmManager)paramContext.getSystemService("alarm")).setTime(l);
    }
  }
  
  private void timeUpdated(boolean paramBoolean)
  {
    Intent localIntent = new Intent("android.intent.action.TIME_SET");
    localIntent.putExtra("android.intent.extra.TIME_PREF_24_HOUR_FORMAT", paramBoolean);
    getActivity().sendBroadcast(localIntent);
  }
  
  protected int getMetricsCategory()
  {
    return 38;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    updateTimeAndDateDisplay(getActivity());
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230755);
    initUI();
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    Object localObject = Calendar.getInstance();
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException();
    case 0: 
      localObject = new DatePickerDialog(getActivity(), 2131821439, this, ((Calendar)localObject).get(1), ((Calendar)localObject).get(2), ((Calendar)localObject).get(5));
      configureDatePicker(((DatePickerDialog)localObject).getDatePicker());
      return (Dialog)localObject;
    }
    return new TimePickerDialog(getActivity(), this, ((Calendar)localObject).get(11), ((Calendar)localObject).get(12), android.text.format.DateFormat.is24HourFormat(getActivity()));
  }
  
  public void onDateSet(DatePicker paramDatePicker, int paramInt1, int paramInt2, int paramInt3)
  {
    paramDatePicker = getActivity();
    if (paramDatePicker != null)
    {
      setDate(paramDatePicker, paramInt1, paramInt2, paramInt3);
      updateTimeAndDateDisplay(paramDatePicker);
    }
  }
  
  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mIntentReceiver);
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    int i;
    if (paramPreference.getKey().equals("auto_time"))
    {
      bool3 = ((Boolean)paramObject).booleanValue();
      paramPreference = getContentResolver();
      if (bool3)
      {
        i = 1;
        Settings.Global.putInt(paramPreference, "auto_time", i);
        paramPreference = this.mTimePref;
        if (!bool3) {
          break label93;
        }
        bool1 = false;
        paramPreference.setEnabled(bool1);
        paramPreference = this.mDatePref;
        if (!bool3) {
          break label99;
        }
        bool1 = bool2;
        paramPreference.setEnabled(bool1);
      }
    }
    label93:
    label99:
    while (!paramPreference.getKey().equals("auto_zone")) {
      for (;;)
      {
        boolean bool3;
        return true;
        i = 0;
        continue;
        bool1 = true;
        continue;
        bool1 = true;
      }
    }
    bool2 = ((Boolean)paramObject).booleanValue();
    paramPreference = getContentResolver();
    if (bool2)
    {
      i = 1;
      Settings.Global.putInt(paramPreference, "auto_time_zone", i);
      paramPreference = this.mTimeZone;
      if (!bool2) {
        break label169;
      }
    }
    for (;;)
    {
      paramPreference.setEnabled(bool1);
      return true;
      i = 0;
      break;
      label169:
      bool1 = true;
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if (paramPreference == this.mDatePref) {
      showDialog(0);
    }
    for (;;)
    {
      return super.onPreferenceTreeClick(paramPreference);
      if (paramPreference == this.mTimePref)
      {
        removeDialog(1);
        showDialog(1);
      }
      else if (paramPreference == this.mTime24Pref)
      {
        boolean bool = ((SwitchPreference)this.mTime24Pref).isChecked();
        set24Hour(bool);
        updateTimeAndDateDisplay(getActivity());
        timeUpdated(bool);
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    ((SwitchPreference)this.mTime24Pref).setChecked(is24Hour());
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIME_TICK");
    localIntentFilter.addAction("android.intent.action.TIME_SET");
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    getActivity().registerReceiver(this.mIntentReceiver, localIntentFilter, null, null);
    updateTimeAndDateDisplay(getActivity());
  }
  
  public void onTimeSet(TimePicker paramTimePicker, int paramInt1, int paramInt2)
  {
    paramTimePicker = getActivity();
    if (paramTimePicker != null)
    {
      setTime(paramTimePicker, paramInt1, paramInt2);
      updateTimeAndDateDisplay(paramTimePicker);
    }
  }
  
  public void updateTimeAndDateDisplay(Context paramContext)
  {
    Calendar localCalendar = Calendar.getInstance();
    this.mDummyDate.setTimeZone(localCalendar.getTimeZone());
    this.mDummyDate.set(localCalendar.get(1), 11, 31, 13, 0, 0);
    Date localDate = this.mDummyDate.getTime();
    if (getResources().getBoolean(2131558480))
    {
      paramContext = Settings.System.getString(paramContext.getContentResolver(), "date_format");
      this.mDatePref.setSummary(android.text.format.DateFormat.format(paramContext, localCalendar.getTime()));
    }
    for (;;)
    {
      this.mTimePref.setSummary(android.text.format.DateFormat.getTimeFormat(getActivity()).format(localCalendar.getTime()));
      this.mTimeZone.setSummary(ZoneGetter.getTimeZoneOffsetAndName(localCalendar.getTimeZone(), localCalendar.getTime()));
      this.mTime24Pref.setSummary(android.text.format.DateFormat.getTimeFormat(getActivity()).format(localDate));
      return;
      this.mDatePref.setSummary(android.text.format.DateFormat.getLongDateFormat(paramContext).format(localCalendar.getTime()));
    }
  }
  
  private static class DateTimeSearchIndexProvider
    extends BaseSearchIndexProvider
  {
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramContext, boolean paramBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      if (UserManager.isDeviceInDemoMode(paramContext)) {
        return localArrayList;
      }
      paramContext = new SearchIndexableResource(paramContext);
      paramContext.xmlResId = 2131230755;
      localArrayList.add(paramContext);
      return localArrayList;
    }
  }
  
  private static class SummaryProvider
    implements SummaryLoader.SummaryProvider
  {
    private final Context mContext;
    private final SummaryLoader mSummaryLoader;
    
    public SummaryProvider(Context paramContext, SummaryLoader paramSummaryLoader)
    {
      this.mContext = paramContext;
      this.mSummaryLoader = paramSummaryLoader;
    }
    
    public void setListening(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        Calendar localCalendar = Calendar.getInstance();
        this.mSummaryLoader.setSummary(this, ZoneGetter.getTimeZoneOffsetAndName(localCalendar.getTimeZone(), localCalendar.getTime()));
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\DateTimeSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */