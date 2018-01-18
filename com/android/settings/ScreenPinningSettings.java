package com.android.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.SwitchBar.OnSwitchChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ScreenPinningSettings
  extends SettingsPreferenceFragment
  implements SwitchBar.OnSwitchChangeListener, Indexable
{
  private static final int CHANGE_LOCK_METHOD_REQUEST = 43;
  private static final CharSequence KEY_USE_SCREEN_LOCK = "use_screen_lock";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      Resources localResources = paramAnonymousContext.getResources();
      SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
      localSearchIndexableRaw.title = localResources.getString(2131693337);
      localSearchIndexableRaw.screenTitle = localResources.getString(2131693337);
      localArrayList.add(localSearchIndexableRaw);
      if (ScreenPinningSettings.-wrap0(paramAnonymousContext))
      {
        paramAnonymousContext = new SearchIndexableRaw(paramAnonymousContext);
        paramAnonymousContext.title = localResources.getString(2131693342);
        paramAnonymousContext.screenTitle = localResources.getString(2131693337);
        localArrayList.add(paramAnonymousContext);
        return localArrayList;
      }
      paramAnonymousContext = new SearchIndexableRaw(paramAnonymousContext);
      paramAnonymousContext.title = localResources.getString(2131693338);
      paramAnonymousContext.screenTitle = localResources.getString(2131693337);
      localArrayList.add(paramAnonymousContext);
      return localArrayList;
    }
  };
  private LockPatternUtils mLockPatternUtils;
  private SwitchBar mSwitchBar;
  private SwitchPreference mUseScreenLock;
  
  private int getCurrentSecurityTitle()
  {
    switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality(UserHandle.myUserId()))
    {
    }
    do
    {
      return 2131693342;
      return 2131693340;
      return 2131693341;
    } while (!this.mLockPatternUtils.isLockPatternEnabled(UserHandle.myUserId()));
    return 2131693339;
  }
  
  private static boolean isLockToAppEnabled(Context paramContext)
  {
    boolean bool = false;
    if (Settings.System.getInt(paramContext.getContentResolver(), "lock_to_app_enabled", 0) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private boolean isScreenLockUsed()
  {
    boolean bool = false;
    if (getCurrentSecurityTitle() != 2131693342) {}
    for (int i = 1;; i = 0)
    {
      if (Settings.Secure.getInt(getContentResolver(), "lock_to_app_exit_locked", i) != 0) {
        bool = true;
      }
      return bool;
    }
  }
  
  private void setLockToAppEnabled(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.System.putInt(localContentResolver, "lock_to_app_enabled", i);
      if (paramBoolean) {
        setScreenLockUsedSetting(isScreenLockUsed());
      }
      return;
    }
  }
  
  private boolean setScreenLockUsed(boolean paramBoolean)
  {
    if ((paramBoolean) && (new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()) == 0))
    {
      Intent localIntent = new Intent("android.app.action.SET_NEW_PASSWORD");
      localIntent.putExtra("minimum_quality", 65536);
      startActivityForResult(localIntent, 43);
      return false;
    }
    setScreenLockUsedSetting(paramBoolean);
    return true;
  }
  
  private void setScreenLockUsedSetting(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "lock_to_app_exit_locked", i);
      return;
    }
  }
  
  protected int getMetricsCategory()
  {
    return 86;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    paramBundle = (SettingsActivity)getActivity();
    this.mLockPatternUtils = new LockPatternUtils(paramBundle);
    this.mSwitchBar = paramBundle.getSwitchBar();
    this.mSwitchBar.addOnSwitchChangeListener(this);
    this.mSwitchBar.show();
    this.mSwitchBar.setChecked(isLockToAppEnabled(getActivity()));
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    boolean bool = false;
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 43)
    {
      if (new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()) != 0) {
        bool = true;
      }
      setScreenLockUsed(bool);
      this.mUseScreenLock.setChecked(bool);
    }
  }
  
  public void onDestroyView()
  {
    super.onDestroyView();
    this.mSwitchBar.removeOnSwitchChangeListener(this);
    this.mSwitchBar.hide();
  }
  
  public void onSwitchChanged(Switch paramSwitch, boolean paramBoolean)
  {
    setLockToAppEnabled(paramBoolean);
    updateDisplay();
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = (ViewGroup)paramView.findViewById(16908351);
    paramBundle = LayoutInflater.from(getContext()).inflate(2130968965, paramView, false);
    paramView.addView(paramBundle);
    setEmptyView(paramBundle);
  }
  
  public void updateDisplay()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null) {
      localPreferenceScreen.removeAll();
    }
    if (isLockToAppEnabled(getActivity()))
    {
      addPreferencesFromResource(2131230834);
      this.mUseScreenLock = ((SwitchPreference)getPreferenceScreen().findPreference(KEY_USE_SCREEN_LOCK));
      this.mUseScreenLock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
      {
        public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
        {
          return ScreenPinningSettings.-wrap1(ScreenPinningSettings.this, ((Boolean)paramAnonymousObject).booleanValue());
        }
      });
      this.mUseScreenLock.setChecked(isScreenLockUsed());
      this.mUseScreenLock.setTitle(getCurrentSecurityTitle());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ScreenPinningSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */