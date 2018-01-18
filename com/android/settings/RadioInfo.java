package com.android.settings;

import android.app.Activity;
import android.app.QueuedWork;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.DataConnectionRealTimeInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.PreciseCallState;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import com.android.ims.ImsConfig;
import com.android.ims.ImsException;
import com.android.ims.ImsManager;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class RadioInfo
  extends Activity
{
  private static final int CELL_INFO_LIST_RATE_DISABLED = Integer.MAX_VALUE;
  private static final int CELL_INFO_LIST_RATE_MAX = 0;
  private static final int EVENT_CFI_CHANGED = 302;
  private static final int EVENT_QUERY_PREFERRED_TYPE_DONE = 1000;
  private static final int EVENT_QUERY_SMSC_DONE = 1005;
  private static final int EVENT_SET_PREFERRED_TYPE_DONE = 1001;
  private static final int EVENT_UPDATE_SMSC_DONE = 1006;
  private static final int MENU_ITEM_GET_PDP_LIST = 4;
  private static final int MENU_ITEM_SELECT_BAND = 0;
  private static final int MENU_ITEM_TOGGLE_DATA = 5;
  private static final int MENU_ITEM_VIEW_ADN = 1;
  private static final int MENU_ITEM_VIEW_FDN = 2;
  private static final int MENU_ITEM_VIEW_SDN = 3;
  private static final String TAG = "RadioInfo";
  private static final String[] mCellInfoRefreshRateLabels = { "Disabled", "Immediate", "Min 5s", "Min 10s", "Min 60s" };
  private static final int[] mCellInfoRefreshRates = { Integer.MAX_VALUE, 0, 5000, 10000, 60000 };
  private static final String[] mPreferredNetworkLabels = { "WCDMA preferred", "GSM only", "WCDMA only", "GSM auto (PRL)", "CDMA auto (PRL)", "CDMA only", "EvDo only", "Global auto (PRL)", "LTE/CDMA auto (PRL)", "LTE/UMTS auto (PRL)", "LTE/CDMA/UMTS auto (PRL)", "LTE only", "LTE/WCDMA", "TD-SCDMA only", "TD-SCDMA/WCDMA", "LTE/TD-SCDMA", "TD-SCDMA/GSM", "TD-SCDMA/UMTS", "LTE/TD-SCDMA/WCDMA", "LTE/TD-SCDMA/UMTS", "TD-SCDMA/CDMA/UMTS", "Global/TD-SCDMA", "Unknown" };
  private TextView callState;
  private Button cellInfoRefreshRateButton;
  private Spinner cellInfoRefreshRateSpinner;
  private TextView dBm;
  private TextView dataNetwork;
  private TextView dnsCheckState;
  private Button dnsCheckToggleButton;
  private TextView gprsState;
  private TextView gsmState;
  private Switch imsVoLteProvisionedSwitch;
  private TextView mCellInfo;
  AdapterView.OnItemSelectedListener mCellInfoRefreshRateHandler = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      RadioInfo.-set0(RadioInfo.this, paramAnonymousInt);
      RadioInfo.-get15(RadioInfo.this).setCellInfoListRate(RadioInfo.-get0()[paramAnonymousInt]);
      RadioInfo.-wrap5(RadioInfo.this);
    }
    
    public void onNothingSelected(AdapterView paramAnonymousAdapterView) {}
  };
  private int mCellInfoRefreshRateIndex;
  private List<CellInfo> mCellInfoResult = null;
  private CellLocation mCellLocationResult = null;
  private TextView mCfi;
  private boolean mCfiValue = false;
  private TextView mDcRtInfoTv;
  private TextView mDeviceId;
  View.OnClickListener mDnsCheckButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      paramAnonymousView = RadioInfo.-get15(RadioInfo.this);
      if (RadioInfo.-get15(RadioInfo.this).isDnsCheckDisabled()) {}
      for (boolean bool = false;; bool = true)
      {
        paramAnonymousView.disableDnsCheck(bool);
        RadioInfo.-wrap11(RadioInfo.this);
        return;
      }
    }
  };
  private MenuItem.OnMenuItemClickListener mGetPdpList = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      RadioInfo.-get15(RadioInfo.this).getDataCallList(null);
      return true;
    }
  };
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      case 1002: 
      case 1003: 
      case 1004: 
      default: 
        super.handleMessage(paramAnonymousMessage);
      }
      do
      {
        do
        {
          return;
          paramAnonymousMessage = (AsyncResult)paramAnonymousMessage.obj;
          if ((paramAnonymousMessage.exception == null) && (paramAnonymousMessage.result != null))
          {
            RadioInfo.-wrap19(RadioInfo.this, ((int[])paramAnonymousMessage.result)[0]);
            return;
          }
          RadioInfo.-wrap19(RadioInfo.this, RadioInfo.-get12().length - 1);
          return;
        } while (((AsyncResult)paramAnonymousMessage.obj).exception == null);
        RadioInfo.-wrap2(RadioInfo.this, "Set preferred network type failed.");
        return;
        paramAnonymousMessage = (AsyncResult)paramAnonymousMessage.obj;
        if (paramAnonymousMessage.exception != null)
        {
          RadioInfo.-get16(RadioInfo.this).setText("refresh error");
          return;
        }
        RadioInfo.-get16(RadioInfo.this).setText((String)paramAnonymousMessage.result);
        return;
        RadioInfo.-get17(RadioInfo.this).setEnabled(true);
      } while (((AsyncResult)paramAnonymousMessage.obj).exception == null);
      RadioInfo.-get16(RadioInfo.this).setText("update error");
    }
  };
  private TextView mHttpClientTest;
  private String mHttpClientTestResult;
  private ImsManager mImsManager = null;
  CompoundButton.OnCheckedChangeListener mImsVoLteCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      RadioInfo.this.setImsVoLteProvisionedState(paramAnonymousBoolean);
    }
  };
  private TextView mLocation;
  private TextView mMwi;
  private boolean mMwiValue = false;
  private List<NeighboringCellInfo> mNeighboringCellResult = null;
  private TextView mNeighboringCids;
  View.OnClickListener mOemInfoButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      paramAnonymousView = new Intent("com.android.settings.OEM_RADIO_INFO");
      try
      {
        RadioInfo.this.startActivity(paramAnonymousView);
        return;
      }
      catch (ActivityNotFoundException paramAnonymousView)
      {
        RadioInfo.-wrap2(RadioInfo.this, "OEM-specific Info/Settings Activity Not Found : " + paramAnonymousView);
      }
    }
  };
  private final PhoneStateListener mPhoneStateListener = new PhoneStateListener()
  {
    public void onCallForwardingIndicatorChanged(boolean paramAnonymousBoolean)
    {
      RadioInfo.-set3(RadioInfo.this, paramAnonymousBoolean);
      RadioInfo.-wrap6(RadioInfo.this);
    }
    
    public void onCallStateChanged(int paramAnonymousInt, String paramAnonymousString)
    {
      RadioInfo.-wrap16(RadioInfo.this);
      RadioInfo.-wrap17(RadioInfo.this, paramAnonymousInt);
    }
    
    public void onCellInfoChanged(List<CellInfo> paramAnonymousList)
    {
      RadioInfo.-wrap2(RadioInfo.this, "onCellInfoChanged: arrayCi=" + paramAnonymousList);
      RadioInfo.-set1(RadioInfo.this, paramAnonymousList);
      RadioInfo.-wrap7(RadioInfo.this, RadioInfo.-get1(RadioInfo.this));
    }
    
    public void onCellLocationChanged(CellLocation paramAnonymousCellLocation)
    {
      RadioInfo.-wrap13(RadioInfo.this, paramAnonymousCellLocation);
    }
    
    public void onDataActivity(int paramAnonymousInt)
    {
      RadioInfo.-wrap9(RadioInfo.this);
    }
    
    public void onDataConnectionRealTimeInfoChanged(DataConnectionRealTimeInfo paramAnonymousDataConnectionRealTimeInfo)
    {
      RadioInfo.-wrap2(RadioInfo.this, "onDataConnectionRealTimeInfoChanged: dcRtInfo=" + paramAnonymousDataConnectionRealTimeInfo);
      RadioInfo.-wrap10(RadioInfo.this, paramAnonymousDataConnectionRealTimeInfo);
    }
    
    public void onDataConnectionStateChanged(int paramAnonymousInt)
    {
      RadioInfo.-wrap8(RadioInfo.this);
      RadioInfo.-wrap16(RadioInfo.this);
    }
    
    public void onMessageWaitingIndicatorChanged(boolean paramAnonymousBoolean)
    {
      RadioInfo.-set4(RadioInfo.this, paramAnonymousBoolean);
      RadioInfo.-wrap14(RadioInfo.this);
    }
    
    public void onPreciseCallStateChanged(PreciseCallState paramAnonymousPreciseCallState)
    {
      RadioInfo.-wrap16(RadioInfo.this);
    }
    
    public void onServiceStateChanged(ServiceState paramAnonymousServiceState)
    {
      RadioInfo.-wrap2(RadioInfo.this, "onServiceStateChanged: ServiceState=" + paramAnonymousServiceState);
      RadioInfo.-wrap21(RadioInfo.this, paramAnonymousServiceState);
      RadioInfo.-wrap20(RadioInfo.this);
      RadioInfo.-wrap16(RadioInfo.this);
      RadioInfo.-wrap12(RadioInfo.this);
    }
    
    public void onSignalStrengthsChanged(SignalStrength paramAnonymousSignalStrength)
    {
      RadioInfo.-wrap2(RadioInfo.this, "onSignalStrengthChanged: SignalStrength=" + paramAnonymousSignalStrength);
      RadioInfo.-wrap22(RadioInfo.this, paramAnonymousSignalStrength);
    }
  };
  View.OnClickListener mPingButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      RadioInfo.-wrap18(RadioInfo.this);
    }
  };
  private String mPingHostnameResultV4;
  private String mPingHostnameResultV6;
  private TextView mPingHostnameV4;
  private TextView mPingHostnameV6;
  AdapterView.OnItemSelectedListener mPreferredNetworkHandler = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      if ((RadioInfo.-get13(RadioInfo.this) != paramAnonymousInt) && (paramAnonymousInt >= 0) && (paramAnonymousInt <= RadioInfo.-get12().length - 2))
      {
        RadioInfo.-set6(RadioInfo.this, paramAnonymousInt);
        paramAnonymousAdapterView = RadioInfo.-get3(RadioInfo.this).obtainMessage(1001);
        RadioInfo.-get15(RadioInfo.this).setPreferredNetworkType(RadioInfo.-get13(RadioInfo.this), paramAnonymousAdapterView);
      }
    }
    
    public void onNothingSelected(AdapterView paramAnonymousAdapterView) {}
  };
  private int mPreferredNetworkTypeResult;
  CompoundButton.OnCheckedChangeListener mRadioPowerOnChangeListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      RadioInfo localRadioInfo = RadioInfo.this;
      StringBuilder localStringBuilder = new StringBuilder().append("toggle radio power: currently ");
      if (RadioInfo.-wrap0(RadioInfo.this)) {}
      for (paramAnonymousCompoundButton = "on";; paramAnonymousCompoundButton = "off")
      {
        RadioInfo.-wrap2(localRadioInfo, paramAnonymousCompoundButton);
        RadioInfo.-get15(RadioInfo.this).setRadioPower(paramAnonymousBoolean);
        return;
      }
    }
  };
  View.OnClickListener mRefreshSmscButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      RadioInfo.-wrap4(RadioInfo.this);
    }
  };
  private MenuItem.OnMenuItemClickListener mSelectBandCallback = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      paramAnonymousMenuItem = new Intent();
      paramAnonymousMenuItem.setClass(RadioInfo.this, BandMode.class);
      RadioInfo.this.startActivity(paramAnonymousMenuItem);
      return true;
    }
  };
  private TelephonyManager mTelephonyManager;
  private MenuItem.OnMenuItemClickListener mToggleData = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      switch (RadioInfo.-get14(RadioInfo.this).getDataState())
      {
      case 1: 
      default: 
        return true;
      case 2: 
        RadioInfo.-get15(RadioInfo.this).setDataEnabled(false);
        return true;
      }
      RadioInfo.-get15(RadioInfo.this).setDataEnabled(true);
      return true;
    }
  };
  View.OnClickListener mUpdateSmscButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      RadioInfo.-get17(RadioInfo.this).setEnabled(false);
      RadioInfo.-get15(RadioInfo.this).setSmscAddress(RadioInfo.-get16(RadioInfo.this).getText().toString(), RadioInfo.-get3(RadioInfo.this).obtainMessage(1006));
    }
  };
  private MenuItem.OnMenuItemClickListener mViewADNCallback = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      paramAnonymousMenuItem = new Intent("android.intent.action.VIEW");
      try
      {
        paramAnonymousMenuItem.setClassName("com.android.phone", "com.android.phone.SimContacts");
        RadioInfo.this.startActivity(paramAnonymousMenuItem);
        return true;
      }
      catch (ActivityNotFoundException paramAnonymousMenuItem)
      {
        for (;;) {}
      }
    }
  };
  private MenuItem.OnMenuItemClickListener mViewFDNCallback = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      paramAnonymousMenuItem = new Intent("android.intent.action.VIEW");
      try
      {
        paramAnonymousMenuItem.setClassName("com.android.phone", "com.android.phone.settings.oneplus.fdn.OPFdnList");
        RadioInfo.this.startActivity(paramAnonymousMenuItem);
        return true;
      }
      catch (ActivityNotFoundException paramAnonymousMenuItem)
      {
        for (;;) {}
      }
    }
  };
  private MenuItem.OnMenuItemClickListener mViewSDNCallback = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      paramAnonymousMenuItem = new Intent("android.intent.action.VIEW", Uri.parse("content://icc/sdn"));
      try
      {
        paramAnonymousMenuItem.setClassName("com.android.phone", "com.android.phone.ADNList");
        RadioInfo.this.startActivity(paramAnonymousMenuItem);
        return true;
      }
      catch (ActivityNotFoundException paramAnonymousMenuItem)
      {
        for (;;) {}
      }
    }
  };
  private TextView number;
  private Button oemInfoButton;
  private TextView operatorName;
  private Phone phone = null;
  private Button pingTestButton;
  private Spinner preferredNetworkType;
  private Switch radioPowerOnSwitch;
  private TextView received;
  private Button refreshSmscButton;
  private TextView roamingState;
  private TextView sent;
  private EditText smsc;
  private Button updateSmscButton;
  private TextView voiceNetwork;
  
  private final String buildCdmaInfoString(CellInfoCdma paramCellInfoCdma)
  {
    CellIdentityCdma localCellIdentityCdma = paramCellInfoCdma.getCellIdentity();
    CellSignalStrengthCdma localCellSignalStrengthCdma = paramCellInfoCdma.getCellSignalStrength();
    if (paramCellInfoCdma.isRegistered()) {}
    for (paramCellInfoCdma = "S  ";; paramCellInfoCdma = "   ") {
      return String.format("%-3.3s %-5.5s %-5.5s %-5.5s %-6.6s %-6.6s %-6.6s %-6.6s %-5.5s", new Object[] { paramCellInfoCdma, getCellInfoDisplayString(localCellIdentityCdma.getSystemId()), getCellInfoDisplayString(localCellIdentityCdma.getNetworkId()), getCellInfoDisplayString(localCellIdentityCdma.getBasestationId()), getCellInfoDisplayString(localCellSignalStrengthCdma.getCdmaDbm()), getCellInfoDisplayString(localCellSignalStrengthCdma.getCdmaEcio()), getCellInfoDisplayString(localCellSignalStrengthCdma.getEvdoDbm()), getCellInfoDisplayString(localCellSignalStrengthCdma.getEvdoEcio()), getCellInfoDisplayString(localCellSignalStrengthCdma.getEvdoSnr()) });
    }
  }
  
  private final String buildCellInfoString(List<CellInfo> paramList)
  {
    Object localObject = new String();
    StringBuilder localStringBuilder1 = new StringBuilder();
    StringBuilder localStringBuilder2 = new StringBuilder();
    StringBuilder localStringBuilder4 = new StringBuilder();
    StringBuilder localStringBuilder3 = new StringBuilder();
    if (paramList != null)
    {
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        CellInfo localCellInfo = (CellInfo)paramList.next();
        if ((localCellInfo instanceof CellInfoLte)) {
          localStringBuilder4.append(buildLteInfoString((CellInfoLte)localCellInfo));
        } else if ((localCellInfo instanceof CellInfoWcdma)) {
          localStringBuilder3.append(buildWcdmaInfoString((CellInfoWcdma)localCellInfo));
        } else if ((localCellInfo instanceof CellInfoGsm)) {
          localStringBuilder2.append(buildGsmInfoString((CellInfoGsm)localCellInfo));
        } else if ((localCellInfo instanceof CellInfoCdma)) {
          localStringBuilder1.append(buildCdmaInfoString((CellInfoCdma)localCellInfo));
        }
      }
      paramList = (List<CellInfo>)localObject;
      if (localStringBuilder4.length() != 0)
      {
        paramList = (String)localObject + String.format("LTE\n%-3.3s %-3.3s %-3.3s %-5.5s %-5.5s %-3.3s %-6.6s %-4.4s %-4.4s %-2.2s\n", new Object[] { "SRV", "MCC", "MNC", "TAC", "CID", "PCI", "EARFCN", "RSRP", "RSRQ", "TA" });
        paramList = paramList + localStringBuilder4.toString();
      }
      localObject = paramList;
      if (localStringBuilder3.length() != 0)
      {
        paramList = paramList + String.format("WCDMA\n%-3.3s %-3.3s %-3.3s %-5.5s %-5.5s %-6.6s %-3.3s %-4.4s\n", new Object[] { "SRV", "MCC", "MNC", "LAC", "CID", "UARFCN", "PSC", "RSCP" });
        localObject = paramList + localStringBuilder3.toString();
      }
      paramList = (List<CellInfo>)localObject;
      if (localStringBuilder2.length() != 0)
      {
        paramList = (String)localObject + String.format("GSM\n%-3.3s %-3.3s %-3.3s %-5.5s %-5.5s %-6.6s %-4.4s %-4.4s\n", new Object[] { "SRV", "MCC", "MNC", "LAC", "CID", "ARFCN", "BSIC", "RSSI" });
        paramList = paramList + localStringBuilder2.toString();
      }
      localObject = paramList;
      if (localStringBuilder1.length() != 0) {
        paramList = paramList + String.format("CDMA/EVDO\n%-3.3s %-5.5s %-5.5s %-5.5s %-6.6s %-6.6s %-6.6s %-6.6s %-5.5s\n", new Object[] { "SRV", "SID", "NID", "BSID", "C-RSSI", "C-ECIO", "E-RSSI", "E-ECIO", "E-SNR" });
      }
    }
    for (localObject = paramList + localStringBuilder1.toString();; localObject = "unknown") {
      return ((String)localObject).toString();
    }
  }
  
  private final String buildGsmInfoString(CellInfoGsm paramCellInfoGsm)
  {
    CellIdentityGsm localCellIdentityGsm = paramCellInfoGsm.getCellIdentity();
    CellSignalStrengthGsm localCellSignalStrengthGsm = paramCellInfoGsm.getCellSignalStrength();
    if (paramCellInfoGsm.isRegistered()) {}
    for (paramCellInfoGsm = "S  ";; paramCellInfoGsm = "   ") {
      return String.format("%-3.3s %-3.3s %-3.3s %-5.5s %-5.5s %-6.6s %-4.4s %-4.4s\n", new Object[] { paramCellInfoGsm, getCellInfoDisplayString(localCellIdentityGsm.getMcc()), getCellInfoDisplayString(localCellIdentityGsm.getMnc()), getCellInfoDisplayString(localCellIdentityGsm.getLac()), getCellInfoDisplayString(localCellIdentityGsm.getCid()), getCellInfoDisplayString(localCellIdentityGsm.getArfcn()), getCellInfoDisplayString(localCellIdentityGsm.getBsic()), getCellInfoDisplayString(localCellSignalStrengthGsm.getDbm()) });
    }
  }
  
  private final String buildLteInfoString(CellInfoLte paramCellInfoLte)
  {
    CellIdentityLte localCellIdentityLte = paramCellInfoLte.getCellIdentity();
    CellSignalStrengthLte localCellSignalStrengthLte = paramCellInfoLte.getCellSignalStrength();
    if (paramCellInfoLte.isRegistered()) {}
    for (paramCellInfoLte = "S  ";; paramCellInfoLte = "   ") {
      return String.format("%-3.3s %-3.3s %-3.3s %-5.5s %-5.5s %-3.3s %-6.6s %-4.4s %-4.4s %-2.2s\n", new Object[] { paramCellInfoLte, getCellInfoDisplayString(localCellIdentityLte.getMcc()), getCellInfoDisplayString(localCellIdentityLte.getMnc()), getCellInfoDisplayString(localCellIdentityLte.getTac()), getCellInfoDisplayString(localCellIdentityLte.getCi()), getCellInfoDisplayString(localCellIdentityLte.getPci()), getCellInfoDisplayString(localCellIdentityLte.getEarfcn()), getCellInfoDisplayString(localCellSignalStrengthLte.getDbm()), getCellInfoDisplayString(localCellSignalStrengthLte.getRsrq()), getCellInfoDisplayString(localCellSignalStrengthLte.getTimingAdvance()) });
    }
  }
  
  private final String buildWcdmaInfoString(CellInfoWcdma paramCellInfoWcdma)
  {
    CellIdentityWcdma localCellIdentityWcdma = paramCellInfoWcdma.getCellIdentity();
    CellSignalStrengthWcdma localCellSignalStrengthWcdma = paramCellInfoWcdma.getCellSignalStrength();
    if (paramCellInfoWcdma.isRegistered()) {}
    for (paramCellInfoWcdma = "S  ";; paramCellInfoWcdma = "   ") {
      return String.format("%-3.3s %-3.3s %-3.3s %-5.5s %-5.5s %-6.6s %-3.3s %-4.4s\n", new Object[] { paramCellInfoWcdma, getCellInfoDisplayString(localCellIdentityWcdma.getMcc()), getCellInfoDisplayString(localCellIdentityWcdma.getMnc()), getCellInfoDisplayString(localCellIdentityWcdma.getLac()), getCellInfoDisplayString(localCellIdentityWcdma.getCid()), getCellInfoDisplayString(localCellIdentityWcdma.getUarfcn()), getCellInfoDisplayString(localCellIdentityWcdma.getPsc()), getCellInfoDisplayString(localCellSignalStrengthWcdma.getDbm()) });
    }
  }
  
  private final String getCellInfoDisplayString(int paramInt)
  {
    if (paramInt != Integer.MAX_VALUE) {
      return Integer.toString(paramInt);
    }
    return "";
  }
  
  private final String getCellInfoDisplayString(long paramLong)
  {
    if (paramLong != Long.MAX_VALUE) {
      return Long.toString(paramLong);
    }
    return "";
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
  
  private boolean isImsVoLteProvisioned()
  {
    boolean bool = false;
    if ((this.phone != null) && (this.mImsManager != null))
    {
      ImsManager localImsManager = this.mImsManager;
      if (ImsManager.isVolteEnabledByPlatform(this.phone.getContext()))
      {
        localImsManager = this.mImsManager;
        bool = ImsManager.isVolteProvisionedOnDevice(this.phone.getContext());
      }
      return bool;
    }
    return false;
  }
  
  private boolean isRadioOn()
  {
    return this.phone.getServiceState().getState() != 3;
  }
  
  private void log(String paramString)
  {
    Log.d("RadioInfo", paramString);
  }
  
  /* Error */
  private final void pingHostname()
  {
    // Byte code:
    //   0: invokestatic 827	java/lang/Runtime:getRuntime	()Ljava/lang/Runtime;
    //   3: ldc_w 829
    //   6: invokevirtual 833	java/lang/Runtime:exec	(Ljava/lang/String;)Ljava/lang/Process;
    //   9: invokevirtual 838	java/lang/Process:waitFor	()I
    //   12: istore_1
    //   13: iload_1
    //   14: ifne +35 -> 49
    //   17: aload_0
    //   18: ldc_w 778
    //   21: putfield 232	com/android/settings/RadioInfo:mPingHostnameResultV4	Ljava/lang/String;
    //   24: invokestatic 827	java/lang/Runtime:getRuntime	()Ljava/lang/Runtime;
    //   27: ldc_w 840
    //   30: invokevirtual 833	java/lang/Runtime:exec	(Ljava/lang/String;)Ljava/lang/Process;
    //   33: invokevirtual 838	java/lang/Process:waitFor	()I
    //   36: istore_1
    //   37: iload_1
    //   38: ifne +62 -> 100
    //   41: aload_0
    //   42: ldc_w 778
    //   45: putfield 235	com/android/settings/RadioInfo:mPingHostnameResultV6	Ljava/lang/String;
    //   48: return
    //   49: aload_0
    //   50: ldc_w 842
    //   53: iconst_1
    //   54: anewarray 498	java/lang/Object
    //   57: dup
    //   58: iconst_0
    //   59: iload_1
    //   60: invokestatic 846	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   63: aastore
    //   64: invokestatic 535	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   67: putfield 232	com/android/settings/RadioInfo:mPingHostnameResultV4	Ljava/lang/String;
    //   70: goto -46 -> 24
    //   73: astore_2
    //   74: aload_0
    //   75: ldc_w 788
    //   78: putfield 232	com/android/settings/RadioInfo:mPingHostnameResultV4	Ljava/lang/String;
    //   81: goto -57 -> 24
    //   84: astore_2
    //   85: aload_0
    //   86: ldc_w 848
    //   89: putfield 235	com/android/settings/RadioInfo:mPingHostnameResultV6	Ljava/lang/String;
    //   92: aload_0
    //   93: ldc_w 848
    //   96: putfield 232	com/android/settings/RadioInfo:mPingHostnameResultV4	Ljava/lang/String;
    //   99: return
    //   100: aload_0
    //   101: ldc_w 842
    //   104: iconst_1
    //   105: anewarray 498	java/lang/Object
    //   108: dup
    //   109: iconst_0
    //   110: iload_1
    //   111: invokestatic 846	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   114: aastore
    //   115: invokestatic 535	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   118: putfield 235	com/android/settings/RadioInfo:mPingHostnameResultV6	Ljava/lang/String;
    //   121: return
    //   122: astore_2
    //   123: aload_0
    //   124: ldc_w 788
    //   127: putfield 235	com/android/settings/RadioInfo:mPingHostnameResultV6	Ljava/lang/String;
    //   130: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	131	0	this	RadioInfo
    //   12	99	1	i	int
    //   73	1	2	localIOException1	IOException
    //   84	1	2	localInterruptedException	InterruptedException
    //   122	1	2	localIOException2	IOException
    // Exception table:
    //   from	to	target	type
    //   0	13	73	java/io/IOException
    //   17	24	73	java/io/IOException
    //   49	70	73	java/io/IOException
    //   0	13	84	java/lang/InterruptedException
    //   17	24	84	java/lang/InterruptedException
    //   24	37	84	java/lang/InterruptedException
    //   41	48	84	java/lang/InterruptedException
    //   49	70	84	java/lang/InterruptedException
    //   74	81	84	java/lang/InterruptedException
    //   100	121	84	java/lang/InterruptedException
    //   123	130	84	java/lang/InterruptedException
    //   24	37	122	java/io/IOException
    //   41	48	122	java/io/IOException
    //   100	121	122	java/io/IOException
  }
  
  private void refreshSmsc()
  {
    this.phone.getSmscAddress(this.mHandler.obtainMessage(1005));
  }
  
  private void restoreFromBundle(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return;
    }
    this.mPingHostnameResultV4 = paramBundle.getString("mPingHostnameResultV4", "");
    this.mPingHostnameResultV6 = paramBundle.getString("mPingHostnameResultV6", "");
    this.mHttpClientTestResult = paramBundle.getString("mHttpClientTestResult", "");
    this.mPingHostnameV4.setText(this.mPingHostnameResultV4);
    this.mPingHostnameV6.setText(this.mPingHostnameResultV6);
    this.mHttpClientTest.setText(this.mHttpClientTestResult);
    this.mPreferredNetworkTypeResult = paramBundle.getInt("mPreferredNetworkTypeResult", mPreferredNetworkLabels.length - 1);
    this.mCellInfoRefreshRateIndex = paramBundle.getInt("mCellInfoRefreshRateIndex", 0);
  }
  
  private final void updateAllCellInfo()
  {
    this.mCellInfo.setText("");
    this.mNeighboringCids.setText("");
    this.mLocation.setText("");
    new Thread()
    {
      public void run()
      {
        RadioInfo.-wrap15(RadioInfo.this, RadioInfo.-get7(RadioInfo.this));
        RadioInfo.-wrap13(RadioInfo.this, RadioInfo.-get2(RadioInfo.this));
        RadioInfo.-wrap7(RadioInfo.this, RadioInfo.-get1(RadioInfo.this));
      }
    }
    {
      public void run()
      {
        RadioInfo.-set1(RadioInfo.this, RadioInfo.-get14(RadioInfo.this).getAllCellInfo());
        RadioInfo.-set2(RadioInfo.this, RadioInfo.-get14(RadioInfo.this).getCellLocation());
        RadioInfo.-set5(RadioInfo.this, RadioInfo.-get14(RadioInfo.this).getNeighboringCellInfo());
        RadioInfo.-get3(RadioInfo.this).post(this.val$updateAllCellInfoResults);
      }
    }.start();
  }
  
  private final void updateCallRedirect()
  {
    this.mCfi.setText(String.valueOf(this.mCfiValue));
  }
  
  private final void updateCellInfo(List<CellInfo> paramList)
  {
    this.mCellInfo.setText(buildCellInfoString(paramList));
  }
  
  private final void updateDataState()
  {
    int i = this.mTelephonyManager.getDataState();
    Resources localResources = getResources();
    String str = localResources.getString(2131690818);
    switch (i)
    {
    }
    for (;;)
    {
      this.gprsState.setText(str);
      return;
      str = localResources.getString(2131690816);
      continue;
      str = localResources.getString(2131690815);
      continue;
      str = localResources.getString(2131690814);
      continue;
      str = localResources.getString(2131690817);
    }
  }
  
  private final void updateDataStats2()
  {
    Object localObject = getResources();
    long l1 = TrafficStats.getMobileTxPackets();
    long l2 = TrafficStats.getMobileRxPackets();
    long l3 = TrafficStats.getMobileTxBytes();
    long l4 = TrafficStats.getMobileRxBytes();
    String str = ((Resources)localObject).getString(2131690819);
    localObject = ((Resources)localObject).getString(2131690820);
    this.sent.setText(l1 + " " + str + ", " + l3 + " " + (String)localObject);
    this.received.setText(l2 + " " + str + ", " + l4 + " " + (String)localObject);
  }
  
  private final void updateDcRtInfoTv(DataConnectionRealTimeInfo paramDataConnectionRealTimeInfo)
  {
    this.mDcRtInfoTv.setText(paramDataConnectionRealTimeInfo.toString());
  }
  
  private void updateDnsCheckState()
  {
    TextView localTextView = this.dnsCheckState;
    if (this.phone.isDnsCheckDisabled()) {}
    for (String str = "0.0.0.0 allowed";; str = "0.0.0.0 not allowed")
    {
      localTextView.setText(str);
      return;
    }
  }
  
  private void updateImsVoLteProvisionedState()
  {
    log("updateImsVoLteProvisionedState isImsVoLteProvisioned()=" + isImsVoLteProvisioned());
    this.imsVoLteProvisionedSwitch.setOnCheckedChangeListener(null);
    this.imsVoLteProvisionedSwitch.setChecked(isImsVoLteProvisioned());
    this.imsVoLteProvisionedSwitch.setOnCheckedChangeListener(this.mImsVoLteCheckedChangeListener);
  }
  
  private final void updateLocation(CellLocation paramCellLocation)
  {
    Object localObject = getResources();
    int i;
    int j;
    TextView localTextView;
    if ((paramCellLocation instanceof GsmCellLocation))
    {
      paramCellLocation = (GsmCellLocation)paramCellLocation;
      i = paramCellLocation.getLac();
      j = paramCellLocation.getCid();
      localTextView = this.mLocation;
      StringBuilder localStringBuilder = new StringBuilder().append(((Resources)localObject).getString(2131690823)).append(" = ");
      if (i == -1)
      {
        paramCellLocation = "unknown";
        localObject = localStringBuilder.append(paramCellLocation).append("   ").append(((Resources)localObject).getString(2131690824)).append(" = ");
        if (j != -1) {
          break label132;
        }
      }
      label132:
      for (paramCellLocation = "unknown";; paramCellLocation = Integer.toHexString(j))
      {
        localTextView.setText(paramCellLocation);
        return;
        paramCellLocation = Integer.toHexString(i);
        break;
      }
    }
    if ((paramCellLocation instanceof CdmaCellLocation))
    {
      paramCellLocation = (CdmaCellLocation)paramCellLocation;
      i = paramCellLocation.getBaseStationId();
      j = paramCellLocation.getSystemId();
      int k = paramCellLocation.getNetworkId();
      int m = paramCellLocation.getBaseStationLatitude();
      int n = paramCellLocation.getBaseStationLongitude();
      localTextView = this.mLocation;
      localObject = new StringBuilder().append("BID = ");
      if (i == -1)
      {
        paramCellLocation = "unknown";
        localObject = ((StringBuilder)localObject).append(paramCellLocation).append("   ").append("SID = ");
        if (j != -1) {
          break label352;
        }
        paramCellLocation = "unknown";
        label239:
        localObject = ((StringBuilder)localObject).append(paramCellLocation).append("   ").append("NID = ");
        if (k != -1) {
          break label360;
        }
        paramCellLocation = "unknown";
        label269:
        localObject = ((StringBuilder)localObject).append(paramCellLocation).append("\n").append("LAT = ");
        if (m != -1) {
          break label369;
        }
        paramCellLocation = "unknown";
        label299:
        localObject = ((StringBuilder)localObject).append(paramCellLocation).append("   ").append("LONG = ");
        if (n != -1) {
          break label378;
        }
      }
      label352:
      label360:
      label369:
      label378:
      for (paramCellLocation = "unknown";; paramCellLocation = Integer.toHexString(n))
      {
        localTextView.setText(paramCellLocation);
        return;
        paramCellLocation = Integer.toHexString(i);
        break;
        paramCellLocation = Integer.toHexString(j);
        break label239;
        paramCellLocation = Integer.toHexString(k);
        break label269;
        paramCellLocation = Integer.toHexString(m);
        break label299;
      }
    }
    this.mLocation.setText("unknown");
  }
  
  private final void updateMessageWaiting()
  {
    this.mMwi.setText(String.valueOf(this.mMwiValue));
  }
  
  private final void updateNeighboringCids(List<NeighboringCellInfo> paramList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramList != null) {
      if (paramList.isEmpty()) {
        localStringBuilder.append("no neighboring cells");
      }
    }
    for (;;)
    {
      this.mNeighboringCids.setText(localStringBuilder.toString());
      return;
      paramList = paramList.iterator();
      while (paramList.hasNext()) {
        localStringBuilder.append(((NeighboringCellInfo)paramList.next()).toString()).append(" ");
      }
      continue;
      localStringBuilder.append("unknown");
    }
  }
  
  private final void updateNetworkType()
  {
    if (this.phone != null)
    {
      this.phone.getServiceState();
      this.dataNetwork.setText(ServiceState.rilRadioTechnologyToString(this.phone.getServiceState().getRilDataRadioTechnology()));
      this.voiceNetwork.setText(ServiceState.rilRadioTechnologyToString(this.phone.getServiceState().getRilVoiceRadioTechnology()));
    }
  }
  
  private final void updatePhoneState(int paramInt)
  {
    Resources localResources = getResources();
    String str = localResources.getString(2131690818);
    switch (paramInt)
    {
    }
    for (;;)
    {
      this.callState.setText(str);
      return;
      str = localResources.getString(2131690811);
      continue;
      str = localResources.getString(2131690812);
      continue;
      str = localResources.getString(2131690813);
    }
  }
  
  private final void updatePingState()
  {
    this.mPingHostnameResultV4 = getResources().getString(2131690818);
    this.mPingHostnameResultV6 = getResources().getString(2131690818);
    this.mHttpClientTestResult = getResources().getString(2131690818);
    this.mPingHostnameV4.setText(this.mPingHostnameResultV4);
    this.mPingHostnameV6.setText(this.mPingHostnameResultV6);
    this.mHttpClientTest.setText(this.mHttpClientTestResult);
    final Runnable local20 = new Runnable()
    {
      public void run()
      {
        RadioInfo.-get10(RadioInfo.this).setText(RadioInfo.-get8(RadioInfo.this));
        RadioInfo.-get11(RadioInfo.this).setText(RadioInfo.-get9(RadioInfo.this));
        RadioInfo.-get4(RadioInfo.this).setText(RadioInfo.-get5(RadioInfo.this));
      }
    };
    new Thread()
    {
      public void run()
      {
        RadioInfo.-wrap3(RadioInfo.this);
        RadioInfo.-get3(RadioInfo.this).post(local20);
      }
    }.start();
    new Thread()
    {
      public void run()
      {
        RadioInfo.-wrap1(RadioInfo.this);
        RadioInfo.-get3(RadioInfo.this).post(local20);
      }
    }.start();
  }
  
  private void updatePreferredNetworkType(int paramInt)
  {
    int i;
    if (paramInt < mPreferredNetworkLabels.length)
    {
      i = paramInt;
      if (paramInt >= 0) {}
    }
    else
    {
      log("EVENT_QUERY_PREFERRED_TYPE_DONE: unknown type=" + paramInt);
      i = mPreferredNetworkLabels.length - 1;
    }
    this.mPreferredNetworkTypeResult = i;
    this.preferredNetworkType.setSelection(this.mPreferredNetworkTypeResult, true);
  }
  
  private final void updateProperties()
  {
    Resources localResources = getResources();
    String str2 = this.phone.getDeviceId();
    String str1 = str2;
    if (str2 == null) {
      str1 = localResources.getString(2131690818);
    }
    this.mDeviceId.setText(str1);
    str2 = this.phone.getLine1Number();
    str1 = str2;
    if (str2 == null) {
      str1 = localResources.getString(2131690818);
    }
    this.number.setText(str1);
  }
  
  private void updateRadioPowerState()
  {
    this.radioPowerOnSwitch.setOnCheckedChangeListener(null);
    this.radioPowerOnSwitch.setChecked(isRadioOn());
    this.radioPowerOnSwitch.setOnCheckedChangeListener(this.mRadioPowerOnChangeListener);
  }
  
  private final void updateServiceState(ServiceState paramServiceState)
  {
    int i = paramServiceState.getState();
    Resources localResources = getResources();
    String str = localResources.getString(2131690818);
    switch (i)
    {
    default: 
      this.gsmState.setText(str);
      if (paramServiceState.getRoaming()) {
        this.roamingState.setText(2131690809);
      }
      break;
    }
    for (;;)
    {
      this.operatorName.setText(paramServiceState.getOperatorAlphaLong());
      return;
      str = localResources.getString(2131690805);
      break;
      str = localResources.getString(2131690807);
      break;
      str = localResources.getString(2131690808);
      break;
      this.roamingState.setText(2131690810);
    }
  }
  
  private final void updateSignalStrength(SignalStrength paramSignalStrength)
  {
    Resources localResources = getResources();
    int k = paramSignalStrength.getDbm();
    int j = paramSignalStrength.getAsuLevel();
    int i = j;
    if (-1 == j) {
      i = 0;
    }
    this.dBm.setText(String.valueOf(k) + " " + localResources.getString(2131690821) + "   " + String.valueOf(i) + " " + localResources.getString(2131690822));
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968943);
    log("Started onCreate");
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    this.phone = PhoneFactory.getDefaultPhone();
    this.mImsManager = ImsManager.getInstance(getApplicationContext(), SubscriptionManager.getDefaultVoicePhoneId());
    this.mDeviceId = ((TextView)findViewById(2131362469));
    this.number = ((TextView)findViewById(2131362216));
    this.callState = ((TextView)findViewById(2131362476));
    this.operatorName = ((TextView)findViewById(2131362470));
    this.roamingState = ((TextView)findViewById(2131362477));
    this.gsmState = ((TextView)findViewById(2131362472));
    this.gprsState = ((TextView)findViewById(2131362473));
    this.voiceNetwork = ((TextView)findViewById(2131362474));
    this.dataNetwork = ((TextView)findViewById(2131362475));
    this.dBm = ((TextView)findViewById(2131362471));
    this.mMwi = ((TextView)findViewById(2131362484));
    this.mCfi = ((TextView)findViewById(2131362485));
    this.mLocation = ((TextView)findViewById(2131362499));
    this.mNeighboringCids = ((TextView)findViewById(2131362500));
    this.mCellInfo = ((TextView)findViewById(2131362501));
    this.mDcRtInfoTv = ((TextView)findViewById(2131362483));
    this.sent = ((TextView)findViewById(2131362486));
    this.received = ((TextView)findViewById(2131362487));
    this.smsc = ((EditText)findViewById(2131362494));
    this.dnsCheckState = ((TextView)findViewById(2131362496));
    this.mPingHostnameV4 = ((TextView)findViewById(2131362480));
    this.mPingHostnameV6 = ((TextView)findViewById(2131362481));
    this.mHttpClientTest = ((TextView)findViewById(2131362482));
    this.preferredNetworkType = ((Spinner)findViewById(2131362478));
    ArrayAdapter localArrayAdapter = new ArrayAdapter(this, 17367048, mPreferredNetworkLabels);
    localArrayAdapter.setDropDownViewResource(17367049);
    this.preferredNetworkType.setAdapter(localArrayAdapter);
    this.cellInfoRefreshRateSpinner = ((Spinner)findViewById(2131362498));
    localArrayAdapter = new ArrayAdapter(this, 17367048, mCellInfoRefreshRateLabels);
    localArrayAdapter.setDropDownViewResource(17367049);
    this.cellInfoRefreshRateSpinner.setAdapter(localArrayAdapter);
    this.imsVoLteProvisionedSwitch = ((Switch)findViewById(2131362490));
    this.radioPowerOnSwitch = ((Switch)findViewById(2131362489));
    this.pingTestButton = ((Button)findViewById(2131362479));
    this.pingTestButton.setOnClickListener(this.mPingButtonHandler);
    this.updateSmscButton = ((Button)findViewById(2131362492));
    this.updateSmscButton.setOnClickListener(this.mUpdateSmscButtonHandler);
    this.refreshSmscButton = ((Button)findViewById(2131362493));
    this.refreshSmscButton.setOnClickListener(this.mRefreshSmscButtonHandler);
    this.dnsCheckToggleButton = ((Button)findViewById(2131362495));
    this.dnsCheckToggleButton.setOnClickListener(this.mDnsCheckButtonHandler);
    this.oemInfoButton = ((Button)findViewById(2131362497));
    this.oemInfoButton.setOnClickListener(this.mOemInfoButtonHandler);
    if (getPackageManager().queryIntentActivities(new Intent("com.android.settings.OEM_RADIO_INFO"), 0).size() == 0) {
      this.oemInfoButton.setEnabled(false);
    }
    this.mCellInfoRefreshRateIndex = 0;
    this.mPreferredNetworkTypeResult = (mPreferredNetworkLabels.length - 1);
    this.phone.getPreferredNetworkType(this.mHandler.obtainMessage(1000));
    restoreFromBundle(paramBundle);
  }
  
  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    paramMenu.add(0, 0, 0, 2131690942).setOnMenuItemClickListener(this.mSelectBandCallback).setAlphabeticShortcut('b');
    paramMenu.add(1, 1, 0, 2131690801).setOnMenuItemClickListener(this.mViewADNCallback);
    paramMenu.add(1, 2, 0, 2131690802).setOnMenuItemClickListener(this.mViewFDNCallback);
    paramMenu.add(1, 3, 0, 2131690803).setOnMenuItemClickListener(this.mViewSDNCallback);
    paramMenu.add(1, 4, 0, 2131690804).setOnMenuItemClickListener(this.mGetPdpList);
    paramMenu.add(1, 5, 0, 2131690796).setOnMenuItemClickListener(this.mToggleData);
    return true;
  }
  
  protected void onPause()
  {
    super.onPause();
    log("onPause: unregister phone & data intents");
    this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    this.phone.setCellInfoListRate(Integer.MAX_VALUE);
  }
  
  public boolean onPrepareOptionsMenu(Menu paramMenu)
  {
    paramMenu = paramMenu.findItem(5);
    int i = this.mTelephonyManager.getDataState();
    boolean bool = true;
    switch (i)
    {
    case 1: 
    default: 
      bool = false;
    }
    for (;;)
    {
      paramMenu.setVisible(bool);
      return true;
      paramMenu.setTitle(2131690796);
      continue;
      paramMenu.setTitle(2131690795);
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    log("Started onResume");
    updateMessageWaiting();
    updateCallRedirect();
    updateDataState();
    updateDataStats2();
    updateRadioPowerState();
    updateImsVoLteProvisionedState();
    updateProperties();
    updateDnsCheckState();
    updateNetworkType();
    updateNeighboringCids(this.mNeighboringCellResult);
    updateLocation(this.mCellLocationResult);
    updateCellInfo(this.mCellInfoResult);
    this.mPingHostnameV4.setText(this.mPingHostnameResultV4);
    this.mPingHostnameV6.setText(this.mPingHostnameResultV6);
    this.mHttpClientTest.setText(this.mHttpClientTestResult);
    this.cellInfoRefreshRateSpinner.setOnItemSelectedListener(this.mCellInfoRefreshRateHandler);
    this.cellInfoRefreshRateSpinner.setSelection(this.mCellInfoRefreshRateIndex);
    this.preferredNetworkType.setSelection(this.mPreferredNetworkTypeResult, true);
    this.preferredNetworkType.setOnItemSelectedListener(this.mPreferredNetworkHandler);
    this.radioPowerOnSwitch.setOnCheckedChangeListener(this.mRadioPowerOnChangeListener);
    this.imsVoLteProvisionedSwitch.setOnCheckedChangeListener(this.mImsVoLteCheckedChangeListener);
    this.mTelephonyManager.listen(this.mPhoneStateListener, 9725);
    this.smsc.clearFocus();
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putString("mPingHostnameResultV4", this.mPingHostnameResultV4);
    paramBundle.putString("mPingHostnameResultV6", this.mPingHostnameResultV6);
    paramBundle.putString("mHttpClientTestResult", this.mHttpClientTestResult);
    paramBundle.putInt("mPreferredNetworkTypeResult", this.mPreferredNetworkTypeResult);
    paramBundle.putInt("mCellInfoRefreshRateIndex", this.mCellInfoRefreshRateIndex);
  }
  
  void setImsVoLteProvisionedState(final boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (String str = "on";; str = "off")
    {
      log(String.format("toggle VoLTE provisioned: %s", new Object[] { str }));
      if ((this.phone != null) && (this.mImsManager != null)) {
        QueuedWork.singleThreadExecutor().submit(new Runnable()
        {
          public void run()
          {
            try
            {
              ImsConfig localImsConfig = RadioInfo.-get6(RadioInfo.this).getConfigInterface();
              if (paramBoolean) {}
              for (int i = 1;; i = 0)
              {
                localImsConfig.setProvisionedValue(10, i);
                return;
              }
              return;
            }
            catch (ImsException localImsException)
            {
              Log.e("RadioInfo", "setImsVoLteProvisioned() exception:", localImsException);
            }
          }
        });
      }
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\RadioInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */