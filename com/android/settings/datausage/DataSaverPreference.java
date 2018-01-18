package com.android.settings.datausage;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

public class DataSaverPreference
  extends Preference
  implements DataSaverBackend.Listener
{
  private final DataSaverBackend mDataSaverBackend;
  
  public DataSaverPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mDataSaverBackend = new DataSaverBackend(paramContext);
  }
  
  public void onAttached()
  {
    super.onAttached();
    this.mDataSaverBackend.addListener(this);
  }
  
  public void onBlacklistStatusChanged(int paramInt, boolean paramBoolean) {}
  
  public void onDataSaverChanged(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = 2131693652;; i = 2131693653)
    {
      setSummary(i);
      return;
    }
  }
  
  public void onDetached()
  {
    super.onDetached();
    this.mDataSaverBackend.addListener(this);
  }
  
  public void onWhitelistStatusChanged(int paramInt, boolean paramBoolean) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataSaverPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */