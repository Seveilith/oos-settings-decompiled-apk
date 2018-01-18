package com.android.settings.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.android.settingslib.wifi.AccessPoint;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

public class WifiStatusTest
  extends Activity
{
  private static final String TAG = "WifiStatusTest";
  private TextView mBSSID;
  private TextView mHiddenSSID;
  private TextView mHttpClientTest;
  private String mHttpClientTestResult;
  private TextView mIPAddr;
  private TextView mLinkSpeed;
  private TextView mMACAddr;
  private TextView mNetworkId;
  private TextView mNetworkState;
  View.OnClickListener mPingButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      WifiStatusTest.-wrap9(WifiStatusTest.this);
    }
  };
  private TextView mPingHostname;
  private String mPingHostnameResult;
  private TextView mRSSI;
  private TextView mSSID;
  private TextView mScanList;
  private TextView mSupplicantState;
  private WifiManager mWifiManager;
  private TextView mWifiState;
  private IntentFilter mWifiStateFilter;
  private final BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
        WifiStatusTest.-wrap4(WifiStatusTest.this, paramAnonymousIntent.getIntExtra("wifi_state", 4));
      }
      do
      {
        do
        {
          return;
          if (paramAnonymousIntent.getAction().equals("android.net.wifi.STATE_CHANGE"))
          {
            WifiStatusTest.-wrap0(WifiStatusTest.this, (NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo"));
            return;
          }
          if (paramAnonymousIntent.getAction().equals("android.net.wifi.SCAN_RESULTS"))
          {
            WifiStatusTest.-wrap1(WifiStatusTest.this);
            return;
          }
        } while (paramAnonymousIntent.getAction().equals("android.net.wifi.supplicant.CONNECTION_CHANGE"));
        if (paramAnonymousIntent.getAction().equals("android.net.wifi.supplicant.STATE_CHANGE"))
        {
          WifiStatusTest.-wrap3(WifiStatusTest.this, (SupplicantState)paramAnonymousIntent.getParcelableExtra("newState"), paramAnonymousIntent.hasExtra("supplicantError"), paramAnonymousIntent.getIntExtra("supplicantError", 0));
          return;
        }
        if (paramAnonymousIntent.getAction().equals("android.net.wifi.RSSI_CHANGED"))
        {
          WifiStatusTest.-wrap2(WifiStatusTest.this, paramAnonymousIntent.getIntExtra("newRssi", 0));
          return;
        }
      } while (paramAnonymousIntent.getAction().equals("android.net.wifi.NETWORK_IDS_CHANGED"));
      Log.e("WifiStatusTest", "Received an unknown Wifi Intent");
    }
  };
  private Button pingTestButton;
  private Button updateButton;
  View.OnClickListener updateButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      paramAnonymousView = WifiStatusTest.-get12(WifiStatusTest.this).getConnectionInfo();
      WifiStatusTest.-wrap8(WifiStatusTest.this, WifiStatusTest.-get12(WifiStatusTest.this).getWifiState());
      WifiStatusTest.-get0(WifiStatusTest.this).setText(paramAnonymousView.getBSSID());
      WifiStatusTest.-get1(WifiStatusTest.this).setText(String.valueOf(paramAnonymousView.getHiddenSSID()));
      int i = paramAnonymousView.getIpAddress();
      StringBuffer localStringBuffer1 = new StringBuffer();
      StringBuffer localStringBuffer2 = localStringBuffer1.append(i & 0xFF).append('.');
      i >>>= 8;
      localStringBuffer2 = localStringBuffer2.append(i & 0xFF).append('.');
      i >>>= 8;
      localStringBuffer2.append(i & 0xFF).append('.').append(i >>> 8 & 0xFF);
      WifiStatusTest.-get4(WifiStatusTest.this).setText(localStringBuffer1);
      WifiStatusTest.-get5(WifiStatusTest.this).setText(String.valueOf(paramAnonymousView.getLinkSpeed()) + " Mbps");
      WifiStatusTest.-get6(WifiStatusTest.this).setText(paramAnonymousView.getMacAddress());
      WifiStatusTest.-get7(WifiStatusTest.this).setText(String.valueOf(paramAnonymousView.getNetworkId()));
      WifiStatusTest.-get10(WifiStatusTest.this).setText(String.valueOf(paramAnonymousView.getRssi()));
      WifiStatusTest.-get11(WifiStatusTest.this).setText(paramAnonymousView.getSSID());
      paramAnonymousView = paramAnonymousView.getSupplicantState();
      WifiStatusTest.-wrap7(WifiStatusTest.this, paramAnonymousView);
    }
  };
  
  private void handleNetworkStateChanged(NetworkInfo paramNetworkInfo)
  {
    String str;
    if (this.mWifiManager.isWifiEnabled())
    {
      WifiInfo localWifiInfo = this.mWifiManager.getConnectionInfo();
      str = localWifiInfo.getSSID();
      paramNetworkInfo = paramNetworkInfo.getDetailedState();
      if (localWifiInfo.getNetworkId() != -1) {
        break label58;
      }
    }
    label58:
    for (boolean bool = true;; bool = false)
    {
      paramNetworkInfo = AccessPoint.getSummary(this, str, paramNetworkInfo, bool, null);
      this.mNetworkState.setText(paramNetworkInfo);
      return;
    }
  }
  
  private void handleScanResultsAvailable()
  {
    List localList = this.mWifiManager.getScanResults();
    StringBuffer localStringBuffer = new StringBuffer();
    if (localList != null)
    {
      int i = localList.size() - 1;
      if (i >= 0)
      {
        ScanResult localScanResult = (ScanResult)localList.get(i);
        if (localScanResult == null) {}
        for (;;)
        {
          i -= 1;
          break;
          if (!TextUtils.isEmpty(localScanResult.SSID)) {
            localStringBuffer.append(localScanResult.SSID + " ");
          }
        }
      }
    }
    this.mScanList.setText(localStringBuffer);
  }
  
  private void handleSignalChanged(int paramInt)
  {
    this.mRSSI.setText(String.valueOf(paramInt));
  }
  
  private void handleSupplicantStateChanged(SupplicantState paramSupplicantState, boolean paramBoolean, int paramInt)
  {
    if (paramBoolean)
    {
      this.mSupplicantState.setText("ERROR AUTHENTICATING");
      return;
    }
    setSupplicantStateText(paramSupplicantState);
  }
  
  private void handleWifiStateChanged(int paramInt)
  {
    setWifiStateText(paramInt);
  }
  
  private void httpClientTest()
  {
    Object localObject3 = null;
    localObject1 = null;
    for (;;)
    {
      try
      {
        localHttpURLConnection = (HttpURLConnection)new URL("https://www.google.com").openConnection();
        localObject1 = localHttpURLConnection;
        localObject3 = localHttpURLConnection;
        if (localHttpURLConnection.getResponseCode() == 200)
        {
          localObject1 = localHttpURLConnection;
          localObject3 = localHttpURLConnection;
          this.mHttpClientTestResult = "Pass";
          if (localHttpURLConnection != null) {
            localHttpURLConnection.disconnect();
          }
          return;
        }
      }
      catch (IOException localIOException)
      {
        HttpURLConnection localHttpURLConnection;
        localObject4 = localObject1;
        this.mHttpClientTestResult = "Fail: IOException";
        return;
      }
      finally
      {
        Object localObject4;
        if (localObject4 == null) {
          continue;
        }
        ((HttpURLConnection)localObject4).disconnect();
      }
      localObject1 = localHttpURLConnection;
      localObject3 = localHttpURLConnection;
      this.mHttpClientTestResult = ("Fail: Code: " + localHttpURLConnection.getResponseMessage());
    }
  }
  
  private final void pingHostname()
  {
    try
    {
      if (Runtime.getRuntime().exec("ping -c 1 -w 100 www.google.com").waitFor() == 0)
      {
        this.mPingHostnameResult = "Pass";
        return;
      }
      this.mPingHostnameResult = "Fail: Host unreachable";
      return;
    }
    catch (UnknownHostException localUnknownHostException)
    {
      this.mPingHostnameResult = "Fail: Unknown Host";
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      this.mPingHostnameResult = "Fail: InterruptedException";
      return;
    }
    catch (IOException localIOException)
    {
      this.mPingHostnameResult = "Fail: IOException";
    }
  }
  
  private void setSupplicantStateText(SupplicantState paramSupplicantState)
  {
    if (SupplicantState.FOUR_WAY_HANDSHAKE.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("FOUR WAY HANDSHAKE");
      return;
    }
    if (SupplicantState.ASSOCIATED.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("ASSOCIATED");
      return;
    }
    if (SupplicantState.ASSOCIATING.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("ASSOCIATING");
      return;
    }
    if (SupplicantState.COMPLETED.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("COMPLETED");
      return;
    }
    if (SupplicantState.DISCONNECTED.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("DISCONNECTED");
      return;
    }
    if (SupplicantState.DORMANT.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("DORMANT");
      return;
    }
    if (SupplicantState.GROUP_HANDSHAKE.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("GROUP HANDSHAKE");
      return;
    }
    if (SupplicantState.INACTIVE.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("INACTIVE");
      return;
    }
    if (SupplicantState.INVALID.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("INVALID");
      return;
    }
    if (SupplicantState.SCANNING.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("SCANNING");
      return;
    }
    if (SupplicantState.UNINITIALIZED.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("UNINITIALIZED");
      return;
    }
    this.mSupplicantState.setText("BAD");
    Log.e("WifiStatusTest", "supplicant state is bad");
  }
  
  private void setWifiStateText(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = "BAD";
      Log.e("WifiStatusTest", "wifi state is bad");
    }
    for (;;)
    {
      this.mWifiState.setText(str);
      return;
      str = getString(2131691528);
      continue;
      str = getString(2131691529);
      continue;
      str = getString(2131691530);
      continue;
      str = getString(2131691531);
      continue;
      str = getString(2131691532);
    }
  }
  
  private final void updatePingState()
  {
    final Handler localHandler = new Handler();
    this.mPingHostnameResult = getResources().getString(2131690818);
    this.mHttpClientTestResult = getResources().getString(2131690818);
    this.mPingHostname.setText(this.mPingHostnameResult);
    this.mHttpClientTest.setText(this.mHttpClientTestResult);
    final Runnable local4 = new Runnable()
    {
      public void run()
      {
        WifiStatusTest.-get8(WifiStatusTest.this).setText(WifiStatusTest.-get9(WifiStatusTest.this));
        WifiStatusTest.-get2(WifiStatusTest.this).setText(WifiStatusTest.-get3(WifiStatusTest.this));
      }
    };
    new Thread()
    {
      public void run()
      {
        WifiStatusTest.-wrap6(WifiStatusTest.this);
        localHandler.post(local4);
      }
    }.start();
    new Thread()
    {
      public void run()
      {
        WifiStatusTest.-wrap5(WifiStatusTest.this);
        localHandler.post(local4);
      }
    }.start();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    this.mWifiStateFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
    this.mWifiStateFilter.addAction("android.net.wifi.STATE_CHANGE");
    this.mWifiStateFilter.addAction("android.net.wifi.SCAN_RESULTS");
    this.mWifiStateFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
    this.mWifiStateFilter.addAction("android.net.wifi.RSSI_CHANGED");
    this.mWifiStateFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
    registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
    setContentView(2130969122);
    this.updateButton = ((Button)findViewById(2131362799));
    this.updateButton.setOnClickListener(this.updateButtonHandler);
    this.mWifiState = ((TextView)findViewById(2131362800));
    this.mNetworkState = ((TextView)findViewById(2131362801));
    this.mSupplicantState = ((TextView)findViewById(2131362802));
    this.mRSSI = ((TextView)findViewById(2131362803));
    this.mBSSID = ((TextView)findViewById(2131362804));
    this.mSSID = ((TextView)findViewById(2131362720));
    this.mHiddenSSID = ((TextView)findViewById(2131362805));
    this.mIPAddr = ((TextView)findViewById(2131362806));
    this.mMACAddr = ((TextView)findViewById(2131362807));
    this.mNetworkId = ((TextView)findViewById(2131362808));
    this.mLinkSpeed = ((TextView)findViewById(2131362809));
    this.mScanList = ((TextView)findViewById(2131362810));
    this.mPingHostname = ((TextView)findViewById(2131362811));
    this.mHttpClientTest = ((TextView)findViewById(2131362482));
    this.pingTestButton = ((Button)findViewById(2131362479));
    this.pingTestButton.setOnClickListener(this.mPingButtonHandler);
  }
  
  protected void onPause()
  {
    super.onPause();
    unregisterReceiver(this.mWifiStateReceiver);
  }
  
  protected void onResume()
  {
    super.onResume();
    registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\WifiStatusTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */