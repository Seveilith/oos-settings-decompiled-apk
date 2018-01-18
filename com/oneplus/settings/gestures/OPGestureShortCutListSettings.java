package com.oneplus.settings.gestures;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.oneplus.settings.BaseActivity;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPGestureShortCutListSettings
  extends BaseActivity
  implements AdapterView.OnItemClickListener
{
  public static final String TAG = "OPGestureShortCutListSettings";
  private OPGestureAppModel mAPPOPGestureAppModel;
  private Drawable mAppDrawable;
  private ApplicationInfo mApplicationInfo;
  private List<OPGestureAppModel> mGestureAppList = new ArrayList();
  private String mGestureKey;
  private String mGesturePackage;
  private ListView mGestureShortcutListView;
  private String mGestureSummary;
  private int mGestureUid;
  private OPGestureShortcutsAdapter mOPGestureShortcutsAdapter;
  private PackageManager mPackageManager;
  private List<ShortcutInfo> mShortcutInfo;
  private String mTitle;
  
  private void initData()
  {
    Object localObject1 = (LauncherApps)getSystemService("launcherapps");
    this.mShortcutInfo = OPGestureUtils.loadShortCuts(this, this.mGesturePackage);
    if (this.mShortcutInfo == null) {
      return;
    }
    this.mGestureAppList.clear();
    localObject1 = new OPGestureAppModel(this.mGesturePackage, this.mTitle, "", 0);
    ((OPGestureAppModel)localObject1).setAppIcon(this.mAppDrawable);
    this.mGestureAppList.add(localObject1);
    int j = this.mShortcutInfo.size();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        ShortcutInfo localShortcutInfo = (ShortcutInfo)this.mShortcutInfo.get(i);
        Object localObject2 = localShortcutInfo.getLongLabel();
        localObject1 = localObject2;
        if (TextUtils.isEmpty((CharSequence)localObject2)) {
          localObject1 = localShortcutInfo.getShortLabel();
        }
        localObject2 = localObject1;
        if (TextUtils.isEmpty((CharSequence)localObject1)) {
          localObject2 = localShortcutInfo.getId();
        }
        localObject1 = new OPGestureAppModel(localShortcutInfo.getPackage(), ((CharSequence)localObject2).toString(), localShortcutInfo.getId(), 0);
        try
        {
          ((OPGestureAppModel)localObject1).setAppIcon(createPackageContext(this.mGesturePackage, 0).getResources().getDrawable(localShortcutInfo.getIconResourceId()));
          this.mGestureAppList.add(localObject1);
          i += 1;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localException.printStackTrace();
          }
        }
      }
    }
  }
  
  private void initView()
  {
    this.mGestureShortcutListView = ((ListView)findViewById(2131362340));
    OPUtils.setListDivider(SettingsBaseApplication.mApplication, this.mGestureShortcutListView, 2130838238, 2130838236, 2131755334);
    this.mGestureShortcutListView.setOnItemClickListener(this);
  }
  
  private void openApps(OPGestureAppModel paramOPGestureAppModel)
  {
    Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenApp:" + paramOPGestureAppModel.getPkgName() + ";" + this.mGestureUid);
  }
  
  private void openShortCuts(OPGestureAppModel paramOPGestureAppModel)
  {
    Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenShortcut:" + paramOPGestureAppModel.getPkgName() + ";" + paramOPGestureAppModel.getShortCutId() + ";" + this.mGestureUid);
  }
  
  private void refreshList()
  {
    initData();
    if (!this.mGesturePackage.equals(OPGestureUtils.getGesturePackageName(this, this.mGestureKey))) {
      Settings.System.putString(getContentResolver(), this.mGestureKey, "OpenApp:" + this.mGesturePackage);
    }
    this.mGestureSummary = OPGestureUtils.getShortCutsNameByID(this, this.mGesturePackage, OPGestureUtils.getShortCutIdByGestureKey(this, this.mGestureKey));
    List localList = this.mGestureAppList;
    if (TextUtils.isEmpty(this.mGestureSummary)) {}
    for (String str = this.mTitle;; str = this.mGestureSummary)
    {
      this.mOPGestureShortcutsAdapter = new OPGestureShortcutsAdapter(this, localList, str);
      this.mGestureShortcutListView.setAdapter(this.mOPGestureShortcutsAdapter);
      return;
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968816);
    paramBundle = getIntent();
    this.mGestureKey = paramBundle.getStringExtra("op_gesture_key");
    this.mGesturePackage = paramBundle.getStringExtra("op_gesture_package");
    this.mGestureUid = paramBundle.getIntExtra("op_gesture_package_uid", -1);
    this.mTitle = paramBundle.getStringExtra("op_gesture_package_app");
    paramBundle = getActionBar();
    paramBundle.setTitle(this.mTitle);
    paramBundle.setDisplayHomeAsUpEnabled(true);
    paramBundle.setHomeButtonEnabled(true);
    this.mPackageManager = getPackageManager();
    try
    {
      this.mApplicationInfo = this.mPackageManager.getApplicationInfo(this.mGesturePackage, 0);
      this.mAppDrawable = this.mApplicationInfo.loadIcon(this.mPackageManager);
      initView();
      return;
    }
    catch (PackageManager.NameNotFoundException paramBundle)
    {
      for (;;) {}
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    paramAdapterView = (OPGestureAppModel)this.mGestureShortcutListView.getItemAtPosition(paramInt);
    switch (paramInt)
    {
    default: 
      openShortCuts(paramAdapterView);
    }
    for (;;)
    {
      setResult(-1);
      finish();
      return;
      openApps(paramAdapterView);
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


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\gestures\OPGestureShortCutListSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */