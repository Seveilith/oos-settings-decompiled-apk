package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class AppGridView
  extends GridView
{
  private static final int MAX_APP_COUNT = 30;
  
  public AppGridView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AppGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AppGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setNumColumns(-1);
    setColumnWidth(getResources().getDimensionPixelSize(2131755605));
    setAdapter(new AppsAdapter(paramContext, 2130968969, 16908308, 16908295));
  }
  
  private static class ActivityEntry
    implements Comparable<ActivityEntry>
  {
    public final ResolveInfo info;
    public final String label;
    
    public ActivityEntry(ResolveInfo paramResolveInfo, String paramString)
    {
      this.info = paramResolveInfo;
      this.label = paramString;
    }
    
    public int compareTo(ActivityEntry paramActivityEntry)
    {
      return this.label.compareToIgnoreCase(paramActivityEntry.label);
    }
    
    public String toString()
    {
      return this.label;
    }
  }
  
  private static class AppsAdapter
    extends ArrayAdapter<AppGridView.ActivityEntry>
  {
    private final int mIconResId;
    private final PackageManager mPackageManager;
    
    public AppsAdapter(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt1, paramInt2);
      this.mIconResId = paramInt3;
      this.mPackageManager = paramContext.getPackageManager();
      loadAllApps();
    }
    
    private void loadAllApps()
    {
      Object localObject = new Intent("android.intent.action.MAIN", null);
      ((Intent)localObject).addCategory("android.intent.category.LAUNCHER");
      PackageManager localPackageManager = this.mPackageManager;
      ArrayList localArrayList = new ArrayList();
      localObject = localPackageManager.queryIntentActivities((Intent)localObject, 0).iterator();
      do
      {
        if (!((Iterator)localObject).hasNext()) {
          break;
        }
        ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject).next();
        CharSequence localCharSequence = localResolveInfo.loadLabel(localPackageManager);
        if (localCharSequence != null) {
          localArrayList.add(new AppGridView.ActivityEntry(localResolveInfo, localCharSequence.toString()));
        }
      } while (localArrayList.size() != 30);
      Collections.sort(localArrayList);
      addAll(localArrayList);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = super.getView(paramInt, paramView, paramViewGroup);
      paramViewGroup = (AppGridView.ActivityEntry)getItem(paramInt);
      ((ImageView)paramView.findViewById(this.mIconResId)).setImageDrawable(paramViewGroup.info.loadIcon(this.mPackageManager));
      return paramView;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return false;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\display\AppGridView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */