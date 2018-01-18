package com.google.tagmanager;

import android.content.Context;
import com.google.analytics.containertag.proto.Serving.SupplementedResource;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class ResourceLoaderSchedulerImpl
  implements Container.ResourceLoaderScheduler
{
  private static final boolean MAY_INTERRUPT_IF_RUNNING = true;
  private LoadCallback<Serving.SupplementedResource> mCallback;
  private boolean mClosed;
  private final String mContainerId;
  private final Context mContext;
  private CtfeHost mCtfeHost;
  private String mCtfeUrlPathAndQuery;
  private final ScheduledExecutorService mExecutor;
  private ScheduledFuture<?> mFuture;
  private final ResourceLoaderFactory mResourceLoaderFactory;
  
  public ResourceLoaderSchedulerImpl(Context paramContext, String paramString, CtfeHost paramCtfeHost)
  {
    this(paramContext, paramString, paramCtfeHost, null, null);
  }
  
  @VisibleForTesting
  ResourceLoaderSchedulerImpl(Context paramContext, String paramString, CtfeHost paramCtfeHost, ScheduledExecutorServiceFactory paramScheduledExecutorServiceFactory, ResourceLoaderFactory paramResourceLoaderFactory)
  {
    this.mCtfeHost = paramCtfeHost;
    this.mContext = paramContext;
    this.mContainerId = paramString;
    if (paramScheduledExecutorServiceFactory != null) {}
    for (;;)
    {
      this.mExecutor = paramScheduledExecutorServiceFactory.createExecutorService();
      if (paramResourceLoaderFactory == null) {
        break;
      }
      this.mResourceLoaderFactory = paramResourceLoaderFactory;
      return;
      paramScheduledExecutorServiceFactory = new ScheduledExecutorServiceFactory()
      {
        public ScheduledExecutorService createExecutorService()
        {
          return Executors.newSingleThreadScheduledExecutor();
        }
      };
    }
    this.mResourceLoaderFactory = new ResourceLoaderFactory()
    {
      public ResourceLoader createResourceLoader(CtfeHost paramAnonymousCtfeHost)
      {
        return new ResourceLoader(ResourceLoaderSchedulerImpl.this.mContext, ResourceLoaderSchedulerImpl.this.mContainerId, paramAnonymousCtfeHost);
      }
    };
  }
  
  private ResourceLoader createResourceLoader(String paramString)
  {
    ResourceLoader localResourceLoader = this.mResourceLoaderFactory.createResourceLoader(this.mCtfeHost);
    localResourceLoader.setLoadCallback(this.mCallback);
    localResourceLoader.setCtfeURLPathAndQuery(this.mCtfeUrlPathAndQuery);
    localResourceLoader.setPreviousVersion(paramString);
    return localResourceLoader;
  }
  
  private void ensureNotClosed()
  {
    try
    {
      boolean bool = this.mClosed;
      if (!bool) {
        return;
      }
      throw new IllegalStateException("called method after closed");
    }
    finally {}
  }
  
  /* Error */
  public void close()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 104	com/google/tagmanager/ResourceLoaderSchedulerImpl:ensureNotClosed	()V
    //   6: aload_0
    //   7: getfield 106	com/google/tagmanager/ResourceLoaderSchedulerImpl:mFuture	Ljava/util/concurrent/ScheduledFuture;
    //   10: ifnonnull +20 -> 30
    //   13: aload_0
    //   14: getfield 59	com/google/tagmanager/ResourceLoaderSchedulerImpl:mExecutor	Ljava/util/concurrent/ScheduledExecutorService;
    //   17: invokeinterface 111 1 0
    //   22: aload_0
    //   23: iconst_1
    //   24: putfield 95	com/google/tagmanager/ResourceLoaderSchedulerImpl:mClosed	Z
    //   27: aload_0
    //   28: monitorexit
    //   29: return
    //   30: aload_0
    //   31: getfield 106	com/google/tagmanager/ResourceLoaderSchedulerImpl:mFuture	Ljava/util/concurrent/ScheduledFuture;
    //   34: iconst_0
    //   35: invokeinterface 117 2 0
    //   40: pop
    //   41: goto -28 -> 13
    //   44: astore_1
    //   45: aload_0
    //   46: monitorexit
    //   47: aload_1
    //   48: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	49	0	this	ResourceLoaderSchedulerImpl
    //   44	4	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	13	44	finally
    //   13	27	44	finally
    //   30	41	44	finally
  }
  
  public void loadAfterDelay(long paramLong, String paramString)
  {
    for (;;)
    {
      try
      {
        Log.v("loadAfterDelay: containerId=" + this.mContainerId + " delay=" + paramLong);
        ensureNotClosed();
        if (this.mCallback != null)
        {
          if (this.mFuture == null) {
            this.mFuture = this.mExecutor.schedule(createResourceLoader(paramString), paramLong, TimeUnit.MILLISECONDS);
          }
        }
        else {
          throw new IllegalStateException("callback must be set before loadAfterDelay() is called.");
        }
      }
      finally {}
      this.mFuture.cancel(false);
    }
  }
  
  public void setCtfeURLPathAndQuery(String paramString)
  {
    try
    {
      ensureNotClosed();
      this.mCtfeUrlPathAndQuery = paramString;
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  public void setLoadCallback(LoadCallback<Serving.SupplementedResource> paramLoadCallback)
  {
    try
    {
      ensureNotClosed();
      this.mCallback = paramLoadCallback;
      return;
    }
    finally
    {
      paramLoadCallback = finally;
      throw paramLoadCallback;
    }
  }
  
  static abstract interface ResourceLoaderFactory
  {
    public abstract ResourceLoader createResourceLoader(CtfeHost paramCtfeHost);
  }
  
  static abstract interface ScheduledExecutorServiceFactory
  {
    public abstract ScheduledExecutorService createExecutorService();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\ResourceLoaderSchedulerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */