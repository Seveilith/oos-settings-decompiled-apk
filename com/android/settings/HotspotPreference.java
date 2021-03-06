package com.android.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class HotspotPreference
  extends Preference
  implements CompoundButton.OnCheckedChangeListener
{
  private Context mContext;
  private TextView mSubSummary;
  private Switch mSwitch;
  private boolean mSwitchEnabled;
  
  public HotspotPreference(Context paramContext)
  {
    this(paramContext, null);
    this.mContext = paramContext;
  }
  
  public HotspotPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843629);
    this.mContext = paramContext;
  }
  
  public HotspotPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mContext = paramContext;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mSubSummary = ((TextView)paramPreferenceViewHolder.findViewById(2131362172));
    this.mSubSummary.setVisibility(8);
    this.mSwitch = ((Switch)paramPreferenceViewHolder.findViewById(2131361960));
    this.mSwitch.setOnCheckedChangeListener(this);
    setChecked(this.mSwitchEnabled);
  }
  
  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    callChangeListener(Boolean.valueOf(paramBoolean));
    this.mSwitchEnabled = paramBoolean;
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (this.mSwitch != null)
    {
      this.mSwitch.setOnCheckedChangeListener(null);
      this.mSwitch.setChecked(paramBoolean);
      this.mSwitch.setOnCheckedChangeListener(this);
    }
    this.mSwitchEnabled = paramBoolean;
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    if (this.mSubSummary == null) {
      return;
    }
    TextView localTextView = this.mSubSummary;
    if (paramCharSequence != null) {}
    for (int i = 0;; i = 8)
    {
      localTextView.setVisibility(i);
      this.mSubSummary.setText(paramCharSequence);
      return;
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\HotspotPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */