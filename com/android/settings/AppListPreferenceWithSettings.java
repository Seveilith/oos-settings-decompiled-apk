package com.android.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class AppListPreferenceWithSettings
  extends AppListPreference
{
  private ComponentName mSettingsComponent;
  private View mSettingsIcon;
  
  public AppListPreferenceWithSettings(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWidgetLayoutResource(2130968931);
  }
  
  private void updateSettingsVisibility()
  {
    if (this.mSettingsIcon == null) {
      return;
    }
    if (this.mSettingsComponent == null)
    {
      this.mSettingsIcon.setVisibility(8);
      return;
    }
    this.mSettingsIcon.setVisibility(0);
  }
  
  public void onBindViewHolder(PreferenceViewHolder paramPreferenceViewHolder)
  {
    super.onBindViewHolder(paramPreferenceViewHolder);
    this.mSettingsIcon = paramPreferenceViewHolder.findViewById(2131362454);
    this.mSettingsIcon.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = new Intent("android.intent.action.MAIN");
        paramAnonymousView.setComponent(AppListPreferenceWithSettings.-get0(AppListPreferenceWithSettings.this));
        AppListPreferenceWithSettings.this.getContext().startActivity(new Intent(paramAnonymousView));
      }
    });
    ((ViewGroup)this.mSettingsIcon.getParent()).setPaddingRelative(0, 0, 0, 0);
    updateSettingsVisibility();
  }
  
  protected void setSettingsComponent(ComponentName paramComponentName)
  {
    this.mSettingsComponent = paramComponentName;
    updateSettingsVisibility();
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\AppListPreferenceWithSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */