package com.oneplus.settings.opfinger;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

public class OPFingerPrintEditCategory
  extends Preference
{
  private LayoutInflater inflater;
  private Context mContext;
  private CharSequence mFingerprintName;
  private TextView mFingerprintNameView;
  private int mLayoutResId = 2130968805;
  
  public OPFingerPrintEditCategory(Context paramContext)
  {
    super(paramContext);
    initViews(paramContext);
  }
  
  public OPFingerPrintEditCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViews(paramContext);
  }
  
  public OPFingerPrintEditCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initViews(paramContext);
  }
  
  private void initViews(Context paramContext)
  {
    this.mContext = paramContext;
    setLayoutResource(this.mLayoutResId);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mFingerprintNameView = ((TextView)paramPreferenceViewHolder.findViewById(2131362312));
    this.mFingerprintNameView.setText(this.mFingerprintName);
    paramPreferenceViewHolder.setDividerAllowedBelow(false);
  }
  
  public void setFingerprintName(CharSequence paramCharSequence)
  {
    this.mFingerprintName = paramCharSequence;
    if (this.mFingerprintNameView != null) {
      this.mFingerprintNameView.setText(paramCharSequence);
    }
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\opfinger\OPFingerPrintEditCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */