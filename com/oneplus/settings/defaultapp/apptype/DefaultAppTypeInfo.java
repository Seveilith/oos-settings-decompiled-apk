package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import java.util.List;

public abstract class DefaultAppTypeInfo
{
  public abstract List<IntentFilter> getAppFilter();
  
  public abstract List<Intent> getAppIntent();
  
  public abstract List<Integer> getAppMatchParam();
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\defaultapp\apptype\DefaultAppTypeInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */