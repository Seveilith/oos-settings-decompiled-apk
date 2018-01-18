package com.android.settings.applications;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;

public class ConvertToFbe
  extends SettingsPreferenceFragment
{
  static final String CONVERT_FBE_EXTRA = "ConvertFBE";
  private static final int KEYGUARD_REQUEST = 55;
  static final String TAG = "ConvertToFBE";
  
  private void convert()
  {
    ((SettingsActivity)getActivity()).startPreferencePanel(ConfirmConvertToFbe.class.getName(), null, 2131689759, null, null, 0);
  }
  
  private boolean runKeyguardConfirmation(int paramInt)
  {
    Resources localResources = getActivity().getResources();
    return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(paramInt, localResources.getText(2131689759));
  }
  
  protected int getMetricsCategory()
  {
    return 402;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 != 55) {
      return;
    }
    if (paramInt2 == -1) {
      convert();
    }
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = paramLayoutInflater.inflate(2130968651, null);
    ((Button)paramLayoutInflater.findViewById(2131362047)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (!ConvertToFbe.-wrap0(ConvertToFbe.this, 55)) {
          ConvertToFbe.-wrap1(ConvertToFbe.this);
        }
      }
    });
    return paramLayoutInflater;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\applications\ConvertToFbe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */