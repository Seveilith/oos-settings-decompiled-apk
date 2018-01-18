package com.google.tagmanager;

import android.content.Context;
import android.net.Uri;
import java.util.Map;

class AdwordsClickReferrerListener
  implements DataLayer.Listener
{
  private final Context context;
  
  public AdwordsClickReferrerListener(Context paramContext)
  {
    this.context = paramContext;
  }
  
  public void changed(Map<Object, Object> paramMap)
  {
    Object localObject = paramMap.get("gtm.url");
    if (localObject != null)
    {
      paramMap = (Map<Object, Object>)localObject;
      if (paramMap != null) {
        break label60;
      }
    }
    label60:
    while (!(paramMap instanceof String))
    {
      return;
      paramMap = paramMap.get("gtm");
      if (paramMap == null) {}
      while (!(paramMap instanceof Map))
      {
        paramMap = (Map<Object, Object>)localObject;
        break;
      }
      paramMap = ((Map)paramMap).get("url");
      break;
    }
    paramMap = Uri.parse((String)paramMap).getQueryParameter("referrer");
    if (paramMap == null) {
      return;
    }
    InstallReferrerUtil.addClickReferrer(this.context, paramMap);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\google\tagmanager\AdwordsClickReferrerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */