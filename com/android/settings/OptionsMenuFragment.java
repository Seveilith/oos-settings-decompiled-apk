package com.android.settings;

import android.os.Bundle;

public abstract class OptionsMenuFragment
  extends InstrumentedFragment
{
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setHasOptionsMenu(true);
  }
}


/* Location:              C:\Users\johan\Desktop\classes2-dex2jar.jar!\com\android\settings\OptionsMenuFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */