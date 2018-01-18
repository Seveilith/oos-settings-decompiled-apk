package com.google.analytics.tracking.android;

import android.text.TextUtils;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Tracker
{
  private final AppFieldsDefaultProvider mAppFieldsDefaultProvider;
  private final ClientIdDefaultProvider mClientIdDefaultProvider;
  private final TrackerHandler mHandler;
  private final String mName;
  private final Map<String, String> mParams = new HashMap();
  private RateLimiter mRateLimiter;
  private final ScreenResolutionDefaultProvider mScreenResolutionDefaultProvider;
  
  Tracker(String paramString1, String paramString2, TrackerHandler paramTrackerHandler)
  {
    this(paramString1, paramString2, paramTrackerHandler, ClientIdDefaultProvider.getProvider(), ScreenResolutionDefaultProvider.getProvider(), AppFieldsDefaultProvider.getProvider(), new SendHitRateLimiter());
  }
  
  @VisibleForTesting
  Tracker(String paramString1, String paramString2, TrackerHandler paramTrackerHandler, ClientIdDefaultProvider paramClientIdDefaultProvider, ScreenResolutionDefaultProvider paramScreenResolutionDefaultProvider, AppFieldsDefaultProvider paramAppFieldsDefaultProvider, RateLimiter paramRateLimiter)
  {
    if (!TextUtils.isEmpty(paramString1))
    {
      this.mName = paramString1;
      this.mHandler = paramTrackerHandler;
      this.mParams.put("&tid", paramString2);
      this.mParams.put("useSecure", "1");
      this.mClientIdDefaultProvider = paramClientIdDefaultProvider;
      this.mScreenResolutionDefaultProvider = paramScreenResolutionDefaultProvider;
      this.mAppFieldsDefaultProvider = paramAppFieldsDefaultProvider;
      this.mRateLimiter = paramRateLimiter;
      return;
    }
    throw new IllegalArgumentException("Tracker name cannot be empty.");
  }
  
  public String get(String paramString)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.GET);
    if (!TextUtils.isEmpty(paramString))
    {
      if (this.mParams.containsKey(paramString)) {
        break label63;
      }
      if (paramString.equals("&ul")) {
        break label77;
      }
      if (this.mClientIdDefaultProvider != null) {
        break label84;
      }
      if (this.mScreenResolutionDefaultProvider != null) {
        break label104;
      }
      label52:
      if (this.mAppFieldsDefaultProvider != null) {
        break label124;
      }
    }
    label63:
    label77:
    label84:
    label104:
    label124:
    while (!this.mAppFieldsDefaultProvider.providesField(paramString))
    {
      return null;
      return null;
      return (String)this.mParams.get(paramString);
      return Utils.getLanguage(Locale.getDefault());
      if (!this.mClientIdDefaultProvider.providesField(paramString)) {
        break;
      }
      return this.mClientIdDefaultProvider.getValue(paramString);
      if (!this.mScreenResolutionDefaultProvider.providesField(paramString)) {
        break label52;
      }
      return this.mScreenResolutionDefaultProvider.getValue(paramString);
    }
    return this.mAppFieldsDefaultProvider.getValue(paramString);
  }
  
  public String getName()
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.GET_TRACKER_NAME);
    return this.mName;
  }
  
  @VisibleForTesting
  RateLimiter getRateLimiter()
  {
    return this.mRateLimiter;
  }
  
  public void send(Map<String, String> paramMap)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.SEND);
    HashMap localHashMap = new HashMap();
    localHashMap.putAll(this.mParams);
    if (paramMap == null)
    {
      if (TextUtils.isEmpty((CharSequence)localHashMap.get("&tid"))) {
        break label95;
      }
      label48:
      paramMap = (String)localHashMap.get("&t");
      if (TextUtils.isEmpty(paramMap)) {
        break label115;
      }
      label67:
      if (!paramMap.equals("transaction")) {
        break label138;
      }
    }
    label95:
    label115:
    label138:
    while ((paramMap.equals("item")) || (this.mRateLimiter.tokenAvailable()))
    {
      this.mHandler.sendHit(localHashMap);
      return;
      localHashMap.putAll(paramMap);
      break;
      Log.w(String.format("Missing tracking id (%s) parameter.", new Object[] { "&tid" }));
      break label48;
      Log.w(String.format("Missing hit type (%s) parameter.", new Object[] { "&t" }));
      paramMap = "";
      break label67;
    }
    Log.w("Too many hits sent too quickly, rate limiting invoked.");
  }
  
  public void set(String paramString1, String paramString2)
  {
    GAUsage.getInstance().setUsage(GAUsage.Field.SET);
    if (paramString2 != null)
    {
      this.mParams.put(paramString1, paramString2);
      return;
    }
    this.mParams.remove(paramString1);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\Tracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */