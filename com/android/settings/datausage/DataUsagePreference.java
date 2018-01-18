package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.text.format.Formatter;
import android.util.AttributeSet;
import com.android.settings.Utils;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.net.DataUsageController.DataUsageInfo;

public class DataUsagePreference
  extends Preference
  implements TemplatePreference
{
  private int mSubId;
  private NetworkTemplate mTemplate;
  
  public DataUsagePreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public Intent getIntent()
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("network_template", this.mTemplate);
    localBundle.putInt("sub_id", this.mSubId);
    return Utils.onBuildStartFragmentIntent(getContext(), DataUsageList.class.getName(), localBundle, getContext().getPackageName(), 0, getTitle(), false);
  }
  
  public void setTemplate(NetworkTemplate paramNetworkTemplate, int paramInt, TemplatePreference.NetworkServices paramNetworkServices)
  {
    this.mTemplate = paramNetworkTemplate;
    this.mSubId = paramInt;
    paramNetworkTemplate = new DataUsageController(getContext()).getDataUsageInfo(this.mTemplate);
    setSummary(getContext().getString(2131693646, new Object[] { Formatter.formatFileSize(getContext(), paramNetworkTemplate.usageLevel), paramNetworkTemplate.period }));
    setIntent(getIntent());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\DataUsagePreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */