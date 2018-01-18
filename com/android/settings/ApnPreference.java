package com.android.settings;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony.Carriers;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class ApnPreference
  extends Preference
  implements CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
  static final String TAG = "ApnPreference";
  private static CompoundButton mCurrentChecked = null;
  private static String mSelectedKey = null;
  private boolean mApnReadOnly = false;
  private boolean mProtectFromCheckedChange = false;
  private boolean mSelectable = true;
  
  public ApnPreference(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ApnPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 2130772441);
  }
  
  public ApnPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public boolean getApnReadOnly()
  {
    return this.mApnReadOnly;
  }
  
  public boolean getSelectable()
  {
    return this.mSelectable;
  }
  
  public boolean isChecked()
  {
    return getKey().equals(mSelectedKey);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    Object localObject = paramPreferenceViewHolder.findViewById(2131361949);
    if ((localObject != null) && ((localObject instanceof RadioButton)))
    {
      localObject = (RadioButton)localObject;
      if (!this.mSelectable) {
        break label110;
      }
      ((RadioButton)localObject).setOnCheckedChangeListener(this);
      boolean bool = getKey().equals(mSelectedKey);
      if (bool)
      {
        mCurrentChecked = (CompoundButton)localObject;
        mSelectedKey = getKey();
      }
      this.mProtectFromCheckedChange = true;
      ((RadioButton)localObject).setChecked(bool);
      this.mProtectFromCheckedChange = false;
      ((RadioButton)localObject).setVisibility(0);
    }
    for (;;)
    {
      paramPreferenceViewHolder = paramPreferenceViewHolder.findViewById(2131361948);
      if ((paramPreferenceViewHolder != null) && ((paramPreferenceViewHolder instanceof RelativeLayout))) {
        paramPreferenceViewHolder.setOnClickListener(this);
      }
      return;
      label110:
      ((RadioButton)localObject).setVisibility(8);
    }
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    Log.i("ApnPreference", "ID: " + getKey() + " :" + paramBoolean);
    if (this.mProtectFromCheckedChange) {
      return;
    }
    if (paramBoolean)
    {
      if (mCurrentChecked != null) {
        mCurrentChecked.setChecked(false);
      }
      mCurrentChecked = paramCompoundButton;
      mSelectedKey = getKey();
      callChangeListener(mSelectedKey);
      return;
    }
    mCurrentChecked = null;
    mSelectedKey = null;
  }
  
  public void onClick(View paramView)
  {
    if ((paramView != null) && (2131361948 == paramView.getId()))
    {
      paramView = getContext();
      if (paramView != null)
      {
        int i = Integer.parseInt(getKey());
        Intent localIntent = new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, i));
        localIntent.putExtra("DISABLE_EDITOR", this.mApnReadOnly);
        paramView.startActivity(localIntent);
      }
    }
  }
  
  public void setApnReadOnly(boolean paramBoolean)
  {
    this.mApnReadOnly = paramBoolean;
  }
  
  public void setChecked()
  {
    mSelectedKey = getKey();
  }
  
  public void setSelectable(boolean paramBoolean)
  {
    this.mSelectable = paramBoolean;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\ApnPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */