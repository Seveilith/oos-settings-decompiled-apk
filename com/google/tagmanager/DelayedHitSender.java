package com.google.tagmanager;

import android.content.Context;
import com.google.android.gms.common.util.VisibleForTesting;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class DelayedHitSender
  implements HitSender
{
  private static DelayedHitSender sInstance;
  private static final Object sInstanceLock = new Object();
  private RateLimiter mRateLimiter;
  private HitSendingThread mSendingThread;
  private String mWrapperQueryParameter;
  private String mWrapperUrl;
  
  private DelayedHitSender(Context paramContext)
  {
    this(HitSendingThreadImpl.getInstance(paramContext), new SendHitRateLimiter());
  }
  
  @VisibleForTesting
  DelayedHitSender(HitSendingThread paramHitSendingThread, RateLimiter paramRateLimiter)
  {
    this.mSendingThread = paramHitSendingThread;
    this.mRateLimiter = paramRateLimiter;
  }
  
  public static HitSender getInstance(Context paramContext)
  {
    synchronized (sInstanceLock)
    {
      if (sInstance != null)
      {
        paramContext = sInstance;
        return paramContext;
      }
      sInstance = new DelayedHitSender(paramContext);
    }
  }
  
  public boolean sendHit(String paramString)
  {
    String str;
    if (this.mRateLimiter.tokenAvailable())
    {
      if (this.mWrapperUrl != null) {
        break label40;
      }
      str = paramString;
    }
    for (;;)
    {
      this.mSendingThread.sendHit(str);
      return true;
      Log.w("Too many urls sent too quickly with the TagManagerSender, rate limiting invoked.");
      return false;
      label40:
      str = paramString;
      if (this.mWrapperQueryParameter != null) {
        try
        {
          str = this.mWrapperUrl + "?" + this.mWrapperQueryParameter + "=" + URLEncoder.encode(paramString, "UTF-8");
          Log.v("Sending wrapped url hit: " + str);
        }
        catch (UnsupportedEncodingException paramString)
        {
          Log.w("Error wrapping URL for testing.", paramString);
        }
      }
    }
    return false;
  }
  
  public void setUrlWrapModeForTesting(String paramString1, String paramString2)
  {
    this.mWrapperUrl = paramString1;
    this.mWrapperQueryParameter = paramString2;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\DelayedHitSender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */