package com.android.settings;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.applications.AppViewHolder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppPicker
  extends ListActivity
{
  public static final String EXTRA_DEBUGGABLE = "com.android.settings.extra.DEBUGGABLE";
  public static final String EXTRA_REQUESTIING_PERMISSION = "com.android.settings.extra.REQUESTIING_PERMISSION";
  private static final Comparator<MyApplicationInfo> sDisplayNameComparator = new Comparator()
  {
    private final Collator collator = Collator.getInstance();
    
    public final int compare(AppPicker.MyApplicationInfo paramAnonymousMyApplicationInfo1, AppPicker.MyApplicationInfo paramAnonymousMyApplicationInfo2)
    {
      return this.collator.compare(paramAnonymousMyApplicationInfo1.label, paramAnonymousMyApplicationInfo2.label);
    }
  };
  private AppListAdapter mAdapter;
  private boolean mDebuggableOnly;
  private String mPermissionName;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPermissionName = getIntent().getStringExtra("com.android.settings.extra.REQUESTIING_PERMISSION");
    this.mDebuggableOnly = getIntent().getBooleanExtra("com.android.settings.extra.DEBUGGABLE", false);
    this.mAdapter = new AppListAdapter(this);
    if (this.mAdapter.getCount() <= 0)
    {
      finish();
      return;
    }
    setListAdapter(this.mAdapter);
  }
  
  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    paramListView = (MyApplicationInfo)this.mAdapter.getItem(paramInt);
    paramView = new Intent();
    if (paramListView.info != null) {
      paramView.setAction(paramListView.info.packageName);
    }
    setResult(-1, paramView);
    finish();
  }
  
  protected void onResume()
  {
    super.onResume();
  }
  
  protected void onStop()
  {
    super.onStop();
  }
  
  public class AppListAdapter
    extends ArrayAdapter<AppPicker.MyApplicationInfo>
  {
    private final LayoutInflater mInflater;
    private final List<AppPicker.MyApplicationInfo> mPackageInfoList = new ArrayList();
    
    public AppListAdapter(Context paramContext)
    {
      super(0);
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      List localList = paramContext.getPackageManager().getInstalledApplications(0);
      int i = 0;
      ApplicationInfo localApplicationInfo;
      if (i < localList.size())
      {
        localApplicationInfo = (ApplicationInfo)localList.get(i);
        if (localApplicationInfo.uid != 1000) {}
      }
      for (;;)
      {
        i += 1;
        break;
        if ((!AppPicker.-get0(AppPicker.this)) || ((localApplicationInfo.flags & 0x2) != 0) || (!"user".equals(Build.TYPE)))
        {
          int m;
          if (AppPicker.-get1(AppPicker.this) != null) {
            m = 0;
          }
          try
          {
            Object localObject = AppPicker.this.getPackageManager().getPackageInfo(localApplicationInfo.packageName, 4096);
            if (((PackageInfo)localObject).requestedPermissions != null)
            {
              localObject = ((PackageInfo)localObject).requestedPermissions;
              int n = localObject.length;
              int j = 0;
              for (;;)
              {
                int k = m;
                if (j < n)
                {
                  boolean bool = localObject[j].equals(AppPicker.-get1(AppPicker.this));
                  if (bool) {
                    k = 1;
                  }
                }
                else
                {
                  if (k == 0) {
                    break;
                  }
                  localObject = new AppPicker.MyApplicationInfo(AppPicker.this);
                  ((AppPicker.MyApplicationInfo)localObject).info = localApplicationInfo;
                  ((AppPicker.MyApplicationInfo)localObject).label = ((AppPicker.MyApplicationInfo)localObject).info.loadLabel(AppPicker.this.getPackageManager()).toString();
                  this.mPackageInfoList.add(localObject);
                  break;
                }
                j += 1;
              }
              Collections.sort(this.mPackageInfoList, AppPicker.-get2());
              this$1 = new AppPicker.MyApplicationInfo(AppPicker.this);
              AppPicker.this.label = paramContext.getText(2131689690);
              this.mPackageInfoList.add(0, AppPicker.this);
              addAll(this.mPackageInfoList);
              return;
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
        }
      }
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = AppViewHolder.createOrRecycle(this.mInflater, paramView);
      paramViewGroup = paramView.rootView;
      AppPicker.MyApplicationInfo localMyApplicationInfo = (AppPicker.MyApplicationInfo)getItem(paramInt);
      paramView.appName.setText(localMyApplicationInfo.label);
      if (localMyApplicationInfo.info != null)
      {
        paramView.appIcon.setImageDrawable(localMyApplicationInfo.info.loadIcon(AppPicker.this.getPackageManager()));
        paramView.summary.setText(localMyApplicationInfo.info.packageName);
      }
      for (;;)
      {
        paramView.disabled.setVisibility(8);
        return paramViewGroup;
        paramView.appIcon.setImageDrawable(null);
        paramView.summary.setText("");
      }
    }
  }
  
  class MyApplicationInfo
  {
    ApplicationInfo info;
    CharSequence label;
    
    MyApplicationInfo() {}
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AppPicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */