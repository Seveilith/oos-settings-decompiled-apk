package com.android.settings;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkTemplate;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.android.settings.applications.ProcStatsData;
import com.android.settings.applications.ProcStatsData.MemInfo;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.net.DataUsageController.DataUsageInfo;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsDumpService
  extends Service
{
  private JSONObject dumpDataUsage()
    throws JSONException
  {
    JSONObject localJSONObject1 = new JSONObject();
    DataUsageController localDataUsageController = new DataUsageController(this);
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService(ConnectivityManager.class);
    Object localObject = SubscriptionManager.from(this);
    TelephonyManager localTelephonyManager = TelephonyManager.from(this);
    if (localConnectivityManager.isNetworkSupported(0))
    {
      JSONArray localJSONArray = new JSONArray();
      localObject = ((SubscriptionManager)localObject).getAllSubscriptionInfoList().iterator();
      while (((Iterator)localObject).hasNext())
      {
        SubscriptionInfo localSubscriptionInfo = (SubscriptionInfo)((Iterator)localObject).next();
        JSONObject localJSONObject2 = dumpDataUsage(NetworkTemplate.buildTemplateMobileAll(localTelephonyManager.getSubscriberId(localSubscriptionInfo.getSubscriptionId())), localDataUsageController);
        localJSONObject2.put("subId", localSubscriptionInfo.getSubscriptionId());
        localJSONArray.put(localJSONObject2);
      }
      localJSONObject1.put("cell", localJSONArray);
    }
    if (localConnectivityManager.isNetworkSupported(1)) {
      localJSONObject1.put("wifi", dumpDataUsage(NetworkTemplate.buildTemplateWifiWildcard(), localDataUsageController));
    }
    if (localConnectivityManager.isNetworkSupported(9)) {
      localJSONObject1.put("ethernet", dumpDataUsage(NetworkTemplate.buildTemplateEthernet(), localDataUsageController));
    }
    return localJSONObject1;
  }
  
  private JSONObject dumpDataUsage(NetworkTemplate paramNetworkTemplate, DataUsageController paramDataUsageController)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    paramNetworkTemplate = paramDataUsageController.getDataUsageInfo(paramNetworkTemplate);
    localJSONObject.put("carrier", paramNetworkTemplate.carrier);
    localJSONObject.put("start", paramNetworkTemplate.startDate);
    localJSONObject.put("usage", paramNetworkTemplate.usageLevel);
    localJSONObject.put("warning", paramNetworkTemplate.warningLevel);
    localJSONObject.put("limit", paramNetworkTemplate.limitLevel);
    return localJSONObject;
  }
  
  private JSONObject dumpMemory()
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    ProcStatsData localProcStatsData = new ProcStatsData(this, false);
    localProcStatsData.refreshStats(true);
    ProcStatsData.MemInfo localMemInfo = localProcStatsData.getMemInfo();
    localJSONObject.put("used", String.valueOf(localMemInfo.realUsedRam));
    localJSONObject.put("free", String.valueOf(localMemInfo.realFreeRam));
    localJSONObject.put("total", String.valueOf(localMemInfo.realTotalRam));
    localJSONObject.put("state", localProcStatsData.getMemState());
    return localJSONObject;
  }
  
  private JSONObject dumpStorage()
    throws JSONException
  {
    JSONObject localJSONObject1 = new JSONObject();
    Iterator localIterator = ((StorageManager)getSystemService(StorageManager.class)).getVolumes().iterator();
    while (localIterator.hasNext())
    {
      VolumeInfo localVolumeInfo = (VolumeInfo)localIterator.next();
      JSONObject localJSONObject2 = new JSONObject();
      if (localVolumeInfo.isMountedReadable())
      {
        File localFile = localVolumeInfo.getPath();
        localJSONObject2.put("used", String.valueOf(localFile.getTotalSpace() - localFile.getFreeSpace()));
        localJSONObject2.put("total", String.valueOf(localFile.getTotalSpace()));
      }
      localJSONObject2.put("path", localVolumeInfo.getInternalPath());
      localJSONObject2.put("state", localVolumeInfo.getState());
      localJSONObject2.put("stateDesc", localVolumeInfo.getStateDescription());
      localJSONObject2.put("description", localVolumeInfo.getDescription());
      localJSONObject1.put(localVolumeInfo.getId(), localJSONObject2);
    }
    return localJSONObject1;
  }
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramFileDescriptor = new JSONObject();
    try
    {
      paramFileDescriptor.put("service", "Settings State");
      paramFileDescriptor.put("storage", dumpStorage());
      paramFileDescriptor.put("datausage", dumpDataUsage());
      paramFileDescriptor.put("memory", dumpMemory());
      paramPrintWriter.println(paramFileDescriptor);
      return;
    }
    catch (Exception paramArrayOfString)
    {
      for (;;)
      {
        paramArrayOfString.printStackTrace();
      }
    }
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SettingsDumpService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */