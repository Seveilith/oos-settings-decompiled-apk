package com.oneplus.settings.statusbar;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.ArraySet;

public class Utils
{
  public static final String CLOCK_SECONDS = "clock_seconds";
  public static final String ICON_BLACKLIST = "icon_blacklist";
  private ContentResolver mContentResolver;
  private Context mContext;
  private int mCurrentUser;
  
  public Utils(Context paramContext)
  {
    this.mContext = paramContext;
    if (this.mContext != null) {
      this.mContentResolver = this.mContext.getContentResolver();
    }
    this.mCurrentUser = ActivityManager.getCurrentUser();
  }
  
  public static ArraySet<String> getIconBlacklist(String paramString)
  {
    ArraySet localArraySet = new ArraySet();
    String str = paramString;
    if (paramString == null) {
      str = "rotate,networkspeed";
    }
    paramString = str.split(",");
    int i = 0;
    int j = paramString.length;
    while (i < j)
    {
      str = paramString[i];
      if (!TextUtils.isEmpty(str)) {
        localArraySet.add(str);
      }
      i += 1;
    }
    return localArraySet;
  }
  
  public int getValue(String paramString, int paramInt)
  {
    return Settings.Secure.getIntForUser(this.mContentResolver, paramString, paramInt, this.mCurrentUser);
  }
  
  public String getValue(String paramString)
  {
    return Settings.Secure.getStringForUser(this.mContentResolver, paramString, this.mCurrentUser);
  }
  
  public void setValue(String paramString, int paramInt)
  {
    Settings.Secure.putIntForUser(this.mContentResolver, paramString, paramInt, this.mCurrentUser);
  }
  
  public void setValue(String paramString1, String paramString2)
  {
    Settings.Secure.putStringForUser(this.mContentResolver, paramString1, paramString2, this.mCurrentUser);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\statusbar\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */