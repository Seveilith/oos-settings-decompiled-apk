package com.android.settings.applications;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.applications.ApplicationsState.AppEntry;

public class AppViewHolder
{
  public ImageView appIcon;
  public TextView appName;
  public TextView disabled;
  public ApplicationsState.AppEntry entry;
  public View rootView;
  public TextView summary;
  
  public static AppViewHolder createOrRecycle(LayoutInflater paramLayoutInflater, View paramView)
  {
    if (paramView == null)
    {
      paramView = paramLayoutInflater.inflate(2130968889, null);
      paramLayoutInflater.inflate(2130969109, (ViewGroup)paramView.findViewById(16908312));
      paramLayoutInflater = new AppViewHolder();
      paramLayoutInflater.rootView = paramView;
      paramLayoutInflater.appName = ((TextView)paramView.findViewById(16908310));
      paramLayoutInflater.appIcon = ((ImageView)paramView.findViewById(16908294));
      paramLayoutInflater.summary = ((TextView)paramView.findViewById(2131362718));
      paramLayoutInflater.disabled = ((TextView)paramView.findViewById(16908304));
      paramView.setTag(paramLayoutInflater);
      return paramLayoutInflater;
    }
    return (AppViewHolder)paramView.getTag();
  }
  
  void updateSizeText(CharSequence paramCharSequence, int paramInt)
  {
    if (ManageApplications.DEBUG) {
      Log.i("ManageApplications", "updateSizeText of " + this.entry.label + " " + this.entry + ": " + this.entry.sizeStr);
    }
    if (this.entry.sizeStr != null) {
      switch (paramInt)
      {
      default: 
        this.summary.setText(this.entry.sizeStr);
      }
    }
    while (this.entry.size != -2L)
    {
      return;
      this.summary.setText(this.entry.internalSizeStr);
      return;
      this.summary.setText(this.entry.externalSizeStr);
      return;
    }
    this.summary.setText(paramCharSequence);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\AppViewHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */