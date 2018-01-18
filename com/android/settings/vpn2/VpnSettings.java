package com.android.settings.vpn2;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.IConnectivityManager;
import android.net.IConnectivityManager.Stub;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.KeyStore;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceScreen;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.internal.util.ArrayUtils;
import com.android.settings.GearPreference;
import com.android.settings.GearPreference.OnGearClickListener;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.google.android.collect.Lists;
import com.oneplus.settings.SettingsBaseApplication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VpnSettings
  extends RestrictedSettingsFragment
  implements Handler.Callback, Preference.OnPreferenceClickListener
{
  private static final String LOG_TAG = "VpnSettings";
  private static final int RESCAN_INTERVAL_MS = 1000;
  private static final int RESCAN_MESSAGE = 0;
  private static final NetworkRequest VPN_REQUEST = new NetworkRequest.Builder().removeCapability(15).removeCapability(13).removeCapability(14).build();
  private Map<AppVpnInfo, AppPreference> mAppPreferences = new ArrayMap();
  private LegacyVpnInfo mConnectedLegacyVpn;
  private ConnectivityManager mConnectivityManager;
  private final IConnectivityManager mConnectivityService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
  private GearPreference.OnGearClickListener mGearListener = new GearPreference.OnGearClickListener()
  {
    public void onGearClick(GearPreference paramAnonymousGearPreference)
    {
      if ((paramAnonymousGearPreference instanceof LegacyVpnPreference))
      {
        paramAnonymousGearPreference = (LegacyVpnPreference)paramAnonymousGearPreference;
        ConfigDialogFragment.show(VpnSettings.this, paramAnonymousGearPreference.getProfile(), true, true);
      }
      while (!(paramAnonymousGearPreference instanceof AppPreference)) {
        return;
      }
      paramAnonymousGearPreference = (AppPreference)paramAnonymousGearPreference;
      AppManagementFragment.show(VpnSettings.-wrap0(VpnSettings.this), paramAnonymousGearPreference);
    }
  };
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private Map<String, LegacyVpnPreference> mLegacyVpnPreferences = new ArrayMap();
  private ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback()
  {
    public void onAvailable(Network paramAnonymousNetwork)
    {
      if (VpnSettings.-get2(VpnSettings.this) != null) {
        VpnSettings.-get2(VpnSettings.this).sendEmptyMessage(0);
      }
    }
    
    public void onLost(Network paramAnonymousNetwork)
    {
      if (VpnSettings.-get2(VpnSettings.this) != null) {
        VpnSettings.-get2(VpnSettings.this).sendEmptyMessage(0);
      }
    }
  };
  private boolean mUnavailable;
  private Handler mUpdater;
  private UserManager mUserManager;
  
  public VpnSettings()
  {
    super("no_config_vpn");
  }
  
  private AppPreference findOrCreatePreference(AppVpnInfo paramAppVpnInfo)
  {
    AppPreference localAppPreference2 = (AppPreference)this.mAppPreferences.get(paramAppVpnInfo);
    AppPreference localAppPreference1 = localAppPreference2;
    if (localAppPreference2 == null)
    {
      localAppPreference1 = new AppPreference(getPrefContext(), paramAppVpnInfo.userId, paramAppVpnInfo.packageName);
      localAppPreference1.setOnGearClickListener(this.mGearListener);
      localAppPreference1.setOnPreferenceClickListener(this);
      this.mAppPreferences.put(paramAppVpnInfo, localAppPreference1);
    }
    return localAppPreference1;
  }
  
  private LegacyVpnPreference findOrCreatePreference(VpnProfile paramVpnProfile)
  {
    LegacyVpnPreference localLegacyVpnPreference2 = (LegacyVpnPreference)this.mLegacyVpnPreferences.get(paramVpnProfile.key);
    LegacyVpnPreference localLegacyVpnPreference1 = localLegacyVpnPreference2;
    if (localLegacyVpnPreference2 == null)
    {
      localLegacyVpnPreference1 = new LegacyVpnPreference(getPrefContext());
      localLegacyVpnPreference1.setOnGearClickListener(this.mGearListener);
      localLegacyVpnPreference1.setOnPreferenceClickListener(this);
      this.mLegacyVpnPreferences.put(paramVpnProfile.key, localLegacyVpnPreference1);
    }
    localLegacyVpnPreference1.setProfile(paramVpnProfile);
    return localLegacyVpnPreference1;
  }
  
  private Set<AppVpnInfo> getAlwaysOnAppVpnInfos()
  {
    ArraySet localArraySet = new ArraySet();
    Iterator localIterator = this.mUserManager.getUserProfiles().iterator();
    while (localIterator.hasNext())
    {
      int i = ((UserHandle)localIterator.next()).getIdentifier();
      String str = this.mConnectivityManager.getAlwaysOnVpnPackageForUser(i);
      if (str != null) {
        localArraySet.add(new AppVpnInfo(i, str));
      }
    }
    return localArraySet;
  }
  
  private Set<AppVpnInfo> getConnectedAppVpns()
  {
    localArraySet = new ArraySet();
    try
    {
      Iterator localIterator = this.mUserManager.getUserProfiles().iterator();
      while (localIterator.hasNext())
      {
        UserHandle localUserHandle = (UserHandle)localIterator.next();
        VpnConfig localVpnConfig = this.mConnectivityService.getVpnConfig(localUserHandle.getIdentifier());
        if ((localVpnConfig != null) && (!localVpnConfig.legacy)) {
          localArraySet.add(new AppVpnInfo(localUserHandle.getIdentifier(), localVpnConfig.user));
        }
      }
      return localArraySet;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("VpnSettings", "Failure updating VPN list with connected app VPNs", localRemoteException);
    }
  }
  
  private Map<String, LegacyVpnInfo> getConnectedLegacyVpns()
  {
    try
    {
      this.mConnectedLegacyVpn = this.mConnectivityService.getLegacyVpnInfo(UserHandle.myUserId());
      if (this.mConnectedLegacyVpn != null)
      {
        Map localMap = Collections.singletonMap(this.mConnectedLegacyVpn.key, this.mConnectedLegacyVpn);
        return localMap;
      }
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("VpnSettings", "Failure updating VPN list with connected legacy VPNs", localRemoteException);
    }
    return Collections.emptyMap();
  }
  
  static List<AppVpnInfo> getVpnApps(Context paramContext, boolean paramBoolean)
  {
    ArrayList localArrayList = Lists.newArrayList();
    Object localObject1 = paramContext;
    if (paramContext == null) {
      localObject1 = SettingsBaseApplication.mApplication;
    }
    Object localObject2;
    Iterator localIterator;
    if (paramBoolean)
    {
      localObject2 = new ArraySet();
      localIterator = UserManager.get((Context)localObject1).getUserProfiles().iterator();
      for (;;)
      {
        paramContext = (Context)localObject2;
        if (!localIterator.hasNext()) {
          break;
        }
        ((Set)localObject2).add(Integer.valueOf(((UserHandle)localIterator.next()).getIdentifier()));
      }
    }
    paramContext = Collections.singleton(Integer.valueOf(UserHandle.myUserId()));
    localObject1 = ((AppOpsManager)((Context)localObject1).getSystemService("appops")).getPackagesForOps(new int[] { 47 });
    if (localObject1 != null)
    {
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (AppOpsManager.PackageOps)((Iterator)localObject1).next();
        int j = UserHandle.getUserId(((AppOpsManager.PackageOps)localObject2).getUid());
        if (paramContext.contains(Integer.valueOf(j)))
        {
          int i = 0;
          localIterator = ((AppOpsManager.PackageOps)localObject2).getOps().iterator();
          while (localIterator.hasNext())
          {
            AppOpsManager.OpEntry localOpEntry = (AppOpsManager.OpEntry)localIterator.next();
            if ((localOpEntry.getOp() == 47) && (localOpEntry.getMode() == 0)) {
              i = 1;
            }
          }
          if (i != 0) {
            localArrayList.add(new AppVpnInfo(j, ((AppOpsManager.PackageOps)localObject2).getPackageName()));
          }
        }
      }
    }
    Collections.sort(localArrayList);
    return localArrayList;
  }
  
  static List<VpnProfile> loadVpnProfiles(KeyStore paramKeyStore, int... paramVarArgs)
  {
    ArrayList localArrayList = Lists.newArrayList();
    String[] arrayOfString = paramKeyStore.list("VPN_");
    int i = 0;
    int j = arrayOfString.length;
    if (i < j)
    {
      Object localObject = arrayOfString[i];
      localObject = VpnProfile.decode((String)localObject, paramKeyStore.get("VPN_" + (String)localObject));
      if ((localObject == null) || (ArrayUtils.contains(paramVarArgs, ((VpnProfile)localObject).type))) {}
      for (;;)
      {
        i += 1;
        break;
        localArrayList.add(localObject);
      }
    }
    return localArrayList;
  }
  
  protected int getHelpResource()
  {
    return 2131693017;
  }
  
  protected int getMetricsCategory()
  {
    return 100;
  }
  
  public boolean handleMessage(final Message paramMessage)
  {
    this.mUpdater.removeMessages(0);
    paramMessage = loadVpnProfiles(this.mKeyStore, new int[0]);
    final List localList = getVpnApps(getActivity(), true);
    final Map localMap = getConnectedLegacyVpns();
    final Set localSet1 = getConnectedAppVpns();
    final Set localSet2 = getAlwaysOnAppVpnInfos();
    final String str = VpnUtils.getLockdownVpn();
    Activity localActivity = getActivity();
    if (localActivity != null) {
      localActivity.runOnUiThread(new Runnable()
      {
        public void run()
        {
          if (!VpnSettings.this.isAdded()) {
            return;
          }
          Object localObject1 = new ArraySet();
          Object localObject2 = paramMessage.iterator();
          Object localObject3;
          Object localObject4;
          if (((Iterator)localObject2).hasNext())
          {
            localObject3 = (VpnProfile)((Iterator)localObject2).next();
            localObject4 = VpnSettings.-wrap2(VpnSettings.this, (VpnProfile)localObject3);
            if (localMap.containsKey(((VpnProfile)localObject3).key))
            {
              ((LegacyVpnPreference)localObject4).setState(((LegacyVpnInfo)localMap.get(((VpnProfile)localObject3).key)).state);
              label105:
              if (str == null) {
                break label154;
              }
            }
            label154:
            for (boolean bool = str.equals(((VpnProfile)localObject3).key);; bool = false)
            {
              ((LegacyVpnPreference)localObject4).setAlwaysOn(bool);
              ((Set)localObject1).add(localObject4);
              break;
              ((LegacyVpnPreference)localObject4).setState(LegacyVpnPreference.STATE_NONE);
              break label105;
            }
          }
          localObject2 = localList.iterator();
          if (((Iterator)localObject2).hasNext())
          {
            localObject3 = (AppVpnInfo)((Iterator)localObject2).next();
            localObject4 = VpnSettings.-wrap1(VpnSettings.this, (AppVpnInfo)localObject3);
            if (localSet1.contains(localObject3)) {
              ((AppPreference)localObject4).setState(3);
            }
            for (;;)
            {
              ((AppPreference)localObject4).setAlwaysOn(localSet2.contains(localObject3));
              ((Set)localObject1).add(localObject4);
              break;
              ((AppPreference)localObject4).setState(AppPreference.STATE_DISCONNECTED);
            }
          }
          VpnSettings.-get1(VpnSettings.this).values().retainAll((Collection)localObject1);
          VpnSettings.-get0(VpnSettings.this).values().retainAll((Collection)localObject1);
          localObject2 = VpnSettings.this.getPreferenceScreen();
          int i = ((PreferenceGroup)localObject2).getPreferenceCount() - 1;
          if (i >= 0)
          {
            localObject3 = ((PreferenceGroup)localObject2).getPreference(i);
            if (((Set)localObject1).contains(localObject3)) {
              ((Set)localObject1).remove(localObject3);
            }
            for (;;)
            {
              i -= 1;
              break;
              ((PreferenceGroup)localObject2).removePreference((Preference)localObject3);
            }
          }
          localObject1 = ((Iterable)localObject1).iterator();
          while (((Iterator)localObject1).hasNext()) {
            ((PreferenceGroup)localObject2).addPreference((Preference)((Iterator)localObject1).next());
          }
        }
      });
    }
    this.mUpdater.sendEmptyMessageDelayed(0, 1000L);
    return true;
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mUserManager = ((UserManager)getSystemService("user"));
    this.mConnectivityManager = ((ConnectivityManager)getSystemService("connectivity"));
    this.mUnavailable = isUiRestricted();
    if (this.mUnavailable) {}
    for (boolean bool = false;; bool = true)
    {
      setHasOptionsMenu(bool);
      addPreferencesFromResource(2131230880);
      return;
    }
  }
  
  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
    paramMenuInflater.inflate(2132017159, paramMenu);
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    for (long l = System.currentTimeMillis(); this.mLegacyVpnPreferences.containsKey(Long.toHexString(l)); l += 1L) {}
    ConfigDialogFragment.show(this, new VpnProfile(Long.toHexString(l)), true, false);
    return true;
  }
  
  public void onPause()
  {
    if (this.mUnavailable)
    {
      super.onPause();
      return;
    }
    this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
    if (this.mUpdater != null) {
      this.mUpdater.removeCallbacksAndMessages(null);
    }
    super.onPause();
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if ((paramPreference instanceof LegacyVpnPreference))
    {
      paramPreference = ((LegacyVpnPreference)paramPreference).getProfile();
      if ((this.mConnectedLegacyVpn != null) && (paramPreference.key.equals(this.mConnectedLegacyVpn.key)) && (this.mConnectedLegacyVpn.state == 3)) {
        try
        {
          this.mConnectedLegacyVpn.intent.send();
          return true;
        }
        catch (Exception localException)
        {
          Log.w("VpnSettings", "Starting config intent failed", localException);
        }
      }
      ConfigDialogFragment.show(this, paramPreference, false, true);
      return true;
    }
    if ((paramPreference instanceof AppPreference))
    {
      paramPreference = (AppPreference)paramPreference;
      if (paramPreference.getState() == 3) {}
      for (boolean bool = true; !bool; bool = false) {
        try
        {
          UserHandle localUserHandle = UserHandle.of(paramPreference.getUserId());
          Context localContext = getActivity().createPackageContextAsUser(getActivity().getPackageName(), 0, localUserHandle);
          Intent localIntent = localContext.getPackageManager().getLaunchIntentForPackage(paramPreference.getPackageName());
          if (localIntent == null) {
            break;
          }
          localContext.startActivityAsUser(localIntent, localUserHandle);
          return true;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          Log.w("VpnSettings", "VPN provider does not exist: " + paramPreference.getPackageName(), localNameNotFoundException);
        }
      }
      AppDialogFragment.show(this, paramPreference.getPackageInfo(), paramPreference.getLabel(), false, bool);
      return true;
    }
    return false;
  }
  
  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    super.onPrepareOptionsMenu(paramMenu);
    int i = 0;
    while (i < paramMenu.size()) {
      if (isUiRestrictedByOnlyAdmin())
      {
        RestrictedLockUtils.setMenuItemAsDisabledByAdmin(getPrefContext(), paramMenu.getItem(i), getRestrictionEnforcedAdmin());
        i += 1;
      }
      else
      {
        MenuItem localMenuItem = paramMenu.getItem(i);
        if (this.mUnavailable) {}
        for (boolean bool = false;; bool = true)
        {
          localMenuItem.setEnabled(bool);
          break;
        }
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.mUnavailable)
    {
      if (!isUiRestrictedByOnlyAdmin()) {
        getEmptyTextView().setText(2131689631);
      }
      getPreferenceScreen().removeAll();
      return;
    }
    getEmptyTextView().setText(2131692875);
    this.mConnectivityManager.registerNetworkCallback(VPN_REQUEST, this.mNetworkCallback);
    if (this.mUpdater == null) {
      this.mUpdater = new Handler(this);
    }
    this.mUpdater.sendEmptyMessage(0);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\VpnSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */