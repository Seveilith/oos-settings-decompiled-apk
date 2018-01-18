package com.oneplus.settings.storage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.provider.SearchIndexableResource;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.deviceinfo.PrivateVolumeSettings;
import com.android.settings.deviceinfo.StorageSettings;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable.SearchIndexProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class OPStorageSettings
  extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private static final String FILE_CREATE_PATH_8974 = "/sys/devices/platform/xhci-hcd/usb1/1-1/1-1:1.0/";
  private static final String FILE_CREATE_PATH_8994 = "/sys/devices/soc.0/f9200000.ssusb/f9200000.dwc3/xhci-hcd.0.auto/usb1/1-1/1-1:1.0/";
  private static final String FILE_CREATE_PATH_8996 = "/sys/devices/soc/6a00000.ssusb/6a00000.dwc3/xhci-hcd.0.auto/usb1/1-1/1-1:1.0/";
  private static final String FILE_DEL_PATH_8974 = "/sys/devices/platform/xhci-hcd/";
  private static final String FILE_DEL_PATH_8994 = "/sys/devices/soc.0/f9200000.ssusb/f9200000.dwc3/xhci-hcd.0.auto/";
  private static final String FILE_DEL_PATH_8996 = "/sys/devices/soc/6a00000.ssusb/6a00000.dwc3/xhci-hcd.0.auto/";
  private static final String KEY_OTG_READ_KEY = "otg_read_enable";
  private static final int MSG_SWITCHUPDATE = 9988;
  private static final String OTG_STATE_8974 = "/sys/module/dwc3/parameters/otg_state";
  private static final String OTG_STATE_8994 = "/sys/module/dwc3/parameters/otg_state";
  private static final String OTG_STATE_8996 = "/sys/module/dwc3_msm/parameters/otg_state";
  private static final String OTG_STATE_PATH = "/proc/otg_config/otg_status";
  private static final String OTG_SUPPORT_PROP = "persist.sys.oem.otg_support";
  private static final String PLATFORM_PROP = "ro.board.platform";
  public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider()
  {
    public List<String> getNonIndexableKeys(Context paramAnonymousContext)
    {
      return null;
    }
    
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context paramAnonymousContext, boolean paramAnonymousBoolean)
    {
      ArrayList localArrayList = new ArrayList();
      paramAnonymousContext = new SearchIndexableResource(paramAnonymousContext);
      paramAnonymousContext.xmlResId = 2131230818;
      localArrayList.add(paramAnonymousContext);
      return localArrayList;
    }
  };
  private static final String STORAGE_SETTINGS_KEY = "storage_settings";
  private static final String TAG = "OPStorageSettings";
  private Handler handler;
  private String isOTGEnable = "false";
  private boolean isOtgRunning = false;
  private boolean isOtgSupport = false;
  private boolean isSupportOTG = false;
  private Context mContext;
  private IMountService mMountService;
  private SwitchPreference mOtgReadEnablePreference;
  private StorageManager mStorageManager;
  private Preference mStoragePreference;
  
  /* Error */
  private IMountService getMountService()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 117	com/oneplus/settings/storage/OPStorageSettings:mMountService	Landroid/os/storage/IMountService;
    //   6: ifnonnull +21 -> 27
    //   9: ldc 119
    //   11: invokestatic 125	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   14: astore_1
    //   15: aload_1
    //   16: ifnull +20 -> 36
    //   19: aload_0
    //   20: aload_1
    //   21: invokestatic 131	android/os/storage/IMountService$Stub:asInterface	(Landroid/os/IBinder;)Landroid/os/storage/IMountService;
    //   24: putfield 117	com/oneplus/settings/storage/OPStorageSettings:mMountService	Landroid/os/storage/IMountService;
    //   27: aload_0
    //   28: getfield 117	com/oneplus/settings/storage/OPStorageSettings:mMountService	Landroid/os/storage/IMountService;
    //   31: astore_1
    //   32: aload_0
    //   33: monitorexit
    //   34: aload_1
    //   35: areturn
    //   36: ldc -123
    //   38: ldc -121
    //   40: invokestatic 141	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   43: pop
    //   44: goto -17 -> 27
    //   47: astore_1
    //   48: aload_0
    //   49: monitorexit
    //   50: aload_1
    //   51: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	52	0	this	OPStorageSettings
    //   14	21	1	localObject1	Object
    //   47	4	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   2	15	47	finally
    //   19	27	47	finally
    //   27	32	47	finally
    //   36	44	47	finally
  }
  
  private void handleOTGStatus(boolean paramBoolean)
  {
    Log.d("OTG", "handleOTGStatus " + paramBoolean);
    if (paramBoolean) {
      SystemProperties.set("persist.sys.oem.otg_support", "true");
    }
    for (;;)
    {
      new Thread("otg_thread")
      {
        public void run()
        {
          int j = 48;
          String str1 = SystemProperties.get("ro.board.platform", "NONE");
          Message localMessage = new Message();
          localMessage.what = 9988;
          Object localObject;
          String str2;
          int i;
          if (str1.equals("msm8996"))
          {
            localObject = "/sys/module/dwc3_msm/parameters/otg_state";
            str1 = "/sys/devices/soc/6a00000.ssusb/6a00000.dwc3/xhci-hcd.0.auto/usb1/1-1/1-1:1.0/";
            str2 = "/sys/devices/soc/6a00000.ssusb/6a00000.dwc3/xhci-hcd.0.auto/";
            i = j;
          }
          for (;;)
          {
            try
            {
              if (!new File((String)localObject).exists()) {
                continue;
              }
              i = j;
              localObject = new FileReader((String)localObject);
              i = j;
              if (localObject != null)
              {
                i = j;
                localObject = new BufferedReader((Reader)localObject);
                i = j;
                j = ((BufferedReader)localObject).read();
                i = j;
                ((BufferedReader)localObject).close();
                i = j;
              }
            }
            catch (IOException localIOException)
            {
              File localFile;
              boolean bool;
              Log.e("OTG", "otg_status missing", localIOException);
              continue;
              continue;
              try
              {
                Thread.sleep(200L);
                OPStorageSettings.-get0(OPStorageSettings.this).sendMessage(localMessage);
                return;
              }
              catch (InterruptedException localInterruptedException1)
              {
                Log.e("OTG", "xhci-hcd exception", localInterruptedException1);
                return;
              }
              if (!localFile.exists()) {
                continue;
              }
              j = i;
              try
              {
                Log.d("OTG", str2 + " is exist! wait 300 ms, " + i + " times!");
                i += 1;
                j = i;
                Thread.sleep(300L);
              }
              catch (InterruptedException localInterruptedException4)
              {
                Log.e("OTG", "xhci-hcd exception", localInterruptedException4);
                i = j;
              }
              continue;
              try
              {
                Thread.sleep(200L);
                OPStorageSettings.-get0(OPStorageSettings.this).sendMessage(localMessage);
                return;
              }
              catch (InterruptedException localInterruptedException2)
              {
                Log.e("OTG", "xhci-hcd exception", localInterruptedException2);
                return;
              }
              OPStorageSettings.-get0(OPStorageSettings.this).sendMessage(localMessage);
            }
            if (i != 49) {
              continue;
            }
            localObject = new File(str1);
            localFile = new File(str2);
            bool = Boolean.parseBoolean(SystemProperties.get("persist.sys.oem.otg_support", "false"));
            i = 1;
            if (!bool) {
              continue;
            }
            if ((((File)localObject).exists()) || (i >= 10)) {
              continue;
            }
            try
            {
              Thread.sleep(300L);
              Log.d("OTG", str1 + " isn't exist! wait 300 ms, " + i + " times!");
              i += 1;
            }
            catch (InterruptedException localInterruptedException3)
            {
              Log.e("OTG", "xhci-hcd exception", localInterruptedException3);
            }
            if (str1.equals("msm8994"))
            {
              localObject = "/sys/module/dwc3/parameters/otg_state";
              str1 = "/sys/devices/soc.0/f9200000.ssusb/f9200000.dwc3/xhci-hcd.0.auto/usb1/1-1/1-1:1.0/";
              str2 = "/sys/devices/soc.0/f9200000.ssusb/f9200000.dwc3/xhci-hcd.0.auto/";
              break;
            }
            localObject = "/sys/module/dwc3/parameters/otg_state";
            str1 = "/sys/devices/platform/xhci-hcd/usb1/1-1/1-1:1.0/";
            str2 = "/sys/devices/platform/xhci-hcd/";
            break;
            i = j;
            localObject = new FileReader("/proc/otg_config/otg_status");
          }
        }
      }.start();
      if (!paramBoolean) {}
      try
      {
        if (this.mMountService != null) {
          this.mMountService.unmountVolume("/storage/UDiskA", true, false);
        }
        return;
      }
      catch (Exception localException) {}
      SystemProperties.set("persist.sys.oem.otg_support", "false");
    }
  }
  
  private void updateView()
  {
    this.mStoragePreference = findPreference("storage_settings");
    System.out.println("zhuyang--mStoragePreference:" + this.mStoragePreference);
    this.mStoragePreference.setOnPreferenceClickListener(this);
    this.mOtgReadEnablePreference = ((SwitchPreference)findPreference("otg_read_enable"));
    this.mOtgReadEnablePreference.setOnPreferenceClickListener(this);
    removePreference("otg_read_enable");
  }
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131230818);
    this.mContext = getActivity();
    this.mStorageManager = ((StorageManager)this.mContext.getSystemService(StorageManager.class));
    this.isSupportOTG = this.mContext.getPackageManager().hasSystemFeature("oem.otgSwitch.support");
    updateView();
    getMountService();
    this.handler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        switch (paramAnonymousMessage.what)
        {
        }
        for (;;)
        {
          super.handleMessage(paramAnonymousMessage);
          return;
          if (OPStorageSettings.-get1(OPStorageSettings.this) != Boolean.parseBoolean(SystemProperties.get("persist.sys.oem.otg_support", "false"))) {
            OPStorageSettings.-wrap0(OPStorageSettings.this, OPStorageSettings.-get1(OPStorageSettings.this));
          } else {
            OPStorageSettings.-set0(OPStorageSettings.this, false);
          }
        }
      }
    };
  }
  
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return false;
  }
  
  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference.getKey().equals("otg_read_enable"))
    {
      if (this.mOtgReadEnablePreference.isChecked()) {}
      for (this.isOtgSupport = true;; this.isOtgSupport = false)
      {
        if (!this.isOtgRunning)
        {
          this.isOtgRunning = true;
          handleOTGStatus(this.isOtgSupport);
        }
        return true;
      }
    }
    if (paramPreference.getKey().equals("storage_settings"))
    {
      paramPreference = this.mStorageManager.getDisks();
      if ((paramPreference != null) && (paramPreference.size() == 0))
      {
        paramPreference = new Bundle();
        paramPreference.putString("android.os.storage.extra.VOLUME_ID", "private");
        startFragment(this, PrivateVolumeSettings.class.getCanonicalName(), -1, 0, paramPreference);
        return true;
      }
      startFragment(this, StorageSettings.class.getCanonicalName(), -1, 0, null);
      return true;
    }
    return false;
  }
  
  public void onResume()
  {
    super.onResume();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\storage\OPStorageSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */