package com.android.settingslib.net;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.INetworkStatsSession;
import android.net.NetworkStats;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.RemoteException;

public class SummaryForAllUidLoader
  extends AsyncTaskLoader<NetworkStats>
{
  private static final String KEY_END = "end";
  private static final String KEY_START = "start";
  private static final String KEY_TEMPLATE = "template";
  private final Bundle mArgs;
  private final INetworkStatsSession mSession;
  
  public SummaryForAllUidLoader(Context paramContext, INetworkStatsSession paramINetworkStatsSession, Bundle paramBundle)
  {
    super(paramContext);
    this.mSession = paramINetworkStatsSession;
    this.mArgs = paramBundle;
  }
  
  public static Bundle buildArgs(NetworkTemplate paramNetworkTemplate, long paramLong1, long paramLong2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("template", paramNetworkTemplate);
    localBundle.putLong("start", paramLong1);
    localBundle.putLong("end", paramLong2);
    return localBundle;
  }
  
  public NetworkStats loadInBackground()
  {
    Object localObject = (NetworkTemplate)this.mArgs.getParcelable("template");
    long l1 = this.mArgs.getLong("start");
    long l2 = this.mArgs.getLong("end");
    try
    {
      localObject = this.mSession.getSummaryForAllUid((NetworkTemplate)localObject, l1, l2, false);
      return (NetworkStats)localObject;
    }
    catch (RemoteException localRemoteException) {}
    return null;
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settingslib\net\SummaryForAllUidLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */