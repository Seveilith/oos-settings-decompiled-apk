package com.android.settings.applications;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.settings.Utils;
import com.android.settingslib.applications.ApplicationsState.AppEntry;
import java.util.List;

public class AppLaunchSettings
  extends AppInfoWithHeader
  implements View.OnClickListener, Preference.OnPreferenceChangeListener
{
  private static final String KEY_APP_LINK_STATE = "app_link_state";
  private static final String KEY_CLEAR_DEFAULTS = "app_launch_clear_defaults";
  private static final String KEY_SUPPORTED_DOMAIN_URLS = "app_launch_supported_domain_urls";
  private static final String TAG = "AppLaunchSettings";
  private static final Intent sBrowserIntent = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.parse("http:"));
  private AppDomainsPreference mAppDomainUrls;
  private DropDownPreference mAppLinkState;
  private ClearDefaultsPreference mClearDefaultsPreference;
  private boolean mHasDomainUrls;
  private boolean mIsBrowser;
  private PackageManager mPm;
  
  private void buildStateDropDown()
  {
    if (this.mIsBrowser)
    {
      this.mAppLinkState.setShouldDisableView(true);
      this.mAppLinkState.setEnabled(false);
      this.mAppDomainUrls.setShouldDisableView(true);
      this.mAppDomainUrls.setEnabled(false);
    }
    do
    {
      return;
      this.mAppLinkState.setEntries(new CharSequence[] { getString(2131693422), getString(2131693423), getString(2131693424) });
      this.mAppLinkState.setEntryValues(new CharSequence[] { Integer.toString(2), Integer.toString(4), Integer.toString(3) });
      this.mAppLinkState.setEnabled(this.mHasDomainUrls);
    } while (!this.mHasDomainUrls);
    int j = this.mPm.getIntentVerificationStatusAsUser(this.mPackageName, UserHandle.myUserId());
    DropDownPreference localDropDownPreference = this.mAppLinkState;
    int i = j;
    if (j == 0) {
      i = 4;
    }
    localDropDownPreference.setValue(Integer.toString(i));
    this.mAppLinkState.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        return AppLaunchSettings.-wrap0(AppLaunchSettings.this, Integer.parseInt((String)paramAnonymousObject));
      }
    });
  }
  
  private CharSequence[] getEntries(String paramString, List<IntentFilterVerificationInfo> paramList, List<IntentFilter> paramList1)
  {
    paramString = Utils.getHandledDomains(this.mPm, paramString);
    return (CharSequence[])paramString.toArray(new CharSequence[paramString.size()]);
  }
  
  private boolean isBrowserApp(String paramString)
  {
    sBrowserIntent.setPackage(paramString);
    paramString = this.mPm.queryIntentActivitiesAsUser(sBrowserIntent, 131072, UserHandle.myUserId());
    int j = paramString.size();
    int i = 0;
    while (i < j)
    {
      ResolveInfo localResolveInfo = (ResolveInfo)paramString.get(i);
      if ((localResolveInfo.activityInfo != null) && (localResolveInfo.handleAllWebDataURI)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private boolean updateAppLinkState(int paramInt)
  {
    if (this.mIsBrowser) {
      return false;
    }
    int i = UserHandle.myUserId();
    if (this.mPm.getIntentVerificationStatusAsUser(this.mPackageName, i) == paramInt) {
      return false;
    }
    boolean bool = this.mPm.updateIntentVerificationStatusAsUser(this.mPackageName, paramInt, i);
    if (bool) {
      return paramInt == this.mPm.getIntentVerificationStatusAsUser(this.mPackageName, i);
    }
    Log.e("AppLaunchSettings", "Couldn't update intent verification status!");
    return bool;
  }
  
  protected AlertDialog createDialog(int paramInt1, int paramInt2)
  {
    return null;
  }
  
  protected int getMetricsCategory()
  {
    return 17;
  }
  
  public void onClick(View paramView) {}
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230773);
    this.mAppDomainUrls = ((AppDomainsPreference)findPreference("app_launch_supported_domain_urls"));
    this.mClearDefaultsPreference = ((ClearDefaultsPreference)findPreference("app_launch_clear_defaults"));
    this.mAppLinkState = ((DropDownPreference)findPreference("app_link_state"));
    this.mPm = getActivity().getPackageManager();
    this.mIsBrowser = isBrowserApp(this.mPackageName);
    if ((this.mAppEntry.info.privateFlags & 0x10) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHasDomainUrls = bool;
      if (!this.mIsBrowser)
      {
        paramBundle = this.mPm.getIntentFilterVerifications(this.mPackageName);
        List localList = this.mPm.getAllIntentFilters(this.mPackageName);
        paramBundle = getEntries(this.mPackageName, paramBundle, localList);
        this.mAppDomainUrls.setTitles(paramBundle);
        this.mAppDomainUrls.setValues(new int[paramBundle.length]);
      }
      buildStateDropDown();
      return;
    }
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return true;
  }
  
  protected boolean refreshUi()
  {
    this.mClearDefaultsPreference.setPackageName(this.mPackageName);
    this.mClearDefaultsPreference.setAppEntry(this.mAppEntry);
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppLaunchSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */