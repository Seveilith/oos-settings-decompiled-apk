package com.google.analytics.tracking.android;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.google.android.gms.common.util.VisibleForTesting;

public class GAServiceManager
  extends ServiceManager
{
  private static final int MSG_KEY = 1;
  private static final Object MSG_OBJECT = new Object();
  private static GAServiceManager instance;
  private boolean connected = true;
  private Context ctx;
  private int dispatchPeriodInSeconds = 1800;
  private Handler handler;
  private boolean listenForNetwork = true;
  private AnalyticsStoreStateListener listener = new AnalyticsStoreStateListener()
  {
    public void reportStoreIsEmpty(boolean paramAnonymousBoolean)
    {
      GAServiceManager.this.updatePowerSaveMode(paramAnonymousBoolean, GAServiceManager.this.connected);
    }
  };
  private GANetworkReceiver networkReceiver;
  private boolean pendingDispatch = true;
  private boolean pendingForceLocalDispatch;
  private String pendingHostOverride;
  private AnalyticsStore store;
  private boolean storeIsEmpty = false;
  private volatile AnalyticsThread thread;
  
  private GAServiceManager() {}
  
  @VisibleForTesting
  GAServiceManager(Context paramContext, AnalyticsThread paramAnalyticsThread, AnalyticsStore paramAnalyticsStore, boolean paramBoolean)
  {
    this.store = paramAnalyticsStore;
    this.thread = paramAnalyticsThread;
    this.listenForNetwork = paramBoolean;
    initialize(paramContext, paramAnalyticsThread);
  }
  
  @VisibleForTesting
  static void clearInstance()
  {
    instance = null;
  }
  
  public static GAServiceManager getInstance()
  {
    if (instance != null) {}
    for (;;)
    {
      return instance;
      instance = new GAServiceManager();
    }
  }
  
  private void initializeHandler()
  {
    this.handler = new Handler(this.ctx.getMainLooper(), new Handler.Callback()
    {
      public boolean handleMessage(Message paramAnonymousMessage)
      {
        if (1 != paramAnonymousMessage.what) {}
        do
        {
          do
          {
            return true;
          } while (!GAServiceManager.MSG_OBJECT.equals(paramAnonymousMessage.obj));
          GAUsage.getInstance().setDisableUsage(true);
          GAServiceManager.this.dispatchLocalHits();
          GAUsage.getInstance().setDisableUsage(false);
        } while ((GAServiceManager.this.dispatchPeriodInSeconds <= 0) || (GAServiceManager.this.storeIsEmpty));
        GAServiceManager.this.handler.sendMessageDelayed(GAServiceManager.this.handler.obtainMessage(1, GAServiceManager.MSG_OBJECT), GAServiceManager.this.dispatchPeriodInSeconds * 1000);
        return true;
      }
    });
    if (this.dispatchPeriodInSeconds <= 0) {
      return;
    }
    this.handler.sendMessageDelayed(this.handler.obtainMessage(1, MSG_OBJECT), this.dispatchPeriodInSeconds * 1000);
  }
  
  private void initializeNetworkReceiver()
  {
    this.networkReceiver = new GANetworkReceiver(this);
    this.networkReceiver.register(this.ctx);
  }
  
  @Deprecated
  public void dispatchLocalHits()
  {
    try
    {
      if (this.thread != null)
      {
        GAUsage.getInstance().setUsage(GAUsage.Field.DISPATCH);
        this.thread.dispatch();
        return;
      }
      Log.v("Dispatch call queued. Dispatch will run once initialization is complete.");
      this.pendingDispatch = true;
      return;
    }
    finally {}
  }
  
  @VisibleForTesting
  AnalyticsStoreStateListener getListener()
  {
    return this.listener;
  }
  
  AnalyticsStore getStore()
  {
    for (;;)
    {
      try
      {
        if (this.store != null)
        {
          if (this.handler != null)
          {
            if (this.networkReceiver == null) {
              break label113;
            }
            AnalyticsStore localAnalyticsStore = this.store;
            return localAnalyticsStore;
          }
        }
        else
        {
          if (this.ctx != null)
          {
            this.store = new PersistentAnalyticsStore(this.listener, this.ctx);
            if (this.pendingHostOverride == null) {
              continue;
            }
            this.store.getDispatcher().overrideHostUrl(this.pendingHostOverride);
            this.pendingHostOverride = null;
            continue;
          }
          throw new IllegalStateException("Cant get a store unless we have a context");
        }
      }
      finally {}
      initializeHandler();
      continue;
      label113:
      if (this.listenForNetwork) {
        initializeNetworkReceiver();
      }
    }
  }
  
  void initialize(Context paramContext, AnalyticsThread paramAnalyticsThread)
  {
    for (;;)
    {
      try
      {
        if (this.ctx == null)
        {
          this.ctx = paramContext.getApplicationContext();
          paramContext = this.thread;
          if (paramContext == null) {}
        }
        else
        {
          return;
        }
        this.thread = paramAnalyticsThread;
        if (!this.pendingDispatch)
        {
          if (!this.pendingForceLocalDispatch) {
            continue;
          }
          setForceLocalDispatch();
          this.pendingForceLocalDispatch = false;
          continue;
        }
        dispatchLocalHits();
      }
      finally {}
      this.pendingDispatch = false;
    }
  }
  
  /* Error */
  void onRadioPowered()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 62	com/google/analytics/tracking/android/GAServiceManager:storeIsEmpty	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: getfield 53	com/google/analytics/tracking/android/GAServiceManager:connected	Z
    //   18: ifeq -7 -> 11
    //   21: aload_0
    //   22: getfield 49	com/google/analytics/tracking/android/GAServiceManager:dispatchPeriodInSeconds	I
    //   25: ifle -14 -> 11
    //   28: aload_0
    //   29: getfield 84	com/google/analytics/tracking/android/GAServiceManager:handler	Landroid/os/Handler;
    //   32: iconst_1
    //   33: getstatic 45	com/google/analytics/tracking/android/GAServiceManager:MSG_OBJECT	Ljava/lang/Object;
    //   36: invokevirtual 204	android/os/Handler:removeMessages	(ILjava/lang/Object;)V
    //   39: aload_0
    //   40: getfield 84	com/google/analytics/tracking/android/GAServiceManager:handler	Landroid/os/Handler;
    //   43: aload_0
    //   44: getfield 84	com/google/analytics/tracking/android/GAServiceManager:handler	Landroid/os/Handler;
    //   47: iconst_1
    //   48: getstatic 45	com/google/analytics/tracking/android/GAServiceManager:MSG_OBJECT	Ljava/lang/Object;
    //   51: invokevirtual 109	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
    //   54: invokevirtual 208	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
    //   57: pop
    //   58: goto -47 -> 11
    //   61: astore_2
    //   62: aload_0
    //   63: monitorexit
    //   64: aload_2
    //   65: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	GAServiceManager
    //   6	2	1	bool	boolean
    //   61	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	61	finally
    //   14	58	61	finally
  }
  
  /* Error */
  @VisibleForTesting
  void overrideHostUrl(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 66	com/google/analytics/tracking/android/GAServiceManager:store	Lcom/google/analytics/tracking/android/AnalyticsStore;
    //   6: ifnull +21 -> 27
    //   9: aload_0
    //   10: getfield 66	com/google/analytics/tracking/android/GAServiceManager:store	Lcom/google/analytics/tracking/android/AnalyticsStore;
    //   13: invokeinterface 173 1 0
    //   18: aload_1
    //   19: invokeinterface 178 2 0
    //   24: aload_0
    //   25: monitorexit
    //   26: return
    //   27: aload_0
    //   28: aload_1
    //   29: putfield 167	com/google/analytics/tracking/android/GAServiceManager:pendingHostOverride	Ljava/lang/String;
    //   32: goto -8 -> 24
    //   35: astore_1
    //   36: aload_0
    //   37: monitorexit
    //   38: aload_1
    //   39: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	40	0	this	GAServiceManager
    //   0	40	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   2	24	35	finally
    //   27	32	35	finally
  }
  
  @Deprecated
  public void setForceLocalDispatch()
  {
    if (this.thread != null)
    {
      GAUsage.getInstance().setUsage(GAUsage.Field.SET_FORCE_LOCAL_DISPATCH);
      this.thread.setForceLocalDispatch();
      return;
    }
    Log.v("setForceLocalDispatch() queued. It will be called once initialization is complete.");
    this.pendingForceLocalDispatch = true;
  }
  
  @Deprecated
  public void setLocalDispatchPeriod(int paramInt)
  {
    for (;;)
    {
      try
      {
        if (this.handler != null)
        {
          GAUsage.getInstance().setUsage(GAUsage.Field.SET_DISPATCH_PERIOD);
          if (this.storeIsEmpty)
          {
            this.dispatchPeriodInSeconds = paramInt;
            if (paramInt > 0) {
              break label83;
            }
          }
        }
        else
        {
          Log.v("Dispatch period set with null handler. Dispatch will run once initialization is complete.");
          this.dispatchPeriodInSeconds = paramInt;
          return;
        }
        if ((!this.connected) || (this.dispatchPeriodInSeconds <= 0)) {
          continue;
        }
        this.handler.removeMessages(1, MSG_OBJECT);
        continue;
        if (this.storeIsEmpty) {
          continue;
        }
      }
      finally {}
      label83:
      if (this.connected) {
        this.handler.sendMessageDelayed(this.handler.obtainMessage(1, MSG_OBJECT), paramInt * 1000);
      }
    }
  }
  
  void updateConnectivityStatus(boolean paramBoolean)
  {
    try
    {
      updatePowerSaveMode(this.storeIsEmpty, paramBoolean);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  @VisibleForTesting
  void updatePowerSaveMode(boolean paramBoolean1, boolean paramBoolean2)
  {
    for (;;)
    {
      try
      {
        if (this.storeIsEmpty != paramBoolean1)
        {
          break label164;
          if (this.dispatchPeriodInSeconds <= 0)
          {
            break label171;
            label22:
            StringBuilder localStringBuilder = new StringBuilder().append("PowerSaveMode ");
            if (!paramBoolean1) {
              break label153;
            }
            break label178;
            String str1;
            Log.v(str1);
            this.storeIsEmpty = paramBoolean1;
            this.connected = paramBoolean2;
          }
        }
        else
        {
          boolean bool = this.connected;
          if (bool != paramBoolean2) {
            break label164;
          }
          return;
          if (!paramBoolean2) {
            break;
          }
          break label171;
        }
        this.handler.removeMessages(1, MSG_OBJECT);
      }
      finally {}
      if ((paramBoolean2) && (this.dispatchPeriodInSeconds > 0))
      {
        this.handler.sendMessageDelayed(this.handler.obtainMessage(1, MSG_OBJECT), this.dispatchPeriodInSeconds * 1000);
        continue;
        label153:
        if (paramBoolean2)
        {
          str2 = "terminated.";
          continue;
          label164:
          if (!paramBoolean1) {
            continue;
          }
          break;
          label171:
          if (!paramBoolean1) {
            break label22;
          }
          continue;
        }
        label178:
        String str2 = "initiated.";
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\GAServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */