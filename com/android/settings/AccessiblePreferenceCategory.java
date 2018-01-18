package com.android.settings;

import android.content.Context;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.View;

public class AccessiblePreferenceCategory
  extends PreferenceCategory
{
  private String mContentDescription;
  
  public AccessiblePreferenceCategory(Context paramContext)
  {
    super(paramContext);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    paramPreferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
  }
  
  public void setContentDescription(String paramString)
  {
    this.mContentDescription = paramString;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AccessiblePreferenceCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */