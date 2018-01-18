package com.oneplus.settings.defaultapp.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import com.android.settings.AppListPreference;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.defaultapp.DefaultAppActivityInfo;
import com.oneplus.settings.defaultapp.DefaultAppLogic;
import com.oneplus.settings.defaultapp.DefaultAppUtils;
import java.util.List;
import java.util.Objects;

public abstract class BaseDefaultPreference
  extends AppListPreference
{
  protected List<DefaultAppActivityInfo> mAppInfoList;
  protected List<String> mAppNameInfoList;
  protected DefaultAppLogic mLogic;
  protected String mType;
  
  public BaseDefaultPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    loadDefaultApps();
  }
  
  private void loadDefaultApps()
  {
    this.mType = getType();
    this.mLogic = DefaultAppLogic.getInstance(SettingsBaseApplication.mApplication);
    this.mAppInfoList = this.mLogic.getAppInfoList(this.mType);
    this.mAppNameInfoList = this.mLogic.getAppPackageNameList(this.mType, this.mAppInfoList);
    String[] arrayOfString = (String[])this.mAppNameInfoList.toArray(new String[this.mAppNameInfoList.size()]);
    String str = this.mLogic.getPmDefaultAppPackageName(this.mType);
    setPackageNames(arrayOfString, str);
    int i;
    if (TextUtils.isEmpty(str)) {
      switch (DefaultAppUtils.getKeyTypeInt(this.mType))
      {
      default: 
        return;
      case 0: 
        i = 2131690445;
      }
    }
    for (;;)
    {
      setSummary(i);
      return;
      i = 2131690446;
      continue;
      i = 2131690447;
      continue;
      i = 2131690448;
    }
  }
  
  protected abstract String getType();
  
  protected boolean persistString(String paramString)
  {
    String str = this.mLogic.getDefaultAppPackageName(this.mType);
    if ((TextUtils.isEmpty(paramString)) || (Objects.equals(paramString, str))) {}
    for (;;)
    {
      setSummary(getEntry());
      return true;
      Log.d("BaseDefaultPreference", "persistString value:" + paramString + ", local defaultAppPackageName:" + str);
      this.mLogic.setDefaultAppPosition(this.mType, this.mAppInfoList, this.mAppNameInfoList, paramString);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\view\BaseDefaultPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */