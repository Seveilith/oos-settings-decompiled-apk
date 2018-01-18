package com.android.settings;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class UsageStatsActivity
  extends Activity
  implements AdapterView.OnItemSelectedListener
{
  private static final String TAG = "UsageStatsActivity";
  private static final boolean localLOGV = false;
  private UsageStatsAdapter mAdapter;
  private LayoutInflater mInflater;
  private PackageManager mPm;
  private UsageStatsManager mUsageStatsManager;
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130969090);
    this.mUsageStatsManager = ((UsageStatsManager)getSystemService("usagestats"));
    this.mInflater = ((LayoutInflater)getSystemService("layout_inflater"));
    this.mPm = getPackageManager();
    ((Spinner)findViewById(2131362646)).setOnItemSelectedListener(this);
    paramBundle = (ListView)findViewById(2131362647);
    this.mAdapter = new UsageStatsAdapter();
    paramBundle.setAdapter(this.mAdapter);
  }
  
  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    this.mAdapter.sortList(paramInt);
  }
  
  public void onNothingSelected(AdapterView<?> paramAdapterView) {}
  
  public static class AppNameComparator
    implements Comparator<UsageStats>
  {
    private Map<String, String> mAppLabelList;
    
    AppNameComparator(Map<String, String> paramMap)
    {
      this.mAppLabelList = paramMap;
    }
    
    public final int compare(UsageStats paramUsageStats1, UsageStats paramUsageStats2)
    {
      return ((String)this.mAppLabelList.get(paramUsageStats1.getPackageName())).compareTo((String)this.mAppLabelList.get(paramUsageStats2.getPackageName()));
    }
  }
  
  static class AppViewHolder
  {
    TextView lastTimeUsed;
    TextView pkgName;
    TextView usageTime;
  }
  
  public static class LastTimeUsedComparator
    implements Comparator<UsageStats>
  {
    public final int compare(UsageStats paramUsageStats1, UsageStats paramUsageStats2)
    {
      return (int)(paramUsageStats2.getLastTimeUsed() - paramUsageStats1.getLastTimeUsed());
    }
  }
  
  class UsageStatsAdapter
    extends BaseAdapter
  {
    private static final int _DISPLAY_ORDER_APP_NAME = 2;
    private static final int _DISPLAY_ORDER_LAST_TIME_USED = 1;
    private static final int _DISPLAY_ORDER_USAGE_TIME = 0;
    private UsageStatsActivity.AppNameComparator mAppLabelComparator;
    private final ArrayMap<String, String> mAppLabelMap = new ArrayMap();
    private int mDisplayOrder = 0;
    private UsageStatsActivity.LastTimeUsedComparator mLastTimeUsedComparator = new UsageStatsActivity.LastTimeUsedComparator();
    private final ArrayList<UsageStats> mPackageStats = new ArrayList();
    private UsageStatsActivity.UsageTimeComparator mUsageTimeComparator = new UsageStatsActivity.UsageTimeComparator();
    
    UsageStatsAdapter()
    {
      Object localObject1 = Calendar.getInstance();
      ((Calendar)localObject1).add(6, -5);
      localObject1 = UsageStatsActivity.-get2(UsageStatsActivity.this).queryUsageStats(4, ((Calendar)localObject1).getTimeInMillis(), System.currentTimeMillis());
      if (localObject1 == null) {
        return;
      }
      ArrayMap localArrayMap = new ArrayMap();
      int j = ((List)localObject1).size();
      int i = 0;
      for (;;)
      {
        if (i < j)
        {
          UsageStats localUsageStats = (UsageStats)((List)localObject1).get(i);
          try
          {
            Object localObject2 = UsageStatsActivity.-get1(UsageStatsActivity.this).getApplicationInfo(localUsageStats.getPackageName(), 0).loadLabel(UsageStatsActivity.-get1(UsageStatsActivity.this)).toString();
            this.mAppLabelMap.put(localUsageStats.getPackageName(), localObject2);
            localObject2 = (UsageStats)localArrayMap.get(localUsageStats.getPackageName());
            if (localObject2 == null) {
              localArrayMap.put(localUsageStats.getPackageName(), localUsageStats);
            } else {
              ((UsageStats)localObject2).add(localUsageStats);
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
        }
        this.mPackageStats.addAll(localArrayMap.values());
        this.mAppLabelComparator = new UsageStatsActivity.AppNameComparator(this.mAppLabelMap);
        sortList();
        return;
        i += 1;
      }
    }
    
    private void sortList()
    {
      System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
      if (this.mDisplayOrder == 0) {
        Collections.sort(this.mPackageStats, this.mUsageTimeComparator);
      }
      for (;;)
      {
        notifyDataSetChanged();
        return;
        if (this.mDisplayOrder == 1) {
          Collections.sort(this.mPackageStats, this.mLastTimeUsedComparator);
        } else if (this.mDisplayOrder == 2) {
          Collections.sort(this.mPackageStats, this.mAppLabelComparator);
        }
      }
    }
    
    public int getCount()
    {
      return this.mPackageStats.size();
    }
    
    public Object getItem(int paramInt)
    {
      return this.mPackageStats.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
      {
        paramView = UsageStatsActivity.-get0(UsageStatsActivity.this).inflate(2130969091, null);
        paramViewGroup = new UsageStatsActivity.AppViewHolder();
        paramViewGroup.pkgName = ((TextView)paramView.findViewById(2131362648));
        paramViewGroup.lastTimeUsed = ((TextView)paramView.findViewById(2131362649));
        paramViewGroup.usageTime = ((TextView)paramView.findViewById(2131362650));
        paramView.setTag(paramViewGroup);
      }
      for (;;)
      {
        UsageStats localUsageStats = (UsageStats)this.mPackageStats.get(paramInt);
        if (localUsageStats == null) {
          break;
        }
        String str = (String)this.mAppLabelMap.get(localUsageStats.getPackageName());
        paramViewGroup.pkgName.setText(str);
        paramViewGroup.lastTimeUsed.setText(DateUtils.formatSameDayTime(localUsageStats.getLastTimeUsed(), System.currentTimeMillis(), 2, 2));
        paramViewGroup.usageTime.setText(DateUtils.formatElapsedTime(localUsageStats.getTotalTimeInForeground() / 1000L));
        return paramView;
        paramViewGroup = (UsageStatsActivity.AppViewHolder)paramView.getTag();
      }
      Log.w("UsageStatsActivity", "No usage stats info for package:" + paramInt);
      return paramView;
    }
    
    void sortList(int paramInt)
    {
      if (this.mDisplayOrder == paramInt) {
        return;
      }
      this.mDisplayOrder = paramInt;
      sortList();
    }
  }
  
  public static class UsageTimeComparator
    implements Comparator<UsageStats>
  {
    public final int compare(UsageStats paramUsageStats1, UsageStats paramUsageStats2)
    {
      return (int)(paramUsageStats2.getTotalTimeInForeground() - paramUsageStats1.getTotalTimeInForeground());
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\UsageStatsActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */