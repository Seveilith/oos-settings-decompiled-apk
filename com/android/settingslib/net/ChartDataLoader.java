package com.android.settingslib.net;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.INetworkStatsSession;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.settingslib.AppItem;

public class ChartDataLoader
  extends AsyncTaskLoader<ChartData>
{
  private static final String KEY_APP = "app";
  private static final String KEY_FIELDS = "fields";
  private static final String KEY_TEMPLATE = "template";
  private final Bundle mArgs;
  private final INetworkStatsSession mSession;
  
  public ChartDataLoader(Context paramContext, INetworkStatsSession paramINetworkStatsSession, Bundle paramBundle)
  {
    super(paramContext);
    this.mSession = paramINetworkStatsSession;
    this.mArgs = paramBundle;
  }
  
  public static Bundle buildArgs(NetworkTemplate paramNetworkTemplate, AppItem paramAppItem)
  {
    return buildArgs(paramNetworkTemplate, paramAppItem, 10);
  }
  
  public static Bundle buildArgs(NetworkTemplate paramNetworkTemplate, AppItem paramAppItem, int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("template", paramNetworkTemplate);
    localBundle.putParcelable("app", paramAppItem);
    localBundle.putInt("fields", paramInt);
    return localBundle;
  }
  
  private NetworkStatsHistory collectHistoryForUid(NetworkTemplate paramNetworkTemplate, int paramInt1, int paramInt2, NetworkStatsHistory paramNetworkStatsHistory)
    throws RemoteException
  {
    paramNetworkTemplate = this.mSession.getHistoryForUid(paramNetworkTemplate, paramInt1, paramInt2, 0, 10);
    if (paramNetworkStatsHistory != null)
    {
      paramNetworkStatsHistory.recordEntireHistory(paramNetworkTemplate);
      return paramNetworkStatsHistory;
    }
    return paramNetworkTemplate;
  }
  
  private ChartData loadInBackground(NetworkTemplate paramNetworkTemplate, AppItem paramAppItem, int paramInt)
    throws RemoteException
  {
    ChartData localChartData = new ChartData();
    try
    {
      localChartData.network = this.mSession.getHistoryForNetwork(paramNetworkTemplate, paramInt);
      if (paramAppItem != null)
      {
        i = paramAppItem.uids.size();
        paramInt = 0;
        while (paramInt < i)
        {
          int j = paramAppItem.uids.keyAt(paramInt);
          localChartData.detailDefault = collectHistoryForUid(paramNetworkTemplate, j, 0, localChartData.detailDefault);
          localChartData.detailForeground = collectHistoryForUid(paramNetworkTemplate, j, 1, localChartData.detailForeground);
          paramInt += 1;
        }
      }
    }
    catch (Exception localException)
    {
      int i;
      for (;;)
      {
        Log.e("ChartDataLoader", "mSession.getHistoryForNetwork error.");
        localException.printStackTrace();
      }
      if (i > 0)
      {
        localChartData.detail = new NetworkStatsHistory(localChartData.detailForeground.getBucketDuration());
        localChartData.detail.recordEntireHistory(localChartData.detailDefault);
        localChartData.detail.recordEntireHistory(localChartData.detailForeground);
        return localChartData;
      }
      localChartData.detailDefault = new NetworkStatsHistory(3600000L);
      localChartData.detailForeground = new NetworkStatsHistory(3600000L);
      localChartData.detail = new NetworkStatsHistory(3600000L);
    }
    return localChartData;
  }
  
  public ChartData loadInBackground()
  {
    Object localObject = (NetworkTemplate)this.mArgs.getParcelable("template");
    AppItem localAppItem = (AppItem)this.mArgs.getParcelable("app");
    int i = this.mArgs.getInt("fields");
    try
    {
      localObject = loadInBackground((NetworkTemplate)localObject, localAppItem, i);
      return (ChartData)localObject;
    }
    catch (RemoteException localRemoteException)
    {
      throw new RuntimeException("problem reading network stats", localRemoteException);
    }
  }
  
  protected void onReset()
  {
    super.onReset();
    cancelLoad();
  }
  
  protected void onStartLoading()
  {
    super.onStartLoading();
    forceLoad();
  }
  
  protected void onStopLoading()
  {
    super.onStopLoading();
    cancelLoad();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\net\ChartDataLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */