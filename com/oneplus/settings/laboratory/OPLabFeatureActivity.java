package com.oneplus.settings.laboratory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OPLabFeatureActivity
  extends Activity
  implements AdapterView.OnItemClickListener
{
  public static final int DATA_LOAD_COMPLETED = 0;
  private static final String ONEPLUS_LAB_FEATURE_KEY = "oneplus_lab_feature_key";
  private static final String ONEPLUS_LAB_FEATURE_SUMMARY = "oneplus_lab_feature_Summary";
  private static final String ONEPLUS_LAB_FEATURE_TITLE = "oneplus_lab_feature_title";
  private static final String ONEPLUS_LAB_FEATURE_TOGGLE_COUNT = "oneplus_lab_feature_toggle_count";
  private static final String ONEPLUS_LAB_FEATURE_TOGGLE_NAMES = "oneplus_lab_feature_toggle_names";
  private static final String ONEPLUS_NFC_SECURITY_MODULE_KEY = "oneplus_nfc_security_module_key";
  private static final String PLUGIN_ACTION = "com.android.ONEPLUS_LAB_PLUGIN";
  private Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        return;
      } while (OPLabFeatureActivity.-get1(OPLabFeatureActivity.this) == null);
      paramAnonymousMessage = new ArrayList();
      paramAnonymousMessage.addAll(OPLabFeatureActivity.-get0(OPLabFeatureActivity.this));
      OPLabFeatureActivity.-get1(OPLabFeatureActivity.this).setData(paramAnonymousMessage);
    }
  };
  private List<OPLabPluginModel> mPluginData = new ArrayList();
  private ListView mPluginList;
  private OPLabPluginListAdapter mPluginListAdapter;
  private ExecutorService mThreadPool = Executors.newCachedThreadPool();
  
  private void gotoDetailPage(OPLabPluginModel paramOPLabPluginModel)
  {
    Intent localIntent = new Intent("android.intent.action.ONEPLUS_LAB_FEATURE_DETAILS");
    localIntent.putExtra("oneplus_lab_feature_toggle_count", paramOPLabPluginModel.getToggleCount());
    localIntent.putExtra("oneplus_lab_feature_toggle_names", paramOPLabPluginModel.getMultiToggleName());
    localIntent.putExtra("oneplus_lab_feature_title", paramOPLabPluginModel.getFeatureTitle());
    localIntent.putExtra("oneplus_lab_feature_Summary", paramOPLabPluginModel.getFeatureSummary());
    localIntent.putExtra("oneplus_lab_feature_key", paramOPLabPluginModel.getFeatureKey());
    startActivity(localIntent);
  }
  
  private void initData(final Handler paramHandler)
  {
    paramHandler = new Runnable()
    {
      public void run()
      {
        OPLabFeatureActivity.this.fetchLockedAppListByPackageInfo();
        paramHandler.sendEmptyMessage(0);
      }
    };
    this.mThreadPool.execute(paramHandler);
  }
  
  public void fetchLockedAppListByActivityInfo()
  {
    try
    {
      Object localObject1 = getPackageManager().queryIntentActivities(new Intent("com.android.ONEPLUS_LAB_PLUGIN"), 128);
      if (((List)localObject1).isEmpty()) {
        return;
      }
      localObject1 = ((Iterable)localObject1).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Object localObject2 = (ResolveInfo)((Iterator)localObject1).next();
        String str1 = ((ResolveInfo)localObject2).activityInfo.metaData.getString("oneplus_lab_package_name");
        String str2 = ((ResolveInfo)localObject2).activityInfo.metaData.getString("oneplus_lab_feature_title");
        String str3 = ((ResolveInfo)localObject2).activityInfo.metaData.getString("oneplus_lab_feature_summary");
        String str4 = ((ResolveInfo)localObject2).activityInfo.metaData.getString("oneplus_lab_feature_toggle_key");
        int i = ((ResolveInfo)localObject2).activityInfo.applicationInfo.uid;
        localObject2 = new OPLabPluginModel();
        ((OPLabPluginModel)localObject2).setPackageName(str1);
        ((OPLabPluginModel)localObject2).setFeatureTitle(str2);
        ((OPLabPluginModel)localObject2).setFeatureSummary(str3);
        ((OPLabPluginModel)localObject2).setFeatureKey(str4);
        this.mPluginData.add(localObject2);
      }
      return;
    }
    catch (Exception localException)
    {
      Log.e("PluginDemo", "some unknown error happened.");
      localException.printStackTrace();
    }
  }
  
  public void fetchLockedAppListByPackageInfo()
  {
    int i;
    OPLabPluginModel localOPLabPluginModel;
    String[] arrayOfString2;
    int i1;
    int j;
    String str2;
    String[] arrayOfString3;
    try
    {
      System.currentTimeMillis();
      localObject = getPackageManager().getInstalledPackages(128);
      if (((List)localObject).isEmpty()) {
        return;
      }
      Iterator localIterator = ((Iterable)localObject).iterator();
      String[] arrayOfString1;
      do
      {
        do
        {
          if (!localIterator.hasNext()) {
            break;
          }
          localPackageInfo = (PackageInfo)localIterator.next();
          localObject = localPackageInfo.applicationInfo.metaData;
        } while ((localObject == null) || (!((Bundle)localObject).containsKey("oneplus_lab_feature")));
        localContext = createPackageContext(localPackageInfo.packageName, 0);
        arrayOfString1 = ((Bundle)localObject).getString("oneplus_lab_feature").split(";");
        i = 0;
      } while (i >= arrayOfString1.length);
      localOPLabPluginModel = new OPLabPluginModel();
      arrayOfString2 = arrayOfString1[i].split(",");
      i1 = arrayOfString2.length;
      j = 2;
      if (i1 <= 3) {
        break label577;
      }
      String str4 = arrayOfString2[0];
      m = localContext.getResources().getIdentifier(str4, "string", localPackageInfo.packageName);
      str2 = arrayOfString2[1];
      int n = localContext.getResources().getIdentifier(str2, "string", localPackageInfo.packageName);
      str3 = arrayOfString2[2];
      k = Integer.parseInt(arrayOfString2[3]);
      arrayOfString3 = (String[])Arrays.copyOfRange(arrayOfString2, 4, i1);
      arrayOfString2 = new String[arrayOfString3.length];
      j = 0;
      if (j < arrayOfString3.length)
      {
        i1 = localContext.getResources().getIdentifier(arrayOfString3[j], "string", localPackageInfo.packageName);
        if (i1 == 0) {
          break label561;
        }
        localObject = localContext.getResources().getString(i1);
        break label548;
      }
      localObject = str4;
      if (m != 0) {
        localObject = localContext.getResources().getString(m);
      }
      localOPLabPluginModel.setFeatureTitle((String)localObject);
      localObject = str2;
      if (n != 0) {
        localObject = localContext.getResources().getString(n);
      }
      localOPLabPluginModel.setFeatureSummary((String)localObject);
      localOPLabPluginModel.setMultiToggleName(arrayOfString2);
      localOPLabPluginModel.setFeatureKey(str3);
      j = k;
      label363:
      if ((OPUtils.isSurportSimNfc(localContext)) || (!"oneplus_nfc_security_module_key".equals(str3))) {
        break label527;
      }
    }
    catch (Exception localException)
    {
      Object localObject;
      PackageInfo localPackageInfo;
      Context localContext;
      int m;
      int k;
      label384:
      Log.e("PluginDemo", "some unknown error happened.");
      localException.printStackTrace();
    }
    k = localContext.getResources().getIdentifier((String)localObject, "string", localPackageInfo.packageName);
    if (i1 > 2)
    {
      str2 = arrayOfString2[1];
      label415:
      m = localContext.getResources().getIdentifier(str2, "string", localPackageInfo.packageName);
      if (i1 < 3) {
        break label600;
      }
    }
    label527:
    label548:
    label561:
    label570:
    label577:
    label600:
    for (String str3 = arrayOfString2[2];; str3 = "")
    {
      if (k != 0) {
        localObject = localContext.getResources().getString(k);
      }
      localOPLabPluginModel.setFeatureTitle((String)localObject);
      if (m != 0) {
        str2 = localContext.getResources().getString(m);
      }
      localOPLabPluginModel.setFeatureSummary(str2);
      localOPLabPluginModel.setFeatureKey(str3);
      break label363;
      return;
      do
      {
        str1 = "";
        break label384;
        localOPLabPluginModel.setToggleCount(j);
        this.mPluginData.add(localOPLabPluginModel);
        break label570;
        for (;;)
        {
          arrayOfString2[j] = str1;
          j += 1;
          break;
          str1 = arrayOfString3[j];
        }
        i += 1;
        break;
      } while (i1 <= 1);
      String str1 = arrayOfString2[0];
      break label384;
      str2 = "";
      break label415;
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968818);
    this.mPluginList = ((ListView)findViewById(2131362351));
    this.mPluginListAdapter = new OPLabPluginListAdapter(this, this.mPluginData);
    this.mPluginList.setAdapter(this.mPluginListAdapter);
    this.mPluginList.setOnItemClickListener(this);
    initData(this.mHandler);
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    gotoDetailPage(this.mPluginListAdapter.getItem(paramInt));
  }
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default: 
      return super.onOptionsItemSelected(paramMenuItem);
    }
    finish();
    return true;
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\laboratory\OPLabFeatureActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */