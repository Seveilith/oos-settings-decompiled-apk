package com.android.settings.fuelgauge;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings.Global;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.Utils;

public class BatterySaverPreference
  extends Preference
{
  private final ContentObserver mObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      BatterySaverPreference.-wrap0(BatterySaverPreference.this);
    }
  };
  private PowerManager mPowerManager;
  
  public BatterySaverPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  private void updateSwitch()
  {
    Context localContext = getContext();
    int i;
    int k;
    if (this.mPowerManager.isPowerSaveMode())
    {
      i = 2131693664;
      k = Settings.Global.getInt(localContext.getContentResolver(), "low_power_trigger_level", 0);
      if (k <= 0) {
        break label79;
      }
    }
    label79:
    for (int j = 2131693667;; j = 2131693666)
    {
      setSummary(localContext.getString(i, new Object[] { localContext.getString(j, new Object[] { Utils.formatPercentage(k) }) }));
      return;
      i = 2131693665;
      break;
    }
  }
  
  public void onAttached()
  {
    super.onAttached();
    this.mPowerManager = ((PowerManager)getContext().getSystemService("power"));
    this.mObserver.onChange(true);
    getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power_trigger_level"), true, this.mObserver);
    getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("low_power"), true, this.mObserver);
  }
  
  public void onDetached()
  {
    super.onDetached();
    getContext().getContentResolver().unregisterContentObserver(this.mObserver);
  }
  
  protected void performClick(View paramView)
  {
    Utils.startWithFragment(getContext(), getFragment(), null, null, 0, 0, getTitle());
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\fuelgauge\BatterySaverPreference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */