package com.android.settings.applications;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settings.InstrumentedFragment;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import java.util.Iterator;

public class AppOpsDetails
  extends InstrumentedFragment
{
  public static final String ARG_PACKAGE_NAME = "package";
  static final String TAG = "AppOpsDetails";
  private AppOpsManager mAppOps;
  private LayoutInflater mInflater;
  private LinearLayout mOperationsSection;
  private PackageInfo mPackageInfo;
  private PackageManager mPm;
  private View mRootView;
  private AppOpsState mState;
  
  private boolean refreshUi()
  {
    if (this.mPackageInfo == null) {
      return false;
    }
    setAppLabelAndIcon(this.mPackageInfo);
    Resources localResources = getActivity().getResources();
    this.mOperationsSection.removeAllViews();
    Object localObject4 = "";
    AppOpsState.OpsTemplate[] arrayOfOpsTemplate = AppOpsState.ALL_TEMPLATES;
    int j = arrayOfOpsTemplate.length;
    int i = 0;
    while (i < j)
    {
      Object localObject1 = arrayOfOpsTemplate[i];
      Iterator localIterator = this.mState.buildState((AppOpsState.OpsTemplate)localObject1, this.mPackageInfo.applicationInfo.uid, this.mPackageInfo.packageName).iterator();
      Object localObject3;
      if (localIterator.hasNext())
      {
        final AppOpsState.AppOpEntry localAppOpEntry = (AppOpsState.AppOpEntry)localIterator.next();
        AppOpsManager.OpEntry localOpEntry = localAppOpEntry.getOpEntry(0);
        View localView = this.mInflater.inflate(2130968616, this.mOperationsSection, false);
        this.mOperationsSection.addView(localView);
        Object localObject5 = AppOpsManager.opToPermission(localOpEntry.getOp());
        localObject1 = localObject4;
        if (localObject5 != null) {
          localObject3 = localObject4;
        }
        for (;;)
        {
          try
          {
            localObject5 = this.mPm.getPermissionInfo((String)localObject5, 0);
            localObject1 = localObject4;
            localObject3 = localObject4;
            if (((PermissionInfo)localObject5).group != null)
            {
              localObject3 = localObject4;
              bool = ((String)localObject4).equals(((PermissionInfo)localObject5).group);
              if (!bool) {
                continue;
              }
              localObject1 = localObject4;
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
            final int k;
            Object localObject2 = localObject3;
            continue;
            boolean bool = false;
            continue;
          }
          ((TextView)localView.findViewById(2131361958)).setText(localAppOpEntry.getSwitchText(this.mState));
          ((TextView)localView.findViewById(2131361959)).setText(localAppOpEntry.getTimeText(localResources, true));
          localObject3 = (Switch)localView.findViewById(2131361960);
          k = AppOpsManager.opToSwitch(localOpEntry.getOp());
          if (this.mAppOps.checkOp(k, localAppOpEntry.getPackageOps().getUid(), localAppOpEntry.getPackageOps().getPackageName()) != 0) {
            continue;
          }
          bool = true;
          ((Switch)localObject3).setChecked(bool);
          ((Switch)localObject3).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
          {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
            {
              paramAnonymousCompoundButton = AppOpsDetails.-get0(AppOpsDetails.this);
              int j = k;
              int k = localAppOpEntry.getPackageOps().getUid();
              String str = localAppOpEntry.getPackageOps().getPackageName();
              if (paramAnonymousBoolean) {}
              for (int i = 0;; i = 1)
              {
                paramAnonymousCompoundButton.setMode(j, k, str, i);
                return;
              }
            }
          });
          localObject4 = localObject1;
          break;
          localObject3 = localObject4;
          localObject4 = ((PermissionInfo)localObject5).group;
          localObject3 = localObject4;
          localObject5 = this.mPm.getPermissionGroupInfo(((PermissionInfo)localObject5).group, 0);
          localObject1 = localObject4;
          localObject3 = localObject4;
          if (((PermissionGroupInfo)localObject5).icon != 0)
          {
            localObject3 = localObject4;
            ((ImageView)localView.findViewById(2131361957)).setImageDrawable(((PermissionGroupInfo)localObject5).loadIcon(this.mPm));
            localObject1 = localObject4;
          }
        }
      }
      i += 1;
    }
    return true;
  }
  
  private String retrieveAppEntry()
  {
    Object localObject = getArguments();
    String str1;
    String str2;
    if (localObject != null)
    {
      str1 = ((Bundle)localObject).getString("package");
      str2 = str1;
      if (str1 == null) {
        if (localObject != null) {
          break label70;
        }
      }
    }
    label70:
    for (localObject = getActivity().getIntent();; localObject = (Intent)((Bundle)localObject).getParcelable("intent"))
    {
      str2 = str1;
      if (localObject != null) {
        str2 = ((Intent)localObject).getData().getSchemeSpecificPart();
      }
      try
      {
        this.mPackageInfo = this.mPm.getPackageInfo(str2, 8704);
        return str2;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("AppOpsDetails", "Exception when retrieving package:" + str2, localNameNotFoundException);
        this.mPackageInfo = null;
      }
      str1 = null;
      break;
    }
    return str2;
  }
  
  private void setAppLabelAndIcon(PackageInfo paramPackageInfo)
  {
    String str = null;
    View localView = this.mRootView.findViewById(2131361955);
    CharSequence localCharSequence = this.mPm.getApplicationLabel(paramPackageInfo.applicationInfo);
    Drawable localDrawable = this.mPm.getApplicationIcon(paramPackageInfo.applicationInfo);
    if (paramPackageInfo != null) {
      str = paramPackageInfo.versionName;
    }
    InstalledAppDetails.setupAppSnippet(localView, localCharSequence, localDrawable, str);
  }
  
  private void setIntentAndFinish(boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("chg", paramBoolean2);
    ((SettingsActivity)getActivity()).finishPreferencePanel(this, -1, localIntent);
  }
  
  protected int getMetricsCategory()
  {
    return 14;
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mState = new AppOpsState(getActivity());
    this.mPm = getActivity().getPackageManager();
    this.mInflater = ((LayoutInflater)getActivity().getSystemService("layout_inflater"));
    this.mAppOps = ((AppOpsManager)getActivity().getSystemService("appops"));
    retrieveAppEntry();
    setHasOptionsMenu(true);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130968615, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, paramLayoutInflater, paramLayoutInflater, false);
    this.mRootView = paramLayoutInflater;
    this.mOperationsSection = ((LinearLayout)paramLayoutInflater.findViewById(2131361956));
    return paramLayoutInflater;
  }
  
  public void onResume()
  {
    super.onResume();
    if (!refreshUi()) {
      setIntentAndFinish(true, true);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppOpsDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */