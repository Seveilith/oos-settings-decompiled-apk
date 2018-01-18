package com.android.settings.wifi;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.Indexable.SearchIndexProvider;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference.UserBadgeCache;
import com.android.settingslib.wifi.WifiTracker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SavedAccessPointsWifiSettings
  extends SettingsPreferenceFragment
  implements Indexable, WifiDialog.WifiDialogListener
{
  private static final String SAVE_DIALOG_ACCESS_POINT_STATE = "wifi_ap_state";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<SearchIndexableRaw> getRawDataToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      String str = paramAnonymousContext.getResources().getString(2131691461);
      Object localObject = new SearchIndexableRaw(paramAnonymousContext);
      ((SearchIndexableRaw)localObject).title = str;
      ((SearchIndexableRaw)localObject).screenTitle = str;
      ((SearchIndexableRaw)localObject).enabled = paramAnonymousBoolean;
      localArrayList.add(localObject);
      localObject = WifiTracker.getCurrentAccessPoints(paramAnonymousContext, true, false, true);
      int j = ((List)localObject).size();
      int i = 0;
      while (i < j)
      {
        SearchIndexableRaw localSearchIndexableRaw = new SearchIndexableRaw(paramAnonymousContext);
        localSearchIndexableRaw.title = ((AccessPoint)((List)localObject).get(i)).getSsidStr();
        localSearchIndexableRaw.screenTitle = str;
        localSearchIndexableRaw.enabled = paramAnonymousBoolean;
        localArrayList.add(localSearchIndexableRaw);
        i += 1;
      }
      return localArrayList;
    }
  };
  private static final String TAG = "SavedAccessPointsWifiSettings";
  private Bundle mAccessPointSavedState;
  private WifiDialog mDialog;
  private AccessPoint mDlgAccessPoint;
  private AccessPoint mSelectedAccessPoint;
  private AccessPointPreference.UserBadgeCache mUserBadgeCache;
  private WifiManager mWifiManager;
  
  private void initPreferences()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    Context localContext = getPrefContext();
    List localList = WifiTracker.getCurrentAccessPoints(localContext, true, false, true);
    Collections.sort(localList, new Comparator()
    {
      public int compare(AccessPoint paramAnonymousAccessPoint1, AccessPoint paramAnonymousAccessPoint2)
      {
        if (paramAnonymousAccessPoint1.getConfigName() != null) {
          return paramAnonymousAccessPoint1.getConfigName().compareTo(paramAnonymousAccessPoint2.getConfigName());
        }
        return -1;
      }
    });
    localPreferenceScreen.removeAll();
    int j = localList.size();
    int i = 0;
    while (i < j)
    {
      LongPressAccessPointPreference localLongPressAccessPointPreference = new LongPressAccessPointPreference((AccessPoint)localList.get(i), localContext, this.mUserBadgeCache, true, this);
      localLongPressAccessPointPreference.setIcon(null);
      localPreferenceScreen.addPreference(localLongPressAccessPointPreference);
      i += 1;
    }
    if (getPreferenceScreen().getPreferenceCount() < 1) {
      Log.w("SavedAccessPointsWifiSettings", "Saved networks activity loaded, but there are no saved networks!");
    }
  }
  
  private void showDialog(LongPressAccessPointPreference paramLongPressAccessPointPreference, boolean paramBoolean)
  {
    if (this.mDialog != null)
    {
      removeDialog(1);
      this.mDialog = null;
    }
    this.mDlgAccessPoint = paramLongPressAccessPointPreference.getAccessPoint();
    showDialog(1);
  }
  
  protected int getMetricsCategory()
  {
    return 106;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    if ((paramBundle != null) && (paramBundle.containsKey("wifi_ap_state"))) {
      this.mAccessPointSavedState = paramBundle.getBundle("wifi_ap_state");
    }
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230887);
    this.mUserBadgeCache = new AccessPointPreference.UserBadgeCache(getPackageManager());
  }
  
  public Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return super.onCreateDialog(paramInt);
    }
    if (this.mDlgAccessPoint == null)
    {
      this.mDlgAccessPoint = new AccessPoint(getActivity(), this.mAccessPointSavedState);
      this.mAccessPointSavedState = null;
    }
    this.mSelectedAccessPoint = this.mDlgAccessPoint;
    this.mDialog = new WifiDialog(getActivity(), this, this.mDlgAccessPoint, 0, true);
    return this.mDialog;
  }
  
  public void onForget(WifiDialog paramWifiDialog)
  {
    if (this.mSelectedAccessPoint != null)
    {
      this.mWifiManager.forget(this.mSelectedAccessPoint.getConfig().networkId, null);
      getPreferenceScreen().removePreference((Preference)this.mSelectedAccessPoint.getTag());
      this.mSelectedAccessPoint = null;
    }
  }
  
  public boolean onPreferenceTreeClick(Preference paramPreference)
  {
    if ((paramPreference instanceof LongPressAccessPointPreference))
    {
      showDialog((LongPressAccessPointPreference)paramPreference, false);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreference);
  }
  
  public void onResume()
  {
    super.onResume();
    initPreferences();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if ((this.mDialog != null) && (this.mDialog.isShowing()) && (this.mDlgAccessPoint != null))
    {
      this.mAccessPointSavedState = new Bundle();
      this.mDlgAccessPoint.saveWifiState(this.mAccessPointSavedState);
      paramBundle.putBundle("wifi_ap_state", this.mAccessPointSavedState);
    }
  }
  
  public void onSubmit(WifiDialog paramWifiDialog) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\SavedAccessPointsWifiSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */