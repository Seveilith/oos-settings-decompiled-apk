package com.android.settings.notification;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import com.android.settings.SettingsPreferenceFragment;

public class SettingPref
{
  public static final int TYPE_GLOBAL = 1;
  public static final int TYPE_SYSTEM = 2;
  protected final int mDefault;
  protected DropDownPreference mDropDown;
  private final String mKey;
  protected final String mSetting;
  protected TwoStatePreference mTwoState;
  protected final int mType;
  private final Uri mUri;
  private final int[] mValues;
  
  public SettingPref(int paramInt1, String paramString1, String paramString2, int paramInt2, int... paramVarArgs)
  {
    this.mType = paramInt1;
    this.mKey = paramString1;
    this.mSetting = paramString2;
    this.mDefault = paramInt2;
    this.mValues = paramVarArgs;
    this.mUri = getUriFor(this.mType, this.mSetting);
  }
  
  protected static int getInt(int paramInt1, ContentResolver paramContentResolver, String paramString, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      throw new IllegalArgumentException();
    case 1: 
      return Settings.Global.getInt(paramContentResolver, paramString, paramInt2);
    }
    return Settings.System.getInt(paramContentResolver, paramString, paramInt2);
  }
  
  private static Uri getUriFor(int paramInt, String paramString)
  {
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException();
    case 1: 
      return Settings.Global.getUriFor(paramString);
    }
    return Settings.System.getUriFor(paramString);
  }
  
  protected static boolean putInt(int paramInt1, ContentResolver paramContentResolver, String paramString, int paramInt2)
  {
    switch (paramInt1)
    {
    default: 
      throw new IllegalArgumentException();
    case 1: 
      return Settings.Global.putInt(paramContentResolver, paramString, paramInt2);
    }
    return Settings.System.putInt(paramContentResolver, paramString, paramInt2);
  }
  
  protected String getCaption(Resources paramResources, int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  public String getKey()
  {
    return this.mKey;
  }
  
  public Uri getUri()
  {
    return this.mUri;
  }
  
  public Preference init(SettingsPreferenceFragment paramSettingsPreferenceFragment)
  {
    final Activity localActivity = paramSettingsPreferenceFragment.getActivity();
    Object localObject2 = paramSettingsPreferenceFragment.getPreferenceScreen().findPreference(this.mKey);
    Object localObject1 = localObject2;
    if (localObject2 != null)
    {
      if (isApplicable(localActivity)) {
        localObject1 = localObject2;
      }
    }
    else
    {
      if (!(localObject1 instanceof TwoStatePreference)) {
        break label105;
      }
      this.mTwoState = ((TwoStatePreference)localObject1);
    }
    for (;;)
    {
      update(localActivity);
      if ((this.mTwoState == null) || (localObject1 == null)) {
        break label208;
      }
      ((Preference)localObject1).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          paramAnonymousPreference = SettingPref.this;
          Context localContext = localActivity;
          if (((Boolean)paramAnonymousObject).booleanValue()) {}
          for (int i = 1;; i = 0)
          {
            paramAnonymousPreference.setSetting(localContext, i);
            return true;
          }
        }
      });
      return this.mTwoState;
      paramSettingsPreferenceFragment.getPreferenceScreen().removePreference((Preference)localObject2);
      localObject1 = null;
      break;
      label105:
      if ((localObject1 instanceof DropDownPreference))
      {
        this.mDropDown = ((DropDownPreference)localObject1);
        paramSettingsPreferenceFragment = new CharSequence[this.mValues.length];
        localObject2 = new CharSequence[this.mValues.length];
        int i = 0;
        while (i < this.mValues.length)
        {
          paramSettingsPreferenceFragment[i] = getCaption(localActivity.getResources(), this.mValues[i]);
          localObject2[i] = Integer.toString(this.mValues[i]);
          i += 1;
        }
        this.mDropDown.setEntries(paramSettingsPreferenceFragment);
        this.mDropDown.setEntryValues((CharSequence[])localObject2);
      }
    }
    label208:
    if ((this.mDropDown != null) && (localObject1 != null))
    {
      ((Preference)localObject1).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          return SettingPref.this.setSetting(localActivity, Integer.parseInt((String)paramAnonymousObject));
        }
      });
      return this.mDropDown;
    }
    return null;
  }
  
  public boolean isApplicable(Context paramContext)
  {
    return true;
  }
  
  protected boolean setSetting(Context paramContext, int paramInt)
  {
    return putInt(this.mType, paramContext.getContentResolver(), this.mSetting, paramInt);
  }
  
  public void update(Context paramContext)
  {
    boolean bool = false;
    int i = getInt(this.mType, paramContext.getContentResolver(), this.mSetting, this.mDefault);
    if (this.mTwoState != null)
    {
      paramContext = this.mTwoState;
      if (i != 0) {
        bool = true;
      }
      paramContext.setChecked(bool);
    }
    while (this.mDropDown == null) {
      return;
    }
    this.mDropDown.setValue(Integer.toString(i));
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\notification\SettingPref.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */