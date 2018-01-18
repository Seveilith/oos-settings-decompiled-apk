package com.android.settings.datausage;

import android.content.Context;
import android.net.NetworkTemplate;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

public class NetworkRestrictionsPreference
  extends Preference
  implements TemplatePreference
{
  public NetworkRestrictionsPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void setTemplate(NetworkTemplate paramNetworkTemplate, int paramInt, TemplatePreference.NetworkServices paramNetworkServices) {}
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\NetworkRestrictionsPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */