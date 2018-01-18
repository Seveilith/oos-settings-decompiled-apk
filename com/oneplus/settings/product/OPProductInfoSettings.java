package com.oneplus.settings.product;

import android.os.Bundle;
import android.view.View;
import com.android.settings.SettingsPreferenceFragment;

public class OPProductInfoSettings
  extends SettingsPreferenceFragment
{
  private final String ONEPLUS_PRODUCT_INFO_INTRODUCE = "oneplus_product_info_introduce";
  private final String PKG_PRODUCT_INFO_INTRODUCE_ = "com.oneplus.noviceteaching";
  
  protected int getMetricsCategory()
  {
    return 9999;
  }
  
  public void onViewCreated(View paramView, Bundle paramBundle)
  {
    super.onViewCreated(paramView, paramBundle);
    addPreferencesFromResource(2131230806);
  }
}


/* Location:              C:\Users\johan\Desktop\dex2jar-2.0\classes3-dex2jar.jar!\com\oneplus\settings\product\OPProductInfoSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */