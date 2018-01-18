package com.android.settings.vpn2;

import com.android.internal.util.Preconditions;
import java.util.Objects;

class AppVpnInfo
  implements Comparable
{
  public final String packageName;
  public final int userId;
  
  public AppVpnInfo(int paramInt, String paramString)
  {
    this.userId = paramInt;
    this.packageName = ((String)Preconditions.checkNotNull(paramString));
  }
  
  public int compareTo(Object paramObject)
  {
    paramObject = (AppVpnInfo)paramObject;
    int j = this.packageName.compareTo(((AppVpnInfo)paramObject).packageName);
    int i = j;
    if (j == 0) {
      i = ((AppVpnInfo)paramObject).userId - this.userId;
    }
    return i;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if ((paramObject instanceof AppVpnInfo))
    {
      paramObject = (AppVpnInfo)paramObject;
      if (this.userId == ((AppVpnInfo)paramObject).userId) {
        bool = Objects.equals(this.packageName, ((AppVpnInfo)paramObject).packageName);
      }
      return bool;
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.packageName, Integer.valueOf(this.userId) });
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\AppVpnInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */