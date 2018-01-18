package com.google.analytics.tracking.android;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import com.google.android.gms.common.util.VisibleForTesting;

class ScreenResolutionDefaultProvider
  implements DefaultProvider
{
  private static ScreenResolutionDefaultProvider sInstance;
  private static Object sInstanceLock = new Object();
  private final Context mContext;
  
  @VisibleForTesting
  protected ScreenResolutionDefaultProvider(Context paramContext)
  {
    this.mContext = paramContext;
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
  
  public static ScreenResolutionDefaultProvider getProvider()
  {
    synchronized (sInstanceLock)
    {
      ScreenResolutionDefaultProvider localScreenResolutionDefaultProvider = sInstance;
      return localScreenResolutionDefaultProvider;
    }
  }
  
  public static void initializeProvider(Context paramContext)
  {
    synchronized (sInstanceLock)
    {
      if (sInstance != null) {
        return;
      }
      sInstance = new ScreenResolutionDefaultProvider(paramContext);
    }
  }
  
  protected String getScreenResolutionString()
  {
    DisplayMetrics localDisplayMetrics = this.mContext.getResources().getDisplayMetrics();
    return localDisplayMetrics.widthPixels + "x" + localDisplayMetrics.heightPixels;
  }
  
  public String getValue(String paramString)
  {
    if (paramString != null)
    {
      if (!paramString.equals("&sr")) {
        return null;
      }
    }
    else {
      return null;
    }
    return getScreenResolutionString();
  }
  
  public boolean providesField(String paramString)
  {
    return "&sr".equals(paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\ScreenResolutionDefaultProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */