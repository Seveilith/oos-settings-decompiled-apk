package com.oneplus.settings.ui;

import android.app.Application;
import android.content.Context;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.SettingsBaseApplication;

public class OPScreenColorModeSummary
  extends RestrictedPreference
{
  private Context mContext;
  private TextView mSummary;
  
  public OPScreenColorModeSummary(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public OPScreenColorModeSummary(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public OPScreenColorModeSummary(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public OPScreenColorModeSummary(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mContext = paramContext;
    setLayoutResource(2130968854);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mSummary = ((TextView)paramPreferenceViewHolder.findViewById(2131362024));
    int i;
    if (Settings.Secure.getInt(this.mContext.getContentResolver(), "night_display_activated", 0) != 1)
    {
      i = 1;
      if (Settings.System.getInt(this.mContext.getContentResolver(), "reading_mode_status_manual", 0) == 1) {
        break label100;
      }
    }
    label100:
    for (int j = 1;; j = 0)
    {
      if (i == 0) {
        this.mSummary.setText(SettingsBaseApplication.mApplication.getText(2131690095));
      }
      if (j == 0) {
        this.mSummary.setText(SettingsBaseApplication.mApplication.getText(2131690403));
      }
      return;
      i = 0;
      break;
    }
  }
  
  public void setSummary(CharSequence paramCharSequence)
  {
    setTextSummary(paramCharSequence.toString());
  }
  
  public void setTextSummary(String paramString)
  {
    if (this.mSummary != null) {
      this.mSummary.setText(paramString);
    }
    notifyChanged();
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\ui\OPScreenColorModeSummary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */