package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.android.settings.TintablePreference;
import com.android.settings.Utils;
import com.oneplus.settings.utils.OPUtils;

public class PowerGaugePreference
  extends TintablePreference
{
  private CharSequence mContentDescription;
  private Context mContext;
  private final int mIconSize;
  private BatteryEntry mInfo;
  private View.OnClickListener mOnClickListener;
  private int mPowerState = -1;
  private CharSequence mProgress;
  private View mSwitch;
  
  public PowerGaugePreference(Context paramContext, Drawable paramDrawable, CharSequence paramCharSequence, BatteryEntry paramBatteryEntry)
  {
    super(paramContext, null);
    if (paramDrawable != null)
    {
      setIcon(paramDrawable);
      setWidgetLayoutResource(2130968932);
      this.mInfo = paramBatteryEntry;
      if (paramCharSequence != null) {
        break label82;
      }
    }
    label82:
    for (paramDrawable = paramContext.getResources().getString(2131690303);; paramDrawable = paramCharSequence)
    {
      this.mContentDescription = paramDrawable;
      this.mIconSize = paramContext.getResources().getDimensionPixelSize(2131755314);
      this.mContext = paramContext;
      return;
      paramDrawable = new ColorDrawable(0);
      break;
    }
  }
  
  private Spanned convertToSpanned(String paramString1, String paramString2)
  {
    paramString1 = "<font color=\"#" + paramString2 + "\">" + paramString1 + "</font>";
    if (Build.VERSION.SDK_INT >= 24) {
      return Html.fromHtml(paramString1, 0);
    }
    return Html.fromHtml(paramString1);
  }
  
  private void setButtonVisible()
  {
    if (this.mSwitch != null)
    {
      if ((this.mPowerState == 0) || (1 == this.mPowerState)) {
        this.mSwitch.setVisibility(0);
      }
    }
    else {
      return;
    }
    this.mSwitch.setVisibility(4);
  }
  
  private void setOnClickListener(View paramView, View.OnClickListener paramOnClickListener)
  {
    this.mOnClickListener = paramOnClickListener;
    if (paramView != null)
    {
      paramView.setOnClickListener(this.mOnClickListener);
      if ((this.mInfo != null) && (this.mInfo.defaultPackageName != null)) {
        paramView.setTag(this.mInfo.defaultPackageName);
      }
    }
  }
  
  private void updatePowerState()
  {
    if (1 == this.mPowerState) {
      setSummary(convertToSpanned(this.mContext.getResources().getString(2131690433, new Object[] { this.mProgress }), "D94B41"));
    }
    for (;;)
    {
      setButtonVisible();
      return;
      if (this.mPowerState == 0) {
        setSummary(this.mContext.getResources().getString(2131690434, new Object[] { this.mProgress }));
      } else {
        setSummary(this.mProgress.toString());
      }
    }
  }
  
  BatteryEntry getInfo()
  {
    return this.mInfo;
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    ((ImageView)paramPreferenceViewHolder.findViewById(16908294)).setLayoutParams(new LinearLayout.LayoutParams(this.mIconSize, this.mIconSize));
    this.mSwitch = paramPreferenceViewHolder.findViewById(2131362455);
    ColorStateList localColorStateList = OPUtils.creatOneplusPrimaryColorStateList(this.mContext);
    ((TextView)this.mSwitch).setText(2131690429);
    ((TextView)this.mSwitch).setTextColor(localColorStateList);
    setOnClickListener(this.mSwitch, this.mOnClickListener);
    setButtonVisible();
    if (this.mContentDescription != null) {
      ((TextView)paramPreferenceViewHolder.findViewById(16908310)).setContentDescription(this.mContentDescription);
    }
  }
  
  public void setContentDescription(String paramString)
  {
    this.mContentDescription = paramString;
    notifyChanged();
  }
  
  public void setOnButtonClickListener(View.OnClickListener paramOnClickListener)
  {
    setOnClickListener(this.mSwitch, paramOnClickListener);
  }
  
  public void setPercent(double paramDouble1, double paramDouble2)
  {
    this.mProgress = Utils.formatPercentage((int)(0.5D + paramDouble2));
    updatePowerState();
    notifyChanged();
  }
  
  public void setState(int paramInt)
  {
    this.mPowerState = paramInt;
    updatePowerState();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\PowerGaugePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */