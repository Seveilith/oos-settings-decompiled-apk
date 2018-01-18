package com.android.settings.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.NotificationManager.Policy;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import android.provider.Settings.System;
import android.service.notification.ZenModeConfig;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZenModePrioritySettings
  extends ZenModeSettingsBase
  implements Indexable
{
  private static final String KEY_CALLS = "calls";
  private static final String KEY_EVENTS = "events";
  private static final String KEY_FAVOURITE_CONTACTS = "favourite_contacts_settings";
  private static final String KEY_LED_SWITCH = "led_switch";
  private static final String KEY_MESSAGES = "messages";
  private static final String KEY_REMINDERS = "reminders";
  private static final String KEY_REPEAT_CALLERS = "repeat_callers";
  private static final String OEM_FAVORITES_PEOPLE = "com.oneplus.contacts.action.FAVORITES_PEOPLE";
  private static final SettingPref[] PREFS = { PREF_LED_LIGHT_SETTING };
  private static final SettingPref PREF_LED_LIGHT_SETTING = new SettingPref(2, "led_switch", "oem_allow_led_light", 0, new int[0]);
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      paramAnonymousContext = new ArrayList();
      paramAnonymousContext.add("calls");
      paramAnonymousContext.add("events");
      return paramAnonymousContext;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230895;
      return Arrays.asList(new SearchIndexableResource[] { paramAnonymousContext });
    }
  };
  private static final int SOURCE_NONE = -1;
  private DropDownPreference mCalls;
  private boolean mDisableListeners;
  private SwitchPreference mEvents;
  private Preference mFavouritePreference;
  private SwitchPreference mLedSwitch;
  private DropDownPreference mMessages;
  private int mNotifyLightEnable;
  private NotificationManager.Policy mPolicy;
  private PrefSettingsObserver mPrefSettingsObserver = new PrefSettingsObserver();
  private SwitchPreference mReminders;
  private SwitchPreference mRepeatCallers;
  
  private static void addSources(DropDownPreference paramDropDownPreference)
  {
    paramDropDownPreference.setEntries(new CharSequence[] { paramDropDownPreference.getContext().getString(2131693302), paramDropDownPreference.getContext().getString(2131693303), paramDropDownPreference.getContext().getString(2131693304), paramDropDownPreference.getContext().getString(2131693305) });
    paramDropDownPreference.setEntryValues(new CharSequence[] { Integer.toString(0), Integer.toString(1), Integer.toString(2), Integer.toString(-1) });
  }
  
  private int getNewPriorityCategories(boolean paramBoolean, int paramInt)
  {
    int i = this.mPolicy.priorityCategories;
    if (paramBoolean) {
      return i | paramInt;
    }
    return i & paramInt;
  }
  
  private boolean isPriorityCategoryEnabled(int paramInt)
  {
    boolean bool = false;
    if ((this.mPolicy.priorityCategories & paramInt) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private void savePolicy(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mPolicy = new NotificationManager.Policy(paramInt1, paramInt2, paramInt3, paramInt4);
    NotificationManager.from(this.mContext).setNotificationPolicy(this.mPolicy);
  }
  
  private void updateControls()
  {
    int j = -1;
    this.mDisableListeners = true;
    Object localObject;
    int i;
    boolean bool;
    if (this.mCalls != null)
    {
      localObject = this.mCalls;
      if (isPriorityCategoryEnabled(8))
      {
        i = this.mPolicy.priorityCallSenders;
        ((DropDownPreference)localObject).setValue(Integer.toString(i));
      }
    }
    else
    {
      localObject = this.mMessages;
      i = j;
      if (isPriorityCategoryEnabled(4)) {
        i = this.mPolicy.priorityMessageSenders;
      }
      ((DropDownPreference)localObject).setValue(Integer.toString(i));
      this.mReminders.setChecked(isPriorityCategoryEnabled(1));
      this.mEvents.setChecked(isPriorityCategoryEnabled(2));
      this.mRepeatCallers.setChecked(isPriorityCategoryEnabled(16));
      localObject = this.mRepeatCallers;
      if (!isPriorityCategoryEnabled(8)) {
        break label168;
      }
      if (this.mPolicy.priorityCallSenders == 0) {
        break label173;
      }
      bool = true;
    }
    for (;;)
    {
      ((SwitchPreference)localObject).setVisible(bool);
      this.mEvents.setVisible(false);
      this.mDisableListeners = false;
      return;
      i = -1;
      break;
      label168:
      bool = true;
      continue;
      label173:
      bool = false;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 141;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230895);
    paramBundle = getPreferenceScreen();
    this.mPolicy = NotificationManager.from(this.mContext).getNotificationPolicy();
    this.mReminders = ((SwitchPreference)paramBundle.findPreference("reminders"));
    this.mReminders.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ZenModePrioritySettings.-get1(ZenModePrioritySettings.this)) {
          return true;
        }
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        MetricsLogger.action(ZenModePrioritySettings.this.mContext, 167, bool);
        if (ZenModePrioritySettings.DEBUG) {
          Log.d("ZenModeSettings", "onPrefChange allowReminders=" + bool);
        }
        ZenModePrioritySettings.-wrap2(ZenModePrioritySettings.this, ZenModePrioritySettings.-wrap1(ZenModePrioritySettings.this, bool, 1), ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityCallSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityMessageSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).suppressedVisualEffects);
        ZenModePrioritySettings.-wrap2(ZenModePrioritySettings.this, ZenModePrioritySettings.-wrap1(ZenModePrioritySettings.this, bool, 2), ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityCallSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityMessageSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).suppressedVisualEffects);
        return true;
      }
    });
    this.mEvents = ((SwitchPreference)paramBundle.findPreference("events"));
    this.mEvents.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ZenModePrioritySettings.-get1(ZenModePrioritySettings.this)) {
          return true;
        }
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        MetricsLogger.action(ZenModePrioritySettings.this.mContext, 168, bool);
        if (ZenModePrioritySettings.DEBUG) {
          Log.d("ZenModeSettings", "onPrefChange allowEvents=" + bool);
        }
        ZenModePrioritySettings.-wrap2(ZenModePrioritySettings.this, ZenModePrioritySettings.-wrap1(ZenModePrioritySettings.this, bool, 2), ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityCallSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityMessageSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).suppressedVisualEffects);
        return true;
      }
    });
    this.mFavouritePreference = findPreference("favourite_contacts_settings");
    this.mFavouritePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        try
        {
          paramAnonymousPreference = new Intent();
          paramAnonymousPreference.setAction("com.oneplus.contacts.action.FAVORITES_PEOPLE");
          paramAnonymousPreference.addCategory("android.intent.category.DEFAULT");
          ZenModePrioritySettings.this.mContext.startActivity(paramAnonymousPreference);
          return false;
        }
        catch (ActivityNotFoundException paramAnonymousPreference)
        {
          for (;;)
          {
            paramAnonymousPreference.printStackTrace();
          }
        }
      }
    });
    this.mMessages = ((DropDownPreference)paramBundle.findPreference("messages"));
    addSources(this.mMessages);
    this.mMessages.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ZenModePrioritySettings.-get1(ZenModePrioritySettings.this)) {
          return false;
        }
        int j = Integer.parseInt((String)paramAnonymousObject);
        boolean bool;
        if (j != -1)
        {
          bool = true;
          if (j != -1) {
            break label152;
          }
        }
        label152:
        for (int i = ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityMessageSenders;; i = j)
        {
          MetricsLogger.action(ZenModePrioritySettings.this.mContext, 169, j);
          if (ZenModePrioritySettings.DEBUG) {
            Log.d("ZenModeSettings", "onPrefChange allowMessages=" + bool + " allowMessagesFrom=" + ZenModeConfig.sourceToString(i));
          }
          ZenModePrioritySettings.-wrap2(ZenModePrioritySettings.this, ZenModePrioritySettings.-wrap1(ZenModePrioritySettings.this, bool, 4), ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityCallSenders, i, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).suppressedVisualEffects);
          return true;
          bool = false;
          break;
        }
      }
    });
    this.mCalls = ((DropDownPreference)paramBundle.findPreference("calls"));
    addSources(this.mCalls);
    this.mCalls.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ZenModePrioritySettings.-get1(ZenModePrioritySettings.this)) {
          return false;
        }
        int j = Integer.parseInt((String)paramAnonymousObject);
        boolean bool;
        if (j != -1)
        {
          bool = true;
          if (j != -1) {
            break label153;
          }
        }
        label153:
        for (int i = ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityCallSenders;; i = j)
        {
          MetricsLogger.action(ZenModePrioritySettings.this.mContext, 170, j);
          if (ZenModePrioritySettings.DEBUG) {
            Log.d("ZenModeSettings", "onPrefChange allowCalls=" + bool + " allowCallsFrom=" + ZenModeConfig.sourceToString(i));
          }
          ZenModePrioritySettings.-wrap2(ZenModePrioritySettings.this, ZenModePrioritySettings.-wrap1(ZenModePrioritySettings.this, bool, 8), i, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityMessageSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).suppressedVisualEffects);
          return true;
          bool = false;
          break;
        }
      }
    });
    this.mRepeatCallers = ((SwitchPreference)paramBundle.findPreference("repeat_callers"));
    this.mRepeatCallers.setSummary(this.mContext.getString(2131693312, new Object[] { Integer.valueOf(this.mContext.getResources().getInteger(17694874)) }));
    this.mRepeatCallers.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ZenModePrioritySettings.-get1(ZenModePrioritySettings.this)) {
          return true;
        }
        boolean bool = ((Boolean)paramAnonymousObject).booleanValue();
        MetricsLogger.action(ZenModePrioritySettings.this.mContext, 171, bool);
        if (ZenModePrioritySettings.DEBUG) {
          Log.d("ZenModeSettings", "onPrefChange allowRepeatCallers=" + bool);
        }
        int i = ZenModePrioritySettings.-wrap1(ZenModePrioritySettings.this, bool, 16);
        ZenModePrioritySettings.-wrap2(ZenModePrioritySettings.this, i, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityCallSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).priorityMessageSenders, ZenModePrioritySettings.-get2(ZenModePrioritySettings.this).suppressedVisualEffects);
        return true;
      }
    });
    updateControls();
    this.mLedSwitch = ((SwitchPreference)paramBundle.findPreference("led_switch"));
    this.mNotifyLightEnable = Settings.System.getInt(getActivity().getContentResolver(), "oem_acc_breath_light", 0);
    if (this.mLedSwitch != null)
    {
      paramBundle = this.mLedSwitch;
      if (this.mNotifyLightEnable != 1) {
        break label312;
      }
    }
    label312:
    for (boolean bool = true;; bool = false)
    {
      paramBundle.setEnabled(bool);
      return;
    }
  }
  
  public void onPause()
  {
    this.mPrefSettingsObserver.register(false);
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    SettingPref[] arrayOfSettingPref = PREFS;
    int i = 0;
    int j = arrayOfSettingPref.length;
    while (i < j)
    {
      arrayOfSettingPref[i].init(this);
      i += 1;
    }
    this.mPrefSettingsObserver.register(true);
  }
  
  protected void onZenModeChanged() {}
  
  protected void onZenModeConfigChanged()
  {
    this.mPolicy = NotificationManager.from(this.mContext).getNotificationPolicy();
    updateControls();
  }
  
  private final class PrefSettingsObserver
    extends ContentObserver
  {
    public PrefSettingsObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      SettingPref[] arrayOfSettingPref = ZenModePrioritySettings.-get0();
      int i = 0;
      int j = arrayOfSettingPref.length;
      while (i < j)
      {
        SettingPref localSettingPref = arrayOfSettingPref[i];
        if (localSettingPref.getUri().equals(paramUri))
        {
          localSettingPref.update(ZenModePrioritySettings.this.mContext);
          return;
        }
        i += 1;
      }
    }
    
    public void register(boolean paramBoolean)
    {
      ContentResolver localContentResolver = ZenModePrioritySettings.-wrap0(ZenModePrioritySettings.this);
      if (paramBoolean)
      {
        SettingPref[] arrayOfSettingPref = ZenModePrioritySettings.-get0();
        int j = arrayOfSettingPref.length;
        int i = 0;
        while (i < j)
        {
          localContentResolver.registerContentObserver(arrayOfSettingPref[i].getUri(), false, this);
          i += 1;
        }
      }
      localContentResolver.unregisterContentObserver(this);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\ZenModePrioritySettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */