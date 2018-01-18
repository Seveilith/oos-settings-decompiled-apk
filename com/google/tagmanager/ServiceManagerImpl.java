package com.google.tagmanager;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.google.android.gms.common.util.VisibleForTesting;

class ServiceManagerImpl
  extends ServiceManager
{
  private static final int MSG_KEY = 1;
  private static final Object MSG_OBJECT = new Object();
  private static ServiceManagerImpl instance;
  private boolean connected = true;
  private Context ctx;
  private int dispatchPeriodInSeconds = 1800;
  private Handler handler;
  private boolean listenForNetwork = true;
  private HitStoreStateListener listener = new HitStoreStateListener()
  {
    public void reportStoreIsEmpty(boolean paramAnonymousBoolean)
    {
      ServiceManagerImpl.this.updatePowerSaveMode(paramAnonymousBoolean, ServiceManagerImpl.this.connected);
    }
  };
  private NetworkReceiver networkReceiver;
  private boolean pendingDispatch = true;
  private boolean readyToDispatch = false;
  private HitStore store;
  private boolean storeIsEmpty = false;
  private volatile HitSendingThread thread;
  
  private ServiceManagerImpl() {}
  
  @VisibleForTesting
  ServiceManagerImpl(Context paramContext, HitSendingThread paramHitSendingThread, HitStore paramHitStore, boolean paramBoolean)
  {
    this.store = paramHitStore;
    this.thread = paramHitSendingThread;
    this.listenForNetwork = paramBoolean;
    initialize(paramContext, paramHitSendingThread);
  }
  
  @VisibleForTesting
  static void clearInstance()
  {
    instance = null;
  }
  
  public static ServiceManagerImpl getInstance()
  {
    if (instance != null) {}
    for (;;)
    {
      return instance;
      instance = new ServiceManagerImpl();
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
          } while (!ServiceManagerImpl.MSG_OBJECT.equals(paramAnonymousMessage.obj));
          ServiceManagerImpl.this.dispatch();
        } while ((ServiceManagerImpl.this.dispatchPeriodInSeconds <= 0) || (ServiceManagerImpl.this.storeIsEmpty));
        ServiceManagerImpl.this.handler.sendMessageDelayed(ServiceManagerImpl.this.handler.obtainMessage(1, ServiceManagerImpl.MSG_OBJECT), ServiceManagerImpl.this.dispatchPeriodInSeconds * 1000);
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
    this.networkReceiver = new NetworkReceiver(this);
    this.networkReceiver.register(this.ctx);
  }
  
  public void dispatch()
  {
    try
    {
      if (this.readyToDispatch)
      {
        this.thread.queueToThread(new Runnable()
        {
          public void run()
          {
            ServiceManagerImpl.this.store.dispatch();
          }
        });
        return;
      }
      Log.v("Dispatch call queued. Dispatch will run once initialization is complete.");
      this.pendingDispatch = true;
      return;
    }
    finally {}
  }
  
  @VisibleForTesting
  HitStoreStateListener getListener()
  {
    return this.listener;
  }
  
  HitStore getStore()
  {
    for (;;)
    {
      try
      {
        if (this.store != null)
        {
          if (this.handler != null)
          {
            this.readyToDispatch = true;
            if (this.pendingDispatch) {
              break label95;
            }
            if (this.networkReceiver == null) {
              break label107;
            }
            HitStore localHitStore = this.store;
            return localHitStore;
          }
        }
        else
        {
          if (this.ctx != null)
          {
            this.store = new PersistentHitStore(this.listener, this.ctx);
            continue;
          }
          throw new IllegalStateException("Cant get a store unless we have a context");
        }
      }
      finally {}
      initializeHandler();
      continue;
      label95:
      dispatch();
      this.pendingDispatch = false;
      continue;
      label107:
      if (this.listenForNetwork) {
        initializeNetworkReceiver();
      }
    }
  }
  
  /* Error */
  void initialize(Context paramContext, HitSendingThread paramHitSendingThread)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 99	com/google/tagmanager/ServiceManagerImpl:ctx	Landroid/content/Context;
    //   6: ifnonnull +23 -> 29
    //   9: aload_0
    //   10: aload_1
    //   11: invokevirtual 170	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   14: putfield 99	com/google/tagmanager/ServiceManagerImpl:ctx	Landroid/content/Context;
    //   17: aload_0
    //   18: getfield 70	com/google/tagmanager/ServiceManagerImpl:thread	Lcom/google/tagmanager/HitSendingThread;
    //   21: astore_1
    //   22: aload_1
    //   23: ifnull +9 -> 32
    //   26: aload_0
    //   27: monitorexit
    //   28: return
    //   29: aload_0
    //   30: monitorexit
    //   31: return
    //   32: aload_0
    //   33: aload_2
    //   34: putfield 70	com/google/tagmanager/ServiceManagerImpl:thread	Lcom/google/tagmanager/HitSendingThread;
    //   37: goto -11 -> 26
    //   40: astore_1
    //   41: aload_0
    //   42: monitorexit
    //   43: aload_1
    //   44: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	45	0	this	ServiceManagerImpl
    //   0	45	1	paramContext	Context
    //   0	45	2	paramHitSendingThread	HitSendingThread
    // Exception table:
    //   from	to	target	type
    //   2	22	40	finally
    //   32	37	40	finally
  }
  
  /* Error */
  void onRadioPowered()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 64	com/google/tagmanager/ServiceManagerImpl:storeIsEmpty	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: getfield 55	com/google/tagmanager/ServiceManagerImpl:connected	Z
    //   18: ifeq -7 -> 11
    //   21: aload_0
    //   22: getfield 49	com/google/tagmanager/ServiceManagerImpl:dispatchPeriodInSeconds	I
    //   25: ifle -14 -> 11
    //   28: aload_0
    //   29: getfield 86	com/google/tagmanager/ServiceManagerImpl:handler	Landroid/os/Handler;
    //   32: iconst_1
    //   33: getstatic 45	com/google/tagmanager/ServiceManagerImpl:MSG_OBJECT	Ljava/lang/Object;
    //   36: invokevirtual 175	android/os/Handler:removeMessages	(ILjava/lang/Object;)V
    //   39: aload_0
    //   40: getfield 86	com/google/tagmanager/ServiceManagerImpl:handler	Landroid/os/Handler;
    //   43: aload_0
    //   44: getfield 86	com/google/tagmanager/ServiceManagerImpl:handler	Landroid/os/Handler;
    //   47: iconst_1
    //   48: getstatic 45	com/google/tagmanager/ServiceManagerImpl:MSG_OBJECT	Ljava/lang/Object;
    //   51: invokevirtual 113	android/os/Handler:obtainMessage	(ILjava/lang/Object;)Landroid/os/Message;
    //   54: invokevirtual 179	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
    //   57: pop
    //   58: goto -47 -> 11
    //   61: astore_2
    //   62: aload_0
    //   63: monitorexit
    //   64: aload_2
    //   65: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	66	0	this	ServiceManagerImpl
    //   6	2	1	bool	boolean
    //   61	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	61	finally
    //   14	58	61	finally
  }
  
  public void setDispatchPeriod(int paramInt)
  {
    for (;;)
    {
      try
      {
        if (this.handler != null)
        {
          if (this.storeIsEmpty)
          {
            this.dispatchPeriodInSeconds = paramInt;
            if (paramInt > 0) {
              break label74;
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
      label74:
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


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ServiceManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */