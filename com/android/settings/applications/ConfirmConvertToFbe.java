package com.android.settings.applications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.settings.SettingsPreferenceFragment;

public class ConfirmConvertToFbe
  extends SettingsPreferenceFragment
{
  static final String TAG = "ConfirmConvertToFBE";
  
  protected int getMetricsCategory()
  {
    return 403;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130968643, null);
    ((Button)paramLayoutInflater.findViewById(2131362033)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = new Intent("android.intent.action.MASTER_CLEAR");
        paramAnonymousView.addFlags(268435456);
        paramAnonymousView.putExtra("android.intent.extra.REASON", "convert_fbe");
        ConfirmConvertToFbe.this.getActivity().sendBroadcast(paramAnonymousView);
      }
    });
    return paramLayoutInflater;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ConfirmConvertToFbe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */