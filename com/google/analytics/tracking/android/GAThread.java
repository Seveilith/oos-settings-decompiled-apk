package com.google.analytics.tracking.android;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.analytics.internal.Command;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

class GAThread
  extends Thread
  implements AnalyticsThread
{
  static final String API_VERSION = "1";
  private static final String CLIENT_VERSION = "ma3.0.2";
  private static final int MAX_SAMPLE_RATE = 100;
  private static final int SAMPLE_RATE_MODULO = 10000;
  private static final int SAMPLE_RATE_MULTIPLIER = 100;
  private static GAThread sInstance;
  private volatile String mClientId;
  private volatile boolean mClosed = false;
  private volatile List<Command> mCommands;
  private final Context mContext;
  private volatile boolean mDisabled = false;
  private volatile String mInstallCampaign;
  private volatile ServiceProxy mServiceProxy;
  private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue();
  
  private GAThread(Context paramContext)
  {
    super("GAThread");
    if (paramContext == null) {}
    for (this.mContext = paramContext;; this.mContext = paramContext.getApplicationContext())
    {
      start();
      return;
    }
  }
  
  @VisibleForTesting
  GAThread(Context paramContext, ServiceProxy paramServiceProxy)
  {
    super("GAThread");
    if (paramContext == null) {}
    for (this.mContext = paramContext;; this.mContext = paramContext.getApplicationContext())
    {
      this.mServiceProxy = paramServiceProxy;
      start();
      return;
    }
  }
  
  private void fillAppParameters(Map<String, String> paramMap)
  {
    AppFieldsDefaultProvider localAppFieldsDefaultProvider = AppFieldsDefaultProvider.getProvider();
    Utils.putIfAbsent(paramMap, "&an", localAppFieldsDefaultProvider.getValue("&an"));
    Utils.putIfAbsent(paramMap, "&av", localAppFieldsDefaultProvider.getValue("&av"));
    Utils.putIfAbsent(paramMap, "&aid", localAppFieldsDefaultProvider.getValue("&aid"));
    Utils.putIfAbsent(paramMap, "&aiid", localAppFieldsDefaultProvider.getValue("&aiid"));
    paramMap.put("&v", "1");
  }
  
  @VisibleForTesting
  static String getAndClearCampaign(Context paramContext)
  {
    try
    {
      Object localObject = paramContext.openFileInput("gaInstallData");
      byte[] arrayOfByte = new byte[' '];
      int i = ((FileInputStream)localObject).read(arrayOfByte, 0, 8192);
      if (((FileInputStream)localObject).available() <= 0)
      {
        ((FileInputStream)localObject).close();
        paramContext.deleteFile("gaInstallData");
        if (i > 0)
        {
          localObject = new String(arrayOfByte, 0, i);
          Log.i("Campaign found: " + (String)localObject);
          return (String)localObject;
        }
      }
      else
      {
        Log.e("Too much campaign data, ignoring it.");
        ((FileInputStream)localObject).close();
        paramContext.deleteFile("gaInstallData");
        return null;
      }
      Log.w("Campaign file is empty.");
      return null;
    }
    catch (FileNotFoundException paramContext)
    {
      Log.i("No campaign data found.");
      return null;
    }
    catch (IOException localIOException)
    {
      Log.e("Error reading campaign data.");
      paramContext.deleteFile("gaInstallData");
    }
    return null;
  }
  
  static GAThread getInstance(Context paramContext)
  {
    if (sInstance != null) {}
    for (;;)
    {
      return sInstance;
      sInstance = new GAThread(paramContext);
    }
  }
  
  private String getUrlScheme(Map<String, String> paramMap)
  {
    if (!paramMap.containsKey("useSecure")) {
      return "https:";
    }
    if (!Utils.safeParseBoolean((String)paramMap.get("useSecure"), true)) {
      return "http:";
    }
    return "https:";
  }
  
  @VisibleForTesting
  static int hashClientIdForSampling(String paramString)
  {
    int k = 1;
    if (TextUtils.isEmpty(paramString)) {}
    int j;
    do
    {
      return k;
      j = paramString.length();
      i = 0;
      j -= 1;
      k = i;
    } while (j < 0);
    k = paramString.charAt(j);
    int i = (i << 6 & 0xFFFFFFF) + k + (k << 14);
    k = 0xFE00000 & i;
    if (k == 0) {}
    for (;;)
    {
      j -= 1;
      break;
      i ^= k >> 21;
    }
  }
  
  private boolean isSampledOut(Map<String, String> paramMap)
  {
    double d;
    if (paramMap.get("&sf") != null)
    {
      d = Utils.safeParseDouble((String)paramMap.get("&sf"), 100.0D);
      if (d >= 100.0D) {
        return false;
      }
    }
    else
    {
      return false;
    }
    if (hashClientIdForSampling((String)paramMap.get("&cid")) % 10000 >= d * 100.0D)
    {
      if (paramMap.get("&t") != null) {}
      for (paramMap = (String)paramMap.get("&t");; paramMap = "unknown")
      {
        Log.v(String.format("%s hit sampled out", new Object[] { paramMap }));
        return true;
      }
    }
    return false;
  }
  
  private String printStackTrace(Throwable paramThrowable)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream localPrintStream = new PrintStream(localByteArrayOutputStream);
    paramThrowable.printStackTrace(localPrintStream);
    localPrintStream.flush();
    return new String(localByteArrayOutputStream.toByteArray());
  }
  
  public void clearHits()
  {
    queueToThread(new Runnable()
    {
      public void run()
      {
        GAThread.this.mServiceProxy.clearHits();
      }
    });
  }
  
  @VisibleForTesting
  void close()
  {
    this.mClosed = true;
    interrupt();
  }
  
  public void dispatch()
  {
    queueToThread(new Runnable()
    {
      public void run()
      {
        GAThread.this.mServiceProxy.dispatch();
      }
    });
  }
  
  public LinkedBlockingQueue<Runnable> getQueue()
  {
    return this.queue;
  }
  
  public Thread getThread()
  {
    return this;
  }
  
  @VisibleForTesting
  protected void init()
  {
    this.mServiceProxy.createService();
    this.mCommands = new ArrayList();
    this.mCommands.add(new Command("appendVersion", "&_v".substring(1), "ma3.0.2"));
    this.mCommands.add(new Command("appendQueueTime", "&qt".substring(1), null));
    this.mCommands.add(new Command("appendCacheBuster", "&z".substring(1), null));
  }
  
  @VisibleForTesting
  boolean isDisabled()
  {
    return this.mDisabled;
  }
  
  @VisibleForTesting
  void queueToThread(Runnable paramRunnable)
  {
    this.queue.add(paramRunnable);
  }
  
  public void run()
  {
    try
    {
      Thread.sleep(5000L);
      try
      {
        if (this.mServiceProxy == null) {
          break label59;
        }
        init();
        this.mClientId = ClientIdDefaultProvider.getProvider().getValue("&cid");
        this.mInstallCampaign = getAndClearCampaign(this.mContext);
      }
      catch (Throwable localThrowable1)
      {
        for (;;)
        {
          Log.e("Error initializing the GAThread: " + printStackTrace(localThrowable1));
          Log.e("Google Analytics will not start up.");
          this.mDisabled = true;
          continue;
          try
          {
            Runnable localRunnable = (Runnable)this.queue.take();
            if (!this.mDisabled) {
              localRunnable.run();
            }
          }
          catch (InterruptedException localInterruptedException2)
          {
            Log.i(localInterruptedException2.toString());
          }
          catch (Throwable localThrowable2)
          {
            Log.e("Error on GAThread: " + printStackTrace(localThrowable2));
            Log.e("Google Analytics is shutting down.");
            this.mDisabled = true;
          }
        }
      }
      if (this.mClosed) {
        return;
      }
    }
    catch (InterruptedException localInterruptedException1)
    {
      for (;;)
      {
        Log.w("sleep interrupted in GAThread initialize");
        continue;
        label59:
        this.mServiceProxy = new GAServiceProxy(this.mContext, this);
      }
    }
  }
  
  public void sendHit(Map<String, String> paramMap)
  {
    final HashMap localHashMap = new HashMap(paramMap);
    paramMap = (String)paramMap.get("&ht");
    if (paramMap == null) {
      if (paramMap == null) {
        break label61;
      }
    }
    for (;;)
    {
      queueToThread(new Runnable()
      {
        public void run()
        {
          if (!TextUtils.isEmpty((CharSequence)localHashMap.get("&cid"))) {
            if (!GoogleAnalytics.getInstance(GAThread.this.mContext).getAppOptOut()) {
              break label59;
            }
          }
          label59:
          while (GAThread.this.isSampledOut(localHashMap))
          {
            return;
            localHashMap.put("&cid", GAThread.this.mClientId);
            break;
          }
          if (TextUtils.isEmpty(GAThread.this.mInstallCampaign)) {}
          for (;;)
          {
            GAThread.this.fillAppParameters(localHashMap);
            Map localMap = HitBuilder.generateHitParams(localHashMap);
            GAThread.this.mServiceProxy.putHit(localMap, Long.valueOf((String)localHashMap.get("&ht")).longValue(), GAThread.this.getUrlScheme(localHashMap), GAThread.this.mCommands);
            return;
            GAUsage.getInstance().setDisableUsage(true);
            localHashMap.putAll(new MapBuilder().setCampaignParamsFromUrl(GAThread.this.mInstallCampaign).build());
            GAUsage.getInstance().setDisableUsage(false);
            GAThread.access$302(GAThread.this, null);
          }
        }
      });
      return;
      try
      {
        Long.valueOf(paramMap).longValue();
      }
      catch (NumberFormatException paramMap)
      {
        paramMap = null;
      }
      break;
      label61:
      localHashMap.put("&ht", Long.toString(System.currentTimeMillis()));
    }
  }
  
  public void setForceLocalDispatch()
  {
    queueToThread(new Runnable()
    {
      public void run()
      {
        GAThread.this.mServiceProxy.setForceLocalDispatch();
      }
    });
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\GAThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */