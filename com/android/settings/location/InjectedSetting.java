package com.android.settings.location;

import android.content.Intent;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.Immutable;
import com.android.internal.util.Preconditions;

@Immutable
class InjectedSetting
{
  public final String className;
  public final int iconId;
  public final UserHandle mUserHandle;
  public final String packageName;
  public final String settingsActivity;
  public final String title;
  
  private InjectedSetting(String paramString1, String paramString2, String paramString3, int paramInt, UserHandle paramUserHandle, String paramString4)
  {
    this.packageName = ((String)Preconditions.checkNotNull(paramString1, "packageName"));
    this.className = ((String)Preconditions.checkNotNull(paramString2, "className"));
    this.title = ((String)Preconditions.checkNotNull(paramString3, "title"));
    this.iconId = paramInt;
    this.mUserHandle = paramUserHandle;
    this.settingsActivity = ((String)Preconditions.checkNotNull(paramString4));
  }
  
  public static InjectedSetting newInstance(String paramString1, String paramString2, String paramString3, int paramInt, UserHandle paramUserHandle, String paramString4)
  {
    if ((paramString1 == null) || (paramString2 == null)) {}
    while ((TextUtils.isEmpty(paramString3)) || (TextUtils.isEmpty(paramString4)))
    {
      if (Log.isLoggable("SettingsInjector", 5)) {
        Log.w("SettingsInjector", "Illegal setting specification: package=" + paramString1 + ", class=" + paramString2 + ", title=" + paramString3 + ", settingsActivity=" + paramString4);
      }
      return null;
    }
    return new InjectedSetting(paramString1, paramString2, paramString3, paramInt, paramUserHandle, paramString4);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof InjectedSetting)) {
      return false;
    }
    paramObject = (InjectedSetting)paramObject;
    boolean bool1 = bool2;
    if (this.packageName.equals(((InjectedSetting)paramObject).packageName))
    {
      bool1 = bool2;
      if (this.className.equals(((InjectedSetting)paramObject).className))
      {
        bool1 = bool2;
        if (this.title.equals(((InjectedSetting)paramObject).title))
        {
          bool1 = bool2;
          if (this.iconId == ((InjectedSetting)paramObject).iconId)
          {
            bool1 = bool2;
            if (this.mUserHandle.equals(((InjectedSetting)paramObject).mUserHandle)) {
              bool1 = this.settingsActivity.equals(((InjectedSetting)paramObject).settingsActivity);
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public Intent getServiceIntent()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName(this.packageName, this.className);
    return localIntent;
  }
  
  public int hashCode()
  {
    return ((((this.packageName.hashCode() * 31 + this.className.hashCode()) * 31 + this.title.hashCode()) * 31 + this.iconId) * 31 + this.mUserHandle.hashCode()) * 31 + this.settingsActivity.hashCode();
  }
  
  public String toString()
  {
    return "InjectedSetting{mPackageName='" + this.packageName + '\'' + ", mClassName='" + this.className + '\'' + ", label=" + this.title + ", iconId=" + this.iconId + ", userId=" + this.mUserHandle.getIdentifier() + ", settingsActivity='" + this.settingsActivity + '\'' + '}';
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\location\InjectedSetting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */