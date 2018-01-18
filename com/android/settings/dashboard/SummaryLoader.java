package com.android.settings.dashboard;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SummaryLoader
{
  private static final boolean DEBUG = false;
  public static final String SUMMARY_PROVIDER_FACTORY = "SUMMARY_PROVIDER_FACTORY";
  private static final String TAG = "SummaryLoader";
  private final Activity mActivity;
  private DashboardAdapter mAdapter;
  private final Handler mHandler = new Handler();
  private boolean mListening;
  private ArraySet<BroadcastReceiver> mReceivers = new ArraySet();
  private final ArrayMap<SummaryProvider, ComponentName> mSummaryMap = new ArrayMap();
  private final List<Tile> mTiles = new ArrayList();
  private final Worker mWorker;
  private boolean mWorkerListening;
  private final HandlerThread mWorkerThread = new HandlerThread("SummaryLoader", 10);
  
  public SummaryLoader(Activity paramActivity, List<DashboardCategory> paramList)
  {
    this.mWorkerThread.start();
    this.mWorker = new Worker(this.mWorkerThread.getLooper());
    this.mActivity = paramActivity;
    int i = 0;
    while (i < paramList.size())
    {
      paramActivity = ((DashboardCategory)paramList.get(i)).tiles;
      int j = 0;
      while (j < paramActivity.size())
      {
        Tile localTile = (Tile)paramActivity.get(j);
        this.mWorker.obtainMessage(1, localTile).sendToTarget();
        j += 1;
      }
      i += 1;
    }
  }
  
  private Bundle getMetaData(Tile paramTile)
  {
    return paramTile.metaData;
  }
  
  private SummaryProvider getSummaryProvider(Tile paramTile)
  {
    if (!this.mActivity.getPackageName().equals(paramTile.intent.getComponent().getPackageName())) {
      return null;
    }
    paramTile = getMetaData(paramTile);
    if (paramTile == null) {
      return null;
    }
    paramTile = paramTile.getString("com.android.settings.FRAGMENT_CLASS");
    if (paramTile == null) {
      return null;
    }
    try
    {
      paramTile = ((SummaryProviderFactory)Class.forName(paramTile).getField("SUMMARY_PROVIDER_FACTORY").get(null)).createSummaryProvider(this.mActivity, this);
      return paramTile;
    }
    catch (ClassNotFoundException paramTile)
    {
      return null;
    }
    catch (NoSuchFieldException paramTile)
    {
      return null;
    }
    catch (ClassCastException paramTile)
    {
      return null;
    }
    catch (IllegalAccessException paramTile) {}
    return null;
  }
  
  private void makeProviderW(Tile paramTile)
  {
    try
    {
      SummaryProvider localSummaryProvider = getSummaryProvider(paramTile);
      if (localSummaryProvider != null) {
        this.mSummaryMap.put(localSummaryProvider, paramTile.intent.getComponent());
      }
      return;
    }
    finally {}
  }
  
  private void setListeningW(boolean paramBoolean)
  {
    try
    {
      boolean bool = this.mWorkerListening;
      if (bool == paramBoolean) {
        return;
      }
      this.mWorkerListening = paramBoolean;
      Iterator localIterator = this.mSummaryMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        SummaryProvider localSummaryProvider = (SummaryProvider)localIterator.next();
        try
        {
          localSummaryProvider.setListening(paramBoolean);
        }
        catch (Exception localException)
        {
          Log.d("SummaryLoader", "Problem in setListening", localException);
        }
      }
    }
    finally {}
  }
  
  public void registerReceiver(final BroadcastReceiver paramBroadcastReceiver, final IntentFilter paramIntentFilter)
  {
    this.mActivity.runOnUiThread(new Runnable()
    {
      public void run()
      {
        if (!SummaryLoader.-get2(SummaryLoader.this)) {
          return;
        }
        SummaryLoader.-get3(SummaryLoader.this).add(paramBroadcastReceiver);
        try
        {
          SummaryLoader.-get0(SummaryLoader.this).registerReceiver(paramBroadcastReceiver, paramIntentFilter);
          return;
        }
        catch (Exception localException)
        {
          Log.d("SummaryLoader", "Problem in registerReceiver", localException);
        }
      }
    });
  }
  
  public void release()
  {
    this.mWorkerThread.quitSafely();
    new Thread(new Runnable()
    {
      public void run()
      {
        SummaryLoader.-wrap1(SummaryLoader.this, false);
      }
    }).start();
  }
  
  public void setAdapter(DashboardAdapter paramDashboardAdapter)
  {
    this.mAdapter = paramDashboardAdapter;
  }
  
  public void setListening(boolean paramBoolean)
  {
    if (this.mListening == paramBoolean) {
      return;
    }
    this.mListening = paramBoolean;
    int i = 0;
    Worker localWorker;
    try
    {
      while (i < this.mReceivers.size())
      {
        this.mActivity.unregisterReceiver((BroadcastReceiver)this.mReceivers.valueAt(i));
        i += 1;
      }
      i = 1;
    }
    catch (Exception localException)
    {
      Log.d("SummaryLoader", "Problem in unregisterReceiver", localException);
      this.mReceivers.clear();
      this.mWorker.removeMessages(2);
      localWorker = this.mWorker;
      if (!paramBoolean) {}
    }
    for (;;)
    {
      localWorker.obtainMessage(2, i, 0).sendToTarget();
      return;
      i = 0;
    }
  }
  
  public void setSummary(final SummaryProvider paramSummaryProvider, final CharSequence paramCharSequence)
  {
    paramSummaryProvider = (ComponentName)this.mSummaryMap.get(paramSummaryProvider);
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        Tile localTile = SummaryLoader.-get1(SummaryLoader.this).getTile(paramSummaryProvider);
        if (localTile == null) {
          return;
        }
        localTile.summary = paramCharSequence;
        SummaryLoader.-get1(SummaryLoader.this).notifyChanged(localTile);
      }
    });
  }
  
  public static abstract interface SummaryProvider
  {
    public abstract void setListening(boolean paramBoolean);
  }
  
  public static abstract interface SummaryProviderFactory
  {
    public abstract SummaryLoader.SummaryProvider createSummaryProvider(Activity paramActivity, SummaryLoader paramSummaryLoader);
  }
  
  private class Worker
    extends Handler
  {
    private static final int MSG_GET_PROVIDER = 1;
    private static final int MSG_SET_LISTENING = 2;
    
    public Worker(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        paramMessage = (Tile)paramMessage.obj;
        SummaryLoader.-wrap0(SummaryLoader.this, paramMessage);
        return;
      }
      if (paramMessage.arg1 != 0) {}
      for (boolean bool = true;; bool = false)
      {
        SummaryLoader.-wrap1(SummaryLoader.this, bool);
        return;
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\dashboard\SummaryLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */