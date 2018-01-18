package com.google.tagmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.HashMap;
import java.util.Map;

class InstallReferrerUtil
{
  static final String INTENT_EXTRA_REFERRER = "referrer";
  static final String PREF_KEY_REFERRER = "referrer";
  static final String PREF_NAME_CLICK_REFERRERS = "gtm_click_referrers";
  static final String PREF_NAME_INSTALL_REFERRER = "gtm_install_referrer";
  @VisibleForTesting
  static Map<String, String> clickReferrers = new HashMap();
  private static String installReferrer;
  
  static void addClickReferrer(Context paramContext, String paramString)
  {
    String str = extractComponent(paramString, "conv");
    if (str == null) {}
    while (str.length() <= 0) {
      return;
    }
    clickReferrers.put(str, paramString);
    SharedPreferencesUtil.saveAsync(paramContext, "gtm_click_referrers", str, paramString);
  }
  
  static void cacheInstallReferrer(String paramString)
  {
    try
    {
      installReferrer = paramString;
      return;
    }
    finally {}
  }
  
  static String extractComponent(String paramString1, String paramString2)
  {
    if (paramString2 != null) {
      return Uri.parse("http://hostname/?" + paramString1).getQueryParameter(paramString2);
    }
    paramString2 = paramString1;
    if (paramString1.length() <= 0) {
      paramString2 = null;
    }
    return paramString2;
  }
  
  static String getClickReferrer(Context paramContext, String paramString1, String paramString2)
  {
    String str = (String)clickReferrers.get(paramString1);
    if (str != null)
    {
      paramContext = str;
      return extractComponent(paramContext, paramString2);
    }
    paramContext = paramContext.getSharedPreferences("gtm_click_referrers", 0);
    if (paramContext == null) {}
    for (paramContext = "";; paramContext = paramContext.getString(paramString1, ""))
    {
      clickReferrers.put(paramString1, paramContext);
      break;
    }
  }
  
  static String getInstallReferrer(Context paramContext, String paramString)
  {
    if (installReferrer != null) {
      return extractComponent(installReferrer, paramString);
    }
    for (;;)
    {
      try
      {
        if (installReferrer != null) {
          break;
        }
      }
      finally {}
      paramContext = paramContext.getSharedPreferences("gtm_install_referrer", 0);
      if (paramContext == null) {
        installReferrer = "";
      } else {
        installReferrer = paramContext.getString("referrer", "");
      }
    }
  }
  
  static void saveInstallReferrer(Context paramContext, String paramString)
  {
    SharedPreferencesUtil.saveAsync(paramContext, "gtm_install_referrer", "referrer", paramString);
    addClickReferrer(paramContext, paramString);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\InstallReferrerUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */