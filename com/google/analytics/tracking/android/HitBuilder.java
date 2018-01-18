package com.google.analytics.tracking.android;

import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class HitBuilder
{
  static String encode(String paramString)
  {
    try
    {
      String str = URLEncoder.encode(paramString, "UTF-8");
      return str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new AssertionError("URL encoding failed for: " + paramString);
    }
  }
  
  static Map<String, String> generateHitParams(Map<String, String> paramMap)
  {
    HashMap localHashMap = new HashMap();
    paramMap = paramMap.entrySet().iterator();
    for (;;)
    {
      if (!paramMap.hasNext()) {
        return localHashMap;
      }
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      if ((((String)localEntry.getKey()).startsWith("&")) && (localEntry.getValue() != null))
      {
        String str = ((String)localEntry.getKey()).substring(1);
        if (!TextUtils.isEmpty(str)) {
          localHashMap.put(str, localEntry.getValue());
        }
      }
    }
  }
  
  static String postProcessHit(Hit paramHit, long paramLong)
  {
    int j = 1;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramHit.getHitParams());
    if (paramHit.getHitTime() <= 0L)
    {
      i = 1;
      if (i == 0)
      {
        paramLong -= paramHit.getHitTime();
        if (paramLong >= 0L) {
          break label105;
        }
      }
    }
    label105:
    for (int i = j;; i = 0)
    {
      if (i == 0) {
        localStringBuilder.append("&qt").append("=").append(paramLong);
      }
      localStringBuilder.append("&z").append("=").append(paramHit.getHitId());
      return localStringBuilder.toString();
      i = 0;
      break;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\HitBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */