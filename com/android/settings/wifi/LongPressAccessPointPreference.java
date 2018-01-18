package com.android.settings.wifi;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;
import com.android.settingslib.wifi.AccessPointPreference.UserBadgeCache;

public class LongPressAccessPointPreference
  extends AccessPointPreference
{
  private final Fragment mFragment;
  
  public LongPressAccessPointPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mFragment = null;
  }
  
  public LongPressAccessPointPreference(AccessPoint paramAccessPoint, Context paramContext, AccessPointPreference.UserBadgeCache paramUserBadgeCache, boolean paramBoolean, Fragment paramFragment)
  {
    super(paramAccessPoint, paramContext, paramUserBadgeCache, paramBoolean);
    this.mFragment = paramFragment;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    if (this.mFragment != null)
    {
      paramPreferenceViewHolder.itemView.setOnCreateContextMenuListener(this.mFragment);
      paramPreferenceViewHolder.itemView.setTag(this);
      paramPreferenceViewHolder.itemView.setLongClickable(true);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\wifi\LongPressAccessPointPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */