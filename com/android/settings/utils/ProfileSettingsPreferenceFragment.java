package com.android.settings.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.drawer.UserAdapter;

public abstract class ProfileSettingsPreferenceFragment
  extends SettingsPreferenceFragment
{
  protected abstract String getIntentActionString();
  
  public void onViewCreated(final View paramView, final Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    paramView = UserAdapter.createUserSpinnerAdapter((UserManager)getSystemService("user"), getActivity());
    if (paramView != null)
    {
      paramBundle = (Spinner)setPinnedHeaderView(2130969008);
      paramBundle.setAdapter(paramView);
      paramBundle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
      {
        public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          paramAnonymousAdapterView = paramView.getUserHandle(paramAnonymousInt);
          if (paramAnonymousAdapterView.getIdentifier() != UserHandle.myUserId())
          {
            paramAnonymousView = new Intent(ProfileSettingsPreferenceFragment.this.getIntentActionString());
            paramAnonymousView.addFlags(268435456);
            paramAnonymousView.addFlags(32768);
            ProfileSettingsPreferenceFragment.this.getActivity().startActivityAsUser(paramAnonymousView, paramAnonymousAdapterView);
            paramBundle.setSelection(0);
          }
        }
        
        public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView) {}
      });
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\utils\ProfileSettingsPreferenceFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */