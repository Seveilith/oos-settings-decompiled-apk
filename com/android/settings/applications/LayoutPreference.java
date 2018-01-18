package com.android.settings.applications;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.internal.R.styleable;
import com.android.settings.Utils;

public class LayoutPreference
  extends Preference
{
  private View mRootView;
  
  public LayoutPreference(Context paramContext, int paramInt)
  {
    this(paramContext, LayoutInflater.from(paramContext).inflate(paramInt, null, false));
  }
  
  public LayoutPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    int i = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Preference, 0, 0).getResourceId(3, 0);
    if (i == 0) {
      throw new IllegalArgumentException("LayoutPreference requires a layout to be defined");
    }
    setView(LayoutInflater.from(getContext()).inflate(i, null, false));
  }
  
  public LayoutPreference(Context paramContext, View paramView)
  {
    super(paramContext);
    setView(paramView);
  }
  
  private void setView(View paramView)
  {
    setLayoutResource(2130968738);
    setSelectable(false);
    ViewGroup localViewGroup = (ViewGroup)paramView.findViewById(2131361954);
    if (localViewGroup != null) {
      Utils.forceCustomPadding(localViewGroup, true);
    }
    this.mRootView = paramView;
    setShouldDisableView(false);
  }
  
  public View findViewById(int paramInt)
  {
    return this.mRootView.findViewById(paramInt);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    paramPreferenceViewHolder = (FrameLayout)paramPreferenceViewHolder.itemView;
    paramPreferenceViewHolder.removeAllViews();
    ViewGroup localViewGroup = (ViewGroup)this.mRootView.getParent();
    if (localViewGroup != null) {
      localViewGroup.removeView(this.mRootView);
    }
    paramPreferenceViewHolder.addView(this.mRootView);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\LayoutPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */