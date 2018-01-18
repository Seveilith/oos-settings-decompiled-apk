package com.android.settingslib.wifi;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.wifi.IWifiManager;
import android.net.wifi.IWifiManager.Stub;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.NetworkSelectionStatus;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TtsSpan.VerbatimBuilder;
import android.util.Log;
import android.util.LruCache;
import android.util.OpFeatures;
import com.android.settingslib.R.array;
import com.android.settingslib.R.string;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;

public class AccessPoint
  implements Comparable<AccessPoint>
{
  public static final int HIGHER_FREQ_24GHZ = 2500;
  public static final int HIGHER_FREQ_5GHZ = 5900;
  private static final String KEY_ACTIVE = "key_active";
  private static final String KEY_CONFIG = "key_config";
  private static final String KEY_NETWORKINFO = "key_networkinfo";
  private static final String KEY_PSKTYPE = "key_psktype";
  private static final String KEY_SCANRESULT = "key_scanresult";
  private static final String KEY_SCANRESULTCACHE = "key_scanresultcache";
  private static final String KEY_SECURITY = "key_security";
  private static final String KEY_SSID = "key_ssid";
  private static final String KEY_WIFIINFO = "key_wifiinfo";
  public static final int LOWER_FREQ_24GHZ = 2400;
  public static final int LOWER_FREQ_5GHZ = 4900;
  public static final int OEM_SIGNAL_LEVELS = 5;
  private static final int PSK_UNKNOWN = 0;
  private static final int PSK_WPA = 1;
  private static final int PSK_WPA2 = 2;
  private static final int PSK_WPA_WPA2 = 3;
  public static final int SECURITY_EAP = 3;
  public static final int SECURITY_NONE = 0;
  public static final int SECURITY_PSK = 2;
  public static final int SECURITY_WAPI_CERT = 5;
  public static final int SECURITY_WAPI_PSK = 4;
  public static final int SECURITY_WEP = 1;
  public static final int SIGNAL_LEVELS = 4;
  static final String TAG = "SettingsLib.AccessPoint";
  private String bssid;
  public boolean foundInScanResult = false;
  private boolean isCurrentConnected = false;
  private AccessPointListener mAccessPointListener;
  private WifiConfiguration mConfig;
  private final Context mContext;
  private WifiInfo mInfo;
  private NetworkInfo mNetworkInfo;
  private int mRssi = Integer.MAX_VALUE;
  public LruCache<String, ScanResult> mScanResultCache = new LruCache(32);
  private long mSeen = 0L;
  private Object mTag;
  private String mWAPIASCertFile;
  private String mWAPIUserCertFile;
  private int networkId = -1;
  private int pskType = 0;
  private int security;
  private String ssid;
  private int wapiPskType;
  
  AccessPoint(Context paramContext, ScanResult paramScanResult)
  {
    this.mContext = paramContext;
    initWithScanResult(paramScanResult);
  }
  
  AccessPoint(Context paramContext, WifiConfiguration paramWifiConfiguration)
  {
    this.mContext = paramContext;
    loadConfig(paramWifiConfiguration);
  }
  
  public AccessPoint(Context paramContext, Bundle paramBundle)
  {
    this.mContext = paramContext;
    this.mConfig = ((WifiConfiguration)paramBundle.getParcelable("key_config"));
    if (this.mConfig != null) {
      loadConfig(this.mConfig);
    }
    if (paramBundle.containsKey("key_ssid")) {
      this.ssid = paramBundle.getString("key_ssid");
    }
    if (paramBundle.containsKey("key_security")) {
      this.security = paramBundle.getInt("key_security");
    }
    if (paramBundle.containsKey("key_psktype")) {
      this.pskType = paramBundle.getInt("key_psktype");
    }
    this.mInfo = ((WifiInfo)paramBundle.getParcelable("key_wifiinfo"));
    if (paramBundle.containsKey("key_networkinfo")) {
      this.mNetworkInfo = ((NetworkInfo)paramBundle.getParcelable("key_networkinfo"));
    }
    if (paramBundle.containsKey("key_scanresultcache"))
    {
      paramContext = paramBundle.getParcelableArrayList("key_scanresultcache");
      this.mScanResultCache.evictAll();
      paramContext = paramContext.iterator();
      while (paramContext.hasNext())
      {
        ScanResult localScanResult = (ScanResult)paramContext.next();
        this.mScanResultCache.put(localScanResult.BSSID, localScanResult);
      }
    }
    if (paramBundle.containsKey("key_active")) {
      if (paramBundle.getInt("key_active") != 1) {
        break label301;
      }
    }
    label301:
    for (boolean bool = true;; bool = false)
    {
      this.isCurrentConnected = bool;
      update(this.mConfig, this.mInfo, this.mNetworkInfo);
      this.mRssi = getRssi();
      this.mSeen = getSeen();
      return;
    }
  }
  
  public static String convertToQuotedString(String paramString)
  {
    return "\"" + paramString + "\"";
  }
  
  private int getOemLevel(int paramInt)
  {
    int i = WifiManager.calculateSignalLevel(paramInt, 5);
    paramInt = i;
    if (i > 0) {
      paramInt = i - 1;
    }
    return paramInt;
  }
  
  private static int getPskType(ScanResult paramScanResult)
  {
    boolean bool1 = paramScanResult.capabilities.contains("WPA-PSK");
    boolean bool2 = paramScanResult.capabilities.contains("WPA2-PSK");
    if ((bool2) && (bool1)) {
      return 3;
    }
    if (bool2) {
      return 2;
    }
    if (bool1) {
      return 1;
    }
    Log.w("SettingsLib.AccessPoint", "Received abnormal flag string: " + paramScanResult.capabilities);
    return 0;
  }
  
  private static int getSecurity(ScanResult paramScanResult)
  {
    if (paramScanResult.capabilities.contains("WEP")) {
      return 1;
    }
    if (paramScanResult.capabilities.contains("PSK")) {
      return 2;
    }
    if (paramScanResult.capabilities.contains("EAP")) {
      return 3;
    }
    if (paramScanResult.capabilities.contains("WAPI-KEY")) {
      return 4;
    }
    if (paramScanResult.capabilities.contains("WAPI-CERT")) {
      return 5;
    }
    return 0;
  }
  
  static int getSecurity(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration.allowedKeyManagement.get(1)) {
      return 2;
    }
    if ((paramWifiConfiguration.allowedKeyManagement.get(2)) || (paramWifiConfiguration.allowedKeyManagement.get(3))) {
      return 3;
    }
    if (paramWifiConfiguration.allowedKeyManagement.get(6)) {
      return 4;
    }
    if (paramWifiConfiguration.allowedKeyManagement.get(7)) {
      return 5;
    }
    if (paramWifiConfiguration.wepKeys[0] != null) {
      return 1;
    }
    return 0;
  }
  
  private String getSettingsSummary(WifiConfiguration paramWifiConfiguration)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if ((isActive()) && (paramWifiConfiguration != null) && (paramWifiConfiguration.isPasspoint()))
    {
      localStringBuilder.append(getSummary(this.mContext, getDetailedState(), false, paramWifiConfiguration.providerFriendlyName));
      if (WifiTracker.sVerboseLogging <= 0) {
        break label658;
      }
      if ((this.mInfo != null) && (this.mNetworkInfo != null) && (this.isCurrentConnected)) {
        localStringBuilder.append(" f=").append(Integer.toString(this.mInfo.getFrequency()));
      }
      localStringBuilder.append(" ").append(getVisibilityStatus());
      if ((paramWifiConfiguration != null) && (!paramWifiConfiguration.getNetworkSelectionStatus().isNetworkEnabled())) {
        break label503;
      }
    }
    for (;;)
    {
      if (paramWifiConfiguration == null) {
        break label658;
      }
      paramWifiConfiguration = paramWifiConfiguration.getNetworkSelectionStatus();
      int i = 0;
      while (i < 10)
      {
        if (paramWifiConfiguration.getDisableReasonCounter(i) != 0) {
          localStringBuilder.append(" ").append(WifiConfiguration.NetworkSelectionStatus.getNetworkDisableReasonString(i)).append("=").append(paramWifiConfiguration.getDisableReasonCounter(i));
        }
        i += 1;
      }
      if (isActive())
      {
        Context localContext = this.mContext;
        NetworkInfo.DetailedState localDetailedState = getDetailedState();
        if (this.mInfo != null) {}
        for (boolean bool = this.mInfo.isEphemeral();; bool = false)
        {
          localStringBuilder.append(getSummary(localContext, localDetailedState, bool));
          break;
        }
      }
      if ((paramWifiConfiguration != null) && (paramWifiConfiguration.isPasspoint()))
      {
        localStringBuilder.append(String.format(this.mContext.getString(R.string.available_via_passpoint), new Object[] { paramWifiConfiguration.providerFriendlyName }));
        break;
      }
      if ((paramWifiConfiguration != null) && (paramWifiConfiguration.hasNoInternetAccess()))
      {
        if (paramWifiConfiguration.getNetworkSelectionStatus().isNetworkPermanentlyDisabled()) {}
        for (i = R.string.wifi_no_internet_no_reconnect;; i = R.string.wifi_no_internet)
        {
          localStringBuilder.append(this.mContext.getString(i));
          break;
        }
      }
      if ((paramWifiConfiguration == null) || (paramWifiConfiguration.getNetworkSelectionStatus().isNetworkEnabled()))
      {
        if (this.mRssi != Integer.MAX_VALUE) {
          break label480;
        }
        localStringBuilder.append(this.mContext.getString(R.string.wifi_not_in_range));
        break;
      }
      switch (paramWifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionDisableReason())
      {
      default: 
        break;
      case 2: 
        localStringBuilder.append(this.mContext.getString(R.string.wifi_disabled_generic));
        break;
      case 3: 
        localStringBuilder.append(this.mContext.getString(R.string.wifi_disabled_password_failure));
        break;
      case 4: 
      case 5: 
        localStringBuilder.append(this.mContext.getString(R.string.wifi_disabled_network_failure));
        break;
      }
      label480:
      if (paramWifiConfiguration == null) {
        break;
      }
      localStringBuilder.append(this.mContext.getString(R.string.wifi_remembered));
      break;
      label503:
      localStringBuilder.append(" (").append(paramWifiConfiguration.getNetworkSelectionStatus().getNetworkStatusString());
      if (paramWifiConfiguration.getNetworkSelectionStatus().getDisableTime() > 0L)
      {
        long l1 = (System.currentTimeMillis() - paramWifiConfiguration.getNetworkSelectionStatus().getDisableTime()) / 1000L;
        long l2 = l1 / 60L % 60L;
        long l3 = l2 / 60L % 60L;
        localStringBuilder.append(", ");
        if (l3 > 0L) {
          localStringBuilder.append(Long.toString(l3)).append("h ");
        }
        localStringBuilder.append(Long.toString(l2)).append("m ");
        localStringBuilder.append(Long.toString(l1 % 60L)).append("s ");
      }
      localStringBuilder.append(")");
    }
    label658:
    return localStringBuilder.toString();
  }
  
  public static String getSummary(Context paramContext, NetworkInfo.DetailedState paramDetailedState, boolean paramBoolean)
  {
    return getSummary(paramContext, null, paramDetailedState, paramBoolean, null);
  }
  
  public static String getSummary(Context paramContext, NetworkInfo.DetailedState paramDetailedState, boolean paramBoolean, String paramString)
  {
    return getSummary(paramContext, null, paramDetailedState, paramBoolean, paramString);
  }
  
  public static String getSummary(Context paramContext, String paramString1, NetworkInfo.DetailedState paramDetailedState, boolean paramBoolean, String paramString2)
  {
    if ((paramDetailedState == NetworkInfo.DetailedState.CONNECTED) && (paramString1 == null))
    {
      if (!TextUtils.isEmpty(paramString2)) {
        return String.format(paramContext.getString(R.string.connected_via_passpoint), new Object[] { paramString2 });
      }
      if (paramBoolean) {
        return paramContext.getString(R.string.connected_via_wfa);
      }
    }
    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
    if (paramDetailedState == NetworkInfo.DetailedState.CONNECTED) {
      paramString2 = IWifiManager.Stub.asInterface(ServiceManager.getService("wifi"));
    }
    try
    {
      paramString2 = paramString2.getCurrentNetwork();
      paramString2 = localConnectivityManager.getNetworkCapabilities(paramString2);
      if ((!OpFeatures.isSupport(new int[] { 1 })) || (paramString2 == null) || (paramString2.hasCapability(16)))
      {
        if (paramDetailedState != null) {
          break label160;
        }
        Log.w("SettingsLib.AccessPoint", "state is null, returning empty summary");
        return "";
      }
    }
    catch (RemoteException paramString2)
    {
      for (;;)
      {
        paramString2 = null;
      }
    }
    return paramContext.getString(R.string.wifi_connected_no_internet);
    label160:
    paramContext = paramContext.getResources();
    if (paramString1 == null) {}
    for (int i = R.array.wifi_status;; i = R.array.wifi_status_with_ssid)
    {
      paramContext = paramContext.getStringArray(i);
      i = paramDetailedState.ordinal();
      if ((i < paramContext.length) && (paramContext[i].length() != 0)) {
        break;
      }
      return "";
    }
    return String.format(paramContext[i], new Object[] { paramString1 });
  }
  
  private String getVisibilityStatus()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Object localObject2 = null;
    Object localObject1 = null;
    String str = null;
    System.currentTimeMillis();
    if (this.mInfo != null)
    {
      str = this.mInfo.getBSSID();
      if (str != null) {
        localStringBuilder.append(" ").append(str);
      }
      localStringBuilder.append(" rssi=").append(this.mInfo.getRssi());
      localStringBuilder.append(" ");
      localStringBuilder.append(" score=").append(this.mInfo.score);
      localStringBuilder.append(String.format(" tx=%.1f,", new Object[] { Double.valueOf(this.mInfo.txSuccessRate) }));
      localStringBuilder.append(String.format("%.1f,", new Object[] { Double.valueOf(this.mInfo.txRetriesRate) }));
      localStringBuilder.append(String.format("%.1f ", new Object[] { Double.valueOf(this.mInfo.txBadRate) }));
      localStringBuilder.append(String.format("rx=%.1f", new Object[] { Double.valueOf(this.mInfo.rxSuccessRate) }));
    }
    int i = WifiConfiguration.INVALID_RSSI;
    int j = WifiConfiguration.INVALID_RSSI;
    int n = 0;
    int i3 = 0;
    int m = 0;
    int k = 0;
    Iterator localIterator = this.mScanResultCache.snapshot().values().iterator();
    while (localIterator.hasNext())
    {
      ScanResult localScanResult = (ScanResult)localIterator.next();
      int i2;
      int i1;
      if ((localScanResult.frequency >= 4900) && (localScanResult.frequency <= 5900))
      {
        i2 = n + 1;
        i1 = i3;
      }
      int i4;
      Object localObject3;
      for (;;)
      {
        if ((localScanResult.frequency < 4900) || (localScanResult.frequency > 5900)) {
          break label547;
        }
        i4 = i;
        if (localScanResult.level > i) {
          i4 = localScanResult.level;
        }
        i3 = i1;
        n = i2;
        i = i4;
        if (k >= 4) {
          break;
        }
        localObject3 = localObject1;
        if (localObject1 == null) {
          localObject3 = new StringBuilder();
        }
        ((StringBuilder)localObject3).append(" \n{").append(localScanResult.BSSID);
        if ((str != null) && (localScanResult.BSSID.equals(str))) {
          ((StringBuilder)localObject3).append("*");
        }
        ((StringBuilder)localObject3).append("=").append(localScanResult.frequency);
        ((StringBuilder)localObject3).append(",").append(localScanResult.level);
        ((StringBuilder)localObject3).append("}");
        k += 1;
        i3 = i1;
        n = i2;
        i = i4;
        localObject1 = localObject3;
        break;
        i1 = i3;
        i2 = n;
        if (localScanResult.frequency >= 2400)
        {
          i1 = i3;
          i2 = n;
          if (localScanResult.frequency <= 2500)
          {
            i1 = i3 + 1;
            i2 = n;
          }
        }
      }
      label547:
      i3 = i1;
      n = i2;
      if (localScanResult.frequency >= 2400)
      {
        i3 = i1;
        n = i2;
        if (localScanResult.frequency <= 2500)
        {
          i4 = j;
          if (localScanResult.level > j) {
            i4 = localScanResult.level;
          }
          i3 = i1;
          n = i2;
          j = i4;
          if (m < 4)
          {
            localObject3 = localObject2;
            if (localObject2 == null) {
              localObject3 = new StringBuilder();
            }
            ((StringBuilder)localObject3).append(" \n{").append(localScanResult.BSSID);
            if ((str != null) && (localScanResult.BSSID.equals(str))) {
              ((StringBuilder)localObject3).append("*");
            }
            ((StringBuilder)localObject3).append("=").append(localScanResult.frequency);
            ((StringBuilder)localObject3).append(",").append(localScanResult.level);
            ((StringBuilder)localObject3).append("}");
            m += 1;
            i3 = i1;
            n = i2;
            j = i4;
            localObject2 = localObject3;
          }
        }
      }
    }
    localStringBuilder.append(" [");
    if (i3 > 0)
    {
      localStringBuilder.append("(").append(i3).append(")");
      if (m > 4) {
        break label876;
      }
      if (localObject2 != null) {
        localStringBuilder.append(((StringBuilder)localObject2).toString());
      }
    }
    localStringBuilder.append(";");
    if (n > 0)
    {
      localStringBuilder.append("(").append(n).append(")");
      if (k > 4) {
        break label914;
      }
      if (localObject1 != null) {
        localStringBuilder.append(((StringBuilder)localObject1).toString());
      }
    }
    for (;;)
    {
      localStringBuilder.append("]");
      return localStringBuilder.toString();
      label876:
      localStringBuilder.append("max=").append(j);
      if (localObject2 == null) {
        break;
      }
      localStringBuilder.append(",").append(((StringBuilder)localObject2).toString());
      break;
      label914:
      localStringBuilder.append("max=").append(i);
      if (localObject1 != null) {
        localStringBuilder.append(",").append(((StringBuilder)localObject1).toString());
      }
    }
  }
  
  private void initWithScanResult(ScanResult paramScanResult)
  {
    this.ssid = paramScanResult.SSID;
    this.bssid = paramScanResult.BSSID;
    this.security = getSecurity(paramScanResult);
    if (this.security == 2) {
      this.pskType = getPskType(paramScanResult);
    }
    this.mRssi = paramScanResult.level;
    this.mSeen = paramScanResult.timestamp;
  }
  
  private boolean isInfoForThisAccessPoint(WifiConfiguration paramWifiConfiguration, WifiInfo paramWifiInfo)
  {
    if ((!isPasspoint()) && (this.networkId != -1)) {
      return this.networkId == paramWifiInfo.getNetworkId();
    }
    if (paramWifiConfiguration != null) {
      return matches(paramWifiConfiguration);
    }
    return this.ssid.equals(removeDoubleQuotes(paramWifiInfo.getSSID()));
  }
  
  private boolean isValiableConnectedBssid()
  {
    return (this.isCurrentConnected) && (this.bssid != null) && (!this.bssid.equals("00:00:00:00:00:00"));
  }
  
  static String removeDoubleQuotes(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return "";
    }
    int i = paramString.length();
    if ((i > 1) && (paramString.charAt(0) == '"') && (paramString.charAt(i - 1) == '"')) {
      return paramString.substring(1, i - 1);
    }
    return paramString;
  }
  
  public static String securityToString(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 1) {
      return "WEP";
    }
    if (paramInt1 == 2)
    {
      if (paramInt2 == 1) {
        return "WPA";
      }
      if (paramInt2 == 2) {
        return "WPA2";
      }
      if (paramInt2 == 3) {
        return "WPA_WPA2";
      }
      return "PSK";
    }
    if (paramInt1 == 3) {
      return "EAP";
    }
    return "NONE";
  }
  
  public void clearConfig()
  {
    this.mConfig = null;
    this.networkId = -1;
  }
  
  public int compareTo(@NonNull AccessPoint paramAccessPoint)
  {
    if ((!isActive()) || (paramAccessPoint.isActive()))
    {
      if ((!isActive()) && (paramAccessPoint.isActive())) {
        return 1;
      }
    }
    else {
      return -1;
    }
    if ((this.mRssi != Integer.MAX_VALUE) && (paramAccessPoint.mRssi == Integer.MAX_VALUE)) {
      return -1;
    }
    if ((this.mRssi == Integer.MAX_VALUE) && (paramAccessPoint.mRssi != Integer.MAX_VALUE)) {
      return 1;
    }
    if ((this.networkId != -1) && (paramAccessPoint.networkId == -1)) {
      return -1;
    }
    if ((this.networkId == -1) && (paramAccessPoint.networkId != -1)) {
      return 1;
    }
    int i = getOemLevel(paramAccessPoint.mRssi) - getOemLevel(this.mRssi);
    if (i != 0) {
      return i;
    }
    return this.ssid.compareToIgnoreCase(paramAccessPoint.ssid);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (!(paramObject instanceof AccessPoint)) {
      return false;
    }
    if (compareTo((AccessPoint)paramObject) == 0) {
      bool = true;
    }
    return bool;
  }
  
  public void generateOpenNetworkConfig()
  {
    if (this.security != 0) {
      throw new IllegalStateException();
    }
    if (this.mConfig != null) {
      return;
    }
    this.mConfig = new WifiConfiguration();
    this.mConfig.SSID = convertToQuotedString(this.ssid);
    this.mConfig.allowedKeyManagement.set(0);
  }
  
  public String getBssid()
  {
    return this.bssid;
  }
  
  public WifiConfiguration getConfig()
  {
    return this.mConfig;
  }
  
  public String getConfigName()
  {
    if ((this.mConfig != null) && (this.mConfig.isPasspoint())) {
      return this.mConfig.providerFriendlyName;
    }
    return this.ssid;
  }
  
  public NetworkInfo.DetailedState getDetailedState()
  {
    if ((this.mNetworkInfo != null) && (this.isCurrentConnected)) {
      return this.mNetworkInfo.getDetailedState();
    }
    Log.w("SettingsLib.AccessPoint", "NetworkInfo is null, cannot return detailed state");
    return null;
  }
  
  public WifiInfo getInfo()
  {
    return this.mInfo;
  }
  
  public int getLevel()
  {
    if (this.mRssi == Integer.MAX_VALUE) {
      return -1;
    }
    return getOemLevel(this.mRssi);
  }
  
  public NetworkInfo getNetworkInfo()
  {
    return this.mNetworkInfo;
  }
  
  public int getRssi()
  {
    int i = Integer.MIN_VALUE;
    Iterator localIterator = this.mScanResultCache.snapshot().values().iterator();
    while (localIterator.hasNext())
    {
      ScanResult localScanResult = (ScanResult)localIterator.next();
      if (isValiableConnectedBssid())
      {
        if ((this.bssid.equals(localScanResult.BSSID)) && (localScanResult.level > i)) {
          i = localScanResult.level;
        }
      }
      else if (localScanResult.level > i) {
        i = localScanResult.level;
      }
    }
    return i;
  }
  
  public String getSavedNetworkSummary()
  {
    Object localObject2 = this.mConfig;
    PackageManager localPackageManager;
    int i;
    Object localObject1;
    if (localObject2 != null)
    {
      localPackageManager = this.mContext.getPackageManager();
      String str = localPackageManager.getNameForUid(1000);
      i = UserHandle.getUserId(((WifiConfiguration)localObject2).creatorUid);
      localObject1 = null;
      if ((((WifiConfiguration)localObject2).creatorName == null) || (!((WifiConfiguration)localObject2).creatorName.equals(str))) {
        break label93;
      }
      localObject1 = this.mContext.getApplicationInfo();
    }
    for (;;)
    {
      if ((localObject1 == null) || (((ApplicationInfo)localObject1).packageName.equals(this.mContext.getString(R.string.settings_package)))) {}
      label93:
      do
      {
        return "";
        try
        {
          localObject2 = AppGlobals.getPackageManager().getApplicationInfo(((WifiConfiguration)localObject2).creatorName, 0, i);
          localObject1 = localObject2;
        }
        catch (RemoteException localRemoteException) {}
      } while (((ApplicationInfo)localObject1).packageName.equals(this.mContext.getString(R.string.certinstaller_package)));
      return this.mContext.getString(R.string.saved_network, new Object[] { ((ApplicationInfo)localObject1).loadLabel(localPackageManager) });
    }
  }
  
  public int getSecurity()
  {
    return this.security;
  }
  
  public String getSecurityString(boolean paramBoolean)
  {
    Context localContext = this.mContext;
    if ((this.mConfig != null) && (this.mConfig.isPasspoint()))
    {
      if (paramBoolean) {
        return localContext.getString(R.string.wifi_security_short_eap);
      }
      return localContext.getString(R.string.wifi_security_eap);
    }
    switch (this.security)
    {
    default: 
      if (paramBoolean) {
        return "";
      }
      break;
    case 3: 
      if (paramBoolean) {
        return localContext.getString(R.string.wifi_security_short_eap);
      }
      return localContext.getString(R.string.wifi_security_eap);
    case 2: 
      switch (this.pskType)
      {
      default: 
        if (paramBoolean) {
          return localContext.getString(R.string.wifi_security_short_psk_generic);
        }
        break;
      case 1: 
        if (paramBoolean) {
          return localContext.getString(R.string.wifi_security_short_wpa);
        }
        return localContext.getString(R.string.wifi_security_wpa);
      case 2: 
        if (paramBoolean) {
          return localContext.getString(R.string.wifi_security_short_wpa2);
        }
        return localContext.getString(R.string.wifi_security_wpa2);
      case 3: 
        if (paramBoolean) {
          return localContext.getString(R.string.wifi_security_short_wpa_wpa2);
        }
        return localContext.getString(R.string.wifi_security_wpa_wpa2);
      }
      return localContext.getString(R.string.wifi_security_psk_generic);
    case 1: 
      if (paramBoolean) {
        return localContext.getString(R.string.wifi_security_short_wep);
      }
      return localContext.getString(R.string.wifi_security_wep);
    case 4: 
      if (paramBoolean) {
        return localContext.getString(R.string.wifi_security_short_WAPI_PSK);
      }
      return localContext.getString(R.string.wifi_security_WAPI_PSK);
    case 5: 
      if (paramBoolean) {
        return localContext.getString(R.string.wifi_security_short_WAPI_CERT);
      }
      return localContext.getString(R.string.wifi_security_WAPI_CERT);
    }
    return localContext.getString(R.string.wifi_security_none);
  }
  
  public long getSeen()
  {
    long l = 0L;
    Iterator localIterator = this.mScanResultCache.snapshot().values().iterator();
    while (localIterator.hasNext())
    {
      ScanResult localScanResult = (ScanResult)localIterator.next();
      if (localScanResult.timestamp > l) {
        l = localScanResult.timestamp;
      }
    }
    return l;
  }
  
  public String getSettingsSummary()
  {
    return getSettingsSummary(this.mConfig);
  }
  
  public CharSequence getSsid()
  {
    SpannableString localSpannableString = new SpannableString(this.ssid);
    localSpannableString.setSpan(new TtsSpan.VerbatimBuilder(this.ssid).build(), 0, this.ssid.length(), 18);
    return localSpannableString;
  }
  
  public String getSsidStr()
  {
    return this.ssid;
  }
  
  public String getSummary()
  {
    return getSettingsSummary(this.mConfig);
  }
  
  public Object getTag()
  {
    return this.mTag;
  }
  
  public int hashCode()
  {
    int i = 0;
    if (this.mInfo != null) {
      i = this.mInfo.hashCode() * 13 + 0;
    }
    return i + this.mRssi * 19 + this.networkId * 23 + this.ssid.hashCode() * 29;
  }
  
  public boolean isActive()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mNetworkInfo != null) {
      if (this.networkId == -1)
      {
        bool1 = bool2;
        if (this.mNetworkInfo.getState() == NetworkInfo.State.DISCONNECTED) {}
      }
      else
      {
        bool1 = this.isCurrentConnected;
      }
    }
    return bool1;
  }
  
  public boolean isConnectable()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (getLevel() != -1)
    {
      bool1 = bool2;
      if (getDetailedState() == null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean isEphemeral()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mInfo != null)
    {
      bool1 = bool2;
      if (this.mInfo.isEphemeral())
      {
        bool1 = bool2;
        if (this.mNetworkInfo != null)
        {
          bool1 = bool2;
          if (this.isCurrentConnected)
          {
            bool1 = bool2;
            if (this.mNetworkInfo.getState() != NetworkInfo.State.DISCONNECTED) {
              bool1 = true;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public boolean isPasspoint()
  {
    if (this.mConfig != null) {
      return this.mConfig.isPasspoint();
    }
    return false;
  }
  
  public boolean isSaved()
  {
    return (this.networkId != -1) || ((this.mConfig != null) && (this.mConfig.networkId != -1));
  }
  
  void loadConfig(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration.isPasspoint())
    {
      this.ssid = paramWifiConfiguration.providerFriendlyName;
      this.bssid = paramWifiConfiguration.BSSID;
      this.security = getSecurity(paramWifiConfiguration);
      this.networkId = paramWifiConfiguration.networkId;
      this.mConfig = paramWifiConfiguration;
      this.wapiPskType = paramWifiConfiguration.wapiPskType;
      Log.e("SettingsLib.AccessPoint", "loadConfig() ssid:" + this.ssid + "  WAPI PSK key type: " + this.wapiPskType + ", networkId: " + this.networkId);
      return;
    }
    if (paramWifiConfiguration.SSID == null) {}
    for (String str = "";; str = removeDoubleQuotes(paramWifiConfiguration.SSID))
    {
      this.ssid = str;
      break;
    }
  }
  
  public boolean matches(ScanResult paramScanResult)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.ssid.equals(paramScanResult.SSID))
    {
      bool1 = bool2;
      if (this.security == getSecurity(paramScanResult)) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public boolean matches(WifiConfiguration paramWifiConfiguration)
  {
    if ((paramWifiConfiguration.isPasspoint()) && (this.mConfig != null) && (this.mConfig.isPasspoint())) {
      return paramWifiConfiguration.FQDN.equals(this.mConfig.FQDN);
    }
    if ((this.ssid.equals(removeDoubleQuotes(paramWifiConfiguration.SSID))) && (this.security == getSecurity(paramWifiConfiguration))) {
      return (this.mConfig == null) || (this.mConfig.shared == paramWifiConfiguration.shared);
    }
    return false;
  }
  
  public void saveWifiState(Bundle paramBundle)
  {
    if (this.ssid != null) {
      paramBundle.putString("key_ssid", getSsidStr());
    }
    paramBundle.putInt("key_security", this.security);
    paramBundle.putInt("key_psktype", this.pskType);
    if (this.mConfig != null) {
      paramBundle.putParcelable("key_config", this.mConfig);
    }
    paramBundle.putParcelable("key_wifiinfo", this.mInfo);
    paramBundle.putParcelableArrayList("key_scanresultcache", new ArrayList(this.mScanResultCache.snapshot().values()));
    if (this.mNetworkInfo != null) {
      paramBundle.putParcelable("key_networkinfo", this.mNetworkInfo);
    }
    if (this.isCurrentConnected) {}
    for (int i = 1;; i = 0)
    {
      paramBundle.putInt("key_active", i);
      return;
    }
  }
  
  public void setListener(AccessPointListener paramAccessPointListener)
  {
    this.mAccessPointListener = paramAccessPointListener;
  }
  
  void setRssi(int paramInt)
  {
    this.mRssi = paramInt;
  }
  
  public void setTag(Object paramObject)
  {
    this.mTag = paramObject;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("AccessPoint(").append(this.ssid);
    if (isSaved()) {
      localStringBuilder.append(',').append("saved");
    }
    if (isActive()) {
      localStringBuilder.append(',').append("active");
    }
    if (isEphemeral()) {
      localStringBuilder.append(',').append("ephemeral");
    }
    if (isConnectable()) {
      localStringBuilder.append(',').append("connectable");
    }
    if (this.security != 0) {
      localStringBuilder.append(',').append(securityToString(this.security, this.pskType));
    }
    return ')';
  }
  
  void update(WifiConfiguration paramWifiConfiguration)
  {
    this.mConfig = paramWifiConfiguration;
    this.networkId = paramWifiConfiguration.networkId;
    if (this.mAccessPointListener != null) {
      this.mAccessPointListener.onAccessPointChanged(this);
    }
  }
  
  boolean update(ScanResult paramScanResult)
  {
    if (matches(paramScanResult))
    {
      this.mScanResultCache.get(paramScanResult.BSSID);
      this.mScanResultCache.put(paramScanResult.BSSID, paramScanResult);
      int k = getLevel();
      int j = getRssi();
      int i;
      if (isValiableConnectedBssid()) {
        if (this.bssid.equals(paramScanResult.BSSID)) {
          i = paramScanResult.level;
        }
      }
      for (;;)
      {
        this.mSeen = getSeen();
        this.mRssi = ((i + j) / 2);
        i = WifiManager.calculateSignalLevel(this.mRssi, 5);
        if ((i > 0) && (i != k) && (this.mAccessPointListener != null)) {
          this.mAccessPointListener.onLevelChanged(this);
        }
        if (this.security == 2) {
          this.pskType = getPskType(paramScanResult);
        }
        if (this.mAccessPointListener != null) {
          this.mAccessPointListener.onAccessPointChanged(this);
        }
        return true;
        i = j;
        continue;
        i = paramScanResult.level;
      }
    }
    return false;
  }
  
  boolean update(WifiConfiguration paramWifiConfiguration, WifiInfo paramWifiInfo, NetworkInfo paramNetworkInfo)
  {
    boolean bool2 = false;
    boolean bool1;
    if ((paramWifiInfo != null) && (isInfoForThisAccessPoint(paramWifiConfiguration, paramWifiInfo))) {
      if (this.isCurrentConnected)
      {
        bool1 = false;
        this.mRssi = paramWifiInfo.getRssi();
        this.mInfo = paramWifiInfo;
        this.mNetworkInfo = paramNetworkInfo;
        this.isCurrentConnected = true;
        this.bssid = paramWifiInfo.getBSSID();
        bool2 = bool1;
        if (this.mAccessPointListener != null)
        {
          this.mAccessPointListener.onAccessPointChanged(this);
          bool2 = bool1;
        }
      }
    }
    do
    {
      do
      {
        return bool2;
        bool1 = true;
        break;
      } while (this.mInfo == null);
      bool2 = true;
      if (this.isCurrentConnected) {
        this.isCurrentConnected = false;
      }
    } while (this.mAccessPointListener == null);
    this.mAccessPointListener.onAccessPointChanged(this);
    return true;
  }
  
  public static abstract interface AccessPointListener
  {
    public abstract void onAccessPointChanged(AccessPoint paramAccessPoint);
    
    public abstract void onLevelChanged(AccessPoint paramAccessPoint);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\wifi\AccessPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */