package com.oneplus.lib.preference;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreferenceGroupAdapter
  extends BaseAdapter
  implements Preference.OnPreferenceChangeInternalListener
{
  private static final String TAG = "PreferenceGroupAdapter";
  private static ViewGroup.LayoutParams sWrapperLayoutParams = new ViewGroup.LayoutParams(-1, -2);
  private Handler mHandler = new Handler();
  private boolean mHasReturnedViewTypeCount = false;
  private Drawable mHighlightedDrawable;
  private int mHighlightedPosition = -1;
  private volatile boolean mIsSyncing = false;
  private PreferenceGroup mPreferenceGroup;
  private ArrayList<PreferenceLayout> mPreferenceLayouts;
  private List<Preference> mPreferenceList;
  private Runnable mSyncRunnable = new Runnable()
  {
    public void run()
    {
      PreferenceGroupAdapter.-wrap0(PreferenceGroupAdapter.this);
    }
  };
  private PreferenceLayout mTempPreferenceLayout = new PreferenceLayout(null);
  
  public PreferenceGroupAdapter(PreferenceGroup paramPreferenceGroup)
  {
    this.mPreferenceGroup = paramPreferenceGroup;
    this.mPreferenceGroup.setOnPreferenceChangeInternalListener(this);
    this.mPreferenceList = new ArrayList();
    this.mPreferenceLayouts = new ArrayList();
    syncMyPreferences();
  }
  
  private void addPreferenceClassName(Preference paramPreference)
  {
    paramPreference = createPreferenceLayout(paramPreference, null);
    int i = Collections.binarySearch(this.mPreferenceLayouts, paramPreference);
    if (i < 0) {
      this.mPreferenceLayouts.add(i * -1 - 1, paramPreference);
    }
  }
  
  private PreferenceLayout createPreferenceLayout(Preference paramPreference, PreferenceLayout paramPreferenceLayout)
  {
    if (paramPreferenceLayout != null) {}
    for (;;)
    {
      PreferenceLayout.-set0(paramPreferenceLayout, paramPreference.getClass().getName());
      PreferenceLayout.-set1(paramPreferenceLayout, paramPreference.getLayoutResource());
      PreferenceLayout.-set2(paramPreferenceLayout, paramPreference.getWidgetLayoutResource());
      return paramPreferenceLayout;
      paramPreferenceLayout = new PreferenceLayout(null);
    }
  }
  
  private void flattenPreferenceGroup(List<Preference> paramList, PreferenceGroup paramPreferenceGroup)
  {
    paramPreferenceGroup.sortPreferences();
    int j = paramPreferenceGroup.getPreferenceCount();
    int i = 0;
    while (i < j)
    {
      Preference localPreference = paramPreferenceGroup.getPreference(i);
      paramList.add(localPreference);
      if ((!this.mHasReturnedViewTypeCount) && (localPreference.canRecycleLayout())) {
        addPreferenceClassName(localPreference);
      }
      if ((localPreference instanceof PreferenceGroup))
      {
        PreferenceGroup localPreferenceGroup = (PreferenceGroup)localPreference;
        if (localPreferenceGroup.isOnSameScreenAsChildren()) {
          flattenPreferenceGroup(paramList, localPreferenceGroup);
        }
      }
      localPreference.setOnPreferenceChangeInternalListener(this);
      i += 1;
    }
  }
  
  private int getHighlightItemViewType()
  {
    return getViewTypeCount() - 1;
  }
  
  /* Error */
  private void syncMyPreferences()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 66	com/oneplus/lib/preference/PreferenceGroupAdapter:mIsSyncing	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: iconst_1
    //   16: putfield 66	com/oneplus/lib/preference/PreferenceGroupAdapter:mIsSyncing	Z
    //   19: aload_0
    //   20: monitorexit
    //   21: new 87	java/util/ArrayList
    //   24: dup
    //   25: aload_0
    //   26: getfield 90	com/oneplus/lib/preference/PreferenceGroupAdapter:mPreferenceList	Ljava/util/List;
    //   29: invokeinterface 176 1 0
    //   34: invokespecial 179	java/util/ArrayList:<init>	(I)V
    //   37: astore_2
    //   38: aload_0
    //   39: aload_2
    //   40: aload_0
    //   41: getfield 79	com/oneplus/lib/preference/PreferenceGroupAdapter:mPreferenceGroup	Lcom/oneplus/lib/preference/PreferenceGroup;
    //   44: invokespecial 166	com/oneplus/lib/preference/PreferenceGroupAdapter:flattenPreferenceGroup	(Ljava/util/List;Lcom/oneplus/lib/preference/PreferenceGroup;)V
    //   47: aload_0
    //   48: aload_2
    //   49: putfield 90	com/oneplus/lib/preference/PreferenceGroupAdapter:mPreferenceList	Ljava/util/List;
    //   52: aload_0
    //   53: invokevirtual 182	com/oneplus/lib/preference/PreferenceGroupAdapter:notifyDataSetChanged	()V
    //   56: aload_0
    //   57: monitorenter
    //   58: aload_0
    //   59: iconst_0
    //   60: putfield 66	com/oneplus/lib/preference/PreferenceGroupAdapter:mIsSyncing	Z
    //   63: aload_0
    //   64: invokevirtual 185	com/oneplus/lib/preference/PreferenceGroupAdapter:notifyAll	()V
    //   67: aload_0
    //   68: monitorexit
    //   69: return
    //   70: astore_2
    //   71: aload_0
    //   72: monitorexit
    //   73: aload_2
    //   74: athrow
    //   75: astore_2
    //   76: aload_0
    //   77: monitorexit
    //   78: aload_2
    //   79: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	80	0	this	PreferenceGroupAdapter
    //   6	2	1	bool	boolean
    //   37	12	2	localArrayList	ArrayList
    //   70	4	2	localObject1	Object
    //   75	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	70	finally
    //   14	19	70	finally
    //   58	67	75	finally
  }
  
  public boolean areAllItemsEnabled()
  {
    return false;
  }
  
  public int getCount()
  {
    return this.mPreferenceList.size();
  }
  
  public Preference getItem(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getCount())) {
      return null;
    }
    return (Preference)this.mPreferenceList.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getCount())) {
      return Long.MIN_VALUE;
    }
    return getItem(paramInt).getId();
  }
  
  public int getItemViewType(int paramInt)
  {
    if (paramInt == this.mHighlightedPosition) {
      return getHighlightItemViewType();
    }
    if (!this.mHasReturnedViewTypeCount) {
      this.mHasReturnedViewTypeCount = true;
    }
    Preference localPreference = getItem(paramInt);
    if (!localPreference.canRecycleLayout()) {
      return -1;
    }
    this.mTempPreferenceLayout = createPreferenceLayout(localPreference, this.mTempPreferenceLayout);
    paramInt = Collections.binarySearch(this.mPreferenceLayouts, this.mTempPreferenceLayout);
    if (paramInt < 0) {
      return -1;
    }
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    Object localObject = getItem(paramInt);
    this.mTempPreferenceLayout = createPreferenceLayout((Preference)localObject, this.mTempPreferenceLayout);
    if ((Collections.binarySearch(this.mPreferenceLayouts, this.mTempPreferenceLayout) < 0) || (getItemViewType(paramInt) == getHighlightItemViewType())) {
      paramView = null;
    }
    localObject = ((Preference)localObject).getView(paramView, paramViewGroup);
    paramView = (View)localObject;
    if (paramInt == this.mHighlightedPosition)
    {
      paramView = (View)localObject;
      if (this.mHighlightedDrawable != null)
      {
        paramView = new FrameLayout(paramViewGroup.getContext());
        paramView.setLayoutParams(sWrapperLayoutParams);
        paramView.setBackgroundDrawable(this.mHighlightedDrawable);
        paramView.addView((View)localObject);
      }
    }
    return paramView;
  }
  
  public int getViewTypeCount()
  {
    if (!this.mHasReturnedViewTypeCount) {
      this.mHasReturnedViewTypeCount = true;
    }
    return Math.max(1, this.mPreferenceLayouts.size()) + 1;
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  public boolean isEnabled(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getCount())) {
      return true;
    }
    return getItem(paramInt).isSelectable();
  }
  
  public void onPreferenceChange(Preference paramPreference)
  {
    notifyDataSetChanged();
  }
  
  public void onPreferenceHierarchyChange(Preference paramPreference)
  {
    this.mHandler.removeCallbacks(this.mSyncRunnable);
    this.mHandler.post(this.mSyncRunnable);
  }
  
  public void setHighlighted(int paramInt)
  {
    this.mHighlightedPosition = paramInt;
  }
  
  public void setHighlightedDrawable(Drawable paramDrawable)
  {
    this.mHighlightedDrawable = paramDrawable;
  }
  
  private static class PreferenceLayout
    implements Comparable<PreferenceLayout>
  {
    private String name;
    private int resId;
    private int widgetResId;
    
    public int compareTo(PreferenceLayout paramPreferenceLayout)
    {
      int i = this.name.compareTo(paramPreferenceLayout.name);
      if (i == 0)
      {
        if (this.resId == paramPreferenceLayout.resId)
        {
          if (this.widgetResId == paramPreferenceLayout.widgetResId) {
            return 0;
          }
          return this.widgetResId - paramPreferenceLayout.widgetResId;
        }
        return this.resId - paramPreferenceLayout.resId;
      }
      return i;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\lib\preference\PreferenceGroupAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */