package com.android.settingslib.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Global;
import android.widget.Toast;
import com.android.settingslib.R.string;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiTracker
{
  private static final boolean DBG = false;
  private static final int NUM_SCANS_TO_CONFIRM_AP_LOSS = 3;
  private static final String TAG = "WifiTracker";
  private static final int WIFI_RESCAN_INTERVAL_MS = 10000;
  public static int sVerboseLogging = 0;
  private ArrayList<AccessPoint> mAccessPoints = new ArrayList();
  private final AtomicBoolean mConnected = new AtomicBoolean(false);
  private final ConnectivityManager mConnectivityManager;
  private final Context mContext;
  private final IntentFilter mFilter;
  private final boolean mIncludePasspoints;
  private final boolean mIncludeSaved;
  private final boolean mIncludeScans;
  private WifiInfo mLastInfo;
  private NetworkInfo mLastNetworkInfo;
  private final WifiListener mListener;
  private final MainHandler mMainHandler;
  private WifiTrackerNetworkCallback mNetworkCallback;
  private final NetworkRequest mNetworkRequest;
  final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if ("android.net.wifi.WIFI_STATE_CHANGED".equals(str)) {
        WifiTracker.-wrap3(WifiTracker.this, paramAnonymousIntent.getIntExtra("wifi_state", 4));
      }
      do
      {
        return;
        if (("android.net.wifi.SCAN_RESULTS".equals(str)) || ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(str)) || ("android.net.wifi.LINK_CONFIGURATION_CHANGED".equals(str)))
        {
          WifiTracker.-get5(WifiTracker.this).sendEmptyMessage(0);
          return;
        }
        if ("android.net.wifi.STATE_CHANGE".equals(str))
        {
          paramAnonymousContext = (NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo");
          WifiTracker.-get0(WifiTracker.this).set(paramAnonymousContext.isConnected());
          WifiTracker.-get3(WifiTracker.this).sendEmptyMessage(0);
          WifiTracker.-get5(WifiTracker.this).sendEmptyMessage(0);
          WifiTracker.-get5(WifiTracker.this).obtainMessage(1, paramAnonymousContext).sendToTarget();
          return;
        }
      } while (!"Auth_password_wrong".equals(str));
      Toast.makeText(paramAnonymousContext, R.string.wifi_auth_password_wrong, 0).show();
    }
  };
  private boolean mRegistered;
  private boolean mSavedNetworksExist;
  private Integer mScanId = Integer.valueOf(0);
  private HashMap<String, ScanResult> mScanResultCache = new HashMap();
  Scanner mScanner;
  private HashMap<String, Integer> mSeenBssids = new HashMap();
  private final WifiManager mWifiManager;
  private final WorkHandler mWorkHandler;
  
  public WifiTracker(Context paramContext, WifiListener paramWifiListener, Looper paramLooper, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramContext, paramWifiListener, paramLooper, paramBoolean1, paramBoolean2, false);
  }
  
  public WifiTracker(Context paramContext, WifiListener paramWifiListener, Looper paramLooper, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this(paramContext, paramWifiListener, paramLooper, paramBoolean1, paramBoolean2, paramBoolean3, (WifiManager)paramContext.getSystemService(WifiManager.class), (ConnectivityManager)paramContext.getSystemService(ConnectivityManager.class), Looper.myLooper());
  }
  
  WifiTracker(Context paramContext, WifiListener paramWifiListener, Looper paramLooper1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, WifiManager paramWifiManager, ConnectivityManager paramConnectivityManager, Looper paramLooper2)
  {
    if ((paramBoolean1) || (paramBoolean2))
    {
      this.mContext = paramContext;
      paramContext = paramLooper2;
      if (paramLooper2 == null) {
        paramContext = Looper.getMainLooper();
      }
      this.mMainHandler = new MainHandler(paramContext);
      if (paramLooper1 == null) {
        break label287;
      }
    }
    for (;;)
    {
      this.mWorkHandler = new WorkHandler(paramLooper1);
      this.mWifiManager = paramWifiManager;
      this.mIncludeSaved = paramBoolean1;
      this.mIncludeScans = paramBoolean2;
      this.mIncludePasspoints = paramBoolean3;
      this.mListener = paramWifiListener;
      this.mConnectivityManager = paramConnectivityManager;
      sVerboseLogging = this.mWifiManager.getVerboseLoggingLevel();
      this.mFilter = new IntentFilter();
      this.mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
      this.mFilter.addAction("android.net.wifi.SCAN_RESULTS");
      this.mFilter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
      this.mFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
      this.mFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
      this.mFilter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
      this.mFilter.addAction("android.net.wifi.STATE_CHANGE");
      this.mFilter.addAction("Auth_password_wrong");
      this.mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addTransportType(1).build();
      return;
      throw new IllegalArgumentException("Must include either saved or scans");
      label287:
      paramLooper1 = paramContext;
    }
  }
  
  public WifiTracker(Context paramContext, WifiListener paramWifiListener, boolean paramBoolean1, boolean paramBoolean2)
  {
    this(paramContext, paramWifiListener, null, paramBoolean1, paramBoolean2);
  }
  
  public WifiTracker(Context paramContext, WifiListener paramWifiListener, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this(paramContext, paramWifiListener, null, paramBoolean1, paramBoolean2, paramBoolean3);
  }
  
  private Collection<ScanResult> fetchScanResults()
  {
    this.mScanId = Integer.valueOf(this.mScanId.intValue() + 1);
    Iterator localIterator = this.mWifiManager.getScanResults().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (ScanResult)localIterator.next();
      if ((((ScanResult)localObject).SSID != null) && (!((ScanResult)localObject).SSID.isEmpty()))
      {
        this.mScanResultCache.put(((ScanResult)localObject).BSSID, localObject);
        this.mSeenBssids.put(((ScanResult)localObject).BSSID, this.mScanId);
      }
    }
    if (this.mScanId.intValue() > 3)
    {
      int i = this.mScanId.intValue();
      localIterator = this.mSeenBssids.entrySet().iterator();
      while (localIterator.hasNext())
      {
        localObject = (Map.Entry)localIterator.next();
        if (((Integer)((Map.Entry)localObject).getValue()).intValue() < Integer.valueOf(i - 3).intValue())
        {
          ScanResult localScanResult = (ScanResult)this.mScanResultCache.get(((Map.Entry)localObject).getKey());
          this.mScanResultCache.remove(((Map.Entry)localObject).getKey());
          localIterator.remove();
        }
      }
    }
    return this.mScanResultCache.values();
  }
  
  private AccessPoint getCachedOrCreate(ScanResult paramScanResult, List<AccessPoint> paramList)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      if (((AccessPoint)paramList.get(i)).matches(paramScanResult))
      {
        paramList = (AccessPoint)paramList.remove(i);
        paramList.update(paramScanResult);
        return paramList;
      }
      i += 1;
    }
    return new AccessPoint(this.mContext, paramScanResult);
  }
  
  private AccessPoint getCachedOrCreate(WifiConfiguration paramWifiConfiguration, List<AccessPoint> paramList)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      if (((AccessPoint)paramList.get(i)).matches(paramWifiConfiguration))
      {
        paramList = (AccessPoint)paramList.remove(i);
        paramList.loadConfig(paramWifiConfiguration);
        return paramList;
      }
      i += 1;
    }
    return new AccessPoint(this.mContext, paramWifiConfiguration);
  }
  
  public static List<AccessPoint> getCurrentAccessPoints(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    paramContext = new WifiTracker(paramContext, null, null, paramBoolean1, paramBoolean2, paramBoolean3);
    paramContext.forceUpdate();
    return paramContext.getAccessPoints();
  }
  
  private WifiConfiguration getWifiConfigurationForNetworkId(int paramInt)
  {
    try
    {
      Object localObject = this.mWifiManager.getConfiguredNetworks();
      if (localObject != null)
      {
        localObject = ((Iterable)localObject).iterator();
        while (((Iterator)localObject).hasNext())
        {
          WifiConfiguration localWifiConfiguration = (WifiConfiguration)((Iterator)localObject).next();
          if ((this.mLastInfo != null) && (paramInt == localWifiConfiguration.networkId)) {
            if (localWifiConfiguration.selfAdded)
            {
              int i = localWifiConfiguration.numAssociation;
              if (i == 0) {
                break;
              }
            }
            else
            {
              return localWifiConfiguration;
            }
          }
        }
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  private void handleResume()
  {
    this.mScanResultCache.clear();
    this.mSeenBssids.clear();
    this.mScanId = Integer.valueOf(0);
  }
  
  private boolean isScanAlwaysAvailable()
  {
    return Settings.Global.getInt(this.mContext.getContentResolver(), "wifi_scan_always_enabled", 0) == 1;
  }
  
  private void updateAccessPoints()
  {
    Object localObject2 = getAccessPoints();
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = ((Iterable)localObject2).iterator();
    while (((Iterator)localObject1).hasNext()) {
      ((AccessPoint)((Iterator)localObject1).next()).clearConfig();
    }
    Object localObject3 = new Multimap(null);
    localObject1 = null;
    if (this.mLastInfo != null) {
      localObject1 = getWifiConfigurationForNetworkId(this.mLastInfo.getNetworkId());
    }
    Object localObject4 = fetchScanResults();
    try
    {
      localObject5 = this.mWifiManager.getConfiguredNetworks();
      if (localObject5 == null) {
        break label337;
      }
      if (((List)localObject5).size() == 0) {
        break label643;
      }
      bool = true;
    }
    catch (Exception localException)
    {
      Object localObject5;
      boolean bool;
      Object localObject6;
      Object localObject7;
      int j;
      int i;
      while (localObject4 != null)
      {
        localObject4 = ((Iterable)localObject4).iterator();
        while (((Iterator)localObject4).hasNext())
        {
          ScanResult localScanResult = (ScanResult)((Iterator)localObject4).next();
          if ((localScanResult.SSID != null) && (localScanResult.SSID.length() != 0) && (!localScanResult.capabilities.contains("[IBSS]")))
          {
            j = 0;
            localObject6 = ((Multimap)localObject3).getAll(localScanResult.SSID).iterator();
            do
            {
              i = j;
              if (!((Iterator)localObject6).hasNext()) {
                break;
              }
              localObject7 = (AccessPoint)((Iterator)localObject6).next();
            } while (!((AccessPoint)localObject7).update(localScanResult));
            ((AccessPoint)localObject7).foundInScanResult = true;
            i = 1;
            if ((i == 0) && (this.mIncludeScans))
            {
              localObject6 = getCachedOrCreate(localScanResult, (List)localObject2);
              if ((this.mLastInfo != null) && (this.mLastNetworkInfo != null)) {
                ((AccessPoint)localObject6).update((WifiConfiguration)localObject1, this.mLastInfo, this.mLastNetworkInfo);
              }
              if (localScanResult.isPasspointNetwork())
              {
                localObject7 = this.mWifiManager.getMatchingWifiConfig(localScanResult);
                if ((localObject7 != null) && (((WifiConfiguration)localObject7).SSID.equals(localScanResult.SSID))) {
                  ((AccessPoint)localObject6).update((WifiConfiguration)localObject7);
                }
              }
              if ((this.mLastInfo != null) && (this.mLastInfo.getBSSID() != null) && (this.mLastInfo.getBSSID().equals(localScanResult.BSSID)) && (localObject1 != null) && (((WifiConfiguration)localObject1).isPasspoint())) {
                ((AccessPoint)localObject6).update((WifiConfiguration)localObject1);
              }
              localArrayList.add(localObject6);
              ((Multimap)localObject3).put(((AccessPoint)localObject6).getSsidStr(), localObject6);
            }
          }
        }
        bool = false;
        continue;
        ((List)localObject2).add(localObject7);
      }
      Collections.sort(localArrayList);
      localObject1 = this.mAccessPoints.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (AccessPoint)((Iterator)localObject1).next();
        if (((AccessPoint)localObject2).getSsid() != null)
        {
          localObject2 = ((AccessPoint)localObject2).getSsidStr();
          j = 0;
          localObject3 = localArrayList.iterator();
          do
          {
            i = j;
            if (!((Iterator)localObject3).hasNext()) {
              break;
            }
            localObject4 = (AccessPoint)((Iterator)localObject3).next();
          } while ((((AccessPoint)localObject4).getSsid() == null) || (!((AccessPoint)localObject4).getSsid().equals(localObject2)));
          i = 1;
          if (i == 0) {}
        }
      }
      this.mAccessPoints = localArrayList;
      this.mMainHandler.sendEmptyMessage(2);
    }
    this.mSavedNetworksExist = bool;
    localObject5 = ((Iterable)localObject5).iterator();
    while (((Iterator)localObject5).hasNext())
    {
      localObject6 = (WifiConfiguration)((Iterator)localObject5).next();
      if ((!((WifiConfiguration)localObject6).selfAdded) || (((WifiConfiguration)localObject6).numAssociation != 0))
      {
        localObject7 = getCachedOrCreate((WifiConfiguration)localObject6, (List)localObject2);
        ((AccessPoint)localObject7).foundInScanResult = false;
        if ((this.mLastInfo != null) && (this.mLastNetworkInfo != null) && (!((WifiConfiguration)localObject6).isPasspoint())) {
          ((AccessPoint)localObject7).update((WifiConfiguration)localObject1, this.mLastInfo, this.mLastNetworkInfo);
        }
        if (!this.mIncludeSaved) {
          break label648;
        }
        if ((!((WifiConfiguration)localObject6).isPasspoint()) || (this.mIncludePasspoints))
        {
          j = 0;
          Iterator localIterator = ((Iterable)localObject4).iterator();
          do
          {
            i = j;
            if (!localIterator.hasNext()) {
              break;
            }
          } while (!((ScanResult)localIterator.next()).SSID.equals(((AccessPoint)localObject7).getSsidStr()));
          i = 1;
          if (i == 0) {
            ((AccessPoint)localObject7).setRssi(Integer.MAX_VALUE);
          }
          localArrayList.add(localObject7);
        }
        if (!((WifiConfiguration)localObject6).isPasspoint()) {
          ((Multimap)localObject3).put(((AccessPoint)localObject7).getSsidStr(), localObject7);
        }
      }
    }
    label337:
    label643:
    label648:
    return;
  }
  
  private void updateNetworkInfo(NetworkInfo arg1)
  {
    if (!this.mWifiManager.isWifiEnabled())
    {
      this.mMainHandler.sendEmptyMessage(4);
      return;
    }
    if ((??? != null) && (???.getDetailedState() == NetworkInfo.DetailedState.OBTAINING_IPADDR)) {
      this.mMainHandler.sendEmptyMessage(4);
    }
    int j;
    for (;;)
    {
      if (??? != null) {
        this.mLastNetworkInfo = ???;
      }
      ??? = null;
      this.mLastInfo = this.mWifiManager.getConnectionInfo();
      if (this.mLastInfo != null) {
        ??? = getWifiConfigurationForNetworkId(this.mLastInfo.getNetworkId());
      }
      j = 0;
      int i = this.mAccessPoints.size() - 1;
      while (i >= 0)
      {
        if (((AccessPoint)this.mAccessPoints.get(i)).update(???, this.mLastInfo, this.mLastNetworkInfo)) {
          j = 1;
        }
        i -= 1;
      }
      this.mMainHandler.sendEmptyMessage(3);
    }
    if (j != 0) {}
    synchronized (this.mAccessPoints)
    {
      Collections.sort(this.mAccessPoints);
      this.mMainHandler.sendEmptyMessage(2);
      return;
    }
  }
  
  private void updateWifiState(int paramInt)
  {
    this.mWorkHandler.obtainMessage(3, paramInt, 0).sendToTarget();
  }
  
  public boolean doSavedNetworksExist()
  {
    return this.mSavedNetworksExist;
  }
  
  public void dump(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("  - wifi tracker ------");
    Iterator localIterator = getAccessPoints().iterator();
    while (localIterator.hasNext())
    {
      AccessPoint localAccessPoint = (AccessPoint)localIterator.next();
      paramPrintWriter.println("  " + localAccessPoint);
    }
  }
  
  public void forceScan()
  {
    if ((this.mWifiManager.isWifiEnabled()) && (this.mScanner != null)) {
      this.mScanner.forceScan();
    }
  }
  
  public void forceUpdate()
  {
    updateAccessPoints();
  }
  
  public List<AccessPoint> getAccessPoints()
  {
    synchronized (this.mAccessPoints)
    {
      ArrayList localArrayList2 = new ArrayList(this.mAccessPoints);
      return localArrayList2;
    }
  }
  
  public WifiManager getManager()
  {
    return this.mWifiManager;
  }
  
  public boolean isConnected()
  {
    return this.mConnected.get();
  }
  
  public boolean isWifiEnabled()
  {
    return this.mWifiManager.isWifiEnabled();
  }
  
  public void pauseScanning()
  {
    if (this.mScanner != null)
    {
      this.mScanner.pause();
      this.mScanner = null;
    }
  }
  
  public void resumeScanning()
  {
    if (this.mScanner == null) {
      this.mScanner = new Scanner();
    }
    this.mWorkHandler.sendEmptyMessage(2);
    if (this.mWifiManager.isWifiEnabled()) {
      this.mScanner.resume();
    }
    for (;;)
    {
      this.mWorkHandler.sendEmptyMessage(0);
      return;
      if (!isScanAlwaysAvailable()) {
        this.mAccessPoints.clear();
      }
    }
  }
  
  public void startTracking()
  {
    resumeScanning();
    if (!this.mRegistered)
    {
      this.mContext.registerReceiver(this.mReceiver, this.mFilter);
      this.mNetworkCallback = new WifiTrackerNetworkCallback(null);
      this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback);
      this.mRegistered = true;
    }
  }
  
  public void stopTracking()
  {
    if (this.mRegistered)
    {
      this.mWorkHandler.removeMessages(0);
      this.mWorkHandler.removeMessages(1);
      this.mContext.unregisterReceiver(this.mReceiver);
      this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
      this.mRegistered = false;
    }
    pauseScanning();
  }
  
  private final class MainHandler
    extends Handler
  {
    private static final int MSG_ACCESS_POINT_CHANGED = 2;
    private static final int MSG_CONNECTED_CHANGED = 0;
    private static final int MSG_PAUSE_SCANNING = 4;
    private static final int MSG_RESUME_SCANNING = 3;
    private static final int MSG_WIFI_STATE_CHANGED = 1;
    
    public MainHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (WifiTracker.-get2(WifiTracker.this) == null) {
        return;
      }
      switch (paramMessage.what)
      {
      }
      do
      {
        do
        {
          return;
          WifiTracker.-get2(WifiTracker.this).onConnectedChanged();
          return;
          WifiTracker.-get2(WifiTracker.this).onWifiStateChanged(paramMessage.arg1);
          return;
          WifiTracker.-get2(WifiTracker.this).onAccessPointsChanged();
          return;
        } while (WifiTracker.this.mScanner == null);
        WifiTracker.this.mScanner.resume();
        return;
      } while (WifiTracker.this.mScanner == null);
      WifiTracker.this.mScanner.pause();
    }
  }
  
  private static class Multimap<K, V>
  {
    private final HashMap<K, List<V>> store = new HashMap();
    
    List<V> getAll(K paramK)
    {
      paramK = (List)this.store.get(paramK);
      if (paramK != null) {
        return paramK;
      }
      return Collections.emptyList();
    }
    
    void put(K paramK, V paramV)
    {
      List localList = (List)this.store.get(paramK);
      Object localObject = localList;
      if (localList == null)
      {
        localObject = new ArrayList(3);
        this.store.put(paramK, localObject);
      }
      ((List)localObject).add(paramV);
    }
  }
  
  class Scanner
    extends Handler
  {
    static final int MSG_SCAN = 0;
    private int mRetry = 0;
    
    Scanner() {}
    
    void forceScan()
    {
      removeMessages(0);
      sendEmptyMessage(0);
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (paramMessage.what != 0) {
        return;
      }
      if (WifiTracker.-get4(WifiTracker.this).startScan()) {
        this.mRetry = 0;
      }
      int i;
      do
      {
        sendEmptyMessageDelayed(0, 10000L);
        return;
        i = this.mRetry + 1;
        this.mRetry = i;
      } while (i < 3);
      this.mRetry = 0;
      if (WifiTracker.-get1(WifiTracker.this) != null) {
        Toast.makeText(WifiTracker.-get1(WifiTracker.this), R.string.wifi_fail_to_scan, 1).show();
      }
    }
    
    boolean isScanning()
    {
      return hasMessages(0);
    }
    
    void pause()
    {
      this.mRetry = 0;
      removeMessages(0);
    }
    
    void resume()
    {
      if (!hasMessages(0)) {
        sendEmptyMessage(0);
      }
    }
  }
  
  public static abstract interface WifiListener
  {
    public abstract void onAccessPointsChanged();
    
    public abstract void onConnectedChanged();
    
    public abstract void onWifiStateChanged(int paramInt);
  }
  
  private final class WifiTrackerNetworkCallback
    extends ConnectivityManager.NetworkCallback
  {
    private WifiTrackerNetworkCallback() {}
    
    public void onCapabilitiesChanged(Network paramNetwork, NetworkCapabilities paramNetworkCapabilities)
    {
      if (paramNetwork.equals(WifiTracker.-get4(WifiTracker.this).getCurrentNetwork())) {
        WifiTracker.-get5(WifiTracker.this).sendEmptyMessage(1);
      }
    }
  }
  
  private final class WorkHandler
    extends Handler
  {
    private static final int MSG_RESUME = 2;
    private static final int MSG_UPDATE_ACCESS_POINTS = 0;
    private static final int MSG_UPDATE_NETWORK_INFO = 1;
    private static final int MSG_UPDATE_WIFI_STATE = 3;
    
    public WorkHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 0: 
        WifiTracker.-wrap1(WifiTracker.this);
        return;
      case 1: 
        WifiTracker.-wrap2(WifiTracker.this, (NetworkInfo)paramMessage.obj);
        return;
      case 2: 
        WifiTracker.-wrap0(WifiTracker.this);
        return;
      }
      if (paramMessage.arg1 == 3) {
        if (WifiTracker.this.mScanner != null) {
          WifiTracker.this.mScanner.resume();
        }
      }
      for (;;)
      {
        WifiTracker.-get3(WifiTracker.this).obtainMessage(1, paramMessage.arg1, 0).sendToTarget();
        return;
        WifiTracker.-set0(WifiTracker.this, null);
        WifiTracker.-set1(WifiTracker.this, null);
        if (WifiTracker.this.mScanner != null) {
          WifiTracker.this.mScanner.pause();
        }
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\wifi\WifiTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */