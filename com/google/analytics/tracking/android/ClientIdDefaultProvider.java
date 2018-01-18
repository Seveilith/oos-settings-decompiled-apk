package com.google.analytics.tracking.android;

import android.content.Context;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

class ClientIdDefaultProvider
  implements DefaultProvider
{
  private static ClientIdDefaultProvider sInstance;
  private static final Object sInstanceLock = new Object();
  private String mClientId;
  private boolean mClientIdLoaded = false;
  private final Object mClientIdLock = new Object();
  private final Context mContext;
  
  protected ClientIdDefaultProvider(Context paramContext)
  {
    this.mContext = paramContext;
    asyncInitializeClientId();
  }
  
  private void asyncInitializeClientId()
  {
    new Thread("client_id_fetcher")
    {
      public void run()
      {
        synchronized (ClientIdDefaultProvider.this.mClientIdLock)
        {
          ClientIdDefaultProvider.access$102(ClientIdDefaultProvider.this, ClientIdDefaultProvider.this.initializeClientId());
          ClientIdDefaultProvider.access$202(ClientIdDefaultProvider.this, true);
          ClientIdDefaultProvider.this.mClientIdLock.notifyAll();
          return;
        }
      }
    }.start();
  }
  
  private String blockingGetClientId()
  {
    if (this.mClientIdLoaded)
    {
      Log.v("Loaded clientId");
      return this.mClientId;
    }
    for (;;)
    {
      synchronized (this.mClientIdLock)
      {
        if (!this.mClientIdLoaded) {}
      }
      Log.v("Waiting for clientId to load");
      try
      {
        for (;;)
        {
          this.mClientIdLock.wait();
          if (this.mClientIdLoaded) {
            break;
          }
        }
      }
      catch (InterruptedException localInterruptedException)
      {
        for (;;)
        {
          Log.e("Exception while waiting for clientId: " + localInterruptedException);
        }
      }
    }
  }
  
  @VisibleForTesting
  static void dropInstance()
  {
    synchronized (sInstanceLock)
    {
      sInstance = null;
      return;
    }
  }
  
  public static ClientIdDefaultProvider getProvider()
  {
    synchronized (sInstanceLock)
    {
      ClientIdDefaultProvider localClientIdDefaultProvider = sInstance;
      return localClientIdDefaultProvider;
    }
  }
  
  public static void initializeProvider(Context paramContext)
  {
    synchronized (sInstanceLock)
    {
      if (sInstance != null) {
        return;
      }
      sInstance = new ClientIdDefaultProvider(paramContext);
    }
  }
  
  private boolean storeClientId(String paramString)
  {
    try
    {
      Log.v("Storing clientId.");
      FileOutputStream localFileOutputStream = this.mContext.openFileOutput("gaClientId", 0);
      localFileOutputStream.write(paramString.getBytes());
      localFileOutputStream.close();
      return true;
    }
    catch (FileNotFoundException paramString)
    {
      Log.e("Error creating clientId file.");
      return false;
    }
    catch (IOException paramString)
    {
      Log.e("Error writing to clientId file.");
    }
    return false;
  }
  
  protected String generateClientId()
  {
    String str = UUID.randomUUID().toString().toLowerCase();
    if (storeClientId(str)) {
      return str;
    }
    return "0";
  }
  
  public String getValue(String paramString)
  {
    if (!"&cid".equals(paramString)) {
      return null;
    }
    return blockingGetClientId();
  }
  
  @VisibleForTesting
  String initializeClientId()
  {
    Object localObject5 = null;
    Object localObject4 = null;
    for (;;)
    {
      try
      {
        localFileInputStream = this.mContext.openFileInput("gaClientId");
        localObject1 = new byte[''];
        int i = localFileInputStream.read((byte[])localObject1, 0, 128);
        if (localFileInputStream.available() <= 0)
        {
          if (i <= 0) {
            continue;
          }
          localObject1 = new String((byte[])localObject1, 0, i);
        }
      }
      catch (FileNotFoundException localFileNotFoundException1)
      {
        FileInputStream localFileInputStream;
        Object localObject1;
        Object localObject2 = localObject4;
        continue;
      }
      catch (IOException localIOException1)
      {
        Object localObject3 = localObject5;
        Log.e("Error reading clientId file, deleting it.");
        this.mContext.deleteFile("gaClientId");
        continue;
      }
      try
      {
        localFileInputStream.close();
        if (localObject1 == null) {
          break label145;
        }
        return (String)localObject1;
      }
      catch (IOException localIOException2)
      {
        continue;
      }
      catch (FileNotFoundException localFileNotFoundException2)
      {
        continue;
      }
      Log.e("clientId file seems corrupted, deleting it.");
      localFileInputStream.close();
      this.mContext.deleteFile("gaClientId");
      localObject1 = null;
      continue;
      Log.e("clientId file seems empty, deleting it.");
      localFileInputStream.close();
      this.mContext.deleteFile("gaClientId");
      localObject1 = null;
    }
    label145:
    return generateClientId();
  }
  
  public boolean providesField(String paramString)
  {
    return "&cid".equals(paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\ClientIdDefaultProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */