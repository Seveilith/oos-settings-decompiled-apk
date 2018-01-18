package com.android.settings.applications;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class SpacePreference
  extends Preference
{
  private int mHeight;
  
  public SpacePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842894);
  }
  
  public SpacePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SpacePreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setLayoutResource(2130969006);
    this.mHeight = paramContext.obtainStyledAttributes(paramAttributeSet, new int[] { 16842997 }, paramInt1, paramInt2).getDimensionPixelSize(0, 0);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-1, this.mHeight);
    paramPreferenceViewHolder.itemView.setLayoutParams(localLayoutParams);
  }
  
  public void setHeight(int paramInt)
  {
    this.mHeight = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\SpacePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */