package com.oneplus.settings.ui;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;

public class OPPreferenceDivider
  extends Preference
{
  private LayoutInflater inflater;
  private Context mContext;
  
  public OPPreferenceDivider(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPPreferenceDivider(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPPreferenceDivider(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(2130968833);
    setEnabled(false);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder.setDividerAllowedBelow(false);
    paramPreferenceViewHolder.setDividerAllowedAbove(false);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPPreferenceDivider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */