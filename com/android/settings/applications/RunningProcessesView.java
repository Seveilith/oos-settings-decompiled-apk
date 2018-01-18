package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.BidiFormatter;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.util.MemInfoReader;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class RunningProcessesView
  extends FrameLayout
  implements AdapterView.OnItemClickListener, AbsListView.RecyclerListener, RunningState.OnRefreshUiListener
{
  long SECONDARY_SERVER_MEM;
  final HashMap<View, ActiveItem> mActiveItems = new HashMap();
  ServiceListAdapter mAdapter;
  ActivityManager mAm;
  TextView mAppsProcessPrefix;
  TextView mAppsProcessText;
  TextView mBackgroundProcessPrefix;
  TextView mBackgroundProcessText;
  StringBuilder mBuilder = new StringBuilder(128);
  LinearColorBar mColorBar;
  Dialog mCurDialog;
  long mCurHighRam = -1L;
  long mCurLowRam = -1L;
  long mCurMedRam = -1L;
  RunningState.BaseItem mCurSelected;
  boolean mCurShowCached = false;
  long mCurTotalRam = -1L;
  Runnable mDataAvail;
  TextView mForegroundProcessPrefix;
  TextView mForegroundProcessText;
  View mHeader;
  ListView mListView;
  MemInfoReader mMemInfoReader = new MemInfoReader();
  final int mMyUserId = UserHandle.myUserId();
  Fragment mOwner;
  RunningState mState;
  
  public RunningProcessesView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void startServiceDetailsActivity(RunningState.MergedItem paramMergedItem)
  {
    if ((this.mOwner != null) && (paramMergedItem != null))
    {
      Bundle localBundle = new Bundle();
      if (paramMergedItem.mProcess != null)
      {
        localBundle.putInt("uid", paramMergedItem.mProcess.mUid);
        localBundle.putString("process", paramMergedItem.mProcess.mProcessName);
      }
      localBundle.putInt("user_id", paramMergedItem.mUserId);
      localBundle.putBoolean("background", this.mAdapter.mShowBackground);
      ((SettingsActivity)this.mOwner.getActivity()).startPreferencePanel(RunningServiceDetails.class.getName(), localBundle, 2131692208, null, null, 0);
    }
  }
  
  public void doCreate()
  {
    this.mAm = ((ActivityManager)getContext().getSystemService("activity"));
    this.mState = RunningState.getInstance(getContext());
    Object localObject = (LayoutInflater)getContext().getSystemService("layout_inflater");
    ((LayoutInflater)localObject).inflate(2130968961, this);
    this.mListView = ((ListView)findViewById(16908298));
    View localView = findViewById(16908292);
    if (localView != null) {
      this.mListView.setEmptyView(localView);
    }
    this.mListView.setOnItemClickListener(this);
    this.mListView.setRecyclerListener(this);
    this.mAdapter = new ServiceListAdapter(this.mState);
    this.mListView.setAdapter(this.mAdapter);
    this.mHeader = ((LayoutInflater)localObject).inflate(2130968959, null);
    this.mListView.addHeaderView(this.mHeader, null, false);
    this.mColorBar = ((LinearColorBar)this.mHeader.findViewById(2131362516));
    localObject = getContext();
    this.mColorBar.setColors(((Context)localObject).getColor(2131493676), Utils.getColorAccent((Context)localObject), ((Context)localObject).getColor(2131493678));
    this.mBackgroundProcessPrefix = ((TextView)this.mHeader.findViewById(2131362521));
    this.mAppsProcessPrefix = ((TextView)this.mHeader.findViewById(2131362519));
    this.mForegroundProcessPrefix = ((TextView)this.mHeader.findViewById(2131362517));
    this.mBackgroundProcessText = ((TextView)this.mHeader.findViewById(2131362522));
    this.mAppsProcessText = ((TextView)this.mHeader.findViewById(2131362520));
    this.mForegroundProcessText = ((TextView)this.mHeader.findViewById(2131362518));
    localObject = new ActivityManager.MemoryInfo();
    this.mAm.getMemoryInfo((ActivityManager.MemoryInfo)localObject);
    this.SECONDARY_SERVER_MEM = ((ActivityManager.MemoryInfo)localObject).secondaryServerThreshold;
  }
  
  public void doPause()
  {
    this.mState.pause();
    this.mDataAvail = null;
    this.mOwner = null;
  }
  
  public boolean doResume(Fragment paramFragment, Runnable paramRunnable)
  {
    this.mOwner = paramFragment;
    this.mState.resume(this);
    if (this.mState.hasData())
    {
      refreshUi(true);
      return true;
    }
    this.mDataAvail = paramRunnable;
    return false;
  }
  
  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    paramAdapterView = (RunningState.MergedItem)((ListView)paramAdapterView).getAdapter().getItem(paramInt);
    this.mCurSelected = paramAdapterView;
    startServiceDetailsActivity(paramAdapterView);
  }
  
  public void onMovedToScrapHeap(View paramView)
  {
    this.mActiveItems.remove(paramView);
  }
  
  public void onRefreshUi(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return;
    case 0: 
      updateTimes();
      return;
    case 1: 
      refreshUi(false);
      updateTimes();
      return;
    }
    refreshUi(true);
    updateTimes();
  }
  
  void refreshUi(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      ??? = this.mAdapter;
      ((ServiceListAdapter)???).refreshItems();
      ((ServiceListAdapter)???).notifyDataSetChanged();
    }
    if (this.mDataAvail != null)
    {
      this.mDataAvail.run();
      this.mDataAvail = null;
    }
    this.mMemInfoReader.readMemInfo();
    for (;;)
    {
      synchronized (this.mState.mLock)
      {
        if (this.mCurShowCached != this.mAdapter.mShowBackground)
        {
          this.mCurShowCached = this.mAdapter.mShowBackground;
          if (this.mCurShowCached)
          {
            this.mForegroundProcessPrefix.setText(getResources().getText(2131692205));
            this.mAppsProcessPrefix.setText(getResources().getText(2131692206));
          }
        }
        else
        {
          long l3 = this.mMemInfoReader.getTotalSize();
          if (!this.mCurShowCached) {
            break label425;
          }
          l1 = this.mMemInfoReader.getFreeSize() + this.mMemInfoReader.getCachedSize();
          l2 = this.mState.mBackgroundProcessMemory;
          long l4 = l3 - l2 - l1;
          if ((this.mCurTotalRam == l3) && (this.mCurHighRam == l4)) {
            break label461;
          }
          this.mCurTotalRam = l3;
          this.mCurHighRam = l4;
          this.mCurMedRam = l2;
          this.mCurLowRam = l1;
          Object localObject2 = BidiFormatter.getInstance();
          String str = ((BidiFormatter)localObject2).unicodeWrap(Formatter.formatShortFileSize(getContext(), l1));
          this.mBackgroundProcessText.setText(getResources().getString(2131692207, new Object[] { str }));
          str = ((BidiFormatter)localObject2).unicodeWrap(Formatter.formatShortFileSize(getContext(), l2));
          this.mAppsProcessText.setText(getResources().getString(2131692207, new Object[] { str }));
          localObject2 = ((BidiFormatter)localObject2).unicodeWrap(Formatter.formatShortFileSize(getContext(), l4));
          this.mForegroundProcessText.setText(getResources().getString(2131692207, new Object[] { localObject2 }));
          this.mColorBar.setRatios((float)l4 / (float)l3, (float)l2 / (float)l3, (float)l1 / (float)l3);
          return;
        }
        this.mForegroundProcessPrefix.setText(getResources().getText(2131692202));
        this.mAppsProcessPrefix.setText(getResources().getText(2131692203));
      }
      label425:
      long l1 = this.mMemInfoReader.getFreeSize() + this.mMemInfoReader.getCachedSize() + this.mState.mBackgroundProcessMemory;
      long l2 = this.mState.mServiceProcessMemory;
      continue;
      label461:
      if (this.mCurMedRam == l2)
      {
        long l5 = this.mCurLowRam;
        if (l5 == l1) {}
      }
    }
  }
  
  void updateTimes()
  {
    Iterator localIterator = this.mActiveItems.values().iterator();
    while (localIterator.hasNext())
    {
      ActiveItem localActiveItem = (ActiveItem)localIterator.next();
      if (localActiveItem.mRootView.getWindowToken() == null) {
        localIterator.remove();
      } else {
        localActiveItem.updateTime(getContext(), this.mBuilder);
      }
    }
  }
  
  public static class ActiveItem
  {
    long mFirstRunTime;
    RunningProcessesView.ViewHolder mHolder;
    RunningState.BaseItem mItem;
    View mRootView;
    ActivityManager.RunningServiceInfo mService;
    boolean mSetBackground;
    
    void updateTime(Context paramContext, StringBuilder paramStringBuilder)
    {
      Object localObject2 = null;
      Object localObject1;
      if ((this.mItem instanceof RunningState.ServiceItem)) {
        localObject1 = this.mHolder.size;
      }
      for (;;)
      {
        if (localObject1 != null)
        {
          this.mSetBackground = false;
          if (this.mFirstRunTime < 0L) {
            break;
          }
          ((TextView)localObject1).setText(DateUtils.formatElapsedTime(paramStringBuilder, (SystemClock.elapsedRealtime() - this.mFirstRunTime) / 1000L));
        }
        return;
        if (this.mItem.mSizeStr != null) {}
        for (localObject1 = this.mItem.mSizeStr;; localObject1 = "")
        {
          if (!((String)localObject1).equals(this.mItem.mCurSizeStr))
          {
            this.mItem.mCurSizeStr = ((String)localObject1);
            this.mHolder.size.setText((CharSequence)localObject1);
          }
          if (!this.mItem.mBackground) {
            break label170;
          }
          localObject1 = localObject2;
          if (this.mSetBackground) {
            break;
          }
          this.mSetBackground = true;
          this.mHolder.uptime.setText("");
          localObject1 = localObject2;
          break;
        }
        label170:
        localObject1 = localObject2;
        if ((this.mItem instanceof RunningState.MergedItem)) {
          localObject1 = this.mHolder.uptime;
        }
      }
      int i = 0;
      if ((this.mItem instanceof RunningState.MergedItem)) {
        if (((RunningState.MergedItem)this.mItem).mServices.size() <= 0) {
          break label245;
        }
      }
      label245:
      for (i = 1; i != 0; i = 0)
      {
        ((TextView)localObject1).setText(paramContext.getResources().getText(2131692186));
        return;
      }
      ((TextView)localObject1).setText("");
    }
  }
  
  class ServiceListAdapter
    extends BaseAdapter
  {
    final LayoutInflater mInflater;
    final ArrayList<RunningState.MergedItem> mItems = new ArrayList();
    ArrayList<RunningState.MergedItem> mOrigItems;
    boolean mShowBackground;
    final RunningState mState;
    
    ServiceListAdapter(RunningState paramRunningState)
    {
      this.mState = paramRunningState;
      this.mInflater = ((LayoutInflater)RunningProcessesView.this.getContext().getSystemService("layout_inflater"));
      refreshItems();
    }
    
    public boolean areAllItemsEnabled()
    {
      return false;
    }
    
    public void bindView(View paramView, int paramInt)
    {
      synchronized (this.mState.mLock)
      {
        int i = this.mItems.size();
        if (paramInt >= i) {
          return;
        }
        Object localObject2 = (RunningProcessesView.ViewHolder)paramView.getTag();
        RunningState.MergedItem localMergedItem = (RunningState.MergedItem)this.mItems.get(paramInt);
        localObject2 = ((RunningProcessesView.ViewHolder)localObject2).bind(this.mState, localMergedItem, RunningProcessesView.this.mBuilder);
        RunningProcessesView.this.mActiveItems.put(paramView, localObject2);
        return;
      }
    }
    
    public int getCount()
    {
      return this.mItems.size();
    }
    
    public Object getItem(int paramInt)
    {
      return this.mItems.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return ((RunningState.MergedItem)this.mItems.get(paramInt)).hashCode();
    }
    
    boolean getShowBackground()
    {
      return this.mShowBackground;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null) {
        paramView = newView(paramViewGroup);
      }
      for (;;)
      {
        bindView(paramView, paramInt);
        return paramView;
      }
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
    
    public boolean isEmpty()
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.mState.hasData())
      {
        bool1 = bool2;
        if (this.mItems.size() == 0) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public boolean isEnabled(int paramInt)
    {
      return !((RunningState.MergedItem)this.mItems.get(paramInt)).mIsProcess;
    }
    
    public View newView(ViewGroup paramViewGroup)
    {
      paramViewGroup = this.mInflater.inflate(2130968960, paramViewGroup, false);
      new RunningProcessesView.ViewHolder(paramViewGroup);
      return paramViewGroup;
    }
    
    void refreshItems()
    {
      ArrayList localArrayList;
      if (this.mShowBackground)
      {
        localArrayList = this.mState.getCurrentBackgroundItems();
        if (this.mOrigItems != localArrayList)
        {
          this.mOrigItems = localArrayList;
          if (localArrayList != null) {
            break label51;
          }
          this.mItems.clear();
        }
      }
      label51:
      do
      {
        return;
        localArrayList = this.mState.getCurrentMergedItems();
        break;
        this.mItems.clear();
        this.mItems.addAll(localArrayList);
      } while (!this.mShowBackground);
      Collections.sort(this.mItems, this.mState.mBackgroundComparator);
    }
    
    void setShowBackground(boolean paramBoolean)
    {
      if (this.mShowBackground != paramBoolean)
      {
        this.mShowBackground = paramBoolean;
        this.mState.setWatchingBackgroundItems(paramBoolean);
        refreshItems();
        RunningProcessesView.this.refreshUi(true);
      }
    }
  }
  
  static class TimeTicker
    extends TextView
  {
    public TimeTicker(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
  }
  
  public static class ViewHolder
  {
    public TextView description;
    public ImageView icon;
    public TextView name;
    public View rootView;
    public TextView size;
    public TextView uptime;
    
    public ViewHolder(View paramView)
    {
      this.rootView = paramView;
      this.icon = ((ImageView)paramView.findViewById(2131361793));
      this.name = ((TextView)paramView.findViewById(2131362120));
      this.description = ((TextView)paramView.findViewById(2131362121));
      this.size = ((TextView)paramView.findViewById(2131362524));
      this.uptime = ((TextView)paramView.findViewById(2131362525));
      paramView.setTag(this);
    }
    
    public RunningProcessesView.ActiveItem bind(RunningState paramRunningState, RunningState.BaseItem paramBaseItem, StringBuilder paramStringBuilder)
    {
      synchronized (paramRunningState.mLock)
      {
        Object localObject2 = this.rootView.getContext().getPackageManager();
        if ((paramBaseItem.mPackageInfo == null) && ((paramBaseItem instanceof RunningState.MergedItem)) && (((RunningState.MergedItem)paramBaseItem).mProcess != null))
        {
          ((RunningState.MergedItem)paramBaseItem).mProcess.ensureLabel((PackageManager)localObject2);
          paramBaseItem.mPackageInfo = ((RunningState.MergedItem)paramBaseItem).mProcess.mPackageInfo;
          paramBaseItem.mDisplayLabel = ((RunningState.MergedItem)paramBaseItem).mProcess.mDisplayLabel;
        }
        this.name.setText(paramBaseItem.mDisplayLabel);
        localObject2 = new RunningProcessesView.ActiveItem();
        ((RunningProcessesView.ActiveItem)localObject2).mRootView = this.rootView;
        ((RunningProcessesView.ActiveItem)localObject2).mItem = paramBaseItem;
        ((RunningProcessesView.ActiveItem)localObject2).mHolder = this;
        ((RunningProcessesView.ActiveItem)localObject2).mFirstRunTime = paramBaseItem.mActiveSince;
        if (paramBaseItem.mBackground)
        {
          this.description.setText(this.rootView.getContext().getText(2131692187));
          paramBaseItem.mCurSizeStr = null;
          this.icon.setImageDrawable(paramBaseItem.loadIcon(this.rootView.getContext(), paramRunningState));
          this.icon.setVisibility(0);
          ((RunningProcessesView.ActiveItem)localObject2).updateTime(this.rootView.getContext(), paramStringBuilder);
          return (RunningProcessesView.ActiveItem)localObject2;
        }
        this.description.setText(paramBaseItem.mDescription);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\RunningProcessesView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */