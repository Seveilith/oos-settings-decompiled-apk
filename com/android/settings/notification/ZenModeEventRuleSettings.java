package com.android.settings.notification;

import android.app.AutomaticZenRule;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.CalendarContract.Calendars;
import android.service.notification.ZenModeConfig;
import android.service.notification.ZenModeConfig.EventInfo;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ZenModeEventRuleSettings
  extends ZenModeRuleSettingsBase
{
  public static final String ACTION = "android.settings.ZEN_MODE_EVENT_RULE_SETTINGS";
  private static final Comparator<CalendarInfo> CALENDAR_NAME = new Comparator()
  {
    public int compare(ZenModeEventRuleSettings.CalendarInfo paramAnonymousCalendarInfo1, ZenModeEventRuleSettings.CalendarInfo paramAnonymousCalendarInfo2)
    {
      return paramAnonymousCalendarInfo1.name.compareTo(paramAnonymousCalendarInfo2.name);
    }
  };
  private static final String KEY_CALENDAR = "calendar";
  private static final String KEY_REPLY = "reply";
  private DropDownPreference mCalendar;
  private List<CalendarInfo> mCalendars;
  private boolean mCreate;
  private ZenModeConfig.EventInfo mEvent;
  private DropDownPreference mReply;
  
  public static void addCalendars(Context paramContext, List<CalendarInfo> paramList)
  {
    Object localObject = null;
    Cursor localCursor;
    try
    {
      localCursor = paramContext.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, new String[] { "_id", "calendar_displayName", "(account_name=ownerAccount) AS \"primary\"" }, "\"primary\" = 1", null, null);
      if (localCursor == null)
      {
        if (localCursor != null) {
          localCursor.close();
        }
        return;
      }
      for (;;)
      {
        localObject = localCursor;
        if (!localCursor.moveToNext()) {
          break;
        }
        localObject = localCursor;
        CalendarInfo localCalendarInfo = new CalendarInfo();
        localObject = localCursor;
        localCalendarInfo.name = localCursor.getString(1);
        localObject = localCursor;
        localCalendarInfo.userId = paramContext.getUserId();
        localObject = localCursor;
        paramList.add(localCalendarInfo);
      }
      if (localCursor == null) {
        return;
      }
    }
    finally
    {
      if (localObject != null) {
        ((Cursor)localObject).close();
      }
    }
    localCursor.close();
  }
  
  public static CalendarInfo findCalendar(Context paramContext, ZenModeConfig.EventInfo paramEventInfo)
  {
    if ((paramContext == null) || (paramEventInfo == null)) {
      return null;
    }
    paramEventInfo = key(paramEventInfo);
    paramContext = getCalendars(paramContext).iterator();
    while (paramContext.hasNext())
    {
      CalendarInfo localCalendarInfo = (CalendarInfo)paramContext.next();
      if (paramEventInfo.equals(key(localCalendarInfo))) {
        return localCalendarInfo;
      }
    }
    return null;
  }
  
  private static List<CalendarInfo> getCalendars(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = UserManager.get(paramContext).getUserProfiles().iterator();
    while (localIterator.hasNext())
    {
      Context localContext = getContextForUser(paramContext, (UserHandle)localIterator.next());
      if (localContext != null) {
        addCalendars(localContext, localArrayList);
      }
    }
    Collections.sort(localArrayList, CALENDAR_NAME);
    return localArrayList;
  }
  
  private static Context getContextForUser(Context paramContext, UserHandle paramUserHandle)
  {
    try
    {
      paramContext = paramContext.createPackageContextAsUser(paramContext.getPackageName(), 0, paramUserHandle);
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return null;
  }
  
  private static String key(int paramInt, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder().append(ZenModeConfig.EventInfo.resolveUserId(paramInt)).append(":");
    String str = paramString;
    if (paramString == null) {
      str = "";
    }
    return str;
  }
  
  private static String key(ZenModeConfig.EventInfo paramEventInfo)
  {
    return key(paramEventInfo.userId, paramEventInfo.calendar);
  }
  
  private static String key(CalendarInfo paramCalendarInfo)
  {
    return key(paramCalendarInfo.userId, paramCalendarInfo.name);
  }
  
  private void reloadCalendar()
  {
    this.mCalendars = getCalendars(this.mContext);
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList1.add(getString(2131693282));
    localArrayList2.add(key(0, null));
    if (this.mEvent != null) {}
    int i;
    for (String str = this.mEvent.calendar;; str = null)
    {
      i = 0;
      Iterator localIterator = this.mCalendars.iterator();
      while (localIterator.hasNext())
      {
        CalendarInfo localCalendarInfo = (CalendarInfo)localIterator.next();
        localArrayList1.add(localCalendarInfo.name);
        localArrayList2.add(key(localCalendarInfo));
        if ((str != null) && (str.equals(localCalendarInfo.name))) {
          i = 1;
        }
      }
    }
    if ((str == null) || (i != 0)) {}
    for (;;)
    {
      this.mCalendar.setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
      this.mCalendar.setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
      return;
      localArrayList1.add(str);
      localArrayList2.add(key(this.mEvent.userId, str));
    }
  }
  
  protected int getEnabledToastText()
  {
    return 2131693277;
  }
  
  protected int getMetricsCategory()
  {
    return 146;
  }
  
  protected String getZenModeDependency()
  {
    return null;
  }
  
  protected void onCreateInternal()
  {
    this.mCreate = true;
    addPreferencesFromResource(2131230893);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    this.mCalendar = ((DropDownPreference)localPreferenceScreen.findPreference("calendar"));
    this.mCalendar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        paramAnonymousPreference = (String)paramAnonymousObject;
        if (paramAnonymousPreference.equals(ZenModeEventRuleSettings.-wrap0(ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this)))) {
          return false;
        }
        int i = paramAnonymousPreference.indexOf(':');
        ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this).userId = Integer.parseInt(paramAnonymousPreference.substring(0, i));
        ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this).calendar = paramAnonymousPreference.substring(i + 1);
        if (ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this).calendar.isEmpty()) {
          ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this).calendar = null;
        }
        ZenModeEventRuleSettings.this.updateRule(ZenModeConfig.toEventConditionId(ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this)));
        return true;
      }
    });
    this.mReply = ((DropDownPreference)localPreferenceScreen.findPreference("reply"));
    this.mReply.setEntries(new CharSequence[] { getString(2131693284), getString(2131693285), getString(2131693286) });
    this.mReply.setEntryValues(new CharSequence[] { Integer.toString(0), Integer.toString(1), Integer.toString(2) });
    this.mReply.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        int i = Integer.parseInt((String)paramAnonymousObject);
        if (i == ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this).reply) {
          return false;
        }
        ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this).reply = i;
        ZenModeEventRuleSettings.this.updateRule(ZenModeConfig.toEventConditionId(ZenModeEventRuleSettings.-get0(ZenModeEventRuleSettings.this)));
        return true;
      }
    });
    reloadCalendar();
    updateControlsInternal();
  }
  
  public void onResume()
  {
    super.onResume();
    if (isUiRestricted()) {
      return;
    }
    if (!this.mCreate) {
      reloadCalendar();
    }
    this.mCreate = false;
  }
  
  protected boolean setRule(AutomaticZenRule paramAutomaticZenRule)
  {
    ZenModeConfig.EventInfo localEventInfo = null;
    if (paramAutomaticZenRule != null) {
      localEventInfo = ZenModeConfig.tryParseEventConditionId(paramAutomaticZenRule.getConditionId());
    }
    this.mEvent = localEventInfo;
    return this.mEvent != null;
  }
  
  protected void updateControlsInternal()
  {
    this.mCalendar.setValue(key(this.mEvent));
    this.mReply.setValue(Integer.toString(this.mEvent.reply));
  }
  
  public static class CalendarInfo
  {
    public String name;
    public int userId;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModeEventRuleSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */