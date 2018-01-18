package com.android.settings.datausage;

import android.content.Context;
import android.net.INetworkPolicyListener;
import android.net.INetworkPolicyListener.Stub;
import android.net.INetworkPolicyManager;
import android.net.INetworkPolicyManager.Stub;
import android.net.NetworkPolicyManager;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.internal.logging.MetricsLogger;
import java.util.ArrayList;

public class DataSaverBackend
{
  private static final String TAG = "DataSaverBackend";
  private SparseBooleanArray mBlacklist;
  private final Context mContext;
  private final Handler mHandler = new Handler();
  private final INetworkPolicyManager mIPolicyManager;
  private final ArrayList<Listener> mListeners = new ArrayList();
  private final INetworkPolicyListener mPolicyListener = new INetworkPolicyListener.Stub()
  {
    public void onMeteredIfacesChanged(String[] paramAnonymousArrayOfString)
      throws RemoteException
    {}
    
    public void onRestrictBackgroundBlacklistChanged(final int paramAnonymousInt, final boolean paramAnonymousBoolean)
    {
      if (DataSaverBackend.-get0(DataSaverBackend.this) == null) {
        DataSaverBackend.-wrap3(DataSaverBackend.this);
      }
      DataSaverBackend.-get0(DataSaverBackend.this).put(paramAnonymousInt, paramAnonymousBoolean);
      DataSaverBackend.-get1(DataSaverBackend.this).post(new Runnable()
      {
        public void run()
        {
          DataSaverBackend.-wrap0(DataSaverBackend.this, paramAnonymousInt, paramAnonymousBoolean);
        }
      });
    }
    
    public void onRestrictBackgroundChanged(final boolean paramAnonymousBoolean)
      throws RemoteException
    {
      DataSaverBackend.-get1(DataSaverBackend.this).post(new Runnable()
      {
        public void run()
        {
          DataSaverBackend.-wrap1(DataSaverBackend.this, paramAnonymousBoolean);
        }
      });
    }
    
    public void onRestrictBackgroundWhitelistChanged(final int paramAnonymousInt, final boolean paramAnonymousBoolean)
    {
      if (DataSaverBackend.-get2(DataSaverBackend.this) == null) {
        DataSaverBackend.-wrap4(DataSaverBackend.this);
      }
      DataSaverBackend.-get2(DataSaverBackend.this).put(paramAnonymousInt, paramAnonymousBoolean);
      DataSaverBackend.-get1(DataSaverBackend.this).post(new Runnable()
      {
        public void run()
        {
          DataSaverBackend.-wrap2(DataSaverBackend.this, paramAnonymousInt, paramAnonymousBoolean);
        }
      });
    }
    
    public void onUidRulesChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      throws RemoteException
    {}
  };
  private final NetworkPolicyManager mPolicyManager;
  private SparseBooleanArray mWhitelist;
  
  public DataSaverBackend(Context paramContext)
  {
    this.mContext = paramContext;
    this.mIPolicyManager = INetworkPolicyManager.Stub.asInterface(ServiceManager.getService("netpolicy"));
    this.mPolicyManager = NetworkPolicyManager.from(paramContext);
  }
  
  private void handleBlacklistChanged(int paramInt, boolean paramBoolean)
  {
    int i = 0;
    while (i < this.mListeners.size())
    {
      ((Listener)this.mListeners.get(i)).onBlacklistStatusChanged(paramInt, paramBoolean);
      i += 1;
    }
  }
  
  private void handleRestrictBackgroundChanged(boolean paramBoolean)
  {
    int i = 0;
    while (i < this.mListeners.size())
    {
      ((Listener)this.mListeners.get(i)).onDataSaverChanged(paramBoolean);
      i += 1;
    }
  }
  
  private void handleWhitelistChanged(int paramInt, boolean paramBoolean)
  {
    int i = 0;
    while (i < this.mListeners.size())
    {
      ((Listener)this.mListeners.get(i)).onWhitelistStatusChanged(paramInt, paramBoolean);
      i += 1;
    }
  }
  
  private void loadBlacklist()
  {
    this.mBlacklist = new SparseBooleanArray();
    try
    {
      int[] arrayOfInt = this.mIPolicyManager.getUidsWithPolicy(1);
      int i = 0;
      int j = arrayOfInt.length;
      while (i < j)
      {
        int k = arrayOfInt[i];
        this.mBlacklist.put(k, true);
        i += 1;
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  private void loadWhitelist()
  {
    this.mWhitelist = new SparseBooleanArray();
    try
    {
      int[] arrayOfInt = this.mIPolicyManager.getRestrictBackgroundWhitelistedUids();
      int i = 0;
      int j = arrayOfInt.length;
      while (i < j)
      {
        int k = arrayOfInt[i];
        this.mWhitelist.put(k, true);
        i += 1;
      }
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void addListener(Listener paramListener)
  {
    this.mListeners.add(paramListener);
    if (this.mListeners.size() == 1) {
      this.mPolicyManager.registerListener(this.mPolicyListener);
    }
    paramListener.onDataSaverChanged(isDataSaverEnabled());
  }
  
  public int getWhitelistedCount()
  {
    int j = 0;
    if (this.mWhitelist == null) {
      loadWhitelist();
    }
    int i = 0;
    while (i < this.mWhitelist.size())
    {
      int k = j;
      if (this.mWhitelist.valueAt(i)) {
        k = j + 1;
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  public boolean isBlacklisted(int paramInt)
  {
    if (this.mBlacklist == null) {
      loadBlacklist();
    }
    return this.mBlacklist.get(paramInt);
  }
  
  public boolean isDataSaverEnabled()
  {
    return this.mPolicyManager.getRestrictBackground();
  }
  
  public boolean isWhitelisted(int paramInt)
  {
    if (this.mWhitelist == null) {
      loadWhitelist();
    }
    return this.mWhitelist.get(paramInt);
  }
  
  public void refreshBlacklist()
  {
    loadBlacklist();
  }
  
  public void refreshWhitelist()
  {
    loadWhitelist();
  }
  
  public void remListener(Listener paramListener)
  {
    this.mListeners.remove(paramListener);
    if (this.mListeners.size() == 0) {
      this.mPolicyManager.unregisterListener(this.mPolicyListener);
    }
  }
  
  public void setDataSaverEnabled(boolean paramBoolean)
  {
    this.mPolicyManager.setRestrictBackground(paramBoolean);
    Context localContext = this.mContext;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      MetricsLogger.action(localContext, 394, i);
      return;
    }
  }
  
  public void setIsBlacklisted(int paramInt, String paramString, boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        NetworkPolicyManager localNetworkPolicyManager = this.mPolicyManager;
        if (!paramBoolean) {
          continue;
        }
        i = 1;
        localNetworkPolicyManager.setUidPolicy(paramInt, i);
      }
      catch (Exception localException)
      {
        int i;
        localException.printStackTrace();
        continue;
      }
      if (paramBoolean) {
        MetricsLogger.action(this.mContext, 396, paramString);
      }
      return;
      i = 0;
    }
  }
  
  public void setIsWhitelisted(int paramInt, String paramString, boolean paramBoolean)
  {
    this.mWhitelist.put(paramInt, paramBoolean);
    if (paramBoolean) {}
    try
    {
      this.mIPolicyManager.addRestrictBackgroundWhitelistedUid(paramInt);
      for (;;)
      {
        if (paramBoolean) {
          MetricsLogger.action(this.mContext, 395, paramString);
        }
        return;
        this.mIPolicyManager.removeRestrictBackgroundWhitelistedUid(paramInt);
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("DataSaverBackend", "Can't reach policy manager", localRemoteException);
      }
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void onBlacklistStatusChanged(int paramInt, boolean paramBoolean);
    
    public abstract void onDataSaverChanged(boolean paramBoolean);
    
    public abstract void onWhitelistStatusChanged(int paramInt, boolean paramBoolean);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataSaverBackend.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */