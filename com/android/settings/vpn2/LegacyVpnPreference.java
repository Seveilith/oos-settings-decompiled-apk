package com.android.settings.vpn2;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.view.View;
import com.android.internal.net.VpnProfile;

public class LegacyVpnPreference
  extends ManageablePreference
{
  private VpnProfile mProfile;
  
  LegacyVpnPreference(Context paramContext)
  {
    super(paramContext, null);
    setIcon(paramContext.getResources().getDrawable(2130838550));
  }
  
  public int compareTo(Preference paramPreference)
  {
    if ((paramPreference instanceof LegacyVpnPreference))
    {
      paramPreference = (LegacyVpnPreference)paramPreference;
      int j = paramPreference.mState - this.mState;
      int i = j;
      if (j == 0)
      {
        j = this.mProfile.name.compareToIgnoreCase(paramPreference.mProfile.name);
        i = j;
        if (j == 0)
        {
          j = this.mProfile.type - paramPreference.mProfile.type;
          i = j;
          if (j == 0) {
            i = this.mProfile.key.compareTo(paramPreference.mProfile.key);
          }
        }
      }
      return i;
    }
    if ((paramPreference instanceof AppPreference))
    {
      paramPreference = (AppPreference)paramPreference;
      if ((this.mState != 3) && (paramPreference.getState() == 3)) {
        return 1;
      }
      return -1;
    }
    return super.compareTo(paramPreference);
  }
  
  public VpnProfile getProfile()
  {
    return this.mProfile;
  }
  
  public void onClick(View paramView)
  {
    if ((paramView.getId() == 2131362454) && (isDisabledByAdmin()))
    {
      performClick();
      return;
    }
    super.onClick(paramView);
  }
  
  public void setProfile(VpnProfile paramVpnProfile)
  {
    String str2 = null;
    if (this.mProfile != null) {}
    for (String str1 = this.mProfile.name;; str1 = null)
    {
      if (paramVpnProfile != null) {
        str2 = paramVpnProfile.name;
      }
      if (!TextUtils.equals(str1, str2))
      {
        setTitle(str2);
        notifyHierarchyChanged();
      }
      this.mProfile = paramVpnProfile;
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\vpn2\LegacyVpnPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */