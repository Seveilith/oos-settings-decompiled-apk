package com.android.settings.datausage;

import android.content.Context;
import android.net.NetworkTemplate;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import com.android.settings.DividedCategory;

public class TemplatePreferenceCategory
  extends DividedCategory
  implements TemplatePreference
{
  private int mSubId;
  private NetworkTemplate mTemplate;
  
  public TemplatePreferenceCategory(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public boolean addPreference(Preference paramPreference)
  {
    if (!(paramPreference instanceof TemplatePreference)) {
      throw new IllegalArgumentException("TemplatePreferenceCategories can only hold TemplatePreferences");
    }
    return super.addPreference(paramPreference);
  }
  
  public void pushTemplates(TemplatePreference.NetworkServices paramNetworkServices)
  {
    if (this.mTemplate == null) {
      throw new RuntimeException("null mTemplate for " + getKey());
    }
    int i = 0;
    while (i < getPreferenceCount())
    {
      ((TemplatePreference)getPreference(i)).setTemplate(this.mTemplate, this.mSubId, paramNetworkServices);
      i += 1;
    }
  }
  
  public void setTemplate(NetworkTemplate paramNetworkTemplate, int paramInt, TemplatePreference.NetworkServices paramNetworkServices)
  {
    this.mTemplate = paramNetworkTemplate;
    this.mSubId = paramInt;
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\datausage\TemplatePreferenceCategory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */