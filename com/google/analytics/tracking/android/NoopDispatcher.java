package com.google.analytics.tracking.android;

import android.text.TextUtils;
import java.util.List;

class NoopDispatcher
  implements Dispatcher
{
  public void close() {}
  
  public int dispatchHits(List<Hit> paramList)
  {
    int j;
    if (paramList != null)
    {
      j = Math.min(paramList.size(), 40);
      if (!Log.isVerbose()) {
        return j;
      }
    }
    else
    {
      return 0;
    }
    Log.v("Hits not actually being sent as dispatch is false...");
    int i = 0;
    label33:
    String str2;
    label75:
    String str1;
    if (i < j)
    {
      if (TextUtils.isEmpty(((Hit)paramList.get(i)).getHitParams())) {
        break label139;
      }
      str2 = HitBuilder.postProcessHit((Hit)paramList.get(i), System.currentTimeMillis());
      if (TextUtils.isEmpty(str2)) {
        break label146;
      }
      if (str2.length() <= 2036) {
        break label153;
      }
      if (str2.length() > 8192) {
        break label160;
      }
      str1 = "POST would be sent:";
    }
    for (;;)
    {
      Log.v(str1 + str2);
      i += 1;
      break label33;
      break;
      label139:
      str2 = "";
      break label75;
      label146:
      str1 = "Hit couldn't be read, wouldn't be sent:";
      continue;
      label153:
      str1 = "GET would be sent:";
      continue;
      label160:
      str1 = "Would be too big:";
    }
  }
  
  public boolean okToDispatch()
  {
    return true;
  }
  
  public void overrideHostUrl(String paramString) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\analytics\tracking\android\NoopDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */