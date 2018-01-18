package com.android.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.settings.applications.LinearColorBar;

public class SummaryPreference
  extends Preference
{
  private static final String TAG = "SummaryPreference";
  private String mAmount;
  private boolean mChartEnabled = true;
  private boolean mColorsSet = false;
  private String mEndLabel;
  private int mLeft;
  private float mLeftRatio;
  private int mMiddle;
  private float mMiddleRatio;
  private int mRight;
  private float mRightRatio;
  private String mStartLabel;
  private String mUnits;
  
  public SummaryPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setLayoutResource(2130968986);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    LinearColorBar localLinearColorBar = (LinearColorBar)paramPreferenceViewHolder.itemView.findViewById(2131362516);
    if (this.mChartEnabled)
    {
      localLinearColorBar.setVisibility(0);
      localLinearColorBar.setRatios(this.mLeftRatio, this.mMiddleRatio, this.mRightRatio);
      if (this.mColorsSet) {
        localLinearColorBar.setColors(this.mLeft, this.mMiddle, this.mRight);
      }
    }
    while ((!this.mChartEnabled) || ((TextUtils.isEmpty(this.mStartLabel)) && (TextUtils.isEmpty(this.mEndLabel))))
    {
      paramPreferenceViewHolder.findViewById(2131362552).setVisibility(8);
      return;
      localLinearColorBar.setVisibility(8);
    }
    paramPreferenceViewHolder.findViewById(2131362552).setVisibility(0);
    ((TextView)paramPreferenceViewHolder.findViewById(16908308)).setText(this.mStartLabel);
    ((TextView)paramPreferenceViewHolder.findViewById(16908309)).setText(this.mEndLabel);
  }
  
  public void setAmount(String paramString)
  {
    this.mAmount = paramString;
    if ((this.mAmount != null) && (this.mUnits != null)) {
      setTitle(TextUtils.expandTemplate(getContext().getText(2131691771), new CharSequence[] { this.mAmount, this.mUnits }));
    }
  }
  
  public void setChartEnabled(boolean paramBoolean)
  {
    if (this.mChartEnabled != paramBoolean)
    {
      this.mChartEnabled = paramBoolean;
      notifyChanged();
    }
  }
  
  public void setColors(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mLeft = paramInt1;
    this.mMiddle = paramInt2;
    this.mRight = paramInt3;
    this.mColorsSet = true;
    notifyChanged();
  }
  
  public void setLabels(String paramString1, String paramString2)
  {
    this.mStartLabel = paramString1;
    this.mEndLabel = paramString2;
    notifyChanged();
  }
  
  public void setRatios(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.mLeftRatio = paramFloat1;
    this.mMiddleRatio = paramFloat2;
    this.mRightRatio = paramFloat3;
    notifyChanged();
  }
  
  public void setUnits(String paramString)
  {
    this.mUnits = paramString;
    if ((this.mAmount != null) && (this.mUnits != null)) {
      setTitle(TextUtils.expandTemplate(getContext().getText(2131691771), new CharSequence[] { this.mAmount, this.mUnits }));
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\SummaryPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */