package com.oneplus.settings.gestures;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.apploader.OPApplicationLoader;
import com.oneplus.settings.better.OPAppModel;
import java.util.ArrayList;
import java.util.List;

public class OPGestureAppListSettings
  extends BaseActivity
  implements AdapterView.OnItemClickListener
{
  public static final int DEFAULT_GESTURE_COUNT = 6;
  public static final int SHORTCUT_REQUESET_CODE = 1;
  private static final int TIME_DELAY = 100;
  private List<OPAppModel> mDefaultGestureAppList = new ArrayList();
  private List<OPAppModel> mGestureAppList = new ArrayList();
  private ListView mGestureAppListView;
  private String mGestureKey;
  private String mGesturePackageName;
  private String mGestureSummary;
  private String mGestureTitle;
  private int mGestureUid;
  private int mGestureValueIndex;
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
      } while ((OPGestureAppListSettings.-get4(OPGestureAppListSettings.this) == null) || (OPGestureAppListSettings.-get3(OPGestureAppListSettings.this) == null));
      OPGestureAppListSettings.-get1(OPGestureAppListSettings.this).clear();
      OPGestureAppListSettings.-get1(OPGestureAppListSettings.this).addAll(OPGestureAppListSettings.-get0(OPGestureAppListSettings.this));
      OPGestureAppListSettings.-get1(OPGestureAppListSettings.this).addAll(OPGestureAppListSettings.-get3(OPGestureAppListSettings.this).getAllAppList());
      OPGestureAppListSettings.-get4(OPGestureAppListSettings.this).setData(OPGestureAppListSettings.-get1(OPGestureAppListSettings.this));
      OPGestureAppListSettings.-get2(OPGestureAppListSettings.this).setSelection(OPGestureAppListSettings.-wrap0(OPGestureAppListSettings.this));
    }
  };
  private View mLoadingContainer;
  private OPApplicationLoader mOPApplicationLoader;
  private OPGestureAppAdapter mOPGestureAppAdapter;
  private PackageManager mPackageManager;
  
  private List<OPAppModel> createDefaultAppList()
  {
    this.mDefaultGestureAppList.clear();
    OPAppModel localOPAppModel1 = new OPAppModel("", getString(2131690365), "", 0, false);
    OPAppModel localOPAppModel2 = new OPAppModel("", getString(2131690366), "", 0, false);
    OPAppModel localOPAppModel3 = new OPAppModel("", getString(2131690367), "", 0, false);
    OPAppModel localOPAppModel4 = new OPAppModel("", getString(2131690368), "", 0, false);
    OPAppModel localOPAppModel5 = new OPAppModel("", getString(2131690369, new Object[] { Boolean.valueOf(false) }), "", 0, false);
    OPAppModel localOPAppModel6 = new OPAppModel("", getString(2131690014), "", 0, false);
    this.mDefaultGestureAppList.add(localOPAppModel1);
    this.mDefaultGestureAppList.add(localOPAppModel2);
    this.mDefaultGestureAppList.add(localOPAppModel3);
    this.mDefaultGestureAppList.add(localOPAppModel4);
    this.mDefaultGestureAppList.add(localOPAppModel5);
    this.mDefaultGestureAppList.add(localOPAppModel6);
    return this.mDefaultGestureAppList;
  }
  
  private void doNothing()
  {
    OPGestureUtils.set0(this, this.mGestureValueIndex);
    Settings.System.putString(getContentResolver(), this.mGestureKey, "");
  }
  
  private int getSelectionPosition()
  {
    this.mGestureSummary = OPGestureUtils.getGestureSummarybyGestureKey(this, this.mGestureKey);
    this.mGesturePackageName = OPGestureUtils.getGesturePackageName(this, this.mGestureKey);
    int k = 0;
    int i = 0;
    for (;;)
    {
      int j = k;
      if (i < this.mGestureAppList.size())
      {
        if (i < 6)
        {
          if (!this.mGestureSummary.equals(((OPAppModel)this.mGestureAppList.get(i)).getLabel())) {
            break label107;
          }
          j = i;
        }
      }
      else {
        return j;
      }
      if (this.mGesturePackageName.equals(((OPAppModel)this.mGestureAppList.get(i)).getPkgName())) {
        return i;
      }
      label107:
      i += 1;
    }
  }
  
  private void gotoShortCutsPickPage(OPAppModel paramOPAppModel)
  {
    Intent localIntent = new Intent("android.intent.action.ONEPLUS_GESTURE_SHORTCUT_LIST_ACTION");
    localIntent.putExtra("op_gesture_key", this.mGestureKey);
    localIntent.putExtra("op_gesture_package", paramOPAppModel.getPkgName());
    localIntent.putExtra("op_gesture_package_uid", paramOPAppModel.getUid());
    localIntent.putExtra("op_gesture_package_app", paramOPAppModel.getLabel());
    startActivityForResult(localIntent, 1);
  }
  
  private void initData()
  {
    this.mOPApplicationLoader.initData(0, this.mHandler);
  }
  
  private void initView()
  {
    this.mGestureAppListView = ((ListView)findViewById(2131362340));
    this.mGestureAppListView.setOnItemClickListener(this);
    this.mPackageManager = getPackageManager();
    this.mOPApplicationLoader = new OPApplicationLoader(this, this.mPackageManager);
    this.mLoadingContainer = findViewById(2131362189);
    this.mOPApplicationLoader.setmLoadingContainer(this.mLoadingContainer);
    this.mOPApplicationLoader.setNeedLoadWorkProfileApps(false);
    createDefaultAppList();
    this.mOPGestureAppAdapter = new OPGestureAppAdapter(this, this.mPackageManager, this.mGestureSummary);
    this.mGestureAppListView.setAdapter(this.mOPGestureAppAdapter);
    initData();
  }
  
  private void openApps(OPAppModel paramOPAppModel)
  {
    Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenApp:" + paramOPAppModel.getPkgName() + ";" + paramOPAppModel.getUid());
  }
  
  private void openBackCamera()
  {
    Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenCamera");
  }
  
  private void openFlashLight()
  {
    Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenTorch");
  }
  
  private void openFrontCamera()
  {
    Settings.System.putString(getContentResolver(), this.mGestureKey, "FrontCamera");
  }
  
  private void openShelf()
  {
    Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenShelf");
  }
  
  private void openTakeVideo()
  {
    Settings.System.putString(getContentResolver(), this.mGestureKey, "TakeVideo");
  }
  
  private void refreshList()
  {
    this.mGestureSummary = OPGestureUtils.getGestureSummarybyGestureKey(this, this.mGestureKey);
    this.mGesturePackageName = OPGestureUtils.getGesturePackageName(this, this.mGestureKey);
    String str = OPGestureUtils.getShortCutIdByGestureKey(this, this.mGestureKey);
    if (OPGestureUtils.hasShortCutsGesture(this, this.mGestureKey)) {}
    for (boolean bool = OPGestureUtils.hasShortCutsId(this, this.mGesturePackageName, str);; bool = false)
    {
      this.mOPGestureAppAdapter.setSelectedItem(this.mGestureSummary, this.mGesturePackageName, this.mGestureUid, bool, OPGestureUtils.getShortCutsNameByID(this, this.mGesturePackageName, str));
      return;
    }
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if ((paramInt1 == 1) && (paramInt2 == -1)) {
      finish();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968816);
    paramBundle = getIntent();
    this.mGestureKey = paramBundle.getStringExtra("op_gesture_key");
    this.mGestureTitle = paramBundle.getStringExtra("op_gesture_action");
    this.mGestureValueIndex = OPGestureUtils.getIndexByGestureValueKey(this.mGestureKey);
    this.mGestureSummary = OPGestureUtils.getGestureSummarybyGestureKey(this, this.mGestureKey);
    paramBundle = OPGestureUtils.getGesturePacakgeUid(this, this.mGestureKey);
    if (TextUtils.isEmpty(paramBundle)) {}
    for (int i = -1;; i = Integer.valueOf(paramBundle).intValue())
    {
      this.mGestureUid = i;
      this.mGesturePackageName = OPGestureUtils.getGesturePackageName(this, this.mGestureKey);
      paramBundle = getActionBar();
      paramBundle.setTitle(this.mGestureTitle);
      paramBundle.setDisplayHomeAsUpEnabled(true);
      paramBundle.setHomeButtonEnabled(true);
      initView();
      return;
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    OPGestureUtils.set1(this, this.mGestureValueIndex);
    int i = 0;
    switch (paramInt)
    {
    default: 
      paramAdapterView = (OPAppModel)this.mGestureAppListView.getItemAtPosition(paramInt);
      if (OPGestureUtils.hasShortCuts(this, paramAdapterView.getPkgName()))
      {
        gotoShortCutsPickPage(paramAdapterView);
        paramInt = 1;
      }
      break;
    }
    for (;;)
    {
      refreshList();
      if (paramInt == 0) {
        finish();
      }
      return;
      doNothing();
      paramInt = i;
      continue;
      openBackCamera();
      paramInt = i;
      continue;
      openFrontCamera();
      paramInt = i;
      continue;
      openTakeVideo();
      paramInt = i;
      continue;
      openFlashLight();
      paramInt = i;
      continue;
      openShelf();
      paramInt = i;
      continue;
      openApps(paramAdapterView);
      paramInt = i;
    }
  }
  
  protected void onPause()
  {
    super.onPause();
  }
  
  protected void onResume()
  {
    super.onResume();
    refreshList();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\gestures\OPGestureAppListSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */