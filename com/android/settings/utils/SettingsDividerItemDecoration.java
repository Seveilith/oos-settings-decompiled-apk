package com.android.settings.utils;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView.ViewHolder;
import com.android.setupwizardlib.DividerItemDecoration;

public class SettingsDividerItemDecoration
  extends DividerItemDecoration
{
  public SettingsDividerItemDecoration(Context paramContext)
  {
    super(paramContext);
  }
  
  protected boolean isDividerAllowedAbove(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((paramViewHolder instanceof PreferenceViewHolder)) {
      return ((PreferenceViewHolder)paramViewHolder).isDividerAllowedAbove();
    }
    return super.isDividerAllowedAbove(paramViewHolder);
  }
  
  protected boolean isDividerAllowedBelow(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((paramViewHolder instanceof PreferenceViewHolder)) {
      return ((PreferenceViewHolder)paramViewHolder).isDividerAllowedBelow();
    }
    return super.isDividerAllowedBelow(paramViewHolder);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\utils\SettingsDividerItemDecoration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */